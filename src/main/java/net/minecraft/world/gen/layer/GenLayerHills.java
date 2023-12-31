package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerHills extends GenLayer
{
    public GenLayerHills(final long par1, final GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(final int par1, final int par2, final int par3, final int par4)
    {
        final int[] aint = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        final int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1)
        {
            for (int j1 = 0; j1 < par3; ++j1)
            {
                this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                final int k1 = aint[j1 + 1 + (i1 + 1) * (par3 + 2)];

                if (this.nextInt(3) == 0)
                {
                    int l1 = k1;

                    if (k1 == BiomeGenBase.desert.biomeID)
                    {
                        l1 = BiomeGenBase.desertHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.forest.biomeID)
                    {
                        l1 = BiomeGenBase.forestHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.taiga.biomeID)
                    {
                        l1 = BiomeGenBase.taigaHills.biomeID;
                    }
                    else if (k1 == BiomeGenBase.plains.biomeID)
                    {
                        l1 = BiomeGenBase.forest.biomeID;
                    }
                    else if (k1 == BiomeGenBase.icePlains.biomeID)
                    {
                        l1 = BiomeGenBase.iceMountains.biomeID;
                    }
                    else if (k1 == BiomeGenBase.jungle.biomeID)
                    {
                        l1 = BiomeGenBase.jungleHills.biomeID;
                    }

                    if (l1 == k1)
                    {
                        aint1[j1 + i1 * par3] = k1;
                    }
                    else
                    {
                        final int i2 = aint[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)];
                        final int j2 = aint[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)];
                        final int k2 = aint[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)];
                        final int l2 = aint[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)];

                        if (i2 == k1 && j2 == k1 && k2 == k1 && l2 == k1)
                        {
                            aint1[j1 + i1 * par3] = l1;
                        }
                        else
                        {
                            aint1[j1 + i1 * par3] = k1;
                        }
                    }
                }
                else
                {
                    aint1[j1 + i1 * par3] = k1;
                }
            }
        }

        return aint1;
    }
}
