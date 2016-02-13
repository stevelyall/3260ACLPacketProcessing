import com.google.common.net.InetAddresses;

import java.net.InetAddress;

/**
 * Created by stevenlyall on 2016-02-12.
 */
public class ACLRule {
    private String type;
    private InetAddress sourceAddr;
    private InetAddress sourceMask;

    public ACLRule(String type, InetAddress sourceAddr, InetAddress sourceMask) {
        this.type = type;
        this.sourceAddr = sourceAddr;
        this.sourceMask = sourceMask;
    }

    @Override
    public String toString() {
        return type + " " + sourceAddr + " " + sourceMask;
    }

    public boolean packetPermitted(Packet p) {
        int packetSource = InetAddresses.coerceToInteger(p.getSource());

        int ruleAddr = InetAddresses.coerceToInteger(this.sourceAddr);
        int ruleAddrMask = InetAddresses.coerceToInteger(this.sourceMask);

        int blockStart = ruleAddr & (~ruleAddrMask);
        int blockEnd = ruleAddr | ruleAddrMask;

        boolean addrWithinBlock = (blockStart <= packetSource && packetSource <= blockEnd);
        if (type.equals("deny") && addrWithinBlock) {
            return false;
        }
        else if (type.equals("allow") && addrWithinBlock) {
            return true;
        }
        return true;
    }
}
