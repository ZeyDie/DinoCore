package net.minecraft.world.gen.structure;

class EnumDoorHelper
{
    static final int[] doorEnum = new int[EnumDoor.values().length];

    static
    {
        try
        {
            doorEnum[EnumDoor.OPENING.ordinal()] = 1;
        }
        catch (final NoSuchFieldError nosuchfielderror)
        {
            ;
        }

        try
        {
            doorEnum[EnumDoor.WOOD_DOOR.ordinal()] = 2;
        }
        catch (final NoSuchFieldError nosuchfielderror1)
        {
            ;
        }

        try
        {
            doorEnum[EnumDoor.GRATES.ordinal()] = 3;
        }
        catch (final NoSuchFieldError nosuchfielderror2)
        {
            ;
        }

        try
        {
            doorEnum[EnumDoor.IRON_DOOR.ordinal()] = 4;
        }
        catch (final NoSuchFieldError nosuchfielderror3)
        {
            ;
        }
    }
}
