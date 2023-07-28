package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingAttackEvent extends LivingEvent
{
    public final DamageSource source;
    public final float ammount;
    public LivingAttackEvent(final EntityLivingBase entity, final DamageSource source, final float ammount)
    {
        super(entity);
        this.source = source;
        this.ammount = ammount;
    }
}
