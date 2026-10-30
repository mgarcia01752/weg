package weg.das;

public final class Barometer {
    private static Integer previous, current;
    private Barometer() {}
    public static synchronized Integer getBarometer(String text) {
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) return null;
            previous = current; current = value; return value;
        } catch (NumberFormatException e) { return null; }
    }
    public static synchronized String getPressureTrendStatus() {
        if (previous == null) return "Initial Reading";
        return current > previous ? "Rising" : current < previous ? "Falling" : "Steady";
    }
}
