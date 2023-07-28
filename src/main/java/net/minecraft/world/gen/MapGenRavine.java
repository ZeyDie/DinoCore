package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class MapGenRavine extends MapGenBase
{
    private float[] field_75046_d = new float[1024];

    protected void generateRavine(final long par1, final int par3, final int par4, final byte[] par5ArrayOfByte, double par6, double par8, double par10, final float par12, float par13, float par14, int par15, int par16, final double par17)
    {
        int par161 = par16;
        int par151 = par15;
        double par61 = par6;
        double par81 = par8;
        double par101 = par10;
        float par141 = par14;
        float par131 = par13;
        final Random random = new Random(par1);
        final double d4 = (double)(par3 * 16 + 8);
        final double d5 = (double)(par4 * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;

        if (par161 <= 0)
        {
            final int j1 = this.range * 16 - 16;
            par161 = j1 - random.nextInt(j1 / 4);
        }

        boolean flag = false;

        if (par151 == -1)
        {
            par151 = par161 / 2;
            flag = true;
        }

        float f5 = 1.0F;

        for (int k1 = 0; k1 < 128; ++k1)
        {
            if (k1 == 0 || random.nextInt(3) == 0)
            {
                f5 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
            }

            this.field_75046_d[k1] = f5 * f5;
        }

        for (; par151 < par161; ++par151)
        {
            double d6 = 1.5D + (double)(MathHelper.sin((float) par151 * (float)Math.PI / (float) par161) * par12 * 1.0F);
            double d7 = d6 * par17;
            d6 *= (double)random.nextFloat() * 0.25D + 0.75D;
            d7 *= (double)random.nextFloat() * 0.25D + 0.75D;
            final float f6 = MathHelper.cos(par141);
            final float f7 = MathHelper.sin(par141);
            par61 += (double)(MathHelper.cos(par131) * f6);
            par81 += (double)f7;
            par101 += (double)(MathHelper.sin(par131) * f6);
            par141 *= 0.7F;
            par141 += f4 * 0.05F;
            par131 += f3 * 0.05F;
            f4 *= 0.8F;
            f3 *= 0.5F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (flag || random.nextInt(4) != 0)
            {
                final double d8 = par61 - d4;
                final double d9 = par101 - d5;
                final double d10 = (double)(par161 - par151);
                final double d11 = (double)(par12 + 2.0F + 16.0F);

                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11)
                {
                    return;
                }

                if (par61 >= d4 - 16.0D - d6 * 2.0D && par101 >= d5 - 16.0D - d6 * 2.0D && par61 <= d4 + 16.0D + d6 * 2.0D && par101 <= d5 + 16.0D + d6 * 2.0D)
                {
                    int l1 = MathHelper.floor_double(par61 - d6) - par3 * 16 - 1;
                    int i2 = MathHelper.floor_double(par61 + d6) - par3 * 16 + 1;
                    int j2 = MathHelper.floor_double(par81 - d7) - 1;
                    int k2 = MathHelper.floor_double(par81 + d7) + 1;
                    int l2 = MathHelper.floor_double(par101 - d6) - par4 * 16 - 1;
                    int i3 = MathHelper.floor_double(par101 + d6) - par4 * 16 + 1;

                    if (l1 < 0)
                    {
                        l1 = 0;
                    }

                    if (i2 > 16)
                    {
                        i2 = 16;
                    }

                    if (j2 < 1)
                    {
                        j2 = 1;
                    }

                    if (k2 > 120)
                    {
                        k2 = 120;
                    }

                    if (l2 < 0)
                    {
                        l2 = 0;
                    }

                    if (i3 > 16)
                    {
                        i3 = 16;
                    }

                    boolean flag1 = false;
                    int j3;
                    int k3;

                    for (j3 = l1; !flag1 && j3 < i2; ++j3)
                    {
                        for (int l3 = l2; !flag1 && l3 < i3; ++l3)
                        {
                            for (int i4 = k2 + 1; !flag1 && i4 >= j2 - 1; --i4)
                            {
                                k3 = (j3 * 16 + l3) * 128 + i4;

                                if (i4 >= 0 && i4 < 128)
                                {
                                    if (isOceanBlock(par5ArrayOfByte, k3, j3, i4, l3, par3, par4))
                                    {
                                        flag1 = true;
                                    }

                                    if (i4 != j2 - 1 && j3 != l1 && j3 != i2 - 1 && l3 != l2 && l3 != i3 - 1)
                                    {
                                        i4 = j2;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag1)
                    {
                        for (j3 = l1; j3 < i2; ++j3)
                        {
                            final double d12 = ((double)(j3 + par3 * 16) + 0.5D - par61) / d6;

                            for (k3 = l2; k3 < i3; ++k3)
                            {
                                final double d13 = ((double)(k3 + par4 * 16) + 0.5D - par101) / d6;
                                int j4 = (j3 * 16 + k3) * 128 + k2;
                                boolean flag2 = false;

                                if (d12 * d12 + d13 * d13 < 1.0D)
                                {
                                    for (int k4 = k2 - 1; k4 >= j2; --k4)
                                    {
                                        final double d14 = ((double)k4 + 0.5D - par81) / d7;

                                        if ((d12 * d12 + d13 * d13) * (double)this.field_75046_d[k4] + d14 * d14 / 6.0D < 1.0D)
                                        {
                                            if (isTopBlock(par5ArrayOfByte, j4, j3, k4, k3, par3, par4))
                                            {
                                                flag2 = true;
                                            }

                                            digBlock(par5ArrayOfByte, j4, j3, k4, k3, par3, par4, flag2);
                                        }

                                        --j4;
                                    }
                                }
                            }
                        }

                        if (flag)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected void recursiveGenerate(final World par1World, final int par2, final int par3, final int par4, final int par5, final byte[] par6ArrayOfByte)
    {
        if (this.rand.nextInt(50) == 0)
        {
            final double d0 = (double)(par2 * 16 + this.rand.nextInt(16));
            final double d1 = (double)(this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            final double d2 = (double)(par3 * 16 + this.rand.nextInt(16));
            final byte b0 = 1;

            for (int i1 = 0; i1 < b0; ++i1)
            {
                final float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                final float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                final float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.generateRavine(this.rand.nextLong(), par4, par5, par6ArrayOfByte, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
            }
        }
    }

    protected boolean isOceanBlock(final byte[] data, final int index, final int x, final int y, final int z, final int chunkX, final int chunkZ)
    {
        return data[index] == Block.waterMoving.blockID || data[index] == Block.waterStill.blockID;
    }

    //Exception biomes to make sure we generate like vanilla
    private boolean isExceptionBiome(final BiomeGenBase biome)
    {
        if (biome == BiomeGenBase.mushroomIsland) return true;
        if (biome == BiomeGenBase.beach) return true;
        if (biome == BiomeGenBase.desert) return true;
        return false;
    }

    //Determine if the block at the specified location is the top block for the biome, we take into account
    //Vanilla bugs to make sure that we generate the map the same way vanilla does.
    private boolean isTopBlock(final byte[] data, final int index, final int x, final int y, final int z, final int chunkX, final int chunkZ)
    {
        final BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        return (isExceptionBiome(biome) ? data[index] == Block.grass.blockID : data[index] == biome.topBlock);
    }

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block
     * Sets the block to lava if y is less then 10, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface and if so 
     * tries to make the floor the biome's top block
     * 
     * @param data Block data array
     * @param index Pre-calculated index into block data
     * @param x local X position
     * @param y local Y position
     * @param z local Z position
     * @param chunkX Chunk X position
     * @param chunkZ Chunk Y position
     * @param foundTop True if we've encountered the biome's top block. Ideally if we've broken the surface.
     */
    protected void digBlock(final byte[] data, final int index, final int x, final int y, final int z, final int chunkX, final int chunkZ, final boolean foundTop)
    {
        final BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        final int top    = (isExceptionBiome(biome) ? Block.grass.blockID : biome.topBlock);
        final int filler = (isExceptionBiome(biome) ? Block.dirt.blockID  : biome.fillerBlock);
        final int block  = data[index];

        if (block == Block.stone.blockID || block == filler || block == top)
        {
            if (y < 10)
            {
                data[index] = (byte)Block.lavaMoving.blockID;
            }
            else
            {
                data[index] = 0;

                if (foundTop && data[index - 1] == filler)
                {
                    data[index - 1] = (byte)top;
                }
            }
        }
    }
}
