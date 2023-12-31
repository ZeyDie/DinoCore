package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum GuiScreenConfirmationType
{
    Warning("Warning!", 16711680),
    Info("Info!", 8226750);
    public final int field_140075_c;
    public final String field_140072_d;

    private GuiScreenConfirmationType(final String par3Str, final int par4)
    {
        this.field_140072_d = par3Str;
        this.field_140075_c = par4;
    }
}
