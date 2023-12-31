package net.minecraft.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityWitch extends EntityMob implements IRangedAttackMob
{
    private static final UUID field_110184_bp = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier field_110185_bq = (new AttributeModifier(field_110184_bp, "Drinking speed penalty", -0.25D, 0)).setSaved(false);

    /** List of items a witch should drop on death. */
    private static final int[] witchDrops = {Item.glowstone.itemID, Item.sugar.itemID, Item.redstone.itemID, Item.spiderEye.itemID, Item.glassBottle.itemID, Item.gunpowder.itemID, Item.stick.itemID, Item.stick.itemID};

    /**
     * Timer used as interval for a witch's attack, decremented every tick if aggressive and when reaches zero the witch
     * will throw a potion at the target entity.
     */
    private int witchAttackTimer;

    public EntityWitch(final World par1World)
    {
        super(par1World);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.getDataWatcher().addObject(21, Byte.valueOf((byte)0));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.witch.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.witch.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.witch.death";
    }

    /**
     * Set whether this witch is aggressive at an entity.
     */
    public void setAggressive(final boolean par1)
    {
        this.getDataWatcher().updateObject(21, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    /**
     * Return whether this witch is aggressive at an entity.
     */
    public boolean getAggressive()
    {
        return this.getDataWatcher().getWatchableObjectByte(21) == 1;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(26.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.getAggressive())
            {
                if (this.witchAttackTimer-- <= 0)
                {
                    this.setAggressive(false);
                    final ItemStack itemstack = this.getHeldItem();
                    this.setCurrentItemOrArmor(0, (ItemStack)null);

                    if (itemstack != null && itemstack.itemID == Item.potion.itemID)
                    {
                        final List list = Item.potion.getEffects(itemstack);

                        if (list != null)
                        {
                            final Iterator iterator = list.iterator();

                            while (iterator.hasNext())
                            {
                                final PotionEffect potioneffect = (PotionEffect)iterator.next();
                                this.addPotionEffect(new PotionEffect(potioneffect));
                            }
                        }
                    }

                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(field_110185_bq);
                }
            }
            else
            {
                short short1 = -1;

                if (this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(Potion.fireResistance))
                {
                    short1 = 16307;
                }
                else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth())
                {
                    short1 = 16341;
                }
                else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D)
                {
                    short1 = 16274;
                }
                else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D)
                {
                    short1 = 16274;
                }

                if (short1 > -1)
                {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.potion, 1, short1));
                    this.witchAttackTimer = this.getHeldItem().getMaxItemUseDuration();
                    this.setAggressive(true);
                    final AttributeInstance attributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                    attributeinstance.removeModifier(field_110185_bq);
                    attributeinstance.applyModifier(field_110185_bq);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F)
            {
                this.worldObj.setEntityState(this, (byte)15);
            }
        }

        super.onLivingUpdate();
    }

    /**
     * Reduces damage, depending on potions
     */
    protected float applyPotionDamageCalculations(final DamageSource par1DamageSource, float par2)
    {
        float par21 = par2;
        par21 = super.applyPotionDamageCalculations(par1DamageSource, par21);

        if (par1DamageSource.getEntity() == this)
        {
            par21 = 0.0F;
        }

        if (par1DamageSource.isMagicDamage())
        {
            par21 = (float)((double) par21 * 0.15D);
        }

        return par21;
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1)
    {
        if (par1 == 15)
        {
            for (int i = 0; i < this.rand.nextInt(35) + 10; ++i)
            {
                this.worldObj.spawnParticle("witchMagic", this.posX + this.rand.nextGaussian() * 0.12999999523162842D, this.boundingBox.maxY + 0.5D + this.rand.nextGaussian() * 0.12999999523162842D, this.posZ + this.rand.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D);
            }
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(final boolean par1, final int par2)
    {
        final int j = this.rand.nextInt(3) + 1;

        for (int k = 0; k < j; ++k)
        {
            int l = this.rand.nextInt(3);
            final int i1 = witchDrops[this.rand.nextInt(witchDrops.length)];

            if (par2 > 0)
            {
                l += this.rand.nextInt(par2 + 1);
            }

            for (int j1 = 0; j1 < l; ++j1)
            {
                this.dropItem(i1, 1);
            }
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        if (!this.getAggressive())
        {
            final EntityPotion entitypotion = new EntityPotion(this.worldObj, this, 32732);
            entitypotion.rotationPitch -= -20.0F;
            final double d0 = par1EntityLivingBase.posX + par1EntityLivingBase.motionX - this.posX;
            final double d1 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858D - this.posY;
            final double d2 = par1EntityLivingBase.posZ + par1EntityLivingBase.motionZ - this.posZ;
            final float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

            if (f1 >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.moveSlowdown))
            {
                entitypotion.setPotionDamage(32698);
            }
            else if (par1EntityLivingBase.getHealth() >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.poison))
            {
                entitypotion.setPotionDamage(32660);
            }
            else if (f1 <= 3.0F && !par1EntityLivingBase.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25F)
            {
                entitypotion.setPotionDamage(32696);
            }

            entitypotion.setThrowableHeading(d0, d1 + (double)(f1 * 0.2F), d2, 0.75F, 8.0F);
            this.worldObj.spawnEntityInWorld(entitypotion);
        }
    }
}
