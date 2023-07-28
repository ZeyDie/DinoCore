package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator
{
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public NoiseGeneratorPerlin()
    {
        this(new Random());
    }

    public NoiseGeneratorPerlin(final Random par1Random)
    {
        this.permutations = new int[512];
        this.xCoord = par1Random.nextDouble() * 256.0D;
        this.yCoord = par1Random.nextDouble() * 256.0D;
        this.zCoord = par1Random.nextDouble() * 256.0D;
        int i;

        for (i = 0; i < 256; this.permutations[i] = i++)
        {
            ;
        }

        for (i = 0; i < 256; ++i)
        {
            final int j = par1Random.nextInt(256 - i) + i;
            final int k = this.permutations[i];
            this.permutations[i] = this.permutations[j];
            this.permutations[j] = k;
            this.permutations[i + 256] = this.permutations[i];
        }
    }

    public final double lerp(final double par1, final double par3, final double par5)
    {
        return par3 + par1 * (par5 - par3);
    }

    public final double func_76309_a(final int par1, final double par2, final double par4)
    {
        final int j = par1 & 15;
        final double d2 = (double)(1 - ((j & 8) >> 3)) * par2;
        final double d3 = j < 4 ? 0.0D : (j != 12 && j != 14 ? par4 : par2);
        return ((j & 1) == 0 ? d2 : -d2) + ((j & 2) == 0 ? d3 : -d3);
    }

    public final double grad(final int par1, final double par2, final double par4, final double par6)
    {
        final int j = par1 & 15;
        final double d3 = j < 8 ? par2 : par4;
        final double d4 = j < 4 ? par4 : (j != 12 && j != 14 ? par6 : par2);
        return ((j & 1) == 0 ? d3 : -d3) + ((j & 2) == 0 ? d4 : -d4);
    }

    /**
     * pars: noiseArray , xOffset , yOffset , zOffset , xSize , ySize , zSize , xScale, yScale , zScale , noiseScale.
     * noiseArray should be xSize*ySize*zSize in size
     */
    public void populateNoiseArray(final double[] par1ArrayOfDouble, final double par2, final double par4, final double par6, final int par8, final int par9, final int par10, final double par11, final double par13, final double par15, final double par17)
    {
        int l;
        int i1;
        double d7;
        double d8;
        double d9;
        int j1;
        double d10;
        int k1;
        int l1;
        int i2;
        int j2;

        if (par9 == 1)
        {
            final boolean flag = false;
            final boolean flag1 = false;
            final boolean flag2 = false;
            final boolean flag3 = false;
            double d11 = 0.0D;
            double d12 = 0.0D;
            j2 = 0;
            final double d13 = 1.0D / par17;

            for (int k2 = 0; k2 < par8; ++k2)
            {
                d7 = par2 + (double)k2 * par11 + this.xCoord;
                int l2 = (int)d7;

                if (d7 < (double)l2)
                {
                    --l2;
                }

                final int i3 = l2 & 255;
                d7 -= (double)l2;
                d8 = d7 * d7 * d7 * (d7 * (d7 * 6.0D - 15.0D) + 10.0D);

                for (j1 = 0; j1 < par10; ++j1)
                {
                    d9 = par6 + (double)j1 * par15 + this.zCoord;
                    k1 = (int)d9;

                    if (d9 < (double)k1)
                    {
                        --k1;
                    }

                    l1 = k1 & 255;
                    d9 -= (double)k1;
                    d10 = d9 * d9 * d9 * (d9 * (d9 * 6.0D - 15.0D) + 10.0D);
                    l = this.permutations[i3] + 0;
                    final int j3 = this.permutations[l] + l1;
                    final int k3 = this.permutations[i3 + 1] + 0;
                    i1 = this.permutations[k3] + l1;
                    d11 = this.lerp(d8, this.func_76309_a(this.permutations[j3], d7, d9), this.grad(this.permutations[i1], d7 - 1.0D, 0.0D, d9));
                    d12 = this.lerp(d8, this.grad(this.permutations[j3 + 1], d7, 0.0D, d9 - 1.0D), this.grad(this.permutations[i1 + 1], d7 - 1.0D, 0.0D, d9 - 1.0D));
                    final double d14 = this.lerp(d10, d11, d12);
                    i2 = j2++;
                    par1ArrayOfDouble[i2] += d14 * d13;
                }
            }
        }
        else
        {
            l = 0;
            final double d15 = 1.0D / par17;
            i1 = -1;
            final boolean flag4 = false;
            final boolean flag5 = false;
            final boolean flag6 = false;
            final boolean flag7 = false;
            final boolean flag8 = false;
            final boolean flag9 = false;
            double d16 = 0.0D;
            d7 = 0.0D;
            double d17 = 0.0D;
            d8 = 0.0D;

            for (j1 = 0; j1 < par8; ++j1)
            {
                d9 = par2 + (double)j1 * par11 + this.xCoord;
                k1 = (int)d9;

                if (d9 < (double)k1)
                {
                    --k1;
                }

                l1 = k1 & 255;
                d9 -= (double)k1;
                d10 = d9 * d9 * d9 * (d9 * (d9 * 6.0D - 15.0D) + 10.0D);

                for (int l3 = 0; l3 < par10; ++l3)
                {
                    double d18 = par6 + (double)l3 * par15 + this.zCoord;
                    int i4 = (int)d18;

                    if (d18 < (double)i4)
                    {
                        --i4;
                    }

                    final int j4 = i4 & 255;
                    d18 -= (double)i4;
                    final double d19 = d18 * d18 * d18 * (d18 * (d18 * 6.0D - 15.0D) + 10.0D);

                    for (int k4 = 0; k4 < par9; ++k4)
                    {
                        double d20 = par4 + (double)k4 * par13 + this.yCoord;
                        int l4 = (int)d20;

                        if (d20 < (double)l4)
                        {
                            --l4;
                        }

                        final int i5 = l4 & 255;
                        d20 -= (double)l4;
                        final double d21 = d20 * d20 * d20 * (d20 * (d20 * 6.0D - 15.0D) + 10.0D);

                        if (k4 == 0 || i5 != i1)
                        {
                            i1 = i5;
                            final int j5 = this.permutations[l1] + i5;
                            final int k5 = this.permutations[j5] + j4;
                            final int l5 = this.permutations[j5 + 1] + j4;
                            final int i6 = this.permutations[l1 + 1] + i5;
                            j2 = this.permutations[i6] + j4;
                            final int j6 = this.permutations[i6 + 1] + j4;
                            d16 = this.lerp(d10, this.grad(this.permutations[k5], d9, d20, d18), this.grad(this.permutations[j2], d9 - 1.0D, d20, d18));
                            d7 = this.lerp(d10, this.grad(this.permutations[l5], d9, d20 - 1.0D, d18), this.grad(this.permutations[j6], d9 - 1.0D, d20 - 1.0D, d18));
                            d17 = this.lerp(d10, this.grad(this.permutations[k5 + 1], d9, d20, d18 - 1.0D), this.grad(this.permutations[j2 + 1], d9 - 1.0D, d20, d18 - 1.0D));
                            d8 = this.lerp(d10, this.grad(this.permutations[l5 + 1], d9, d20 - 1.0D, d18 - 1.0D), this.grad(this.permutations[j6 + 1], d9 - 1.0D, d20 - 1.0D, d18 - 1.0D));
                        }

                        final double d22 = this.lerp(d21, d16, d7);
                        final double d23 = this.lerp(d21, d17, d8);
                        final double d24 = this.lerp(d19, d22, d23);
                        i2 = l++;
                        par1ArrayOfDouble[i2] += d24 * d15;
                    }
                }
            }
        }
    }
}
