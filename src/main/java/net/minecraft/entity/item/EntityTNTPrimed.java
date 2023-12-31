package net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EntityTNTPrimed extends Entity
{
    /** How long the fuse is */
    public int fuse;
    private EntityLivingBase tntPlacedBy;
    public float yield = 4; // CraftBukkit
    public boolean isIncendiary = false; // CraftBukkit

    public EntityTNTPrimed(final World par1World)
    {
        super(par1World);
        this.fuse = 0;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
    }

    public EntityTNTPrimed(final World par1World, final double par2, final double par4, final double par6, final EntityLivingBase par8EntityLivingBase)
    {
        this(par1World);
        this.setPosition(par2, par4, par6);
        final float f = (float)(Math.random() * Math.PI * 2.0D);
        this.motionX = (double)(-((float)Math.sin((double)f)) * 0.02F);
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)(-((float)Math.cos((double)f)) * 0.02F);
        this.fuse = 80;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
        this.tntPlacedBy = par8EntityLivingBase;
    }

    protected void entityInit() {}

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }

        if (this.fuse-- <= 0)
        {
            // CraftBukkit start - Need to reverse the order of the explosion and the entity death so we have a location for the event
            if (!this.worldObj.isRemote)
            {
                this.explode();
            }

            this.setDead();
            // CraftBukkit end
        }
        else
        {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode()
    {
        // CraftBukkit start
        // float f = 4.0F;
        final CraftServer server = this.worldObj.getServer();
        final ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity.getEntity(server, this));
        server.getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            // give 'this' instead of (Entity) null so we know what causes the damage
            this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, event.getRadius(), event.getFire(), true);
        }

        // CraftBukkit end
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Fuse", (byte)this.fuse);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        this.fuse = par1NBTTagCompound.getByte("Fuse");
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * returns null or the entityliving it was placed or ignited by
     */
    public EntityLivingBase getTntPlacedBy()
    {
        return this.tntPlacedBy;
    }
}
