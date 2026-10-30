package weg.core;

import weg.ui.WegMainUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

/** Optional display test: xvfb-run java ... weg.core.UiSmokeTest OUTPUT_DIRECTORY. */
public final class UiSmokeTest {
    private static JButton button(Container root, String text) {
        for (Component component : root.getComponents()) {
            if (component instanceof JButton b && text.equals(b.getText())) return b;
            if (component instanceof Container nested) { JButton found = button(nested, text); if (found != null) return found; }
        }
        return null;
    }
    public static void main(String[] args) throws Exception {
        Path output = Path.of(args[0]); Files.createDirectories(output);
        AtomicReference<WegMainUI> reference = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> { error.printStackTrace(); failure.compareAndSet(null, error); });
        SwingUtilities.invokeAndWait(() -> {
            WegMainUI ui = new WegMainUI(Options.parse(new String[]{"--output", output.toString()}));
            reference.set(ui); ui.setVisible(true);
        });
        WegMainUI ui = reference.get();
        try {
            Thread.sleep(1200);
            SwingUtilities.invokeAndWait(() -> button(ui, "Start trip").doClick());
            Thread.sleep(3200);
            SwingUtilities.invokeAndWait(() -> {
                BufferedImage image = new BufferedImage(ui.getWidth(), ui.getHeight(), BufferedImage.TYPE_INT_RGB);
                ui.paint(image.getGraphics());
                try { ImageIO.write(image, "png", output.resolve("dashboard.png").toFile()); }
                catch (java.io.IOException e) { throw new RuntimeException(e); }
                button(ui, "Stop trip").doClick();
                button(ui, "Disconnect").doClick();
            });
            Thread.sleep(300);
            SwingUtilities.invokeAndWait(() -> button(ui, "Connect").doClick());
            Thread.sleep(1200);
            SwingUtilities.invokeAndWait(() -> { ui.setSize(800, 480); });
            Thread.sleep(200);
            SwingUtilities.invokeAndWait(() -> {
                BufferedImage image = new BufferedImage(ui.getWidth(), ui.getHeight(), BufferedImage.TYPE_INT_RGB);
                ui.paint(image.getGraphics());
                try { ImageIO.write(image, "png", output.resolve("dashboard-small.png").toFile()); }
                catch (java.io.IOException e) { throw new RuntimeException(e); }
            });
        } finally {
            SwingUtilities.invokeAndWait(() -> ui.dispatchEvent(new WindowEvent(ui, WindowEvent.WINDOW_CLOSING)));
        }
        Thread.sleep(700);
        if (failure.get() != null) throw new AssertionError("UI thread failed", failure.get());
        try (var files = Files.list(output)) {
            Path csv = files.filter(p -> p.toString().endsWith(".csv")).findFirst().orElseThrow();
            if (Files.readAllLines(csv).size() < 3) throw new AssertionError("Recording did not persist samples");
        }
        if (ui.isDisplayable()) throw new AssertionError("Window did not close");
        System.out.println("PASS: GUI start, recording, stop, reconnect, resize and shutdown");
    }
}
