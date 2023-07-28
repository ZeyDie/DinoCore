package net.minecraft.util;

public class StatCollector
{
    private static StringTranslate localizedName = StringTranslate.getInstance();

    /**
     * Translates a Stat name
     */
    public static String translateToLocal(final String par0Str)
    {
        return localizedName.translateKey(par0Str);
    }

    /**
     * Translates a Stat name with format args
     */
    public static String translateToLocalFormatted(final String par0Str, final Object ... par1ArrayOfObj)
    {
        return localizedName.translateKeyFormat(par0Str, par1ArrayOfObj);
    }

    public static boolean func_94522_b(final String par0Str)
    {
        return localizedName.containsTranslateKey(par0Str);
    }
}
