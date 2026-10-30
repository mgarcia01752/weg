package weg.das;

import weg.core.Sample;
import weg.core.SensorProtocol;
import java.time.Instant;
import java.util.*;

/** Compatibility facade around the validated NMEA parser. */
public class Gps {
    private Sample sample;
    private Sample previous;
    private List<String> fields = List.of();
    private double lastLat = Double.NaN, lastLon = Double.NaN, lastSpeed;
    private long lastNanos;
    public boolean parse(String sentence) {
        if (sentence == null) return false;
        try {
            Sample next = SensorProtocol.parse("600:||||" + sentence.strip(), Instant.now());
            previous = sample; sample = next;
            fields = List.copyOf(Arrays.asList(sentence.strip().split(",", -1)));
            return true;
        } catch (java.io.IOException e) { return false; }
    }
    public String getUTC() {
        return fields.size() < 2 ? "—" : utcToClock(fields.get(1));
    }
    public String getLatitude() { return display(getLatitudeDecimal(), true); }
    public String getLongitude() { return display(getLongitudeDecimal(), false); }
    public double getLatitudeDecimal() { return sample == null ? Double.NaN : sample.latitude(); }
    public double getLongitudeDecimal() { return sample == null ? Double.NaN : sample.longitude(); }
    public String getNSIndicator() { return sample == null || !sample.fix() ? "" : sample.latitude() < 0 ? "S" : "N"; }
    public String getEWIndicator() { return sample == null || !sample.fix() ? "" : sample.longitude() < 0 ? "W" : "E"; }
    public Integer getNumSatellites() { return sample == null ? 0 : sample.satellites(); }
    public Integer getFixIndicator() { return sample != null && sample.fix() ? 1 : 0; }
    public List<String> getCurrentGpsData() { return fields; }
    private static String display(double decimal, boolean latitude) {
        if (!Double.isFinite(decimal)) return "—";
        String hemisphere = latitude ? decimal < 0 ? "S" : "N" : decimal < 0 ? "W" : "E";
        return hemisphere + " " + dms(Math.abs(decimal));
    }
    private static String dms(double value) {
        int degrees = (int)value;
        double minutes = (value - degrees) * 60;
        return String.format(Locale.ROOT, "%d° %d' %.1f\"", degrees, (int)minutes, (minutes - (int)minutes) * 60);
    }
    /** Historical method accepts NMEA ddmm.mmmm rather than decimal degrees. */
    public static String decimalToDMS(double value) {
        double absolute = Math.abs(value);
        return dms((int)(absolute / 100) + (absolute % 100) / 60);
    }
    public static String utcToClock(String value) {
        if (value == null || !value.matches("[0-9]{6}(\\.[0-9]+)?")) return "—";
        return value.substring(0,2) + ":" + value.substring(2,4) + ":" + value.substring(4,6);
    }
    public String compassUpdate(String latitude, String longitude) {
        if (previous == null || sample == null || !previous.fix() || !sample.fix() || Sample.distanceM(previous, sample) < 1) return "—";
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[(int)Math.round(Sample.bearing(previous, sample) / 45) % 8];
    }
    /** Historical API returns speed in mph, not minutes per mile. */
    public Double updatePace(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)) { lastNanos = 0; return Double.NaN; }
        long now = System.nanoTime();
        if (lastNanos != 0) {
            double seconds = (now - lastNanos) / 1e9;
            if (seconds > 0 && seconds <= 10) lastSpeed = distance(lastLat, lastLon, latitude, longitude, "M") / seconds * 3600;
            else lastSpeed = Double.NaN;
        }
        lastLat = latitude; lastLon = longitude; lastNanos = now; return lastSpeed;
    }
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        Sample a = new Sample(Instant.EPOCH, Double.NaN, Double.NaN, Double.NaN, lat1, lon1, Double.NaN, 0, true);
        Sample b = new Sample(Instant.EPOCH, Double.NaN, Double.NaN, Double.NaN, lat2, lon2, Double.NaN, 0, true);
        double metres = Sample.distanceM(a, b);
        return "K".equals(unit) ? metres / 1000 : "N".equals(unit) ? metres / 1852 : metres / 1609.344;
    }
}
