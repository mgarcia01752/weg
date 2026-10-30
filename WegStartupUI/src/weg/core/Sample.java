package weg.core;

import java.time.Instant;

/** Immutable observation; NaN means unavailable, never a fabricated zero. */
public record Sample(Instant time, double temperatureC, double pressureHpa,
                     double uvIndex, double latitude, double longitude,
                     double elevationM, int satellites, boolean fix) {
    public Sample {
        if (time == null) throw new IllegalArgumentException("Missing timestamp");
        if (fix && (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                || Math.abs(latitude) > 90 || Math.abs(longitude) > 180))
            throw new IllegalArgumentException("Invalid position");
        if (satellites < 0) throw new IllegalArgumentException("Invalid satellite count");
        for (double value : new double[]{temperatureC, pressureHpa, uvIndex, elevationM, latitude, longitude})
            if (Double.isInfinite(value)) throw new IllegalArgumentException("Infinite measurement");
    }
    public static Sample unavailable(Instant time) {
        return new Sample(time, Double.NaN, Double.NaN, Double.NaN,
                Double.NaN, Double.NaN, Double.NaN, 0, false);
    }
    public static double distanceM(Sample a, Sample b) {
        double lat = Math.toRadians(b.latitude - a.latitude);
        double lon = Math.toRadians(b.longitude - a.longitude);
        double h = Math.pow(Math.sin(lat / 2), 2) + Math.cos(Math.toRadians(a.latitude))
                * Math.cos(Math.toRadians(b.latitude)) * Math.pow(Math.sin(lon / 2), 2);
        return 6371000 * 2 * Math.asin(Math.sqrt(Math.max(0, Math.min(1, h))));
    }
    public static double bearing(Sample a, Sample b) {
        double d = Math.toRadians(b.longitude - a.longitude);
        double x = Math.sin(d) * Math.cos(Math.toRadians(b.latitude));
        double y = Math.cos(Math.toRadians(a.latitude)) * Math.sin(Math.toRadians(b.latitude))
                - Math.sin(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) * Math.cos(d);
        return (Math.toDegrees(Math.atan2(x, y)) + 360) % 360;
    }
}
