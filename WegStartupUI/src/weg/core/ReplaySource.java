package weg.core;

import java.io.*;
import java.nio.file.*;

/** Streaming replay; samples retain their recorded timestamps. */
public final class ReplaySource implements SampleSource {
    private final BufferedReader reader;
    private final Path file;
    private int row = 1;
    public ReplaySource(Path file) throws IOException {
        this.file = file;
        reader = Files.newBufferedReader(file);
        try {
            if (!TripLog.HEADER.equals(reader.readLine())) throw new IOException("Unsupported trip CSV header");
        } catch (IOException e) {
            try { reader.close(); } catch (IOException close) { e.addSuppressed(close); }
            throw e;
        }
    }
    @Override public Sample read() throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        row++;
        try { return TripLog.decode(line); }
        catch (IOException e) { throw new IOException("Invalid trip row " + row, e); }
    }
    @Override public String description() { return "PLAYBACK · " + file.getFileName(); }
    @Override public void close() throws IOException { reader.close(); }
}
