package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
class SelectionListInvited extends SelectionListBase
{
    final GuiScreenConfigureWorld theGuiScreenConfigureWorld;

    public SelectionListInvited(final GuiScreenConfigureWorld par1GuiScreenConfigureWorld)
    {
        super(GuiScreenConfigureWorld.getMinecraft(par1GuiScreenConfigureWorld), GuiScreenConfigureWorld.func_96271_b(par1GuiScreenConfigureWorld), GuiScreenConfigureWorld.func_96274_a(par1GuiScreenConfigureWorld, 2), GuiScreenConfigureWorld.func_96269_c(par1GuiScreenConfigureWorld), GuiScreenConfigureWorld.func_96274_a(par1GuiScreenConfigureWorld, 9) - GuiScreenConfigureWorld.func_96274_a(par1GuiScreenConfigureWorld, 2), 12);
        this.theGuiScreenConfigureWorld = par1GuiScreenConfigureWorld;
    }

    protected int func_96608_a()
    {
        return GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.size() + 1;
    }

    protected void func_96615_a(final int par1, final boolean par2)
    {
        if (par1 < GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.size())
        {
            GuiScreenConfigureWorld.func_96270_b(this.theGuiScreenConfigureWorld, par1);
        }
    }

    protected boolean func_96609_a(final int par1)
    {
        return par1 == GuiScreenConfigureWorld.func_96263_e(this.theGuiScreenConfigureWorld);
    }

    protected int func_96613_b()
    {
        return this.func_96608_a() * 12;
    }

    protected void func_96611_c() {}

    protected void func_96610_a(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        if (par1 < GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.size())
        {
            this.func_98263_b(par1, par2, par3, par4, par5Tessellator);
        }
    }

    private void func_98263_b(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        final String s = (String)GuiScreenConfigureWorld.func_96266_d(this.theGuiScreenConfigureWorld).field_96402_f.get(par1);
        this.theGuiScreenConfigureWorld.drawString(GuiScreenConfigureWorld.func_96273_f(this.theGuiScreenConfigureWorld), s, par2 + 2, par3 + 1, 16777215);
    }
}
