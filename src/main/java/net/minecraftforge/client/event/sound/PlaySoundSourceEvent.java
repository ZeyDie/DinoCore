package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;

public class PlaySoundSourceEvent extends SoundEvent
{
    public final SoundManager manager;
    public final String name;
    public final float x;
    public final float y;
    public final float z;
    public PlaySoundSourceEvent(final SoundManager manager, final String name, final float x, final float y, final float z)
    {
        this.manager = manager;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}