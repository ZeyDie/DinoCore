package net.minecraftforge.client;

import net.minecraft.client.settings.EnumOptions;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumOS;
import net.minecraft.world.EnumGameType;
import net.minecraftforge.common.EnumHelper;

public class EnumHelperClient extends EnumHelper
{

    private static Class[][] clentTypes =
    {
        {EnumGameType.class, int.class, String.class},
        {EnumOptions.class, String.class, boolean.class, boolean.class},
        {EnumOS.class},
        {EnumRarity.class, int.class, String.class}
    };
    
    public static EnumGameType addGameType(final String name, final int id, final String displayName)
    {
        return addEnum(EnumGameType.class, name, id, displayName);
    }
    
    public static EnumOptions addOptions(final String name, final String langName, final boolean isSlider, final boolean isToggle)
    {
        return addEnum(EnumOptions.class, name, langName, isSlider, isToggle);
    }
    
    public static EnumOS addOS2(final String name)
    {
        return addEnum(EnumOS.class, name);
    }
    
    public static EnumRarity addRarity(final String name, final int color, final String displayName)
    {
        return addEnum(EnumRarity.class, name, color, displayName);
    }

    public static <T extends Enum<? >> T addEnum(final Class<T> enumType, final String enumName, final Object... paramValues)
    {
        return addEnum(clentTypes, enumType, enumName, paramValues);
    }
}
