import com.google.common.net.InetAddresses;

import java.net.InetAddress;

public class Packet {
    private InetAddress source;
    private InetAddress dest;

    public Packet(InetAddress soruce, InetAddress dest) {
        this.source = soruce;
        this.dest = dest;
    }

    public InetAddress getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Source: " + source.toString() + " Destination: " + dest.toString();
    }
}
