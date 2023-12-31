package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiScreenClientOutdated extends GuiScreen
{
    private final GuiScreen previousScreen;

    public GuiScreenClientOutdated(final GuiScreen par1GuiScreen)
    {
        this.previousScreen = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, "Back"));
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawDefaultBackground();
        final String s = I18n.getString("mco.client.outdated.title");
        final String s1 = I18n.getString("mco.client.outdated.msg");
        this.drawCenteredString(this.fontRenderer, s, this.width / 2, this.height / 2 - 50, 16711680);
        this.drawCenteredString(this.fontRenderer, s1, this.width / 2, this.height / 2 - 30, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == 0)
        {
            this.mc.displayGuiScreen(this.previousScreen);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(final char par1, final int par2)
    {
        if (par2 == 28 || par2 == 156)
        {
            this.mc.displayGuiScreen(this.previousScreen);
        }
    }
}
