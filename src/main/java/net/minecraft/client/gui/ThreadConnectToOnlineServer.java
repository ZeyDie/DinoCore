package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.mco.McoServer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@SideOnly(Side.CLIENT)
class ThreadConnectToOnlineServer extends Thread
{
    final McoServer field_96597_a;

    final GuiSlotOnlineServerList field_96596_b;

    ThreadConnectToOnlineServer(final GuiSlotOnlineServerList par1GuiSlotOnlineServerList, final McoServer par2McoServer)
    {
        this.field_96596_b = par1GuiSlotOnlineServerList;
        this.field_96597_a = par2McoServer;
    }

    public void run()
    {
        boolean flag = false;
        label194:
        {
            label195:
            {
                label196:
                {
                    label197:
                    {
                        label198:
                        {
                            try
                            {
                                flag = true;

                                if (!this.field_96597_a.field_96411_l)
                                {
                                    this.field_96597_a.field_96411_l = true;
                                    this.field_96597_a.field_96412_m = -2L;
                                    this.field_96597_a.field_96414_k = "";
                                    GuiScreenOnlineServers.func_140016_k();
                                    final long i = System.nanoTime();
                                    GuiScreenOnlineServers.func_140024_a(this.field_96596_b.field_96294_a, this.field_96597_a);
                                    final long j = System.nanoTime();
                                    this.field_96597_a.field_96412_m = (j - i) / 1000000L;
                                    flag = false;
                                }
                                else if (this.field_96597_a.field_102022_m)
                                {
                                    this.field_96597_a.field_102022_m = false;
                                    GuiScreenOnlineServers.func_140024_a(this.field_96596_b.field_96294_a, this.field_96597_a);
                                    flag = false;
                                }
                                else
                                {
                                    flag = false;
                                }

                                break label194;
                            }
                            catch (final UnknownHostException unknownhostexception)
                            {
                                this.field_96597_a.field_96412_m = -1L;
                                flag = false;
                                break label195;
                            }
                            catch (final SocketTimeoutException sockettimeoutexception)
                            {
                                this.field_96597_a.field_96412_m = -1L;
                                flag = false;
                                break label196;
                            }
                            catch (final ConnectException connectexception)
                            {
                                this.field_96597_a.field_96412_m = -1L;
                                flag = false;
                                break label198;
                            }
                            catch (final IOException ioexception)
                            {
                                this.field_96597_a.field_96412_m = -1L;
                                flag = false;
                            }
                            catch (final Exception exception)
                            {
                                this.field_96597_a.field_96412_m = -1L;
                                flag = false;
                                break label197;
                            }
                            finally
                            {
                                if (flag)
                                {
                                    synchronized (GuiScreenOnlineServers.func_140029_i())
                                    {
                                        GuiScreenOnlineServers.func_140021_r();
                                    }
                                }
                            }

                            synchronized (GuiScreenOnlineServers.func_140029_i())
                            {
                                GuiScreenOnlineServers.func_140021_r();
                                return;
                            }
                        }

                        synchronized (GuiScreenOnlineServers.func_140029_i())
                        {
                            GuiScreenOnlineServers.func_140021_r();
                            return;
                        }
                    }

                    synchronized (GuiScreenOnlineServers.func_140029_i())
                    {
                        GuiScreenOnlineServers.func_140021_r();
                        return;
                    }
                }

                synchronized (GuiScreenOnlineServers.func_140029_i())
                {
                    GuiScreenOnlineServers.func_140021_r();
                    return;
                }
            }

            synchronized (GuiScreenOnlineServers.func_140029_i())
            {
                GuiScreenOnlineServers.func_140021_r();
                return;
            }
        }

        synchronized (GuiScreenOnlineServers.func_140029_i())
        {
            GuiScreenOnlineServers.func_140021_r();
        }
    }
}
