package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import org.bukkit.craftbukkit.v1_6_R3.SpigotTimings;
import org.bukkit.craftbukkit.v1_6_R3.TrigMath;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.*;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityLivingBase extends Entity {
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
    private BaseAttributeMap attributeMap;
    public CombatTracker _combatTracker = new CombatTracker(this); // CraftBukkit - private -> public, remove final
    public final HashMap activePotionsMap = new HashMap(); // CraftBukkit - protected -> public

    /**
     * The equipment this mob was previously wearing, used for syncing.
     */
    private final ItemStack[] previousEquipment = new ItemStack[5];

    /**
     * Whether an arm swing is currently in progress.
     */
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public float prevHealth;

    /**
     * The amount of time remaining this entity should act 'hurt'. (Visual appearance of red tint)
     */
    public int hurtTime;

    /**
     * What the hurt time was max set to last.
     */
    public int maxHurtTime;

    /**
     * The yaw at which this entity was last attacked from.
     */
    public float attackedAtYaw;

    /**
     * The amount of time remaining this entity should act 'dead', i.e. have a corpse in the world.
     */
    public int deathTime;
    public int attackTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;

    /**
     * Only relevant when limbYaw is not 0(the entity is moving). Influences where in its swing legs and arms currently
     * are.
     */
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset;
    public float prevRenderYawOffset;

    /**
     * Entity head rotation yaw
     */
    public float rotationYawHead;

    /**
     * Entity head rotation yaw at previous tick
     */
    public float prevRotationYawHead;

    /**
     * A factor used to determine how far this entity will move each tick if it is jumping or falling.
     */
    public float jumpMovementFactor = 0.02F;

    /**
     * The most recent player that has attacked this entity
     */
    public EntityPlayer attackingPlayer; // CraftBukkit - protected -> public

    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements. Used to determine whether the entity
     * should drop items on death.
     */
    protected int recentlyHit;

    /**
     * This gets set on entity death, but never used. Looks like a duplicate of isDead
     */
    protected boolean dead;

    /**
     * Holds the living entity age, used to control the despawn.
     */
    protected int entityAge;
    protected float field_70768_au;
    protected float field_110154_aX;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected float field_70741_aB;

    /**
     * The score value of the Mob, the amount of points the mob is worth.
     */
    protected int scoreValue;

    /**
     * Damage taken in the last hit. Mobs are resistant to damage less than this for a short time after taking damage.
     */
    public float lastDamage; // CraftBukkit - protected -> public

    /**
     * used to check whether entity is jumping.
     */
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;

    /**
     * The number of updates over which the new position and rotation are to be applied to the entity.
     */
    protected int newPosRotationIncrements;

    /**
     * The new X position to be applied to the entity.
     */
    protected double newPosX;

    /**
     * The new Y position to be applied to the entity.
     */
    protected double newPosY;
    protected double newPosZ;

    /**
     * The new yaw rotation to be applied to the entity.
     */
    protected double newRotationYaw;

    /**
     * The new yaw rotation to be applied to the entity.
     */
    protected double newRotationPitch;

    /**
     * Whether the DataWatcher needs to be updated with the active potions
     */
    public boolean potionsNeedUpdate = true; // CraftBukkit - private -> public

    /**
     * is only being set, has no uses as of MC 1.1
     */
    public EntityLivingBase entityLivingToAttack; // CraftBukkit - private -> public
    private int revengeTimer;
    private EntityLivingBase lastAttacker;

    /**
     * Holds the value of ticksExisted when setLastAttacker was last called.
     */
    private int lastAttackerTime;

    /**
     * A factor used to determine how far this entity will move each tick if it is walking on land. Adjusted by speed,
     * and slipperiness of the current block.
     */
    private float landMovementFactor;

    /**
     * Number of ticks since last jump
     */
    private int jumpTicks;
    private float field_110151_bq;
    // CraftBukkit start
    public int expToDrop;
    public int maxAirTicks = 300;
    // CraftBukkit end

    public EntityLivingBase(final World par1World) {
        super(par1World);
        this.applyEntityAttributes();
        // CraftBukkit - setHealth(getMaxHealth()) inlined and simplified to skip the instanceof check for EntityPlayer, as getBukkitEntity() is not initialized in constructor
        this.dataWatcher.updateObject(6, (float) this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());
        this.preventEntitySpawning = true;
        this.field_70770_ap = (float) (Math.random() + 1.0D) * 0.01F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_70769_ao = (float) Math.random() * 12398.0F;
        this.rotationYaw = (float) (Math.random() * Math.PI * 2.0D);
        this.rotationYawHead = this.rotationYaw;
        this.stepHeight = 0.5F;
    }

    protected void entityInit() {
        this.dataWatcher.addObject(7, Integer.valueOf(0));
        this.dataWatcher.addObject(8, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(9, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(6, Float.valueOf(1.0F));
    }

    protected void applyEntityAttributes() {
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.movementSpeed);

        if (!this.isAIEnabled()) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.10000000149011612D);
        }
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(final double par1, final boolean par3) {
        if (!this.isInWater()) {
            this.handleWaterMovement();
        }

        if (par3 && this.fallDistance > 0.0F) {
            final int i = MathHelper.floor_double(this.posX);
            final int j = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double) this.yOffset);
            final int k = MathHelper.floor_double(this.posZ);
            int l = this.worldObj.getBlockId(i, j, k);

            if (l == 0) {
                final int i1 = this.worldObj.blockGetRenderType(i, j - 1, k);

                if (i1 == 11 || i1 == 32 || i1 == 21) {
                    l = this.worldObj.getBlockId(i, j - 1, k);
                }
            }

            if (l > 0) {
                Block.blocksList[l].onFallenUpon(this.worldObj, i, j, k, this, this.fallDistance);
            }
        }

        super.updateFallState(par1, par3);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate() {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");

        if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
        }

        if (this.isImmuneToFire() || this.worldObj.isRemote) {
            this.extinguish();
        }

        final boolean flag = this instanceof EntityPlayer && ((EntityPlayer) this).capabilities.disableDamage;

        if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water)) {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag) {
                this.setAir(this.decreaseAirSupply(this.getAir()));

                if (this.getAir() == -20) {
                    this.setAir(0);

                    for (int i = 0; i < 8; ++i) {
                        final float f = this.rand.nextFloat() - this.rand.nextFloat();
                        final float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                        final float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                        this.worldObj.spawnParticle("bubble", this.posX + (double) f, this.posY + (double) f1, this.posZ + (double) f2, this.motionX, this.motionY, this.motionZ);
                    }

                    this.attackEntityFrom(DamageSource.drown, 2.0F);
                }
            }

            this.extinguish();

            if (!this.worldObj.isRemote && this.isRiding() && ridingEntity != null && ridingEntity.shouldDismountInWater(this)) {
                this.mountEntity((Entity) null);
            }
        } else {
            // CraftBukkit start - Only set if needed to work around a DataWatcher inefficiency
            if (this.getAir() != 300) {
                this.setAir(maxAirTicks);
            }

            // CraftBukkit end
        }

        this.prevCameraPitch = this.cameraPitch;

        if (this.attackTime > 0) {
            --this.attackTime;
        }

        if (this.hurtTime > 0) {
            --this.hurtTime;
        }

        // CraftBukkit
        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
            --this.hurtResistantTime;
        }

        if (this.getHealth() <= 0.0F) {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0) {
            --this.recentlyHit;
        } else {
            this.attackingPlayer = null;
        }

        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
            this.lastAttacker = null;
        }

        if (this.entityLivingToAttack != null && !this.entityLivingToAttack.isEntityAlive()) {
            this.setRevengeTarget((EntityLivingBase) null);
        }

        this.updatePotionEffects();
        this.field_70763_ax = this.field_70764_aw;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    // CraftBukkit start
    public int getExpReward() {
        final int exp = this.getExperiencePoints(this.attackingPlayer);

        if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && !this.isChild()) {
            return exp;
        } else {
            return 0;
        }
    }
    // CraftBukkit end

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild() {
        return false;
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate() {
        ++this.deathTime;

        if (this.deathTime >= 20 && !this.isDead)   // CraftBukkit - (this.deathTicks == 20) -> (this.deathTicks >= 20 && !this.dead)
        {
            int i;
            // CraftBukkit start - Update getExpReward() above if the removed if() changes!
            i = this.expToDrop;

            while (i > 0) {
                final int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            this.expToDrop = 0;
            // CraftBukkit end
            this.setDead();

            for (i = 0; i < 20; ++i) {
                final double d0 = this.rand.nextGaussian() * 0.02D;
                final double d1 = this.rand.nextGaussian() * 0.02D;
                final double d2 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("explode", this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }
        }
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(final int par1) {
        final int j = EnchantmentHelper.getRespiration(this);
        return j > 0 && this.rand.nextInt(j + 1) > 0 ? par1 : par1 - 1;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(final EntityPlayer par1EntityPlayer) {
        return 0;
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer() {
        return false;
    }

    public Random getRNG() {
        return this.rand;
    }

    public EntityLivingBase getAITarget() {
        return this.entityLivingToAttack;
    }

    public int func_142015_aE() {
        return this.revengeTimer;
    }

    public void setRevengeTarget(final EntityLivingBase par1EntityLivingBase) {
        this.entityLivingToAttack = par1EntityLivingBase;
        this.revengeTimer = this.ticksExisted;
        ForgeHooks.onLivingSetAttackTarget(this, par1EntityLivingBase);
    }

    public EntityLivingBase getLastAttacker() {
        return this.lastAttacker;
    }

    public int getLastAttackerTime() {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(final Entity par1Entity) {
        if (par1Entity instanceof EntityLivingBase) {
            this.lastAttacker = (EntityLivingBase) par1Entity;
        } else {
            this.lastAttacker = null;
        }

        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge() {
        return this.entityAge;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setFloat("HealF", this.getHealth());
        par1NBTTagCompound.setShort("Health", (short) ((int) Math.ceil((double) this.getHealth())));
        par1NBTTagCompound.setShort("HurtTime", (short) this.hurtTime);
        par1NBTTagCompound.setShort("DeathTime", (short) this.deathTime);
        par1NBTTagCompound.setShort("AttackTime", (short) this.attackTime);
        par1NBTTagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());
        ItemStack[] aitemstack = this.getLastActiveItems();
        int i = aitemstack.length;
        int j;
        ItemStack itemstack;

        for (j = 0; j < i; ++j) {
            itemstack = aitemstack[j];

            if (itemstack != null) {
                this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
            }
        }

        par1NBTTagCompound.setTag("Attributes", SharedMonsterAttributes.func_111257_a(this.getAttributeMap()));
        aitemstack = this.getLastActiveItems();
        i = aitemstack.length;

        for (j = 0; j < i; ++j) {
            itemstack = aitemstack[j];

            if (itemstack != null) {
                this.attributeMap.applyAttributeModifiers(itemstack.getAttributeModifiers());
            }
        }

        if (!this.activePotionsMap.isEmpty()) {
            final NBTTagList nbttaglist = new NBTTagList();
            final Iterator iterator = this.activePotionsMap.values().iterator();

            while (iterator.hasNext()) {
                final PotionEffect potioneffect = (PotionEffect) iterator.next();
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            par1NBTTagCompound.setTag("ActiveEffects", nbttaglist);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        this.setAbsorptionAmount(par1NBTTagCompound.getFloat("AbsorptionAmount"));

        if (par1NBTTagCompound.hasKey("Attributes") && this.worldObj != null && !this.worldObj.isRemote) {
            SharedMonsterAttributes.func_111260_a(this.getAttributeMap(), par1NBTTagCompound.getTagList("Attributes"), this.worldObj == null ? null : this.worldObj.getWorldLogAgent());
        }

        if (par1NBTTagCompound.hasKey("ActiveEffects")) {
            final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("ActiveEffects");

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                final NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
                final PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound1);
                this.activePotionsMap.put(Integer.valueOf(potioneffect.getPotionID()), potioneffect);
            }
        }

        // CraftBukkit start
        if (par1NBTTagCompound.hasKey("Bukkit.MaxHealth")) {
            final NBTBase nbtbase = par1NBTTagCompound.getTag("Bukkit.MaxHealth");

            if (nbtbase.getId() == 5) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double) ((NBTTagFloat) nbtbase).data);
            } else if (nbtbase.getId() == 3) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double) ((NBTTagInt) nbtbase).data);
            }
        }

        // CraftBukkit end

        if (par1NBTTagCompound.hasKey("HealF")) {
            this.setHealth(par1NBTTagCompound.getFloat("HealF"));
        } else {
            final NBTBase nbtbase = par1NBTTagCompound.getTag("Health");

            if (nbtbase == null) {
                this.setHealth(this.getMaxHealth());
            } else if (nbtbase.getId() == 5) {
                this.setHealth(((NBTTagFloat) nbtbase).data);
            } else if (nbtbase.getId() == 2) {
                this.setHealth((float) ((NBTTagShort) nbtbase).data);
            }
        }

        this.hurtTime = par1NBTTagCompound.getShort("HurtTime");
        this.deathTime = par1NBTTagCompound.getShort("DeathTime");
        this.attackTime = par1NBTTagCompound.getShort("AttackTime");
    }

    protected void updatePotionEffects() {
        final Iterator iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext()) {
            final Integer integer = (Integer) iterator.next();
            final PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.get(integer);

            if (!potioneffect.onUpdate(this)) {
                if (!this.worldObj.isRemote) {
                    iterator.remove();
                    this.onFinishedPotionEffect(potioneffect);
                }
            } else if (potioneffect.getDuration() % 600 == 0) {
                this.onChangedPotionEffect(potioneffect, false);
            }
        }

        int i;

        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                if (this.activePotionsMap.isEmpty()) {
                    this.dataWatcher.updateObject(8, Byte.valueOf((byte) 0));
                    this.dataWatcher.updateObject(7, Integer.valueOf(0));
                    this.setInvisible(false);
                } else {
                    i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(8, Byte.valueOf((byte) (PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0)));
                    this.dataWatcher.updateObject(7, Integer.valueOf(i));
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            this.potionsNeedUpdate = false;
        }

        i = this.dataWatcher.getWatchableObjectInt(7);
        final boolean flag = this.dataWatcher.getWatchableObjectByte(8) > 0;

        if (i > 0) {
            boolean flag1 = false;

            if (!this.isInvisible()) {
                flag1 = this.rand.nextBoolean();
            } else {
                flag1 = this.rand.nextInt(15) == 0;
            }

            if (flag) {
                flag1 &= this.rand.nextInt(5) == 0;
            }

            if (flag1 && i > 0) {
                final double d0 = (double) (i >> 16 & 255) / 255.0D;
                final double d1 = (double) (i >> 8 & 255) / 255.0D;
                final double d2 = (double) (i >> 0 & 255) / 255.0D;
                this.worldObj.spawnParticle(flag ? "mobSpellAmbient" : "mobSpell", this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height - (double) this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2);
            }
        }
    }

    public void clearActivePotions() {
        final Iterator iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext()) {
            final Integer integer = (Integer) iterator.next();
            final PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.get(integer);

            if (!this.worldObj.isRemote) {
                iterator.remove();
                this.onFinishedPotionEffect(potioneffect);
            }
        }
    }

    public Collection getActivePotionEffects() {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(final int par1) {
        // CraftBukkit - Add size check for efficiency
        return !this.activePotionsMap.isEmpty() && this.activePotionsMap.containsKey(Integer.valueOf(par1));
    }

    public boolean isPotionActive(final Potion par1Potion) {
        // CraftBukkit - Add size check for efficiency
        return !this.activePotionsMap.isEmpty() && this.activePotionsMap.containsKey(Integer.valueOf(par1Potion.id));
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
     */
    public PotionEffect getActivePotionEffect(final Potion par1Potion) {
        return (PotionEffect) this.activePotionsMap.get(Integer.valueOf(par1Potion.id));
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(final PotionEffect par1PotionEffect) {
        if (this.isPotionApplicable(par1PotionEffect)) {
            if (this.activePotionsMap.containsKey(Integer.valueOf(par1PotionEffect.getPotionID()))) {
                ((PotionEffect) this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID()))).combine(par1PotionEffect);
                this.onChangedPotionEffect((PotionEffect) this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID())), true);
            } else {
                this.activePotionsMap.put(Integer.valueOf(par1PotionEffect.getPotionID()), par1PotionEffect);
                this.onNewPotionEffect(par1PotionEffect);
            }
        }
    }

    public boolean isPotionApplicable(final PotionEffect par1PotionEffect) {
        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            final int i = par1PotionEffect.getPotionID();

            if (i == Potion.regeneration.id || i == Potion.poison.id) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if this entity is undead.
     */
    public boolean isEntityUndead() {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Remove the specified potion effect from this entity.
     */
    public void removePotionEffectClient(final int par1) {
        this.activePotionsMap.remove(Integer.valueOf(par1));
    }

    /**
     * Remove the specified potion effect from this entity.
     */
    public void removePotionEffect(final int par1) {
        final PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.remove(Integer.valueOf(par1));

        if (potioneffect != null) {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    protected void onNewPotionEffect(final PotionEffect par1PotionEffect) {
        this.potionsNeedUpdate = true;

        if (!this.worldObj.isRemote) {
            Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(final PotionEffect par1PotionEffect, final boolean par2) {
        this.potionsNeedUpdate = true;

        if (par2 && !this.worldObj.isRemote) {
            Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
            Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(final PotionEffect par1PotionEffect) {
        this.potionsNeedUpdate = true;

        if (!this.worldObj.isRemote) {
            Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    // CraftBukkit start - Delegate so we can handle providing a reason for health being regained

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(final float par1) {
        heal(par1, EntityRegainHealthEvent.RegainReason.CUSTOM);
    }

    public void heal(final float f, final EntityRegainHealthEvent.RegainReason regainReason) {
        final float f1 = this.getHealth();

        if (f1 > 0.0F) {
            final EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, regainReason);
            this.worldObj.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                this.setHealth((float) (this.getHealth() + event.getAmount()));
            }
        }
    }

    public final float getHealth() {
        // CraftBukkit start - Use unscaled health
        if (this instanceof EntityPlayerMP) {
            return (float) ((EntityPlayerMP) this).getBukkitEntity().getHealth();
        }

        // CraftBukkit end
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(final float par1) {
        // CraftBukkit start - Handle scaled health
        if (this instanceof EntityPlayerMP) {
            final org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer player = ((EntityPlayerMP) this).getBukkitEntity();

            // Squeeze
            if (par1 < 0.0F) {
                player.setRealHealth(0.0D);
            } else if (par1 > player.getMaxHealth()) {
                player.setRealHealth(player.getMaxHealth());
            } else {
                player.setRealHealth(par1);
            }

            this.dataWatcher.updateObject(6, Float.valueOf(player.getScaledHealth()));
            return;
        }

        // CraftBukkit end
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(par1, 0.0F, this.getMaxHealth())));
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, float par2) {
        float par21 = par2;
        if (ForgeHooks.onLivingAttack(this, par1DamageSource, par21)) return false;
        if (this.isEntityInvulnerable()) {
            return false;
        } else if (this.worldObj.isRemote) {
            return false;
        } else {
            this.entityAge = 0;

            if (this.getHealth() <= 0.0F) {
                return false;
            } else if (par1DamageSource.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
                return false;
            } else {
                if ((par1DamageSource == DamageSource.anvil || par1DamageSource == DamageSource.fallingBlock) && this.getCurrentItemOrArmor(4) != null) {
                    this.getCurrentItemOrArmor(4).damageItem((int) (par21 * 4.0F + this.rand.nextFloat() * par21 * 2.0F), this);
                    par21 *= 0.75F;
                }

                this.limbSwingAmount = 1.5F;
                boolean flag = true;
                // CraftBukkit start
                final EntityDamageEvent event = CraftEventFactory.handleEntityDamageEvent(this, par1DamageSource, par21);

                if (event != null) {
                    if (event.isCancelled()) {
                        return false;
                    }

                    par21 = (float) event.getDamage();
                }

                // CraftBukkit end

                if ((float) this.hurtResistantTime > (float) this.maxHurtResistantTime / 2.0F) {
                    if (par21 <= this.lastDamage) {
                        return false;
                    }

                    this.damageEntity(par1DamageSource, par21 - this.lastDamage);
                    this.lastDamage = par21;
                    flag = false;
                } else {
                    this.lastDamage = par21;
                    this.prevHealth = this.getHealth();
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(par1DamageSource, par21);
                    this.hurtTime = this.maxHurtTime = 10;
                }

                this.attackedAtYaw = 0.0F;
                final Entity entity = par1DamageSource.getEntity();

                if (entity != null) {
                    if (entity instanceof EntityLivingBase) {
                        this.setRevengeTarget((EntityLivingBase) entity);
                    }

                    if (entity instanceof EntityPlayer) {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer) entity;
                    } else if (entity instanceof EntityWolf) {
                        final EntityWolf entitywolf = (EntityWolf) entity;

                        if (entitywolf.isTamed()) {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (flag) {
                    this.worldObj.setEntityState(this, (byte) 2);

                    if (par1DamageSource != DamageSource.drown) {
                        this.setBeenAttacked();
                    }

                    if (entity != null) {
                        double d0 = entity.posX - this.posX;
                        double d1;

                        for (d1 = entity.posZ - this.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - this.rotationYaw;
                        this.knockBack(entity, par21, d0, d1);
                    } else {
                        this.attackedAtYaw = (float) ((int) (Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F) {
                    if (flag) {
                        this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
                    }

                    this.onDeath(par1DamageSource);
                } else if (flag) {
                    this.playSound(this.getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
                }

                return true;
            }
        }
    }

    /**
     * Renders broken item particles using the given ItemStack
     */
    public void renderBrokenItemStack(final ItemStack par1ItemStack) {
        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

        for (int i = 0; i < 5; ++i) {
            final Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vec3.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec3.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
            Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double) this.rand.nextFloat() - 0.5D) * 0.3D, (double) (-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
            vec31.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec31.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
            vec31 = vec31.addVector(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ);
            this.worldObj.spawnParticle("iconcrack_" + par1ItemStack.getItem().itemID, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(final DamageSource par1DamageSource) {
        if (ForgeHooks.onLivingDeath(this, par1DamageSource)) return;
        final Entity entity = par1DamageSource.getEntity();
        final EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (this.scoreValue >= 0 && entitylivingbase != null) {
            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }

        if (entity != null) {
            entity.onKillEntity(this);
        }

        this.dead = true;

        if (!this.worldObj.isRemote) {
            int i = 0;

            if (entity instanceof EntityPlayer) {
                i = EnchantmentHelper.getLootingModifier((EntityLivingBase) entity);
            }

            captureDrops = true;
            capturedDrops.clear();
            int j = 0;

            if (!this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                this.dropFewItems(this.recentlyHit > 0, i);
                this.dropEquipment(this.recentlyHit > 0, i);

                if (this.recentlyHit > 0) {
                    j = this.rand.nextInt(200) - i;

                    if (j < 5) {
                        this.dropRareDrop(j <= 0 ? 1 : 0);
                    }
                }
            }

            captureDrops = false;

            if (!ForgeHooks.onLivingDrops(this, par1DamageSource, capturedDrops, i, recentlyHit > 0, j)) {
                // Cauldron start - capture drops for plugins then fire event
                if (!capturedDrops.isEmpty()) {
                    final java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();
                    for (final EntityItem item : capturedDrops) {
                        loot.add(CraftItemStack.asCraftMirror(item.getEntityItem()));
                    }
                    CraftEventFactory.callEntityDeathEvent(this, loot);
                } else {
                    CraftEventFactory.callEntityDeathEvent(this);
                }
                // Cauldron end
                for (final EntityItem item : capturedDrops) {
                    worldObj.spawnEntityInWorld(item);
                }
            }
        }

        this.worldObj.setEntityState(this, (byte) 3);
    }

    /**
     * Drop the equipment for this entity.
     */
    protected void dropEquipment(final boolean par1, final int par2) {
    }

    /**
     * knocks back this entity
     */
    public void knockBack(final Entity par1Entity, final float par2, final double par3, final double par5) {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
            this.isAirBorne = true;
            final float f1 = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
            final float f2 = 0.4F;
            this.motionX /= 2.0D;
            this.motionY /= 2.0D;
            this.motionZ /= 2.0D;
            this.motionX -= par3 / (double) f1 * (double) f2;
            this.motionY += (double) f2;
            this.motionZ -= par5 / (double) f1 * (double) f2;

            if (this.motionY > 0.4000000059604645D) {
                this.motionY = 0.4000000059604645D;
            }
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound() {
        return "damage.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound() {
        return "damage.hit";
    }

    protected void dropRareDrop(final int par1) {
    } // Cauldron - use Forge method since we already capture drops

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(final boolean par1, final int par2) {
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder() {
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_double(this.boundingBox.minY);
        final int k = MathHelper.floor_double(this.posZ);
        final int l = this.worldObj.getBlockId(i, j, k);
        return ForgeHooks.isLivingOnLadder(Block.blocksList[l], worldObj, i, j, k, this);
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive() {
        return !this.isDead && this.getHealth() > 0.0F;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1) {
        float par11 = ForgeHooks.onLivingFall(this, par1);
        if (par11 <= 0) return;
        super.fall(par11);
        final PotionEffect potioneffect = this.getActivePotionEffect(Potion.jump);
        final float f1 = potioneffect != null ? (float) (potioneffect.getAmplifier() + 1) : 0.0F;
        // CraftBukkit start
        float i = MathHelper.ceiling_float_int(par11 - 3.0F - f1);

        if (i > 0) {
            final EntityDamageEvent event = CraftEventFactory.callEntityDamageEvent(null, this, EntityDamageEvent.DamageCause.FALL, i);

            if (event.isCancelled()) {
                return;
            }

            i = (float) event.getDamage();

            if (i > 0) {
                this.getBukkitEntity().setLastDamageCause(event);
            }
        }

        // CraftBukkit end

        if (i > 0) {
            if (i > 4) {
                this.playSound("damage.fallbig", 1.0F, 1.0F);
            } else {
                this.playSound("damage.fallsmall", 1.0F, 1.0F);
            }

            this.attackEntityFrom(DamageSource.fall, (float) i);
            final int j = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.20000000298023224D - (double) this.yOffset), MathHelper.floor_double(this.posZ));

            if (j > 0) {
                final StepSound stepsound = Block.blocksList[j].stepSound;
                this.playSound(stepsound.getStepSound(), stepsound.getVolume() * 0.5F, stepsound.getPitch() * 0.75F);
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation() {
        this.hurtTime = this.maxHurtTime = 10;
        this.attackedAtYaw = 0.0F;
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue() {
        int i = 0;
        final ItemStack[] aitemstack = this.getLastActiveItems();
        final int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            final ItemStack itemstack = aitemstack[k];

            if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
                final int l = ((ItemArmor) itemstack.getItem()).damageReduceAmount;
                i += l;
            }
        }

        return i;
    }

    protected void damageArmor(final float par1) {
    }

    /**
     * Reduces damage, depending on armor
     */
    protected float applyArmorCalculations(final DamageSource par1DamageSource, float par2) {
        float par21 = par2;
        if (!par1DamageSource.isUnblockable()) {
            final int i = 25 - this.getTotalArmorValue();
            final float f1 = par21 * (float) i;
            this.damageArmor(par21);
            par21 = f1 / 25.0F;
        }

        return par21;
    }

    /**
     * Reduces damage, depending on potions
     */
    protected float applyPotionDamageCalculations(final DamageSource par1DamageSource, float par2) {
        float par21 = par2;
        if (this instanceof EntityZombie) {
            par21 = par21;
        }

        int i;
        int j;
        float f1;

        if (this.isPotionActive(Potion.resistance) && par1DamageSource != DamageSource.outOfWorld) {
            i = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
            j = 25 - i;
            f1 = par21 * (float) j;
            par21 = f1 / 25.0F;
        }

        if (par21 <= 0.0F) {
            return 0.0F;
        } else {
            i = EnchantmentHelper.getEnchantmentModifierDamage(this.getLastActiveItems(), par1DamageSource);

            if (i > 20) {
                i = 20;
            }

            if (i > 0 && i <= 20) {
                j = 25 - i;
                f1 = par21 * (float) j;
                par21 = f1 / 25.0F;
            }

            return par21;
        }
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
            par21 = this.applyArmorCalculations(par1DamageSource, par21);
            par21 = this.applyPotionDamageCalculations(par1DamageSource, par21);
            final float f1 = par21;
            par21 = Math.max(par21 - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - par21));

            if (par21 != 0.0F) {
                final float f2 = this.getHealth();
                this.setHealth(f2 - par21);
                this.func_110142_aN().func_94547_a(par1DamageSource, f2, par21);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - par21);
            }
        }
    }

    public CombatTracker func_110142_aN() {
        return this._combatTracker;
    }

    public EntityLivingBase func_94060_bK() {
        return (EntityLivingBase) (this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
    }

    public final float getMaxHealth() {
        return (float) this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    /**
     * counts the amount of arrows stuck in the entity. getting hit by arrows increases this, used in rendering
     */
    public final int getArrowCountInEntity() {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    /**
     * sets the amount of arrows stuck in the entity. used for rendering those
     */
    public final void setArrowCountInEntity(final int par1) {
        this.dataWatcher.updateObject(9, Byte.valueOf((byte) par1));
    }

    /**
     * Returns an integer indicating the end point of the swing animation, used by {@link #swingProgress} to provide a
     * progress indicator. Takes dig speed enchantments into account.
     */
    private int getArmSwingAnimationEnd() {
        return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem() {
        final ItemStack stack = this.getHeldItem();

        if (stack != null && stack.getItem() != null) {
            final Item item = stack.getItem();
            if (item.onEntitySwing(this, stack)) {
                return;
            }
        }

        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;

            if (this.worldObj instanceof WorldServer) {
                ((WorldServer) this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet18Animation(this, 1));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1) {
        if (par1 == 2) {
            this.limbSwingAmount = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
            this.attackedAtYaw = 0.0F;
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.attackEntityFrom(DamageSource.generic, 0.0F);
        } else if (par1 == 3) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.setHealth(0.0F);
            this.onDeath(DamageSource.generic);
        } else {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill() {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
    }

    /**
     * Updates the arm swing progress counters and animation progress
     */
    protected void updateArmSwingProgress() {
        final int i = this.getArmSwingAnimationEnd();

        if (this.isSwingInProgress) {
            ++this.swingProgressInt;

            if (this.swingProgressInt >= i) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float) this.swingProgressInt / (float) i;
    }

    public AttributeInstance getEntityAttribute(final Attribute par1Attribute) {
        return this.getAttributeMap().getAttributeInstance(par1Attribute);
    }

    public BaseAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new ServersideAttributeMap();
        }

        return this.attributeMap;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public abstract ItemStack getHeldItem();

    /**
     * 0 = item, 1-n is armor
     */
    public abstract ItemStack getCurrentItemOrArmor(int i);

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public abstract void setCurrentItemOrArmor(int i, ItemStack itemstack);

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(final boolean par1) {
        super.setSprinting(par1);
        final AttributeInstance attributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (attributeinstance.getModifier(sprintingSpeedBoostModifierUUID) != null) {
            attributeinstance.removeModifier(sprintingSpeedBoostModifier);
        }

        if (par1) {
            attributeinstance.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    public abstract ItemStack[] getLastActiveItems();

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 1.0F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked() {
        return this.getHealth() <= 0.0F;
    }

    /**
     * Move the entity to the coordinates informed, but keep yaw/pitch values.
     */
    public void setPositionAndUpdate(final double par1, final double par3, final double par5) {
        this.setLocationAndAngles(par1, par3, par5, this.rotationYaw, this.rotationPitch);
    }

    /**
     * Moves the entity to a position out of the way of its mount.
     */
    public void dismountEntity(final Entity par1Entity) {
        double d0 = par1Entity.posX;
        double d1 = par1Entity.boundingBox.minY + (double) par1Entity.height;
        double d2 = par1Entity.posZ;

        for (double d3 = -1.5D; d3 < 2.0D; ++d3) {
            for (double d4 = -1.5D; d4 < 2.0D; ++d4) {
                if (d3 != 0.0D || d4 != 0.0D) {
                    final int i = (int) (this.posX + d3);
                    final int j = (int) (this.posZ + d4);
                    final AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(d3, 1.0D, d4);

                    if (this.worldObj.getCollidingBlockBounds(axisalignedbb).isEmpty()) {
                        if (this.worldObj.doesBlockHaveSolidTopSurface(i, (int) this.posY, j)) {
                            this.setPositionAndUpdate(this.posX + d3, this.posY + 1.0D, this.posZ + d4);
                            return;
                        }

                        if (this.worldObj.doesBlockHaveSolidTopSurface(i, (int) this.posY - 1, j) || this.worldObj.getBlockMaterial(i, (int) this.posY - 1, j) == Material.water) {
                            d0 = this.posX + d3;
                            d1 = this.posY + 1.0D;
                            d2 = this.posZ + d4;
                        }
                    }
                }
            }
        }

        this.setPositionAndUpdate(d0, d1, d2);
    }

    @SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the Icon Index of the item currently held
     */
    public Icon getItemIcon(final ItemStack par1ItemStack, final int par2) {
        return par1ItemStack.getIconIndex();
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump() {
        this.motionY = 0.41999998688697815D;

        if (this.isPotionActive(Potion.jump)) {
            this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting()) {
            final float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motionZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(final float par1, final float par2) {
        double d0;

        if (this.isInWater() && (!(this instanceof EntityPlayer) || !((EntityPlayer) this).capabilities.isFlying)) {
            d0 = this.posY;
            this.moveFlying(par1, par2, this.isAIEnabled() ? 0.04F : 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
                this.motionY = 0.30000001192092896D;
            }
        } else if (this.handleLavaMovement() && (!(this instanceof EntityPlayer) || !((EntityPlayer) this).capabilities.isFlying)) {
            d0 = this.posY;
            this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
                this.motionY = 0.30000001192092896D;
            }
        } else {
            float f2 = 0.91F;

            if (this.onGround) {
                f2 = 0.54600006F;
                final int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

                if (i > 0) {
                    f2 = Block.blocksList[i].slipperiness * 0.91F;
                }
            }

            final float f3 = 0.16277136F / (f2 * f2 * f2);
            final float f4;

            if (this.onGround) {
                f4 = this.getAIMoveSpeed() * f3;
            } else {
                f4 = this.jumpMovementFactor;
            }

            this.moveFlying(par1, par2, f4);
            f2 = 0.91F;

            if (this.onGround) {
                f2 = 0.54600006F;
                final int j = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

                if (j > 0) {
                    f2 = Block.blocksList[j].slipperiness * 0.91F;
                }
            }

            if (this.isOnLadder()) {
                final float f5 = 0.15F;

                if (this.motionX < (double) (-f5)) {
                    this.motionX = (double) (-f5);
                }

                if (this.motionX > (double) f5) {
                    this.motionX = (double) f5;
                }

                if (this.motionZ < (double) (-f5)) {
                    this.motionZ = (double) (-f5);
                }

                if (this.motionZ > (double) f5) {
                    this.motionZ = (double) f5;
                }

                this.fallDistance = 0.0F;

                if (this.motionY < -0.15D) {
                    this.motionY = -0.15D;
                }

                final boolean flag = this.isSneaking() && this instanceof EntityPlayer;

                if (flag && this.motionY < 0.0D) {
                    this.motionY = 0.0D;
                }
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && this.isOnLadder()) {
                this.motionY = 0.2D;
            }

            if (this.worldObj.isRemote && (!this.worldObj.blockExists((int) this.posX, 0, (int) this.posZ) || !this.worldObj.getChunkFromBlockCoords((int) this.posX, (int) this.posZ).isChunkLoaded)) {
                if (this.posY > 0.0D) {
                    this.motionY = -0.1D;
                } else {
                    this.motionY = 0.0D;
                }
            } else {
                this.motionY -= 0.08D;
            }

            this.motionY *= 0.9800000190734863D;
            this.motionX *= (double) f2;
            this.motionZ *= (double) f2;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        d0 = this.posX - this.prevPosX;
        final double d1 = this.posZ - this.prevPosZ;
        float f6 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f6 > 1.0F) {
            f6 = 1.0F;
        }

        this.limbSwingAmount += (f6 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled() {
        return false;
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed() {
        return this.isAIEnabled() ? this.landMovementFactor : 0.1F;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(final float par1) {
        this.landMovementFactor = par1;
    }

    public boolean attackEntityAsMob(final Entity par1Entity) {
        this.setLastAttacker(par1Entity);
        return false;
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping() {
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        if (ForgeHooks.onLivingUpdate(this)) return;
        SpigotTimings.timerEntityBaseTick.startTiming(); // Spigot
        super.onUpdate();

        if (!this.worldObj.isRemote) {
            final int i = this.getArrowCountInEntity();

            if (i > 0) {
                if (this.arrowHitTimer <= 0) {
                    this.arrowHitTimer = 20 * (30 - i);
                }

                --this.arrowHitTimer;

                if (this.arrowHitTimer <= 0) {
                    this.setArrowCountInEntity(i - 1);
                }
            }

            for (int j = 0; j < 5; ++j) {
                final ItemStack itemstack = this.previousEquipment[j];
                final ItemStack itemstack1 = this.getCurrentItemOrArmor(j);

                if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                    ((WorldServer) this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet5PlayerInventory(this.entityId, j, itemstack1));

                    if (itemstack != null) {
                        this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
                    }

                    if (itemstack1 != null) {
                        this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
                    }

                    this.previousEquipment[j] = itemstack1 == null ? null : itemstack1.copy();
                }
            }
        }

        SpigotTimings.timerEntityBaseTick.stopTiming(); // Spigot
        this.onLivingUpdate();
        SpigotTimings.timerEntityTickRest.startTiming(); // Spigot
        final double d0 = this.posX - this.prevPosX;
        final double d1 = this.posZ - this.prevPosZ;
        final float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.renderYawOffset;
        float f2 = 0.0F;
        this.field_70768_au = this.field_110154_aX;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            f1 = (float) TrigMath.atan2(d1, d0) * 180.0F / (float) Math.PI - 90.0F;  // CraftBukkit - Math -> TrigMath
        }

        if (this.swingProgress > 0.0F) {
            f1 = this.rotationYaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.field_110154_aX += (f3 - this.field_110154_aX) * 0.3F;
        this.worldObj.theProfiler.startSection("headTurn");
        f2 = this.func_110146_f(f1, f2);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
            this.prevRotationYawHead += 360.0F;
        }

        this.worldObj.theProfiler.endSection();
        this.field_70764_aw += f2;
        SpigotTimings.timerEntityTickRest.stopTiming(); // Spigot
    }

    protected float func_110146_f(final float par1, float par2) {
        float par21 = par2;
        final float f2 = MathHelper.wrapAngleTo180_float(par1 - this.renderYawOffset);
        this.renderYawOffset += f2 * 0.3F;
        float f3 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        final boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - f3;

        if (f3 * f3 > 2500.0F) {
            this.renderYawOffset += f3 * 0.2F;
        }

        if (flag) {
            par21 *= -1.0F;
        }

        return par21;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }

        if (this.newPosRotationIncrements > 0) {
            final double d0 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
            final double d1 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
            final double d2 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
            final double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.newPosRotationIncrements);
            this.rotationPitch = (float) ((double) this.rotationPitch + (this.newRotationPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if (!this.isClientWorld()) {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D) {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D) {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D) {
            this.motionZ = 0.0D;
        }

        SpigotTimings.timerEntityAI.startTiming(); // Spigot
        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if (this.isClientWorld()) {
            if (this.isAIEnabled()) {
                this.worldObj.theProfiler.startSection("newAi");
                this.updateAITasks();
                this.worldObj.theProfiler.endSection();
            } else {
                this.worldObj.theProfiler.startSection("oldAi");
                this.updateEntityActionState();
                this.worldObj.theProfiler.endSection();
                this.rotationYawHead = this.rotationYaw;
            }
        }
        SpigotTimings.timerEntityAI.stopTiming(); // Spigot

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");

        if (this.isJumping) {
            if (!this.isInWater() && !this.handleLavaMovement()) {
                if (this.onGround && this.jumpTicks == 0) {
                    this.jump();
                    this.jumpTicks = 10;
                }
            } else {
                this.motionY += 0.03999999910593033D;
            }
        } else {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;

        SpigotTimings.timerEntityAIMove.startTiming(); // Spigot
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        SpigotTimings.timerEntityAIMove.stopTiming(); // Spigot

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isRemote) {
            SpigotTimings.timerEntityAICollision.startTiming(); // Spigot
            this.collideWithNearbyEntities();
            SpigotTimings.timerEntityAICollision.stopTiming(); // Spigot

        }

        this.worldObj.theProfiler.endSection();
    }

    protected void updateAITasks() {
    }

    protected void collideWithNearbyEntities() {
        if (!this.canBePushed()) return; // Cauldron don't get the list if the entity can't be pushed
        final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (this.canBePushed() && list != null && !list.isEmpty()) // Spigot: Add this.canBePushed() condition
        {
            for (int i = 0; i < list.size(); ++i) {
                final Entity entity = (Entity) list.get(i);

                // TODO better check now?
                // CraftBukkit start - Only handle mob (non-player) collisions every other tick
                if (entity instanceof EntityLivingBase && !(this instanceof EntityPlayerMP) && this.ticksExisted % 2 == 0) {
                    continue;
                }

                // CraftBukkit end

                if (entity.canBePushed()) {
                    this.collideWithEntity(entity);
                }
            }
        }
    }

    protected void collideWithEntity(final Entity par1Entity) {
        par1Entity.applyEntityCollision(this);
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden() {
        super.updateRidden();
        this.field_70768_au = this.field_110154_aX;
        this.field_110154_aX = 0.0F;
        this.fallDistance = 0.0F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(final double par1, final double par3, final double par5, final float par7, final float par8, final int par9) {
        this.yOffset = 0.0F;
        this.newPosX = par1;
        this.newPosY = par3;
        this.newPosZ = par5;
        this.newRotationYaw = (double) par7;
        this.newRotationPitch = (double) par8;
        this.newPosRotationIncrements = par9;
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick() {
    }

    protected void updateEntityActionState() {
        ++this.entityAge;
    }

    public void setJumping(final boolean par1) {
        this.isJumping = par1;
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void onItemPickup(final Entity par1Entity, final int par2) {
        if (!par1Entity.isDead && !this.worldObj.isRemote) {
            final EntityTracker entitytracker = ((WorldServer) this.worldObj).getEntityTracker();

            if (par1Entity instanceof EntityItem) {
                entitytracker.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }

            if (par1Entity instanceof EntityArrow) {
                entitytracker.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }

            if (par1Entity instanceof EntityXPOrb) {
                entitytracker.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }
        }
    }

    /**
     * returns true if the entity provided in the argument can be seen. (Raytrace)
     */
    public boolean canEntityBeSeen(final Entity par1Entity) {
        return this.worldObj.clip(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ), this.worldObj.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY + (double) par1Entity.getEyeHeight(), par1Entity.posZ)) == null;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec() {
        return this.getLook(1.0F);
    }

    /**
     * interpolated look vector
     */
    public Vec3 getLook(final float par1) {
        final float f1;
        final float f2;
        final float f3;
        final float f4;

        if (par1 == 1.0F) {
            f1 = MathHelper.cos(-this.rotationYaw * 0.017453292F - (float) Math.PI);
            f2 = MathHelper.sin(-this.rotationYaw * 0.017453292F - (float) Math.PI);
            f3 = -MathHelper.cos(-this.rotationPitch * 0.017453292F);
            f4 = MathHelper.sin(-this.rotationPitch * 0.017453292F);
            return this.worldObj.getWorldVec3Pool().getVecFromPool((double) (f2 * f3), (double) f4, (double) (f1 * f3));
        } else {
            f1 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * par1;
            f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * par1;
            f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
            f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
            final float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            final float f6 = MathHelper.sin(-f1 * 0.017453292F);
            return this.worldObj.getWorldVec3Pool().getVecFromPool((double) (f4 * f5), (double) f6, (double) (f3 * f5));
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args: partialTickTime
     */
    public float getSwingProgress(final float par1) {
        float f1 = this.swingProgress - this.prevSwingProgress;

        if (f1 < 0.0F) {
            ++f1;
        }

        return this.prevSwingProgress + f1 * par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * interpolated position vector
     */
    public Vec3 getPosition(final float par1) {
        if (par1 == 1.0F) {
            return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
        } else {
            final double d0 = this.prevPosX + (this.posX - this.prevPosX) * (double) par1;
            final double d1 = this.prevPosY + (this.posY - this.prevPosY) * (double) par1;
            final double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double) par1;
            return this.worldObj.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Performs a ray trace for the distance specified and using the partial tick time. Args: distance, partialTickTime
     */
    public MovingObjectPosition rayTrace(final double par1, final float par3) {
        final Vec3 vec3 = this.getPosition(par3);
        final Vec3 vec31 = this.getLook(par3);
        final Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        return this.worldObj.clip(vec3, vec32);
    }

    /**
     * Returns whether the entity is in a local (client) world
     */
    public boolean isClientWorld() {
        return !this.worldObj.isRemote;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed() {
        return !this.isDead;
    }

    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked() {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(final float par1) {
        this.rotationYawHead = par1;
    }

    public float getAbsorptionAmount() {
        return this.field_110151_bq;
    }

    public void setAbsorptionAmount(float par1) {
        float par11 = par1;
        if (par11 < 0.0F) {
            par11 = 0.0F;
        }

        this.field_110151_bq = par11;
    }

    public Team getTeam() {
        return null;
    }

    public boolean isOnSameTeam(final EntityLivingBase par1EntityLivingBase) {
        return this.isOnTeam(par1EntityLivingBase.getTeam());
    }

    /**
     * Returns true if the entity is on a specific team.
     */
    public boolean isOnTeam(final Team par1Team) {
        return this.getTeam() != null ? this.getTeam().isSameTeam(par1Team) : false;
    }

    /***
     * Removes all potion effects that have curativeItem as a curative item for its effect
     * @param curativeItem The itemstack we are using to cure potion effects
     */
    public void curePotionEffects(final ItemStack curativeItem) {
        final Iterator<Integer> potionKey = activePotionsMap.keySet().iterator();

        if (worldObj.isRemote) {
            return;
        }

        while (potionKey.hasNext()) {
            final Integer key = potionKey.next();
            final PotionEffect effect = (PotionEffect) activePotionsMap.get(key);

            if (effect.isCurativeItem(curativeItem)) {
                potionKey.remove();
                onFinishedPotionEffect(effect);
            }
        }
    }

    /**
     * Returns true if the entity's rider (EntityPlayer) should face forward when mounted.
     * currently only used in vanilla code by pigs.
     *
     * @param player The player who is riding the entity.
     * @return If the player should orient the same direction as this entity.
     */
    public boolean shouldRiderFaceForward(final EntityPlayer player) {
        return this instanceof EntityPig;
    }
}
