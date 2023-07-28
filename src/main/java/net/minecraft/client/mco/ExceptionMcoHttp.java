package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExceptionMcoHttp extends RuntimeException
{
    public ExceptionMcoHttp(final String par1Str, final Exception par2Exception)
    {
        super(par1Str, par2Exception);
    }
}
