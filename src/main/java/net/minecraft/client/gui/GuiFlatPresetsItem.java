package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class GuiFlatPresetsItem
{
    /** ID for the item used as icon for this preset. */
    public int iconId;

    /** Name for this preset. */
    public String presetName;

    /** Data for this preset. */
    public String presetData;

    public GuiFlatPresetsItem(final int par1, final String par2Str, final String par3Str)
    {
        this.iconId = par1;
        this.presetName = par2Str;
        this.presetData = par3Str;
    }
}
