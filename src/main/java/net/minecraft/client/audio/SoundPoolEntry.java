package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.net.URL;

@SideOnly(Side.CLIENT)
public class SoundPoolEntry
{
    private final String soundName;
    private final URL soundUrl;

    public SoundPoolEntry(final String par1Str, final URL par2URL)
    {
        this.soundName = par1Str;
        this.soundUrl = par2URL;
    }

    public String getSoundName()
    {
        return this.soundName;
    }

    public URL getSoundUrl()
    {
        return this.soundUrl;
    }
}
