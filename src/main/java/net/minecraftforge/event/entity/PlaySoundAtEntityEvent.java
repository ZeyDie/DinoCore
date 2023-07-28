package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent
{ 
    public String name;
    public final float volume;
    public final float pitch;
    
    public PlaySoundAtEntityEvent(final Entity entity, final String name, final float volume, final float pitch)
    {
        super(entity);
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }
}
