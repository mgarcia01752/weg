package weg.das;

import java.util.Locale;

/** Compatibility helpers; missing values never become valid zero readings. */
public final class Temperature {
    public static double LOW_TEMP = Double.NaN, HIGH_TEMP = Double.NaN;
    private Temperature() {}
    public static synchronized String getTemp(String text) {
        try {
            double value = Double.parseDouble(text);
            if (!Double.isFinite(value)) return "—";
            LOW_TEMP = Double.isNaN(LOW_TEMP) ? value : Math.min(LOW_TEMP, value);
            HIGH_TEMP = Double.isNaN(HIGH_TEMP) ? value : Math.max(HIGH_TEMP, value);
            return String.format(Locale.ROOT, "%.1f", value);
        } catch (NumberFormatException | NullPointerException e) { return "—"; }
    }
    public static String FahrenheitToCelsius(String text) {
        try {
            double value = Double.parseDouble(text);
            return Double.isFinite(value) ? String.format(Locale.ROOT, "%.1f", (value - 32) * 5 / 9) : "—";
        } catch (NumberFormatException | NullPointerException e) { return "—"; }
    }
}
