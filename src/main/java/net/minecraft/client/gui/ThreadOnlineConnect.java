package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.packet.Packet2ClientProtocol;

import java.net.ConnectException;
import java.net.UnknownHostException;

@SideOnly(Side.CLIENT)
class ThreadOnlineConnect extends Thread
{
    final String field_96595_a;

    final int field_96593_b;

    final TaskOnlineConnect field_96594_c;

    ThreadOnlineConnect(final TaskOnlineConnect par1TaskOnlineConnect, final String par2Str, final int par3)
    {
        this.field_96594_c = par1TaskOnlineConnect;
        this.field_96595_a = par2Str;
        this.field_96593_b = par3;
    }

    public void run()
    {
        try
        {
            TaskOnlineConnect.func_96583_a(this.field_96594_c, new NetClientHandler(this.field_96594_c.getMinecraft(), this.field_96595_a, this.field_96593_b, TaskOnlineConnect.func_98172_a(this.field_96594_c)));

            if (this.field_96594_c.wasScreenClosed())
            {
                return;
            }

            this.field_96594_c.setMessage(I18n.getString("mco.connect.authorizing"));
            TaskOnlineConnect.func_96580_a(this.field_96594_c).addToSendQueue(new Packet2ClientProtocol(78, this.field_96594_c.getMinecraft().getSession().getUsername(), this.field_96595_a, this.field_96593_b));
        }
        catch (final UnknownHostException unknownhostexception)
        {
            if (this.field_96594_c.wasScreenClosed())
            {
                return;
            }

            this.field_96594_c.getMinecraft().displayGuiScreen(new GuiScreenDisconnectedOnline(TaskOnlineConnect.func_98172_a(this.field_96594_c), "connect.failed", "disconnect.genericReason", new Object[] {"Unknown host \'" + this.field_96595_a + "\'"}));
        }
        catch (final ConnectException connectexception)
        {
            if (this.field_96594_c.wasScreenClosed())
            {
                return;
            }

            this.field_96594_c.getMinecraft().displayGuiScreen(new GuiScreenDisconnectedOnline(TaskOnlineConnect.func_98172_a(this.field_96594_c), "connect.failed", "disconnect.genericReason", new Object[] {connectexception.getMessage()}));
        }
        catch (final Exception exception)
        {
            if (this.field_96594_c.wasScreenClosed())
            {
                return;
            }

            exception.printStackTrace();
            this.field_96594_c.getMinecraft().displayGuiScreen(new GuiScreenDisconnectedOnline(TaskOnlineConnect.func_98172_a(this.field_96594_c), "connect.failed", "disconnect.genericReason", new Object[] {exception.toString()}));
        }
    }
}
