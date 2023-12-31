package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.mco.PendingInvite;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
class GuiScreenPendingInvitationList extends GuiScreenSelectLocation
{
    final GuiScreenPendingInvitation field_130120_a;

    public GuiScreenPendingInvitationList(final GuiScreenPendingInvitation par1GuiScreenPendingInvitation)
    {
        super(GuiScreenPendingInvitation.func_130054_j(par1GuiScreenPendingInvitation), par1GuiScreenPendingInvitation.width, par1GuiScreenPendingInvitation.height, 32, par1GuiScreenPendingInvitation.height - 64, 36);
        this.field_130120_a = par1GuiScreenPendingInvitation;
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).size() + 1;
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(final int par1, final boolean par2)
    {
        if (par1 < GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).size())
        {
            GuiScreenPendingInvitation.func_130053_a(this.field_130120_a, par1);
        }
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(final int par1)
    {
        return par1 == GuiScreenPendingInvitation.func_130049_d(this.field_130120_a);
    }

    protected boolean func_104086_b(final int par1)
    {
        return false;
    }

    protected int func_130003_b()
    {
        return this.getSize() * 36;
    }

    protected void func_130004_c()
    {
        this.field_130120_a.drawDefaultBackground();
    }

    protected void drawSlot(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        if (par1 < GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).size())
        {
            this.func_130119_b(par1, par2, par3, par4, par5Tessellator);
        }
    }

    private void func_130119_b(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        final PendingInvite pendinginvite = (PendingInvite)GuiScreenPendingInvitation.func_130042_e(this.field_130120_a).get(par1);
        this.field_130120_a.drawString(GuiScreenPendingInvitation.func_130045_k(this.field_130120_a), pendinginvite.field_130092_b, par2 + 2, par3 + 1, 16777215);
        this.field_130120_a.drawString(GuiScreenPendingInvitation.func_130052_l(this.field_130120_a), pendinginvite.field_130093_c, par2 + 2, par3 + 12, 7105644);
    }
}
