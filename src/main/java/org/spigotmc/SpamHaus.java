package org.spigotmc;

import net.minecraft.network.NetLoginHandler;

import java.net.InetAddress;

public class SpamHaus {

    private SpamHaus() {
    }

    public static boolean filterIp(final NetLoginHandler con) {
        if (SpigotConfig.preventProxies) {
            try {
                final InetAddress address = con.getSocket().getInetAddress();
                final String ip = address.getHostAddress();

                if (!address.isLoopbackAddress()) {
                    final String[] split = ip.split("\\.");
                    final StringBuilder lookup = new StringBuilder();
                    for (int i = split.length - 1; i >= 0; i--) {
                        lookup.append(split[i]);
                        lookup.append(".");
                    }
                    lookup.append("xbl.spamhaus.org.");
                    if (InetAddress.getByName(lookup.toString()) != null) {
                        con.raiseErrorAndDisconnect("Your IP address (" + ip + ") is flagged as unsafe by spamhaus.org/xbl");
                        return true;
                    }
                }
            } catch (final Exception ex) {
            }
        }
        return false;
    }
}
