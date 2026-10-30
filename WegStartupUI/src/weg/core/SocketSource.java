package weg.core;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/** One bounded request per connection, compatible with the original bridge. */
public final class SocketSource implements SampleSource {
    private final String host;
    private final int port;
    private volatile Socket active;
    private volatile boolean closed;
    public SocketSource(String host, int port) {
        if (port < 1 || port > 65535) throw new IllegalArgumentException("Invalid port");
        this.host = host; this.port = port;
    }
    @Override public Sample read() throws IOException {
        return SensorProtocol.parse(readFrame(), Instant.now());
    }
    /** Raw bounded frame for the legacy DasConnection facade. */
    public String readFrame() throws IOException {
        if (closed) throw new IOException("Source closed");
        try (Socket socket = new Socket()) {
            active = socket;
            if (closed) throw new IOException("Source closed");
            socket.connect(new InetSocketAddress(host, port), 1500);
            socket.setSoTimeout(4000);
            socket.getOutputStream().write("600\n".getBytes(StandardCharsets.US_ASCII));
            InputStream in = socket.getInputStream();
            ByteArrayOutputStream frame = new ByteArrayOutputStream();
            long deadline = System.nanoTime() + 4_000_000_000L;
            while (System.nanoTime() < deadline) {
                socket.setSoTimeout((int)Math.max(1, (deadline - System.nanoTime()) / 1_000_000));
                int b = in.read();
                if (b < 0 || b == 0 || b == '\n') {
                    if (frame.size() > 0) break;
                    if (b < 0) throw new EOFException("Bridge closed without a sample");
                    continue;
                }
                if (b != '\r') frame.write(b);
                if (frame.size() > 512) throw new IOException("Sensor frame exceeds 512 bytes");
            }
            if (System.nanoTime() >= deadline) throw new SocketTimeoutException("Frame deadline exceeded");
            String response = frame.toString(StandardCharsets.US_ASCII);
            if (response.startsWith("ERR ")) throw new IOException(response.substring(4));
            return response;
        } finally { active = null; }
    }
    @Override public String description() { return "LIVE · " + host + ":" + port; }
    @Override public void close() throws IOException {
        closed = true;
        Socket socket = active;
        if (socket != null) socket.close();
    }
}
