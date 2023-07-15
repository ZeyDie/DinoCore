package net.minecraft.server.dedicated;

import cpw.mods.fml.common.FMLLog;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerListenThread;

import java.io.IOException;
import java.net.InetAddress;

public class DedicatedServerListenThread extends NetworkListenThread {
    /**
     * Instance of ServerListenThread.
     */
    private final ServerListenThread theServerListenThread;

    public DedicatedServerListenThread(MinecraftServer par1MinecraftServer, InetAddress par2InetAddress, int par3) throws IOException {
        super(par1MinecraftServer);

        //TODO ZeyCodeClear
        //this.theServerListenThread = new ServerListenThread(this, par2InetAddress, par3);
        //TODO ZeyCodeStart
        this.theServerListenThread = new ServerListenThread(this, par3, par2InetAddress);
        //TODO ZeyCodeEnd

        //this.theServerListenThread.start();
    }

    @Override
    public void stopListening() {
        super.stopListening();
        this.theServerListenThread.func_71768_b();
        this.theServerListenThread.interrupt();
    }

    /**
     * processes packets and pending connections
     */
    @Override
    public void networkTick() {
        // Cauldron start - mobius hooks
        ProfilerSection.NETWORK_TICK.start();
        this.theServerListenThread.processPendingConnections();
        super.networkTick();
        ProfilerSection.NETWORK_TICK.stop();
        // Cauldron end
    }

    public DedicatedServer getDedicatedServer() {
        return (DedicatedServer) super.getServer();
    }

    public void func_71761_a(InetAddress par1InetAddress) {
        this.theServerListenThread.func_71769_a(par1InetAddress);
    }

    @Override
    public MinecraftServer getServer() {
        return this.getDedicatedServer();
    }
}
