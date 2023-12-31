package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

import java.util.Iterator;
import java.util.List;

public class EntityAIHurtByTarget extends EntityAITarget
{
    boolean entityCallsForHelp;
    private int field_142052_b;

    public EntityAIHurtByTarget(final EntityCreature par1EntityCreature, final boolean par2)
    {
        super(par1EntityCreature, false);
        this.entityCallsForHelp = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        final int i = this.taskOwner.func_142015_aE();
        return i != this.field_142052_b && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.field_142052_b = this.taskOwner.func_142015_aE();

        if (this.entityCallsForHelp)
        {
            final double d0 = this.getTargetDistance();
            final List list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand(d0, 10.0D, d0));
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final EntityCreature entitycreature = (EntityCreature)iterator.next();

                if (this.taskOwner != entitycreature && entitycreature.getAttackTarget() == null && !entitycreature.isOnSameTeam(this.taskOwner.getAITarget()))
                {
                    entitycreature.setAttackTarget(this.taskOwner.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}
