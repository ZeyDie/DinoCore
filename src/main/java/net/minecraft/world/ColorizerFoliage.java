package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorizerFoliage
{
    /** Color buffer for foliage */
    private static int[] foliageBuffer = new int[65536];

    public static void setFoliageBiomeColorizer(final int[] par0ArrayOfInteger)
    {
        foliageBuffer = par0ArrayOfInteger;
    }

    /**
     * Gets foliage color from temperature and humidity. Args: temperature, humidity
     */
    public static int getFoliageColor(final double par0, double par2)
    {
        double par21 = par2;
        par21 *= par0;
        final int i = (int)((1.0D - par0) * 255.0D);
        final int j = (int)((1.0D - par21) * 255.0D);
        return foliageBuffer[j << 8 | i];
    }

    /**
     * Gets the foliage color for pine type (metadata 1) trees
     */
    public static int getFoliageColorPine()
    {
        return 6396257;
    }

    /**
     * Gets the foliage color for birch type (metadata 2) trees
     */
    public static int getFoliageColorBirch()
    {
        return 8431445;
    }

    public static int getFoliageColorBasic()
    {
        return 4764952;
    }
}
