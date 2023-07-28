package net.minecraftforge.client.event.sound;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

/**
 * Raised when the SoundManager tries to play a 'Streaming' file,
 * in vanilla it is only the Jukebox that uses this function.
 * 
 * If you return null from this function it will prevent the sound from being played,
 * you can return a different entry if you want to change the sound being played.
 * 
 */
public class PlayStreamingEvent extends SoundResultEvent
{
    public final float x;
    public final float y;
    public final float z;
    public PlayStreamingEvent(final SoundManager manager, final SoundPoolEntry source, final String name, final float x, final float y, final float z)
    { 
        super(manager, source, name, 0.0f, 0.0f);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}