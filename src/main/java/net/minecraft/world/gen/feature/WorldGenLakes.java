package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class WorldGenLakes extends WorldGenerator
{
    private int blockIndex;

    public WorldGenLakes(final int par1)
    {
        this.blockIndex = par1;
    }

    public boolean generate(final World par1World, final Random par2Random, int par3, int par4, int par5)
    {
        int par31 = par3;
        int par51 = par5;
        int par41 = par4;
        par31 -= 8;

        for (par51 -= 8; par41 > 5 && par1World.isAirBlock(par31, par41, par51); --par41)
        {
            ;
        }

        if (par41 <= 4)
        {
            return false;
        }
        else
        {
            par41 -= 4;
            final boolean[] aboolean = new boolean[2048];
            final int l = par2Random.nextInt(4) + 4;
            int i1;

            for (i1 = 0; i1 < l; ++i1)
            {
                final double d0 = par2Random.nextDouble() * 6.0D + 3.0D;
                final double d1 = par2Random.nextDouble() * 4.0D + 2.0D;
                final double d2 = par2Random.nextDouble() * 6.0D + 3.0D;
                final double d3 = par2Random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                final double d4 = par2Random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                final double d5 = par2Random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int j1 = 1; j1 < 15; ++j1)
                {
                    for (int k1 = 1; k1 < 15; ++k1)
                    {
                        for (int l1 = 1; l1 < 7; ++l1)
                        {
                            final double d6 = ((double)j1 - d3) / (d0 / 2.0D);
                            final double d7 = ((double)l1 - d4) / (d1 / 2.0D);
                            final double d8 = ((double)k1 - d5) / (d2 / 2.0D);
                            final double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                            if (d9 < 1.0D)
                            {
                                aboolean[(j1 * 16 + k1) * 8 + l1] = true;
                            }
                        }
                    }
                }
            }

            int i2;
            int j2;
            boolean flag;

            for (i1 = 0; i1 < 16; ++i1)
            {
                for (j2 = 0; j2 < 16; ++j2)
                {
                    for (i2 = 0; i2 < 8; ++i2)
                    {
                        flag = !aboolean[(i1 * 16 + j2) * 8 + i2] && (i1 < 15 && aboolean[((i1 + 1) * 16 + j2) * 8 + i2] || i1 > 0 && aboolean[((i1 - 1) * 16 + j2) * 8 + i2] || j2 < 15 && aboolean[(i1 * 16 + j2 + 1) * 8 + i2] || j2 > 0 && aboolean[(i1 * 16 + (j2 - 1)) * 8 + i2] || i2 < 7 && aboolean[(i1 * 16 + j2) * 8 + i2 + 1] || i2 > 0 && aboolean[(i1 * 16 + j2) * 8 + (i2 - 1)]);

                        if (flag)
                        {
                            final Material material = par1World.getBlockMaterial(par31 + i1, par41 + i2, par51 + j2);

                            if (i2 >= 4 && material.isLiquid())
                            {
                                return false;
                            }

                            if (i2 < 4 && !material.isSolid() && par1World.getBlockId(par31 + i1, par41 + i2, par51 + j2) != this.blockIndex)
                            {
                                return false;
                            }
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1)
            {
                for (j2 = 0; j2 < 16; ++j2)
                {
                    for (i2 = 0; i2 < 8; ++i2)
                    {
                        if (aboolean[(i1 * 16 + j2) * 8 + i2])
                        {
                            par1World.setBlock(par31 + i1, par41 + i2, par51 + j2, i2 >= 4 ? 0 : this.blockIndex, 0, 2);
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1)
            {
                for (j2 = 0; j2 < 16; ++j2)
                {
                    for (i2 = 4; i2 < 8; ++i2)
                    {
                        if (aboolean[(i1 * 16 + j2) * 8 + i2] && par1World.getBlockId(par31 + i1, par41 + i2 - 1, par51 + j2) == Block.dirt.blockID && par1World.getSavedLightValue(EnumSkyBlock.Sky, par31 + i1, par41 + i2, par51 + j2) > 0)
                        {
                            final BiomeGenBase biomegenbase = par1World.getBiomeGenForCoords(par31 + i1, par51 + j2);

                            if (biomegenbase.topBlock == Block.mycelium.blockID)
                            {
                                par1World.setBlock(par31 + i1, par41 + i2 - 1, par51 + j2, Block.mycelium.blockID, 0, 2);
                            }
                            else
                            {
                                par1World.setBlock(par31 + i1, par41 + i2 - 1, par51 + j2, Block.grass.blockID, 0, 2);
                            }
                        }
                    }
                }
            }

            if (Block.blocksList[this.blockIndex].blockMaterial == Material.lava)
            {
                for (i1 = 0; i1 < 16; ++i1)
                {
                    for (j2 = 0; j2 < 16; ++j2)
                    {
                        for (i2 = 0; i2 < 8; ++i2)
                        {
                            flag = !aboolean[(i1 * 16 + j2) * 8 + i2] && (i1 < 15 && aboolean[((i1 + 1) * 16 + j2) * 8 + i2] || i1 > 0 && aboolean[((i1 - 1) * 16 + j2) * 8 + i2] || j2 < 15 && aboolean[(i1 * 16 + j2 + 1) * 8 + i2] || j2 > 0 && aboolean[(i1 * 16 + (j2 - 1)) * 8 + i2] || i2 < 7 && aboolean[(i1 * 16 + j2) * 8 + i2 + 1] || i2 > 0 && aboolean[(i1 * 16 + j2) * 8 + (i2 - 1)]);

                            if (flag && (i2 < 4 || par2Random.nextInt(2) != 0) && par1World.getBlockMaterial(par31 + i1, par41 + i2, par51 + j2).isSolid())
                            {
                                par1World.setBlock(par31 + i1, par41 + i2, par51 + j2, Block.stone.blockID, 0, 2);
                            }
                        }
                    }
                }
            }

            if (Block.blocksList[this.blockIndex].blockMaterial == Material.water)
            {
                for (i1 = 0; i1 < 16; ++i1)
                {
                    for (j2 = 0; j2 < 16; ++j2)
                    {
                        final byte b0 = 4;

                        if (par1World.isBlockFreezable(par31 + i1, par41 + b0, par51 + j2))
                        {
                            par1World.setBlock(par31 + i1, par41 + b0, par51 + j2, Block.ice.blockID, 0, 2);
                        }
                    }
                }
            }

            return true;
        }
    }
}
