package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;

/**
 * Raised by the SoundManager.loadSoundSettings, this would be a good place for 
 * adding your custom sounds to the SoundPool.
 */
public class SoundLoadEvent extends SoundEvent
{
    public final SoundManager manager;
    public SoundLoadEvent(final SoundManager manager)
    {
        this.manager = manager;
    }
}
