package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorizerGrass
{
    /** Color buffer for grass */
    private static int[] grassBuffer = new int[65536];

    public static void setGrassBiomeColorizer(final int[] par0ArrayOfInteger)
    {
        grassBuffer = par0ArrayOfInteger;
    }

    /**
     * Gets grass color from temperature and humidity. Args: temperature, humidity
     */
    public static int getGrassColor(final double par0, double par2)
    {
        double par21 = par2;
        par21 *= par0;
        final int i = (int)((1.0D - par0) * 255.0D);
        final int j = (int)((1.0D - par21) * 255.0D);
        return grassBuffer[j << 8 | i];
    }
}
