package weg.core;

import java.time.Duration;
import java.time.Instant;

/** Distance excludes missing fixes, non-increasing timestamps and gaps over ten seconds. */
public final class TripStats {
    private Sample previous;
    private Instant start, latest;
    private double distance, speed = Double.NaN, bearing = Double.NaN;
    public void accept(Sample s) {
        if (start == null) start = s.time();
        latest = s.time();
        speed = Double.NaN; bearing = Double.NaN;
        if (s.fix() && previous != null && previous.fix()) {
            double seconds = Duration.between(previous.time(), s.time()).toMillis() / 1000.0;
            if (seconds > 0 && seconds <= 10) {
                double delta = Sample.distanceM(previous, s);
                distance += delta;
                speed = delta / seconds * 3.6;
                if (delta >= 1) bearing = Sample.bearing(previous, s);
            }
        }
        previous = s;
    }
    public void gap() { previous = null; speed = Double.NaN; bearing = Double.NaN; }
    public double distanceKm() { return distance / 1000; }
    public double speedKph() { return speed; }
    public double bearing() { return bearing; }
    public long elapsedSeconds() {
        return start == null || latest == null ? 0 : Math.max(0, Duration.between(start, latest).toSeconds());
    }
}
