package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@SideOnly(Side.CLIENT)
class ThreadPollServers extends Thread
{
    /** An Instnace of ServerData. */
    final ServerData pollServersServerData;

    /** Slot container for the server list */
    final GuiSlotServer serverSlotContainer;

    ThreadPollServers(final GuiSlotServer par1GuiSlotServer, final ServerData par2ServerData)
    {
        this.serverSlotContainer = par1GuiSlotServer;
        this.pollServersServerData = par2ServerData;
    }

    public void run()
    {
        boolean flag = false;
        label183:
        {
            label184:
            {
                label185:
                {
                    label186:
                    {
                        label187:
                        {
                            try
                            {
                                flag = true;
                                this.pollServersServerData.serverMOTD = EnumChatFormatting.DARK_GRAY + "Polling..";
                                final long i = System.nanoTime();
                                GuiMultiplayer.func_82291_a(this.pollServersServerData);
                                final long j = System.nanoTime();
                                this.pollServersServerData.pingToServer = (j - i) / 1000000L;
                                flag = false;
                                break label183;
                            }
                            catch (final UnknownHostException unknownhostexception)
                            {
                                this.pollServersServerData.pingToServer = -1L;
                                this.pollServersServerData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
                                flag = false;
                            }
                            catch (final SocketTimeoutException sockettimeoutexception)
                            {
                                this.pollServersServerData.pingToServer = -1L;
                                this.pollServersServerData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t reach server";
                                flag = false;
                                break label187;
                            }
                            catch (final ConnectException connectexception)
                            {
                                this.pollServersServerData.pingToServer = -1L;
                                this.pollServersServerData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t reach server";
                                flag = false;
                                break label186;
                            }
                            catch (final IOException ioexception)
                            {
                                this.pollServersServerData.pingToServer = -1L;
                                this.pollServersServerData.serverMOTD = EnumChatFormatting.DARK_RED + "Communication error";
                                flag = false;
                                break label185;
                            }
                            catch (final Exception exception)
                            {
                                this.pollServersServerData.pingToServer = -1L;
                                this.pollServersServerData.serverMOTD = "ERROR: " + exception.getClass();
                                flag = false;
                                break label184;
                            }
                            finally
                            {
                                if (flag)
                                {
                                    synchronized (GuiMultiplayer.getLock())
                                    {
                                        GuiMultiplayer.decreaseThreadsPending();
                                    }
                                }
                            }

                            synchronized (GuiMultiplayer.getLock())
                            {
                                GuiMultiplayer.decreaseThreadsPending();
                                return;
                            }
                        }

                        synchronized (GuiMultiplayer.getLock())
                        {
                            GuiMultiplayer.decreaseThreadsPending();
                            return;
                        }
                    }

                    synchronized (GuiMultiplayer.getLock())
                    {
                        GuiMultiplayer.decreaseThreadsPending();
                        return;
                    }
                }

                synchronized (GuiMultiplayer.getLock())
                {
                    GuiMultiplayer.decreaseThreadsPending();
                    return;
                }
            }

            synchronized (GuiMultiplayer.getLock())
            {
                GuiMultiplayer.decreaseThreadsPending();
                return;
            }
        }

        synchronized (GuiMultiplayer.getLock())
        {
            GuiMultiplayer.decreaseThreadsPending();
        }
    }
}
