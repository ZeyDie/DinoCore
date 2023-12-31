package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class CallableLevelSeed implements Callable
{
    final WorldInfo worldInfoInstance;

    CallableLevelSeed(final WorldInfo par1WorldInfo)
    {
        this.worldInfoInstance = par1WorldInfo;
    }

    public String callLevelSeed()
    {
        return String.valueOf(this.worldInfoInstance.getSeed());
    }

    public Object call()
    {
        return this.callLevelSeed();
    }
}
