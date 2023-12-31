package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityAITarget extends EntityAIBase
{
    /** The entity that this task belongs to */
    protected EntityCreature taskOwner;

    /**
     * If true, EntityAI targets must be able to be seen (cannot be blocked by walls) to be suitable targets.
     */
    protected boolean shouldCheckSight;

    /**
     * When true, only entities that can be reached with minimal effort will be targetted.
     */
    private boolean nearbyOnly;

    /**
     * When nearbyOnly is true: 0 -> No target, but OK to search; 1 -> Nearby target found; 2 -> Target too far.
     */
    private int targetSearchStatus;

    /**
     * When nearbyOnly is true, this throttles target searching to avoid excessive pathfinding.
     */
    private int targetSearchDelay;
    private int field_75298_g;

    public EntityAITarget(final EntityCreature par1EntityCreature, final boolean par2)
    {
        this(par1EntityCreature, par2, false);
    }

    public EntityAITarget(final EntityCreature par1EntityCreature, final boolean par2, final boolean par3)
    {
        this.taskOwner = par1EntityCreature;
        this.shouldCheckSight = par2;
        this.nearbyOnly = par3;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        final EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
            final double d0 = this.getTargetDistance();

            if (this.taskOwner.getDistanceSqToEntity(entitylivingbase) > d0 * d0)
            {
                return false;
            }
            else
            {
                if (this.shouldCheckSight)
                {
                    if (this.taskOwner.getEntitySenses().canSee(entitylivingbase))
                    {
                        this.field_75298_g = 0;
                    }
                    else if (++this.field_75298_g > 60)
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    protected double getTargetDistance()
    {
        final AttributeInstance attributeinstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return attributeinstance == null ? 16.0D : attributeinstance.getAttributeValue();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.field_75298_g = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.taskOwner.setAttackTarget((EntityLivingBase)null);
    }

    /**
     * A method used to see if an entity is a suitable target through a number of checks.
     */
    protected boolean isSuitableTarget(final EntityLivingBase par1EntityLivingBase, final boolean par2)
    {
        if (par1EntityLivingBase == null)
        {
            return false;
        }
        else if (par1EntityLivingBase == this.taskOwner)
        {
            return false;
        }
        else if (!par1EntityLivingBase.isEntityAlive())
        {
            return false;
        }
        else if (!this.taskOwner.canAttackClass(par1EntityLivingBase.getClass()))
        {
            return false;
        }
        else
        {
            if (this.taskOwner instanceof EntityOwnable && StringUtils.isNotEmpty(((EntityOwnable)this.taskOwner).getOwnerName()))
            {
                if (par1EntityLivingBase instanceof EntityOwnable && ((EntityOwnable)this.taskOwner).getOwnerName().equals(((EntityOwnable)par1EntityLivingBase).getOwnerName()))
                {
                    return false;
                }

                if (par1EntityLivingBase == ((EntityOwnable)this.taskOwner).getOwner())
                {
                    return false;
                }
            }
            else if (par1EntityLivingBase instanceof EntityPlayer && !par2 && ((EntityPlayer)par1EntityLivingBase).capabilities.disableDamage)
            {
                return false;
            }

            if (!this.taskOwner.func_110176_b(MathHelper.floor_double(par1EntityLivingBase.posX), MathHelper.floor_double(par1EntityLivingBase.posY), MathHelper.floor_double(par1EntityLivingBase.posZ)))
            {
                return false;
            }
            else if (this.shouldCheckSight && !this.taskOwner.getEntitySenses().canSee(par1EntityLivingBase))
            {
                return false;
            }
            else
            {
                if (this.nearbyOnly)
                {
                    if (--this.targetSearchDelay <= 0)
                    {
                        this.targetSearchStatus = 0;
                    }

                    if (this.targetSearchStatus == 0)
                    {
                        this.targetSearchStatus = this.canEasilyReach(par1EntityLivingBase) ? 1 : 2;
                    }

                    if (this.targetSearchStatus == 2)
                    {
                        return false;
                    }
                }

                // CraftBukkit start - Check all the different target goals for the reason, default to RANDOM_TARGET
                EntityTargetEvent.TargetReason reason = EntityTargetEvent.TargetReason.RANDOM_TARGET;

                if (this instanceof EntityAIDefendVillage)
                {
                    reason = EntityTargetEvent.TargetReason.DEFEND_VILLAGE;
                }
                else if (this instanceof EntityAIHurtByTarget)
                {
                    reason = EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY;
                }
                else if (this instanceof EntityAINearestAttackableTarget)
                {
                    if (par1EntityLivingBase instanceof EntityPlayer)
                    {
                        reason = EntityTargetEvent.TargetReason.CLOSEST_PLAYER;
                    }
                }
                else if (this instanceof EntityAIOwnerHurtByTarget)
                {
                    reason = EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER;
                }
                else if (this instanceof EntityAIOwnerHurtTarget)
                {
                    reason = EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET;
                }

                final org.bukkit.event.entity.EntityTargetLivingEntityEvent event = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callEntityTargetLivingEvent(this.taskOwner, par1EntityLivingBase, reason);

                if (event.isCancelled() || event.getTarget() == null)
                {
                    this.taskOwner.setAttackTarget(null);
                    return false;
                }
                else if (par1EntityLivingBase.getBukkitEntity() != event.getTarget())
                {
                    this.taskOwner.setAttackTarget((EntityLivingBase)((CraftEntity) event.getTarget()).getHandle());
                }

                if (this.taskOwner instanceof EntityCreature)
                {
                    ((EntityCreature) this.taskOwner).entityToAttack = ((CraftEntity) event.getTarget()).getHandle();
                }
                // CraftBukkit end
                return true;
            }
        }
    }

    /**
     * Checks to see if this entity can find a short path to the given target.
     */
    private boolean canEasilyReach(final EntityLivingBase par1EntityLivingBase)
    {
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        final PathEntity pathentity = this.taskOwner.getNavigator().getPathToEntityLiving(par1EntityLivingBase);

        if (pathentity == null)
        {
            return false;
        }
        else
        {
            final PathPoint pathpoint = pathentity.getFinalPathPoint();

            if (pathpoint == null)
            {
                return false;
            }
            else
            {
                final int i = pathpoint.xCoord - MathHelper.floor_double(par1EntityLivingBase.posX);
                final int j = pathpoint.zCoord - MathHelper.floor_double(par1EntityLivingBase.posZ);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
