package weg.core;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;

/** Legacy PIC frame: 600:|UV|temperature Fahrenheit|pressure hPa|NMEA. */
public final class SensorProtocol {
    private SensorProtocol() {}
    public static Sample parse(String frame, Instant received) throws IOException {
        try {
            String[] fields = frame.strip().split("\\|", -1);
            if (fields.length != 5 || !fields[0].equals("600:"))
                throw new IllegalArgumentException("Expected full sensor frame");
            String sentence = fields[4].strip();
            validateChecksum(sentence);
            String[] gps = sentence.substring(0, sentence.indexOf('*')).split(",", -1);
            boolean gga = gps[0].matches("\\$[A-Z]{2}GGA");
            boolean rmc = gps[0].matches("\\$[A-Z]{2}RMC");
            if ((!gga && !rmc) || gps.length < (gga ? 15 : 10))
                throw new IllegalArgumentException("Unsupported or incomplete GPS sentence");
            int quality = gga ? Integer.parseInt(gps[6]) : 0;
            if (quality < 0 || quality > 8 || (rmc && !gps[2].matches("[AV]")))
                throw new IllegalArgumentException("Invalid GPS fix status");
            boolean fix = gga ? quality > 0 : gps[2].equals("A");
            int latIndex = gga ? 2 : 3;
            double lat = fix ? coordinate(gps[latIndex], gps[latIndex + 1], true) : Double.NaN;
            double lon = fix ? coordinate(gps[latIndex + 2], gps[latIndex + 3], false) : Double.NaN;
            double elevation = gga && fix && gps[10].equals("M") ? number(gps[9]) : Double.NaN;
            int satellites = gga && !gps[7].isEmpty() ? Integer.parseInt(gps[7]) : 0;
            double fahrenheit = number(fields[2]);
            double pressure = number(fields[3]);
            double uv = number(fields[1]);
            if (pressure <= 0 || uv < 0) throw new IllegalArgumentException("Invalid sensor range");
            return new Sample(received, (fahrenheit - 32) * 5 / 9, pressure, uv,
                    lat, lon, elevation, satellites, fix);
        } catch (RuntimeException ex) {
            throw new IOException("Invalid sensor frame: " + ex.getMessage(), ex);
        }
    }
    private static double number(String value) {
        if (value.isBlank()) return Double.NaN;
        double result = Double.parseDouble(value);
        if (!Double.isFinite(result)) throw new IllegalArgumentException("Non-finite measurement");
        return result;
    }
    public static double coordinate(String value, String hemisphere, boolean latitude) {
        if (!value.matches(latitude ? "[0-9]{4}(\\.[0-9]+)?" : "[0-9]{5}(\\.[0-9]+)?"))
            throw new IllegalArgumentException("Invalid coordinate format");
        if (!(latitude ? hemisphere.matches("[NS]") : hemisphere.matches("[EW]")))
            throw new IllegalArgumentException("Invalid hemisphere");
        double raw = Double.parseDouble(value);
        int degrees = (int)(raw / 100);
        double minutes = raw - degrees * 100;
        double decimal = degrees + minutes / 60;
        if (minutes >= 60 || decimal > (latitude ? 90 : 180))
            throw new IllegalArgumentException("Coordinate out of range");
        return hemisphere.equals("S") || hemisphere.equals("W") ? -decimal : decimal;
    }
    public static void validateChecksum(String sentence) {
        int star = sentence.indexOf('*');
        if (!sentence.startsWith("$") || star < 1 || star + 3 != sentence.length())
            throw new IllegalArgumentException("Missing NMEA checksum");
        int sum = 0;
        for (int i = 1; i < star; i++) sum ^= sentence.charAt(i);
        if (sum != Integer.parseInt(sentence.substring(star + 1), 16))
            throw new IllegalArgumentException("NMEA checksum mismatch");
    }
    public static String checksum(String body) {
        int sum = 0;
        for (char c : body.toCharArray()) sum ^= c;
        return "$" + body + String.format(Locale.ROOT, "*%02X", sum);
    }
}
