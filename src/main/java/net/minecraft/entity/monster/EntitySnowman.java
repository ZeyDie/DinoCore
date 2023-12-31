package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.event.block.EntityBlockFormEvent;

// CraftBukkit start
// CraftBukkit end

public class EntitySnowman extends EntityGolem implements IRangedAttackMob
{
    public EntitySnowman(final World par1World)
    {
        super(par1World);
        this.setSize(0.4F, 1.8F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.mobSelector));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }

        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getBiomeGenForCoords(i, j).getFloatTemperature() > 1.0F)
        {
            this.attackEntityFrom(CraftEventFactory.MELTING, 1.0F); // CraftBukkit - DamageSource.BURN -> CraftEventFactory.MELTING
        }

        for (i = 0; i < 4; ++i)
        {
            j = MathHelper.floor_double(this.posX + (double)((float)(i % 2 * 2 - 1) * 0.25F));
            final int k = MathHelper.floor_double(this.posY);
            final int l = MathHelper.floor_double(this.posZ + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25F));

            if (this.worldObj.getBlockId(j, k, l) == 0 && this.worldObj.getBiomeGenForCoords(j, l).getFloatTemperature() < 0.8F && Block.snow.canPlaceBlockAt(this.worldObj, j, k, l))
            {
                // CraftBukkit start
                final org.bukkit.block.BlockState blockState = this.worldObj.getWorld().getBlockAt(j, k, l).getState();
                blockState.setTypeId(Block.snow.blockID);
                final EntityBlockFormEvent event = new EntityBlockFormEvent(this.getBukkitEntity(), blockState.getBlock(), blockState);
                this.worldObj.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    blockState.update(true);
                }

                // CraftBukkit end
            }
        }
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.snowball.itemID;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(final boolean par1, final int par2)
    {
        final int j = this.rand.nextInt(16);

        for (int k = 0; k < j; ++k)
        {
            this.dropItem(Item.snowball.itemID, 1);
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(final EntityLivingBase par1EntityLivingBase, final float par2)
    {
        final EntitySnowball entitysnowball = new EntitySnowball(this.worldObj, this);
        final double d0 = par1EntityLivingBase.posX - this.posX;
        final double d1 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858D - entitysnowball.posY;
        final double d2 = par1EntityLivingBase.posZ - this.posZ;
        final float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entitysnowball);
    }
}
