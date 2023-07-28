package net.minecraft.entity.ai;

public class EntityAITaskEntry
{
    /** The EntityAIBase object. */
    public EntityAIBase action;

    /** Priority of the EntityAIBase */
    public int priority;

    /** The EntityAITasks object of which this is an entry. */
    final EntityAITasks tasks;

    public EntityAITaskEntry(final EntityAITasks par1EntityAITasks, final int par2, final EntityAIBase par3EntityAIBase)
    {
        this.tasks = par1EntityAITasks;
        this.priority = par2;
        this.action = par3EntityAIBase;
    }
}
