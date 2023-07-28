package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@SideOnly(Side.CLIENT)
public class ThreadLanServerPing extends Thread
{
    private final String motd;

    /** The socket we're using to send packets on. */
    private final DatagramSocket socket;
    private boolean isStopping = true;
    private final String address;

    public ThreadLanServerPing(final String par1Str, final String par2Str) throws IOException
    {
        super("LanServerPinger");
        this.motd = par1Str;
        this.address = par2Str;
        this.setDaemon(true);
        this.socket = new DatagramSocket();
    }

    public void run()
    {
        final String s = getPingResponse(this.motd, this.address);
        final byte[] abyte = s.getBytes();

        while (!this.isInterrupted() && this.isStopping)
        {
            try
            {
                final InetAddress inetaddress = InetAddress.getByName("224.0.2.60");
                final DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length, inetaddress, 4445);
                this.socket.send(datagrampacket);
            }
            catch (final IOException ioexception)
            {
                Minecraft.getMinecraft().getLogAgent().logWarning("LanServerPinger: " + ioexception.getMessage());
                break;
            }

            try
            {
                sleep(1500L);
            }
            catch (final InterruptedException interruptedexception)
            {
                ;
            }
        }
    }

    public void interrupt()
    {
        super.interrupt();
        this.isStopping = false;
    }

    public static String getPingResponse(final String par0Str, final String par1Str)
    {
        return "[MOTD]" + par0Str + "[/MOTD][AD]" + par1Str + "[/AD]";
    }

    public static String getMotdFromPingResponse(final String par0Str)
    {
        final int i = par0Str.indexOf("[MOTD]");

        if (i < 0)
        {
            return "missing no";
        }
        else
        {
            final int j = par0Str.indexOf("[/MOTD]", i + "[MOTD]".length());
            return j < i ? "missing no" : par0Str.substring(i + "[MOTD]".length(), j);
        }
    }

    public static String getAdFromPingResponse(final String par0Str)
    {
        final int i = par0Str.indexOf("[/MOTD]");

        if (i < 0)
        {
            return null;
        }
        else
        {
            final int j = par0Str.indexOf("[/MOTD]", i + "[/MOTD]".length());

            if (j >= 0)
            {
                return null;
            }
            else
            {
                final int k = par0Str.indexOf("[AD]", i + "[/MOTD]".length());

                if (k < 0)
                {
                    return null;
                }
                else
                {
                    final int l = par0Str.indexOf("[/AD]", k + "[AD]".length());
                    return l < k ? null : par0Str.substring(k + "[AD]".length(), l);
                }
            }
        }
    }
}
