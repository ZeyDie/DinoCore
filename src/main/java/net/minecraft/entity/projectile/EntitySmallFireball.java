package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

public class EntitySmallFireball extends EntityFireball
{
    public EntitySmallFireball(final World par1World)
    {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(final World par1World, final EntityLivingBase par2EntityLivingBase, final double par3, final double par5, final double par7)
    {
        super(par1World, par2EntityLivingBase, par3, par5, par7);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.setSize(0.3125F, 0.3125F);
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
                if (!par1MovingObjectPosition.entityHit.isImmuneToFire() && par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F))
                {
                    // CraftBukkit start - Entity damage by entity event + combust event
                    final EntityCombustByEntityEvent event = new EntityCombustByEntityEvent((org.bukkit.entity.Projectile) this.getBukkitEntity(), par1MovingObjectPosition.entityHit.getBukkitEntity(), 5);
                    par1MovingObjectPosition.entityHit.worldObj.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled())
                    {
                        par1MovingObjectPosition.entityHit.setFire(event.getDuration());
                    }
                    // CraftBukkit end
                }
            }
            else
            {
                int i = par1MovingObjectPosition.blockX;
                int j = par1MovingObjectPosition.blockY;
                int k = par1MovingObjectPosition.blockZ;

                switch (par1MovingObjectPosition.sideHit)
                {
                    case 0:
                        --j;
                        break;
                    case 1:
                        ++j;
                        break;
                    case 2:
                        --k;
                        break;
                    case 3:
                        ++k;
                        break;
                    case 4:
                        --i;
                        break;
                    case 5:
                        ++i;
                }

                if (this.worldObj.isAirBlock(i, j, k))
                {
                    // CraftBukkit start
                    if (!org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callBlockIgniteEvent(worldObj, i, j, k, this).isCancelled())
                    {
                        this.worldObj.setBlock(i, j, k, Block.fire.blockID);
                    }
                    // CraftBukkit end
                }
            }

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
}
