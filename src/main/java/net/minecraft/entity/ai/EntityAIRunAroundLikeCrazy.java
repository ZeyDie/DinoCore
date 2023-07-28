package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class EntityAIRunAroundLikeCrazy extends EntityAIBase
{
    private EntityHorse horseHost;
    private double field_111178_b;
    private double field_111179_c;
    private double field_111176_d;
    private double field_111177_e;

    public EntityAIRunAroundLikeCrazy(final EntityHorse par1EntityHorse, final double par2)
    {
        this.horseHost = par1EntityHorse;
        this.field_111178_b = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.horseHost.isTame() && this.horseHost.riddenByEntity != null)
        {
            final Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.horseHost, 5, 4);

            if (vec3 == null)
            {
                return false;
            }
            else
            {
                this.field_111179_c = vec3.xCoord;
                this.field_111176_d = vec3.yCoord;
                this.field_111177_e = vec3.zCoord;
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.horseHost.getNavigator().tryMoveToXYZ(this.field_111179_c, this.field_111176_d, this.field_111177_e, this.field_111178_b);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.horseHost.getNavigator().noPath() && this.horseHost.riddenByEntity != null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.horseHost.getRNG().nextInt(50) == 0)
        {
            if (this.horseHost.riddenByEntity instanceof EntityPlayer)
            {
                final int i = this.horseHost.getTemper();
                final int j = this.horseHost.getMaxTemper();

                if (j > 0 && this.horseHost.getRNG().nextInt(j) < i)
                {
                    this.horseHost.setTamedBy((EntityPlayer)this.horseHost.riddenByEntity);
                    this.horseHost.worldObj.setEntityState(this.horseHost, (byte)7);
                    return;
                }

                this.horseHost.increaseTemper(5);
            }

            // CraftBukkit start - Handle dismounting to account for VehicleExitEvent being fired.
            if (this.horseHost.riddenByEntity != null)
            {
                this.horseHost.riddenByEntity.mountEntity((Entity)null);

                // If the entity still has a passenger, then a plugin cancelled the event.
                if (this.horseHost.riddenByEntity != null)
                {
                    return;
                }
            }

            // this.field_111180_a.riddenByEntity = null;
            // CraftBukkit end
            this.horseHost.makeHorseRearWithSound();
            this.horseHost.worldObj.setEntityState(this.horseHost, (byte)6);
        }
    }
}
