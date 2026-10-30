package weg.core;

import java.io.IOException;
import java.time.Instant;

/** Deterministic route and optional faults; no network or hardware required. */
public final class Simulator implements SampleSource {
    private final boolean faults;
    private int tick;
    public Simulator(boolean faults) { this.faults = faults; }
    @Override public Sample read() throws IOException {
        int n = tick++;
        if (faults && n % 40 >= 30 && n % 40 <= 32) throw new IOException("Simulated sensor disconnect");
        boolean fix = !(faults && n % 40 >= 20 && n % 40 <= 23);
        double angle = n * 0.008;
        return new Sample(Instant.now(), 18 + 3 * Math.sin(n * 0.02),
                845 + 2 * Math.cos(n * 0.015), 3 + Math.sin(n * 0.01),
                fix ? 39.7392 + 0.0015 * Math.sin(angle) : Double.NaN,
                fix ? -104.9903 + 0.002 * Math.cos(angle) : Double.NaN,
                fix ? 1609 + 8 * Math.sin(angle) : Double.NaN, fix ? 9 : 0, fix);
    }
    @Override public String description() { return faults ? "SIMULATION · fault scenarios" : "SIMULATION · foothills loop"; }
}
