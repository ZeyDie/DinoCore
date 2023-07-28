package net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IMinecartCollisionHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;

import java.util.List;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityMinecart extends Entity
{
    protected boolean isInReverse;
    protected final IUpdatePlayerListBox field_82344_g;
    protected String entityName;

    /** Minecart rotational logic matrix */
    protected static final int[][][] matrix = {{{0, 0, -1}, {0, 0, 1}}, {{ -1, 0, 0}, {1, 0, 0}}, {{ -1, -1, 0}, {1, 0, 0}}, {{ -1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, { -1, 0, 0}}, {{0, 0, -1}, { -1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};

    /** appears to be the progress of the turn */
    protected int turnProgress;
    protected double minecartX;
    protected double minecartY;
    protected double minecartZ;
    protected double minecartYaw;
    protected double minecartPitch;
    @SideOnly(Side.CLIENT)
    protected double velocityX;
    @SideOnly(Side.CLIENT)
    protected double velocityY;
    @SideOnly(Side.CLIENT)
    protected double velocityZ;

    // CraftBukkit start
    public boolean slowWhenEmpty = true;
    private double derailedX = 0.5;
    private double derailedY = 0.5;
    private double derailedZ = 0.5;
    private double flyingX = 0.95;
    private double flyingY = 0.95;
    private double flyingZ = 0.95;
    public double maxSpeed = 0.4D;
    // CraftBukkit end
    /* Forge: Minecart Compatibility Layer Integration. */
    public static float defaultMaxSpeedAirLateral = 0.4f;
    public static float defaultMaxSpeedAirVertical = -1.0f;
    public static double defaultDragAir = 0.94999998807907104D;
    protected boolean canUseRail = true;
    protected boolean canBePushed = true;
    private static IMinecartCollisionHandler collisionHandler = null;

    /* Instance versions of the above physics properties */
    private float currentSpeedRail = getMaxCartSpeedOnRail();
    protected float maxSpeedAirLateral = defaultMaxSpeedAirLateral;
    protected float maxSpeedAirVertical = defaultMaxSpeedAirVertical;
    protected double dragAir = defaultDragAir;

    public EntityMinecart(final World par1World)
    {
        super(par1World);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
        this.yOffset = this.height / 2.0F;
        this.field_82344_g = par1World != null ? par1World.getMinecartSoundUpdater(this) : null;
    }

    /**
     * Creates a new minecart of the specified type in the specified location in the given world. par0World - world to
     * create the minecart in, double par1,par3,par5 represent x,y,z respectively. int par7 specifies the type: 1 for
     * MinecartChest, 2 for MinecartFurnace, 3 for MinecartTNT, 4 for MinecartMobSpawner, 5 for MinecartHopper and 0 for
     * a standard empty minecart
     */
    public static EntityMinecart createMinecart(final World par0World, final double par1, final double par3, final double par5, final int par7)
    {
        switch (par7)
        {
            case 1:
                return new EntityMinecartChest(par0World, par1, par3, par5);
            case 2:
                return new EntityMinecartFurnace(par0World, par1, par3, par5);
            case 3:
                return new EntityMinecartTNT(par0World, par1, par3, par5);
            case 4:
                return new EntityMinecartMobSpawner(par0World, par1, par3, par5);
            case 5:
                return new EntityMinecartHopper(par0World, par1, par3, par5);
            default:
                return new EntityMinecartEmpty(par0World, par1, par3, par5);
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Float(0.0F));
        this.dataWatcher.addObject(20, new Integer(0));
        this.dataWatcher.addObject(21, new Integer(6));
        this.dataWatcher.addObject(22, Byte.valueOf((byte)0));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(final Entity par1Entity)
    {
        if (getCollisionHandler() != null)
        {
            return getCollisionHandler().getCollisionBox(this, par1Entity);
        }
        return par1Entity.canBePushed() ? par1Entity.boundingBox : null;
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        if (getCollisionHandler() != null)
        {
            return getCollisionHandler().getBoundingBox(this);
        }
        return null;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return canBePushed;
    }

    public EntityMinecart(final World par1World, final double par2, final double par4, final double par6)
    {
        this(par1World);
        this.setPosition(par2, par4, par6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
        this.worldObj.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleCreateEvent((Vehicle) this.getBukkitEntity())); // CraftBukkit
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.0D - 0.30000001192092896D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, float par2)
    {
        float par21 = par2;
        if (!this.worldObj.isRemote && !this.isDead)
        {
            if (this.isEntityInvulnerable())
            {
                return false;
            }
            else
            {
                // CraftBukkit start
                final Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                final org.bukkit.entity.Entity passenger = (par1DamageSource.getEntity() == null) ? null : par1DamageSource.getEntity().getBukkitEntity();
                final VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, par21);
                this.worldObj.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled())
                {
                    return true;
                }

                par21 = (float) event.getDamage();
                // CraftBukkit end
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setBeenAttacked();
                this.setDamage(this.getDamage() + par21 * 10.0F);
                final boolean flag = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode;

                if (flag || this.getDamage() > 40.0F)
                {
                    if (this.riddenByEntity != null)
                    {
                        this.riddenByEntity.mountEntity(this);
                    }

                    // CraftBukkit start
                    final VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
                    this.worldObj.getServer().getPluginManager().callEvent(destroyEvent);

                    if (destroyEvent.isCancelled())
                    {
                        this.setDamage(40); // Maximize damage so this doesn't get triggered again right away
                        return true;
                    }
                    // CraftBukkit end
                    if (flag && !this.isInvNameLocalized())
                    {
                        this.setDead();
                    }
                    else
                    {
                        this.killMinecart(par1DamageSource);
                    }
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public void killMinecart(final DamageSource par1DamageSource)
    {
        this.setDead();
        final ItemStack itemstack = new ItemStack(Item.minecartEmpty, 1);

        if (this.entityName != null)
        {
            itemstack.setItemName(this.entityName);
        }

        this.entityDropItem(itemstack, 0.0F);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();

        if (this.field_82344_g != null)
        {
            this.field_82344_g.update();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        // CraftBukkit start
        final double prevX = this.posX;
        final double prevY = this.posY;
        final double prevZ = this.posZ;
        final float prevYaw = this.rotationYaw;
        final float prevPitch = this.rotationPitch;
        // CraftBukkit end
        if (this.field_82344_g != null)
        {
            this.field_82344_g.update();
        }

        if (this.getRollingAmplitude() > 0)
        {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F)
        {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        int i;

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            final MinecraftServer minecraftserver = ((WorldServer)this.worldObj).getMinecraftServer();
            i = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (true || minecraftserver.getAllowNether())   // CraftBukkit - multi-world should still allow teleport even if default vanilla nether disabled
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

        if (this.worldObj.isRemote)
        {
            if (this.turnProgress > 0)
            {
                final double d0 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
                final double d1 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
                final double d2 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
                final double d3 = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.turnProgress);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
                --this.turnProgress;
                this.setPosition(d0, d1, d2);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            final int j = MathHelper.floor_double(this.posX);
            i = MathHelper.floor_double(this.posY);
            final int k = MathHelper.floor_double(this.posZ);

            if (BlockRailBase.isRailBlockAt(this.worldObj, j, i - 1, k))
            {
                --i;
            }

            final double d4 = this.maxSpeed; // CraftBukkit
            final double d5 = 0.0078125D;
            final int l = this.worldObj.getBlockId(j, i, k);

            if (canUseRail() && BlockRailBase.isRailBlock(l))
            {
                final BlockRailBase rail = (BlockRailBase)Block.blocksList[l];
                final float railMaxSpeed = rail.getRailMaxSpeed(worldObj, this, j, i, k);
                final double maxSpeed = Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
                final int i1 = rail.getBasicRailMetadata(worldObj, this, j, i, k);
                this.updateOnTrack(j, i, k, maxSpeed, getSlopeAdjustment(), l, i1);
                if (l == Block.railActivator.blockID)
                {
                    this.onActivatorRailPass(j, i, k, (worldObj.getBlockMetadata(j, i, k) & 8) != 0);
                }
            }
            else
            {
                this.func_94088_b(onGround ? d4 : getMaxSpeedAirLateral());
            }

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            final double d6 = this.prevPosX - this.posX;
            final double d7 = this.prevPosZ - this.posZ;

            if (d6 * d6 + d7 * d7 > 0.001D)
            {
                this.rotationYaw = (float)(Math.atan2(d7, d6) * 180.0D / Math.PI);

                if (this.isInReverse)
                {
                    this.rotationYaw += 180.0F;
                }
            }

            final double d8 = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);

            if (d8 < -170.0D || d8 >= 170.0D)
            {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);
            // CraftBukkit start
            final org.bukkit.World bworld = this.worldObj.getWorld();
            final Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
            final Location to = new Location(bworld, this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            final Vehicle vehicle = (Vehicle) this.getBukkitEntity();
            this.worldObj.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

            if (!from.equals(to))
            {
                this.worldObj.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
            }

            // CraftBukkit end
            final AxisAlignedBB box;
            if (getCollisionHandler() != null)
            {
                box = getCollisionHandler().getMinecartCollisionBox(this);
            }
            else
            {
                box = boundingBox.expand(0.2D, 0.0D, 0.2D);
            }

            final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, box);

            if (list != null && !list.isEmpty())
            {
                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    final Entity entity = (Entity)list.get(j1);

                    if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart)
                    {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead)
            {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
            }

            MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent(this, j, i, k));
        }
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(final int par1, final int par2, final int par3, final boolean par4) {}

    protected void func_94088_b(final double par1)
    {
        if (this.motionX < -par1)
        {
            this.motionX = -par1;
        }

        if (this.motionX > par1)
        {
            this.motionX = par1;
        }

        if (this.motionZ < -par1)
        {
            this.motionZ = -par1;
        }

        if (this.motionZ > par1)
        {
            this.motionZ = par1;
        }

        double moveY = motionY;
        if(getMaxSpeedAirVertical() > 0 && motionY > getMaxSpeedAirVertical())
        {
            moveY = getMaxSpeedAirVertical();
            if(Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f)
            {
                moveY = 0.15f;
                motionY = moveY;
            }
        }

        if (this.onGround)
        {
            // CraftBukkit start
            this.motionX *= this.derailedX;
            this.motionY *= this.derailedY;
            this.motionZ *= this.derailedZ;
            // CraftBukkit end
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (!this.onGround)
        {
            // CraftBukkit start // Cauldron - CB changed to flyingX but Forge changed to getDragAir() - prefer Forge in this case
            this.motionX *= getDragAir();
            this.motionY *= getDragAir();
            this.motionZ *= getDragAir();
            // CraftBukkit end
        }
    }

    protected void updateOnTrack(final int par1, final int par2, final int par3, final double par4, final double par6, final int par8, int par9)
    {
        int par91 = par9;
        this.fallDistance = 0.0F;
        final Vec3 vec3 = this.func_70489_a(this.posX, this.posY, this.posZ);
        this.posY = (double)par2;
        boolean flag = false;
        boolean flag1 = false;

        if (par8 == Block.railPowered.blockID)
        {
            flag = (worldObj.getBlockMetadata(par1, par2, par3) & 8) != 0;
            flag1 = !flag;
        }

        if (((BlockRailBase)Block.blocksList[par8]).isPowered())
        {
            par91 &= 7;
        }

        if (par91 >= 2 && par91 <= 5)
        {
            this.posY = (double)(par2 + 1);
        }

        if (par91 == 2)
        {
            this.motionX -= par6;
        }

        if (par91 == 3)
        {
            this.motionX += par6;
        }

        if (par91 == 4)
        {
            this.motionZ += par6;
        }

        if (par91 == 5)
        {
            this.motionZ -= par6;
        }

        final int[][] aint = matrix[par91];
        double d2 = (double)(aint[1][0] - aint[0][0]);
        double d3 = (double)(aint[1][2] - aint[0][2]);
        final double d4 = Math.sqrt(d2 * d2 + d3 * d3);
        final double d5 = this.motionX * d2 + this.motionZ * d3;

        if (d5 < 0.0D)
        {
            d2 = -d2;
            d3 = -d3;
        }

        double d6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (d6 > 2.0D)
        {
            d6 = 2.0D;
        }

        this.motionX = d6 * d2 / d4;
        this.motionZ = d6 * d3 / d4;
        double d7;
        double d8;
        double d9;
        double d10;

        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase)
        {
            d7 = (double)((EntityLivingBase)this.riddenByEntity).moveForward;

            if (d7 > 0.0D)
            {
                d8 = -Math.sin((double)(this.riddenByEntity.rotationYaw * (float)Math.PI / 180.0F));
                d9 = Math.cos((double)(this.riddenByEntity.rotationYaw * (float)Math.PI / 180.0F));
                d10 = this.motionX * this.motionX + this.motionZ * this.motionZ;

                if (d10 < 0.01D)
                {
                    this.motionX += d8 * 0.1D;
                    this.motionZ += d9 * 0.1D;
                    flag1 = false;
                }
            }
        }

        if (flag1 && shouldDoRailFunctions())
        {
            d7 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d7 < 0.03D)
            {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            }
            else
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        d7 = 0.0D;
        d8 = (double)par1 + 0.5D + (double)aint[0][0] * 0.5D;
        d9 = (double)par3 + 0.5D + (double)aint[0][2] * 0.5D;
        d10 = (double)par1 + 0.5D + (double)aint[1][0] * 0.5D;
        final double d11 = (double)par3 + 0.5D + (double)aint[1][2] * 0.5D;
        d2 = d10 - d8;
        d3 = d11 - d9;
        final double d12;
        final double d13;

        if (d2 == 0.0D)
        {
            this.posX = (double)par1 + 0.5D;
            d7 = this.posZ - (double)par3;
        }
        else if (d3 == 0.0D)
        {
            this.posZ = (double)par3 + 0.5D;
            d7 = this.posX - (double)par1;
        }
        else
        {
            d12 = this.posX - d8;
            d13 = this.posZ - d9;
            d7 = (d12 * d2 + d13 * d3) * 2.0D;
        }

        this.posX = d8 + d2 * d7;
        this.posZ = d9 + d3 * d7;
        this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);

        moveMinecartOnRail(par1, par2, par3, par4);

        if (aint[0][1] != 0 && MathHelper.floor_double(this.posX) - par1 == aint[0][0] && MathHelper.floor_double(this.posZ) - par3 == aint[0][2])
        {
            this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
        }
        else if (aint[1][1] != 0 && MathHelper.floor_double(this.posX) - par1 == aint[1][0] && MathHelper.floor_double(this.posZ) - par3 == aint[1][2])
        {
            this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
        }

        this.applyDrag();
        final Vec3 vec31 = this.func_70489_a(this.posX, this.posY, this.posZ);

        if (vec31 != null && vec3 != null)
        {
            final double d14 = (vec3.yCoord - vec31.yCoord) * 0.05D;
            d6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d6 > 0.0D)
            {
                this.motionX = this.motionX / d6 * (d6 + d14);
                this.motionZ = this.motionZ / d6 * (d6 + d14);
            }

            this.setPosition(this.posX, vec31.yCoord, this.posZ);
        }

        final int j1 = MathHelper.floor_double(this.posX);
        final int k1 = MathHelper.floor_double(this.posZ);

        if (j1 != par1 || k1 != par3)
        {
            d6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = d6 * (double)(j1 - par1);
            this.motionZ = d6 * (double)(k1 - par3);
        }

        if(shouldDoRailFunctions())
        {
            ((BlockRailBase)Block.blocksList[par8]).onMinecartPass(worldObj, this, par1, par2, par3);
        }

        if (flag && shouldDoRailFunctions())
        {
            final double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d15 > 0.01D)
            {
                final double d16 = 0.06D;
                this.motionX += this.motionX / d15 * d16;
                this.motionZ += this.motionZ / d15 * d16;
            }
            else if (par91 == 1)
            {
                if (this.worldObj.isBlockNormalCube(par1 - 1, par2, par3))
                {
                    this.motionX = 0.02D;
                }
                else if (this.worldObj.isBlockNormalCube(par1 + 1, par2, par3))
                {
                    this.motionX = -0.02D;
                }
            }
            else if (par91 == 0)
            {
                if (this.worldObj.isBlockNormalCube(par1, par2, par3 - 1))
                {
                    this.motionZ = 0.02D;
                }
                else if (this.worldObj.isBlockNormalCube(par1, par2, par3 + 1))
                {
                    this.motionZ = -0.02D;
                }
            }
        }
    }

    protected void applyDrag()
    {
        if (this.riddenByEntity != null || !this.slowWhenEmpty)   // CraftBukkit
        {
            this.motionX *= 0.996999979019165D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.996999979019165D;
        }
        else
        {
            this.motionX *= 0.9599999785423279D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.9599999785423279D;
        }
    }

    @SideOnly(Side.CLIENT)
    public Vec3 func_70495_a(double par1, double par3, double par5, final double par7)
    {
        double par11 = par1;
        double par51 = par5;
        double par31 = par3;
        final int i = MathHelper.floor_double(par11);
        int j = MathHelper.floor_double(par31);
        final int k = MathHelper.floor_double(par51);

        if (BlockRailBase.isRailBlockAt(this.worldObj, i, j - 1, k))
        {
            --j;
        }

        final int l = this.worldObj.getBlockId(i, j, k);

        if (!BlockRailBase.isRailBlock(l))
        {
            return null;
        }
        else
        {
            final int i1 = ((BlockRailBase)Block.blocksList[l]).getBasicRailMetadata(worldObj, this, i, j, k);

            par31 = (double)j;

            if (i1 >= 2 && i1 <= 5)
            {
                par31 = (double)(j + 1);
            }

            final int[][] aint = matrix[i1];
            double d4 = (double)(aint[1][0] - aint[0][0]);
            double d5 = (double)(aint[1][2] - aint[0][2]);
            final double d6 = Math.sqrt(d4 * d4 + d5 * d5);
            d4 /= d6;
            d5 /= d6;
            par11 += d4 * par7;
            par51 += d5 * par7;

            if (aint[0][1] != 0 && MathHelper.floor_double(par11) - i == aint[0][0] && MathHelper.floor_double(par51) - k == aint[0][2])
            {
                par31 += (double)aint[0][1];
            }
            else if (aint[1][1] != 0 && MathHelper.floor_double(par11) - i == aint[1][0] && MathHelper.floor_double(par51) - k == aint[1][2])
            {
                par31 += (double)aint[1][1];
            }

            return this.func_70489_a(par11, par31, par51);
        }
    }

    public Vec3 func_70489_a(double par1, double par3, double par5)
    {
        double par11 = par1;
        double par51 = par5;
        double par31 = par3;
        final int i = MathHelper.floor_double(par11);
        int j = MathHelper.floor_double(par31);
        final int k = MathHelper.floor_double(par51);

        if (BlockRailBase.isRailBlockAt(this.worldObj, i, j - 1, k))
        {
            --j;
        }

        final int l = this.worldObj.getBlockId(i, j, k);

        if (BlockRailBase.isRailBlock(l))
        {
            final int i1 = ((BlockRailBase)Block.blocksList[l]).getBasicRailMetadata(worldObj, this, i, j, k);
            par31 = (double)j;

            if (i1 >= 2 && i1 <= 5)
            {
                par31 = (double)(j + 1);
            }

            final int[][] aint = matrix[i1];
            double d3 = 0.0D;
            final double d4 = (double)i + 0.5D + (double)aint[0][0] * 0.5D;
            final double d5 = (double)j + 0.5D + (double)aint[0][1] * 0.5D;
            final double d6 = (double)k + 0.5D + (double)aint[0][2] * 0.5D;
            final double d7 = (double)i + 0.5D + (double)aint[1][0] * 0.5D;
            final double d8 = (double)j + 0.5D + (double)aint[1][1] * 0.5D;
            final double d9 = (double)k + 0.5D + (double)aint[1][2] * 0.5D;
            final double d10 = d7 - d4;
            final double d11 = (d8 - d5) * 2.0D;
            final double d12 = d9 - d6;

            if (d10 == 0.0D)
            {
                par11 = (double)i + 0.5D;
                d3 = par51 - (double)k;
            }
            else if (d12 == 0.0D)
            {
                par51 = (double)k + 0.5D;
                d3 = par11 - (double)i;
            }
            else
            {
                final double d13 = par11 - d4;
                final double d14 = par51 - d6;
                d3 = (d13 * d10 + d14 * d12) * 2.0D;
            }

            par11 = d4 + d10 * d3;
            par31 = d5 + d11 * d3;
            par51 = d6 + d12 * d3;

            if (d11 < 0.0D)
            {
                ++par31;
            }

            if (d11 > 0.0D)
            {
                par31 += 0.5D;
            }

            return this.worldObj.getWorldVec3Pool().getVecFromPool(par11, par31, par51);
        }
        else
        {
            return null;
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.getBoolean("CustomDisplayTile"))
        {
            this.setDisplayTile(par1NBTTagCompound.getInteger("DisplayTile"));
            this.setDisplayTileData(par1NBTTagCompound.getInteger("DisplayData"));
            this.setDisplayTileOffset(par1NBTTagCompound.getInteger("DisplayOffset"));
        }

        if (par1NBTTagCompound.hasKey("CustomName") && !par1NBTTagCompound.getString("CustomName").isEmpty())
        {
            this.entityName = par1NBTTagCompound.getString("CustomName");
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        if (this.hasDisplayTile())
        {
            par1NBTTagCompound.setBoolean("CustomDisplayTile", true);
            par1NBTTagCompound.setInteger("DisplayTile", this.getDisplayTile() == null ? 0 : this.getDisplayTile().blockID);
            par1NBTTagCompound.setInteger("DisplayData", this.getDisplayTileData());
            par1NBTTagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }

        if (this.entityName != null && !this.entityName.isEmpty())
        {
            par1NBTTagCompound.setString("CustomName", this.entityName);
        }
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(final Entity par1Entity)
    {
        MinecraftForge.EVENT_BUS.post(new MinecartCollisionEvent(this, par1Entity));
        if (getCollisionHandler() != null)
        {
            getCollisionHandler().onEntityCollision(this, par1Entity);
            return;
        }
        if (!this.worldObj.isRemote)
        {
            if (par1Entity != this.riddenByEntity)
            {
                // CraftBukkit start
                final Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                final org.bukkit.entity.Entity hitEntity = (par1Entity == null) ? null : par1Entity.getBukkitEntity();
                final VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, hitEntity);
                this.worldObj.getServer().getPluginManager().callEvent(collisionEvent);

                if (collisionEvent.isCancelled())
                {
                    return;
                }

                // CraftBukkit end

                if (par1Entity instanceof EntityLivingBase && !(par1Entity instanceof EntityPlayer) && !(par1Entity instanceof EntityIronGolem) && this.getMinecartType() == 0 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D && this.riddenByEntity == null && par1Entity.ridingEntity == null)
                {
                    par1Entity.mountEntity(this);
                }

                double d0 = par1Entity.posX - this.posX;
                double d1 = par1Entity.posZ - this.posZ;
                double d2 = d0 * d0 + d1 * d1;

                // CraftBukkit - collision
                if (d2 >= 9.999999747378752E-5D && !collisionEvent.isCollisionCancelled())
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
                    d0 *= 0.10000000149011612D;
                    d1 *= 0.10000000149011612D;
                    d0 *= (double)(1.0F - this.entityCollisionReduction);
                    d1 *= (double)(1.0F - this.entityCollisionReduction);
                    d0 *= 0.5D;
                    d1 *= 0.5D;

                    if (par1Entity instanceof EntityMinecart)
                    {
                        final double d4 = par1Entity.posX - this.posX;
                        final double d5 = par1Entity.posZ - this.posZ;
                        final Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(d4, 0.0D, d5).normalize();
                        final Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F), 0.0D, (double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F)).normalize();
                        final double d6 = Math.abs(vec3.dotProduct(vec31));

                        if (d6 < 0.800000011920929D)
                        {
                            return;
                        }

                        double d7 = par1Entity.motionX + this.motionX;
                        double d8 = par1Entity.motionZ + this.motionZ;

                        if (((EntityMinecart)par1Entity).isPoweredCart() && !isPoweredCart())
                        {
                            this.motionX *= 0.20000000298023224D;
                            this.motionZ *= 0.20000000298023224D;
                            this.addVelocity(par1Entity.motionX - d0, 0.0D, par1Entity.motionZ - d1);
                            par1Entity.motionX *= 0.949999988079071D;
                            par1Entity.motionZ *= 0.949999988079071D;
                        }
                        else if (!((EntityMinecart)par1Entity).isPoweredCart() && isPoweredCart())
                        {
                            par1Entity.motionX *= 0.20000000298023224D;
                            par1Entity.motionZ *= 0.20000000298023224D;
                            par1Entity.addVelocity(this.motionX + d0, 0.0D, this.motionZ + d1);
                            this.motionX *= 0.949999988079071D;
                            this.motionZ *= 0.949999988079071D;
                        }
                        else
                        {
                            d7 /= 2.0D;
                            d8 /= 2.0D;
                            this.motionX *= 0.20000000298023224D;
                            this.motionZ *= 0.20000000298023224D;
                            this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                            par1Entity.motionX *= 0.20000000298023224D;
                            par1Entity.motionZ *= 0.20000000298023224D;
                            par1Entity.addVelocity(d7 + d0, 0.0D, d8 + d1);
                        }
                    }
                    else
                    {
                        this.addVelocity(-d0, 0.0D, -d1);
                        par1Entity.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(final double par1, final double par3, final double par5, final float par7, final float par8, final int par9)
    {
        this.minecartX = par1;
        this.minecartY = par3;
        this.minecartZ = par5;
        this.minecartYaw = (double)par7;
        this.minecartPitch = (double)par8;
        this.turnProgress = par9 + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    /**
     * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public void setDamage(final float par1)
    {
        this.dataWatcher.updateObject(19, Float.valueOf(par1));
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(final double par1, final double par3, final double par5)
    {
        this.velocityX = this.motionX = par1;
        this.velocityY = this.motionY = par3;
        this.velocityZ = this.motionZ = par5;
    }

    /**
     * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public float getDamage()
    {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the rolling amplitude the cart rolls while being attacked.
     */
    public void setRollingAmplitude(final int par1)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(par1));
    }

    /**
     * Gets the rolling amplitude the cart rolls while being attacked.
     */
    public int getRollingAmplitude()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
     */
    public void setRollingDirection(final int par1)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    /**
     * Gets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
     */
    public int getRollingDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    public abstract int getMinecartType();

    public Block getDisplayTile()
    {
        if (!this.hasDisplayTile())
        {
            return this.getDefaultDisplayTile();
        }
        else
        {
            final int i = this.getDataWatcher().getWatchableObjectInt(20) & 65535;
            return i > 0 && i < Block.blocksList.length ? Block.blocksList[i] : null;
        }
    }

    public Block getDefaultDisplayTile()
    {
        return null;
    }

    public int getDisplayTileData()
    {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileData() : this.getDataWatcher().getWatchableObjectInt(20) >> 16;
    }

    public int getDefaultDisplayTileData()
    {
        return 0;
    }

    public int getDisplayTileOffset()
    {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
    }

    public int getDefaultDisplayTileOffset()
    {
        return 6;
    }

    public void setDisplayTile(final int par1)
    {
        this.getDataWatcher().updateObject(20, Integer.valueOf(par1 & 65535 | this.getDisplayTileData() << 16));
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileData(final int par1)
    {
        final Block block = this.getDisplayTile();
        final int j = block == null ? 0 : block.blockID;
        this.getDataWatcher().updateObject(20, Integer.valueOf(j & 65535 | par1 << 16));
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(final int par1)
    {
        this.getDataWatcher().updateObject(21, Integer.valueOf(par1));
        this.setHasDisplayTile(true);
    }

    public boolean hasDisplayTile()
    {
        return this.getDataWatcher().getWatchableObjectByte(22) == 1;
    }

    public void setHasDisplayTile(final boolean par1)
    {
        this.getDataWatcher().updateObject(22, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    /**
     * Sets the minecart's name.
     */
    public void setMinecartName(final String par1Str)
    {
        this.entityName = par1Str;
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
        return this.entityName != null ? this.entityName : super.getEntityName();
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized()
    {
        return this.entityName != null;
    }

    public String func_95999_t()
    {
        return this.entityName;
    }

    // CraftBukkit start - Methods for getting and setting flying and derailed velocity modifiers
    public Vector getFlyingVelocityMod()
    {
        return new Vector(flyingX, flyingY, flyingZ);
    }

    public void setFlyingVelocityMod(final Vector flying)
    {
        flyingX = flying.getX();
        flyingY = flying.getY();
        flyingZ = flying.getZ();
    }

    public Vector getDerailedVelocityMod()
    {
        return new Vector(derailedX, derailedY, derailedZ);
    }

    public void setDerailedVelocityMod(final Vector derailed)
    {
        derailedX = derailed.getX();
        derailedY = derailed.getY();
        derailedZ = derailed.getZ();
    }
    // CraftBukkit end

    /**
     * Moved to allow overrides.
     * This code handles minecart movement and speed capping when on a rail.
     */
    public void moveMinecartOnRail(final int x, final int y, final int z, final double par4){
        double d12 = this.motionX;
        double d13 = this.motionZ;

        if (this.riddenByEntity != null)
        {
            d12 *= 0.75D;
            d13 *= 0.75D;
        }

        if (d12 < -par4)
        {
            d12 = -par4;
        }

        if (d12 > par4)
        {
            d12 = par4;
        }

        if (d13 < -par4)
        {
            d13 = -par4;
        }

        if (d13 > par4)
        {
            d13 = par4;
        }

        this.moveEntity(d12, 0.0D, d13);
    }

    /**
     * Gets the current global Minecart Collision handler if none
     * is registered, returns null
     * @return The collision handler or null
     */
    public static IMinecartCollisionHandler getCollisionHandler()
    {
        return collisionHandler;
    }

    /**
     * Sets the global Minecart Collision handler, overwrites any
     * that is currently set.
     * @param handler The new handler
     */
    public static void setCollisionHandler(final IMinecartCollisionHandler handler)
    {
        collisionHandler = handler;
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * This should be an ItemStack that can be used by the player to place the cart,
     * but is not necessary the item the cart drops when destroyed.
     * @return An ItemStack that can be used to place the cart.
     */
    public ItemStack getCartItem()
    {
        if (this instanceof EntityMinecartChest)
        {
            return new ItemStack(Item.minecartCrate);
        }
        else if (this instanceof EntityMinecartTNT)
        {
            return new ItemStack(Item.minecartTnt);
        }
        else if (this instanceof EntityMinecartFurnace)
        {
            return new ItemStack(Item.minecartPowered);
        }
        else if (this instanceof EntityMinecartHopper)
        {
            return new ItemStack(Item.minecartHopper);
        }
        return new ItemStack(Item.minecartEmpty);
    }

    /**
     * Returns true if this cart can currently use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @return True if the minecart can use rails.
     */
    public boolean canUseRail()
    {
        return canUseRail;
    }

    /**
     * Set whether the minecart can use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @param use Whether the minecart can currently use rails.
     */
    public void setCanUseRail(final boolean use)
    {
        canUseRail = use;
    }

    /**
     * Return false if this cart should not call onMinecartPass() and should ignore Powered Rails.
     * @return True if this cart should call onMinecartPass().
     */
    public boolean shouldDoRailFunctions()
    {
        return true;
    }

    /**
     * Returns true if this cart is self propelled.
     * @return True if powered.
     */
    public boolean isPoweredCart()
    {
        return getMinecartType()== 2;
    }

    /**
     * Returns true if this cart can be ridden by an Entity.
     * @return True if this cart can be ridden.
     */
    public boolean canBeRidden()
    {
        if(this instanceof EntityMinecartEmpty)
        {
            return true;
        }
        return false;
    }

    /**
     * Getters/setters for physics variables
     */

    /**
     * Returns the carts max speed when traveling on rails. Carts going faster
     * than 1.1 cause issues with chunk loading. Carts cant traverse slopes or
     * corners at greater than 0.5 - 0.6. This value is compared with the rails
     * max speed and the carts current speed cap to determine the carts current
     * max speed. A normal rail's max speed is 0.4.
     *
     * @return Carts max speed.
     */
    public float getMaxCartSpeedOnRail()
    {
        return 1.2f;
    }

    /**
     * Returns the current speed cap for the cart when traveling on rails. This
     * functions differs from getMaxCartSpeedOnRail() in that it controls
     * current movement and cannot be overridden. The value however can never be
     * higher than getMaxCartSpeedOnRail().
     *
     * @return
     */
    public final float getCurrentCartSpeedCapOnRail()
    {
        return currentSpeedRail;
    }

    public final void setCurrentCartSpeedCapOnRail(float value)
    {
        float value1 = Math.min(value, getMaxCartSpeedOnRail());
        currentSpeedRail = value1;
    }

    public float getMaxSpeedAirLateral()
    {
        return maxSpeedAirLateral;
    }

    public void setMaxSpeedAirLateral(final float value)
    {
        maxSpeedAirLateral = value;
    }

    public float getMaxSpeedAirVertical()
    {
        return maxSpeedAirVertical;
    }

    public void setMaxSpeedAirVertical(final float value)
    {
        maxSpeedAirVertical = value;
    }

    public double getDragAir()
    {
        return dragAir;
    }

    public void setDragAir(final double value)
    {
        dragAir = value;
    }

    public double getSlopeAdjustment()
    {
        return 0.0078125D;
    }
}
