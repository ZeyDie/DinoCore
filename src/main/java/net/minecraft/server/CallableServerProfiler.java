package net.minecraft.server;

import java.util.concurrent.Callable;

public class CallableServerProfiler implements Callable
{
    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    public CallableServerProfiler(final MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
    }

    public String callServerProfiler()
    {
        final int i = this.mcServer.worldServers[0].getWorldVec3Pool().getPoolSize();
        final int j = 56 * i;
        final int k = j / 1024 / 1024;
        final int l = this.mcServer.worldServers[0].getWorldVec3Pool().func_82590_d();
        final int i1 = 56 * l;
        final int j1 = i1 / 1024 / 1024;
        return i + " (" + j + " bytes; " + k + " MB) allocated, " + l + " (" + i1 + " bytes; " + j1 + " MB) used";
    }

    public Object call()
    {
        return this.callServerProfiler();
    }
}
