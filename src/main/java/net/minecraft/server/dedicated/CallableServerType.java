package net.minecraft.server.dedicated;

import java.util.concurrent.Callable;

class CallableServerType implements Callable
{
    final DedicatedServer theDedicatedServer;

    CallableServerType(final DedicatedServer par1DedicatedServer)
    {
        this.theDedicatedServer = par1DedicatedServer;
    }

    public String callServerType()
    {
        return "Dedicated Server (map_server.txt)";
    }

    public Object call()
    {
        return this.callServerType();
    }
}
