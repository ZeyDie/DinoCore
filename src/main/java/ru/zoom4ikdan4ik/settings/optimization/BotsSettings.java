package ru.zoom4ikdan4ik.settings.optimization;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public final class BotsSettings extends AbstractSettings {
    private BotsGson botsGson = new BotsGson();

    @Override
    public final String getConfigName() {
        return "bots";
    }

    @Override
    public final void setSettings(final IGson object) {
        this.botsGson = (BotsGson) object;
    }

    @Override
    public final BotsGson getSettings() {
        return this.botsGson;
    }

    public static BotsGson getInstance() {
        return MinecraftServer.getServer().botsSettings.getSettings();
    }

    public static final class BotsGson implements IGson {
        public boolean enableAntiBots = true;
        public long banDelayMinutes = 30;

        public Map<String, Long> blackListIPs = new HashMap<>();

        public final boolean contains(final Socket socket) {
            return this.contains(socket.getInetAddress());
        }

        public final boolean contains(final InetAddress inetAddress) {
            return this.contains(this.reformatInetAddress(inetAddress));
        }

        public final boolean contains(final String string) {
            return this.blackListIPs.containsKey(string);
        }

        public final void add(final Socket socket) {
            if (socket != null)
                this.add(socket.getInetAddress());
        }

        public final void add(final InetAddress inetAddress) {
            this.add(this.reformatInetAddress(inetAddress));
        }

        public final void add(final String string) {
            if (this.enableAntiBots) {
                FMLLog.info("IP %s was blocked! Next connection will ignore...", string);

                this.blackListIPs.put(string, System.currentTimeMillis() + this.banDelayMinutes * 60 * 1000);
            }
        }

        public final void remove(final Socket socket) {
            if (socket != null)
                this.remove(socket.getInetAddress());
        }

        public final void remove(final InetAddress inetAddress) {
            this.remove(this.reformatInetAddress(inetAddress));
        }

        public final void remove(final String string) {
            this.blackListIPs.remove(string);
        }

        private String reformatInetAddress(final InetAddress inetAddress) {
            return inetAddress.getHostAddress().replace("/", "").split(":")[0];
        }

        public final BotsGson update() {
            for (final String address : this.blackListIPs.keySet()) {
                final long time = this.blackListIPs.get(address);

                if (time < System.currentTimeMillis())
                    this.remove(address);
            }

            return this;
        }
    }
}
