package net.minecraftforge.event.terraingen;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.event.Event;

public class WorldTypeEvent extends Event
{
    public final WorldType worldType;

    public WorldTypeEvent(final WorldType worldType)
    {
        this.worldType = worldType;
    }

    public static class BiomeSize extends WorldTypeEvent
    {
        public final byte originalSize;
        public byte newSize;
        
        public BiomeSize(final WorldType worldType, final byte original)
        {
            super(worldType);
            originalSize = original;
            newSize = original;
        }
    }

    public static class InitBiomeGens extends WorldTypeEvent
    {
        public final long seed;
        public final GenLayer[] originalBiomeGens;
        public GenLayer[] newBiomeGens;
        
        public InitBiomeGens(final WorldType worldType, final long seed, final GenLayer[] original)
        {
            super(worldType);
            this.seed = seed;
            originalBiomeGens = original;
            newBiomeGens = original.clone();
        }
    }
}
