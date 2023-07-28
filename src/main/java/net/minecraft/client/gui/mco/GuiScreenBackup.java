package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.mco.Backup;
import net.minecraft.client.mco.GuiScreenConfirmationType;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiScreenBackup extends GuiScreen
{
    private final GuiScreenConfigureWorld field_110380_a;
    private final long field_110377_b;
    private List field_110378_c = Collections.emptyList();
    private GuiScreenBackupSelectionList field_110375_d;
    private int field_110376_e = -1;
    private GuiButton field_110379_p;

    public GuiScreenBackup(final GuiScreenConfigureWorld par1GuiScreenConfigureWorld, final long par2)
    {
        this.field_110380_a = par1GuiScreenConfigureWorld;
        this.field_110377_b = par2;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.field_110375_d = new GuiScreenBackupSelectionList(this);
        (new GuiScreenBackupDownloadThread(this)).start();
        this.func_110369_g();
    }

    private void func_110369_g()
    {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 6, this.height - 52, 153, 20, I18n.getString("gui.back")));
        this.buttonList.add(this.field_110379_p = new GuiButton(1, this.width / 2 - 154, this.height - 52, 153, 20, I18n.getString("mco.backup.button.restore")));
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
                final String s = I18n.getString("mco.configure.world.restore.question.line1");
                final String s1 = I18n.getString("mco.configure.world.restore.question.line2");
                this.mc.displayGuiScreen(new GuiScreenConfirmation(this, GuiScreenConfirmationType.Warning, s, s1, 1));
            }
            else if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen(this.field_110380_a);
            }
            else
            {
                this.field_110375_d.actionPerformed(par1GuiButton);
            }
        }
    }

    public void confirmClicked(final boolean par1, final int par2)
    {
        if (par1 && par2 == 1)
        {
            this.func_110374_h();
        }
        else
        {
            this.mc.displayGuiScreen(this);
        }
    }

    private void func_110374_h()
    {
        if (this.field_110376_e >= 0 && this.field_110376_e < this.field_110378_c.size())
        {
            final Backup backup = (Backup)this.field_110378_c.get(this.field_110376_e);
            final GuiScreenBackupRestoreTask guiscreenbackuprestoretask = new GuiScreenBackupRestoreTask(this, backup, (GuiScreenBackupDownloadThread)null);
            final GuiScreenLongRunningTask guiscreenlongrunningtask = new GuiScreenLongRunningTask(this.mc, this.field_110380_a, guiscreenbackuprestoretask);
            guiscreenlongrunningtask.func_98117_g();
            this.mc.displayGuiScreen(guiscreenlongrunningtask);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(final int par1, final int par2, final float par3)
    {
        this.drawDefaultBackground();
        this.field_110375_d.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, I18n.getString("mco.backup.title"), this.width / 2, 20, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    static Minecraft func_110366_a(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static List func_110373_a(final GuiScreenBackup par0GuiScreenBackup, final List par1List)
    {
        return par0GuiScreenBackup.field_110378_c = par1List;
    }

    static long func_110367_b(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110377_b;
    }

    static Minecraft func_130030_c(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static GuiScreenConfigureWorld func_130031_d(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110380_a;
    }

    static Minecraft func_130035_e(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static Minecraft func_130036_f(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.mc;
    }

    static List func_110370_e(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110378_c;
    }

    static int func_130029_a(final GuiScreenBackup par0GuiScreenBackup, final int par1)
    {
        return par0GuiScreenBackup.field_110376_e = par1;
    }

    static int func_130034_h(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.field_110376_e;
    }

    static FontRenderer func_130032_i(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.fontRenderer;
    }

    static FontRenderer func_130033_j(final GuiScreenBackup par0GuiScreenBackup)
    {
        return par0GuiScreenBackup.fontRenderer;
    }
}
