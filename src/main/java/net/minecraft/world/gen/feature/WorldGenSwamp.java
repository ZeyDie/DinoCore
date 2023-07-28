package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenSwamp extends WorldGenerator
{
    public boolean generate(final World par1World, final Random par2Random, final int par3, int par4, final int par5)
    {
        int par41 = par4;
        final int l;

        for (l = par2Random.nextInt(4) + 5; par1World.getBlockMaterial(par3, par41 - 1, par5) == Material.water; --par41)
        {
            ;
        }

        boolean flag = true;

        if (par41 >= 1 && par41 + l + 1 <= 128)
        {
            int i1;
            int j1;
            int k1;
            int l1;

            for (i1 = par41; i1 <= par41 + 1 + l; ++i1)
            {
                byte b0 = 1;

                if (i1 == par41)
                {
                    b0 = 0;
                }

                if (i1 >= par41 + 1 + l - 2)
                {
                    b0 = 3;
                }

                for (j1 = par3 - b0; j1 <= par3 + b0 && flag; ++j1)
                {
                    for (k1 = par5 - b0; k1 <= par5 + b0 && flag; ++k1)
                    {
                        if (i1 >= 0 && i1 < 128)
                        {
                            l1 = par1World.getBlockId(j1, i1, k1);

                            if (l1 != 0 && (Block.blocksList[l1] != null && !Block.blocksList[l1].isLeaves(par1World, j1, i1, k1)))
                            {
                                if (l1 != Block.waterStill.blockID && l1 != Block.waterMoving.blockID)
                                {
                                    flag = false;
                                }
                                else if (i1 > par41)
                                {
                                    flag = false;
                                }
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                i1 = par1World.getBlockId(par3, par41 - 1, par5);

                if ((i1 == Block.grass.blockID || i1 == Block.dirt.blockID) && par41 < 128 - l - 1)
                {
                    this.setBlock(par1World, par3, par41 - 1, par5, Block.dirt.blockID);
                    int i2;
                    int j2;

                    for (j2 = par41 - 3 + l; j2 <= par41 + l; ++j2)
                    {
                        j1 = j2 - (par41 + l);
                        k1 = 2 - j1 / 2;

                        for (l1 = par3 - k1; l1 <= par3 + k1; ++l1)
                        {
                            i2 = l1 - par3;

                            for (int k2 = par5 - k1; k2 <= par5 + k1; ++k2)
                            {
                                final int l2 = k2 - par5;

                                final Block block = Block.blocksList[par1World.getBlockId(l1, j2, k2)];

                                if ((Math.abs(i2) != k1 || Math.abs(l2) != k1 || par2Random.nextInt(2) != 0 && j1 != 0) && 
                                    (block == null || block.canBeReplacedByLeaves(par1World, l1, j2, k2)))
                                {
                                    this.setBlock(par1World, l1, j2, k2, Block.leaves.blockID);
                                }
                            }
                        }
                    }

                    for (j2 = 0; j2 < l; ++j2)
                    {
                        j1 = par1World.getBlockId(par3, par41 + j2, par5);

                        final Block block = Block.blocksList[j1];

                        if (j1 == 0 || (block != null && block.isLeaves(par1World, par3, par41 + j2, par5)) || j1 == Block.waterMoving.blockID || j1 == Block.waterStill.blockID)
                        {
                            this.setBlock(par1World, par3, par41 + j2, par5, Block.wood.blockID);
                        }
                    }

                    for (j2 = par41 - 3 + l; j2 <= par41 + l; ++j2)
                    {
                        j1 = j2 - (par41 + l);
                        k1 = 2 - j1 / 2;

                        for (l1 = par3 - k1; l1 <= par3 + k1; ++l1)
                        {
                            for (i2 = par5 - k1; i2 <= par5 + k1; ++i2)
                            {
                                final Block block = Block.blocksList[par1World.getBlockId(l1, j2, i2)];
                                if (block != null && block.isLeaves(par1World, l1, j2, i2))
                                {
                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(l1 - 1, j2, i2) == 0)
                                    {
                                        this.generateVines(par1World, l1 - 1, j2, i2, 8);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(l1 + 1, j2, i2) == 0)
                                    {
                                        this.generateVines(par1World, l1 + 1, j2, i2, 2);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(l1, j2, i2 - 1) == 0)
                                    {
                                        this.generateVines(par1World, l1, j2, i2 - 1, 1);
                                    }

                                    if (par2Random.nextInt(4) == 0 && par1World.getBlockId(l1, j2, i2 + 1) == 0)
                                    {
                                        this.generateVines(par1World, l1, j2, i2 + 1, 4);
                                    }
                                }
                            }
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Generates vines at the given position until it hits a block.
     */
    private void generateVines(final World par1World, final int par2, int par3, final int par4, final int par5)
    {
        int par31 = par3;
        this.setBlockAndMetadata(par1World, par2, par31, par4, Block.vine.blockID, par5);
        int i1 = 4;

        while (true)
        {
            --par31;

            if (par1World.getBlockId(par2, par31, par4) != 0 || i1 <= 0)
            {
                return;
            }

            this.setBlockAndMetadata(par1World, par2, par31, par4, Block.vine.blockID, par5);
            --i1;
        }
    }
}
