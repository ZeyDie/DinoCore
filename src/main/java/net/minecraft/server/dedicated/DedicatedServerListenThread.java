package net.minecraft.server.dedicated;

import com.zeydie.netty.server.NettyServerListenThread;
import com.zeydie.settings.optimization.CoreSettings;
import com.zeydie.settings.optimization.NettySettings;
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

    //TODO ZeyCodeStart
    private final NettyServerListenThread nettyServerListenThread;
    //TODO ZeyCodeEnd

    public DedicatedServerListenThread(final MinecraftServer par1MinecraftServer, final InetAddress par2InetAddress, final int par3) throws IOException {
        super(par1MinecraftServer);

        this.theServerListenThread = new ServerListenThread(this, par2InetAddress, par3);

        //TODO ZeyCodeStart
        this.nettyServerListenThread = new NettyServerListenThread(this, par3);

        if (NettySettings.getInstance().getSettings().isEnable())
            this.nettyServerListenThread.start();
        else
            //TODO ZeyCodeEnd

            this.theServerListenThread.start();
    }

    @Override
    public void stopListening() {
        super.stopListening();

        //TODO ZeyCodeStart
        if (NettySettings.getInstance().getSettings().isEnable()) {
            this.nettyServerListenThread.interrupt();
            return;
        }
        //TODO ZeyCodeEnd

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

        //TODO ZeyCodeStart
        if (NettySettings.getInstance().getSettings().isEnable())
            this.nettyServerListenThread.processPendingConnections();
        else
            //TODO ZeyCodeEnd

            this.theServerListenThread.processPendingConnections();
        super.networkTick();
        ProfilerSection.NETWORK_TICK.stop();
        // Cauldron end
    }

    public DedicatedServer getDedicatedServer() {
        return (DedicatedServer) super.getServer();
    }

    public void func_71761_a(final InetAddress par1InetAddress) {
        //TODO ZeyCodeStart
        if (NettySettings.getInstance().getSettings().isEnable())
            this.nettyServerListenThread.func_71769_a(par1InetAddress);
        else
            //TODO ZeyCodeEnd

            this.theServerListenThread.func_71769_a(par1InetAddress);
    }

    @Override
    public MinecraftServer getServer() {
        return this.getDedicatedServer();
    }
}
