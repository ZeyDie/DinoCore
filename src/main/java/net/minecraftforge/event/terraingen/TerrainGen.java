package net.minecraftforge.event.terraingen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;

import java.util.Random;

public abstract class TerrainGen
{
    public static NoiseGeneratorOctaves[] getModdedNoiseGenerators(final World world, final Random rand, final NoiseGeneratorOctaves[] original)
    {
        final InitNoiseGensEvent event = new InitNoiseGensEvent(world, rand, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.newNoiseGens;
    }

    public static MapGenBase getModdedMapGen(final MapGenBase original, final InitMapGenEvent.EventType type)
    {
        final InitMapGenEvent event = new InitMapGenEvent(type, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.newGen;
    }
    
    public static boolean populate(final IChunkProvider chunkProvider, final World world, final Random rand, final int chunkX, final int chunkZ, final boolean hasVillageGenerated, final Populate.EventType type)
    {
        final PopulateChunkEvent.Populate event = new PopulateChunkEvent.Populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
    
    public static boolean decorate(final World world, final Random rand, final int chunkX, final int chunkZ, final Decorate.EventType type)
    {
        final Decorate event = new Decorate(world, rand, chunkX, chunkZ, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
    
    public static boolean generateOre(final World world, final Random rand, final WorldGenerator generator, final int worldX, final int worldZ, final GenerateMinable.EventType type)
    {
        final GenerateMinable event = new GenerateMinable(world, rand, generator, worldX, worldZ, type);
        MinecraftForge.ORE_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
    
    public static boolean saplingGrowTree(final World world, final Random rand, final int x, final int y, final int z)
    {
        final SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, x, y, z);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
}
