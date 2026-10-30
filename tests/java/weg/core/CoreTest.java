package weg.core;

import weg.das.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.concurrent.*;
import javax.xml.parsers.DocumentBuilderFactory;

/** Dependency-free behavioral tests; run with scripts/test.sh. */
public final class CoreTest {
    private static int checks;
    private static void check(boolean value, String message) {
        checks++; if (!value) throw new AssertionError(message);
    }
    private static void near(double actual, double expected, String message) {
        check(Math.abs(actual - expected) < 0.000001, message + ": " + actual);
    }
    private interface Operation { void run() throws Exception; }
    private static void rejects(Operation operation, String message) throws Exception {
        try { operation.run(); } catch (IOException | IllegalArgumentException expected) { checks++; return; }
        throw new AssertionError(message);
    }
    private static String sentence(String body) { return SensorProtocol.checksum(body); }
    private static String gga = sentence("GPGGA,070024.000,4009.3988,N,07452.0023,W,1,07,1.26,65.1,M,-34.0,M,,");
    private static Sample sample(long time, double lat, double lon, boolean fix) {
        return new Sample(Instant.ofEpochSecond(time), -5, 1000, 3, fix ? lat : Double.NaN,
                fix ? lon : Double.NaN, fix ? 100 : Double.NaN, fix ? 8 : 0, fix);
    }
    public static void main(String[] args) throws Exception {
        Sample parsed = SensorProtocol.parse("600:|3|-5.0|1000|" + gga, Instant.EPOCH);
        near(parsed.latitude(), 40 + 9.3988 / 60, "NMEA latitude");
        near(parsed.longitude(), -(74 + 52.0023 / 60), "NMEA longitude");
        near(parsed.temperatureC(), (-5 - 32) * 5.0 / 9, "Negative Fahrenheit");
        near(parsed.elevationM(), 65.1, "Elevation");
        near(SensorProtocol.coordinate("1230.0000", "S", true), -12.5, "Southern hemisphere");
        rejects(() -> SensorProtocol.coordinate("9060.0", "N", true), "Invalid minutes");
        rejects(() -> SensorProtocol.coordinate("9000.1", "N", true), "Invalid latitude");
        rejects(() -> SensorProtocol.coordinate("1230.0", "W", true), "Wrong hemisphere");
        rejects(() -> SensorProtocol.parse("600:|3|5|1000|" + gga.replace("4009", "4109"), Instant.EPOCH), "Bad checksum");
        rejects(() -> SensorProtocol.parse("600:|3|oops|1000|" + gga, Instant.EPOCH), "Bad temperature");
        rejects(() -> SensorProtocol.parse("600:|3|NaN|1000|" + gga, Instant.EPOCH), "Non-finite sensor value");
        String noFix = sentence("GNGGA,070024.000,,,,,0,00,99.9,,M,,M,,");
        Sample missing = SensorProtocol.parse("600:||||" + noFix, Instant.EPOCH);
        check(!missing.fix() && Double.isNaN(missing.latitude()) && Double.isNaN(missing.pressureHpa()), "Missing values stay missing");
        String rmc = sentence("GNRMC,070024.000,A,1230.0000,S,07430.0000,E,0,0,291026,,,A");
        Sample r = SensorProtocol.parse("600:|1|32|1000|" + rmc, Instant.EPOCH);
        near(r.latitude(), -12.5, "RMC latitude"); check(r.fix(), "RMC fix");
        Gps legacy = new Gps(); check(legacy.parse(gga), "Legacy parser");
        near(legacy.getLatitudeDecimal(), parsed.latitude(), "Legacy corrected coordinate");
        check(!legacy.parse(null), "Null GPS tolerated");
        check(Temperature.getTemp("-5.0").equals("-5.0"), "Legacy negative temperature");
        check(Temperature.getTemp("oops").equals("—"), "Legacy malformed temperature");
        Barometer.getBarometer("1000"); Barometer.getBarometer("1001");
        check(Barometer.getPressureTrendStatus().equals("Rising"), "Pressure trend initializes");
        check(UV.getUV("bad") == null, "Invalid UV does not become zero");
        TripStats stats = new TripStats();
        Sample first = sample(100, 40, -74, true), second = sample(101, 40.00001, -74, true);
        stats.accept(first); stats.accept(second);
        check(stats.distanceKm() > 0 && stats.speedKph() > 0, "Distance and speed");
        near(stats.bearing(), 0, "Northward course");
        double distance = stats.distanceKm();
        stats.accept(sample(102, 0, 0, false)); stats.accept(sample(103, 41, -74, true));
        near(stats.distanceKm(), distance, "No invented distance across missing fix");
        stats.accept(sample(200, 42, -74, true)); near(stats.distanceKm(), distance, "Long gap excluded");
        stats.gap(); stats.accept(sample(201, 43, -74, true)); near(stats.distanceKm(), distance, "Disconnect excluded");
        Path folder = Files.createTempDirectory("weg-core-test-");
        Path csv = folder.resolve("trip.csv"), gpx = folder.resolve("trip.gpx");
        try {
            try (TripLog log = new TripLog(csv)) { log.append(first); log.append(sample(102, 0, 0, false)); log.append(second); }
            try (ReplaySource replay = new ReplaySource(csv)) {
                check(first.equals(replay.read()), "CSV round trip");
                check(!replay.read().fix(), "Missing fix round trip"); replay.read();
                check(replay.read() == null, "Replay terminates at EOF");
            }
            TripLog.exportGpx(csv, gpx);
            var factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true);
            var document = factory.newDocumentBuilder().parse(gpx.toFile());
            check(document.getElementsByTagNameNS("http://www.topografix.com/GPX/1/1", "trkpt").getLength() == 2, "Only fixed positions exported");
            check(document.getElementsByTagNameNS("http://www.topografix.com/GPX/1/1", "trkseg").getLength() == 2, "GPS gaps create separate GPX segments");
            rejects(() -> TripLog.exportGpx(csv, gpx), "Never overwrite existing GPX");
            rejects(() -> TripLog.exportGpx(csv, csv), "Never overwrite CSV");
            rejects(() -> new TripLog(csv), "Never overwrite a trip");
            Files.writeString(folder.resolve("bad.csv"), TripLog.HEADER + "\nbad\n");
            rejects(() -> TripLog.exportGpx(folder.resolve("bad.csv"), folder.resolve("bad.gpx")), "Bad replay fails export");
            check(!Files.exists(folder.resolve("bad.gpx")), "Failed export not published");
        } finally {
            try (var paths = Files.walk(folder)) { for (Path p : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.delete(p); }
        }
        rejects(() -> Options.parse(new String[]{"--port", "0"}), "Invalid port");
        rejects(() -> Options.parse(new String[]{"--host"}), "Missing argument");
        rejects(() -> Options.parse(new String[]{"--mode", "replay"}), "Missing replay file");
        Simulator simulator = new Simulator(true);
        int failures = 0, missingFixes = 0;
        for (int i=0; i<40; i++) {
            try { if (!simulator.read().fix()) missingFixes++; } catch (IOException e) { failures++; }
        }
        check(failures == 3 && missingFixes == 4, "Simulator exercises recovery and fix loss");
        socketCase("600:|3|50|1000|" + gga + "\n", false);
        socketCase("ERR sensor unavailable\n", true);
        socketCase("x".repeat(600), true);
        socketCase("", true);
        try (ServerSocket server = new ServerSocket(0)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> peer = executor.submit(() -> {
                try (Socket socket = server.accept()) {
                    socket.getInputStream().readNBytes(4);
                    for (int i=0; i<60; i++) { socket.getOutputStream().write('x'); Thread.sleep(100); }
                } catch (Exception ignored) { /* Expected when the deadline closes the client. */ }
            });
            long start = System.nanoTime();
            try (SocketSource source = new SocketSource("127.0.0.1", server.getLocalPort())) {
                rejects(source::read, "Trickling peer must time out");
                check((System.nanoTime() - start) / 1e9 < 5, "Whole-frame deadline");
            } finally { peer.get(7, TimeUnit.SECONDS); executor.shutdownNow(); }
        }
        System.out.println("PASS: " + checks + " Java behavioral checks");
    }
    private static void socketCase(String response, boolean failure) throws Exception {
        try (ServerSocket server = new ServerSocket(0)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> peer = executor.submit(() -> {
                try (Socket socket = server.accept()) {
                    String command = new String(socket.getInputStream().readNBytes(4), StandardCharsets.US_ASCII);
                    if (!command.equals("600\n")) throw new AssertionError("Wrong wire request");
                    for (byte b : response.getBytes(StandardCharsets.US_ASCII)) socket.getOutputStream().write(b);
                } catch (IOException e) { throw new UncheckedIOException(e); }
            });
            try (SocketSource source = new SocketSource("127.0.0.1", server.getLocalPort())) {
                if (failure) rejects(source::read, "Invalid socket response");
                else check(source.read().fix(), "Socket response parses");
            } finally { peer.get(2, TimeUnit.SECONDS); executor.shutdownNow(); }
        }
    }
}
