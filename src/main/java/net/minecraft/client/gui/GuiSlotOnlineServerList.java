package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
class GuiSlotOnlineServerList extends GuiScreenSelectLocation
{
    final GuiScreenOnlineServers field_96294_a;

    public GuiSlotOnlineServerList(final GuiScreenOnlineServers par1GuiScreenOnlineServers)
    {
        super(GuiScreenOnlineServers.func_140037_f(par1GuiScreenOnlineServers), par1GuiScreenOnlineServers.width, par1GuiScreenOnlineServers.height, 32, par1GuiScreenOnlineServers.height - 64, 36);
        this.field_96294_a = par1GuiScreenOnlineServers;
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size() + 1;
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(final int par1, final boolean par2)
    {
        if (par1 < GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size())
        {
            final McoServer mcoserver = (McoServer)GuiScreenOnlineServers.func_140013_c(this.field_96294_a).get(par1);
            GuiScreenOnlineServers.func_140036_b(this.field_96294_a, mcoserver.field_96408_a);

            if (!GuiScreenOnlineServers.func_140015_g(this.field_96294_a).getSession().getUsername().equals(mcoserver.field_96405_e))
            {
                GuiScreenOnlineServers.func_140038_h(this.field_96294_a).displayString = I18n.getString("mco.selectServer.leave");
            }
            else
            {
                GuiScreenOnlineServers.func_140038_h(this.field_96294_a).displayString = I18n.getString("mco.selectServer.configure");
            }

            GuiScreenOnlineServers.func_140033_i(this.field_96294_a).enabled = mcoserver.field_96404_d.equals("OPEN") && !mcoserver.field_98166_h;

            if (par2 && GuiScreenOnlineServers.func_140033_i(this.field_96294_a).enabled)
            {
                GuiScreenOnlineServers.func_140008_c(this.field_96294_a, GuiScreenOnlineServers.func_140041_a(this.field_96294_a));
            }
        }
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(final int par1)
    {
        return par1 == GuiScreenOnlineServers.func_140027_d(this.field_96294_a, GuiScreenOnlineServers.func_140041_a(this.field_96294_a));
    }

    protected boolean func_104086_b(final int par1)
    {
        try
        {
            return par1 >= 0 && par1 < GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size() && ((McoServer)GuiScreenOnlineServers.func_140013_c(this.field_96294_a).get(par1)).field_96405_e.toLowerCase().equals(GuiScreenOnlineServers.func_104032_j(this.field_96294_a).getSession().getUsername());
        }
        catch (final Exception exception)
        {
            return false;
        }
    }

    protected int func_130003_b()
    {
        return this.getSize() * 36;
    }

    protected void func_130004_c()
    {
        this.field_96294_a.drawDefaultBackground();
    }

    protected void drawSlot(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        if (par1 < GuiScreenOnlineServers.func_140013_c(this.field_96294_a).size())
        {
            this.func_96292_b(par1, par2, par3, par4, par5Tessellator);
        }
    }

    private void func_96292_b(final int par1, final int par2, final int par3, final int par4, final Tessellator par5Tessellator)
    {
        final McoServer mcoserver = (McoServer)GuiScreenOnlineServers.func_140013_c(this.field_96294_a).get(par1);
        this.field_96294_a.drawString(GuiScreenOnlineServers.func_140023_k(this.field_96294_a), mcoserver.func_96398_b(), par2 + 2, par3 + 1, 16777215);
        final short short1 = 207;
        final byte b0 = 1;

        if (mcoserver.field_98166_h)
        {
            GuiScreenOnlineServers.func_104031_c(this.field_96294_a, par2 + short1, par3 + b0, this.field_104094_d, this.field_104095_e);
        }
        else if (mcoserver.field_96404_d.equals("CLOSED"))
        {
            GuiScreenOnlineServers.func_140035_b(this.field_96294_a, par2 + short1, par3 + b0, this.field_104094_d, this.field_104095_e);
        }
        else if (mcoserver.field_96405_e.equals(GuiScreenOnlineServers.func_140014_l(this.field_96294_a).getSession().getUsername()) && mcoserver.field_104063_i < 7)
        {
            this.func_96293_a(par1, par2 - 14, par3, mcoserver);
            GuiScreenOnlineServers.func_140031_a(this.field_96294_a, par2 + short1, par3 + b0, this.field_104094_d, this.field_104095_e, mcoserver.field_104063_i);
        }
        else if (mcoserver.field_96404_d.equals("OPEN"))
        {
            GuiScreenOnlineServers.func_140020_c(this.field_96294_a, par2 + short1, par3 + b0, this.field_104094_d, this.field_104095_e);
            this.func_96293_a(par1, par2 - 14, par3, mcoserver);
        }

        this.field_96294_a.drawString(GuiScreenOnlineServers.func_140039_m(this.field_96294_a), mcoserver.func_96397_a(), par2 + 2, par3 + 12, 7105644);
        this.field_96294_a.drawString(GuiScreenOnlineServers.func_98079_k(this.field_96294_a), mcoserver.field_96405_e, par2 + 2, par3 + 12 + 11, 5000268);
    }

    private void func_96293_a(final int par1, final int par2, final int par3, final McoServer par4McoServer)
    {
        if (par4McoServer.field_96403_g != null)
        {
            synchronized (GuiScreenOnlineServers.func_140029_i())
            {
                if (GuiScreenOnlineServers.func_140018_j() < 5 && (!par4McoServer.field_96411_l || par4McoServer.field_102022_m))
                {
                    (new ThreadConnectToOnlineServer(this, par4McoServer)).start();
                }
            }

            if (par4McoServer.field_96414_k != null)
            {
                this.field_96294_a.drawString(GuiScreenOnlineServers.func_110402_q(this.field_96294_a), par4McoServer.field_96414_k, par2 + 215 - GuiScreenOnlineServers.func_140010_p(this.field_96294_a).getStringWidth(par4McoServer.field_96414_k), par3 + 1, 8421504);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiScreenOnlineServers.func_142023_q(this.field_96294_a).getTextureManager().bindTexture(Gui.icons);
        }
    }
}
