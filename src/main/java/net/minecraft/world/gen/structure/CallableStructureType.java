package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;

class CallableStructureType implements Callable
{
    final MapGenStructure theMapStructureGenerator;

    CallableStructureType(final MapGenStructure par1MapGenStructure)
    {
        this.theMapStructureGenerator = par1MapGenStructure;
    }

    public String callStructureType()
    {
        return this.theMapStructureGenerator.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.callStructureType();
    }
}
