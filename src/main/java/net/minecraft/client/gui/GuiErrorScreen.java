package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiErrorScreen extends GuiScreen
{
    /**
     * Unused class. Would contain a message drawn to the center of the screen.
     */
    private String message1;

    /**
     * Unused class. Would contain a message drawn to the center of the screen.
     */
    private String message2;

    public GuiErrorScreen(final String par1Str, final String par2Str)
    {
        this.message1 = par1Str;
        this.message2 = par2Str;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.getString("gui.cancel")));
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.fontRenderer, this.message1, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRenderer, this.message2, this.width / 2, 110, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(final char par1, final int par2) {}

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(final GuiButton par1GuiButton)
    {
        this.mc.displayGuiScreen((GuiScreen)null);
    }
}
