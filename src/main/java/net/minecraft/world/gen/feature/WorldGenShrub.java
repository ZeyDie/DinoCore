package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenShrub extends WorldGenerator
{
    private int field_76527_a;
    private int field_76526_b;

    public WorldGenShrub(final int par1, final int par2)
    {
        this.field_76526_b = par1;
        this.field_76527_a = par2;
    }

    public boolean generate(final World par1World, final Random par2Random, final int par3, int par4, final int par5)
    {
        int par41 = par4;
        int l;

        Block block = null;
        do 
        {
            block = Block.blocksList[par1World.getBlockId(par3, par41, par5)];
            if (block != null && !block.isAirBlock(par1World, par3, par41, par5) && !block.isLeaves(par1World, par3, par41, par5))
            {
                break;
            }
            par41--;
        } while (par41 > 0);

        final int i1 = par1World.getBlockId(par3, par41, par5);

        if (i1 == Block.dirt.blockID || i1 == Block.grass.blockID)
        {
            ++par41;
            this.setBlockAndMetadata(par1World, par3, par41, par5, Block.wood.blockID, this.field_76526_b);

            for (int j1 = par41; j1 <= par41 + 2; ++j1)
            {
                final int k1 = j1 - par41;
                final int l1 = 2 - k1;

                for (int i2 = par3 - l1; i2 <= par3 + l1; ++i2)
                {
                    final int j2 = i2 - par3;

                    for (int k2 = par5 - l1; k2 <= par5 + l1; ++k2)
                    {
                        final int l2 = k2 - par5;

                        block = Block.blocksList[par1World.getBlockId(i2, j1, k2)];

                        if ((Math.abs(j2) != l1 || Math.abs(l2) != l1 || par2Random.nextInt(2) != 0) && 
                            (block == null || block.canBeReplacedByLeaves(par1World, i2, j1, k2)))
                        {
                            this.setBlockAndMetadata(par1World, i2, j1, k2, Block.leaves.blockID, this.field_76527_a);
                        }
                    }
                }
            }

            // CraftBukkit start - Return false if gen was unsuccessful
        }
        else
        {
            return false;
        }

        // CraftBukkit end
        return true;
    }
}
