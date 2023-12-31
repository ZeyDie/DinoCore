package net.minecraft.world.biome;

import net.minecraft.world.ChunkPosition;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WorldChunkManagerHell extends WorldChunkManager
{
    /** this is the sole biome to utilize for this world */
    private BiomeGenBase biomeToUse;
    private float hellTemperature;

    /** The rainfall in the world */
    private float rainfall;

    public WorldChunkManagerHell(final BiomeGenBase par1BiomeGenBase, final float par2, final float par3)
    {
        this.biomeToUse = par1BiomeGenBase;
        this.hellTemperature = par2;
        this.rainfall = par3;
    }

    /**
     * Returns the BiomeGenBase related to the x, z position on the world.
     */
    public BiomeGenBase getBiomeGenAt(final int par1, final int par2)
    {
        return this.biomeToUse;
    }

    /**
     * Returns an array of biomes for the location input.
     */
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, final int par2, final int par3, final int par4, final int par5)
    {
        BiomeGenBase[] par1ArrayOfBiomeGenBase1 = par1ArrayOfBiomeGenBase;
        if (par1ArrayOfBiomeGenBase1 == null || par1ArrayOfBiomeGenBase1.length < par4 * par5)
        {
            par1ArrayOfBiomeGenBase1 = new BiomeGenBase[par4 * par5];
        }

        Arrays.fill(par1ArrayOfBiomeGenBase1, 0, par4 * par5, this.biomeToUse);
        return par1ArrayOfBiomeGenBase1;
    }

    /**
     * Returns a list of temperatures to use for the specified blocks.  Args: listToReuse, x, y, width, length
     */
    public float[] getTemperatures(float[] par1ArrayOfFloat, final int par2, final int par3, final int par4, final int par5)
    {
        float[] par1ArrayOfFloat1 = par1ArrayOfFloat;
        if (par1ArrayOfFloat1 == null || par1ArrayOfFloat1.length < par4 * par5)
        {
            par1ArrayOfFloat1 = new float[par4 * par5];
        }

        Arrays.fill(par1ArrayOfFloat1, 0, par4 * par5, this.hellTemperature);
        return par1ArrayOfFloat1;
    }

    /**
     * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
     */
    public float[] getRainfall(float[] par1ArrayOfFloat, final int par2, final int par3, final int par4, final int par5)
    {
        float[] par1ArrayOfFloat1 = par1ArrayOfFloat;
        if (par1ArrayOfFloat1 == null || par1ArrayOfFloat1.length < par4 * par5)
        {
            par1ArrayOfFloat1 = new float[par4 * par5];
        }

        Arrays.fill(par1ArrayOfFloat1, 0, par4 * par5, this.rainfall);
        return par1ArrayOfFloat1;
    }

    /**
     * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager Args: oldBiomeList, x, z, width, depth
     */
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] par1ArrayOfBiomeGenBase, final int par2, final int par3, final int par4, final int par5)
    {
        BiomeGenBase[] par1ArrayOfBiomeGenBase1 = par1ArrayOfBiomeGenBase;
        if (par1ArrayOfBiomeGenBase1 == null || par1ArrayOfBiomeGenBase1.length < par4 * par5)
        {
            par1ArrayOfBiomeGenBase1 = new BiomeGenBase[par4 * par5];
        }

        Arrays.fill(par1ArrayOfBiomeGenBase1, 0, par4 * par5, this.biomeToUse);
        return par1ArrayOfBiomeGenBase1;
    }

    /**
     * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
     * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
     */
    public BiomeGenBase[] getBiomeGenAt(final BiomeGenBase[] par1ArrayOfBiomeGenBase, final int par2, final int par3, final int par4, final int par5, final boolean par6)
    {
        return this.loadBlockGeneratorData(par1ArrayOfBiomeGenBase, par2, par3, par4, par5);
    }

    /**
     * Finds a valid position within a range, that is in one of the listed biomes. Searches {par1,par2} +-par3 blocks.
     * Strongly favors positive y positions.
     */
    public ChunkPosition findBiomePosition(final int par1, final int par2, final int par3, final List par4List, final Random par5Random)
    {
        return par4List.contains(this.biomeToUse) ? new ChunkPosition(par1 - par3 + par5Random.nextInt(par3 * 2 + 1), 0, par2 - par3 + par5Random.nextInt(par3 * 2 + 1)) : null;
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(final int par1, final int par2, final int par3, final List par4List)
    {
        return par4List.contains(this.biomeToUse);
    }
}
