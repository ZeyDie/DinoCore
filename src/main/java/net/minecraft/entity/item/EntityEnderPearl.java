package net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

// CraftBukkit start
// CraftBukkit end

public class EntityEnderPearl extends EntityThrowable
{
    public EntityEnderPearl(final World par1World)
    {
        super(par1World);
    }

    public EntityEnderPearl(final World par1World, final EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }

    @SideOnly(Side.CLIENT)
    public EntityEnderPearl(final World par1World, final double par2, final double par4, final double par6)
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

        for (int i = 0; i < 32; ++i)
        {
            this.worldObj.spawnParticle("portal", this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
        }

        if (!this.worldObj.isRemote)
        {
            if (this.getThrower() != null && this.getThrower() instanceof EntityPlayerMP)
            {
                final EntityPlayerMP entityplayermp = (EntityPlayerMP)this.getThrower();

                if (!entityplayermp.playerNetServerHandler.connectionClosed && entityplayermp.worldObj == this.worldObj)
                {
                    final EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5);
                    // Cauldron start - invert condition; return if cancelled otherwise fall through to CB event
                    if (MinecraftForge.EVENT_BUS.post(event)){
                        this.setDead();
                        return;
                    }
                    // Cauldron end
                                    
                    // CraftBukkit start
                    final org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer player = entityplayermp.getBukkitEntity();
                    final org.bukkit.Location location = getBukkitEntity().getLocation();
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());
                    final PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                    Bukkit.getPluginManager().callEvent(teleEvent);

                    if (!teleEvent.isCancelled() && !entityplayermp.playerNetServerHandler.connectionClosed)
                    {
                        entityplayermp.playerNetServerHandler.teleport(teleEvent.getTo());
                        this.getThrower().fallDistance = 0.0F;
                        final EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(this.getBukkitEntity(), player, EntityDamageByEntityEvent.DamageCause.FALL, 5.0D);
                        Bukkit.getPluginManager().callEvent(damageEvent);

                        if (!damageEvent.isCancelled() && !entityplayermp.playerNetServerHandler.connectionClosed)
                        {
                            entityplayermp.initialInvulnerability = -1; // Remove spawning invulnerability
                            player.setLastDamageCause(damageEvent);
                            entityplayermp.attackEntityFrom(DamageSource.fall, (float) damageEvent.getDamage());
                        }
                    }
                    // CraftBukkit end
                }
            }

            this.setDead();
        }
    }
}
