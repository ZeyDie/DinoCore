package net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class I18n
{
    private static Locale i18nLocale;

    static void setLocale(final Locale par0Locale)
    {
        i18nLocale = par0Locale;
    }

    public static String getString(final String par0Str)
    {
        return i18nLocale.translateKey(par0Str);
    }

    public static String getStringParams(final String par0Str, final Object ... par1ArrayOfObj)
    {
        return i18nLocale.formatMessage(par0Str, par1ArrayOfObj);
    }
}
