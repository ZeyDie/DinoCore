package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

public class PlaySoundEffectEvent extends SoundResultEvent
{
    public PlaySoundEffectEvent(final SoundManager manager, final SoundPoolEntry source, final String name, final float volume, final float pitch)
    { super(manager, source, name, volume, pitch); }
}
