package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@SideOnly(Side.CLIENT)
class SoundPoolURLConnection extends URLConnection
{
    private final ResourceLocation field_110659_b;

    final SoundPool theSoundPool;

    private SoundPoolURLConnection(final SoundPool par1SoundPool, final URL par2URL)
    {
        super(par2URL);
        this.theSoundPool = par1SoundPool;
        this.field_110659_b = new ResourceLocation(par2URL.getPath());
    }

    public void connect() {}

    public InputStream getInputStream()
    {
        try
        {
            return SoundPool.func_110655_a(this.theSoundPool).getResource(this.field_110659_b).getInputStream();
        }
        catch (final Exception ex)
        {
            return null;
        }
    }

    SoundPoolURLConnection(final SoundPool par1SoundPool, final URL par2URL, final SoundPoolProtocolHandler par3SoundPoolProtocolHandler)
    {
        this(par1SoundPool, par2URL);
    }
}
