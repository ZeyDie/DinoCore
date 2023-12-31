package org.bukkit.craftbukkit.v1_6_R3.util;


public class ServerShutdownThread extends Thread {
    private final net.minecraft.server.MinecraftServer server;

    public ServerShutdownThread(final net.minecraft.server.MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.stopServer();
        } catch (final net.minecraft.world.MinecraftException ex) {
            ex.printStackTrace();
        } finally {
            try {
                server.reader.getTerminal().restore();
            } catch (final Exception e) {
            }
        }
    }
}
