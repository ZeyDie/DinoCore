package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.regex.Pattern;

public class StringUtils
{
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    @SideOnly(Side.CLIENT)

    /**
     * Returns the time elapsed for the given number of ticks, in "mm:ss" format.
     */
    public static String ticksToElapsedTime(final int par0)
    {
        int j = par0 / 20;
        final int k = j / 60;
        j %= 60;
        return j < 10 ? k + ":0" + j : k + ":" + j;
    }

    public static String stripControlCodes(final String par0Str)
    {
        return patternControlCode.matcher(par0Str).replaceAll("");
    }
}
