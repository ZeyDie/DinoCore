package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget
{
    private EntityTameable theTameable;

    public EntityAITargetNonTamed(final EntityTameable par1EntityTameable, final Class par2Class, final int par3, final boolean par4)
    {
        super(par1EntityTameable, par2Class, par3, par4);
        this.theTameable = par1EntityTameable;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return !this.theTameable.isTamed() && super.shouldExecute();
    }
}
