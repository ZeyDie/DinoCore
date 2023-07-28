package net.minecraft.server;

public class ThreadMinecraftServer extends Thread
{
    /** Instance of MinecraftServer. */
    final MinecraftServer theServer;

    public ThreadMinecraftServer(final MinecraftServer par1MinecraftServer, final String par2Str)
    {
        super(par2Str);
        this.theServer = par1MinecraftServer;
    }

    public void run()
    {
        this.theServer.run();
    }
}
