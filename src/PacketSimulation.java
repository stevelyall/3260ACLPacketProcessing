import com.google.common.net.InetAddresses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PacketSimulation {

    private ArrayList<ACLRule> acl;

    private List<Packet> packets;
    private List<Packet> discarded;
    private List<Packet> forwarded;

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Error: Must pass paths to ACL and packet files");
            System.err.println("Usage: PacketSimulation <ACL file path> <packet file path>");
            return;
        }

        System.out.println("Simulating ACL at E0 out on 172.16.3.0...");
        File aclFile = new File(args[0]);
        File packetsFile = new File(args[1]);

        PacketSimulation simulation = new PacketSimulation(aclFile, packetsFile);
        simulation.start();
    }


    public PacketSimulation(File acl, File packets) {
        if (acl == null) {
            System.err.println("Error: can't open ACL file");
            return;
        }
        initializeACL(acl);
        initializePackets(packets);

    }

    public void initializeACL(File aclFile) {
        acl = new ArrayList<ACLRule>();

        String[] split;
        InetAddress source, sMask;
        try {
            BufferedReader br = new BufferedReader(new FileReader(aclFile));
            String line;
            while ((line = br.readLine()) != null) {
                split = line.split(" ");
                if (split[0].equals("access-list")) {
                    source = InetAddresses.forString(split[3]);
                    sMask = InetAddresses.forString(split[4]);
                    acl.add(new ACLRule(split[2], source, sMask));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("ACL with " + acl.size() + " rules loaded");

    }

    public void start() {
        for (Packet p : packets) {
            if (!shouldForwardPacket(p)) {
                System.out.println("DISCARDING packet: " + p.toString());
                discarded.add(p);
            } else {
                System.out.println("FORWARDING packet: " + p.toString());
                forwarded.add(p);
            }
        }

        System.out.println("--------------");
        System.out.println("Number of packets: " + packets.size());
        System.out.println("Packets forwarded: " + forwarded.size());
        System.out.println("Packets discarded: " + discarded.size());
        System.out.println("--------------");
    }

    private boolean shouldForwardPacket(Packet p) {
        for (ACLRule r : acl) {
            if (!r.packetPermitted(p)) {
                return false;
            }
        }
        return true;
    }

    public void initializePackets(File file) {
        packets = new ArrayList<Packet>();

        String[] split;
        InetAddress source, dest;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                split = line.split(" ");
                source = InetAddresses.forString(split[0]);
                dest = InetAddresses.forString(split[1]);
                packets.add(new Packet(source, dest));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(packets.size() + " packets loaded:");
        printPacketList(packets);

        discarded = new ArrayList<Packet>();
        forwarded = new ArrayList<Packet>();
    }

    private void printPacketList(List<Packet> list) {
        System.out.println("--------------");
        for (Packet p : list) {
            System.out.println(p);
        }
        System.out.println("--------------");
    }

}
