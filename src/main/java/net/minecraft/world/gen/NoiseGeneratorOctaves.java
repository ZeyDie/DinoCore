package net.minecraft.world.gen;

import net.minecraft.util.MathHelper;

import java.util.Random;

public class NoiseGeneratorOctaves extends NoiseGenerator
{
    /**
     * Collection of noise generation functions.  Output is combined to produce different octaves of noise.
     */
    private NoiseGeneratorPerlin[] generatorCollection;
    private int octaves;

    public NoiseGeneratorOctaves(final Random par1Random, final int par2)
    {
        this.octaves = par2;
        this.generatorCollection = new NoiseGeneratorPerlin[par2];

        for (int j = 0; j < par2; ++j)
        {
            this.generatorCollection[j] = new NoiseGeneratorPerlin(par1Random);
        }
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public double[] generateNoiseOctaves(double[] par1ArrayOfDouble, final int par2, final int par3, final int par4, final int par5, final int par6, final int par7, final double par8, final double par10, final double par12)
    {
        double[] par1ArrayOfDouble1 = par1ArrayOfDouble;
        if (par1ArrayOfDouble1 == null)
        {
            par1ArrayOfDouble1 = new double[par5 * par6 * par7];
        }
        else
        {
            for (int k1 = 0; k1 < par1ArrayOfDouble1.length; ++k1)
            {
                par1ArrayOfDouble1[k1] = 0.0D;
            }
        }

        double d3 = 1.0D;

        for (int l1 = 0; l1 < this.octaves; ++l1)
        {
            double d4 = (double)par2 * d3 * par8;
            final double d5 = (double)par3 * d3 * par10;
            double d6 = (double)par4 * d3 * par12;
            long i2 = MathHelper.floor_double_long(d4);
            long j2 = MathHelper.floor_double_long(d6);
            d4 -= (double)i2;
            d6 -= (double)j2;
            i2 %= 16777216L;
            j2 %= 16777216L;
            d4 += (double)i2;
            d6 += (double)j2;
            this.generatorCollection[l1].populateNoiseArray(par1ArrayOfDouble1, d4, d5, d6, par5, par6, par7, par8 * d3, par10 * d3, par12 * d3, d3);
            d3 /= 2.0D;
        }

        return par1ArrayOfDouble1;
    }

    /**
     * Bouncer function to the main one with some default arguments.
     */
    public double[] generateNoiseOctaves(final double[] par1ArrayOfDouble, final int par2, final int par3, final int par4, final int par5, final double par6, final double par8, final double par10)
    {
        return this.generateNoiseOctaves(par1ArrayOfDouble, par2, 10, par3, par4, 1, par5, par6, 1.0D, par8);
    }
}
