package net.minecraft.server.dedicated;

import java.util.concurrent.Callable;

class CallableType implements Callable
{
    /** Reference to the DecitatedServer object. */
    final DedicatedServer theDecitatedServer;

    CallableType(final DedicatedServer par1DedicatedServer)
    {
        this.theDecitatedServer = par1DedicatedServer;
    }

    public String getType()
    {
        final String s = this.theDecitatedServer.getServerModName();
        return !s.equals("vanilla") ? "Definitely; Server brand changed to \'" + s + "\'" : "Unknown (can\'t tell)";
    }

    public Object call()
    {
        return this.getType();
    }
}
