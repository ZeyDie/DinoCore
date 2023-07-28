package net.minecraft.entity.player;

import com.zeydie.settings.optimization.CoreSettings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.*;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.TrigMath;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender {
    public static final String PERSISTED_NBT_TAG = "PlayerPersisted";
    /**
     * Inventory of the player
     */
    public InventoryPlayer inventory = new InventoryPlayer(this);
    private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

    /**
     * The Container for the player's inventory (which opens when they press E)
     */
    public Container inventoryContainer;

    /**
     * The Container the player has open.
     */
    public Container openContainer;

    /**
     * The player's food stats. (See class FoodStats)
     */
    protected FoodStats foodStats = new FoodStats();

    /**
     * Used to tell if the player pressed jump twice. If this is at 0 and it's pressed (And they are allowed to fly, as
     * defined in the player's movementInput) it sets this to 7. If it's pressed and it's greater than 0 enable fly.
     */
    protected int flyToggleTimer;
    public float prevCameraYaw;
    public float cameraYaw;
    public final String username;

    /**
     * Used by EntityPlayer to prevent too many xp orbs from getting absorbed at once.
     */
    public int xpCooldown;
    public double field_71091_bM;
    public double field_71096_bN;
    public double field_71097_bO;
    public double field_71094_bP;
    public double field_71095_bQ;
    public double field_71085_bR;
    // CraftBukkit start

    /**
     * Boolean value indicating weather a player is sleeping or not
     */
    public boolean sleeping; // protected -> public
    public boolean fauxSleeping;
    public String spawnWorld = "";

    @Override
    public CraftHumanEntity getBukkitEntity() {
        return (CraftHumanEntity) super.getBukkitEntity();
    }
    // CraftBukkit end

    /**
     * The chunk coordinates of the bed the player is in (null if player isn't in a bed).
     */
    public ChunkCoordinates playerLocation;
    public int sleepTimer; // CraftBukkit - private -> public
    public float field_71079_bU;
    @SideOnly(Side.CLIENT)
    public float field_71082_cx;
    public float field_71089_bV;

    /**
     * Holds the last coordinate to spawn based on last bed that the player sleep.
     */
    private ChunkCoordinates spawnChunk;
    private HashMap<Integer, ChunkCoordinates> spawnChunkMap = new HashMap<Integer, ChunkCoordinates>();

    /**
     * Whether this player's spawn point is forced, preventing execution of bed checks.
     */
    private boolean spawnForced;
    private HashMap<Integer, Boolean> spawnForcedMap = new HashMap<Integer, Boolean>();

    /**
     * Holds the coordinate of the player when enter a minecraft to ride.
     */
    private ChunkCoordinates startMinecartRidingCoordinate;

    /**
     * The player's capabilities. (See class PlayerCapabilities)
     */
    public PlayerCapabilities capabilities = new PlayerCapabilities();
    public int oldLevel = -1; // CraftBukkit

    /**
     * The current experience level the player is on.
     */
    public int experienceLevel;

    /**
     * The total amount of experience the player has. This also includes the amount of experience within their
     * Experience Bar.
     */
    public int experienceTotal;

    /**
     * The current amount of experience the player has within their Experience Bar.
     */
    public float experience;

    /**
     * This is the item that is in use when the player is holding down the useItemButton (e.g., bow, food, sword)
     */
    private ItemStack itemInUse;

    /**
     * This field starts off equal to getMaxItemUseDuration and is decremented on each tick
     */
    private int itemInUseCount;
    protected float speedOnGround = 0.1F;
    protected float speedInAir = 0.02F;
    private int field_82249_h;

    /**
     * An instance of a fishing rod's hook. If this isn't null, the icon image of the fishing rod is slightly different
     */
    public EntityFishHook fishEntity;

    public EntityPlayer(final World par1World, final String par2Str) {
        super(par1World);
        this.username = par2Str;
        this.inventoryContainer = new ContainerPlayer(this.inventory, !par1World.isRemote, this);
        this.openContainer = this.inventoryContainer;
        this.yOffset = 1.62F;
        final ChunkCoordinates chunkcoordinates = par1World.getSpawnPoint();
        this.setLocationAndAngles((double) chunkcoordinates.posX + 0.5D, (double) (chunkcoordinates.posY + 1), (double) chunkcoordinates.posZ + 0.5D, 0.0F, 0.0F);
        this.field_70741_aB = 180.0F;
        this.fireResistance = 20;
        this.eyeHeight = this.getDefaultEyeHeight();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.attackDamage).setAttribute(1.0D);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(17, Float.valueOf(0.0F));
        this.dataWatcher.addObject(18, Integer.valueOf(0));
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns the ItemStack containing the itemInUse
     */
    public ItemStack getItemInUse() {
        return this.itemInUse;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the item in use count
     */
    public int getItemInUseCount() {
        return this.itemInUseCount;
    }

    /**
     * Checks if the entity is currently using an item (e.g., bow, food, sword) by holding down the useItemButton
     */
    public boolean isUsingItem() {
        return this.itemInUse != null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * gets the duration for how long the current itemInUse has been in use
     */
    public int getItemInUseDuration() {
        return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
    }

    public void stopUsingItem() {
        if (this.itemInUse != null) {
            this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
        }

        this.clearItemInUse();
    }

    public void clearItemInUse() {
        this.itemInUse = null;
        this.itemInUseCount = 0;

        if (!this.worldObj.isRemote) {
            this.setEating(false);
        }
    }

    public boolean isBlocking() {
        return this.isUsingItem() && Item.itemsList[this.itemInUse.itemID].getItemUseAction(this.itemInUse) == EnumAction.block;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        FMLCommonHandler.instance().onPlayerPreTick(this);
        if (this.itemInUse != null) {
            final ItemStack itemstack = this.inventory.getCurrentItem();

            if (itemstack == this.itemInUse) {
                itemInUse.getItem().onUsingItemTick(itemInUse, this, itemInUseCount);
                if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
                    this.updateItemUse(itemstack, 5);
                }

                if (--this.itemInUseCount == 0 && !this.worldObj.isRemote) {
                    this.onItemUseFinish();
                }
            } else {
                this.clearItemInUse();
            }
        }

        if (this.xpCooldown > 0) {
            --this.xpCooldown;
        }

        if (this.isPlayerSleeping()) {
            ++this.sleepTimer;

            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!this.worldObj.isRemote) {
                if (!this.isInBed()) {
                    this.wakeUpPlayer(true, true, false);
                } else if (this.worldObj.isDaytime()) {
                    this.wakeUpPlayer(false, true, true);
                }
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;

            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }

        super.onUpdate();

        if (!this.worldObj.isRemote && this.openContainer != null && !ForgeHooks.canInteractWith(this, this.openContainer)) {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }

        if (this.isBurning() && this.capabilities.disableDamage) {
            this.extinguish();
        }

        this.field_71091_bM = this.field_71094_bP;
        this.field_71096_bN = this.field_71095_bQ;
        this.field_71097_bO = this.field_71085_bR;
        final double d0 = this.posX - this.field_71094_bP;
        final double d1 = this.posY - this.field_71095_bQ;
        final double d2 = this.posZ - this.field_71085_bR;

        final double d3 = 10.0D;

        if (d0 > d3) {
            this.field_71091_bM = this.field_71094_bP = this.posX;
        }

        if (d2 > d3) {
            this.field_71097_bO = this.field_71085_bR = this.posZ;
        }

        if (d1 > d3) {
            this.field_71096_bN = this.field_71095_bQ = this.posY;
        }

        if (d0 < -d3) {
            this.field_71091_bM = this.field_71094_bP = this.posX;
        }

        if (d2 < -d3) {
            this.field_71097_bO = this.field_71085_bR = this.posZ;
        }

        if (d1 < -d3) {
            this.field_71096_bN = this.field_71095_bQ = this.posY;
        }

        this.field_71094_bP += d0 * 0.25D;
        this.field_71085_bR += d2 * 0.25D;
        this.field_71095_bQ += d1 * 0.25D;
        this.addStat(StatList.minutesPlayedStat, 1);

        if (this.ridingEntity == null) {
            this.startMinecartRidingCoordinate = null;
        }

        if (!this.worldObj.isRemote) {
            this.foodStats.onUpdate(this);
        }
        FMLCommonHandler.instance().onPlayerPostTick(this);
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime() {
        return this.capabilities.disableDamage ? 0 : 80;
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown() {
        return 10;
    }

    public void playSound(final String par1Str, final float par2, final float par3) {
        this.worldObj.playSoundToNearExcept(this, par1Str, par2, par3);
    }

    /**
     * Plays sounds and makes particles for item in use state
     */
    protected void updateItemUse(final ItemStack par1ItemStack, final int par2) {
        if (par1ItemStack.getItemUseAction() == EnumAction.drink) {
            this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (par1ItemStack.getItemUseAction() == EnumAction.eat) {
            for (int j = 0; j < par2; ++j) {
                final Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                vec3.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
                vec3.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
                Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double) this.rand.nextFloat() - 0.5D) * 0.3D, (double) (-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
                vec31.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
                vec31.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
                vec31 = vec31.addVector(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ);
                this.worldObj.spawnParticle("iconcrack_" + par1ItemStack.getItem().itemID + "_" + par1ItemStack.getItemDamage(), vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
            }

            this.playSound("random.eat", 0.5F + 0.5F * (float) this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish() {
        if (this.itemInUse != null) {
            this.updateItemUse(this.itemInUse, 16);
            final int i = this.itemInUse.stackSize;
            // CraftBukkit start
            final org.bukkit.inventory.ItemStack craftItem = CraftItemStack.asBukkitCopy(this.itemInUse);
            final PlayerItemConsumeEvent event = new PlayerItemConsumeEvent((Player) this.getBukkitEntity(), craftItem);
            worldObj.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                // Update client
                if (this instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) this).playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot((byte) 0, openContainer.getSlotFromInventory((IInventory) this.inventory, this.inventory.currentItem).slotIndex, this.itemInUse));
                    // Spigot Start
                    ((EntityPlayerMP) this).getBukkitEntity().updateInventory();
                    ((EntityPlayerMP) this).getBukkitEntity().updateScaledHealth();
                    // Spigot End
                }

                return;
            }

            // Plugin modified the item, process it but don't remove it
            if (!craftItem.equals(event.getItem())) {
                CraftItemStack.asNMSCopy(event.getItem()).onFoodEaten(this.worldObj, this);

                // Update client
                if (this instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) this).playerNetServerHandler.sendPacketToPlayer(new Packet103SetSlot((byte) 0, openContainer.getSlotFromInventory((IInventory) this.inventory, this.inventory.currentItem).slotIndex, this.itemInUse));
                }

                return;
            }
            // CraftBukkit end
            final ItemStack itemstack = this.itemInUse.onFoodEaten(this.worldObj, this);

            if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i) {
                this.inventory.mainInventory[this.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0) {
                    this.inventory.mainInventory[this.inventory.currentItem] = null;
                }
            }

            this.clearItemInUse();
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1) {
        if (par1 == 9) {
            this.onItemUseFinish();
        } else {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * Dead and sleeping entities cannot move
     */
    //TODO ZoomCodeReplace protected on public
    public boolean isMovementBlocked() {
        return this.getHealth() <= 0.0F || this.isPlayerSleeping();
    }

    /**
     * sets current screen to null (used on escape buttons of GUIs)
     */
    public void closeScreen() {
        this.openContainer = this.inventoryContainer;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(final Entity par1Entity) {
        // CraftBukkit start - mirror Entity mount changes
        this.setPassengerOf(par1Entity);
    }

    public void setPassengerOf(final Entity entity) {
        // CraftBukkit end
        if (this.ridingEntity != null && entity == null) {
            this.worldObj.getServer().getPluginManager().callEvent(new org.spigotmc.event.entity.EntityDismountEvent(this.getBukkitEntity(), this.ridingEntity.getBukkitEntity()));
            // CraftBukkit start - use parent method instead to correctly fire VehicleExitEvent
            final Entity originalVehicle = this.ridingEntity;
            // First statement moved down, second statement handled in parent method.
            /*
            if (!this.worldObj.isRemote)
            {
                this.func_110145_l(this.ridingEntity);
            }

            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
            */
            super.setPassengerOf(entity);

            if (!this.worldObj.isRemote && this.ridingEntity == null) {
                this.dismountEntity(originalVehicle);
            }

            // CraftBukkit end
        } else {
            super.setPassengerOf(entity); // CraftBukkit - call new parent
        }
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden() {
        if (!this.worldObj.isRemote && this.isSneaking()) {
            this.mountEntity((Entity) null);
            this.setSneaking(false);
        } else {
            final double d0 = this.posX;
            final double d1 = this.posY;
            final double d2 = this.posZ;
            final float f = this.rotationYaw;
            final float f1 = this.rotationPitch;
            super.updateRidden();
            this.prevCameraYaw = this.cameraYaw;
            this.cameraYaw = 0.0F;
            this.addMountedMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);

            if (this.ridingEntity instanceof EntityLivingBase && ((EntityLivingBase) ridingEntity).shouldRiderFaceForward(this)) {
                this.rotationPitch = f1;
                this.rotationYaw = f;
                this.renderYawOffset = ((EntityLivingBase) this.ridingEntity).renderYawOffset;
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    public void preparePlayerToSpawn() {
        this.yOffset = 1.62F;
        this.setSize(0.6F, 1.8F);
        super.preparePlayerToSpawn();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    //TODO ZoomCodeReplace protected on public
    public void updateEntityActionState() {
        super.updateEntityActionState();
        this.updateArmSwingProgress();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        if (this.flyToggleTimer > 0) {
            --this.flyToggleTimer;
        }

        if (this.worldObj.difficultySetting == 0 && this.getHealth() < this.getMaxHealth() && this.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.ticksExisted % 20 * 12 == 0) {
            // CraftBukkit - added regain reason of "REGEN" for filtering purposes.
            this.heal(1.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.REGEN);
        }

        this.inventory.decrementAnimations();
        this.prevCameraYaw = this.cameraYaw;
        super.onLivingUpdate();
        final AttributeInstance attributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (!this.worldObj.isRemote) {
            attributeinstance.setAttribute((double) this.capabilities.getWalkSpeed());
        }

        this.jumpMovementFactor = this.speedInAir;

        if (this.isSprinting()) {
            this.jumpMovementFactor = (float) ((double) this.jumpMovementFactor + (double) this.speedInAir * 0.3D);
        }

        this.setAIMoveSpeed((float) attributeinstance.getAttributeValue());
        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f1 = (float) TrigMath.atan(-this.motionY * 0.20000000298023224D) * 15.0F;  // CraftBukkit - Math -> TrigMath

        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0.0F) {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0.0F) {
            f1 = 0.0F;
        }

        this.cameraYaw += (f - this.cameraYaw) * 0.4F;
        this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;

        if (this.getHealth() > 0.0F) {
            AxisAlignedBB axisalignedbb = null;

            if (this.ridingEntity != null && !this.ridingEntity.isDead) {
                axisalignedbb = this.boundingBox.func_111270_a(this.ridingEntity.boundingBox).expand(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.boundingBox.expand(1.0D, 0.5D, 1.0D);
            }

            final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);

            if (list != null && this.canBePushed()) // Spigot: Add this.canBePushed() condition
            {
                for (int i = 0; i < list.size(); ++i) {
                    final Entity entity = (Entity) list.get(i);

                    if (!entity.isDead) {
                        this.collideWithPlayer(entity);
                    }
                }
            }
        }
    }

    private void collideWithPlayer(final Entity par1Entity) {
        par1Entity.onCollideWithPlayer(this);
    }

    public int getScore() {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * Set player's score
     */
    public void setScore(final int par1) {
        this.dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    /**
     * Add to player's score
     */
    public void addScore(final int par1) {
        final int j = this.getScore();
        this.dataWatcher.updateObject(18, Integer.valueOf(j + par1));
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(final DamageSource par1DamageSource) {
        if (ForgeHooks.onLivingDeath(this, par1DamageSource)) return;
        super.onDeath(par1DamageSource);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.10000000149011612D;

        captureDrops = true;
        capturedDrops.clear();

        if (this.username.equals("Notch")) {
            this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
        }

        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            this.inventory.dropAllItems();
        }

        captureDrops = false;

        if (!worldObj.isRemote) {
            final PlayerDropsEvent event = new PlayerDropsEvent(this, par1DamageSource, capturedDrops, recentlyHit > 0);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                for (final EntityItem item : capturedDrops) {
                    joinEntityItemWithWorld(item);
                }
            }
        }

        if (par1DamageSource != null) {
            this.motionX = (double) (-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F);
            this.motionZ = (double) (-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F);
        } else {
            this.motionX = this.motionZ = 0.0D;
        }

        this.yOffset = 0.1F;
        this.addStat(StatList.deathsStat, 1);
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(final Entity par1Entity, final int par2) {
        this.addScore(par2);
        // CraftBukkit - Get our scores instead
        final Collection<Score> collection = this.worldObj.getServer().getScoreboardManager().getScoreboardScores(ScoreObjectiveCriteria.totalKillCount, this.getEntityName(), new java.util.ArrayList<Score>());

        if (par1Entity instanceof EntityPlayer) {
            this.addStat(StatList.playerKillsStat, 1);
            // CraftBukkit - Get our scores instead
            this.worldObj.getServer().getScoreboardManager().getScoreboardScores(ScoreObjectiveCriteria.playerKillCount, this.getEntityName(), collection);
        } else {
            this.addStat(StatList.mobKillsStat, 1);
        }

        final Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            final Score score = (Score) iterator.next(); // CraftBukkit - Use our scores instead
            score.func_96648_a();
        }
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem dropOneItem(final boolean par1) {
        final ItemStack stack = inventory.getCurrentItem();

        if (stack == null) {
            return null;
        }

        if (stack.getItem().onDroppedByPlayer(stack, this)) {
            final int count = par1 && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1;
            return ForgeHooks.onPlayerTossEvent(this, inventory.decrStackSize(inventory.currentItem, count));
        }

        return null;
    }

    /**
     * Args: itemstack - called when player drops an item stack that's not in his inventory (like items still placed in
     * a workbench while the workbench'es GUI gets closed)
     */
    public EntityItem dropPlayerItem(final ItemStack par1ItemStack) {
        return ForgeHooks.onPlayerTossEvent(this, par1ItemStack);
    }

    /**
     * Args: itemstack, flag
     */
    public EntityItem dropPlayerItemWithRandomChoice(final ItemStack par1ItemStack, final boolean par2) {
        if (par1ItemStack == null) {
            return null;
        } else if (par1ItemStack.stackSize == 0) {
            return null;
        } else {
            final EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY - 0.30000001192092896D + (double) this.getEyeHeight(), this.posZ, par1ItemStack);
            entityitem.delayBeforeCanPickup = 40;
            float f = 0.1F;
            final float f1;

            if (par2) {
                f1 = this.rand.nextFloat() * 0.5F;
                final float f2 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                entityitem.motionX = (double) (-MathHelper.sin(f2) * f1);
                entityitem.motionZ = (double) (MathHelper.cos(f2) * f1);
                entityitem.motionY = 0.20000000298023224D;
            } else {
                f = 0.3F;
                entityitem.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
                entityitem.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f);
                entityitem.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f + 0.1F);
                f = 0.02F;
                f1 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                f *= this.rand.nextFloat();
                entityitem.motionX += Math.cos((double) f1) * (double) f;
                entityitem.motionY += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                entityitem.motionZ += Math.sin((double) f1) * (double) f;
            }

            // CraftBukkit start
            final Player player = (Player) this.getBukkitEntity();
            final CraftItem drop = new CraftItem(this.worldObj.getServer(), entityitem);
            final PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
            this.worldObj.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                player.getInventory().addItem(drop.getItemStack());
                return null;
            }
            // CraftBukkit end
            this.joinEntityItemWithWorld(entityitem);
            this.addStat(StatList.dropStat, 1);
            return entityitem;
        }
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    public void joinEntityItemWithWorld(final EntityItem par1EntityItem) {
        if (captureDrops) {
            capturedDrops.add(par1EntityItem);
            return;
        }
        this.worldObj.spawnEntityInWorld(par1EntityItem);
    }

    /**
     * Returns how strong the player is against the specified block at this moment
     * Deprecated in favor of the more sensitive version
     */
    @Deprecated
    public float getCurrentPlayerStrVsBlock(final Block par1Block, final boolean par2) {
        return getCurrentPlayerStrVsBlock(par1Block, par2, 0);
    }

    public float getCurrentPlayerStrVsBlock(final Block par1Block, final boolean par2, final int meta) {
        final ItemStack stack = inventory.getCurrentItem();
        float f = (stack == null ? 1.0F : stack.getItem().getStrVsBlock(stack, par1Block, meta));

        if (f > 1.0F) {
            final int i = EnchantmentHelper.getEfficiencyModifier(this);
            final ItemStack itemstack = this.inventory.getCurrentItem();

            if (i > 0 && itemstack != null) {
                final float f1 = (float) (i * i + 1);

                final boolean canHarvest = ForgeHooks.canToolHarvestBlock(par1Block, meta, itemstack);

                if (!canHarvest && f <= 1.0F) {
                    f += f1 * 0.08F;
                } else {
                    f += f1;
                }
            }
        }

        if (this.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (this.isPotionActive(Potion.digSlowdown)) {
            f *= 1.0F - (float) (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
        }

        if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        f = ForgeEventFactory.getBreakSpeed(this, par1Block, meta, f);
        return (f < 0 ? 0 : f);
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
     */
    public boolean canHarvestBlock(final Block par1Block) {
        return ForgeEventFactory.doPlayerHarvestCheck(this, par1Block, inventory.canHarvestBlock(par1Block));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Inventory");
        this.inventory.readFromNBT(nbttaglist);
        this.inventory.currentItem = par1NBTTagCompound.getInteger("SelectedItemSlot");
        this.sleeping = par1NBTTagCompound.getBoolean("Sleeping");
        this.sleepTimer = par1NBTTagCompound.getShort("SleepTimer");
        this.experience = par1NBTTagCompound.getFloat("XpP");
        this.experienceLevel = par1NBTTagCompound.getInteger("XpLevel");
        this.experienceTotal = par1NBTTagCompound.getInteger("XpTotal");
        this.setScore(par1NBTTagCompound.getInteger("Score"));

        if (this.sleeping) {
            this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.wakeUpPlayer(true, true, false);
        }

        // CraftBukkit start
        this.spawnWorld = par1NBTTagCompound.getString("SpawnWorld");

        if ("".equals(spawnWorld)) {
            this.spawnWorld = this.worldObj.getServer().getWorlds().get(0).getName();
        }
        // CraftBukkit end
        if (par1NBTTagCompound.hasKey("SpawnX") && par1NBTTagCompound.hasKey("SpawnY") && par1NBTTagCompound.hasKey("SpawnZ")) {
            this.spawnChunk = new ChunkCoordinates(par1NBTTagCompound.getInteger("SpawnX"), par1NBTTagCompound.getInteger("SpawnY"), par1NBTTagCompound.getInteger("SpawnZ"));
            this.spawnForced = par1NBTTagCompound.getBoolean("SpawnForced");
        }
        NBTTagList spawnlist = null;
        spawnlist = par1NBTTagCompound.getTagList("Spawns");
        for (int i = 0; i < spawnlist.tagCount(); ++i) {
            final NBTTagCompound spawndata = (NBTTagCompound) spawnlist.tagAt(i);
            final int spawndim = spawndata.getInteger("Dim");
            this.spawnChunkMap.put(spawndim, new ChunkCoordinates(spawndata.getInteger("SpawnX"), spawndata.getInteger("SpawnY"), spawndata.getInteger("SpawnZ")));
            this.spawnForcedMap.put(spawndim, spawndata.getBoolean("SpawnForced"));
        }

        this.foodStats.readNBT(par1NBTTagCompound);
        this.capabilities.readCapabilitiesFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("EnderItems")) {
            final NBTTagList nbttaglist1 = par1NBTTagCompound.getTagList("EnderItems");
            this.theInventoryEnderChest.loadInventoryFromNBT(nbttaglist1);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
        par1NBTTagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
        par1NBTTagCompound.setBoolean("Sleeping", this.sleeping);
        par1NBTTagCompound.setShort("SleepTimer", (short) this.sleepTimer);
        par1NBTTagCompound.setFloat("XpP", this.experience);
        par1NBTTagCompound.setInteger("XpLevel", this.experienceLevel);
        par1NBTTagCompound.setInteger("XpTotal", this.experienceTotal);
        par1NBTTagCompound.setInteger("Score", this.getScore());

        if (this.spawnChunk != null) {
            par1NBTTagCompound.setInteger("SpawnX", this.spawnChunk.posX);
            par1NBTTagCompound.setInteger("SpawnY", this.spawnChunk.posY);
            par1NBTTagCompound.setInteger("SpawnZ", this.spawnChunk.posZ);
            par1NBTTagCompound.setBoolean("SpawnForced", this.spawnForced);
            par1NBTTagCompound.setString("SpawnWorld", spawnWorld); // CraftBukkit - fixes bed spawns for multiworld worlds
        }
        final NBTTagList spawnlist = new NBTTagList();
        for (final Entry<Integer, ChunkCoordinates> entry : this.spawnChunkMap.entrySet()) {
            final NBTTagCompound spawndata = new NBTTagCompound();
            final ChunkCoordinates spawn = entry.getValue();
            if (spawn == null) continue;
            Boolean forced = spawnForcedMap.get(entry.getKey());
            if (forced == null) forced = false;
            spawndata.setInteger("Dim", entry.getKey());
            spawndata.setInteger("SpawnX", spawn.posX);
            spawndata.setInteger("SpawnY", spawn.posY);
            spawndata.setInteger("SpawnZ", spawn.posZ);
            spawndata.setBoolean("SpawnForced", forced);
            spawnlist.appendTag(spawndata);
        }
        par1NBTTagCompound.setTag("Spawns", spawnlist);

        this.foodStats.writeNBT(par1NBTTagCompound);
        this.capabilities.writeCapabilitiesToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest(final IInventory par1IInventory) {
    }

    public void displayGUIHopper(final TileEntityHopper par1TileEntityHopper) {
    }

    public void displayGUIHopperMinecart(final EntityMinecartHopper par1EntityMinecartHopper) {
    }

    public void displayGUIHorse(final EntityHorse par1EntityHorse, final IInventory par2IInventory) {
    }

    public void displayGUIEnchantment(final int par1, final int par2, final int par3, final String par4Str) {
    }

    /**
     * Displays the GUI for interacting with an anvil.
     */
    public void displayGUIAnvil(final int par1, final int par2, final int par3) {
    }

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void displayGUIWorkbench(final int par1, final int par2, final int par3) {
    }

    public float getEyeHeight() {
        return eyeHeight;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void resetHeight() {
        this.yOffset = 1.62F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, float par2) {
        float par21 = par2;
        if (ForgeHooks.onLivingAttack(this, par1DamageSource, par21)) return false;
        if (this.isEntityInvulnerable()) {
            return false;
        } else if (this.capabilities.disableDamage && !par1DamageSource.canHarmInCreative()) {
            return false;
        } else {
            this.entityAge = 0;

            if (this.getHealth() <= 0.0F) {
                return false;
            } else {
                if (this.isPlayerSleeping() && !this.worldObj.isRemote) {
                    this.wakeUpPlayer(true, true, false);
                }

                if (par1DamageSource.isDifficultyScaled()) {
                    if (this.worldObj.difficultySetting == 0) {
                        return false; // CraftBukkit - i = 0 -> return false
                    }

                    if (this.worldObj.difficultySetting == 1) {
                        par21 = par21 / 2.0F + 1.0F;
                    }

                    if (this.worldObj.difficultySetting == 3) {
                        par21 = par21 * 3.0F / 2.0F;
                    }
                }

                if (false && par21 == 0.0F)   // CraftBukkit - Don't filter out 0 damage
                {
                    return false;
                } else {
                    Entity entity = par1DamageSource.getEntity();

                    if (entity instanceof EntityArrow && ((EntityArrow) entity).shootingEntity != null) {
                        entity = ((EntityArrow) entity).shootingEntity;
                    }

                    this.addStat(StatList.damageTakenStat, Math.round(par21 * 10.0F));
                    return super.attackEntityFrom(par1DamageSource, par21);
                }
            }
        }
    }

    public boolean canAttackPlayer(final EntityPlayer par1EntityPlayer) {
        // CraftBukkit start - Change to check OTHER player's scoreboard team according to API
        // To summarize this method's logic, it's "Can parameter hurt this"
        final org.bukkit.scoreboard.Team team;

        if (par1EntityPlayer instanceof EntityPlayerMP) {
            final EntityPlayerMP thatPlayer = (EntityPlayerMP) par1EntityPlayer;
            team = thatPlayer.getBukkitEntity().getScoreboard().getPlayerTeam(thatPlayer.getBukkitEntity());

            if (team == null || team.allowFriendlyFire()) {
                return true;
            }
        } else {
            // This should never be called, but is implemented anyway
            final org.bukkit.OfflinePlayer thisPlayer = par1EntityPlayer.worldObj.getServer().getOfflinePlayer(par1EntityPlayer.username);
            team = par1EntityPlayer.worldObj.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(thisPlayer);

            if (team == null || team.allowFriendlyFire()) {
                return true;
            }
        }

        if (this instanceof EntityPlayerMP) {
            return !team.hasPlayer(((EntityPlayerMP) this).getBukkitEntity());
        }

        return !team.hasPlayer(this.worldObj.getServer().getOfflinePlayer(this.username));
        // CraftBukkit end
    }

    /**
     * Called when the player attack or gets attacked, it's alert all wolves in the area that are owned by the player to
     * join the attack or defend the player.
     */
    protected void alertWolves(final EntityLivingBase par1EntityLivingBase, final boolean par2) {
        if (!(par1EntityLivingBase instanceof EntityCreeper) && !(par1EntityLivingBase instanceof EntityGhast)) {
            if (par1EntityLivingBase instanceof EntityWolf) {
                final EntityWolf entitywolf = (EntityWolf) par1EntityLivingBase;

                if (entitywolf.isTamed() && this.username.equals(entitywolf.getOwnerName())) {
                    return;
                }
            }

            if (!(par1EntityLivingBase instanceof EntityPlayer) || this.canAttackPlayer((EntityPlayer) par1EntityLivingBase)) {
                if (!(par1EntityLivingBase instanceof EntityHorse) || !((EntityHorse) par1EntityLivingBase).isTame()) {
                    final List list = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getAABBPool().getAABB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
                    final Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        final EntityWolf entitywolf1 = (EntityWolf) iterator.next();

                        if (entitywolf1.isTamed() && entitywolf1.getEntityToAttack() == null && this.username.equals(entitywolf1.getOwnerName()) && (!par2 || !entitywolf1.isSitting())) {
                            entitywolf1.setSitting(false);
                            entitywolf1.setTarget(par1EntityLivingBase);
                        }
                    }
                }
            }
        }
    }

    protected void damageArmor(final float par1) {
        this.inventory.damageArmor(par1);
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue() {
        return this.inventory.getTotalArmorValue();
    }

    /**
     * When searching for vulnerable players, if a player is invisible, the return value of this is the chance of seeing
     * them anyway.
     */
    public float getArmorVisibility() {
        int i = 0;
        final ItemStack[] aitemstack = this.inventory.armorInventory;
        final int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            final ItemStack itemstack = aitemstack[k];

            if (itemstack != null) {
                ++i;
            }
        }

        return (float) i / (float) this.inventory.armorInventory.length;
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void damageEntity(final DamageSource par1DamageSource, float par2) {
        float par21 = par2;
        if (!this.isEntityInvulnerable()) {
            par21 = ForgeHooks.onLivingHurt(this, par1DamageSource, par21);
            if (par21 <= 0) return;
            if (!par1DamageSource.isUnblockable() && this.isBlocking() && par21 > 0.0F) {
                par21 = (1.0F + par21) * 0.5F;
            }

            par21 = ArmorProperties.ApplyArmor(this, inventory.armorInventory, par1DamageSource, par21);
            if (par21 <= 0) return;
            par21 = this.applyPotionDamageCalculations(par1DamageSource, par21);
            final float f1 = par21;
            par21 = Math.max(par21 - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - par21));

            if (par21 != 0.0F) {
                this.addExhaustion(par1DamageSource.getHungerDamage());
                final float f2 = this.getHealth();
                this.setHealth(this.getHealth() - par21);
                this.func_110142_aN().func_94547_a(par1DamageSource, f2, par21);
            }
        }
    }

    /**
     * Displays the furnace GUI for the passed in furnace entity. Args: tileEntityFurnace
     */
    public void displayGUIFurnace(final TileEntityFurnace par1TileEntityFurnace) {
    }

    /**
     * Displays the dipsenser GUI for the passed in dispenser entity. Args: TileEntityDispenser
     */
    public void displayGUIDispenser(final TileEntityDispenser par1TileEntityDispenser) {
    }

    /**
     * Displays the GUI for editing a sign. Args: tileEntitySign
     */
    public void displayGUIEditSign(final TileEntity par1TileEntity) {
    }

    /**
     * Displays the GUI for interacting with a brewing stand.
     */
    public void displayGUIBrewingStand(final TileEntityBrewingStand par1TileEntityBrewingStand) {
    }

    /**
     * Displays the GUI for interacting with a beacon.
     */
    public void displayGUIBeacon(final TileEntityBeacon par1TileEntityBeacon) {
    }

    public void displayGUIMerchant(final IMerchant par1IMerchant, final String par2Str) {
    }

    /**
     * Displays the GUI for interacting with a book.
     */
    public void displayGUIBook(final ItemStack par1ItemStack) {
    }

    public boolean interactWith(final Entity par1Entity) {
        if (MinecraftForge.EVENT_BUS.post(new EntityInteractEvent(this, par1Entity))) return false;
        ItemStack itemstack = this.getCurrentEquippedItem();
        final ItemStack itemstack1 = itemstack != null ? itemstack.copy() : null;

        if (!par1Entity.interactFirst(this)) {
            if (itemstack != null && par1Entity instanceof EntityLivingBase) {
                if (this.capabilities.isCreativeMode) {
                    itemstack = itemstack1;
                }

                if (itemstack.func_111282_a(this, (EntityLivingBase) par1Entity)) {
                    // CraftBukkit - bypass infinite items; <= 0 -> == 0
                    if (itemstack.stackSize == 0 && !this.capabilities.isCreativeMode) {
                        this.destroyCurrentEquippedItem();
                    }

                    return true;
                }
            }

            return false;
        } else {
            if (itemstack != null && itemstack == this.getCurrentEquippedItem()) {
                if (itemstack.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                    this.destroyCurrentEquippedItem();
                } else if (itemstack.stackSize < itemstack1.stackSize && this.capabilities.isCreativeMode) {
                    itemstack.stackSize = itemstack1.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Returns the currently being used item by the player.
     */
    public ItemStack getCurrentEquippedItem() {
        return this.inventory.getCurrentItem();
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void destroyCurrentEquippedItem() {
        final ItemStack orig = getCurrentEquippedItem();
        this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack) null);
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this, orig));
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset() {
        return (double) (this.yOffset - 0.5F);
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
     * called on it. Args: targetEntity
     */
    public void attackTargetEntityWithCurrentItem(final Entity par1Entity) {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(this, par1Entity))) {
            return;
        }
        final ItemStack stack = getCurrentEquippedItem();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, this, par1Entity)) {
            return;
        }
        if (par1Entity.canAttackWithItem()) {
            if (!par1Entity.hitByEntity(this)) {
                float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                float f1 = 0.0F;

                if (par1Entity instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) par1Entity);
                    i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) par1Entity);
                }

                if (this.isSprinting()) {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F) {
                    final boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && par1Entity instanceof EntityLivingBase;

                    if (flag && f > 0.0F) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    final int j = EnchantmentHelper.getFireAspectModifier(this);

                    if (par1Entity instanceof EntityLivingBase && j > 0 && !par1Entity.isBurning()) {
                        flag1 = true;
                        par1Entity.setFire(1);
                    }

                    //TODO Minecraft 1.12.2 start
                    final Entity targetEntity = par1Entity;

                    final double d1 = targetEntity.motionX;
                    final double d2 = targetEntity.motionY;
                    final double d3 = targetEntity.motionZ;
                    //TODO Minecraft 1.12.2 end

                    final boolean flag2 = par1Entity.attackEntityFrom(DamageSource.causePlayerDamage(this), f);

                    // CraftBukkit start - Return when the damage fails so that the item will not lose durability
                    if (!flag2) {
                        if (flag1) {
                            par1Entity.extinguish();
                        }
                        return;
                    }
                    // CraftBukkit end
                    if (flag2) {

                        //TODO Minecraft 1.12.2 start
                        if (CoreSettings.getInstance().getSettings().isPvp1_12_2()) {
                            if (i > 0) {
                                if (targetEntity instanceof EntityLivingBase)
                                    ((EntityLivingBase) targetEntity).knockBack(this, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                                else
                                    targetEntity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * (float) i * 0.5F));


                                this.motionX *= 0.6D;
                                this.motionZ *= 0.6D;
                                this.setSprinting(false);
                            }

                            if (this.getCurrentEquippedItem() != null)
                                if (this.getCurrentEquippedItem().getItem() instanceof ItemSword)
                                    ((EntityLivingBase) targetEntity).knockBack(this, 0.4F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));

                            if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                                // CraftBukkit start - Add Velocity Event
                                boolean cancelled = false;
                                final Player player = (Player) targetEntity.getBukkitEntity();
                                final org.bukkit.util.Vector velocity = new Vector(d1, d2, d3);

                                final PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                                this.worldObj.getServer().getPluginManager().callEvent(event);

                                if (event.isCancelled())
                                    cancelled = true;
                                else if (!velocity.equals(event.getVelocity()))
                                    player.setVelocity(event.getVelocity());

                                if (!cancelled) {
                                    ((EntityPlayerMP) targetEntity).playerNetServerHandler.sendFastPacketToPlayer(new Packet28EntityVelocity(targetEntity));

                                    targetEntity.velocityChanged = false;
                                    targetEntity.motionX = d1;
                                    targetEntity.motionY = d2;
                                    targetEntity.motionZ = d3;
                                }
                                // CraftBukkit end
                            }
                        } else
                            //TODO Minecraft 1.12.2 End

                            if (i > 0) {
                                par1Entity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F));
                                this.motionX *= 0.6D;
                                this.motionZ *= 0.6D;
                                this.setSprinting(false);
                            }

                        if (flag) {
                            this.onCriticalHit(par1Entity);
                        }

                        if (f1 > 0.0F) {
                            this.onEnchantmentCritical(par1Entity);
                        }

                        if (f >= 18.0F) {
                            this.triggerAchievement(AchievementList.overkill);
                        }

                        this.setLastAttacker(par1Entity);

                        if (par1Entity instanceof EntityLivingBase) {
                            EnchantmentThorns.func_92096_a(this, (EntityLivingBase) par1Entity, this.rand);
                        }
                    }

                    final ItemStack itemstack = this.getCurrentEquippedItem();
                    Object object = par1Entity;

                    if (par1Entity instanceof EntityDragonPart) {
                        final IEntityMultiPart ientitymultipart = ((EntityDragonPart) par1Entity).entityDragonObj;

                        if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase) {
                            object = (EntityLivingBase) ientitymultipart;
                        }
                    }

                    if (itemstack != null && object instanceof EntityLivingBase) {
                        itemstack.hitEntity((EntityLivingBase) object, this);

                        // CraftBukkit - bypass infinite items; <= 0 -> == 0
                        if (itemstack.stackSize == 0) {
                            this.destroyCurrentEquippedItem();
                        }
                    }

                    if (par1Entity instanceof EntityLivingBase) {
                        if (par1Entity.isEntityAlive()) {
                            this.alertWolves((EntityLivingBase) par1Entity, true);
                        }
                        this.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

                        if (j > 0 && flag2) {
                            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                            final EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), par1Entity.getBukkitEntity(), j * 4);
                            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);
                            if (!combustEvent.isCancelled()) {
                                par1Entity.setFire(combustEvent.getDuration());
                            }
                            // CraftBukkit end
                        } else if (flag1) {
                            par1Entity.extinguish();
                        }
                    }

                    this.addExhaustion(0.3F);
                }
            }
        }
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(final Entity par1Entity) {
    }

    public void onEnchantmentCritical(final Entity par1Entity) {
    }

    @SideOnly(Side.CLIENT)
    public void respawnPlayer() {
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead() {
        super.setDead();
        this.inventoryContainer.onContainerClosed(this);

        if (this.openContainer != null) {
            // CraftBukkit start
            final InventoryCloseEvent event = new InventoryCloseEvent(this.openContainer.getBukkitView());
            if (this.openContainer.getBukkitView() != null)
                Bukkit.getServer().getPluginManager().callEvent(event); // Cauldron - allow vanilla mods to bypass
            // CraftBukkit end
            this.openContainer.onContainerClosed(this);
        }
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock() {
        return !this.sleeping && super.isEntityInsideOpaqueBlock();
    }

    /**
     * Attempts to have the player sleep in a bed at the specified location.
     */
    public EnumStatus sleepInBedAt(final int par1, final int par2, final int par3) {
        final PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(this, par1, par2, par3);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.result != null) {
            return event.result;
        }
        if (!this.worldObj.isRemote) {
            if (this.isPlayerSleeping() || !this.isEntityAlive()) {
                return EnumStatus.OTHER_PROBLEM;
            }

            if (!this.worldObj.provider.isSurfaceWorld()) {
                return EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (this.worldObj.isDaytime()) {
                return EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.posX - (double) par1) > 3.0D || Math.abs(this.posY - (double) par2) > 2.0D || Math.abs(this.posZ - (double) par3) > 3.0D) {
                return EnumStatus.TOO_FAR_AWAY;
            }

            final double d0 = 8.0D;
            final double d1 = 5.0D;
            final List list = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().getAABB((double) par1 - d0, (double) par2 - d1, (double) par3 - d0, (double) par1 + d0, (double) par2 + d1, (double) par3 + d0));

            if (!list.isEmpty()) {
                return EnumStatus.NOT_SAFE;
            }
        }

        if (this.isRiding()) {
            this.mountEntity((Entity) null);
        }

        // CraftBukkit start
        if (this.getBukkitEntity() instanceof Player) {
            final Player player = (Player) this.getBukkitEntity();
            final org.bukkit.block.Block bed = this.worldObj.getWorld().getBlockAt(par1, par2, par3);
            final PlayerBedEnterEvent bedEvent = new PlayerBedEnterEvent(player, bed);
            this.worldObj.getServer().getPluginManager().callEvent(bedEvent);

            if (bedEvent.isCancelled()) {
                return EnumStatus.OTHER_PROBLEM;
            }
        }
        // CraftBukkit end

        this.setSize(0.2F, 0.2F);
        this.yOffset = 0.2F;

        if (this.worldObj.blockExists(par1, par2, par3)) {
            final int l = this.worldObj.getBlockMetadata(par1, par2, par3);
            int i1 = BlockBed.getDirection(l);
            final Block block = Block.blocksList[worldObj.getBlockId(par1, par2, par3)];
            if (block != null) {
                i1 = block.getBedDirection(worldObj, par1, par2, par3);
            }
            float f = 0.5F;
            float f1 = 0.5F;

            switch (i1) {
                case 0:
                    f1 = 0.9F;
                    break;
                case 1:
                    f = 0.1F;
                    break;
                case 2:
                    f1 = 0.1F;
                    break;
                case 3:
                    f = 0.9F;
            }

            this.func_71013_b(i1);
            this.setPosition((double) ((float) par1 + f), (double) ((float) par2 + 0.9375F), (double) ((float) par3 + f1));
        } else {
            this.setPosition((double) ((float) par1 + 0.5F), (double) ((float) par2 + 0.9375F), (double) ((float) par3 + 0.5F));
        }

        this.sleeping = true;
        this.sleepTimer = 0;
        this.playerLocation = new ChunkCoordinates(par1, par2, par3);
        this.motionX = this.motionZ = this.motionY = 0.0D;

        if (!this.worldObj.isRemote) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        return EnumStatus.OK;
    }

    private void func_71013_b(final int par1) {
        this.field_71079_bU = 0.0F;
        this.field_71089_bV = 0.0F;

        switch (par1) {
            case 0:
                this.field_71089_bV = -1.8F;
                break;
            case 1:
                this.field_71079_bU = 1.8F;
                break;
            case 2:
                this.field_71089_bV = 1.8F;
                break;
            case 3:
                this.field_71079_bU = -1.8F;
        }
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void wakeUpPlayer(final boolean par1, final boolean par2, final boolean par3) {
        this.setSize(0.6F, 1.8F);
        this.resetHeight();
        final ChunkCoordinates chunkcoordinates = this.playerLocation;
        ChunkCoordinates chunkcoordinates1 = this.playerLocation;

        final Block block = (chunkcoordinates == null ? null : Block.blocksList[worldObj.getBlockId(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ)]);

        if (chunkcoordinates != null && block != null && block.isBed(worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this)) {
            block.setBedOccupied(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this, false);
            chunkcoordinates1 = block.getBedSpawnPosition(worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this);

            if (chunkcoordinates1 == null) {
                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ);
            }

            this.setPosition((double) ((float) chunkcoordinates1.posX + 0.5F), (double) ((float) chunkcoordinates1.posY + this.yOffset + 0.1F), (double) ((float) chunkcoordinates1.posZ + 0.5F));
        }

        this.sleeping = false;

        if (!this.worldObj.isRemote && par2) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        // CraftBukkit start
        if (this.getBukkitEntity() instanceof Player) {
            final Player player = (Player) this.getBukkitEntity();
            final org.bukkit.block.Block bed;

            if (chunkcoordinates != null) {
                bed = this.worldObj.getWorld().getBlockAt(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ);
            } else {
                bed = this.worldObj.getWorld().getBlockAt(player.getLocation());
            }

            final PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed);
            this.worldObj.getServer().getPluginManager().callEvent(event);
        }
        // CraftBukkit end
        if (par1) {
            this.sleepTimer = 0;
        } else {
            this.sleepTimer = 100;
        }

        if (par3) {
            this.setSpawnChunk(this.playerLocation, false);
        }
    }

    /**
     * Checks if the player is currently in a bed
     */
    private boolean isInBed() {
        final ChunkCoordinates c = playerLocation;
        final int blockID = worldObj.getBlockId(c.posX, c.posY, c.posZ);
        return Block.blocksList[blockID] != null && Block.blocksList[blockID].isBed(worldObj, c.posX, c.posY, c.posZ, this);
    }

    /**
     * Ensure that a block enabling respawning exists at the specified coordinates and find an empty space nearby to
     * spawn.
     */
    public static ChunkCoordinates verifyRespawnCoordinates(final World par0World, final ChunkCoordinates par1ChunkCoordinates, final boolean par2) {
        final IChunkProvider ichunkprovider = par0World.getChunkProvider();
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ - 3 >> 4);
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX - 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);
        ichunkprovider.loadChunk(par1ChunkCoordinates.posX + 3 >> 4, par1ChunkCoordinates.posZ + 3 >> 4);

        final ChunkCoordinates c = par1ChunkCoordinates;
        final Block block = Block.blocksList[par0World.getBlockId(c.posX, c.posY, c.posZ)];

        if (block != null && block.isBed(par0World, c.posX, c.posY, c.posZ, null)) {
            final ChunkCoordinates chunkcoordinates1 = block.getBedSpawnPosition(par0World, c.posX, c.posY, c.posZ, null);
            return chunkcoordinates1;
        } else {
            final Material material = par0World.getBlockMaterial(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY, par1ChunkCoordinates.posZ);
            final Material material1 = par0World.getBlockMaterial(par1ChunkCoordinates.posX, par1ChunkCoordinates.posY + 1, par1ChunkCoordinates.posZ);
            final boolean flag1 = !material.isSolid() && !material.isLiquid();
            final boolean flag2 = !material1.isSolid() && !material1.isLiquid();
            return par2 && flag1 && flag2 ? par1ChunkCoordinates : null;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the orientation of the bed in degrees.
     */
    public float getBedOrientationInDegrees() {
        if (this.playerLocation != null) {
            final int x = playerLocation.posX;
            final int y = playerLocation.posY;
            final int z = playerLocation.posZ;
            final Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
            final int i = (block == null ? 0 : block.getBedDirection(worldObj, x, y, z));

            switch (i) {
                case 0:
                    return 90.0F;
                case 1:
                    return 0.0F;
                case 2:
                    return 270.0F;
                case 3:
                    return 180.0F;
            }
        }

        return 0.0F;
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping() {
        return this.sleeping;
    }

    /**
     * Returns whether or not the player is asleep and the screen has fully faded.
     */
    public boolean isPlayerFullyAsleep() {
        return this.sleeping && this.sleepTimer >= 100;
    }

    @SideOnly(Side.CLIENT)
    public int getSleepTimer() {
        return this.sleepTimer;
    }

    @SideOnly(Side.CLIENT)
    protected boolean getHideCape(final int par1) {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1 << par1) != 0;
    }

    protected void setHideCape(final int par1, final boolean par2) {
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (par2) {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 | 1 << par1)));
        } else {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 & ~(1 << par1))));
        }
    }

    /**
     * Add a chat message to the player
     */
    public void addChatMessage(final String par1Str) {
    }

    /**
     * Returns the location of the bed the player will respawn at, or null if the player has not slept in a bed.
     */
    @Deprecated
    public ChunkCoordinates getBedLocation() {
        return getBedLocation(this.dimension);
    }

    @Deprecated
    public boolean isSpawnForced() {
        return isSpawnForced(this.dimension);
    }

    /**
     * A dimension aware version of getBedLocation.
     *
     * @param dimension The dimension to get the bed spawn for
     * @return The player specific spawn location for the dimension.  May be null.
     */
    public ChunkCoordinates getBedLocation(final int dimension) {
        if (dimension == 0) return this.spawnChunk;
        return this.spawnChunkMap.get(dimension);
    }

    /**
     * A dimension aware version of isSpawnForced.
     * Noramally isSpawnForced is used to determine if the respawn system should check for a bed or not.
     * This just extends that to be dimension aware.
     *
     * @param dimension The dimension to get whether to check for a bed before spawning for
     * @return The player specific spawn location for the dimension.  May be null.
     */
    public boolean isSpawnForced(final int dimension) {
        if (dimension == 0) return this.spawnForced;
        final Boolean forced = this.spawnForcedMap.get(dimension);
        if (forced == null) return false;
        return forced;
    }

    /**
     * Defines a spawn coordinate to player spawn. Used by bed after the player sleep on it.
     */
    public void setSpawnChunk(final ChunkCoordinates par1ChunkCoordinates, final boolean par2) {
        if (this.dimension != 0) {
            setSpawnChunk(par1ChunkCoordinates, par2, this.dimension);
            return;
        }
        if (par1ChunkCoordinates != null) {
            this.spawnChunk = new ChunkCoordinates(par1ChunkCoordinates);
            this.spawnForced = par2;
            this.spawnWorld = this.worldObj.worldInfo.getWorldName(); // CraftBukkit
        } else {
            this.spawnChunk = null;
            this.spawnForced = false;
            this.spawnWorld = ""; // CraftBukkit
        }
    }

    /**
     * A dimension aware version of setSpawnChunk.
     * This functions identically, but allows you to specify which dimension to affect, rather than affecting the player's current dimension.
     *
     * @param chunkCoordinates The spawn point to set as the player-specific spawn point for the dimension
     * @param forced           Whether or not the respawn code should check for a bed at this location (true means it won't check for a bed)
     * @param dimension        Which dimension to apply the player-specific respawn point to
     */
    public void setSpawnChunk(final ChunkCoordinates chunkCoordinates, final boolean forced, final int dimension) {
        if (dimension == 0) {
            if (chunkCoordinates != null) {
                this.spawnChunk = new ChunkCoordinates(chunkCoordinates);
                this.spawnForced = forced;
            } else {
                this.spawnChunk = null;
                this.spawnForced = false;
            }
            return;
        }
        if (chunkCoordinates != null) {
            this.spawnChunkMap.put(dimension, new ChunkCoordinates(chunkCoordinates));
            this.spawnForcedMap.put(dimension, forced);
        } else {
            this.spawnChunkMap.remove(dimension);
            this.spawnForcedMap.remove(dimension);
        }
    }

    /**
     * Will trigger the specified trigger.
     */
    public void triggerAchievement(final StatBase par1StatBase) {
        this.addStat(par1StatBase, 1);
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(final StatBase par1StatBase, final int par2) {
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    //TODO ZoomCodeReplace protected on public
    public void jump() {
        super.jump();
        this.addStat(StatList.jumpStat, 1);

        if (this.isSprinting()) {
            this.addExhaustion(0.8F);
        } else {
            this.addExhaustion(0.2F);
        }
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(final float par1, final float par2) {
        final double d0 = this.posX;
        final double d1 = this.posY;
        final double d2 = this.posZ;

        if (this.capabilities.isFlying && this.ridingEntity == null) {
            final double d3 = this.motionY;
            final float f2 = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.getFlySpeed();
            super.moveEntityWithHeading(par1, par2);
            this.motionY = d3 * 0.6D;
            this.jumpMovementFactor = f2;
        } else {
            super.moveEntityWithHeading(par1, par2);
        }

        this.addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed() {
        return (float) this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }

    /**
     * Adds a value to a movement statistic field - like run, walk, swin or climb.
     */
    public void addMovementStat(final double par1, final double par3, final double par5) {
        if (this.ridingEntity == null) {
            final int i;

            if (this.isInsideOfMaterial(Material.water)) {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);

                if (i > 0) {
                    this.addStat(StatList.distanceDoveStat, i);
                    this.addExhaustion(0.015F * (float) i * 0.01F);
                }
            } else if (this.isInWater()) {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (i > 0) {
                    this.addStat(StatList.distanceSwumStat, i);
                    this.addExhaustion(0.015F * (float) i * 0.01F);
                }
            } else if (this.isOnLadder()) {
                if (par3 > 0.0D) {
                    this.addStat(StatList.distanceClimbedStat, (int) Math.round(par3 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (i > 0) {
                    this.addStat(StatList.distanceWalkedStat, i);

                    if (this.isSprinting()) {
                        this.addExhaustion(0.099999994F * (float) i * 0.01F);
                    } else {
                        this.addExhaustion(0.01F * (float) i * 0.01F);
                    }
                }
            } else {
                i = Math.round(MathHelper.sqrt_double(par1 * par1 + par5 * par5) * 100.0F);

                if (i > 25) {
                    this.addStat(StatList.distanceFlownStat, i);
                }
            }
        }
    }

    /**
     * Adds a value to a mounted movement statistic field - by minecart, boat, or pig.
     */
    private void addMountedMovementStat(final double par1, final double par3, final double par5) {
        if (this.ridingEntity != null) {
            final int i = Math.round(MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5) * 100.0F);

            if (i > 0) {
                if (this.ridingEntity instanceof EntityMinecart) {
                    this.addStat(StatList.distanceByMinecartStat, i);

                    if (this.startMinecartRidingCoordinate == null) {
                        this.startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
                    } else if ((double) this.startMinecartRidingCoordinate.getDistanceSquared(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000000.0D) {
                        this.addStat(AchievementList.onARail, 1);
                    }
                } else if (this.ridingEntity instanceof EntityBoat) {
                    this.addStat(StatList.distanceByBoatStat, i);
                } else if (this.ridingEntity instanceof EntityPig) {
                    this.addStat(StatList.distanceByPigStat, i);
                }
            }
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(final float par1) {
        if (!this.capabilities.allowFlying) {
            if (par1 >= 2.0F) {
                this.addStat(StatList.distanceFallenStat, (int) Math.round((double) par1 * 100.0D));
            }

            super.fall(par1);
        } else {
            MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(this, par1));
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(final EntityLivingBase par1EntityLivingBase) {
        if (par1EntityLivingBase instanceof IMob) {
            this.triggerAchievement(AchievementList.killEnemy);
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb() {
        if (!this.capabilities.isFlying) {
            super.setInWeb();
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the Icon Index of the item currently held
     */
    public Icon getItemIcon(final ItemStack par1ItemStack, final int par2) {
        Icon icon = super.getItemIcon(par1ItemStack, par2);

        if (par1ItemStack.itemID == Item.fishingRod.itemID && this.fishEntity != null) {
            icon = Item.fishingRod.func_94597_g();
        } else {
            if (par1ItemStack.getItem().requiresMultipleRenderPasses()) {
                return par1ItemStack.getItem().getIcon(par1ItemStack, par2);
            }

            if (this.itemInUse != null && par1ItemStack.itemID == Item.bow.itemID) {
                final int j = par1ItemStack.getMaxItemUseDuration() - this.itemInUseCount;

                if (j >= 18) {
                    return Item.bow.getItemIconForUseDuration(2);
                }

                if (j > 13) {
                    return Item.bow.getItemIconForUseDuration(1);
                }

                if (j > 0) {
                    return Item.bow.getItemIconForUseDuration(0);
                }
            }
            icon = par1ItemStack.getItem().getIcon(par1ItemStack, par2, this, itemInUse, itemInUseCount);
        }

        return icon;
    }

    public ItemStack getCurrentArmor(final int par1) {
        return this.inventory.armorItemInSlot(par1);
    }

    /**
     * This method increases the player's current amount of experience.
     */
    public void addExperience(int par1) {
        int par11 = par1;
        this.addScore(par11);
        final int j = Integer.MAX_VALUE - this.experienceTotal;

        if (par11 > j) {
            par11 = j;
        }

        this.experience += (float) par11 / (float) this.xpBarCap();

        for (this.experienceTotal += par11; this.experience >= 1.0F; this.experience /= (float) this.xpBarCap()) {
            this.experience = (this.experience - 1.0F) * (float) this.xpBarCap();
            this.addExperienceLevel(1);
        }
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(final int par1) {
        this.experienceLevel += par1;

        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0F;
            this.experienceTotal = 0;
        }

        if (par1 > 0 && this.experienceLevel % 5 == 0 && (float) this.field_82249_h < (float) this.ticksExisted - 100.0F) {
            final float f = this.experienceLevel > 30 ? 1.0F : (float) this.experienceLevel / 30.0F;
            this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
            this.field_82249_h = this.ticksExisted;
        }
    }

    /**
     * This method returns the cap amount of experience that the experience bar can hold. With each level, the
     * experience cap on the player's experience bar is raised by 10.
     */
    public int xpBarCap() {
        return this.experienceLevel >= 30 ? 62 + (this.experienceLevel - 30) * 7 : (this.experienceLevel >= 15 ? 17 + (this.experienceLevel - 15) * 3 : 17);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void addExhaustion(final float par1) {
        if (!this.capabilities.disableDamage) {
            if (!this.worldObj.isRemote) {
                this.foodStats.addExhaustion(par1);
            }
        }
    }

    /**
     * Returns the player's FoodStats object.
     */
    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    public boolean canEat(final boolean par1) {
        return (par1 || this.foodStats.needFood()) && !this.capabilities.disableDamage;
    }

    /**
     * Checks if the player's health is not full and not zero.
     */
    public boolean shouldHeal() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void setItemInUse(final ItemStack par1ItemStack, final int par2) {
        if (par1ItemStack != this.itemInUse) {
            this.itemInUse = par1ItemStack;
            this.itemInUseCount = par2;

            if (!this.worldObj.isRemote) {
                this.setEating(true);
            }
        }
    }

    /**
     * Returns true if the given block can be mined with the current tool in adventure mode.
     */
    public boolean isCurrentToolAdventureModeExempt(final int par1, final int par2, final int par3) {
        if (this.capabilities.allowEdit) {
            return true;
        } else {
            final int l = this.worldObj.getBlockId(par1, par2, par3);

            if (l > 0) {
                final Block block = Block.blocksList[l];

                if (block.blockMaterial.isAdventureModeExempt()) {
                    return true;
                }

                if (this.getCurrentEquippedItem() != null) {
                    final ItemStack itemstack = this.getCurrentEquippedItem();

                    if (itemstack.canHarvestBlock(block) || itemstack.getStrVsBlock(block) > 1.0F) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean canPlayerEdit(final int par1, final int par2, final int par3, final int par4, final ItemStack par5ItemStack) {
        return this.capabilities.allowEdit ? true : (par5ItemStack != null ? par5ItemStack.canEditBlocks() : false);
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(final EntityPlayer par1EntityPlayer) {
        if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            return 0;
        } else {
            final int i = this.experienceLevel * 7;
            return i > 100 ? 100 : i;
        }
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer() {
        return true;
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName() {
        return this.username;
    }

    @SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    /**
     * Copies the values from the given player into this player if boolean par2 is true. Always clones Ender Chest
     * Inventory.
     */
    public void clonePlayer(final EntityPlayer par1EntityPlayer, final boolean par2) {
        if (par2) {
            this.inventory.copyInventory(par1EntityPlayer.inventory);
            this.setHealth(par1EntityPlayer.getHealth());
            this.foodStats = par1EntityPlayer.foodStats;
            this.experienceLevel = par1EntityPlayer.experienceLevel;
            this.experienceTotal = par1EntityPlayer.experienceTotal;
            this.experience = par1EntityPlayer.experience;
            this.setScore(par1EntityPlayer.getScore());
            this.teleportDirection = par1EntityPlayer.teleportDirection;
        } else if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            this.inventory.copyInventory(par1EntityPlayer.inventory);
            this.experienceLevel = par1EntityPlayer.experienceLevel;
            this.experienceTotal = par1EntityPlayer.experienceTotal;
            this.experience = par1EntityPlayer.experience;
            this.setScore(par1EntityPlayer.getScore());
        }

        this.spawnChunkMap = par1EntityPlayer.spawnChunkMap;
        this.spawnForcedMap = par1EntityPlayer.spawnForcedMap;
        this.theInventoryEnderChest = par1EntityPlayer.theInventoryEnderChest;

        //Copy over a section of the Entity Data from the old player.
        //Allows mods to specify data that persists after players respawn.
        final NBTTagCompound old = par1EntityPlayer.getEntityData();
        if (old.hasKey(PERSISTED_NBT_TAG)) {
            getEntityData().setCompoundTag(PERSISTED_NBT_TAG, old.getCompoundTag(PERSISTED_NBT_TAG));
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return !this.capabilities.isFlying;
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities() {
    }

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(final EnumGameType par1EnumGameType) {
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return this.username;
    }

    public World getEntityWorld() {
        return this.worldObj;
    }

    /**
     * Returns the InventoryEnderChest of this player.
     */
    public InventoryEnderChest getInventoryEnderChest() {
        return this.theInventoryEnderChest;
    }

    /**
     * 0 = item, 1-n is armor
     */
    public ItemStack getCurrentItemOrArmor(final int par1) {
        return par1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[par1 - 1];
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem() {
        return this.inventory.getCurrentItem();
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(final int par1, final ItemStack par2ItemStack) {
        if (par1 == 0) {
            this.inventory.mainInventory[this.inventory.currentItem] = par2ItemStack;
        } else {
            this.inventory.armorInventory[par1 - 1] = par2ItemStack;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Only used by renderer in EntityLivingBase subclasses.\nDetermines if an entity is visible or not to a specfic
     * player, if the entity is normally invisible.\nFor EntityLivingBase subclasses, returning false when invisible
     * will render the entity semitransparent.
     */
    public boolean isInvisibleToPlayer(final EntityPlayer par1EntityPlayer) {
        if (!this.isInvisible()) {
            return false;
        } else {
            final Team team = this.getTeam();
            return team == null || par1EntityPlayer == null || par1EntityPlayer.getTeam() != team || !team.func_98297_h();
        }
    }

    public ItemStack[] getLastActiveItems() {
        return this.inventory.armorInventory;
    }

    @SideOnly(Side.CLIENT)
    public boolean getHideCape() {
        return this.getHideCape(1);
    }

    public boolean isPushedByWater() {
        return !this.capabilities.isFlying;
    }

    public Scoreboard getWorldScoreboard() {
        return this.worldObj.getScoreboard();
    }

    public Team getTeam() {
        return this.getWorldScoreboard().getPlayersTeam(this.username);
    }

    /**
     * Returns the translated name of the entity.
     */
    public String getTranslatedEntityName() {
        return ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayName());
    }

    public void setAbsorptionAmount(float par1) {
        float par11 = par1;
        if (par11 < 0.0F) {
            par11 = 0.0F;
        }

        this.getDataWatcher().updateObject(17, Float.valueOf(par11));
    }

    public float getAbsorptionAmount() {
        return this.getDataWatcher().getWatchableObjectFloat(17);
    }

    public void openGui(final Object mod, final int modGuiId, final World world, final int x, final int y, final int z) {
        FMLNetworkHandler.openGui(this, mod, modGuiId, world, x, y, z);
    }

    /* ===================================== FORGE START =====================================*/

    public float eyeHeight;
    private String displayname;

    /**
     * Returns the default eye height of the player
     *
     * @return player default eye height
     */
    public float getDefaultEyeHeight() {
        return 0.12F;
    }

    /**
     * Get the currently computed display name, cached for efficiency.
     *
     * @return the current display name
     */
    public String getDisplayName() {
        if (this.displayname == null) {
            this.displayname = ForgeEventFactory.getPlayerDisplayName(this, this.username);
        }
        return this.displayname;
    }

    /**
     * Force the displayed name to refresh
     */
    public void refreshDisplayName() {
        this.displayname = ForgeEventFactory.getPlayerDisplayName(this, this.username);
    }
}
