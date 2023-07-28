package net.minecraft.world.gen.feature;

import net.minecraft.world.World;

import java.util.Random;

public abstract class WorldGenerator
{
    /**
     * Sets wither or not the generator should notify blocks of blocks it changes. When the world is first generated,
     * this is false, when saplings grow, this is true.
     */
    private final boolean doBlockNotify;

    public WorldGenerator()
    {
        this.doBlockNotify = false;
    }

    public WorldGenerator(final boolean par1)
    {
        this.doBlockNotify = par1;
    }

    public abstract boolean generate(World world, Random random, int i, int j, int k);

    /**
     * Rescales the generator settings, only used in WorldGenBigTree
     */
    public void setScale(final double par1, final double par3, final double par5) {}

    /**
     * Sets the block without metadata in the world, notifying neighbors if enabled.
     */
    protected void setBlock(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        this.setBlockAndMetadata(par1World, par2, par3, par4, par5, 0);
    }

    /**
     * Sets the block in the world, notifying neighbors if enabled.
     */
    protected void setBlockAndMetadata(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        if (this.doBlockNotify)
        {
            par1World.setBlock(par2, par3, par4, par5, par6, 3);
        }
        else
        {
            par1World.setBlock(par2, par3, par4, par5, par6, 2);
        }
    }
}
