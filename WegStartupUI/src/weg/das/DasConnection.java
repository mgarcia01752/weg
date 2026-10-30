package weg.das;

import java.io.IOException;
import java.net.InetAddress;
import weg.core.SocketSource;

/** Legacy facade. Failures return null; callers must display an unavailable state. */
public class DasConnection {
    public static final int IPC_PORT = 5000;
    private final InetAddress address;
    private final int port;
    public DasConnection(InetAddress address, int port) { this.address = address; this.port = port; }
    public DasConnection() { this(InetAddress.getLoopbackAddress(), IPC_PORT); }
    public InetAddress getDasInetAddress() { return address; }
    public String get(int command) {
        if (command != DasCommands.FULL_SENSOR_DUMP)
            throw new IllegalArgumentException("The PIC supports only the full sensor dump (600)");
        try (SocketSource source = new SocketSource(address.getHostAddress(), port)) {
            return source.readFrame();
        } catch (IOException e) { return null; }
    }
}
