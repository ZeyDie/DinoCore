package org.bukkit.craftbukkit.v1_6_R3.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftEntityEquipment;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.*;

public class CraftLivingEntity extends CraftEntity implements LivingEntity {
    private CraftEntityEquipment equipment;
    // Cauldron start
    public Class<? extends net.minecraft.entity.EntityLivingBase> entityClass;
    public String entityName;
    // Cauldron end

    public CraftLivingEntity(final CraftServer server, final net.minecraft.entity.EntityLivingBase entity) {
        super(server, entity);
        // Cauldron start
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
        // Cauldron end

        if (entity instanceof net.minecraft.entity.EntityLiving) {
            equipment = new CraftEntityEquipment(this);
        }
    }

    public double getHealth() {
        return Math.min(Math.max(0, getHandle().getHealth()), getMaxHealth());
    }

    public void setHealth(final double health) {
        if ((health < 0) || (health > getMaxHealth())) {
            throw new IllegalArgumentException("Health must be between 0 and " + getMaxHealth());
        }

        // Cauldron start - setHealth must be set before onDeath to respect events that may prevent death.
        getHandle().setHealth((float) health);

        if (entity instanceof net.minecraft.entity.player.EntityPlayerMP && health == 0) {
            ((net.minecraft.entity.player.EntityPlayerMP) entity).onDeath(net.minecraft.util.DamageSource.generic);
        }
        // Cauldron end
    }

    public double getMaxHealth() {
        return getHandle().getMaxHealth();
    }

    public void setMaxHealth(final double amount) {
        Validate.isTrue(amount > 0, "Max health must be greater than 0");

        getHandle().getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.maxHealth).setAttribute(amount);

        if (getHealth() > amount) {
            setHealth(amount);
        }
    }

    public void resetMaxHealth() {
        setMaxHealth(getHandle().getMaxHealth());
    }

    @Deprecated
    public Egg throwEgg() {
        return launchProjectile(Egg.class);
    }

    @Deprecated
    public Snowball throwSnowball() {
        return launchProjectile(Snowball.class);
    }

    public double getEyeHeight() {
        return getHandle().getEyeHeight();
    }

    public double getEyeHeight(final boolean ignoreSneaking) {
        return getEyeHeight();
    }

    private List<Block> getLineOfSight(final HashSet<Byte> transparent, int maxDistance, final int maxLength) {
        int maxDistance1 = maxDistance;
        if (maxDistance1 > 120) {
            maxDistance1 = 120;
        }
        final ArrayList<Block> blocks = new ArrayList<Block>();
        final Iterator<Block> itr = new BlockIterator(this, maxDistance1);
        while (itr.hasNext()) {
            final Block block = itr.next();
            blocks.add(block);
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }
            final int id = block.getTypeId();
            if (transparent == null) {
                if (id != 0) {
                    break;
                }
            } else {
                if (!transparent.contains((byte) id)) {
                    break;
                }
            }
        }
        return blocks;
    }

    public List<Block> getLineOfSight(final HashSet<Byte> transparent, final int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 0);
    }

    public Block getTargetBlock(final HashSet<Byte> transparent, final int maxDistance) {
        final List<Block> blocks = getLineOfSight(transparent, maxDistance, 1);
        return blocks.get(0);
    }

    public List<Block> getLastTwoTargetBlocks(final HashSet<Byte> transparent, final int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 2);
    }

    @Deprecated
    public Arrow shootArrow() {
        return launchProjectile(Arrow.class);
    }

    public int getRemainingAir() {
        return getHandle().getAir();
    }

    public void setRemainingAir(final int ticks) {
        getHandle().setAir(ticks);
    }

    public int getMaximumAir() {
        return getHandle().maxAirTicks;
    }

    public void setMaximumAir(final int ticks) {
        getHandle().maxAirTicks = ticks;
    }

    public void damage(final double amount) {
        damage(amount, null);
    }

    public void damage(final double amount, final org.bukkit.entity.Entity source) {
        net.minecraft.util.DamageSource reason = net.minecraft.util.DamageSource.generic;

        if (source instanceof HumanEntity) {
            reason = net.minecraft.util.DamageSource.causePlayerDamage(((CraftHumanEntity) source).getHandle());
        } else if (source instanceof LivingEntity) {
            reason = net.minecraft.util.DamageSource.causeMobDamage(((CraftLivingEntity) source).getHandle());
        }

        if (entity instanceof net.minecraft.entity.boss.EntityDragon) {
            ((net.minecraft.entity.boss.EntityDragon) entity).func_82195_e(reason, (float) amount);
        } else {
            entity.attackEntityFrom(reason, (float) amount);
        }
    }

    public Location getEyeLocation() {
        final Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    public int getMaximumNoDamageTicks() {
        return getHandle().maxHurtResistantTime;
    }

    public void setMaximumNoDamageTicks(final int ticks) {
        getHandle().maxHurtResistantTime = ticks;
    }

    public double getLastDamage() {
        return getHandle().lastDamage;
    }

    public void setLastDamage(final double damage) {
        getHandle().lastDamage = (float) damage;
    }

    public int getNoDamageTicks() {
        return getHandle().hurtResistantTime;
    }

    public void setNoDamageTicks(final int ticks) {
        getHandle().hurtResistantTime = ticks;
    }

    @Override
    public net.minecraft.entity.EntityLivingBase getHandle() {
        return (net.minecraft.entity.EntityLivingBase) entity;
    }

    public void setHandle(final net.minecraft.entity.EntityLivingBase entity) {
        super.setHandle(entity);
    }

    @Override
    public String toString() {
        return this.entityName; // Cauldron
    }

    public Player getKiller() {
        return getHandle().attackingPlayer == null ? null : (Player) getHandle().attackingPlayer.getBukkitEntity();
    }

    public boolean addPotionEffect(final PotionEffect effect) {
        return addPotionEffect(effect, false);
    }

    public boolean addPotionEffect(final PotionEffect effect, final boolean force) {
        if (hasPotionEffect(effect.getType())) {
            if (!force) {
                return false;
            }
            removePotionEffect(effect.getType());
        }
        getHandle().addPotionEffect(new net.minecraft.potion.PotionEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient()));
        return true;
    }

    public boolean addPotionEffects(final Collection<PotionEffect> effects) {
        boolean success = true;
        for (final PotionEffect effect : effects) {
            success &= addPotionEffect(effect);
        }
        return success;
    }

    public boolean hasPotionEffect(final PotionEffectType type) {
        return getHandle().isPotionActive(net.minecraft.potion.Potion.potionTypes[type.getId()]);
    }

    public void removePotionEffect(final PotionEffectType type) {
        getHandle().removePotionEffect(type.getId()); // Should be removeEffect.
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        final List<PotionEffect> effects = new ArrayList<PotionEffect>();
        for (final Object raw : getHandle().activePotionsMap.values()) {
            if (!(raw instanceof net.minecraft.potion.PotionEffect))
                continue;
            final net.minecraft.potion.PotionEffect handle = (net.minecraft.potion.PotionEffect) raw;
            if (PotionEffectType.getById(handle.getPotionID()) == null) continue; // Cauldron - ignore null types
            effects.add(new PotionEffect(PotionEffectType.getById(handle.getPotionID()), handle.getDuration(), handle.getAmplifier(), handle.getIsAmbient()));
        }
        return effects;
    }

    @SuppressWarnings("unchecked")
    public <T extends Projectile> T launchProjectile(final Class<? extends T> projectile) {
        final net.minecraft.world.World world = ((CraftWorld) getWorld()).getHandle();
        net.minecraft.entity.Entity launch = null;

        if (Snowball.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntitySnowball(world, getHandle());
        } else if (Egg.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntityEgg(world, getHandle());
        } else if (EnderPearl.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.item.EntityEnderPearl(world, getHandle());
        } else if (Arrow.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntityArrow(world, getHandle(), 1);
        } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
            launch = new net.minecraft.entity.projectile.EntityPotion(world, getHandle(), CraftItemStack.asNMSCopy(new ItemStack(Material.POTION, 1)));
        } else if (Fireball.class.isAssignableFrom(projectile)) {
            final Location location = getEyeLocation();
            final Vector direction = location.getDirection().multiply(10);

            if (SmallFireball.class.isAssignableFrom(projectile)) {
                launch = new net.minecraft.entity.projectile.EntitySmallFireball(world, getHandle(), direction.getX(), direction.getY(), direction.getZ());
            } else if (WitherSkull.class.isAssignableFrom(projectile)) {
                launch = new net.minecraft.entity.projectile.EntityWitherSkull(world, getHandle(), direction.getX(), direction.getY(), direction.getZ());
            } else {
                launch = new net.minecraft.entity.projectile.EntityLargeFireball(world, getHandle(), direction.getX(), direction.getY(), direction.getZ());
            }

            launch.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }

        Validate.notNull(launch, "Projectile not supported");

        world.spawnEntityInWorld(launch);
        return (T) launch.getBukkitEntity();
    }

    public EntityType getType() {
        // Cauldron start
        final EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
        // Cauldron end
    }

    public boolean hasLineOfSight(final Entity other) {
        return getHandle().canEntityBeSeen(((CraftEntity) other).getHandle());
    }

    public boolean getRemoveWhenFarAway() {
        return getHandle() instanceof net.minecraft.entity.EntityLiving && !((net.minecraft.entity.EntityLiving) getHandle()).persistenceRequired;
    }

    public void setRemoveWhenFarAway(final boolean remove) {
        if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).persistenceRequired = !remove;
        }
    }

    public EntityEquipment getEquipment() {
        return equipment;
    }

    public void setCanPickupItems(final boolean pickup) {
        if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).canPickUpLoot = pickup;
        }
    }

    public boolean getCanPickupItems() {
        return getHandle() instanceof net.minecraft.entity.EntityLiving && ((net.minecraft.entity.EntityLiving) getHandle()).canPickUpLoot;
    }

    @Override
    public boolean teleport(final Location location, final PlayerTeleportEvent.TeleportCause cause) {
        if (getHealth() == 0) {
            return false;
        }

        return super.teleport(location, cause);
    }

    public void setCustomName(String name) {
        String name1 = name;
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return;
        }

        if (name1 == null) {
            name1 = "";
        }

        // Names cannot be more than 64 characters due to DataWatcher limitations
        if (name1.length() > 64) {
            name1 = name1.substring(0, 64);
        }

        ((net.minecraft.entity.EntityLiving) getHandle()).setCustomNameTag(name1);
    }

    public String getCustomName() {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return null;
        }

        final String name = ((net.minecraft.entity.EntityLiving) getHandle()).getCustomNameTag();

        if (name == null || name.isEmpty()) {
            return null;
        }

        return name;
    }

    public void setCustomNameVisible(final boolean flag) {
        if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).setAlwaysRenderNameTag(flag);
        }
    }

    public boolean isCustomNameVisible() {
        return getHandle() instanceof net.minecraft.entity.EntityLiving && ((net.minecraft.entity.EntityLiving) getHandle()).getAlwaysRenderNameTag();
    }

    public boolean isLeashed() {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return false;
        }
        return ((net.minecraft.entity.EntityLiving) getHandle()).getLeashedToEntity() != null;
    }

    public Entity getLeashHolder() throws IllegalStateException {
        if (!isLeashed()) {
            throw new IllegalStateException("Entity not leashed");
        }
        return ((net.minecraft.entity.EntityLiving) getHandle()).getLeashedToEntity().getBukkitEntity();
    }

    private boolean unleash() {
        if (!isLeashed()) {
            return false;
        }
        ((net.minecraft.entity.EntityLiving) getHandle()).clearLeashed(true, false);
        return true;
    }

    public boolean setLeashHolder(final Entity holder) {
        if ((getHandle() instanceof net.minecraft.entity.boss.EntityWither) || !(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return false;
        }

        if (holder == null) {
            return unleash();
        }

        if (holder.isDead()) {
            return false;
        }

        unleash();
        ((net.minecraft.entity.EntityLiving) getHandle()).setLeashedToEntity(((CraftEntity) holder).getHandle(), true);
        return true;
    }

    @Deprecated
    public int _INVALID_getLastDamage() {
        return NumberConversions.ceil(getLastDamage());
    }

    @Deprecated
    public void _INVALID_setLastDamage(final int damage) {
        setLastDamage(damage);
    }

    @Deprecated
    public void _INVALID_damage(final int amount) {
        damage(amount);
    }

    @Deprecated
    public void _INVALID_damage(final int amount, final Entity source) {
        damage(amount, source);
    }

    @Deprecated
    public int _INVALID_getHealth() {
        return NumberConversions.ceil(getHealth());
    }

    @Deprecated
    public void _INVALID_setHealth(final int health) {
        setHealth(health);
    }

    @Deprecated
    public int _INVALID_getMaxHealth() {
        return NumberConversions.ceil(getMaxHealth());
    }

    @Deprecated
    public void _INVALID_setMaxHealth(final int health) {
        setMaxHealth(health);
    }
}
