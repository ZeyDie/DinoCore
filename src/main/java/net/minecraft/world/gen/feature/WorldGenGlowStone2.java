package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenGlowStone2 extends WorldGenerator
{
    public boolean generate(final World par1World, final Random par2Random, final int par3, final int par4, final int par5)
    {
        if (!par1World.isAirBlock(par3, par4, par5))
        {
            return false;
        }
        else if (par1World.getBlockId(par3, par4 + 1, par5) != Block.netherrack.blockID)
        {
            return false;
        }
        else
        {
            par1World.setBlock(par3, par4, par5, Block.glowStone.blockID, 0, 2);

            for (int l = 0; l < 1500; ++l)
            {
                final int i1 = par3 + par2Random.nextInt(8) - par2Random.nextInt(8);
                final int j1 = par4 - par2Random.nextInt(12);
                final int k1 = par5 + par2Random.nextInt(8) - par2Random.nextInt(8);

                if (par1World.getBlockId(i1, j1, k1) == 0)
                {
                    int l1 = 0;

                    for (int i2 = 0; i2 < 6; ++i2)
                    {
                        int j2 = 0;

                        if (i2 == 0)
                        {
                            j2 = par1World.getBlockId(i1 - 1, j1, k1);
                        }

                        if (i2 == 1)
                        {
                            j2 = par1World.getBlockId(i1 + 1, j1, k1);
                        }

                        if (i2 == 2)
                        {
                            j2 = par1World.getBlockId(i1, j1 - 1, k1);
                        }

                        if (i2 == 3)
                        {
                            j2 = par1World.getBlockId(i1, j1 + 1, k1);
                        }

                        if (i2 == 4)
                        {
                            j2 = par1World.getBlockId(i1, j1, k1 - 1);
                        }

                        if (i2 == 5)
                        {
                            j2 = par1World.getBlockId(i1, j1, k1 + 1);
                        }

                        if (j2 == Block.glowStone.blockID)
                        {
                            ++l1;
                        }
                    }

                    if (l1 == 1)
                    {
                        par1World.setBlock(i1, j1, k1, Block.glowStone.blockID, 0, 2);
                    }
                }
            }

            return true;
        }
    }
}
