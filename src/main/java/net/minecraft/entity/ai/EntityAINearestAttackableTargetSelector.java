package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

class EntityAINearestAttackableTargetSelector implements IEntitySelector
{
    final IEntitySelector field_111103_c;

    final EntityAINearestAttackableTarget field_111102_d;

    EntityAINearestAttackableTargetSelector(final EntityAINearestAttackableTarget par1EntityAINearestAttackableTarget, final IEntitySelector par2IEntitySelector)
    {
        this.field_111102_d = par1EntityAINearestAttackableTarget;
        this.field_111103_c = par2IEntitySelector;
    }

    /**
     * Return whether the specified entity is applicable to this filter.
     */
    public boolean isEntityApplicable(final Entity par1Entity)
    {
        return !(par1Entity instanceof EntityLivingBase) ? false : (this.field_111103_c != null && !this.field_111103_c.isEntityApplicable(par1Entity) ? false : this.field_111102_d.isSuitableTarget((EntityLivingBase)par1Entity, false));
    }
}
