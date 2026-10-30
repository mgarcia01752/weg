package weg.das;

public final class UV {
    private UV() {}
    public static Integer getUV(String text) {
        try { int value = Integer.parseInt(text); return value >= 0 ? value : null; }
        catch (NumberFormatException e) { return null; }
    }
}
