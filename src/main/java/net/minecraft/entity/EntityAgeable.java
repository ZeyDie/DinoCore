package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature
{
    private float field_98056_d = -1.0F;
    private float field_98057_e;
    public boolean ageLocked = false; // CraftBukkit

    public EntityAgeable(final World par1World)
    {
        super(par1World);
    }

    public abstract EntityAgeable createChild(EntityAgeable entityageable);

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(final EntityPlayer par1EntityPlayer)
    {
        final ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

        if (itemstack != null && itemstack.itemID == Item.monsterPlacer.itemID)
        {
            if (!this.worldObj.isRemote)
            {
                final Class oclass = EntityList.getClassFromID(itemstack.getItemDamage());

                if (oclass != null && oclass.isAssignableFrom(this.getClass()))
                {
                    final EntityAgeable entityageable = this.createChild(this);

                    if (entityageable != null)
                    {
                        entityageable.setGrowingAge(-24000);
                        entityageable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                        this.worldObj.addEntity(entityageable, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG); // CraftBukkit

                        if (itemstack.hasDisplayName())
                        {
                            entityageable.setCustomNameTag(itemstack.getDisplayName());
                        }

                        if (!par1EntityPlayer.capabilities.isCreativeMode)
                        {
                            --itemstack.stackSize;

                            if (itemstack.stackSize == 0)    // CraftBukkit - allow less than 0 stacks as "infinite"
                            {
                                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                            }
                        }
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(12, new Integer(0));
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. Don't confuse this with EntityLiving.getAge. With a negative value the
     * Entity is considered a child.
     */
    public int getGrowingAge()
    {
        return this.dataWatcher.getWatchableObjectInt(12);
    }

    /**
     * "Adds the value of the parameter times 20 to the age of this entity. If the entity is an adult (if the entity's
     * age is greater than 0), it will have no effect."
     */
    public void addGrowth(final int par1)
    {
        int j = this.getGrowingAge();
        j += par1 * 20;

        if (j > 0)
        {
            j = 0;
        }

        this.setGrowingAge(j);
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. With a negative value the Entity is considered a child.
     */
    public void setGrowingAge(final int par1)
    {
        this.dataWatcher.updateObject(12, Integer.valueOf(par1));
        this.setScaleForAge(this.isChild());
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Age", this.getGrowingAge());
        par1NBTTagCompound.setBoolean("AgeLocked", this.ageLocked); // CraftBukkit
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setGrowingAge(par1NBTTagCompound.getInteger("Age"));
        this.ageLocked = par1NBTTagCompound.getBoolean("AgeLocked"); // CraftBukkit
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.worldObj.isRemote || this.ageLocked)   // CraftBukkit
        {
            this.setScaleForAge(this.isChild());
        }
        else
        {
            int i = this.getGrowingAge();

            if (i < 0)
            {
                ++i;
                this.setGrowingAge(i);
            }
            else if (i > 0)
            {
                --i;
                this.setGrowingAge(i);
            }
        }
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return this.getGrowingAge() < 0;
    }

    /**
     * "Sets the scale for an ageable entity according to the boolean parameter, which says if it's a child."
     */
    public void setScaleForAge(final boolean par1)
    {
        this.setScale(par1 ? 0.5F : 1.0F);
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected final void setSize(final float par1, final float par2)
    {
        final boolean flag = this.field_98056_d > 0.0F;
        this.field_98056_d = par1;
        this.field_98057_e = par2;

        if (!flag)
        {
            this.setScale(1.0F);
        }
    }

    protected final void setScale(final float par1)
    {
        super.setSize(this.field_98056_d * par1, this.field_98057_e * par1);
    }
}
