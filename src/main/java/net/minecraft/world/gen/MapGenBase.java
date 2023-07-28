package net.minecraft.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class MapGenBase
{
    /** The number of Chunks to gen-check in any given direction. */
    protected int range = 8;

    /** The RNG used by the MapGen classes. */
    protected Random rand = new Random();

    /** This world object. */
    protected World worldObj;

    public void generate(final IChunkProvider par1IChunkProvider, final World par2World, final int par3, final int par4, final byte[] par5ArrayOfByte)
    {
        final int k = this.range;
        this.worldObj = par2World;
        this.rand.setSeed(par2World.getSeed());
        final long l = this.rand.nextLong();
        final long i1 = this.rand.nextLong();

        for (int j1 = par3 - k; j1 <= par3 + k; ++j1)
        {
            for (int k1 = par4 - k; k1 <= par4 + k; ++k1)
            {
                final long l1 = (long)j1 * l;
                final long i2 = (long)k1 * i1;
                this.rand.setSeed(l1 ^ i2 ^ par2World.getSeed());
                this.recursiveGenerate(par2World, j1, k1, par3, par4, par5ArrayOfByte);
            }
        }
    }

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected void recursiveGenerate(final World par1World, final int par2, final int par3, final int par4, final int par5, final byte[] par6ArrayOfByte) {}
}
