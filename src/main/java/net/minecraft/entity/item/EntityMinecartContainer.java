package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

// CraftBukkit start
// CraftBukkit end

public abstract class EntityMinecartContainer extends EntityMinecart implements IInventory
{
    private ItemStack[] minecartContainerItems = new ItemStack[36];

    /**
     * When set to true, the minecart will drop all items when setDead() is called. When false (such as when travelling
     * dimensions) it preserves its contents.
     */
    private boolean dropContentsWhenDead = true;

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents()
    {
        return this.minecartContainerItems;
    }

    public void onOpen(final CraftHumanEntity who)
    {
        transaction.add(who);
    }

    public void onClose(final CraftHumanEntity who)
    {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers()
    {
        return transaction;
    }

    public InventoryHolder getOwner()
    {
        final org.bukkit.entity.Entity cart = getBukkitEntity();

        if (cart instanceof InventoryHolder)
        {
            return (InventoryHolder) cart;
        }

        return null;
    }

    public void setMaxStackSize(final int size)
    {
        maxStack = size;
    }
    // CraftBukkit end

    public EntityMinecartContainer(final World par1World)
    {
        super(par1World);
    }

    public EntityMinecartContainer(final World par1World, final double par2, final double par4, final double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public void killMinecart(final DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);

        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            final ItemStack itemstack = this.getStackInSlot(i);

            if (itemstack != null)
            {
                final float f = this.rand.nextFloat() * 0.8F + 0.1F;
                final float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                final float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                while (itemstack.stackSize > 0)
                {
                    int j = this.rand.nextInt(21) + 10;

                    if (j > itemstack.stackSize)
                    {
                        j = itemstack.stackSize;
                    }

                    itemstack.stackSize -= j;
                    final EntityItem entityitem = new EntityItem(this.worldObj, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));
                    final float f3 = 0.05F;
                    entityitem.motionX = (double)((float)this.rand.nextGaussian() * f3);
                    entityitem.motionY = (double)((float)this.rand.nextGaussian() * f3 + 0.2F);
                    entityitem.motionZ = (double)((float)this.rand.nextGaussian() * f3);
                    this.worldObj.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(final int par1)
    {
        return this.minecartContainerItems[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(final int par1, final int par2)
    {
        if (this.minecartContainerItems[par1] != null)
        {
            final ItemStack itemstack;

            if (this.minecartContainerItems[par1].stackSize <= par2)
            {
                itemstack = this.minecartContainerItems[par1];
                this.minecartContainerItems[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.minecartContainerItems[par1].splitStack(par2);

                if (this.minecartContainerItems[par1].stackSize == 0)
                {
                    this.minecartContainerItems[par1] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(final int par1)
    {
        if (this.minecartContainerItems[par1] != null)
        {
            final ItemStack itemstack = this.minecartContainerItems[par1];
            this.minecartContainerItems[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack)
    {
        this.minecartContainerItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged() {}

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer)
    {
        return this.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack)
    {
        return true;
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return this.isInvNameLocalized() ? this.func_95999_t() : "container.minecart";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(final int par1)
    {
        // Spigot Start
        for (final HumanEntity human : new java.util.ArrayList<HumanEntity>(transaction))
        {
            human.closeInventory();
        }

        // Spigot End
        this.dropContentsWhenDead = false;
        super.travelToDimension(par1);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        if (this.dropContentsWhenDead)
        {
            for (int i = 0; i < this.getSizeInventory(); ++i)
            {
                final ItemStack itemstack = this.getStackInSlot(i);

                if (itemstack != null)
                {
                    final float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    final float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                    final float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int j = this.rand.nextInt(21) + 10;

                        if (j > itemstack.stackSize)
                        {
                            j = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j;
                        final EntityItem entityitem = new EntityItem(this.worldObj, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }

                        final float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.rand.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.rand.nextGaussian() * f3);
                        this.worldObj.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }

        super.setDead();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        final NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.minecartContainerItems.length; ++i)
        {
            if (this.minecartContainerItems[i] != null)
            {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.minecartContainerItems[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            final int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.minecartContainerItems.length)
            {
                this.minecartContainerItems[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(final EntityPlayer par1EntityPlayer)
    {
        if(MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, par1EntityPlayer))) 
        {
            return true;
        }
        if (!this.worldObj.isRemote)
        {
            par1EntityPlayer.displayGUIChest(this);
        }

        return true;
    }

    protected void applyDrag()
    {
        final int i = 15 - Container.calcRedstoneFromInventory(this);
        final float f = 0.98F + (float)i * 0.001F;
        this.motionX *= (double)f;
        this.motionY *= 0.0D;
        this.motionZ *= (double)f;
    }
}
