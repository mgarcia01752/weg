package weg.ui;

import weg.core.*;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.time.Instant;
import javax.swing.SwingUtilities;

public final class WegStartupUI {
    private WegStartupUI() {}
    public static void main(String[] args) {
        try {
            Options options = Options.parse(args);
            if (options.help()) { System.out.print(Options.usage()); return; }
            if (options.headless()) { record(options); return; }
            if (GraphicsEnvironment.isHeadless())
                throw new IllegalArgumentException("No display available. Use --headless --samples 10.");
            SwingUtilities.invokeLater(() -> new WegMainUI(options).setVisible(true));
        } catch (Exception e) {
            System.err.println("WEG: " + e.getMessage());
            System.exit(1);
        }
    }
    private static void record(Options options) throws Exception {
        Path file = options.output().resolve(options.mode() + "-trip-" + Instant.now().toString().replace(':', '-') + ".csv");
        int count = 0;
        try (SampleSource source = options.source(); TripLog log = new TripLog(file)) {
            System.out.println(source.description());
            for (int i = 0; i < options.samples(); i++) {
                try {
                    Sample sample = source.read();
                    if (sample == null) break;
                    log.append(sample); count++;
                } catch (java.io.IOException e) {
                    if (options.mode().equals("replay")) throw e;
                    log.append(Sample.unavailable(Instant.now()));
                    System.err.println("Sample unavailable: " + e.getMessage());
                }
                if (i + 1 < options.samples()) Thread.sleep(1000);
            }
        }
        if (count == 0) throw new java.io.IOException("No valid samples received; inspect the trip log: " + file);
        TripLog.exportGpx(file, Path.of(file.toString().replaceFirst("\\.csv$", ".gpx")));
        System.out.println("Recorded " + count + " samples: " + file.toAbsolutePath());
    }
}
