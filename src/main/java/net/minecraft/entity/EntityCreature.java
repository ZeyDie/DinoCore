package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.TrigMath;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityUnleashEvent;

import java.util.UUID;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityCreature extends EntityLiving
{
    public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    public static final AttributeModifier field_110181_i = (new AttributeModifier(field_110179_h, "Fleeing speed bonus", 2.0D, 2)).setSaved(false);
    public PathEntity pathToEntity; // CraftBukkit - private -> public

    /** The Entity this EntityCreature is set to attack. */
    public Entity entityToAttack; // CraftBukkit - protected -> public

    /**
     * returns true if a creature has attacked recently only used for creepers and skeletons
     */
    protected boolean hasAttacked;

    /** Used to make a creature speed up and wander away when hit. */
    protected int fleeingTick;
    private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);

    /** If -1 there is no maximum distance */
    private float maximumHomeDistance = -1.0F;
    private EntityAIBase field_110178_bs = new EntityAIMoveTowardsRestriction(this, 1.0D);
    private boolean field_110180_bt;

    public EntityCreature(final World par1World)
    {
        super(par1World);
    }

    /**
     * Disables a mob's ability to move on its own while true.
     */
    protected boolean isMovementCeased()
    {
        return false;
    }

    protected void updateEntityActionState()
    {
        this.worldObj.theProfiler.startSection("ai");

        if (this.fleeingTick > 0 && --this.fleeingTick == 0)
        {
            final AttributeInstance attributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            attributeinstance.removeModifier(field_110181_i);
        }

        this.hasAttacked = this.isMovementCeased();
        final float f11 = 16.0F;

        if (this.entityToAttack == null)
        {
            // CraftBukkit start
            final Entity target = this.findPlayerToAttack();

            if (target != null)
            {
                final EntityTargetEvent event = new EntityTargetEvent(this.getBukkitEntity(), target.getBukkitEntity(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
                this.worldObj.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    if (event.getTarget() == null)
                    {
                        this.entityToAttack = null;
                    }
                    else
                    {
                        this.entityToAttack = ((CraftEntity) event.getTarget()).getHandle();
                    }
                }
            }

            // CraftBukkit end

            if (this.entityToAttack != null)
            {
                this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f11, true, false, false, true);
            }
        }
        else if (this.entityToAttack.isEntityAlive())
        {
            final float f1 = this.entityToAttack.getDistanceToEntity(this);

            if (this.canEntityBeSeen(this.entityToAttack))
            {
                this.attackEntity(this.entityToAttack, f1);
            }
        }
        else
        {
            // CraftBukkit start
            final EntityTargetEvent event = new EntityTargetEvent(this.getBukkitEntity(), null, EntityTargetEvent.TargetReason.TARGET_DIED);
            this.worldObj.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
            {
                if (event.getTarget() == null)
                {
                    this.entityToAttack = null;
                }
                else
                {
                    this.entityToAttack = ((CraftEntity) event.getTarget()).getHandle();
                }
            }
            // CraftBukkit end
        }

        this.worldObj.theProfiler.endSection();

        if (!this.hasAttacked && this.entityToAttack != null && (this.pathToEntity == null || this.rand.nextInt(20) == 0))
        {
            this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f11, true, false, false, true);
        }
        else if (!this.hasAttacked && (this.pathToEntity == null && this.rand.nextInt(180) == 0 || this.rand.nextInt(120) == 0 || this.fleeingTick > 0) && this.entityAge < 100)
        {
            this.updateWanderPath();
        }

        final int i = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
        final boolean flag = this.isInWater();
        final boolean flag1 = this.handleLavaMovement();
        this.rotationPitch = 0.0F;

        if (this.pathToEntity != null && this.rand.nextInt(100) != 0)
        {
            this.worldObj.theProfiler.startSection("followpath");
            Vec3 vec3 = this.pathToEntity.getPosition(this);
            final double d0 = (double)(this.width * 2.0F);

            while (vec3 != null && vec3.squareDistanceTo(this.posX, vec3.yCoord, this.posZ) < d0 * d0)
            {
                this.pathToEntity.incrementPathIndex();

                if (this.pathToEntity.isFinished())
                {
                    vec3 = null;
                    this.pathToEntity = null;
                }
                else
                {
                    vec3 = this.pathToEntity.getPosition(this);
                }
            }

            this.isJumping = false;

            if (vec3 != null)
            {
                final double d1 = vec3.xCoord - this.posX;
                final double d2 = vec3.zCoord - this.posZ;
                final double d3 = vec3.yCoord - (double)i;
                // CraftBukkit - Math -> TrigMath
                final float f2 = (float)(TrigMath.atan2(d2, d1) * 180.0D / Math.PI) - 90.0F;
                float f3 = MathHelper.wrapAngleTo180_float(f2 - this.rotationYaw);
                this.moveForward = (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();

                if (f3 > 30.0F)
                {
                    f3 = 30.0F;
                }

                if (f3 < -30.0F)
                {
                    f3 = -30.0F;
                }

                this.rotationYaw += f3;

                if (this.hasAttacked && this.entityToAttack != null)
                {
                    final double d4 = this.entityToAttack.posX - this.posX;
                    final double d5 = this.entityToAttack.posZ - this.posZ;
                    final float f4 = this.rotationYaw;
                    this.rotationYaw = (float)(Math.atan2(d5, d4) * 180.0D / Math.PI) - 90.0F;
                    f3 = (f4 - this.rotationYaw + 90.0F) * (float)Math.PI / 180.0F;
                    this.moveStrafing = -MathHelper.sin(f3) * this.moveForward * 1.0F;
                    this.moveForward = MathHelper.cos(f3) * this.moveForward * 1.0F;
                }

                if (d3 > 0.0D)
                {
                    this.isJumping = true;
                }
            }

            if (this.entityToAttack != null)
            {
                this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
            }

            if (this.isCollidedHorizontally && !this.hasPath())
            {
                this.isJumping = true;
            }

            if (this.rand.nextFloat() < 0.8F && (flag || flag1))
            {
                this.isJumping = true;
            }

            this.worldObj.theProfiler.endSection();
        }
        else
        {
            super.updateEntityActionState();
            this.pathToEntity = null;
        }
    }

    /**
     * Time remaining during which the Animal is sped up and flees.
     */
    protected void updateWanderPath()
    {
        this.worldObj.theProfiler.startSection("stroll");
        boolean flag = false;
        int i = -1;
        int j = -1;
        int k = -1;
        float f = -99999.0F;

        for (int l = 0; l < 10; ++l)
        {
            final int i1 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
            final int j1 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
            final int k1 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
            final float f1 = this.getBlockPathWeight(i1, j1, k1);

            if (f1 > f)
            {
                f = f1;
                i = i1;
                j = j1;
                k = k1;
                flag = true;
            }
        }

        if (flag)
        {
            this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, i, j, k, 10.0F, true, false, false, true);
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(final Entity par1Entity, final float par2) {}

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(final int par1, final int par2, final int par3)
    {
        return 0.0F;
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return null;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_double(this.boundingBox.minY);
        final int k = MathHelper.floor_double(this.posZ);
        return super.getCanSpawnHere() && this.getBlockPathWeight(i, j, k) >= 0.0F;
    }

    /**
     * Returns true if entity has a path to follow
     */
    public boolean hasPath()
    {
        return this.pathToEntity != null;
    }

    /**
     * sets the Entities walk path in EntityCreature
     */
    public void setPathToEntity(final PathEntity par1PathEntity)
    {
        this.pathToEntity = par1PathEntity;
    }

    /**
     * Returns current entities target
     */
    public Entity getEntityToAttack()
    {
        return this.entityToAttack;
    }

    /**
     * Sets the entity which is to be attacked.
     */
    public void setTarget(final Entity par1Entity)
    {
        this.entityToAttack = par1Entity;
    }

    public boolean func_110173_bK()
    {
        return this.func_110176_b(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    public boolean func_110176_b(final int par1, final int par2, final int par3)
    {
        return this.maximumHomeDistance == -1.0F ? true : this.homePosition.getDistanceSquared(par1, par2, par3) < this.maximumHomeDistance * this.maximumHomeDistance;
    }

    public void setHomeArea(final int par1, final int par2, final int par3, final int par4)
    {
        this.homePosition.set(par1, par2, par3);
        this.maximumHomeDistance = (float)par4;
    }

    /**
     * Returns the chunk coordinate object of the home position.
     */
    public ChunkCoordinates getHomePosition()
    {
        return this.homePosition;
    }

    public float func_110174_bM()
    {
        return this.maximumHomeDistance;
    }

    public void detachHome()
    {
        this.maximumHomeDistance = -1.0F;
    }

    /**
     * Returns whether a home area is defined for this entity.
     */
    public boolean hasHome()
    {
        return this.maximumHomeDistance != -1.0F;
    }

    protected void func_110159_bB()
    {
        super.func_110159_bB();

        if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj)
        {
            final Entity entity = this.getLeashedToEntity();
            this.setHomeArea((int)entity.posX, (int)entity.posY, (int)entity.posZ, 5);
            final float f = this.getDistanceToEntity(entity);

            if (this instanceof EntityTameable && ((EntityTameable)this).isSitting())
            {
                if (f > 10.0F)
                {
                    this.worldObj.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
                    this.clearLeashed(true, true);
                }

                return;
            }

            if (!this.field_110180_bt)
            {
                this.tasks.addTask(2, this.field_110178_bs);
                this.getNavigator().setAvoidsWater(false);
                this.field_110180_bt = true;
            }

            this.func_142017_o(f);

            if (f > 4.0F)
            {
                this.getNavigator().tryMoveToEntityLiving(entity, 1.0D);
            }

            if (f > 6.0F)
            {
                final double d0 = (entity.posX - this.posX) / (double)f;
                final double d1 = (entity.posY - this.posY) / (double)f;
                final double d2 = (entity.posZ - this.posZ) / (double)f;
                this.motionX += d0 * Math.abs(d0) * 0.4D;
                this.motionY += d1 * Math.abs(d1) * 0.4D;
                this.motionZ += d2 * Math.abs(d2) * 0.4D;
            }

            if (f > 10.0F)
            {
                this.worldObj.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
                this.clearLeashed(true, true);
            }
        }
        else if (!this.getLeashed() && this.field_110180_bt)
        {
            this.field_110180_bt = false;
            this.tasks.removeTask(this.field_110178_bs);
            this.getNavigator().setAvoidsWater(true);
            this.detachHome();
        }
    }

    protected void func_142017_o(final float par1) {}
}
