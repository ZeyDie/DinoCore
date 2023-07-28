package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIWatchClosest2 extends EntityAIWatchClosest
{
    public EntityAIWatchClosest2(final EntityLiving par1EntityLiving, final Class par2Class, final float par3, final float par4)
    {
        super(par1EntityLiving, par2Class, par3, par4);
        this.setMutexBits(3);
    }
}
