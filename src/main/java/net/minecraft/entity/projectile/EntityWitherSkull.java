package net.minecraft.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EntityWitherSkull extends EntityFireball
{
    public EntityWitherSkull(final World par1World)
    {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityWitherSkull(final World par1World, final EntityLivingBase par2EntityLivingBase, final double par3, final double par5, final double par7)
    {
        super(par1World, par2EntityLivingBase, par3, par5, par7);
        this.setSize(0.3125F, 0.3125F);
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor()
    {
        return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
    }

    @SideOnly(Side.CLIENT)
    public EntityWitherSkull(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.setSize(0.3125F, 0.3125F);
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }

    /**
     * Gets a block's resistance to this entity's explosion. Used to make rails immune to TNT minecarts' explosions and
     * Wither skulls more destructive.
     */
    public float getBlockExplosionResistance(final Explosion par1Explosion, final World par2World, final int par3, final int par4, final int par5, final Block par6Block)
    {
        float f = super.getBlockExplosionResistance(par1Explosion, par2World, par3, par4, par5, par6Block);

        if (this.isInvulnerable() && par6Block != Block.bedrock && par6Block != Block.endPortal && par6Block != Block.endPortalFrame)
        {
            f = Math.min(0.8F, f);
        }

        return f;
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
                if (this.shootingEntity != null)
                {
                    if (par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F) && !par1MovingObjectPosition.entityHit.isEntityAlive())
                    {
                        this.shootingEntity.heal(5.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.WITHER); // CraftBukkit
                    }
                }
                else
                {
                    par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.magic, 5.0F);
                }

                if (par1MovingObjectPosition.entityHit instanceof EntityLivingBase)
                {
                    byte b0 = 0;

                    if (this.worldObj.difficultySetting > 1)
                    {
                        if (this.worldObj.difficultySetting == 2)
                        {
                            b0 = 10;
                        }
                        else if (this.worldObj.difficultySetting == 3)
                        {
                            b0 = 40;
                        }
                    }

                    if (b0 > 0)
                    {
                        ((EntityLivingBase)par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * b0, 1));
                    }
                }
            }

            // CraftBukkit start
            final ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 1.0F, false);
            this.worldObj.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
            {
                this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, event.getRadius(), event.getFire(), this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
            }

            // CraftBukkit end
            this.setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2)
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(10, Byte.valueOf((byte)0));
    }

    /**
     * Return whether this skull comes from an invulnerable (aura) wither boss.
     */
    public boolean isInvulnerable()
    {
        return this.dataWatcher.getWatchableObjectByte(10) == 1;
    }

    /**
     * Set whether this skull comes from an invulnerable (aura) wither boss.
     */
    public void setInvulnerable(final boolean par1)
    {
        this.dataWatcher.updateObject(10, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }
}
