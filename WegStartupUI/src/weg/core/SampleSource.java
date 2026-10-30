package weg.core;

import java.io.IOException;

public interface SampleSource extends AutoCloseable {
    /** Null indicates the end of a replay. Calls must finish within a bounded time. */
    Sample read() throws IOException;
    String description();
    @Override default void close() throws IOException {}
}
