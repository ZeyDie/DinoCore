package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.painting.PaintingBreakEvent;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityHanging extends Entity
{
    private int tickCounter1;
    public int hangingDirection;
    public int xPosition;
    public int yPosition;
    public int zPosition;

    public EntityHanging(final World par1World)
    {
        super(par1World);
        this.yOffset = 0.0F;
        this.setSize(0.5F, 0.5F);
    }

    public EntityHanging(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        this(par1World);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
    }

    protected void entityInit() {}

    public void setDirection(final int par1)
    {
        this.hangingDirection = par1;
        this.prevRotationYaw = this.rotationYaw = (float)(par1 * 90);
        float f = (float)this.getWidthPixels();
        float f1 = (float)this.getHeightPixels();
        float f2 = (float)this.getWidthPixels();

        if (par1 != 2 && par1 != 0)
        {
            f = 0.5F;
        }
        else
        {
            f2 = 0.5F;
            this.rotationYaw = this.prevRotationYaw = (float)(Direction.rotateOpposite[par1] * 90);
        }

        f /= 32.0F;
        f1 /= 32.0F;
        f2 /= 32.0F;
        float f3 = (float)this.xPosition + 0.5F;
        float f4 = (float)this.yPosition + 0.5F;
        float f5 = (float)this.zPosition + 0.5F;
        final float f6 = 0.5625F;

        if (par1 == 2)
        {
            f5 -= f6;
        }

        if (par1 == 1)
        {
            f3 -= f6;
        }

        if (par1 == 0)
        {
            f5 += f6;
        }

        if (par1 == 3)
        {
            f3 += f6;
        }

        if (par1 == 2)
        {
            f3 -= this.func_70517_b(this.getWidthPixels());
        }

        if (par1 == 1)
        {
            f5 += this.func_70517_b(this.getWidthPixels());
        }

        if (par1 == 0)
        {
            f3 += this.func_70517_b(this.getWidthPixels());
        }

        if (par1 == 3)
        {
            f5 -= this.func_70517_b(this.getWidthPixels());
        }

        f4 += this.func_70517_b(this.getHeightPixels());
        this.setPosition((double)f3, (double)f4, (double)f5);
        final float f7 = -0.03125F;
        this.boundingBox.setBounds((double)(f3 - f - f7), (double)(f4 - f1 - f7), (double)(f5 - f2 - f7), (double)(f3 + f + f7), (double)(f4 + f1 + f7), (double)(f5 + f2 + f7));
    }

    private float func_70517_b(final int par1)
    {
        return par1 == 32 ? 0.5F : (par1 == 64 ? 0.5F : 0.0F);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.tickCounter1++ == 100 && !this.worldObj.isRemote)
        {
            this.tickCounter1 = 0;

            if (!this.isDead && !this.onValidSurface())
            {
                // CraftBukkit start
                final Material material = this.worldObj.getBlockMaterial((int) this.posX, (int) this.posY, (int) this.posZ);
                final HangingBreakEvent.RemoveCause cause;

                if (!material.equals(Material.air))
                {
                    // TODO: This feels insufficient to catch 100% of suffocation cases
                    cause = HangingBreakEvent.RemoveCause.OBSTRUCTION;
                }
                else
                {
                    cause = HangingBreakEvent.RemoveCause.PHYSICS;
                }

                final HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), cause);
                this.worldObj.getServer().getPluginManager().callEvent(event);
                PaintingBreakEvent paintingEvent = null;

                if (this instanceof EntityPainting)
                {
                    // Fire old painting event until it can be removed
                    paintingEvent = new PaintingBreakEvent((Painting) this.getBukkitEntity(), PaintingBreakEvent.RemoveCause.valueOf(cause.name()));
                    paintingEvent.setCancelled(event.isCancelled());
                    this.worldObj.getServer().getPluginManager().callEvent(paintingEvent);
                }

                if (isDead || event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled()))
                {
                    return;
                }

                // CraftBukkit end
                this.setDead();
                this.onBroken((Entity)null);
            }
        }
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
        {
            return false;
        }
        else
        {
            final int i = Math.max(1, this.getWidthPixels() / 16);
            final int j = Math.max(1, this.getHeightPixels() / 16);
            int k = this.xPosition;
            int l = this.yPosition;
            int i1 = this.zPosition;

            if (this.hangingDirection == 2)
            {
                k = MathHelper.floor_double(this.posX - (double)((float)this.getWidthPixels() / 32.0F));
            }

            if (this.hangingDirection == 1)
            {
                i1 = MathHelper.floor_double(this.posZ - (double)((float)this.getWidthPixels() / 32.0F));
            }

            if (this.hangingDirection == 0)
            {
                k = MathHelper.floor_double(this.posX - (double)((float)this.getWidthPixels() / 32.0F));
            }

            if (this.hangingDirection == 3)
            {
                i1 = MathHelper.floor_double(this.posZ - (double)((float)this.getWidthPixels() / 32.0F));
            }

            l = MathHelper.floor_double(this.posY - (double)((float)this.getHeightPixels() / 32.0F));

            for (int j1 = 0; j1 < i; ++j1)
            {
                for (int k1 = 0; k1 < j; ++k1)
                {
                    final Material material;

                    if (this.hangingDirection != 2 && this.hangingDirection != 0)
                    {
                        material = this.worldObj.getBlockMaterial(this.xPosition, l + k1, i1 + j1);
                    }
                    else
                    {
                        material = this.worldObj.getBlockMaterial(k + j1, l + k1, this.zPosition);
                    }

                    if (!material.isSolid())
                    {
                        return false;
                    }
                }
            }

            final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
            final Iterator iterator = list.iterator();
            Entity entity;

            do
            {
                if (!iterator.hasNext())
                {
                    return true;
                }

                entity = (Entity)iterator.next();
            }
            while (!(entity instanceof EntityHanging));

            return false;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(final Entity par1Entity)
    {
        return par1Entity instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)par1Entity), 0.0F) : false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            if (!this.isDead && !this.worldObj.isRemote)
            {
                // CraftBukkit start
                HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.DEFAULT);
                PaintingBreakEvent paintingEvent = null;

                if (par1DamageSource.getEntity() != null)
                {
                    event = new org.bukkit.event.hanging.HangingBreakByEntityEvent((Hanging) this.getBukkitEntity(), par1DamageSource.getEntity() == null ? null : par1DamageSource.getEntity().getBukkitEntity());

                    if (this instanceof EntityPainting)
                    {
                        // Fire old painting event until it can be removed
                        paintingEvent = new org.bukkit.event.painting.PaintingBreakByEntityEvent((Painting) this.getBukkitEntity(), par1DamageSource.getEntity() == null ? null : par1DamageSource.getEntity().getBukkitEntity());
                    }
                }
                else if (par1DamageSource.isExplosion())
                {
                    event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.EXPLOSION);
                }

                this.worldObj.getServer().getPluginManager().callEvent(event);

                if (paintingEvent != null)
                {
                    paintingEvent.setCancelled(event.isCancelled());
                    this.worldObj.getServer().getPluginManager().callEvent(paintingEvent);
                }

                if (isDead || event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled()))
                {
                    return true;
                }

                // CraftBukkit end
                this.setDead();
                this.setBeenAttacked();
                this.onBroken(par1DamageSource.getEntity());
            }

            return true;
        }
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(final double par1, final double par3, final double par5)
    {
        if (!this.worldObj.isRemote && !this.isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D)
        {
            if (isDead)
            {
                return;    // CraftBukkit
            }

            // CraftBukkit start
            final HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.PHYSICS);
            this.worldObj.getServer().getPluginManager().callEvent(event);
            PaintingBreakEvent paintingEvent = null;

            if (this instanceof EntityPainting)
            {
                // Fire old painting event until it can be removed
                paintingEvent = new PaintingBreakEvent((Painting) this.getBukkitEntity(), PaintingBreakEvent.RemoveCause.valueOf(event.getCause().name()));
                paintingEvent.setCancelled(event.isCancelled());
                this.worldObj.getServer().getPluginManager().callEvent(paintingEvent);
            }

            if (event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled()))
            {
                return;
            }

            // CraftBukkit end
            this.setDead();
            this.onBroken((Entity)null);
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(final double par1, final double par3, final double par5)
    {
        if (false && !this.worldObj.isRemote && !this.isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D)   // CraftBukkit - not needed
        {
            this.setDead();
            this.onBroken((Entity)null);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Direction", (byte)this.hangingDirection);
        par1NBTTagCompound.setInteger("TileX", this.xPosition);
        par1NBTTagCompound.setInteger("TileY", this.yPosition);
        par1NBTTagCompound.setInteger("TileZ", this.zPosition);

        switch (this.hangingDirection)
        {
            case 0:
                par1NBTTagCompound.setByte("Dir", (byte)2);
                break;
            case 1:
                par1NBTTagCompound.setByte("Dir", (byte)1);
                break;
            case 2:
                par1NBTTagCompound.setByte("Dir", (byte)0);
                break;
            case 3:
                par1NBTTagCompound.setByte("Dir", (byte)3);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.hasKey("Direction"))
        {
            this.hangingDirection = par1NBTTagCompound.getByte("Direction");
        }
        else
        {
            switch (par1NBTTagCompound.getByte("Dir"))
            {
                case 0:
                    this.hangingDirection = 2;
                    break;
                case 1:
                    this.hangingDirection = 1;
                    break;
                case 2:
                    this.hangingDirection = 0;
                    break;
                case 3:
                    this.hangingDirection = 3;
            }
        }

        this.xPosition = par1NBTTagCompound.getInteger("TileX");
        this.yPosition = par1NBTTagCompound.getInteger("TileY");
        this.zPosition = par1NBTTagCompound.getInteger("TileZ");
        this.setDirection(this.hangingDirection);
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public abstract void onBroken(Entity entity);

    protected boolean shouldSetPosAfterLoading()
    {
        return false;
    }
}
