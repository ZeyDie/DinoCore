package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.PlayerCapabilities;

public enum EnumGameType
{
    NOT_SET(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure");
    int id;
    String name;

    private EnumGameType(final int par3, final String par4Str)
    {
        this.id = par3;
        this.name = par4Str;
    }

    /**
     * Returns the ID of this game type
     */
    public int getID()
    {
        return this.id;
    }

    /**
     * Returns the name of this game type
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Configures the player capabilities based on the game type
     */
    public void configurePlayerCapabilities(final PlayerCapabilities par1PlayerCapabilities)
    {
        if (this == CREATIVE)
        {
            par1PlayerCapabilities.allowFlying = true;
            par1PlayerCapabilities.isCreativeMode = true;
            par1PlayerCapabilities.disableDamage = true;
        }
        else
        {
            par1PlayerCapabilities.allowFlying = false;
            par1PlayerCapabilities.isCreativeMode = false;
            par1PlayerCapabilities.disableDamage = false;
            par1PlayerCapabilities.isFlying = false;
        }

        par1PlayerCapabilities.allowEdit = !this.isAdventure();
    }

    /**
     * Returns true if this is the ADVENTURE game type
     */
    public boolean isAdventure()
    {
        return this == ADVENTURE;
    }

    /**
     * Returns true if this is the CREATIVE game type
     */
    public boolean isCreative()
    {
        return this == CREATIVE;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if this is the SURVIVAL or ADVENTURE game type
     */
    public boolean isSurvivalOrAdventure()
    {
        return this == SURVIVAL || this == ADVENTURE;
    }

    /**
     * Returns the game type with the specified ID, or SURVIVAL if none found. Args: id
     */
    public static EnumGameType getByID(final int par0)
    {
        final EnumGameType[] aenumgametype = values();
        final int j = aenumgametype.length;

        for (int k = 0; k < j; ++k)
        {
            final EnumGameType enumgametype = aenumgametype[k];

            if (enumgametype.id == par0)
            {
                return enumgametype;
            }
        }

        return SURVIVAL;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the game type with the specified name, or SURVIVAL if none found. This is case sensitive. Args: name
     */
    public static EnumGameType getByName(final String par0Str)
    {
        final EnumGameType[] aenumgametype = values();
        final int i = aenumgametype.length;

        for (int j = 0; j < i; ++j)
        {
            final EnumGameType enumgametype = aenumgametype[j];

            if (enumgametype.name.equals(par0Str))
            {
                return enumgametype;
            }
        }

        return SURVIVAL;
    }
}
