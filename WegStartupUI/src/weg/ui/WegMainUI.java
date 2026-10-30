package weg.ui;

import weg.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/** All presentation state belongs to Swing's event thread; I/O runs on one worker. */
@SuppressWarnings("serial") // Windows are not serialized; trips use CSV.
public final class WegMainUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(16, 25, 33), PANEL = new Color(25, 39, 49);
    private static final Color INK = new Color(231, 241, 240), MUTED = new Color(158, 180, 187), ACCENT = new Color(80, 220, 175);
    private final Options options;
    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "weg-acquisition"); thread.setDaemon(true); return thread;
    });
    private final ExecutorService cameraWorker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "weg-camera"); thread.setDaemon(true); return thread;
    });
    private final JLabel status = label("Ready", 14, MUTED);
    private final JLabel recordingStatus = label("Not recording", 13, MUTED);
    private final JLabel location = label("Waiting for position", 14, MUTED);
    private final Map<String, JLabel> values = new LinkedHashMap<>();
    private final Plot route = new Plot(true), temperature = new Plot(false);
    private final JButton connect = new JButton("Connect"), record = new JButton("Start trip"), photo = new JButton("Take photo");
    private final JComboBox<String> modes = new JComboBox<>(new String[]{"Simulation", "Live sensors", "Playback"});
    private final JTextField host, port;
    private final JCheckBox faults = new JCheckBox("Simulate faults");
    private final javax.swing.Timer freshness;
    private SampleSource source;
    private ScheduledFuture<?> polling;
    private TripLog log;
    private Path tripFile;
    private Path replay;
    private TripStats stats = new TripStats();
    private volatile int generation;
    private boolean connected, recording, cameraBusy;
    private long receivedAt;
    private Sample latest;

    public WegMainUI(Options options) {
        super("WEG · Excursion recorder");
        this.options = options; replay = options.replay();
        host = new JTextField(options.host(), 12); port = new JTextField(Integer.toString(options.port()), 5);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND);
        JPanel content = panel(new BorderLayout(16, 16));
        content.setOpaque(true); content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        setContentPane(content);
        JPanel heading = panel(new BorderLayout());
        heading.add(label("WEG / FIELD COMPANION", 26, INK), BorderLayout.NORTH);
        heading.add(status, BorderLayout.SOUTH);
        content.add(heading, BorderLayout.NORTH);
        JPanel body = panel(new BorderLayout(14, 14));
        JPanel metrics = panel(new GridLayout(2, 4, 12, 12));
        for (String name : new String[]{"DISTANCE / km", "SPEED / km/h", "ELAPSED", "COURSE / °", "TEMPERATURE / °C", "PRESSURE / hPa", "UV INDEX", "ELEVATION / m"}) {
            JPanel card = new JPanel(new BorderLayout(6, 10)); card.setBackground(PANEL);
            card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
            card.add(label(name, 12, MUTED), BorderLayout.NORTH);
            JLabel value = label("—", 28, INK); values.put(name, value); card.add(value);
            metrics.add(card);
        }
        body.add(metrics, BorderLayout.NORTH);
        JPanel charts = panel(new GridLayout(1, 2, 14, 0));
        charts.add(route);
        JPanel history = panel(new BorderLayout());
        JComboBox<String> metric = new JComboBox<>(new String[]{"Temperature", "Pressure"});
        metric.addActionListener(e -> { temperature.pressure = metric.getSelectedIndex() == 1; temperature.repaint(); });
        history.add(metric, BorderLayout.NORTH); history.add(temperature);
        charts.add(history); body.add(charts);
        body.add(location, BorderLayout.SOUTH);
        body.setPreferredSize(new Dimension(720, 480));
        JScrollPane scroll = new JScrollPane(body, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(24); content.add(scroll);
        JPanel controls = panel(new GridLayout(0, 1, 0, 8));
        JPanel connection = panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        connection.add(modes); connection.add(label("Host", 13, MUTED)); connection.add(host);
        connection.add(label("Port", 13, MUTED)); connection.add(port);
        faults.setOpaque(false); faults.setForeground(INK); faults.setSelected(options.faults());
        connection.add(faults); connection.add(connect);
        controls.add(connection);
        JPanel actions = panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton trips = new JButton("Open trip…"), export = new JButton("Export GPX…");
        actions.add(record); actions.add(photo); actions.add(trips); actions.add(export);
        controls.add(actions); controls.add(recordingStatus); content.add(controls, BorderLayout.SOUTH);
        modes.setSelectedIndex(options.mode().equals("live") ? 1 : options.mode().equals("replay") ? 2 : 0);
        connect.addActionListener(e -> { if (connected) disconnect(); else connect(); });
        record.addActionListener(e -> toggleRecording());
        trips.addActionListener(e -> selectTrip());
        export.addActionListener(e -> exportTrip());
        photo.addActionListener(e -> takePhoto());
        modes.addActionListener(e -> updateControls());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                disconnect(); freshness.stop();
                worker.execute(() -> SwingUtilities.invokeLater(() -> dispose()));
                worker.shutdown(); cameraWorker.shutdownNow();
            }
        });
        freshness = new javax.swing.Timer(1000, e -> {
            if (connected && receivedAt > 0 && System.currentTimeMillis() - receivedAt > 5000) {
                status.setText("STALE · no fresh sample for " + (System.currentTimeMillis() - receivedAt) / 1000 + " s");
                status.setForeground(new Color(255, 191, 105));
            }
        });
        freshness.start();
        setMinimumSize(new Dimension(800, 480)); setSize(1100, 740); setLocationRelativeTo(null);
        updateControls();
        // Simulation is safe to start immediately. Hardware connections remain explicit.
        if (modes.getSelectedIndex() == 0) connect();
    }
    private static JPanel panel(LayoutManager layout) { JPanel p = new JPanel(layout); p.setOpaque(false); return p; }
    private static JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text); l.setForeground(color); l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size)); return l;
    }
    private void updateControls() {
        boolean live = modes.getSelectedIndex() == 1;
        host.setEnabled(!connected && live); port.setEnabled(!connected && live);
        modes.setEnabled(!connected); faults.setEnabled(!connected && modes.getSelectedIndex() == 0);
        record.setEnabled(connected && modes.getSelectedIndex() != 2);
        photo.setEnabled(connected && live && recording && !cameraBusy);
    }
    private void connect() {
        if (modes.getSelectedIndex() == 2 && replay == null) { selectTrip(); return; }
        final int selected = modes.getSelectedIndex();
        final String address = host.getText().trim();
        final boolean simulateFaults = faults.isSelected();
        final int socketPort;
        try {
            socketPort = Integer.parseInt(port.getText().trim());
            if (socketPort < 1 || socketPort > 65535) throw new NumberFormatException();
        } catch (NumberFormatException e) { error("Port must be between 1 and 65535"); return; }
        final Path replayFile = replay;
        int token = ++generation;
        connected = true; connect.setText("Disconnect"); receivedAt = 0;
        stats = new TripStats(); route.clear(); temperature.clear(); latest = null;
        values.values().forEach(v -> v.setText("—")); location.setText("Waiting for position");
        status.setText("Connecting…"); updateControls();
        worker.execute(() -> {
            try {
                source = selected == 0 ? new Simulator(simulateFaults) : selected == 1
                        ? new SocketSource(address, socketPort) : new ReplaySource(replayFile);
                if (token != generation) { source.close(); source = null; return; }
                polling = worker.scheduleWithFixedDelay(() -> acquire(token), 0, 1, TimeUnit.SECONDS);
            } catch (IOException e) { onUi(token, () -> { error(e.getMessage()); disconnect(); }); }
        });
    }
    private void acquire(int token) {
        if (token != generation || source == null) return;
        try {
            Sample sample = source.read();
            if (sample == null) { onUi(token, () -> { disconnect(); status.setText("Playback complete"); }); return; }
            appendRecording(sample, token);
            String description = source.description();
            onUi(token, () -> present(sample, description));
        } catch (IOException | RuntimeException e) {
            appendRecording(Sample.unavailable(Instant.now()), token);
            onUi(token, () -> {
                stats.gap(); route.gap();
                status.setText("UNAVAILABLE · " + e.getMessage()); status.setForeground(new Color(255, 191, 105));
                if (modes.getSelectedIndex() == 2) disconnect();
            });
        }
    }
    private void appendRecording(Sample sample, int token) {
        if (log == null) return;
        try { log.append(sample); }
        catch (IOException e) {
            stopLog();
            onUi(token, () -> {
                recording = false; record.setText("Start trip"); recordingStatus.setText("Recording failed");
                updateControls(); error(e.getMessage());
            });
        }
    }
    private void onUi(int token, Runnable action) {
        SwingUtilities.invokeLater(() -> { if (token == generation) action.run(); });
    }
    private void present(Sample sample, String description) {
        latest = sample; receivedAt = System.currentTimeMillis(); stats.accept(sample);
        status.setText(description + " · " + (sample.fix() ? "GPS fix · " + sample.satellites() + " satellites" : "NO GPS FIX"));
        status.setForeground(sample.fix() ? ACCENT : new Color(255, 191, 105));
        String[] display = {format(stats.distanceKm(), 2), format(stats.speedKph(), 1),
                String.format(Locale.ROOT, "%02d:%02d:%02d", stats.elapsedSeconds()/3600, stats.elapsedSeconds()/60%60, stats.elapsedSeconds()%60),
                format(Double.isFinite(stats.bearing()) ? Math.round(stats.bearing()) % 360 : Double.NaN, 0), format(sample.temperatureC(), 1), format(sample.pressureHpa(), 1),
                format(sample.uvIndex(), 1), format(sample.elevationM(), 0)};
        int i = 0; for (JLabel value : values.values()) value.setText(display[i++]);
        location.setText(sample.fix() ? String.format(Locale.ROOT, "%.6f, %.6f  ·  %s", sample.latitude(), sample.longitude(), sample.time())
                : "Position unavailable · environmental measurements continue · " + sample.time());
        route.add(sample); temperature.add(sample);
    }
    private static String format(double n, int decimals) {
        return Double.isFinite(n) ? String.format(Locale.ROOT, "%." + decimals + "f", n) : "—";
    }
    private void disconnect() {
        connected = false; ++generation;
        ScheduledFuture<?> task = polling; if (task != null) task.cancel(false);
        worker.execute(() -> {
            if (source != null) try { source.close(); } catch (IOException ignored) { /* Already disconnected. */ }
            source = null; stopLog();
        });
        recording = false; record.setText("Start trip"); recordingStatus.setText("Not recording");
        connect.setText("Connect"); status.setText("Disconnected"); status.setForeground(MUTED); updateControls();
    }
    private void toggleRecording() {
        if (recording) {
            recording = false; record.setText("Start trip"); recordingStatus.setText("Trip saved");
            worker.execute(this::stopLog); updateControls(); return;
        }
        int token = generation;
        String prefix = modes.getSelectedIndex() == 0 ? "simulate" : "live";
        record.setEnabled(false);
        worker.execute(() -> {
            try {
                if (token != generation) return;
                Path path = options.output().resolve(prefix + "-trip-" + Instant.now().toString().replace(':', '-') + ".csv");
                log = new TripLog(path);
                onUi(token, () -> {
                    tripFile = path; recording = true; record.setText("Stop trip");
                    recordingStatus.setText("Recording · " + path.getFileName());
                    stats = new TripStats(); route.clear(); temperature.clear(); updateControls();
                });
            } catch (IOException e) { onUi(token, () -> { updateControls(); error(e.getMessage()); }); }
        });
    }
    private void stopLog() {
        if (log == null) return;
        try { log.close(); }
        catch (IOException e) { SwingUtilities.invokeLater(() -> error("Could not close trip: " + e.getMessage())); }
        finally { log = null; }
    }
    private void selectTrip() {
        if (connected) { error("Disconnect before opening a recorded trip."); return; }
        JFileChooser chooser = new JFileChooser(options.output().toFile());
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("WEG trip CSV", "csv"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            replay = chooser.getSelectedFile().toPath(); modes.setSelectedIndex(2); connect();
        }
    }
    private void exportTrip() {
        Path input = modes.getSelectedIndex() == 2 ? replay : tripFile;
        if (input == null) { error("Record or open a trip first."); return; }
        if (recording) { error("Stop recording before exporting the trip."); return; }
        JFileChooser chooser = new JFileChooser(options.output().toFile());
        chooser.setSelectedFile(Path.of(input.toString().replaceFirst("\\.csv$", ".gpx")).toFile());
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path output = chooser.getSelectedFile().toPath();
        worker.execute(() -> {
            try { TripLog.exportGpx(input, output); SwingUtilities.invokeLater(() -> recordingStatus.setText("Exported · " + output.getFileName())); }
            catch (IOException e) { SwingUtilities.invokeLater(() -> error(e.getMessage())); }
        });
    }
    private void takePhoto() {
        Path csv = tripFile;
        Sample position = latest;
        cameraBusy = true; photo.setEnabled(false);
        cameraWorker.execute(() -> {
            Path image = Path.of(csv.toString().replaceFirst("\\.csv$", ""), "photo-" + Instant.now().toString().replace(':', '-') + ".jpg");
            Process process = null;
            try {
                Files.createDirectories(image.getParent());
                process = new ProcessBuilder("rpicam-still", "--nopreview", "--timeout", "1000", "--output", image.toString())
                        .redirectErrorStream(true).redirectOutput(image.resolveSibling(image.getFileName() + ".log").toFile()).start();
                if (!process.waitFor(12, TimeUnit.SECONDS)) { process.destroyForcibly(); throw new IOException("Camera timed out"); }
                if (process.exitValue() != 0) throw new IOException("Camera failed; see photo log");
                if (position != null) {
                    try (TripLog metadata = new TripLog(image.resolveSibling(image.getFileName() + ".csv"))) { metadata.append(position); }
                }
                SwingUtilities.invokeLater(() -> recordingStatus.setText("Photo saved · " + image.getFileName()));
            } catch (IOException e) { SwingUtilities.invokeLater(() -> error("Camera unavailable: " + e.getMessage())); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally {
                if (process != null && process.isAlive()) process.destroyForcibly();
                SwingUtilities.invokeLater(() -> { cameraBusy = false; updateControls(); });
            }
        });
    }
    private void error(String message) { JOptionPane.showMessageDialog(this, message, "WEG", JOptionPane.WARNING_MESSAGE); }

    private static final class Plot extends JPanel {
        private static final long serialVersionUID = 1L;
        private final boolean route;
        private boolean pressure;
        private final List<Sample> samples = new ArrayList<>();
        Plot(boolean route) { this.route = route; setBackground(PANEL); setPreferredSize(new Dimension(350, 290)); }
        void clear() { samples.clear(); repaint(); }
        void gap() { if (samples.isEmpty() || samples.get(samples.size()-1) != null) samples.add(null); }
        void add(Sample s) { samples.add(s); if (samples.size() > 1800) samples.remove(0); repaint(); }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(MUTED); g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            g.drawString(route ? "ROUTE · north up · no basemap" : (pressure ? "PRESSURE · hPa · recent samples" : "TEMPERATURE · °C · recent samples"), 18, 26);
            List<double[]> points = new ArrayList<>();
            double referenceLat = samples.stream().filter(s -> s != null && s.fix()).mapToDouble(Sample::latitude).findFirst().orElse(0);
            double lonScale = Math.cos(Math.toRadians(referenceLat));
            for (int i=0; i<samples.size(); i++) {
                Sample s = samples.get(i);
                if (s == null || (route ? !s.fix() : !Double.isFinite(pressure ? s.pressureHpa() : s.temperatureC()))) { points.add(null); continue; }
                points.add(route ? new double[]{s.longitude()*lonScale, s.latitude()} : new double[]{i, pressure ? s.pressureHpa() : s.temperatureC()});
            }
            List<double[]> valid = points.stream().filter(Objects::nonNull).toList();
            if (valid.isEmpty()) { g.drawString("Waiting for samples", 18, 65); g.dispose(); return; }
            double minX = valid.stream().mapToDouble(p->p[0]).min().orElse(0), maxX = valid.stream().mapToDouble(p->p[0]).max().orElse(1);
            double minY = valid.stream().mapToDouble(p->p[1]).min().orElse(0), maxY = valid.stream().mapToDouble(p->p[1]).max().orElse(1);
            int width = getWidth()-64, height = getHeight()-96;
            if (width <= 0 || height <= 0) { g.dispose(); return; }
            double rangeX = Math.max(0.00001, maxX-minX), rangeY = Math.max(route ? 0.00001 : 0.1, maxY-minY);
            if (!route) maxY = minY + rangeY;
            double scaleX = width/rangeX, scaleY = height/rangeY;
            if (route) { scaleX = Math.min(scaleX, scaleY); scaleY = scaleX; }
            g.setColor(new Color(47, 65, 75));
            for (int i=0; i<=4; i++) g.drawLine(32, 50+i*height/4, getWidth()-32, 50+i*height/4);
            if (!route) { g.setColor(MUTED); g.drawString(format(maxY, 1), 18, 46); g.drawString(format(minY, 1), 18, getHeight()-18); }
            g.setStroke(new BasicStroke(2.5f)); g.setColor(ACCENT);
            Point previous = null;
            for (double[] p : points) {
                if (p == null) { previous = null; continue; }
                Point current = new Point((int)(32+(width-rangeX*scaleX)/2+(p[0]-minX)*scaleX), (int)(50+height-(height-rangeY*scaleY)/2-(p[1]-minY)*scaleY));
                if (previous != null) g.drawLine(previous.x, previous.y, current.x, current.y);
                previous = current;
            }
            if (previous != null) g.fillOval(previous.x-4, previous.y-4, 8, 8);
            g.dispose();
        }
    }
}
