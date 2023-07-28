package org.bukkit.craftbukkit.v1_6_R3.event;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.FakePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_6_R3.util.CraftDamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.BookMeta;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Cauldron start
// Cauldron end


public class CraftEventFactory {
    public static final net.minecraft.util.DamageSource MELTING = CraftDamageSource.copyOf(net.minecraft.util.DamageSource.onFire);
    public static final net.minecraft.util.DamageSource POISON = CraftDamageSource.copyOf(net.minecraft.util.DamageSource.magic);

    // helper methods
    private static boolean canBuild(final CraftWorld world, final Player player, final int x, final int z) {
        final net.minecraft.world.WorldServer worldServer = world.getHandle();
        final int spawnSize = Bukkit.getServer().getSpawnRadius();

        if (world.getHandle().provider.dimensionId != 0) return true;
        if (spawnSize <= 0) return true;
        if (((CraftServer) Bukkit.getServer()).getHandle().getOps().isEmpty()) return true;
        if (player.isOp()) return true;

        final net.minecraft.util.ChunkCoordinates chunkcoordinates = worldServer.getSpawnPoint();

        final int distanceFromSpawn = Math.max(Math.abs(x - chunkcoordinates.posX), Math.abs(z - chunkcoordinates.posZ));
        return distanceFromSpawn > spawnSize;
    }

    public static <T extends Event> T callEvent(final T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Block place methods
     */
    // Cauldron start
    public static BlockMultiPlaceEvent callBlockMultiPlaceEvent(final net.minecraft.world.World world, final net.minecraft.entity.player.EntityPlayer who, final List<BlockState> blockStates, final int clickedX, final int clickedY, final int clickedZ) {
        final CraftWorld craftWorld = world.getWorld();
        final CraftServer craftServer = world.getServer();

        final Player player = (who == null) ? null : (Player) who.getBukkitEntity();

        final Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);

        boolean canBuild = true;
        for (int i = 0; i < blockStates.size(); i++) {
            if (!canBuild(craftWorld, player, blockStates.get(i).getX(), blockStates.get(i).getZ())) {
                canBuild = false;
                break;
            }
        }

        final BlockMultiPlaceEvent event = new BlockMultiPlaceEvent(blockStates, blockClicked, player.getItemInHand(), player, canBuild);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }
    // Cauldron end

    public static BlockPlaceEvent callBlockPlaceEvent(final net.minecraft.world.World world, final net.minecraft.entity.player.EntityPlayer who, final BlockState replacedBlockState, final int clickedX, final int clickedY, final int clickedZ) {
        final CraftWorld craftWorld = world.getWorld();
        final CraftServer craftServer = world.getServer();

        final Player player = (who == null) ? null : (Player) who.getBukkitEntity();

        final Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        final Block placedBlock = replacedBlockState.getBlock();

        final boolean canBuild = canBuild(craftWorld, player, placedBlock.getX(), placedBlock.getZ());

        final BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, replacedBlockState, blockClicked, player.getItemInHand(), player, canBuild);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Mob spawner event
     */
    public static SpawnerSpawnEvent callSpawnerSpawnEvent(final net.minecraft.entity.Entity spawnee, final int spawnerX, final int spawnerY, final int spawnerZ) {
        final org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity entity = spawnee.getBukkitEntity();
        BlockState state = entity.getWorld().getBlockAt(spawnerX, spawnerY, spawnerZ).getState();

        if (!(state instanceof CreatureSpawner)) {
            state = null;
        }

        final SpawnerSpawnEvent event = new SpawnerSpawnEvent(entity, (CreatureSpawner) state);
        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Bucket methods
     */
    public static PlayerBucketEmptyEvent callPlayerBucketEmptyEvent(final net.minecraft.entity.player.EntityPlayer who, final int clickedX, final int clickedY, final int clickedZ, final int clickedFace, final net.minecraft.item.ItemStack itemInHand) {
        return (PlayerBucketEmptyEvent) getPlayerBucketEvent(false, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, net.minecraft.item.Item.bucketEmpty);
    }

    public static PlayerBucketFillEvent callPlayerBucketFillEvent(final net.minecraft.entity.player.EntityPlayer who, final int clickedX, final int clickedY, final int clickedZ, final int clickedFace, final net.minecraft.item.ItemStack itemInHand, final net.minecraft.item.Item bucket) {
        return (PlayerBucketFillEvent) getPlayerBucketEvent(true, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, bucket);
    }

    private static PlayerEvent getPlayerBucketEvent(final boolean isFilling, final net.minecraft.entity.player.EntityPlayer who, final int clickedX, final int clickedY, final int clickedZ, final int clickedFace, final net.minecraft.item.ItemStack itemstack, final net.minecraft.item.Item item) {
        final Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        final CraftItemStack itemInHand = CraftItemStack.asNewCraftStack(item);
        final Material bucket = Material.getMaterial(itemstack.itemID);

        final CraftWorld craftWorld = (CraftWorld) player.getWorld();
        final CraftServer craftServer = (CraftServer) player.getServer();

        final Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        final BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        PlayerEvent event = null;
        if (isFilling) {
            event = new PlayerBucketFillEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketFillEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        } else {
            event = new PlayerBucketEmptyEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketEmptyEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        }

        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Player Interact event
     */
    public static PlayerInteractEvent callPlayerInteractEvent(final net.minecraft.entity.player.EntityPlayer who, final Action action, final net.minecraft.item.ItemStack itemstack) {
        if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
            throw new IllegalArgumentException();
        }
        return callPlayerInteractEvent(who, action, 0, 256, 0, 0, itemstack);
    }

    public static PlayerInteractEvent callPlayerInteractEvent(final net.minecraft.entity.player.EntityPlayer who, Action action, final int clickedX, final int clickedY, final int clickedZ, final int clickedFace, final net.minecraft.item.ItemStack itemstack) {
        Action action1 = action;
        final Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);

        final CraftWorld craftWorld = (CraftWorld) player.getWorld();
        final CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = clickedY > 255 ? null : craftWorld.getBlockAt(clickedX, clickedY, clickedZ); // Cauldron - Don't bother getting the block if it's just going to be set to null later. Avoids pointles chunkloading if ChunkProviderServer.loadChunkOnProvideRequest is enabled
        final BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        if (clickedY > 255) {
            blockClicked = null;
            switch (action1) {
            case LEFT_CLICK_BLOCK:
                action1 = Action.LEFT_CLICK_AIR;
                break;
            case RIGHT_CLICK_BLOCK:
                action1 = Action.RIGHT_CLICK_AIR;
                break;
            }
        }

        if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
            itemInHand = null;
        }

        final PlayerInteractEvent event = new PlayerInteractEvent(player, action1, itemInHand, blockClicked, blockFace);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * EntityShootBowEvent
     */
    public static EntityShootBowEvent callEntityShootBowEvent(final net.minecraft.entity.EntityLivingBase who, final net.minecraft.item.ItemStack itemstack, final net.minecraft.entity.projectile.EntityArrow entityArrow, final float force) {
        final LivingEntity shooter = (LivingEntity) who.getBukkitEntity();
        CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
        final Arrow arrow = (Arrow) entityArrow.getBukkitEntity();

        if (itemInHand != null && (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0)) {
            itemInHand = null;
        }

        final EntityShootBowEvent event = new EntityShootBowEvent(shooter, itemInHand, arrow, force);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * BlockDamageEvent
     */
    public static BlockDamageEvent callBlockDamageEvent(final net.minecraft.entity.player.EntityPlayer who, final int x, final int y, final int z, final net.minecraft.item.ItemStack itemstack, final boolean instaBreak) {
        final Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        final CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);

        final CraftWorld craftWorld = (CraftWorld) player.getWorld();
        final CraftServer craftServer = (CraftServer) player.getServer();

        final Block blockClicked = craftWorld.getBlockAt(x, y, z);

        final BlockDamageEvent event = new BlockDamageEvent(player, blockClicked, itemInHand, instaBreak);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * CreatureSpawnEvent
     */
    public static CreatureSpawnEvent callCreatureSpawnEvent(final net.minecraft.entity.EntityLivingBase entityliving, final SpawnReason spawnReason) {
        final LivingEntity entity = (LivingEntity) entityliving.getBukkitEntity();
        final CraftServer craftServer = (CraftServer) entity.getServer();

        final CreatureSpawnEvent event = new CreatureSpawnEvent(entity, spawnReason);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityTameEvent
     */
    public static EntityTameEvent callEntityTameEvent(final net.minecraft.entity.EntityLiving entity, final net.minecraft.entity.player.EntityPlayer tamer) {
        final org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        final org.bukkit.entity.AnimalTamer bukkitTamer = (tamer != null ? tamer.getBukkitEntity() : null);
        final CraftServer craftServer = (CraftServer) bukkitEntity.getServer();

        entity.persistenceRequired = true;

        final EntityTameEvent event = new EntityTameEvent((LivingEntity) bukkitEntity, bukkitTamer);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemSpawnEvent
     */
    public static ItemSpawnEvent callItemSpawnEvent(final net.minecraft.entity.item.EntityItem entityitem) {
        final org.bukkit.entity.Item entity = (org.bukkit.entity.Item) entityitem.getBukkitEntity();
        final CraftServer craftServer = (CraftServer) entity.getServer();

        final ItemSpawnEvent event = new ItemSpawnEvent(entity, entity.getLocation());

        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemDespawnEvent
     */
    public static ItemDespawnEvent callItemDespawnEvent(final net.minecraft.entity.item.EntityItem entityitem) {
        final org.bukkit.entity.Item entity = (org.bukkit.entity.Item) entityitem.getBukkitEntity();

        final ItemDespawnEvent event = new ItemDespawnEvent(entity, entity.getLocation());

        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * PotionSplashEvent
     */
    public static PotionSplashEvent callPotionSplashEvent(final net.minecraft.entity.projectile.EntityPotion potion, final Map<LivingEntity, Double> affectedEntities) {
        final ThrownPotion thrownPotion = (ThrownPotion) potion.getBukkitEntity();

        final PotionSplashEvent event = new PotionSplashEvent(thrownPotion, affectedEntities);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * BlockFadeEvent
     */
    public static BlockFadeEvent callBlockFadeEvent(final Block block, final int type) {
        final BlockState state = block.getState();
        state.setTypeId(type);

        final BlockFadeEvent event = new BlockFadeEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static void handleBlockSpreadEvent(final Block block, final Block source, final int type, final int data) {
        final BlockState state = block.getState();
        state.setTypeId(type);
        state.setRawData((byte) data);

        final BlockSpreadEvent event = new BlockSpreadEvent(block, source, state);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            state.update(true);
        }
    }

    public static EntityDeathEvent callEntityDeathEvent(final net.minecraft.entity.EntityLivingBase victim) {
        return callEntityDeathEvent(victim, new ArrayList<org.bukkit.inventory.ItemStack>(0));
    }

    public static EntityDeathEvent callEntityDeathEvent(final net.minecraft.entity.EntityLivingBase victim, final List<org.bukkit.inventory.ItemStack> drops) {
        final CraftLivingEntity entity = (CraftLivingEntity) victim.getBukkitEntity();
        final EntityDeathEvent event = new EntityDeathEvent(entity, drops, victim.getExpReward());
        //org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        victim.expToDrop = event.getDroppedExp();
        // Cauldron start - handle any drop changes from plugins
        victim.capturedDrops.clear();
        for (final org.bukkit.inventory.ItemStack stack : event.getDrops())
        {
            final net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(victim.worldObj, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), CraftItemStack.asNMSCopy(stack));
            if (entityitem != null)
            {
                victim.capturedDrops.add((EntityItem)entityitem);
            }
        }
        // Cauldron end

        return event;
    }

    public static PlayerDeathEvent callPlayerDeathEvent(final net.minecraft.entity.player.EntityPlayerMP victim, final List<org.bukkit.inventory.ItemStack> drops, final String deathMessage) {
        final CraftPlayer entity = victim.getBukkitEntity();
        final PlayerDeathEvent event = new PlayerDeathEvent(entity, drops, victim.getExpReward(), 0, deathMessage);
        //org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        victim.keepLevel = event.getKeepLevel();
        victim.newLevel = event.getNewLevel();
        victim.newTotalExp = event.getNewTotalExp();
        victim.expToDrop = event.getDroppedExp();
        victim.newExp = event.getNewExp();
        victim.capturedDrops.clear(); // Cauldron - we must clear pre-capture to avoid duplicates

        for (final org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            // Cauldron start - add support for Forge's PlayerDropsEvent
            //world.dropItemNaturally(entity.getLocation(), stack); // handle world drop in EntityPlayerMP
            if (victim.captureDrops)
            {
                final net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(victim.worldObj, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), CraftItemStack.asNMSCopy(stack));
                if (entityitem != null)
                {
                    victim.capturedDrops.add((EntityItem)entityitem);
                }
            }
            // Cauldron end
        }

        return event;
    }

    /**
     * Server methods
     */
    public static ServerListPingEvent callServerListPingEvent(final Server craftServer, final InetAddress address, final String motd, final int numPlayers, final int maxPlayers) {
        final ServerListPingEvent event = new ServerListPingEvent(address, motd, numPlayers, maxPlayers);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityDamage(ByEntityEvent)
     */
    public static EntityDamageEvent callEntityDamageEvent(final net.minecraft.entity.Entity damager, final net.minecraft.entity.Entity damagee, final DamageCause cause, final double damage) {
        final EntityDamageEvent event;
        if (damager != null) {
            event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), damagee.getBukkitEntity(), cause, damage);
        } else {
            event = new EntityDamageEvent(damagee.getBukkitEntity(), cause, damage);
        }

        callEvent(event);

        if (!event.isCancelled()) {
            event.getEntity().setLastDamageCause(event);
        }

        return event;
    }

    public static EntityDamageEvent handleEntityDamageEvent(final net.minecraft.entity.Entity entity, final net.minecraft.util.DamageSource source, final float damage) {
        if (source instanceof net.minecraft.util.EntityDamageSource) {
            net.minecraft.entity.Entity damager = source.getEntity();
            DamageCause cause = DamageCause.ENTITY_ATTACK;

            if (source instanceof net.minecraft.util.EntityDamageSourceIndirect) {
                damager = ((net.minecraft.util.EntityDamageSourceIndirect) source).getProximateDamageSource();
                // Cauldron start - vanilla compatibility
                if (damager != null) {
                    if (damager.getBukkitEntity() instanceof ThrownPotion) {
                        cause = DamageCause.MAGIC;
                    } else if (damager.getBukkitEntity() instanceof Projectile) {
                        cause = DamageCause.PROJECTILE;
                    }
                }
                // Cauldron end
            } else if ("thorns".equals(source.damageType)) {
                cause = DamageCause.THORNS;
            }

            return callEntityDamageEvent(damager, entity, cause, damage);
        } else if (source == net.minecraft.util.DamageSource.outOfWorld) {
            final EntityDamageEvent event = callEvent(new EntityDamageByBlockEvent(null, entity.getBukkitEntity(), DamageCause.VOID, damage));
            if (!event.isCancelled()) {
                event.getEntity().setLastDamageCause(event);
            }
            return event;
        }

        DamageCause cause = null;
        if (source == net.minecraft.util.DamageSource.inFire) {
            cause = DamageCause.FIRE;
        } else if (source == net.minecraft.util.DamageSource.starve) {
            cause = DamageCause.STARVATION;
        } else if (source == net.minecraft.util.DamageSource.wither) {
            cause = DamageCause.WITHER;
        } else if (source == net.minecraft.util.DamageSource.inWall) {
            cause = DamageCause.SUFFOCATION;
        } else if (source == net.minecraft.util.DamageSource.drown) {
            cause = DamageCause.DROWNING;
        } else if (source == net.minecraft.util.DamageSource.onFire) {
            cause = DamageCause.FIRE_TICK;
        } else if (source == MELTING) {
            cause = DamageCause.MELTING;
        } else if (source == POISON) {
            cause = DamageCause.POISON;
        } else if (source == net.minecraft.util.DamageSource.magic) {
            cause = DamageCause.MAGIC;
        }

        if (cause != null) {
            return callEntityDamageEvent(null, entity, cause, damage);
        }

        // If an event was called earlier, we return null.
        // EG: Cactus, Lava, EntityEnderPearl "fall", FallingSand
        return null;
    }

    // Non-Living Entities such as EntityEnderCrystal need to call this
    public static boolean handleNonLivingEntityDamageEvent(final net.minecraft.entity.Entity entity, final net.minecraft.util.DamageSource source, final float damage) {
        if (!(source instanceof net.minecraft.util.EntityDamageSource)) {
            return false;
        }
        // We don't need to check for null, since EntityDamageSource will always return an event
        final EntityDamageEvent event = handleEntityDamageEvent(entity, source, damage);
        return event.isCancelled() || event.getDamage() == 0;
    }

    public static PlayerLevelChangeEvent callPlayerLevelChangeEvent(final Player player, final int oldLevel, final int newLevel) {
        final PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(player, oldLevel, newLevel);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerExpChangeEvent callPlayerExpChangeEvent(final net.minecraft.entity.player.EntityPlayer entity, final int expAmount) {
        final Player player = (Player) entity.getBukkitEntity();
        final PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, expAmount);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static void handleBlockGrowEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final int type, final int data) {
        final Block block = world.getWorld().getBlockAt(x, y, z);
        final CraftBlockState state = (CraftBlockState) block.getState();
        state.setTypeId(type);
        state.setRawData((byte) data);

        final BlockGrowEvent event = new BlockGrowEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            state.update(true);
        }
    }

    public static FoodLevelChangeEvent callFoodLevelChangeEvent(final net.minecraft.entity.player.EntityPlayer entity, final int level) {
        final FoodLevelChangeEvent event = new FoodLevelChangeEvent(entity.getBukkitEntity(), level);
        entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static PigZapEvent callPigZapEvent(final net.minecraft.entity.Entity pig, final net.minecraft.entity.Entity lightning, final net.minecraft.entity.Entity pigzombie) {
        final PigZapEvent event = new PigZapEvent((Pig) pig.getBukkitEntity(), (LightningStrike) lightning.getBukkitEntity(), (PigZombie) pigzombie.getBukkitEntity());
        pig.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static HorseJumpEvent callHorseJumpEvent(final net.minecraft.entity.Entity horse, final float power) {
        final HorseJumpEvent event = new HorseJumpEvent((Horse) horse.getBukkitEntity(), power);
        horse.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(final org.bukkit.entity.Entity entity, final Block block, final Material material) {
        return callEntityChangeBlockEvent(entity, block, material, 0);
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(final net.minecraft.entity.Entity entity, final Block block, final Material material) {
        return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, 0);
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(final net.minecraft.entity.Entity entity, final int x, final int y, final int z, final int type, final int data) {
        final Block block = entity.worldObj.getWorld().getBlockAt(x, y, z);
        final Material material = Material.getMaterial(type);

        return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, data);
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(final org.bukkit.entity.Entity entity, final Block block, final Material material, final int data) {
        final EntityChangeBlockEvent event = new EntityChangeBlockEvent(entity, block, material, (byte) data);
        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static CreeperPowerEvent callCreeperPowerEvent(final net.minecraft.entity.Entity creeper, final net.minecraft.entity.Entity lightning, final CreeperPowerEvent.PowerCause cause) {
        final CreeperPowerEvent event = new CreeperPowerEvent((Creeper) creeper.getBukkitEntity(), (LightningStrike) lightning.getBukkitEntity(), cause);
        creeper.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityTargetEvent callEntityTargetEvent(final net.minecraft.entity.Entity entity, final net.minecraft.entity.Entity target, final EntityTargetEvent.TargetReason reason) {
        final EntityTargetEvent event = new EntityTargetEvent(entity.getBukkitEntity(), target == null ? null : target.getBukkitEntity(), reason);
        entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityTargetLivingEntityEvent callEntityTargetLivingEvent(final net.minecraft.entity.Entity entity, final net.minecraft.entity.EntityLivingBase target, final EntityTargetEvent.TargetReason reason) {
        final EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(entity.getBukkitEntity(), (LivingEntity) target.getBukkitEntity(), reason);
        entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityBreakDoorEvent callEntityBreakDoorEvent(final net.minecraft.entity.Entity entity, final int x, final int y, final int z) {
        final org.bukkit.entity.Entity entity1 = entity.getBukkitEntity();
        final Block block = entity1.getWorld().getBlockAt(x, y, z);

        final EntityBreakDoorEvent event = new EntityBreakDoorEvent((LivingEntity) entity1, block);
        entity1.getServer().getPluginManager().callEvent(event);

        return event;
    }

    // Cauldron start - allow inventory force close to be toggled
    public static net.minecraft.inventory.Container callInventoryOpenEvent(final net.minecraft.entity.player.EntityPlayerMP player, final net.minecraft.inventory.Container container) {
        return callInventoryOpenEvent(player, container, true);
    }

    public static net.minecraft.inventory.Container callInventoryOpenEvent(final net.minecraft.entity.player.EntityPlayerMP player, final net.minecraft.inventory.Container container, final boolean closeInv) {
        if (player.openContainer != player.inventoryContainer && closeInv) { // fire INVENTORY_CLOSE if one already open
    // Cauldron end
            player.playerNetServerHandler.handleCloseWindow(new net.minecraft.network.packet.Packet101CloseWindow(player.openContainer.windowId));
        }

        final CraftServer server = player.worldObj.getServer();
        final CraftPlayer craftPlayer = player.getBukkitEntity();
        // Cauldron start - vanilla compatibility
        try {
            player.openContainer.transferTo(container, craftPlayer);
        }
        catch (final AbstractMethodError e) {
            // do nothing
        }
        // Cauldron end
        final InventoryOpenEvent event = new InventoryOpenEvent(container.getBukkitView());
        if (container.getBukkitView() != null) server.getPluginManager().callEvent(event); // Cauldron - allow vanilla mods to bypass

        if (event.isCancelled()) {
            container.transferTo(player.openContainer, craftPlayer);
            // Cauldron start - handle close for modded containers
            if (!closeInv) { // fire INVENTORY_CLOSE if one already open
                player.openContainer = container; // make sure the right container is processed
                player.closeScreen();
                player.openContainer = player.inventoryContainer;
            }
            // Cauldron end
            return null;
        }

        return container;
    }

    public static net.minecraft.item.ItemStack callPreCraftEvent(final net.minecraft.inventory.InventoryCrafting matrix, final net.minecraft.item.ItemStack result, final InventoryView lastCraftView, final boolean isRepair) {
        final CraftInventoryCrafting inventory = new CraftInventoryCrafting(matrix, matrix.resultInventory);
        inventory.setResult(CraftItemStack.asCraftMirror(result));

        final PrepareItemCraftEvent event = new PrepareItemCraftEvent(inventory, lastCraftView, isRepair);
        Bukkit.getPluginManager().callEvent(event);

        final org.bukkit.inventory.ItemStack bitem = event.getInventory().getResult();

        return CraftItemStack.asNMSCopy(bitem);
    }

    public static ProjectileLaunchEvent callProjectileLaunchEvent(final net.minecraft.entity.Entity entity) {
        final Projectile bukkitEntity = (Projectile) entity.getBukkitEntity();
        final ProjectileLaunchEvent event = new ProjectileLaunchEvent(bukkitEntity);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static ProjectileHitEvent callProjectileHitEvent(final net.minecraft.entity.Entity entity) {
        final ProjectileHitEvent event = new ProjectileHitEvent((Projectile) entity.getBukkitEntity());
        entity.worldObj.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static ExpBottleEvent callExpBottleEvent(final net.minecraft.entity.Entity entity, final int exp) {
        final ThrownExpBottle bottle = (ThrownExpBottle) entity.getBukkitEntity();
        final ExpBottleEvent event = new ExpBottleEvent(bottle, exp);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static BlockRedstoneEvent callRedstoneChange(final net.minecraft.world.World world, final int x, final int y, final int z, final int oldCurrent, final int newCurrent) {
        final BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(x, y, z), oldCurrent, newCurrent);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static NotePlayEvent callNotePlayEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final byte instrument, final byte note) {
        final NotePlayEvent event = new NotePlayEvent(world.getWorld().getBlockAt(x, y, z), org.bukkit.Instrument.getByType(instrument), new org.bukkit.Note(note));
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static void callPlayerItemBreakEvent(final net.minecraft.entity.player.EntityPlayer human, final net.minecraft.item.ItemStack brokenItem) {
        final CraftItemStack item = CraftItemStack.asCraftMirror(brokenItem);
        final PlayerItemBreakEvent event = new PlayerItemBreakEvent((Player) human.getBukkitEntity(), item);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static BlockIgniteEvent callBlockIgniteEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final int igniterX, final int igniterY, final int igniterZ) {
        final org.bukkit.World bukkitWorld = world.getWorld();
        final Block igniter = bukkitWorld.getBlockAt(igniterX, igniterY, igniterZ);
        final IgniteCause cause;
        switch (igniter.getType()) {
            case LAVA:
            case STATIONARY_LAVA:
                cause = IgniteCause.LAVA;
                break;
            case DISPENSER:
                cause = IgniteCause.FLINT_AND_STEEL;
                break;
            case FIRE: // Fire or any other unknown block counts as SPREAD.
            default:
                cause = IgniteCause.SPREAD;
        }

        final BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), cause, igniter);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static BlockIgniteEvent callBlockIgniteEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final net.minecraft.entity.Entity igniter) {
        final org.bukkit.World bukkitWorld = world.getWorld();
        final org.bukkit.entity.Entity bukkitIgniter = igniter.getBukkitEntity();
        final IgniteCause cause;
        switch (bukkitIgniter.getType()) {
        case ENDER_CRYSTAL:
            cause = IgniteCause.ENDER_CRYSTAL;
            break;
        case LIGHTNING:
            cause = IgniteCause.LIGHTNING;
            break;
        case SMALL_FIREBALL:
        case FIREBALL:
            cause = IgniteCause.FIREBALL;
            break;
        default:
            cause = IgniteCause.FLINT_AND_STEEL;
        }

        final BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), cause, bukkitIgniter);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static BlockIgniteEvent callBlockIgniteEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final net.minecraft.world.Explosion explosion) {
        final org.bukkit.World bukkitWorld = world.getWorld();
        final org.bukkit.entity.Entity igniter = explosion.exploder == null ? null : explosion.exploder.getBukkitEntity();

        final BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), IgniteCause.EXPLOSION, igniter);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static BlockIgniteEvent callBlockIgniteEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final IgniteCause cause, final net.minecraft.entity.Entity igniter) {
        final BlockIgniteEvent event = new BlockIgniteEvent(world.getWorld().getBlockAt(x, y, z), cause, igniter.getBukkitEntity());
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static void handleInventoryCloseEvent(final net.minecraft.entity.player.EntityPlayer human) {
        final InventoryCloseEvent event = new InventoryCloseEvent(human.openContainer.getBukkitView());
        if (human.openContainer.getBukkitView() != null) human.worldObj.getServer().getPluginManager().callEvent(event); // Cauldron - allow vanilla mods to bypass
        human.openContainer.transferTo(human.inventoryContainer, human.getBukkitEntity());
    }

    public static void handleEditBookEvent(final net.minecraft.entity.player.EntityPlayerMP player, final net.minecraft.item.ItemStack newBookItem) {
        final int itemInHandIndex = player.inventory.currentItem;

        final PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(player.getBukkitEntity(), player.inventory.currentItem, (BookMeta) CraftItemStack.getItemMeta(player.inventory.getCurrentItem()), (BookMeta) CraftItemStack.getItemMeta(newBookItem), newBookItem.itemID == net.minecraft.item.Item.writtenBook.itemID);
        player.worldObj.getServer().getPluginManager().callEvent(editBookEvent);
        final net.minecraft.item.ItemStack itemInHand = player.inventory.getStackInSlot(itemInHandIndex);

        // If they've got the same item in their hand, it'll need to be updated.
        if (itemInHand.itemID == net.minecraft.item.Item.writableBook.itemID) {
            if (!editBookEvent.isCancelled()) {
                CraftItemStack.setItemMeta(itemInHand, editBookEvent.getNewBookMeta());
                if (editBookEvent.isSigning()) {
                    itemInHand.itemID = net.minecraft.item.Item.writtenBook.itemID;
                }
            }

            // Client will have updated its idea of the book item; we need to overwrite that
            final net.minecraft.inventory.Slot slot = player.openContainer.getSlotFromInventory((net.minecraft.inventory.IInventory) player.inventory, itemInHandIndex);
            player.playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet103SetSlot(player.openContainer.windowId, slot.slotNumber, itemInHand));
        }
    }

    public static PlayerUnleashEntityEvent callPlayerUnleashEntityEvent(final net.minecraft.entity.EntityLiving entity, final net.minecraft.entity.player.EntityPlayer player) {
        final PlayerUnleashEntityEvent event = new PlayerUnleashEntityEvent(entity.getBukkitEntity(), (Player) player.getBukkitEntity());
        entity.worldObj.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerLeashEntityEvent callPlayerLeashEntityEvent(final net.minecraft.entity.EntityLiving entity, final net.minecraft.entity.Entity leashHolder, final net.minecraft.entity.player.EntityPlayer player) {
        final PlayerLeashEntityEvent event = new PlayerLeashEntityEvent(entity.getBukkitEntity(), leashHolder.getBukkitEntity(), (Player) player.getBukkitEntity());
        entity.worldObj.getServer().getPluginManager().callEvent(event);
        return event;
    }

    // Cauldron start
    public static BlockBreakEvent callBlockBreakEvent(final net.minecraft.world.World world, final int x, final int y, final int z, final net.minecraft.block.Block block, final int blockMetadata, final net.minecraft.entity.player.EntityPlayer player)
    {
        final org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(x, y, z);
        final org.bukkit.event.block.BlockBreakEvent blockBreakEvent = new org.bukkit.event.block.BlockBreakEvent(bukkitBlock, (CraftPlayer)player.getBukkitEntity());
        if (!(player instanceof FakePlayer) && player instanceof EntityPlayerMP)
        {
            final EntityPlayerMP playermp = (EntityPlayerMP)player;
            if (!(playermp.theItemInWorldManager.getGameType().isAdventure() && !playermp.isCurrentToolAdventureModeExempt(x, y, z)) && !(playermp.theItemInWorldManager.getGameType().isCreative() && playermp.getHeldItem() != null && playermp.getHeldItem().getItem() instanceof ItemSword))
            {
                int exp = 0;
                if (!(block == null || !playermp.canHarvestBlock(block) || // Handle empty block or player unable to break block scenario
                        block.canSilkHarvest(world, playermp, x, y, z, blockMetadata) && EnchantmentHelper.getSilkTouchModifier(playermp))) // If the block is being silk harvested, the exp dropped is 0
                {
                    final int meta = block.getDamageValue(world, x, y, z);
                    final int bonusLevel = EnchantmentHelper.getFortuneModifier(playermp);
                    exp = block.getExpDrop(world, meta, bonusLevel);
                }
                blockBreakEvent.setExpToDrop(exp);
            }
            else blockBreakEvent.setCancelled(true);
        }
        world.getServer().getPluginManager().callEvent(blockBreakEvent);
        return blockBreakEvent;
    }
    // Cauldron end
}
