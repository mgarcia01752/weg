package weg.das;

import java.io.IOException;

/** Optional preview for legacy callers; the main recorder captures photos on demand. */
public class Camera implements Runnable, AutoCloseable {
    public static final int FULL_SCREEN = 0, PREVIEW_SCREEN = 1;
    private Process process;
    public Camera() {}
    public Camera(int view) {
        if (view != FULL_SCREEN && view != PREVIEW_SCREEN) throw new IllegalArgumentException("Invalid camera view");
    }
    @Override public void run() { start(); }
    public synchronized void start() {
        if (process != null && process.isAlive()) return;
        try { process = new ProcessBuilder("rpicam-vid", "--timeout", "0").inheritIO().start(); }
        catch (IOException e) { throw new IllegalStateException("Camera preview unavailable", e); }
    }
    public synchronized void killProcess() { if (process != null) { process.destroy(); process = null; } }
    @Override public void close() { killProcess(); }
}
