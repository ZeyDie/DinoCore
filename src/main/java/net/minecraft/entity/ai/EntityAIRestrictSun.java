package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;

public class EntityAIRestrictSun extends EntityAIBase
{
    private EntityCreature theEntity;

    public EntityAIRestrictSun(final EntityCreature par1EntityCreature)
    {
        this.theEntity = par1EntityCreature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.theEntity.worldObj.isDaytime();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.theEntity.getNavigator().setAvoidSun(true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theEntity.getNavigator().setAvoidSun(false);
    }
}
