package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

// CraftBukkit start
// CraftBukkit end

public class EntityEgg extends EntityThrowable
{
    public EntityEgg(final World par1World)
    {
        super(par1World);
    }

    public EntityEgg(final World par1World, final EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }

    public EntityEgg(final World par1World, final double par2, final double par4, final double par6)
    {
        super(par1World, par2, par4, par6);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(final MovingObjectPosition par1MovingObjectPosition)
    {
        if (par1MovingObjectPosition.entityHit != null)
        {
            par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        // CraftBukkit start
        boolean hatching = !this.worldObj.isRemote && this.rand.nextInt(8) == 0;
        int numHatching = (this.rand.nextInt(32) == 0) ? 4 : 1;

        if (!hatching)
        {
            numHatching = 0;
        }

        EntityType hatchingType = EntityType.CHICKEN;
        final Entity shooter = this.getThrower();

        if (shooter instanceof EntityPlayerMP)
        {
            final Player player = (shooter == null) ? null : (Player) shooter.getBukkitEntity();
            final PlayerEggThrowEvent event = new PlayerEggThrowEvent(player, (org.bukkit.entity.Egg) this.getBukkitEntity(), hatching, (byte) numHatching, hatchingType);
            this.worldObj.getServer().getPluginManager().callEvent(event);
            hatching = event.isHatching();
            numHatching = event.getNumHatches();
            hatchingType = event.getHatchingType();
        }

        if (hatching)
        {
            for (int k = 0; k < numHatching; k++)
            {
                final org.bukkit.entity.Entity entity = worldObj.getWorld().spawn(new org.bukkit.Location(worldObj.getWorld(), this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F), hatchingType.getEntityClass(), org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.EGG);

                if (entity instanceof Ageable)
                {
                    ((Ageable) entity).setBaby();
                }
            }
        }

        // CraftBukkit end

        for (int j = 0; j < 8; ++j)
        {
            this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.worldObj.isRemote)
        {
            this.setDead();
        }
    }
}
