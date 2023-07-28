package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;

public class LivingSetAttackTargetEvent extends LivingEvent
{

    public final EntityLivingBase target;
    public LivingSetAttackTargetEvent(final EntityLivingBase entity, final EntityLivingBase target)
    {
        super(entity);
        this.target = target;
    }

}
