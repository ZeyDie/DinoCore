package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget
{
    EntityTameable theEntityTameable;
    EntityLivingBase theTarget;
    private int field_142050_e;

    public EntityAIOwnerHurtTarget(final EntityTameable par1EntityTameable)
    {
        super(par1EntityTameable, false);
        this.theEntityTameable = par1EntityTameable;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theEntityTameable.isTamed())
        {
            return false;
        }
        else
        {
            final EntityLivingBase entitylivingbase = this.theEntityTameable.func_130012_q();

            if (entitylivingbase == null)
            {
                return false;
            }
            else
            {
                this.theTarget = entitylivingbase.getLastAttacker();
                final int i = entitylivingbase.getLastAttackerTime();
                return i != this.field_142050_e && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.func_142018_a(this.theTarget, entitylivingbase);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theTarget);
        final EntityLivingBase entitylivingbase = this.theEntityTameable.func_130012_q();

        if (entitylivingbase != null)
        {
            this.field_142050_e = entitylivingbase.getLastAttackerTime();
        }

        super.startExecuting();
    }
}
