package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    public final DamageSource source;
    public LivingDeathEvent(final EntityLivingBase entity, final DamageSource source)
    {
        super(entity);
        this.source = source;
    }

}
