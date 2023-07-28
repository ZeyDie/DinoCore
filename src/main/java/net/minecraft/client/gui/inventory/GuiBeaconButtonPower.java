package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;

@SideOnly(Side.CLIENT)
class GuiBeaconButtonPower extends GuiBeaconButton
{
    private final int field_82261_l;
    private final int field_82262_m;

    /** Beacon GUI this button belongs to. */
    final GuiBeacon beaconGui;

    public GuiBeaconButtonPower(final GuiBeacon par1GuiBeacon, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        super(par2, par3, par4, GuiContainer.field_110408_a, 0 + Potion.potionTypes[par5].getStatusIconIndex() % 8 * 18, 198 + Potion.potionTypes[par5].getStatusIconIndex() / 8 * 18);
        this.beaconGui = par1GuiBeacon;
        this.field_82261_l = par5;
        this.field_82262_m = par6;
    }

    public void func_82251_b(final int par1, final int par2)
    {
        String s = I18n.getString(Potion.potionTypes[this.field_82261_l].getName());

        if (this.field_82262_m >= 3 && this.field_82261_l != Potion.regeneration.id)
        {
            s = s + " II";
        }

        this.beaconGui.drawCreativeTabHoveringText(s, par1, par2);
    }
}
