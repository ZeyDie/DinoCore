package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.*;
import java.util.Map.Entry;

public class FlatGeneratorInfo
{
    /** List of layers on this preset. */
    private final List flatLayers = new ArrayList();

    /** List of world features enabled on this preset. */
    private final Map worldFeatures = new HashMap();
    private int biomeToUse;

    /**
     * Return the biome used on this preset.
     */
    public int getBiome()
    {
        return this.biomeToUse;
    }

    /**
     * Set the biome used on this preset.
     */
    public void setBiome(final int par1)
    {
        this.biomeToUse = par1;
    }

    /**
     * Return the list of world features enabled on this preset.
     */
    public Map getWorldFeatures()
    {
        return this.worldFeatures;
    }

    /**
     * Return the list of layers on this preset.
     */
    public List getFlatLayers()
    {
        return this.flatLayers;
    }

    public void func_82645_d()
    {
        int i = 0;
        FlatLayerInfo flatlayerinfo;

        for (final Iterator iterator = this.flatLayers.iterator(); iterator.hasNext(); i += flatlayerinfo.getLayerCount())
        {
            flatlayerinfo = (FlatLayerInfo)iterator.next();
            flatlayerinfo.setMinY(i);
        }
    }

    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(2);
        stringbuilder.append(";");
        int i;

        for (i = 0; i < this.flatLayers.size(); ++i)
        {
            if (i > 0)
            {
                stringbuilder.append(",");
            }

            stringbuilder.append(((FlatLayerInfo)this.flatLayers.get(i)).toString());
        }

        stringbuilder.append(";");
        stringbuilder.append(this.biomeToUse);

        if (!this.worldFeatures.isEmpty())
        {
            stringbuilder.append(";");
            i = 0;
            final Iterator iterator = this.worldFeatures.entrySet().iterator();

            while (iterator.hasNext())
            {
                final Entry entry = (Entry)iterator.next();

                if (i++ > 0)
                {
                    stringbuilder.append(",");
                }

                stringbuilder.append(((String)entry.getKey()).toLowerCase());
                final Map map = (Map)entry.getValue();

                if (!map.isEmpty())
                {
                    stringbuilder.append("(");
                    int j = 0;
                    final Iterator iterator1 = map.entrySet().iterator();

                    while (iterator1.hasNext())
                    {
                        final Entry entry1 = (Entry)iterator1.next();

                        if (j++ > 0)
                        {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append((String)entry1.getKey());
                        stringbuilder.append("=");
                        stringbuilder.append((String)entry1.getValue());
                    }

                    stringbuilder.append(")");
                }
            }
        }
        else
        {
            stringbuilder.append(";");
        }

        return stringbuilder.toString();
    }

    private static FlatLayerInfo func_82646_a(final String par0Str, final int par1)
    {
        String[] astring = par0Str.split("x", 2);
        int j = 1;
        int k = 0;

        if (astring.length == 2)
        {
            try
            {
                j = Integer.parseInt(astring[0]);

                if (par1 + j >= 256)
                {
                    j = 256 - par1;
                }

                if (j < 0)
                {
                    j = 0;
                }
            }
            catch (final Throwable throwable)
            {
                return null;
            }
        }

        int l;

        try
        {
            final String s1 = astring[astring.length - 1];
            astring = s1.split(":", 2);
            l = Integer.parseInt(astring[0]);

            if (astring.length > 1)
            {
                k = Integer.parseInt(astring[1]);
            }

            if (Block.blocksList[l] == null)
            {
                l = 0;
                k = 0;
            }

            if (k < 0 || k > 15)
            {
                k = 0;
            }
        }
        catch (final Throwable throwable1)
        {
            return null;
        }

        final FlatLayerInfo flatlayerinfo = new FlatLayerInfo(j, l, k);
        flatlayerinfo.setMinY(par1);
        return flatlayerinfo;
    }

    private static List func_82652_b(final String par0Str)
    {
        if (par0Str != null && par0Str.length() >= 1)
        {
            final ArrayList arraylist = new ArrayList();
            final String[] astring = par0Str.split(",");
            int i = 0;
            final String[] astring1 = astring;
            final int j = astring.length;

            for (int k = 0; k < j; ++k)
            {
                final String s1 = astring1[k];
                final FlatLayerInfo flatlayerinfo = func_82646_a(s1, i);

                if (flatlayerinfo == null)
                {
                    return null;
                }

                arraylist.add(flatlayerinfo);
                i += flatlayerinfo.getLayerCount();
            }

            return arraylist;
        }
        else
        {
            return null;
        }
    }

    public static FlatGeneratorInfo createFlatGeneratorFromString(final String par0Str)
    {
        if (par0Str == null)
        {
            return getDefaultFlatGenerator();
        }
        else
        {
            final String[] astring = par0Str.split(";", -1);
            final int i = astring.length == 1 ? 0 : MathHelper.parseIntWithDefault(astring[0], 0);

            if (i >= 0 && i <= 2)
            {
                final FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
                int j = astring.length == 1 ? 0 : 1;
                final List list = func_82652_b(astring[j++]);

                if (list != null && !list.isEmpty())
                {
                    flatgeneratorinfo.getFlatLayers().addAll(list);
                    flatgeneratorinfo.func_82645_d();
                    int k = BiomeGenBase.plains.biomeID;

                    if (i > 0 && astring.length > j)
                    {
                        k = MathHelper.parseIntWithDefault(astring[j++], k);
                    }

                    flatgeneratorinfo.setBiome(k);

                    if (i > 0 && astring.length > j)
                    {
                        final String[] astring1 = astring[j++].toLowerCase().split(",");
                        final String[] astring2 = astring1;
                        final int l = astring1.length;

                        for (int i1 = 0; i1 < l; ++i1)
                        {
                            final String s1 = astring2[i1];
                            final String[] astring3 = s1.split("\\(", 2);
                            final HashMap hashmap = new HashMap();

                            if (!astring3[0].isEmpty())
                            {
                                flatgeneratorinfo.getWorldFeatures().put(astring3[0], hashmap);

                                if (astring3.length > 1 && astring3[1].endsWith(")") && astring3[1].length() > 1)
                                {
                                    final String[] astring4 = astring3[1].substring(0, astring3[1].length() - 1).split(" ");

                                    for (int j1 = 0; j1 < astring4.length; ++j1)
                                    {
                                        final String[] astring5 = astring4[j1].split("=", 2);

                                        if (astring5.length == 2)
                                        {
                                            hashmap.put(astring5[0], astring5[1]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        flatgeneratorinfo.getWorldFeatures().put("village", new HashMap());
                    }

                    return flatgeneratorinfo;
                }
                else
                {
                    return getDefaultFlatGenerator();
                }
            }
            else
            {
                return getDefaultFlatGenerator();
            }
        }
    }

    public static FlatGeneratorInfo getDefaultFlatGenerator()
    {
        final FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
        flatgeneratorinfo.setBiome(BiomeGenBase.plains.biomeID);
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Block.bedrock.blockID));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(2, Block.dirt.blockID));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Block.grass.blockID));
        flatgeneratorinfo.func_82645_d();
        flatgeneratorinfo.getWorldFeatures().put("village", new HashMap());
        return flatgeneratorinfo;
    }
}
