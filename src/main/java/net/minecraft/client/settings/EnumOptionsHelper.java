package net.minecraft.client.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)

class EnumOptionsHelper
{
    static final int[] enumOptionsMappingHelperArray = new int[EnumOptions.values().length];

    static
    {
        try
        {
            enumOptionsMappingHelperArray[EnumOptions.INVERT_MOUSE.ordinal()] = 1;
        }
        catch (final NoSuchFieldError nosuchfielderror)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.VIEW_BOBBING.ordinal()] = 2;
        }
        catch (final NoSuchFieldError nosuchfielderror1)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.ANAGLYPH.ordinal()] = 3;
        }
        catch (final NoSuchFieldError nosuchfielderror2)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.ADVANCED_OPENGL.ordinal()] = 4;
        }
        catch (final NoSuchFieldError nosuchfielderror3)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.RENDER_CLOUDS.ordinal()] = 5;
        }
        catch (final NoSuchFieldError nosuchfielderror4)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.CHAT_COLOR.ordinal()] = 6;
        }
        catch (final NoSuchFieldError nosuchfielderror5)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.CHAT_LINKS.ordinal()] = 7;
        }
        catch (final NoSuchFieldError nosuchfielderror6)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.CHAT_LINKS_PROMPT.ordinal()] = 8;
        }
        catch (final NoSuchFieldError nosuchfielderror7)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.USE_SERVER_TEXTURES.ordinal()] = 9;
        }
        catch (final NoSuchFieldError nosuchfielderror8)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.SNOOPER_ENABLED.ordinal()] = 10;
        }
        catch (final NoSuchFieldError nosuchfielderror9)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.USE_FULLSCREEN.ordinal()] = 11;
        }
        catch (final NoSuchFieldError nosuchfielderror10)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.ENABLE_VSYNC.ordinal()] = 12;
        }
        catch (final NoSuchFieldError nosuchfielderror11)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.SHOW_CAPE.ordinal()] = 13;
        }
        catch (final NoSuchFieldError nosuchfielderror12)
        {
            ;
        }

        try
        {
            enumOptionsMappingHelperArray[EnumOptions.TOUCHSCREEN.ordinal()] = 14;
        }
        catch (final NoSuchFieldError nosuchfielderror13)
        {
            ;
        }
    }
}
