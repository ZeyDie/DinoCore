package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;

    public EntityAIMoveIndoors(final EntityCreature par1EntityCreature)
    {
        this.entityObj = par1EntityCreature;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining()) && !this.entityObj.worldObj.provider.hasNoSky)
        {
            if (this.entityObj.getRNG().nextInt(50) != 0)
            {
                return false;
            }
            else if (this.insidePosX != -1 && this.entityObj.getDistanceSq((double)this.insidePosX, this.entityObj.posY, (double)this.insidePosZ) < 4.0D)
            {
                return false;
            }
            else
            {
                final Village village = this.entityObj.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ), 14);

                if (village == null)
                {
                    return false;
                }
                else
                {
                    this.doorInfo = village.findNearestDoorUnrestricted(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ));
                    return this.doorInfo != null;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entityObj.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.insidePosX = -1;

        if (this.entityObj.getDistanceSq((double)this.doorInfo.getInsidePosX(), (double)this.doorInfo.posY, (double)this.doorInfo.getInsidePosZ()) > 256.0D)
        {
            final Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, this.entityObj.worldObj.getWorldVec3Pool().getVecFromPool((double)this.doorInfo.getInsidePosX() + 0.5D, (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsidePosZ() + 0.5D));

            if (vec3 != null)
            {
                this.entityObj.getNavigator().tryMoveToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord, 1.0D);
            }
        }
        else
        {
            this.entityObj.getNavigator().tryMoveToXYZ((double)this.doorInfo.getInsidePosX() + 0.5D, (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsidePosZ() + 0.5D, 1.0D);
        }
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.insidePosX = this.doorInfo.getInsidePosX();
        this.insidePosZ = this.doorInfo.getInsidePosZ();
        this.doorInfo = null;
    }
}
