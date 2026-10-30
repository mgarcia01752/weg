package weg.core;

import java.nio.file.Path;

public record Options(String mode, String host, int port, Path replay, Path output,
                      boolean faults, int samples, boolean help, boolean headless) {
    public static Options parse(String[] args) {
        String mode = "simulate", host = "127.0.0.1";
        int port = 5000, samples = 10;
        Path replay = null, output = Path.of(System.getProperty("user.home"), ".weg", "trips");
        boolean faults = false, help = false, headless = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help", "-h" -> help = true;
                case "--faults" -> faults = true;
                case "--headless" -> headless = true;
                case "--mode" -> mode = value(args, ++i);
                case "--host" -> host = value(args, ++i);
                case "--port" -> port = Integer.parseInt(value(args, ++i));
                case "--replay" -> { replay = Path.of(value(args, ++i)); mode = "replay"; }
                case "--output" -> output = Path.of(value(args, ++i));
                case "--samples" -> samples = Integer.parseInt(value(args, ++i));
                default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        if (!mode.matches("simulate|live|replay")) throw new IllegalArgumentException("Mode must be simulate, live or replay");
        if (mode.equals("replay") && replay == null) throw new IllegalArgumentException("Replay requires --replay FILE");
        if (port < 1 || port > 65535 || samples < 1) throw new IllegalArgumentException("Port or sample count out of range");
        return new Options(mode, host, port, replay, output, faults, samples, help, headless);
    }
    private static String value(String[] args, int i) {
        if (i >= args.length) throw new IllegalArgumentException("Missing option value");
        return args[i];
    }
    public SampleSource source() throws java.io.IOException {
        return switch (mode) {
            case "live" -> new SocketSource(host, port);
            case "replay" -> new ReplaySource(replay);
            default -> new Simulator(faults);
        };
    }
    public static String usage() {
        return """
                WEG · Wearable Excursion Gear
                Usage: ./scripts/run.sh [options]
                  --mode simulate|live|replay   Default: simulate
                  --faults                     Simulate GPS loss and disconnects
                  --host HOST --port PORT      Live bridge (127.0.0.1:5000)
                  --replay FILE                Play a WEG CSV, one row per second
                  --output DIRECTORY           Trip folder (default ~/.weg/trips)
                  --headless --samples N       Record N attempts without a display
                  --help                       Show this help
                Camera is optional: install rpicam-still on the Pi to take photos.
                """;
    }
}
