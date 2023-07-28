package net.minecraftforge.event.terraingen;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.Event;

import java.util.Random;

public class OreGenEvent extends Event
{
    public final World world;
    public final Random rand;
    public final int worldX;
    public final int worldZ;
    
    public OreGenEvent(final World world, final Random rand, final int worldX, final int worldZ)
    {
        this.world = world;
        this.rand = rand;
        this.worldX = worldX;
        this.worldZ = worldZ;
    }
    
    public static class Pre extends OreGenEvent
    {
        public Pre(final World world, final Random rand, final int worldX, final int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
    
    public static class Post extends OreGenEvent
    {
        public Post(final World world, final Random rand, final int worldX, final int worldZ)
        {
            super(world, rand, worldX, worldZ);
        }
    }
    
    /**
     * This event is fired when an ore is generated in a chunk.
     * 
     * You can set the result to DENY to prevent the default ore generation.
     */
    @HasResult
    public static class GenerateMinable extends OreGenEvent
    {
        public static enum EventType { COAL, DIAMOND, DIRT, GOLD, GRAVEL, IRON, LAPIS, REDSTONE, CUSTOM }
        
        public final EventType type;
        public final WorldGenerator generator;
        
        public GenerateMinable(final World world, final Random rand, final WorldGenerator generator, final int worldX, final int worldZ, final EventType type)
        {
            super(world, rand, worldX, worldZ);
            this.generator = generator;
            this.type = type;
        }
    }
}
