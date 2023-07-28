package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;

public class PlaySoundEffectSourceEvent extends SoundEvent
{
    public final SoundManager manager;
    public final String name;
    public PlaySoundEffectSourceEvent(final SoundManager manager, final String name)
    {
        this.manager = manager;
        this.name = name;
    }
}
