package net.minecraft.entity;

class EnumEntitySizeHelper
{
    static final int[] field_96565_a = new int[EnumEntitySize.values().length];

    static
    {
        try
        {
            field_96565_a[EnumEntitySize.SIZE_1.ordinal()] = 1;
        }
        catch (final NoSuchFieldError nosuchfielderror)
        {
            ;
        }

        try
        {
            field_96565_a[EnumEntitySize.SIZE_2.ordinal()] = 2;
        }
        catch (final NoSuchFieldError nosuchfielderror1)
        {
            ;
        }

        try
        {
            field_96565_a[EnumEntitySize.SIZE_3.ordinal()] = 3;
        }
        catch (final NoSuchFieldError nosuchfielderror2)
        {
            ;
        }

        try
        {
            field_96565_a[EnumEntitySize.SIZE_4.ordinal()] = 4;
        }
        catch (final NoSuchFieldError nosuchfielderror3)
        {
            ;
        }

        try
        {
            field_96565_a[EnumEntitySize.SIZE_5.ordinal()] = 5;
        }
        catch (final NoSuchFieldError nosuchfielderror4)
        {
            ;
        }

        try
        {
            field_96565_a[EnumEntitySize.SIZE_6.ordinal()] = 6;
        }
        catch (final NoSuchFieldError nosuchfielderror5)
        {
            ;
        }
    }
}
