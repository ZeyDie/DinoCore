package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
class GuiBeaconButtonConfirm extends GuiBeaconButton
{
    /** Beacon GUI this button belongs to. */
    final GuiBeacon beaconGui;

    public GuiBeaconButtonConfirm(final GuiBeacon par1GuiBeacon, final int par2, final int par3, final int par4)
    {
        super(par2, par3, par4, GuiBeacon.getBeaconGuiTextures(), 90, 220);
        this.beaconGui = par1GuiBeacon;
    }

    public void func_82251_b(final int par1, final int par2)
    {
        this.beaconGui.drawCreativeTabHoveringText(I18n.getString("gui.done"), par1, par2);
    }
}
