package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.net.URI;

@SideOnly(Side.CLIENT)
public class GuiButtonLink extends GuiButton
{
    public GuiButtonLink(final int par1, final int par2, final int par3, final int par4, final int par5, final String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    public void func_96135_a(final String par1Str)
    {
        try
        {
            final URI uri = new URI(par1Str);
            final Class oclass = Class.forName("java.awt.Desktop");
            final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (final Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }
}
