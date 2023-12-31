package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent
{
    public final EntityLightningBolt lightning;

    public EntityStruckByLightningEvent(final Entity entity, final EntityLightningBolt lightning)
    {
        super(entity);
        this.lightning = lightning;
    }
}
