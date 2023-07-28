package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public abstract class TaskLongRunning implements Runnable
{
    /** The GUI screen showing progress of this task. */
    protected GuiScreenLongRunningTask taskGUI;

    public void setGUI(final GuiScreenLongRunningTask par1GuiScreenLongRunningTask)
    {
        this.taskGUI = par1GuiScreenLongRunningTask;
    }

    /**
     * Displays the given message in place of the progress bar, and adds a "Back" button.
     */
    public void setFailedMessage(final String par1Str)
    {
        this.taskGUI.setFailedMessage(par1Str);
    }

    public void setMessage(final String par1Str)
    {
        this.taskGUI.setMessage(par1Str);
    }

    public Minecraft getMinecraft()
    {
        return this.taskGUI.func_96208_g();
    }

    public boolean wasScreenClosed()
    {
        return this.taskGUI.wasScreenClosed();
    }

    public void updateScreen() {}

    public void buttonClicked(final GuiButton par1GuiButton) {}

    public void initGUI() {}
}
