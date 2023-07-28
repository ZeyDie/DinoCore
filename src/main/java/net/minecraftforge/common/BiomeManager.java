package net.minecraftforge.common;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;

import java.util.ArrayList;

public class BiomeManager
{
    public static void addVillageBiome(final BiomeGenBase biome, final boolean canSpawn)
    {
        if (!MapGenVillage.villageSpawnBiomes.contains(biome))
        {
            final ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(MapGenVillage.villageSpawnBiomes);
            biomes.add(biome);
            MapGenVillage.villageSpawnBiomes = biomes;
        }
    }

    public static void removeVillageBiome(final BiomeGenBase biome)
    {
        if (MapGenVillage.villageSpawnBiomes.contains(biome))
        {
            final ArrayList<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>(MapGenVillage.villageSpawnBiomes);
            biomes.remove(biome);
            MapGenVillage.villageSpawnBiomes = biomes;
        }
    }

    public static void addStrongholdBiome(final BiomeGenBase biome)
    {
        if (!MapGenStronghold.allowedBiomes.contains(biome))
        {
            MapGenStronghold.allowedBiomes.add(biome);
        }
    }

    public static void removeStrongholdBiome(final BiomeGenBase biome)
    {
        if (MapGenStronghold.allowedBiomes.contains(biome))
        {
            MapGenStronghold.allowedBiomes.remove(biome);
        }
    }

    public static void addSpawnBiome(final BiomeGenBase biome)
    {
        if (!WorldChunkManager.allowedBiomes.contains(biome))
        {
            WorldChunkManager.allowedBiomes.add(biome);
        }
    }

    public static void removeSpawnBiome(final BiomeGenBase biome)
    {
        if (WorldChunkManager.allowedBiomes.contains(biome))
        {
            WorldChunkManager.allowedBiomes.remove(biome);
        }
    }
}