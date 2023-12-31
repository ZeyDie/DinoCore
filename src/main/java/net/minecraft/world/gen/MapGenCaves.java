package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class MapGenCaves extends MapGenBase
{
    /**
     * Generates a larger initial cave node than usual. Called 25% of the time.
     */
    protected void generateLargeCaveNode(final long par1, final int par3, final int par4, final byte[] par5ArrayOfByte, final double par6, final double par8, final double par10)
    {
        this.generateCaveNode(par1, par3, par4, par5ArrayOfByte, par6, par8, par10, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    /**
     * Generates a node in the current cave system recursion tree.
     */
    protected void generateCaveNode(final long par1, final int par3, final int par4, final byte[] par5ArrayOfByte, double par6, double par8, double par10, final float par12, float par13, float par14, int par15, int par16, final double par17)
    {
        int par161 = par16;
        int par151 = par15;
        double par61 = par6;
        double par81 = par8;
        double par101 = par10;
        float par141 = par14;
        float par131 = par13;
        final double d4 = (double)(par3 * 16 + 8);
        final double d5 = (double)(par4 * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;
        final Random random = new Random(par1);

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

        final int k1 = random.nextInt(par161 / 2) + par161 / 4;

        for (final boolean flag1 = random.nextInt(6) == 0; par151 < par161; ++par151)
        {
            final double d6 = 1.5D + (double)(MathHelper.sin((float) par151 * (float)Math.PI / (float) par161) * par12 * 1.0F);
            final double d7 = d6 * par17;
            final float f5 = MathHelper.cos(par141);
            final float f6 = MathHelper.sin(par141);
            par61 += (double)(MathHelper.cos(par131) * f5);
            par81 += (double)f6;
            par101 += (double)(MathHelper.sin(par131) * f5);

            if (flag1)
            {
                par141 *= 0.92F;
            }
            else
            {
                par141 *= 0.7F;
            }

            par141 += f4 * 0.1F;
            par131 += f3 * 0.1F;
            f4 *= 0.9F;
            f3 *= 0.75F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag && par151 == k1 && par12 > 1.0F && par161 > 0)
            {
                this.generateCaveNode(random.nextLong(), par3, par4, par5ArrayOfByte, par61, par81, par101, random.nextFloat() * 0.5F + 0.5F, par131 - ((float)Math.PI / 2.0F), par141 / 3.0F, par151, par161, 1.0D);
                this.generateCaveNode(random.nextLong(), par3, par4, par5ArrayOfByte, par61, par81, par101, random.nextFloat() * 0.5F + 0.5F, par131 + ((float)Math.PI / 2.0F), par141 / 3.0F, par151, par161, 1.0D);
                return;
            }

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

                    boolean flag2 = false;
                    int j3;
                    int k3;

                    for (j3 = l1; !flag2 && j3 < i2; ++j3)
                    {
                        for (int l3 = l2; !flag2 && l3 < i3; ++l3)
                        {
                            for (int i4 = k2 + 1; !flag2 && i4 >= j2 - 1; --i4)
                            {
                                k3 = (j3 * 16 + l3) * 128 + i4;

                                if (i4 >= 0 && i4 < 128)
                                {
                                    if (isOceanBlock(par5ArrayOfByte, k3, j3, i4, l3, par3, par4))
                                    {
                                        flag2 = true;
                                    }

                                    if (i4 != j2 - 1 && j3 != l1 && j3 != i2 - 1 && l3 != l2 && l3 != i3 - 1)
                                    {
                                        i4 = j2;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag2)
                    {
                        for (j3 = l1; j3 < i2; ++j3)
                        {
                            final double d12 = ((double)(j3 + par3 * 16) + 0.5D - par61) / d6;

                            for (k3 = l2; k3 < i3; ++k3)
                            {
                                final double d13 = ((double)(k3 + par4 * 16) + 0.5D - par101) / d6;
                                int j4 = (j3 * 16 + k3) * 128 + k2;
                                boolean flag3 = false;

                                if (d12 * d12 + d13 * d13 < 1.0D)
                                {
                                    for (int k4 = k2 - 1; k4 >= j2; --k4)
                                    {
                                        final double d14 = ((double)k4 + 0.5D - par81) / d7;

                                        if (d14 > -0.7D && d12 * d12 + d14 * d14 + d13 * d13 < 1.0D)
                                        {
                                            if (isTopBlock(par5ArrayOfByte, j4, j3, k4, k3, par3, par4))
                                            {
                                                flag3 = true;
                                            }

                                            digBlock(par5ArrayOfByte, j4, j3, k4, k3, par3, par4, flag3);
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
        int i1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);

        if (this.rand.nextInt(15) != 0)
        {
            i1 = 0;
        }

        for (int j1 = 0; j1 < i1; ++j1)
        {
            final double d0 = (double)(par2 * 16 + this.rand.nextInt(16));
            final double d1 = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
            final double d2 = (double)(par3 * 16 + this.rand.nextInt(16));
            int k1 = 1;

            if (this.rand.nextInt(4) == 0)
            {
                this.generateLargeCaveNode(this.rand.nextLong(), par4, par5, par6ArrayOfByte, d0, d1, d2);
                k1 += this.rand.nextInt(4);
            }

            for (int l1 = 0; l1 < k1; ++l1)
            {
                final float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                final float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0)
                {
                    f2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.generateCaveNode(this.rand.nextLong(), par4, par5, par6ArrayOfByte, d0, d1, d2, f2, f, f1, 0, 0, 1.0D);
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
