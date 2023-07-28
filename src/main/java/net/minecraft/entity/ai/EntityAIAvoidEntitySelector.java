package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

class EntityAIAvoidEntitySelector implements IEntitySelector
{
    final EntityAIAvoidEntity entityAvoiderAI;

    EntityAIAvoidEntitySelector(final EntityAIAvoidEntity par1EntityAIAvoidEntity)
    {
        this.entityAvoiderAI = par1EntityAIAvoidEntity;
    }

    /**
     * Return whether the specified entity is applicable to this filter.
     */
    public boolean isEntityApplicable(final Entity par1Entity)
    {
        return par1Entity.isEntityAlive() && EntityAIAvoidEntity.func_98217_a(this.entityAvoiderAI).getEntitySenses().canSee(par1Entity);
    }
}
