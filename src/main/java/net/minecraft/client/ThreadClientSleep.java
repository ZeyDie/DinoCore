package net.minecraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ThreadClientSleep extends Thread
{
    /** A reference to the Minecraft object. */
    final Minecraft mc;

    ThreadClientSleep(final Minecraft par1Minecraft, final String par2Str)
    {
        super(par2Str);
        this.mc = par1Minecraft;
    }

    public void run()
    {
        while (this.mc.running)
        {
            try
            {
                Thread.sleep(2147483647L);
            }
            catch (final InterruptedException interruptedexception)
            {
                ;
            }
        }
    }
}
