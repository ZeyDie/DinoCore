package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreenSelectLocation;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
class GuiScreenMcoWorldTemplateSelectionList extends GuiScreenSelectLocation
{
    final GuiScreenMcoWorldTemplate field_111245_a;

    public GuiScreenMcoWorldTemplateSelectionList(final GuiScreenMcoWorldTemplate par1GuiScreenMcoWorldTemplate)
    {
        super(GuiScreenMcoWorldTemplate.func_130066_c(par1GuiScreenMcoWorldTemplate), par1GuiScreenMcoWorldTemplate.width, par1GuiScreenMcoWorldTemplate.height, 32, par1GuiScreenMcoWorldTemplate.height - 64, 36);
        this.field_111245_a = par1GuiScreenMcoWorldTemplate;
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size() + 1;
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(final int par1, final boolean par2)
    {
        if (par1 < GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size())
        {
            GuiScreenMcoWorldTemplate.func_130064_a(this.field_111245_a, par1);
            GuiScreenMcoWorldTemplate.func_130065_a(this.field_111245_a, (WorldTemplate)null);
        }
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(final int par1)
    {
        return GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).isEmpty() ? false : (par1 >= GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size() ? false : (GuiScreenMcoWorldTemplate.func_130067_e(this.field_111245_a) != null ? GuiScreenMcoWorldTemplate.func_130067_e(this.field_111245_a).field_110732_b.equals(((WorldTemplate)GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).get(par1)).field_110732_b) : par1 == GuiScreenMcoWorldTemplate.func_130062_f(this.field_111245_a)));
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
        this.field_111245_a.drawDefaultBackground();
    }

    protected void drawSlot(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        if (par1 < GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).size())
        {
            this.func_111244_b(par1, par2, par3, par4, par5Tessellator);
        }
    }

    private void func_111244_b(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        final WorldTemplate worldtemplate = (WorldTemplate)GuiScreenMcoWorldTemplate.func_110395_c(this.field_111245_a).get(par1);
        this.field_111245_a.drawString(GuiScreenMcoWorldTemplate.func_110389_g(this.field_111245_a), worldtemplate.field_110732_b, par2 + 2, par3 + 1, 16777215);
        this.field_111245_a.drawString(GuiScreenMcoWorldTemplate.func_110387_h(this.field_111245_a), worldtemplate.field_110731_d, par2 + 2, par3 + 12, 7105644);
        this.field_111245_a.drawString(GuiScreenMcoWorldTemplate.func_110384_i(this.field_111245_a), worldtemplate.field_110733_c, par2 + 2 + 207 - GuiScreenMcoWorldTemplate.func_130063_j(this.field_111245_a).getStringWidth(worldtemplate.field_110733_c), par3 + 1, 5000268);
    }
}
