package net.minecraft.entity;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.cauldron.CauldronHooks;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TravelAgent;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.SpigotTimings;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.*;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.spigotmc.CustomTimingsHandler;

import java.util.*;

// CraftBukkit start
// CraftBukkit end
// Cauldron start
// Cauldron end

public abstract class Entity
{
    // CraftBukkit start
    private static final int CURRENT_LEVEL = 2;
    static boolean isLevelAtLeast(final NBTTagCompound tag, final int level)
    {
        return tag.hasKey("Bukkit.updateLevel") && tag.getInteger("Bukkit.updateLevel") >= level;
    }
    // CraftBukkit end

    private static int nextEntityID;
    public int entityId;
    public double renderDistanceWeight;

    /**
     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
     * prevent spawning.
     */
    public boolean preventEntitySpawning;

    /** The entity that is riding this entity */
    public Entity riddenByEntity;

    /** The entity we are currently riding */
    public Entity ridingEntity;
    public boolean forceSpawn;

    /** Reference to the World object. */
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;

    /** Entity position X */
    public double posX;

    /** Entity position Y */
    public double posY;

    /** Entity position Z */
    public double posZ;

    /** Entity motion X */
    public double motionX;

    /** Entity motion Y */
    public double motionY;

    /** Entity motion Z */
    public double motionZ;

    /** Entity rotation Yaw */
    public float rotationYaw;

    /** Entity rotation Pitch */
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;

    /** Axis aligned bounding box. */
    public final AxisAlignedBB boundingBox;
    public boolean onGround;

    /**
     * True if after a move this entity has collided with something on X- or Z-axis
     */
    public boolean isCollidedHorizontally;

    /**
     * True if after a move this entity has collided with something on Y-axis
     */
    public boolean isCollidedVertically;

    /**
     * True if after a move this entity has collided with something either vertically or horizontally
     */
    public boolean isCollided;
    public boolean velocityChanged;
    protected boolean isInWeb;
    public boolean field_70135_K;

    /**
     * Gets set by setDead, so this must be the flag whether an Entity is dead (inactive may be better term)
     */
    public boolean isDead;
    public float yOffset;

    /** How wide this entity is considered to be */
    public float width;

    /** How high this entity is considered to be */
    public float height;

    /** The previous ticks distance walked multiplied by 0.6 */
    public float prevDistanceWalkedModified;

    /** The distance walked multiplied by 0.6 */
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;

    /**
     * The distance that has to be exceeded in order to triger a new step sound and an onEntityWalking event on a block
     */
    private int nextStepDistance;

    /**
     * The entity's X coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosX;

    /**
     * The entity's Y coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosY;

    /**
     * The entity's Z coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosZ;
    public float ySize;

    /**
     * How high this entity can step up when running into a block to try to get over it (currently make note the entity
     * will always step up this amount and not just the amount needed)
     */
    public float stepHeight;

    /**
     * Whether this entity won't clip with collision or not (make note it won't disable gravity)
     */
    public boolean noClip;

    /**
     * Reduces the velocity applied by entity collisions by the specified percent.
     */
    public float entityCollisionReduction;
    protected Random rand;

    /** How many ticks has this entity had ran since being alive */
    public int ticksExisted;

    /**
     * The amount of ticks you have to stand inside of fire before be set on fire
     */
    public int fireResistance;
    public int fire; // CraftBukkit - private -> public

    /**
     * Whether this entity is currently inside of water (if it handles water movement that is)
     */
    public boolean inWater; // Spigot - protected -> public

    /**
     * Remaining time an entity will be "immune" to further damage after being hurt.
     */
    public int hurtResistantTime;
    private boolean firstUpdate;
    protected boolean isImmuneToFire;
    public DataWatcher dataWatcher;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;

    /** Has this entity been added to the chunk its within */
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    @SideOnly(Side.CLIENT)
    public int serverPosX;
    @SideOnly(Side.CLIENT)
    public int serverPosY;
    @SideOnly(Side.CLIENT)
    public int serverPosZ;

    /**
     * Render entity even if it is outside the camera frustum. Only true in EntityFish for now. Used in RenderGlobal:
     * render if ignoreFrustumCheck or in frustum.
     */
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    public int timeUntilPortal;

    /** Whether the entity is inside a Portal */
    protected boolean inPortal;
    protected int portalCounter;

    /** Which dimension the player is in (-1 = the Nether, 0 = normal world) */
    public int dimension;
    protected int teleportDirection;
    private boolean invulnerable;
    public UUID entityUniqueID; // CraftBukkit - private -> public
    public EnumEntitySize myEntitySize;
    public boolean valid; // CraftBukkit
    
    // Spigot start
    public CustomTimingsHandler tickTimer = SpigotTimings.getEntityTimings(this); // Spigot

    public final byte activationType = org.spigotmc.ActivationRange.initializeEntityActivationType(this);
    public final boolean defaultActivationState;
    public long activatedTick = 0;
    public void inactiveTick() { }
    // Spigot end
            
    /** Forge: Used to store custom data for each entity. */
    private NBTTagCompound customEntityData;
    public boolean captureDrops = false;
    public ArrayList<EntityItem> capturedDrops = new ArrayList<EntityItem>();

    private HashMap<String, IExtendedEntityProperties> extendedProperties;
    public String spawnReason; // Cauldron - used to handle CraftBukkit's SpawnReason with CustomSpawners

    public Entity(final World par1World)
    {
        this.entityId = nextEntityID++;
        this.renderDistanceWeight = 1.0D;
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.field_70135_K = true;
        this.width = 0.6F;
        this.height = 1.8F;
        this.nextStepDistance = 1;
        this.rand = new Random();
        this.fireResistance = 1;
        this.firstUpdate = true;
        this.dataWatcher = new DataWatcher();
        this.entityUniqueID = new UUID(rand.nextLong(), rand.nextLong()); // Spigot
        this.myEntitySize = EnumEntitySize.SIZE_2;
        this.worldObj = par1World;
        this.setPosition(0.0D, 0.0D, 0.0D);

        if (par1World != null && par1World.getWorld() != null) // Cauldron - add second null check for Worlds without CraftWorld
        {
            this.dimension = par1World.provider.dimensionId;
            // Spigot start
            this.defaultActivationState = org.spigotmc.ActivationRange.initializeEntityActivationState(this, par1World.spigotConfig);
        }
        else
        {
            this.defaultActivationState = false;
        }

        // Spigot end
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        this.entityInit();

        extendedProperties = new HashMap<String, IExtendedEntityProperties>();

        MinecraftForge.EVENT_BUS.post(new EntityEvent.EntityConstructing(this));

        for (final IExtendedEntityProperties props : this.extendedProperties.values())
        {
            props.init(this, par1World);
        }
    }

    protected abstract void entityInit();

    public DataWatcher getDataWatcher()
    {
        return this.dataWatcher;
    }

    public boolean equals(final Object par1Obj)
    {
        return par1Obj instanceof Entity ? ((Entity)par1Obj).entityId == this.entityId : false;
    }

    public int hashCode()
    {
        return this.entityId;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    protected void preparePlayerToSpawn()
    {
        if (this.worldObj != null)
        {
            while (this.posY > 0.0D)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
                {
                    break;
                }

                ++this.posY;
            }

            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.rotationPitch = 0.0F;
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        this.isDead = true;
        // Cauldron start
        if (worldObj != null)
        {
            net.minecraftforge.cauldron.CauldronHooks.logEntityDeath(this);
        }
        // Cauldron end
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected void setSize(final float par1, final float par2)
    {
        float f2;

        if (par1 != this.width || par2 != this.height)
        {
            f2 = this.width;
            this.width = par1;
            this.height = par2;
            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;

            if (this.width > f2 && !this.firstUpdate && !this.worldObj.isRemote)
            {
                this.moveEntity((double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
            }
        }

        f2 = par1 % 2.0F;

        if ((double)f2 < 0.375D)
        {
            this.myEntitySize = EnumEntitySize.SIZE_1;
        }
        else if ((double)f2 < 0.75D)
        {
            this.myEntitySize = EnumEntitySize.SIZE_2;
        }
        else if ((double)f2 < 1.0D)
        {
            this.myEntitySize = EnumEntitySize.SIZE_3;
        }
        else if ((double)f2 < 1.375D)
        {
            this.myEntitySize = EnumEntitySize.SIZE_4;
        }
        else if ((double)f2 < 1.75D)
        {
            this.myEntitySize = EnumEntitySize.SIZE_5;
        }
        else
        {
            this.myEntitySize = EnumEntitySize.SIZE_6;
        }
    }

    /**
     * Sets the rotation of the entity
     */
    protected void setRotation(float par1, float par2)
    {
        // CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
        float par11 = par1;
        float par21 = par2;
        if (Float.isNaN(par11))
        {
            par11 = 0;
        }

        if ((par11 == Float.POSITIVE_INFINITY) || (par11 == Float.NEGATIVE_INFINITY))
        {
            if (this instanceof EntityPlayerMP)
            {
                this.worldObj.getServer().getLogger().warning(((CraftPlayer) this.getBukkitEntity()).getName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Nope");
            }

            par11 = 0;
        }

        // pitch was sometimes set to NaN, so we need to set it back to 0.
        if (Float.isNaN(par21))
        {
            par21 = 0;
        }

        if ((par21 == Float.POSITIVE_INFINITY) || (par21 == Float.NEGATIVE_INFINITY))
        {
            if (this instanceof EntityPlayerMP)
            {
                this.worldObj.getServer().getLogger().warning(((CraftPlayer) this.getBukkitEntity()).getName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Nope");
            }

            par21 = 0;
        }

        // CraftBukkit end
        this.rotationYaw = par11 % 360.0F;
        this.rotationPitch = par21 % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(final double par1, final double par3, final double par5)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        final float f = this.width / 2.0F;
        final float f1 = this.height;
        this.boundingBox.setBounds(par1 - (double)f, par3 - (double)this.yOffset + (double)this.ySize, par5 - (double)f, par1 + (double)f, par3 - (double)this.yOffset + (double)this.ySize + (double)f1, par5 + (double)f);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Adds par1*0.15 to the entity's yaw, and *subtracts* par2*0.15 from the pitch. Clamps pitch from -90 to 90. Both
     * arguments in degrees.
     */
    public void setAngles(final float par1, final float par2)
    {
        final float f2 = this.rotationPitch;
        final float f3 = this.rotationYaw;
        this.rotationYaw = (float)((double)this.rotationYaw + (double)par1 * 0.15D);
        this.rotationPitch = (float)((double)this.rotationPitch - (double)par2 * 0.15D);

        if (this.rotationPitch < -90.0F)
        {
            this.rotationPitch = -90.0F;
        }

        if (this.rotationPitch > 90.0F)
        {
            this.rotationPitch = 90.0F;
        }

        this.prevRotationPitch += this.rotationPitch - f2;
        this.prevRotationYaw += this.rotationYaw - f3;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.onEntityUpdate();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        this.worldObj.theProfiler.startSection("entityBaseTick");

        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        int i;

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            final MinecraftServer minecraftserver = ((WorldServer)this.worldObj).getMinecraftServer();
            i = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (true || minecraftserver.getAllowNether())   // CraftBukkit
                {
                    if (this.ridingEntity == null && this.portalCounter++ >= i)
                    {
                        this.portalCounter = i;
                        this.timeUntilPortal = this.getPortalCooldown();
                        final byte b0;

                        if (this.worldObj.provider.dimensionId == -1)
                        {
                            b0 = 0;
                        }
                        else
                        {
                            b0 = -1;
                        }

                        this.travelToDimension(b0);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }

        if (this.isSprinting() && !this.isInWater())
        {
            final int j = MathHelper.floor_double(this.posX);
            i = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            final int k = MathHelper.floor_double(this.posZ);
            final int l = this.worldObj.getBlockId(j, i, k);

            if (l > 0)
            {
                this.worldObj.spawnParticle("tilecrack_" + l + "_" + this.worldObj.getBlockMetadata(j, i, k), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D);
            }
        }

        this.handleWaterMovement();

        if (this.worldObj.isRemote)
        {
            this.fire = 0;
        }
        else if (this.fire > 0)
        {
            if (this.isImmuneToFire)
            {
                this.fire -= 4;

                if (this.fire < 0)
                {
                    this.fire = 0;
                }
            }
            else
            {
                if (this.fire % 20 == 0)
                {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                }

                --this.fire;
            }
        }

        if (this.handleLavaMovement())
        {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (!this.worldObj.isRemote)
        {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime()
    {
        return 0;
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void setOnFireFromLava()
    {
        if (!this.isImmuneToFire)
        {
            // CraftBukkit start - Fallen in lava TODO: this event spams!
            if (this instanceof EntityLivingBase)
            {
                final Server server = this.worldObj.getServer();
                // TODO: shouldn't be sending null for the block.
                final org.bukkit.block.Block damager = null; // ((WorldServer) this.l).getWorld().getBlockAt(i, j, k);
                final org.bukkit.entity.Entity damagee = this.getBukkitEntity();
                final EntityDamageByBlockEvent event = new EntityDamageByBlockEvent(damager, damagee, EntityDamageEvent.DamageCause.LAVA, 4.0D);
                server.getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    damagee.setLastDamageCause(event);
                    this.attackEntityFrom(DamageSource.lava, (float) event.getDamage());
                }

                if (this.fire <= 0)
                {
                    // not on fire yet
                    final EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
                    server.getPluginManager().callEvent(combustEvent);

                    if (!combustEvent.isCancelled())
                    {
                        this.setFire(combustEvent.getDuration());
                    }
                }
                else
                {
                    // This will be called every single tick the entity is in lava, so don't throw an event
                    this.setFire(15);
                }

                return;
            }

            // CraftBukkit end - we also don't throw an event unless the object in lava is living, to save on some event calls
            this.attackEntityFrom(DamageSource.lava, 4.0F);
            this.setFire(15);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setFire(final int par1)
    {
        int j = par1 * 20;
        j = EnchantmentProtection.getFireTimeForEntity(this, j);

        if (this.fire < j)
        {
            this.fire = j;
        }
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.fire = 0;
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.setDead();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     */
    public boolean isOffsetPositionInLiquid(final double par1, final double par3, final double par5)
    {
        final AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
        final List list = this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb);
        return !list.isEmpty() ? false : !this.worldObj.isAnyLiquid(axisalignedbb);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        // CraftBukkit start - Don't do anything if we aren't moving
        double par11 = par1;
        double par31 = par3;
        double par51 = par5;
        if (par11 == 0 && par31 == 0 && par51 == 0 &&
                this.ridingEntity == null && this.riddenByEntity == null)
            return;
        if (!CauldronHooks.checkEntitySpeed(this, par11, par31, par51)) return; // Check for entities violating the speed limit

        // CraftBukkit end
        SpigotTimings.entityMoveTimer.startTiming(); // Spigot

        if (this.noClip) {
            this.boundingBox.offset(par11, par31, par51);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else {
            this.worldObj.theProfiler.startSection("move");
            this.ySize *= 0.4F;
            final double d3 = this.posX;
            final double d4 = this.posY;
            final double d5 = this.posZ;

            if (this.isInWeb) {
                this.isInWeb = false;
                par11 *= 0.25D;
                par31 *= 0.05000000074505806D;
                par51 *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double d6 = par11;
            final double d7 = par31;
            double d8 = par51;
            final AxisAlignedBB axisalignedbb = this.boundingBox.copy();
            final boolean flag = this.onGround && this.isSneaking() && this instanceof EntityPlayer;

            if (flag) {
                final double d9;

                for (d9 = 0.05D; par11 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par11, -1.0D, 0.0D)).isEmpty(); d6 = par11)
                {
                    if (par11 < d9 && par11 >= -d9)
                    {
                        par11 = 0.0D;
                    }
                    else if (par11 > 0.0D)
                    {
                        par11 -= d9;
                    }
                    else
                    {
                        par11 += d9;
                    }
                }

                for (; par51 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, par51)).isEmpty(); d8 = par51)
                {
                    if (par51 < d9 && par51 >= -d9)
                    {
                        par51 = 0.0D;
                    }
                    else if (par51 > 0.0D)
                    {
                        par51 -= d9;
                    }
                    else
                    {
                        par51 += d9;
                    }
                }

                while (par11 != 0.0D && par51 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par11, -1.0D, par51)).isEmpty()) {
                    if (par11 < d9 && par11 >= -d9) {
                        par11 = 0.0D;
                    }
                    else if (par11 > 0.0D) {
                        par11 -= d9;
                    }
                    else {
                        par11 += d9;
                    }

                    if (par51 < d9 && par51 >= -d9)
                    {
                        par51 = 0.0D;
                    }
                    else if (par51 > 0.0D)
                    {
                        par51 -= d9;
                    }
                    else
                    {
                        par51 += d9;
                    }

                    d6 = par11;
                    d8 = par51;
                }
            }

            List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par11, par31, par51));

            try {
                for (int i = 0; i < list.size(); ++i) {
                    par31 = ((AxisAlignedBB) list.get(i)).calculateYOffset(this.boundingBox, par31);
                }
            } catch (final Throwable throwable) { }

            this.boundingBox.offset(0.0D, par31, 0.0D);

            if (!this.field_70135_K && d7 != par31) {
                par51 = 0.0D;
                par31 = 0.0D;
                par11 = 0.0D;
            }

            final boolean flag1 = !(!this.onGround && (d7 == par31 || d7 >= 0.0D));
            int j;

            try {
                for (j = 0; j < list.size(); ++j) {
                    par11 = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, par11);
                }

            this.boundingBox.offset(par11, 0.0D, 0.0D);

                if (!this.field_70135_K && d6 != par11) {
                    par51 = 0.0D;
                    par31 = 0.0D;
                    par11 = 0.0D;
                }

                for (j = 0; j < list.size(); ++j) {
                    par51 = ((AxisAlignedBB) list.get(j)).calculateZOffset(this.boundingBox, par51);
                }


                this.boundingBox.offset(0.0D, 0.0D, par51);

                if (!this.field_70135_K && d8 != par51) {
                    par51 = 0.0D;
                    par31 = 0.0D;
                    par11 = 0.0D;
                }

            } catch (final Throwable t) { }

            double d10;
            double d11;
            int k;
            double d12;

            if (this.stepHeight > 0.0F && flag1 && (flag || this.ySize < 0.05F) && (d6 != par11 || d8 != par51)) {
                d12 = par11;
                d10 = par31;
                d11 = par51;
                par11 = d6;
                par31 = this.stepHeight;
                par51 = d8;
                final AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
                this.boundingBox.setBB(axisalignedbb);
                list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d6, par31, d8));

                try {
                    for (k = 0; k < list.size(); ++k) {
                        par31 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par31);
                    }
                } catch (final Throwable t) {}

                this.boundingBox.offset(0.0D, par31, 0.0D);

                if (!this.field_70135_K && d7 != par31) {
                    par51 = 0.0D;
                    par31 = 0.0D;
                    par11 = 0.0D;
                }

                try {
                    for (k = 0; k < list.size(); ++k) {
                        par11 = ((AxisAlignedBB)list.get(k)).calculateXOffset(this.boundingBox, par11);
                    }
                } catch (final Throwable t) {}

                this.boundingBox.offset(par11, 0.0D, 0.0D);

                if (!this.field_70135_K && d6 != par11)
                {
                    par51 = 0.0D;
                    par31 = 0.0D;
                    par11 = 0.0D;
                }

                try {
                    for (k = 0; k < list.size(); ++k) {
                        par51 = ((AxisAlignedBB)list.get(k)).calculateZOffset(this.boundingBox, par51);
                    }

                    this.boundingBox.offset(0.0D, 0.0D, par51);
                } catch (final Throwable t) {}

                if (!this.field_70135_K && d8 != par51) {
                    par51 = 0.0D;
                    par31 = 0.0D;
                    par11 = 0.0D;
                }

                if (!this.field_70135_K && d7 != par31)
                {
                    par51 = 0.0D;
                    par31 = 0.0D;
                    par11 = 0.0D;
                }
                else
                {
                    par31 = (double)(-this.stepHeight);

                    try {
                        for (k = 0; k < list.size(); ++k) {
                            par31 = ((AxisAlignedBB) list.get(k)).calculateYOffset(this.boundingBox, par31);
                        }

                        this.boundingBox.offset(0.0D, par31, 0.0D);
                    } catch (final Throwable t) {}
                }

                if (d12 * d12 + d11 * d11 >= par11 * par11 + par51 * par51) {
                    par11 = d12;
                    par31 = d10;
                    par51 = d11;
                    this.boundingBox.setBB(axisalignedbb1);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            this.isCollidedHorizontally = d6 != par11 || d8 != par51;
            this.isCollidedVertically = d7 != par31;
            this.onGround = d7 != par31 && d7 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(par31, this.onGround);

            if (d6 != par11) {
                this.motionX = 0.0D;
            }

            if (d7 != par31) {
                this.motionY = 0.0D;
            }

            if (d8 != par51) {
                this.motionZ = 0.0D;
            }

            d12 = this.posX - d3;
            d10 = this.posY - d4;
            d11 = this.posZ - d5;

            // CraftBukkit start
            if ((this.isCollidedHorizontally) && (this.getBukkitEntity() instanceof Vehicle) && this.worldObj.getWorld() != null) // Cauldron - fixes MFR NPE with grinder/slaughterhouse
            {
                final Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.block.Block block = this.worldObj.getWorld().getBlockAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - (double) this.yOffset), MathHelper.floor_double(this.posZ));

                if (d6 > par11)
                {
                    block = block.getRelative(BlockFace.EAST);
                }
                else if (d6 < par11)
                {
                    block = block.getRelative(BlockFace.WEST);
                }
                else if (d8 > par51)
                {
                    block = block.getRelative(BlockFace.SOUTH);
                }
                else if (d8 < par51)
                {
                    block = block.getRelative(BlockFace.NORTH);
                }

                final VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, block);
                this.worldObj.getServer().getPluginManager().callEvent(event);
            }
            // CraftBukkit end
            if (this.canTriggerWalking() && !flag && this.ridingEntity == null)
            {
                final int l = MathHelper.floor_double(this.posX);
                k = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
                final int i1 = MathHelper.floor_double(this.posZ);
                int j1 = this.worldObj.getBlockId(l, k, i1);

                if (j1 == 0)
                {
                    final int k1 = this.worldObj.blockGetRenderType(l, k - 1, i1);

                    if (k1 == 11 || k1 == 32 || k1 == 21)
                    {
                        j1 = this.worldObj.getBlockId(l, k - 1, i1);
                    }
                }

                if (j1 != Block.ladder.blockID)
                {
                    d10 = 0.0D;
                }

                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(d12 * d12 + d11 * d11) * 0.6D);
                this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(d12 * d12 + d10 * d10 + d11 * d11) * 0.6D);

                if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && j1 > 0)
                {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;

                    if (this.isInWater())
                    {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F)
                        {
                            f = 1.0F;
                        }

                        this.playSound("liquid.swim", f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.playStepSound(l, k, i1, j1);
                    Block.blocksList[j1].onEntityWalking(this.worldObj, l, k, i1, this);
                }
            }

            try
            {
                this.doBlockCollisions();
            }
            catch (final Throwable throwable)
            {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity tile collision");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            final boolean flag2 = this.isWet();

            if (this.worldObj.isBoundingBoxBurning(this.boundingBox.contract(0.001D, 0.001D, 0.001D)))
            {
                this.burn(1);

                if (!flag2)
                {
                    ++this.fire;

                    // CraftBukkit start - Not on fire yet
                    if (this.fire <= 0)   // Only throw events on the first combust, otherwise it spams
                    {
                        final EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), 8);
                        this.worldObj.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled())
                        {
                            this.setFire(event.getDuration());
                        }
                    }
                    else
                    {
                        // CraftBukkit end
                        this.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0)
            {
                this.fire = -this.fireResistance;
            }

            if (flag2 && this.fire > 0)
            {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }

        SpigotTimings.entityMoveTimer.stopTiming(); // Spigot
    }

    /**
     * Checks for block collisions, and calls the associated onBlockCollided method for the collided block.
     */
    protected void doBlockCollisions()
    {
        final int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
        final int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
        final int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
        final int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
        final int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
        final int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);

        if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1))
        {
            for (int k1 = i; k1 <= l; ++k1)
            {
                for (int l1 = j; l1 <= i1; ++l1)
                {
                    for (int i2 = k; i2 <= j1; ++i2)
                    {
                        final int j2 = this.worldObj.getBlockId(k1, l1, i2);

                        if (j2 > 0)
                        {
                            try
                            {
                                Block.blocksList[j2].onEntityCollidedWithBlock(this.worldObj, k1, l1, i2, this);
                            }
                            catch (final Throwable throwable)
                            {
                                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with tile");
                                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Tile being collided with");
                                CrashReportCategory.addBlockCrashInfo(crashreportcategory, k1, l1, i2, j2, this.worldObj.getBlockMetadata(k1, l1, i2));
                                throw new ReportedException(crashreport);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(final int par1, final int par2, final int par3, final int par4)
    {
        StepSound stepsound = Block.blocksList[par4].stepSound;

        if (this.worldObj.getBlockId(par1, par2 + 1, par3) == Block.snow.blockID)
        {
            stepsound = Block.snow.stepSound;
            this.playSound(stepsound.getStepSound(), stepsound.getVolume() * 0.15F, stepsound.getPitch());
        }
        else if (!Block.blocksList[par4].blockMaterial.isLiquid())
        {
            this.playSound(stepsound.getStepSound(), stepsound.getVolume() * 0.15F, stepsound.getPitch());
        }
    }

    public void playSound(final String par1Str, final float par2, final float par3)
    {
        this.worldObj.playSoundAtEntity(this, par1Str, par2, par3);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return true;
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(final double par1, final boolean par3)
    {
        if (par3)
        {
            if (this.fallDistance > 0.0F)
            {
                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        }
        else if (par1 < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - par1);
        }
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }

    // Cauldron start - vanilla compatibility
    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(final int par1)
    {
        this.burn((float)par1);
    }
    // Cauldron end

    protected void burn(final float i)   // CraftBukkit - int -> float
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.inFire, (float) i);
        }
    }

    public final boolean isImmuneToFire()
    {
        return this.isImmuneToFire;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(final float par1)
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.fall(par1);
        }
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean isWet()
    {
        return this.inWater || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ));
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean isInWater()
    {
        return this.inWater;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                this.playSound("liquid.splash", f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                final float f1 = (float)MathHelper.floor_double(this.boundingBox.minY);
                int i;
                float f2;
                float f3;

                for (i = 0; (float)i < 1.0F + this.width * 20.0F; ++i)
                {
                    f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("bubble", this.posX + (double)f2, (double)(f1 + 1.0F), this.posZ + (double)f3, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
                }

                for (i = 0; (float)i < 1.0F + this.width * 20.0F; ++i)
                {
                    f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + (double)f2, (double)(f1 + 1.0F), this.posZ + (double)f3, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        }
        else
        {
            this.inWater = false;
        }

        return this.inWater;
    }

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    public boolean isInsideOfMaterial(final Material par1Material)
    {
        final double d0 = this.posY + (double)this.getEyeHeight();
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_float((float)MathHelper.floor_double(d0));
        final int k = MathHelper.floor_double(this.posZ);
        final int l = this.worldObj.getBlockId(i, j, k);

        final Block block = Block.blocksList[l];
        if (block != null && block.blockMaterial == par1Material)
        {
            double filled = block.getFilledPercentage(worldObj, i, j, k);
            if (filled < 0)
            {
                filled *= -1;
                //filled -= 0.11111111F; //Why this is needed.. not sure...
                return d0 > (double)(j + (1 - filled));
            }
            else
            {
                return d0 < (double)(j + filled);
            }
        }
        else
        {
            return false;
        }
    }

    public float getEyeHeight()
    {
        return 0.0F;
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean handleLavaMovement()
    {
        return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }

    /**
     * Used in both water and by flying objects
     */
    public void moveFlying(float par1, float par2, final float par3)
    {
        float par11 = par1;
        float par21 = par2;
        float f3 = par11 * par11 + par21 * par21;

        if (f3 >= 1.0E-4F)
        {
            f3 = MathHelper.sqrt_float(f3);

            if (f3 < 1.0F)
            {
                f3 = 1.0F;
            }

            f3 = par3 / f3;
            par11 *= f3;
            par21 *= f3;
            final float f4 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
            final float f5 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
            this.motionX += (double)(par11 * f5 - par21 * f4);
            this.motionZ += (double)(par21 * f5 + par11 * f4);
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(final float par1)
    {
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(i, 0, j))
        {
            final double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            final int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
            return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(final float par1)
    {
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(i, 0, j))
        {
            final double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            final int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
            return this.worldObj.getLightBrightness(i, k, j);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(final World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Sets the entity's position and rotation. Args: posX, posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation(final double par1, final double par3, final double par5, final float par7, final float par8)
    {
        this.prevPosX = this.posX = par1;
        this.prevPosY = this.posY = par3;
        this.prevPosZ = this.posZ = par5;
        this.prevRotationYaw = this.rotationYaw = par7;
        this.prevRotationPitch = this.rotationPitch = par8;
        this.ySize = 0.0F;
        final double d3 = (double)(this.prevRotationYaw - par7);

        if (d3 < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }

        if (d3 >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(par7, par8);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(final double par1, final double par3, final double par5, final float par7, final float par8)
    {
        this.lastTickPosX = this.prevPosX = this.posX = par1;
        this.lastTickPosY = this.prevPosY = this.posY = par3 + (double)this.yOffset;
        this.lastTickPosZ = this.prevPosZ = this.posZ = par5;
        this.rotationYaw = par7;
        this.rotationPitch = par8;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float getDistanceToEntity(final Entity par1Entity)
    {
        final float f = (float)(this.posX - par1Entity.posX);
        final float f1 = (float)(this.posY - par1Entity.posY);
        final float f2 = (float)(this.posZ - par1Entity.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    /**
     * Gets the squared distance to the position. Args: x, y, z
     */
    public double getDistanceSq(final double par1, final double par3, final double par5)
    {
        final double d3 = this.posX - par1;
        final double d4 = this.posY - par3;
        final double d5 = this.posZ - par5;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    /**
     * Gets the distance to the position. Args: x, y, z
     */
    public double getDistance(final double par1, final double par3, final double par5)
    {
        final double d3 = this.posX - par1;
        final double d4 = this.posY - par3;
        final double d5 = this.posZ - par5;
        return (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
    }

    /**
     * Returns the squared distance to the entity. Args: entity
     */
    public double getDistanceSqToEntity(final Entity par1Entity)
    {
        final double d0 = this.posX - par1Entity.posX;
        final double d1 = this.posY - par1Entity.posY;
        final double d2 = this.posZ - par1Entity.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(final EntityPlayer par1EntityPlayer) {}

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(final Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != this && par1Entity.ridingEntity != this)
        {
            double d0 = par1Entity.posX - this.posX;
            double d1 = par1Entity.posZ - this.posZ;
            double d2 = MathHelper.abs_max(d0, d1);

            if (d2 >= 0.009999999776482582D)
            {
                d2 = (double)MathHelper.sqrt_double(d2);
                d0 /= d2;
                d1 /= d2;
                double d3 = 1.0D / d2;

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                d0 *= d3;
                d1 *= d3;
                d0 *= 0.05000000074505806D;
                d1 *= 0.05000000074505806D;
                d0 *= (double)(1.0F - this.entityCollisionReduction);
                d1 *= (double)(1.0F - this.entityCollisionReduction);
                this.addVelocity(-d0, 0.0D, -d1);
                par1Entity.addVelocity(d0, 0.0D, d1);
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(final double par1, final double par3, final double par5)
    {
        this.motionX += par1;
        this.motionY += par3;
        this.motionZ += par5;
        this.isAirBorne = true;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        this.velocityChanged = true;
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
            this.setBeenAttacked();
            return false;
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
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(final Entity par1Entity, final int par2) {}

    @SideOnly(Side.CLIENT)

    /**
     * Checks using a Vec3d to determine if this entity is within range of that vector to be rendered. Args: vec3D
     */
    public boolean isInRangeToRenderVec3D(final Vec3 par1Vec3)
    {
        final double d0 = this.posX - par1Vec3.xCoord;
        final double d1 = this.posY - par1Vec3.yCoord;
        final double d2 = this.posZ - par1Vec3.zCoord;
        final double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        return this.isInRangeToRenderDist(d3);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(final double par1)
    {
        double d1 = this.boundingBox.getAverageEdgeLength();
        d1 *= 64.0D * this.renderDistanceWeight;
        return par1 < d1 * d1;
    }

    /**
     * Like writeToNBTOptional but does not check if the entity is ridden. Used for saving ridden entities with their
     * riders.
     */
    public boolean writeMountToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        final String s = this.getEntityString();

        if (!this.isDead && s != null)
        {
            par1NBTTagCompound.setString("id", s);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(final NBTTagCompound par1NBTTagCompound)
    {
        final String s = this.getEntityString();

        if (!this.isDead && s != null && this.riddenByEntity == null)
        {
            par1NBTTagCompound.setString("id", s);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        try
        {
            par1NBTTagCompound.setTag("Pos", this.newDoubleNBTList(new double[] {this.posX, this.posY + (double)this.ySize, this.posZ}));
            par1NBTTagCompound.setTag("Motion", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));

            // CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
            // TODO: make sure this is the best way to address this.
            if (Float.isNaN(this.rotationYaw))
            {
                this.rotationYaw = 0;
            }

            if (Float.isNaN(this.rotationPitch))
            {
                this.rotationPitch = 0;
            }

            // CraftBukkit end
            par1NBTTagCompound.setTag("Rotation", this.newFloatNBTList(new float[] {this.rotationYaw, this.rotationPitch}));
            par1NBTTagCompound.setFloat("FallDistance", this.fallDistance);
            par1NBTTagCompound.setShort("Fire", (short)this.fire);
            par1NBTTagCompound.setShort("Air", (short)this.getAir());
            par1NBTTagCompound.setBoolean("OnGround", this.onGround);
            par1NBTTagCompound.setInteger("Dimension", this.dimension);
            par1NBTTagCompound.setBoolean("Invulnerable", this.invulnerable);
            par1NBTTagCompound.setInteger("PortalCooldown", this.timeUntilPortal);
            par1NBTTagCompound.setLong("UUIDMost", this.entityUniqueID.getMostSignificantBits());
            par1NBTTagCompound.setLong("UUIDLeast", this.entityUniqueID.getLeastSignificantBits());
            // CraftBukkit start
            par1NBTTagCompound.setLong("WorldUUIDLeast", this.worldObj.getSaveHandler().getUUID().getLeastSignificantBits());
            par1NBTTagCompound.setLong("WorldUUIDMost", this.worldObj.getSaveHandler().getUUID().getMostSignificantBits());
            par1NBTTagCompound.setInteger("Bukkit.updateLevel", CURRENT_LEVEL);
            // CraftBukkit end
            if (customEntityData != null)
            {
                par1NBTTagCompound.setCompoundTag("ForgeData", customEntityData);
            }

            for (final String identifier : this.extendedProperties.keySet()){
                try{
                    final IExtendedEntityProperties props = this.extendedProperties.get(identifier);
                    props.saveNBTData(par1NBTTagCompound);
                }catch (final Throwable t){
                    FMLLog.severe("Failed to save extended properties for %s.  This is a mod issue.", identifier);
                    t.printStackTrace();
                }
            }

            this.writeEntityToNBT(par1NBTTagCompound);

            if (this.ridingEntity != null)
            {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound("Riding");

                if (this.ridingEntity.writeMountToNBT(nbttagcompound1))
                {
                    par1NBTTagCompound.setTag("Riding", nbttagcompound1);
                }
            }
        }
        catch (final Throwable throwable)
        {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being saved");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        try
        {
            final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Pos");
            final NBTTagList nbttaglist1 = par1NBTTagCompound.getTagList("Motion");
            final NBTTagList nbttaglist2 = par1NBTTagCompound.getTagList("Rotation");
            this.motionX = ((NBTTagDouble)nbttaglist1.tagAt(0)).data;
            this.motionY = ((NBTTagDouble)nbttaglist1.tagAt(1)).data;
            this.motionZ = ((NBTTagDouble)nbttaglist1.tagAt(2)).data;
            /* CraftBukkit start - Moved section down
            if (Math.abs(this.motionX) > 10.0D)
            {
                this.motionX = 0.0D;
            }

            if (Math.abs(this.motionY) > 10.0D)
            {
                this.motionY = 0.0D;
            }

            if (Math.abs(this.motionZ) > 10.0D)
            {
                this.motionZ = 0.0D;
            }
            // CraftBukkit end */
            this.prevPosX = this.lastTickPosX = this.posX = ((NBTTagDouble)nbttaglist.tagAt(0)).data;
            this.prevPosY = this.lastTickPosY = this.posY = ((NBTTagDouble)nbttaglist.tagAt(1)).data;
            this.prevPosZ = this.lastTickPosZ = this.posZ = ((NBTTagDouble)nbttaglist.tagAt(2)).data;
            this.prevRotationYaw = this.rotationYaw = ((NBTTagFloat)nbttaglist2.tagAt(0)).data;
            this.prevRotationPitch = this.rotationPitch = ((NBTTagFloat)nbttaglist2.tagAt(1)).data;
            this.fallDistance = par1NBTTagCompound.getFloat("FallDistance");
            this.fire = par1NBTTagCompound.getShort("Fire");
            this.setAir(par1NBTTagCompound.getShort("Air"));
            this.onGround = par1NBTTagCompound.getBoolean("OnGround");
            this.dimension = par1NBTTagCompound.getInteger("Dimension");
            this.invulnerable = par1NBTTagCompound.getBoolean("Invulnerable");
            this.timeUntilPortal = par1NBTTagCompound.getInteger("PortalCooldown");

            if (par1NBTTagCompound.hasKey("UUIDMost") && par1NBTTagCompound.hasKey("UUIDLeast"))
            {
                this.entityUniqueID = new UUID(par1NBTTagCompound.getLong("UUIDMost"), par1NBTTagCompound.getLong("UUIDLeast"));
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            if (par1NBTTagCompound.hasKey("ForgeData"))
            {
                customEntityData = par1NBTTagCompound.getCompoundTag("ForgeData");
            }

            for (final String identifier : this.extendedProperties.keySet()){
                try{
                    final IExtendedEntityProperties props = this.extendedProperties.get(identifier);
                    props.loadNBTData(par1NBTTagCompound);
                }catch (final Throwable t){
                    FMLLog.severe("Failed to load extended properties for %s.  This is a mod issue.", identifier);
                    t.printStackTrace();
                }
            }

            //Rawr, legacy code, Vanilla added a UUID, keep this so older maps will convert properly
            if (par1NBTTagCompound.hasKey("PersistentIDMSB") && par1NBTTagCompound.hasKey("PersistentIDLSB"))
            {
                this.entityUniqueID = new UUID(par1NBTTagCompound.getLong("PersistentIDMSB"), par1NBTTagCompound.getLong("PersistentIDLSB"));
            }
            this.readEntityFromNBT(par1NBTTagCompound);

            // CraftBukkit start
            if (this instanceof EntityLivingBase)
            {
                final EntityLivingBase entity = (EntityLivingBase) this;

                // Reset the persistence for tamed animals
                if (entity instanceof EntityTameable && !isLevelAtLeast(par1NBTTagCompound, 2) && !par1NBTTagCompound.getBoolean("PersistenceRequired"))
                {
                    final EntityLiving entityliving = (EntityLiving) entity;
                    entityliving.persistenceRequired = !entityliving.canDespawn();
                }
            }

            // CraftBukkit end

            // CraftBukkit start - Exempt Vehicles from notch's sanity check
            if (!(this.getBukkitEntity() instanceof Vehicle))
            {
                if (Math.abs(this.motionX) > 10.0D)
                {
                    this.motionX = 0.0D;
                }

                if (Math.abs(this.motionY) > 10.0D)
                {
                    this.motionY = 0.0D;
                }

                if (Math.abs(this.motionZ) > 10.0D)
                {
                    this.motionZ = 0.0D;
                }
            }

            // CraftBukkit end

            // CraftBukkit start - Reset world
            if (this instanceof EntityPlayerMP)
            {
                final Server server = Bukkit.getServer();
                org.bukkit.World bworld = null;
                // TODO: Remove World related checks, replaced with WorldUID.
                final String worldName = par1NBTTagCompound.getString("World");

                if (par1NBTTagCompound.hasKey("WorldUUIDMost") && par1NBTTagCompound.hasKey("WorldUUIDLeast"))
                {
                    final UUID uid = new UUID(par1NBTTagCompound.getLong("WorldUUIDMost"), par1NBTTagCompound.getLong("WorldUUIDLeast"));
                    bworld = server.getWorld(uid);
                }
                else
                {
                    bworld = server.getWorld(worldName);
                }

                if (bworld == null)
                {
                    final EntityPlayerMP entityPlayer = (EntityPlayerMP) this;
                    // Cauldron start - use CraftBukkit's fallback world code if no valid world is found.
                    entityPlayer.setWorld(MinecraftServer.getServer().worldServerForDimension(entityPlayer.dimension));
                }
                else
                {
                    this.setWorld(((CraftWorld) bworld).getHandle());
                    // Cauldron end
                }
            }

            // CraftBukkit end
        }
        catch (final Throwable throwable)
        {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being loaded");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean shouldSetPosAfterLoading()
    {
        return true;
    }

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String getEntityString()
    {
        return EntityList.getEntityString(this);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void readEntityFromNBT(NBTTagCompound nbttagcompound);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected abstract void writeEntityToNBT(NBTTagCompound nbttagcompound);

    public void onChunkLoad() {}

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected NBTTagList newDoubleNBTList(final double ... par1ArrayOfDouble)
    {
        final NBTTagList nbttaglist = new NBTTagList();
        final double[] adouble = par1ArrayOfDouble;
        final int i = par1ArrayOfDouble.length;

        for (int j = 0; j < i; ++j)
        {
            final double d1 = adouble[j];
            nbttaglist.appendTag(new NBTTagDouble((String)null, d1));
        }

        return nbttaglist;
    }

    /**
     * Returns a new NBTTagList filled with the specified floats
     */
    protected NBTTagList newFloatNBTList(final float ... par1ArrayOfFloat)
    {
        final NBTTagList nbttaglist = new NBTTagList();
        final float[] afloat = par1ArrayOfFloat;
        final int i = par1ArrayOfFloat.length;

        for (int j = 0; j < i; ++j)
        {
            final float f1 = afloat[j];
            nbttaglist.appendTag(new NBTTagFloat((String)null, f1));
        }

        return nbttaglist;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return this.height / 2.0F;
    }

    /**
     * Drops an item stack at the entity's position. Args: itemID, count
     */
    public EntityItem dropItem(final int par1, final int par2)
    {
        return this.dropItemWithOffset(par1, par2, 0.0F);
    }

    /**
     * Drops an item stack with a specified y offset. Args: itemID, count, yOffset
     */
    public EntityItem dropItemWithOffset(final int par1, final int par2, final float par3)
    {
        return this.entityDropItem(new ItemStack(par1, par2, 0), par3);
    }

    /**
     * Drops an item at the position of the entity.
     */
    public EntityItem entityDropItem(final ItemStack par1ItemStack, final float par2)
    {
        if (par1ItemStack.stackSize == 0)
        {
            return null;
        }
        else
        {
            final EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + (double)par2, this.posZ, par1ItemStack);
            entityitem.delayBeforeCanPickup = 10;
            if (captureDrops)
            {
                capturedDrops.add(entityitem);
            }
            else
            {
                this.worldObj.spawnEntityInWorld(entityitem);
            }
            return entityitem;
        }
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        for (int i = 0; i < 8; ++i)
        {
            final float f = ((float)((i >> 0) % 2) - 0.5F) * this.width * 0.8F;
            final float f1 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
            final float f2 = ((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F;
            final int j = MathHelper.floor_double(this.posX + (double)f);
            final int k = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)f1);
            final int l = MathHelper.floor_double(this.posZ + (double)f2);

            if (this.worldObj.isBlockNormalCube(j, k, l))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(final EntityPlayer par1EntityPlayer)
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(final Entity par1Entity)
    {
        return null;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        if (this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();

            if (this.ridingEntity != null)
            {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

                for (this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D)
                {
                    ;
                }

                while (this.entityRiderYawDelta < -180.0D)
                {
                    this.entityRiderYawDelta += 360.0D;
                }

                while (this.entityRiderPitchDelta >= 180.0D)
                {
                    this.entityRiderPitchDelta -= 360.0D;
                }

                while (this.entityRiderPitchDelta < -180.0D)
                {
                    this.entityRiderPitchDelta += 360.0D;
                }

                double d0 = this.entityRiderYawDelta * 0.5D;
                double d1 = this.entityRiderPitchDelta * 0.5D;
                final float f = 10.0F;

                if (d0 > (double)f)
                {
                    d0 = (double)f;
                }

                if (d0 < (double)(-f))
                {
                    d0 = (double)(-f);
                }

                if (d1 > (double)f)
                {
                    d1 = (double)f;
                }

                if (d1 < (double)(-f))
                {
                    d1 = (double)(-f);
                }

                this.entityRiderYawDelta -= d0;
                this.entityRiderPitchDelta -= d1;
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return (double)this.yOffset;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.75D;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(final Entity par1Entity)
    {
        // CraftBukkit start
        this.setPassengerOf(par1Entity);
    }

    public CraftEntity bukkitEntity;

    public CraftEntity getBukkitEntity()
    {
        if (this.bukkitEntity == null)
        {
            this.bukkitEntity = CraftEntity.getEntity(this.worldObj.getServer(), this);
        }

        return this.bukkitEntity;
    }

    public void setPassengerOf(final Entity entity)
    {
        // mountEntity(null) doesn't really fly for overloaded methods,
        // so this method is needed
        final Entity originalVehicle = this.ridingEntity;
        final Entity originalPassenger = this.ridingEntity == null ? null : this.ridingEntity.riddenByEntity;
        final PluginManager pluginManager = Bukkit.getPluginManager();
        this.getBukkitEntity(); // make sure bukkitEntity is initialised
        // CraftBukkit end
        this.entityRiderPitchDelta = 0.0D;
        this.entityRiderYawDelta = 0.0D;

        if (entity == null)
        {
            if (this.ridingEntity != null)
            {
                // CraftBukkit start
                if ((this.bukkitEntity instanceof LivingEntity) && (this.ridingEntity.getBukkitEntity() instanceof Vehicle))
                {
                    final VehicleExitEvent event = new VehicleExitEvent((Vehicle) this.ridingEntity.getBukkitEntity(), (LivingEntity) this.bukkitEntity);
                    pluginManager.callEvent(event);

                    if (event.isCancelled() || this.ridingEntity != originalVehicle)
                    {
                        return;
                    }
                }
                // CraftBukkit end
                pluginManager.callEvent(new org.spigotmc.event.entity.EntityDismountEvent(this.getBukkitEntity(), this.ridingEntity.getBukkitEntity())); // Spigot

                this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else
        {
            // CraftBukkit start
            if ((this.bukkitEntity instanceof LivingEntity) && (entity.getBukkitEntity() instanceof Vehicle) && entity.worldObj.func_72916_c_CodeFix_Public((int) entity.posX >> 4, (int) entity.posZ >> 4))
            {
                // It's possible to move from one vehicle to another.  We need to check if they're already in a vehicle, and fire an exit event if they are.
                VehicleExitEvent exitEvent = null;
                if (this.ridingEntity != null && this.ridingEntity.getBukkitEntity() instanceof Vehicle)
                {
                    exitEvent = new VehicleExitEvent((Vehicle) this.ridingEntity.getBukkitEntity(), (LivingEntity) this.bukkitEntity);
                    pluginManager.callEvent(exitEvent);

                    if (exitEvent.isCancelled() || this.ridingEntity != originalVehicle || (this.ridingEntity != null && this.ridingEntity.riddenByEntity != originalPassenger))
                    {
                        return;
                    }
                }

                final VehicleEnterEvent event = new VehicleEnterEvent((Vehicle) entity.getBukkitEntity(), this.bukkitEntity);
                pluginManager.callEvent(event);

                // If a plugin messes with the vehicle or the vehicle's passenger
                if (event.isCancelled() || this.ridingEntity != originalVehicle || (this.ridingEntity != null && this.ridingEntity.riddenByEntity != originalPassenger))
                {
                    // If we only cancelled the enterevent then we need to put the player in a decent position.
                    if (exitEvent != null && this.ridingEntity == originalVehicle && this.ridingEntity != null && this.ridingEntity.riddenByEntity == originalPassenger)
                    {
                        this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double) this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                        this.ridingEntity.riddenByEntity = null;
                        this.ridingEntity = null;
                    }

                    return;
                }
            }
            // CraftBukkit end
            // Spigot Start
            if ( entity.worldObj.chunkExists( (int) entity.posX >> 4, (int) entity.posZ >> 4 ) )
            {
                final org.spigotmc.event.entity.EntityMountEvent event = new org.spigotmc.event.entity.EntityMountEvent( this.getBukkitEntity(), entity.getBukkitEntity() );
                pluginManager.callEvent( event );
                if ( event.isCancelled() )
                {
                    return;
                }
            }
            // Spigot End
            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = entity;
            entity.riddenByEntity = this;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(final double par1, double par3, final double par5, final float par7, final float par8, final int par9)
    {
        double par31 = par3;
        this.setPosition(par1, par31, par5);
        this.setRotation(par7, par8);
        final List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.contract(0.03125D, 0.0D, 0.03125D));

        if (!list.isEmpty())
        {
            double d3 = 0.0D;

            for (int j = 0; j < list.size(); ++j)
            {
                final AxisAlignedBB axisalignedbb = (AxisAlignedBB)list.get(j);

                if (axisalignedbb.maxY > d3)
                {
                    d3 = axisalignedbb.maxY;
                }
            }

            par31 += d3 - this.boundingBox.minY;
            this.setPosition(par1, par31, par5);
        }
    }

    public float getCollisionBorderSize()
    {
        return 0.1F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return null;
    }

    /**
     * Called by portal blocks when an entity is within it.
     */
    public void setInPortal()
    {
        if (this.timeUntilPortal > 0)
        {
            this.timeUntilPortal = this.getPortalCooldown();
        }
        else
        {
            final double d0 = this.prevPosX - this.posX;
            final double d1 = this.prevPosZ - this.posZ;

            if (!this.worldObj.isRemote && !this.inPortal)
            {
                this.teleportDirection = Direction.getMovementDirection(d0, d1);
            }

            this.inPortal = true;
        }
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 900;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(final double par1, final double par3, final double par5)
    {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1) {}

    @SideOnly(Side.CLIENT)

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation() {}

    public ItemStack[] getLastActiveItems()
    {
        return null;
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(final int par1, final ItemStack par2ItemStack) {}

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return !this.isImmuneToFire && (this.fire > 0 || this.getFlag(0));
    }

    /**
     * Returns true if the entity is riding another entity, used by render to rotate the legs to be in 'sit' position
     * for players.
     */
    public boolean isRiding()
    {
        return this.ridingEntity != null && ridingEntity.shouldRiderSit();
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.getFlag(1);
    }

    /**
     * Sets the sneaking flag.
     */
    public void setSneaking(final boolean par1)
    {
        this.setFlag(1, par1);
    }

    /**
     * Get if the Entity is sprinting.
     */
    public boolean isSprinting()
    {
        return this.getFlag(3);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(final boolean par1)
    {
        this.setFlag(3, par1);
    }

    public boolean isInvisible()
    {
        return this.getFlag(5);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Only used by renderer in EntityLivingBase subclasses.\nDetermines if an entity is visible or not to a specfic
     * player, if the entity is normally invisible.\nFor EntityLivingBase subclasses, returning false when invisible
     * will render the entity semitransparent.
     */
    public boolean isInvisibleToPlayer(final EntityPlayer par1EntityPlayer)
    {
        return this.isInvisible();
    }

    public void setInvisible(final boolean par1)
    {
        this.setFlag(5, par1);
    }

    @SideOnly(Side.CLIENT)
    public boolean isEating()
    {
        return this.getFlag(4);
    }

    public void setEating(final boolean par1)
    {
        this.setFlag(4, par1);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean getFlag(final int par1)
    {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << par1) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(final int par1, final boolean par2)
    {
        final byte b0 = this.dataWatcher.getWatchableObjectByte(0);

        if (par2)
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(b0 | 1 << par1)));
        }
        else
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(b0 & ~(1 << par1))));
        }
    }

    public int getAir()
    {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(final int par1)
    {
        this.dataWatcher.updateObject(1, Short.valueOf((short)par1));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void onStruckByLightning(final EntityLightningBolt par1EntityLightningBolt)
    {
        // CraftBukkit start
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        if (thisBukkitEntity == null) return; // Cauldron - skip mod entities with no wrapper (TODO: create a wrapper)
        if (par1EntityLightningBolt == null) return; // Cauldron - skip null entities, see #392
        final org.bukkit.entity.Entity stormBukkitEntity = par1EntityLightningBolt.getBukkitEntity();
        if (stormBukkitEntity == null) return; // Cauldron - skip mod entities with no wrapper (TODO: create a wrapper)
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (thisBukkitEntity instanceof Painting)
        {
            final PaintingBreakByEntityEvent event = new PaintingBreakByEntityEvent((Painting) thisBukkitEntity, stormBukkitEntity);
            pluginManager.callEvent(event);

            if (event.isCancelled())
            {
                return;
            }
        }

        final EntityDamageEvent event = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callEntityDamageEvent(par1EntityLightningBolt, this, EntityDamageEvent.DamageCause.LIGHTNING, 5.0D);

        if (event.isCancelled())
        {
            return;
        }

        this.burn((float) event.getDamage());
        // CraftBukkit end
        ++this.fire;

        if (this.fire == 0)
        {
            // CraftBukkit start - Call a combust event when lightning strikes
            final EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
            pluginManager.callEvent(entityCombustEvent);

            if (!entityCombustEvent.isCancelled())
            {
                this.setFire(entityCombustEvent.getDuration());
            }

            // CraftBukkit end
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(final EntityLivingBase par1EntityLivingBase) {}

    /**
     * Adds velocity to push the entity out of blocks at the specified x, y, z position Args: x, y, z
     */
    protected boolean pushOutOfBlocks(final double par1, final double par3, final double par5)
    {
        final int i = MathHelper.floor_double(par1);
        final int j = MathHelper.floor_double(par3);
        final int k = MathHelper.floor_double(par5);
        final double d3 = par1 - (double)i;
        final double d4 = par3 - (double)j;
        final double d5 = par5 - (double)k;
        final List list = this.worldObj.getCollidingBlockBounds(this.boundingBox);

        if (list.isEmpty() && !this.worldObj.isBlockFullCube(i, j, k))
        {
            return false;
        }
        else
        {
            final boolean flag = !this.worldObj.isBlockFullCube(i - 1, j, k);
            final boolean flag1 = !this.worldObj.isBlockFullCube(i + 1, j, k);
            final boolean flag2 = !this.worldObj.isBlockFullCube(i, j - 1, k);
            final boolean flag3 = !this.worldObj.isBlockFullCube(i, j + 1, k);
            final boolean flag4 = !this.worldObj.isBlockFullCube(i, j, k - 1);
            final boolean flag5 = !this.worldObj.isBlockFullCube(i, j, k + 1);
            byte b0 = 3;
            double d6 = 9999.0D;

            if (flag && d3 < d6)
            {
                d6 = d3;
                b0 = 0;
            }

            if (flag1 && 1.0D - d3 < d6)
            {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if (flag3 && 1.0D - d4 < d6)
            {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if (flag4 && d5 < d6)
            {
                d6 = d5;
                b0 = 4;
            }

            if (flag5 && 1.0D - d5 < d6)
            {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            final float f = this.rand.nextFloat() * 0.2F + 0.1F;

            if (b0 == 0)
            {
                this.motionX = (double)(-f);
            }

            if (b0 == 1)
            {
                this.motionX = (double)f;
            }

            if (b0 == 2)
            {
                this.motionY = (double)(-f);
            }

            if (b0 == 3)
            {
                this.motionY = (double)f;
            }

            if (b0 == 4)
            {
                this.motionZ = (double)(-f);
            }

            if (b0 == 5)
            {
                this.motionZ = (double)f;
            }

            return true;
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
        String s = EntityList.getEntityString(this);

        if (s == null)
        {
            s = "generic";
        }

        return StatCollector.translateToLocal("entity." + s + ".name");
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return null;
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(final Entity par1Entity)
    {
        return this == par1Entity;
    }

    public float getRotationYawHead()
    {
        return 0.0F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(final float par1) {}

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(final Entity par1Entity)
    {
        return false;
    }

    public String toString()
    {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] {this.getClass().getSimpleName(), this.getEntityName(), Integer.valueOf(this.entityId), this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(), Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)});
    }

    /**
     * Return whether this entity is invulnerable to damage.
     */
    public boolean isEntityInvulnerable()
    {
        return this.invulnerable;
    }

    /**
     * Sets this entity's location and angles to the location and angles of the passed in entity.
     */
    public void copyLocationAndAnglesFrom(final Entity par1Entity)
    {
        this.setLocationAndAngles(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
    }

    /**
     * Copies important data from another entity to this entity. Used when teleporting entities between worlds, as this
     * actually deletes the teleporting entity and re-creates it on the other side. Params: Entity to copy from, unused
     * (always true)
     */
    public void copyDataFrom(final Entity par1Entity, final boolean par2)
    {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        par1Entity.writeToNBT(nbttagcompound);
        this.readFromNBT(nbttagcompound);
        this.timeUntilPortal = par1Entity.timeUntilPortal;
        this.teleportDirection = par1Entity.teleportDirection;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(final int par1)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            this.worldObj.theProfiler.startSection("changeDimension");
            final MinecraftServer minecraftserver = MinecraftServer.getServer();
            // CraftBukkit start - Move logic into new function "teleportToLocation"
            // int j = this.dimension;
            // Cauldron start - Allow Forge hotloading on teleport
            final WorldServer exitWorld = minecraftserver.worldServerForDimension(par1);

            final Location enter = this.getBukkitEntity().getLocation();
            Location exit = exitWorld != null ? minecraftserver.getConfigurationManager().calculateTarget(enter, minecraftserver.worldServerForDimension(par1)) : null;
            final boolean useTravelAgent = exitWorld != null && !(this.dimension == 1 && exitWorld.dimension == 1); // don't use agent for custom worlds or return from THE_END
            // Cauldron start - check if teleporter is instance of TravelAgent before attempting to cast to it
            final Teleporter teleporter = exit != null ? ((CraftWorld) exit.getWorld()).getHandle().getDefaultTeleporter() : null;
            final TravelAgent agent = (teleporter != null && teleporter instanceof TravelAgent) ? (TravelAgent)teleporter : CraftTravelAgent.DEFAULT;  // return arbitrary TA to compensate for implementation dependent plugins
            // Cauldron end
            final EntityPortalEvent event = new EntityPortalEvent(this.getBukkitEntity(), enter, exit, agent);
            event.useTravelAgent(useTravelAgent);
            event.getEntity().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled() || event.getTo() == null || !this.isEntityAlive())
            {
                return;
            }

            exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
            this.teleportTo(exit, true);
        }
    }

    public void teleportTo(final Location exit, final boolean portal) {
        final WorldServer worldserver = ((CraftWorld) this.getBukkitEntity().getLocation().getWorld()).getHandle();
        final WorldServer worldserver1 = ((CraftWorld) exit.getWorld()).getHandle();
        final int i = worldserver1.dimension;
        // CraftBukkit end
        this.dimension = i;
        this.worldObj.removeEntity(this);
        this.isDead = false;
        this.worldObj.theProfiler.startSection("reposition");
        // CraftBukkit start - Ensure chunks are loaded in case TravelAgent is not used which would initially cause chunks to load during find/create
        // minecraftserver.getPlayerList().a(this, j, worldserver, worldserver1);
        final boolean before = worldserver1.theChunkProviderServer.loadChunkOnProvideRequest;  // Cauldron start - load chunks on provide request
        worldserver1.theChunkProviderServer.loadChunkOnProvideRequest = true;
        worldserver1.getMinecraftServer().getConfigurationManager().repositionEntity(this, exit, portal);
        worldserver1.theChunkProviderServer.loadChunkOnProvideRequest = before; // Cauldron end
        // CraftBukkit end
        this.worldObj.theProfiler.endStartSection("reloading");
        final Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldserver1);

        if (entity != null)
        {
            entity.copyDataFrom(this, true);
            worldserver1.spawnEntityInWorld(entity);
            // CraftBukkit start - Forward the CraftEntity to the new entity
            this.getBukkitEntity().setHandle(entity);
            entity.bukkitEntity = this.getBukkitEntity();
            // CraftBukkit end
        }

        this.isDead = true;
        this.worldObj.theProfiler.endSection();
        worldserver.resetUpdateEntityTick();
        worldserver1.resetUpdateEntityTick();
        this.worldObj.theProfiler.endSection();
    }

    /**
     * Gets a block's resistance to this entity's explosion. Used to make rails immune to TNT minecarts' explosions and
     * Wither skulls more destructive.
     */
    public float getBlockExplosionResistance(final Explosion par1Explosion, final World par2World, final int par3, final int par4, final int par5, final Block par6Block)
    {
        return par6Block.getExplosionResistance(this, par2World, par3, par4, par5, posX, posY + (double)getEyeHeight(), posZ);
    }

    public boolean shouldExplodeBlock(final Explosion par1Explosion, final World par2World, final int par3, final int par4, final int par5, final int par6, final float par7)
    {
        return true;
    }

    /**
     * The number of iterations PathFinder.getSafePoint will execute before giving up.
     */
    public int getMaxSafePointTries()
    {
        return 3;
    }

    public int getTeleportDirection()
    {
        return this.teleportDirection;
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return false;
    }

    public void addEntityCrashInfo(final CrashReportCategory par1CrashReportCategory)
    {
        par1CrashReportCategory.addCrashSectionCallable("Entity Type", new CallableEntityType(this));
        par1CrashReportCategory.addCrashSection("Entity ID", Integer.valueOf(this.entityId));
        par1CrashReportCategory.addCrashSectionCallable("Entity Name", new CallableEntityName(this));
        par1CrashReportCategory.addCrashSection("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)}));
        par1CrashReportCategory.addCrashSection("Entity\'s Block location", CrashReportCategory.getLocationInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)));
        par1CrashReportCategory.addCrashSection("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.motionX), Double.valueOf(this.motionY), Double.valueOf(this.motionZ)}));
    }

    @SideOnly(Side.CLIENT)

    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return this.isBurning();
    }

    public UUID getUniqueID()
    {
        return this.entityUniqueID;
    }

    public boolean isPushedByWater()
    {
        return true;
    }

    /**
     * Returns the translated name of the entity.
     */
    public String getTranslatedEntityName()
    {
        return this.getEntityName();
    }

    /* ================================== Forge Start =====================================*/
    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     * @return A NBTTagCompound
     */
    public NBTTagCompound getEntityData()
    {
        if (customEntityData == null)
        {
            customEntityData = new NBTTagCompound();
        }
        return customEntityData;
    }

    /**
     * Used in model rendering to determine if the entity riding this entity should be in the 'sitting' position.
     * @return false to prevent an entity that is mounted to this entity from displaying the 'sitting' animation.
     */
    public boolean shouldRiderSit()
    {
        return true;
    }

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickedResult(final MovingObjectPosition target)
    {
        if (this instanceof EntityPainting)
        {
            return new ItemStack(Item.painting);
        }
        else if (this instanceof EntityMinecart)
        {
            return ((EntityMinecart)this).getCartItem();
        }
        else if (this instanceof EntityBoat)
        {
            return new ItemStack(Item.boat);
        }
        else if (this instanceof EntityItemFrame)
        {
            final ItemStack held = ((EntityItemFrame)this).getDisplayedItem();
            if (held == null)
            {
                return new ItemStack(Item.itemFrame);
            }
            else
            {
                return held.copy();
            }
        }
        else if (this instanceof EntityLeashKnot)
        {
            return new ItemStack(Item.leash);
        }
        else
        {
            final int id = EntityList.getEntityID(this);
            if (id > 0 && EntityList.entityEggs.containsKey(id))
            {
                return new ItemStack(Item.monsterPlacer, 1, id);
            }
        }
        return null;
    }

    public UUID getPersistentID()
    {
        return entityUniqueID;
    }

    /**
     * Reset the entity ID to a new value. Not to be used from Mod code
     */
    public final void resetEntityId()
    {
        this.entityId = nextEntityID++;
    }

    public boolean shouldRenderInPass(final int pass)
    {
        return pass == 0;
    }

    /**
     * Returns true if the entity is of the @link{EnumCreatureType} provided
     * @param type The EnumCreatureType type this entity is evaluating
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    public boolean isCreatureType(final EnumCreatureType type, final boolean forSpawnCount)
    {
        return type.getCreatureClass().isAssignableFrom(this.getClass());
    }

    /**
     * Register the instance of IExtendedProperties into the entity's collection.
     * @param identifier The identifier which you can use to retrieve these properties for the entity.
     * @param properties The instanceof IExtendedProperties to register
     * @return The identifier that was used to register the extended properties.  Empty String indicates an error.  If your requested key already existed, this will return a modified one that is unique.
     */
    public String registerExtendedProperties(String identifier, final IExtendedEntityProperties properties)
    {
        String identifier1 = identifier;
        if (identifier1 == null)
        {
            FMLLog.warning("Someone is attempting to register extended properties using a null identifier.  This is not allowed.  Aborting.  This may have caused instability.");
            return "";
        }
        if (properties == null)
        {
            FMLLog.warning("Someone is attempting to register null extended properties.  This is not allowed.  Aborting.  This may have caused instability.");
            return "";
        }

        final String baseIdentifier = identifier1;
        int identifierModCount = 1;
        while (this.extendedProperties.containsKey(identifier1))
        {
            identifier1 = String.format("%s%d", baseIdentifier, identifierModCount++);
        }

        if (baseIdentifier != identifier1)
        {
            FMLLog.info("An attempt was made to register exended properties using an existing key.  The duplicate identifier (%s) has been remapped to %s.", baseIdentifier, identifier1);
        }

        this.extendedProperties.put(identifier1, properties);
        return identifier1;
    }

    /**
     * Gets the extended properties identified by the passed in key
     * @param identifier The key that identifies the extended properties.
     * @return The instance of IExtendedProperties that was found, or null.
     */
    public IExtendedEntityProperties getExtendedProperties(final String identifier)
    {
        return this.extendedProperties.get(identifier);
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the
     * ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    public boolean canRiderInteract()
    {
        return false;
    }

    /**
     * If the rider should be dismounted from the entity when the entity goes under water
     *
     * @param rider The entity that is riding
     * @return if the entity should be dismounted when under water
     */
    public boolean shouldDismountInWater(final Entity rider){
        return this instanceof EntityLivingBase;
    }
}
