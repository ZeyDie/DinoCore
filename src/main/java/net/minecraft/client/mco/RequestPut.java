package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.OutputStream;

@SideOnly(Side.CLIENT)
public class RequestPut extends Request
{
    private byte[] field_96369_c;

    public RequestPut(final String par1Str, final byte[] par2ArrayOfByte, final int par3, final int par4)
    {
        super(par1Str, par3, par4);
        this.field_96369_c = par2ArrayOfByte;
    }

    public RequestPut func_96368_f()
    {
        try
        {
            this.field_96367_a.setDoOutput(true);
            this.field_96367_a.setDoInput(true);
            this.field_96367_a.setRequestMethod("PUT");
            final OutputStream outputstream = this.field_96367_a.getOutputStream();
            outputstream.write(this.field_96369_c);
            outputstream.flush();
            return this;
        }
        catch (final Exception exception)
        {
            throw new ExceptionMcoHttp("Failed URL: " + this.field_96365_b, exception);
        }
    }

    public Request func_96359_e()
    {
        return this.func_96368_f();
    }
}
