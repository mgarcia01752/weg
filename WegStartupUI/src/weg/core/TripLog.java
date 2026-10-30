package weg.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.Locale;

/** Append-and-flush CSV is the recoverable source; GPX can be regenerated anytime. */
public final class TripLog implements AutoCloseable {
    public static final String HEADER = "time,temperature_c,pressure_hpa,uv_index,latitude,longitude,elevation_m,satellites,fix";
    private final BufferedWriter writer;
    private final Path path;
    public TripLog(Path path) throws IOException {
        this.path = path.toAbsolutePath();
        Files.createDirectories(this.path.getParent());
        writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        try { writer.write(HEADER); writer.newLine(); writer.flush(); }
        catch (IOException e) {
            try { writer.close(); } catch (IOException close) { e.addSuppressed(close); }
            throw e;
        }
    }
    public Path path() { return path; }
    public synchronized void append(Sample s) throws IOException {
        writer.write(String.format(Locale.ROOT, "%s,%s,%s,%s,%s,%s,%s,%d,%b%n", s.time(),
                s.temperatureC(), s.pressureHpa(), s.uvIndex(), s.latitude(), s.longitude(),
                s.elevationM(), s.satellites(), s.fix()));
        writer.flush();
    }
    @Override public synchronized void close() throws IOException { writer.close(); }
    public static Sample decode(String line) throws IOException {
        try {
            String[] f = line.split(",", -1);
            if (f.length != 9 || !(f[8].equals("true") || f[8].equals("false")))
                throw new IllegalArgumentException("Invalid columns");
            return new Sample(Instant.parse(f[0]), Double.parseDouble(f[1]), Double.parseDouble(f[2]),
                    Double.parseDouble(f[3]), Double.parseDouble(f[4]), Double.parseDouble(f[5]),
                    Double.parseDouble(f[6]), Integer.parseInt(f[7]), Boolean.parseBoolean(f[8]));
        } catch (RuntimeException e) { throw new IOException("Invalid trip row", e); }
    }
    public static void exportGpx(Path csv, Path destination) throws IOException {
        // Validate and write into a sibling temporary file so errors cannot publish partial GPX.
        Path target = destination.toAbsolutePath();
        if (target.equals(csv.toAbsolutePath())) throw new IOException("GPX must not overwrite its CSV source");
        if (Files.exists(target)) throw new FileAlreadyExistsException(target.toString());
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), ".weg-gpx-", ".tmp");
        try {
            try (ReplaySource source = new ReplaySource(csv);
                 BufferedWriter out = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gpx version=\"1.1\" creator=\"WEG\" xmlns=\"http://www.topografix.com/GPX/1/1\"><trk><name>WEG excursion</name>\n");
                boolean segment = false;
                Sample previous = null;
                for (Sample sample; (sample = source.read()) != null;) {
                    boolean gap = previous != null && (sample.time().isBefore(previous.time())
                            || java.time.Duration.between(previous.time(), sample.time()).toSeconds() > 10);
                    if ((!sample.fix() || gap) && segment) { out.write("</trkseg>\n"); segment = false; }
                    if (sample.fix()) {
                        if (!segment) { out.write("<trkseg>\n"); segment = true; }
                        out.write(String.format(Locale.ROOT, "<trkpt lat=\"%.8f\" lon=\"%.8f\">", sample.latitude(), sample.longitude()));
                        if (Double.isFinite(sample.elevationM())) out.write("<ele>" + sample.elevationM() + "</ele>");
                        out.write("<time>" + sample.time() + "</time></trkpt>\n");
                    }
                    previous = sample;
                }
                if (segment) out.write("</trkseg>\n");
                out.write("</trk></gpx>\n");
            }
            Files.move(temp, target);
        } finally { Files.deleteIfExists(temp); }
    }
}
