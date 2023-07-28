package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenSand extends WorldGenerator
{
    /** Stores ID for WorldGenSand */
    private int sandID;

    /** The maximum radius used when generating a patch of blocks. */
    private int radius;

    public WorldGenSand(final int par1, final int par2)
    {
        this.sandID = par2;
        this.radius = par1;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        if (par1World.getBlockMaterial(par3, par4, par5) != Material.water)
        {
            return false;
        }
        else
        {
            final int l = par2Random.nextInt(this.radius - 2) + 2;
            final byte b0 = 2;

            for (int i1 = par3 - l; i1 <= par3 + l; ++i1)
            {
                for (int j1 = par5 - l; j1 <= par5 + l; ++j1)
                {
                    final int k1 = i1 - par3;
                    final int l1 = j1 - par5;

                    if (k1 * k1 + l1 * l1 <= l * l)
                    {
                        for (int i2 = par4 - b0; i2 <= par4 + b0; ++i2)
                        {
                            final int j2 = par1World.getBlockId(i1, i2, j1);

                            if (j2 == Block.dirt.blockID || j2 == Block.grass.blockID)
                            {
                                par1World.setBlock(i1, i2, j1, this.sandID, 0, 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
