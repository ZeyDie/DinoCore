package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiScreenMcoWorldTemplate extends GuiScreen
{
    private final ScreenWithCallback field_110401_a;
    private WorldTemplate field_110398_b;
    private List field_110399_c = Collections.emptyList();
    private GuiScreenMcoWorldTemplateSelectionList field_110396_d;
    private int field_110397_e = -1;
    private GuiButton field_110400_p;

    public GuiScreenMcoWorldTemplate(final ScreenWithCallback par1ScreenWithCallback, final WorldTemplate par2WorldTemplate)
    {
        this.field_110401_a = par1ScreenWithCallback;
        this.field_110398_b = par2WorldTemplate;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.field_110396_d = new GuiScreenMcoWorldTemplateSelectionList(this);
        (new GuiScreenMcoWorldTemplateDownloadThread(this)).start();
        this.func_110385_g();
    }

    private void func_110385_g()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("gui.cancel")));
        this.buttonList.add(this.field_110400_p = new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.template.button.select")));
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.func_110394_h();
            }
            else if (par1GuiButton.id == 0)
            {
                this.field_110401_a.func_110354_a((Object)null);
                this.mc.displayGuiScreen(this.field_110401_a);
            }
            else
            {
                this.field_110396_d.actionPerformed(par1GuiButton);
            }
        }
    }

    private void func_110394_h()
    {
        if (this.field_110397_e >= 0 && this.field_110397_e < this.field_110399_c.size())
        {
            this.field_110401_a.func_110354_a(this.field_110399_c.get(this.field_110397_e));
            this.mc.displayGuiScreen(this.field_110401_a);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawDefaultBackground();
        this.field_110396_d.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.template.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    static Minecraft func_110382_a(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.mc;
    }

    static List func_110388_a(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate, final List par1List)
    {
        return par0GuiScreenMcoWorldTemplate.field_110399_c = par1List;
    }

    static Minecraft func_110392_b(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.mc;
    }

    static Minecraft func_130066_c(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.mc;
    }

    static List func_110395_c(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110399_c;
    }

    static int func_130064_a(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate, final int par1)
    {
        return par0GuiScreenMcoWorldTemplate.field_110397_e = par1;
    }

    static WorldTemplate func_130065_a(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate, final WorldTemplate par1WorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110398_b = par1WorldTemplate;
    }

    static WorldTemplate func_130067_e(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110398_b;
    }

    static int func_130062_f(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.field_110397_e;
    }

    static FontRenderer func_110389_g(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }

    static FontRenderer func_110387_h(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }

    static FontRenderer func_110384_i(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }

    static FontRenderer func_130063_j(final GuiScreenMcoWorldTemplate par0GuiScreenMcoWorldTemplate)
    {
        return par0GuiScreenMcoWorldTemplate.fontRenderer;
    }
}
