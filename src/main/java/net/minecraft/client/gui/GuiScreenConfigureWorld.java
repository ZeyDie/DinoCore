package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.mco.GuiScreenBackup;
import net.minecraft.client.gui.mco.GuiScreenResetWorld;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.GuiScreenConfirmationType;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiScreenConfigureWorld extends GuiScreen
{
    private final GuiScreen field_96285_a;
    private McoServer field_96280_b;
    private SelectionListInvited field_96282_c;
    private int field_96277_d;
    private int field_96286_n;
    private int field_96287_o;
    private int field_96284_p = -1;
    private String field_96283_q;
    private GuiButton field_96281_r;
    private GuiButton field_96279_s;
    private GuiButton field_96278_t;
    private GuiButton field_96276_u;
    private GuiButton field_98128_v;
    private GuiButton field_98127_w;
    private GuiButton field_98129_x;
    private GuiButton field_110381_z;
    private boolean field_102020_y;

    public GuiScreenConfigureWorld(final GuiScreen par1GuiScreen, final McoServer par2McoServer)
    {
        this.field_96285_a = par1GuiScreen;
        this.field_96280_b = par2McoServer;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.field_96277_d = this.width / 2 - 200;
        this.field_96286_n = 180;
        this.field_96287_o = this.width / 2;
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (this.field_96280_b.field_96404_d.equals("CLOSED"))
        {
            this.buttonList.add(this.field_96281_r = new GuiButton(0, this.field_96277_d, this.func_96264_a(12), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.open")));
            this.field_96281_r.enabled = !this.field_96280_b.field_98166_h;
        }
        else
        {
            this.buttonList.add(this.field_96279_s = new GuiButton(1, this.field_96277_d, this.func_96264_a(12), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.close")));
            this.field_96279_s.enabled = !this.field_96280_b.field_98166_h;
        }

        this.buttonList.add(this.field_98129_x = new GuiButton(7, this.field_96277_d + this.field_96286_n / 2 + 2, this.func_96264_a(12), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.subscription")));
        this.buttonList.add(this.field_96278_t = new GuiButton(5, this.field_96277_d, this.func_96264_a(10), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.edit")));
        this.buttonList.add(this.field_96276_u = new GuiButton(6, this.field_96277_d + this.field_96286_n / 2 + 2, this.func_96264_a(10), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.reset")));
        this.buttonList.add(this.field_98128_v = new GuiButton(4, this.field_96287_o, this.func_96264_a(10), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.invite")));
        this.buttonList.add(this.field_98127_w = new GuiButton(3, this.field_96287_o + this.field_96286_n / 2 + 2, this.func_96264_a(10), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.uninvite")));
        this.buttonList.add(this.field_110381_z = new GuiButton(8, this.field_96287_o, this.func_96264_a(12), this.field_96286_n / 2 - 2, 20, I18n.getString("mco.configure.world.buttons.backup")));
        this.buttonList.add(new GuiButton(10, this.field_96287_o + this.field_96286_n / 2 + 2, this.func_96264_a(12), this.field_96286_n / 2 - 2, 20, I18n.getString("gui.back")));
        this.field_96282_c = new SelectionListInvited(this);
        this.field_96278_t.enabled = !this.field_96280_b.field_98166_h;
        this.field_96276_u.enabled = !this.field_96280_b.field_98166_h;
        this.field_98128_v.enabled = !this.field_96280_b.field_98166_h;
        this.field_98127_w.enabled = !this.field_96280_b.field_98166_h;
        this.field_110381_z.enabled = !this.field_96280_b.field_98166_h;
    }

    private int func_96264_a(final int par1)
    {
        return 40 + par1 * 13;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 10)
            {
                if (this.field_102020_y)
                {
                    ((GuiScreenOnlineServers)this.field_96285_a).func_102018_a(this.field_96280_b.field_96408_a);
                }

                this.mc.displayGuiScreen(this.field_96285_a);
            }
            else if (par1GuiButton.id == 5)
            {
                this.mc.displayGuiScreen(new GuiScreenEditOnlineWorld(this, this.field_96285_a, this.field_96280_b));
            }
            else if (par1GuiButton.id == 1)
            {
                final String s = I18n.getString("mco.configure.world.close.question.line1");
                final String s1 = I18n.getString("mco.configure.world.close.question.line2");
                this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Info, s, s1, 1));
            }
            else if (par1GuiButton.id == 0)
            {
                this.func_96268_g();
            }
            else if (par1GuiButton.id == 4)
            {
                this.mc.displayGuiScreen(new GuiScreenInvite(this.field_96285_a, this, this.field_96280_b));
            }
            else if (par1GuiButton.id == 3)
            {
                this.func_96272_i();
            }
            else if (par1GuiButton.id == 6)
            {
                this.mc.displayGuiScreen(new GuiScreenResetWorld(this, this.field_96280_b));
            }
            else if (par1GuiButton.id == 7)
            {
                this.mc.displayGuiScreen(new GuiScreenSubscription(this, this.field_96280_b));
            }
            else if (par1GuiButton.id == 8)
            {
                this.mc.displayGuiScreen(new GuiScreenBackup(this, this.field_96280_b.field_96408_a));
            }
        }
    }

    private void func_96268_g()
    {
        final McoClient mcoclient = new McoClient(this.mc.getSession());

        try
        {
            final Boolean obool = mcoclient.func_96383_b(this.field_96280_b.field_96408_a);

            if (obool.booleanValue())
            {
                this.field_102020_y = true;
                this.field_96280_b.field_96404_d = "OPEN";
                this.initGui();
            }
        }
        catch (final ExceptionMcoService exceptionmcoservice)
        {
            this.mc.getLogAgent().logSevere(exceptionmcoservice.toString());
        }
        catch (final IOException ioexception)
        {
            this.mc.getLogAgent().logWarning("Realms: could not parse response");
        }
    }

    private void func_96275_h()
    {
        final McoClient mcoclient = new McoClient(this.mc.getSession());

        try
        {
            final boolean flag = mcoclient.func_96378_c(this.field_96280_b.field_96408_a).booleanValue();

            if (flag)
            {
                this.field_102020_y = true;
                this.field_96280_b.field_96404_d = "CLOSED";
                this.initGui();
            }
        }
        catch (final ExceptionMcoService exceptionmcoservice)
        {
            this.mc.getLogAgent().logSevere(exceptionmcoservice.toString());
        }
        catch (final IOException ioexception)
        {
            this.mc.getLogAgent().logWarning("Realms: could not parse response");
        }
    }

    private void func_96272_i()
    {
        if (this.field_96284_p >= 0 && this.field_96284_p < this.field_96280_b.field_96402_f.size())
        {
            this.field_96283_q = (String)this.field_96280_b.field_96402_f.get(this.field_96284_p);
            final GuiYesNo guiyesno = new GuiYesNo(this, "Warning!", I18n.getString("mco.configure.world.uninvite.question") + " \'" + this.field_96283_q + "\'", 3);
            this.mc.displayGuiScreen(guiyesno);
        }
    }

    public void confirmClicked(final boolean par1, final int par2)
    {
        if (par2 == 3)
        {
            if (par1)
            {
                final McoClient mcoclient = new McoClient(this.mc.getSession());

                try
                {
                    mcoclient.func_96381_a(this.field_96280_b.field_96408_a, this.field_96283_q);
                }
                catch (final ExceptionMcoService exceptionmcoservice)
                {
                    this.mc.getLogAgent().logSevere(exceptionmcoservice.toString());
                }

                this.func_96267_d(this.field_96284_p);
            }

            this.mc.displayGuiScreen(new GuiScreenConfigureWorld(this.field_96285_a, this.field_96280_b));
        }

        if (par2 == 1)
        {
            if (par1)
            {
                this.func_96275_h();
            }

            this.mc.displayGuiScreen(this);
        }
    }

    private void func_96267_d(final int par1)
    {
        this.field_96280_b.field_96402_f.remove(par1);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(final char par1, final int par2) {}

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(final int par1, final int par2, final int par3)
    {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawDefaultBackground();
        this.field_96282_c.func_96612_a(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.configure.world.title"), this.width / 2, 17, 16777215);
        this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.name"), this.field_96277_d, this.func_96264_a(1), 10526880);
        this.drawString(this.fontRenderer, this.field_96280_b.func_96398_b(), this.field_96277_d, this.func_96264_a(2), 16777215);
        this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.description"), this.field_96277_d, this.func_96264_a(4), 10526880);
        this.drawString(this.fontRenderer, this.field_96280_b.func_96397_a(), this.field_96277_d, this.func_96264_a(5), 16777215);
        this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.status"), this.field_96277_d, this.func_96264_a(7), 10526880);
        this.drawString(this.fontRenderer, this.func_104045_j(), this.field_96277_d, this.func_96264_a(8), 16777215);
        this.drawString(this.fontRenderer, I18n.getString("mco.configure.world.invited"), this.field_96287_o, this.func_96264_a(1), 10526880);
        super.drawScreen(par1, par2, par3);
    }

    private String func_104045_j()
    {
        if (this.field_96280_b.field_98166_h)
        {
            return "Expired";
        }
        else
        {
            final String s = this.field_96280_b.field_96404_d.toLowerCase();
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }

    static Minecraft getMinecraft(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld)
    {
        return par0GuiScreenConfigureWorld.mc;
    }

    static int func_96271_b(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld)
    {
        return par0GuiScreenConfigureWorld.field_96287_o;
    }

    static int func_96274_a(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld, final int par1)
    {
        return par0GuiScreenConfigureWorld.func_96264_a(par1);
    }

    static int func_96269_c(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld)
    {
        return par0GuiScreenConfigureWorld.field_96286_n;
    }

    static McoServer func_96266_d(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld)
    {
        return par0GuiScreenConfigureWorld.field_96280_b;
    }

    static int func_96270_b(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld, final int par1)
    {
        return par0GuiScreenConfigureWorld.field_96284_p = par1;
    }

    static int func_96263_e(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld)
    {
        return par0GuiScreenConfigureWorld.field_96284_p;
    }

    static FontRenderer func_96273_f(final GuiScreenConfigureWorld par0GuiScreenConfigureWorld)
    {
        return par0GuiScreenConfigureWorld.fontRenderer;
    }
}
