package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.network.packet.Packet2ClientProtocol;

import java.net.ConnectException;
import java.net.UnknownHostException;

@SideOnly(Side.CLIENT)
class ThreadConnectToServer extends Thread
{
    /** The IP address or domain used to connect. */
    final String ip;

    /** The port used to connect. */
    final int port;

    /** A reference to the GuiConnecting object. */
    final GuiConnecting connectingGui;

    ThreadConnectToServer(final GuiConnecting par1GuiConnecting, final String par2Str, final int par3)
    {
        this.connectingGui = par1GuiConnecting;
        this.ip = par2Str;
        this.port = par3;
    }

    public void run()
    {
        try
        {
            GuiConnecting.setNetClientHandler(this.connectingGui, new NetClientHandler(GuiConnecting.func_74256_a(this.connectingGui), this.ip, this.port));

            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet2ClientProtocol(78, GuiConnecting.func_74254_c(this.connectingGui).getSession().getUsername(), this.ip, this.port));
        }
        catch (final UnknownHostException unknownhostexception)
        {
            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            GuiConnecting.func_74250_f(this.connectingGui).displayGuiScreen(new GuiDisconnected(GuiConnecting.func_98097_e(this.connectingGui), "connect.failed", "disconnect.genericReason", new Object[] {"Unknown host \'" + this.ip + "\'"}));
        }
        catch (final ConnectException connectexception)
        {
            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            GuiConnecting.func_74251_g(this.connectingGui).displayGuiScreen(new GuiDisconnected(GuiConnecting.func_98097_e(this.connectingGui), "connect.failed", "disconnect.genericReason", new Object[] {connectexception.getMessage()}));
        }
        catch (final Exception exception)
        {
            if (GuiConnecting.isCancelled(this.connectingGui))
            {
                return;
            }

            exception.printStackTrace();
            GuiConnecting.func_98096_h(this.connectingGui).displayGuiScreen(new GuiDisconnected(GuiConnecting.func_98097_e(this.connectingGui), "connect.failed", "disconnect.genericReason", new Object[] {exception.toString()}));
        }
    }
}
