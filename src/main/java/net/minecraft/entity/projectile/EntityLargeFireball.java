package net.minecraft.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EntityLargeFireball extends EntityFireball
{
    public int field_92057_e = 1;

    public EntityLargeFireball(final World par1World)
    {
        super(par1World);
    }

    @SideOnly(Side.CLIENT)
    public EntityLargeFireball(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
    }

    public EntityLargeFireball(final World par1World, final EntityLivingBase par2EntityLivingBase, final double par3, final double par5, final double par7)
    {
        super(par1World, par2EntityLivingBase, par3, par5, par7);
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(final MovingObjectPosition par1MovingObjectPosition)
    {
        if (!this.worldObj.isRemote)
        {
            if (par1MovingObjectPosition.entityHit != null)
            {
                par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F);
            }

            // CraftBukkit start
            final ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity.getEntity(this.worldObj.getServer(), this));
            this.worldObj.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
            {
                // give 'this' instead of (Entity) null so we know what causes the damage
                this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, event.getRadius(), event.getFire(), this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
            }

            // CraftBukkit end
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("ExplosionPower", this.field_92057_e);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("ExplosionPower"))
        {
            // CraftBukkit - set yield when setting explosionpower
            this.yield = this.field_92057_e = par1NBTTagCompound.getInteger("ExplosionPower");
        }
    }
}
