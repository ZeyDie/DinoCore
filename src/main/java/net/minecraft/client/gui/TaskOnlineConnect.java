package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.*;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class TaskOnlineConnect extends TaskLongRunning
{
    private NetClientHandler field_96586_a;
    private final McoServer field_96585_c;
    private final GuiScreen field_96584_d;

    public TaskOnlineConnect(final GuiScreen par1GuiScreen, final McoServer par2McoServer)
    {
        this.field_96584_d = par1GuiScreen;
        this.field_96585_c = par2McoServer;
    }

    public void run()
    {
        this.setMessage(I18n.getString("mco.connect.connecting"));
        final McoClient mcoclient = new McoClient(this.getMinecraft().getSession());
        boolean flag = false;
        boolean flag1 = false;
        int i = 5;
        McoServerAddress mcoserveraddress = null;

        for (int j = 0; j < 10 && !this.wasScreenClosed(); ++j)
        {
            try
            {
                mcoserveraddress = mcoclient.func_96374_a(this.field_96585_c.field_96408_a);
                flag = true;
            }
            catch (final ExceptionRetryCall exceptionretrycall)
            {
                i = exceptionretrycall.field_96393_c;
            }
            catch (final ExceptionMcoService exceptionmcoservice)
            {
                flag1 = true;
                this.setFailedMessage(exceptionmcoservice.toString());
                Minecraft.getMinecraft().getLogAgent().logSevere(exceptionmcoservice.toString());
                break;
            }
            catch (final IOException ioexception)
            {
                Minecraft.getMinecraft().getLogAgent().logWarning("Realms: could not parse response");
            }
            catch (final Exception exception)
            {
                flag1 = true;
                this.setFailedMessage(exception.getLocalizedMessage());
            }

            if (flag)
            {
                break;
            }

            this.func_111251_a(i);
        }

        if (!this.wasScreenClosed() && !flag1)
        {
            if (flag)
            {
                final ServerAddress serveraddress = ServerAddress.func_78860_a(mcoserveraddress.field_96417_a);
                this.func_96582_a(serveraddress.getIP(), serveraddress.getPort());
            }
            else
            {
                this.getMinecraft().displayGuiScreen(this.field_96584_d);
            }
        }
    }

    private void func_111251_a(final int par1)
    {
        try
        {
            Thread.sleep((long)(par1 * 1000));
        }
        catch (final InterruptedException interruptedexception)
        {
            Minecraft.getMinecraft().getLogAgent().logWarning(interruptedexception.getLocalizedMessage());
        }
    }

    private void func_96582_a(final String par1Str, final int par2)
    {
        (new ThreadOnlineConnect(this, par1Str, par2)).start();
    }

    public void updateScreen()
    {
        if (this.field_96586_a != null)
        {
            this.field_96586_a.processReadPackets();
        }
    }

    static NetClientHandler func_96583_a(final TaskOnlineConnect par0TaskOnlineConnect, final NetClientHandler par1NetClientHandler)
    {
        return par0TaskOnlineConnect.field_96586_a = par1NetClientHandler;
    }

    static GuiScreen func_98172_a(final TaskOnlineConnect par0TaskOnlineConnect)
    {
        return par0TaskOnlineConnect.field_96584_d;
    }

    static NetClientHandler func_96580_a(final TaskOnlineConnect par0TaskOnlineConnect)
    {
        return par0TaskOnlineConnect.field_96586_a;
    }
}
