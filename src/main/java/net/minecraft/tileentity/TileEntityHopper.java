package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

// CraftBukkit start
// CraftBukkit end

public class TileEntityHopper extends TileEntity implements Hopper
{
    private ItemStack[] hopperItemStacks = new ItemStack[5];

    /** The name that is displayed if the hopper was renamed */
    private String inventoryName;
    private int transferCooldown = -1;

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents()
    {
        return this.hopperItemStacks;
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

    public void setMaxStackSize(final int size)
    {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityHopper() {}

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        this.hopperItemStacks = new ItemStack[this.getSizeInventory()];

        if (par1NBTTagCompound.hasKey("CustomName"))
        {
            this.inventoryName = par1NBTTagCompound.getString("CustomName");
        }

        this.transferCooldown = par1NBTTagCompound.getInteger("TransferCooldown");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            final byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.hopperItemStacks.length)
            {
                this.hopperItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        final NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.hopperItemStacks.length; ++i)
        {
            if (this.hopperItemStacks[i] != null)
            {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.hopperItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
        par1NBTTagCompound.setInteger("TransferCooldown", this.transferCooldown);

        if (this.isInvNameLocalized())
        {
            par1NBTTagCompound.setString("CustomName", this.inventoryName);
        }
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.hopperItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(final int par1)
    {
        return this.hopperItemStacks[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(final int par1, final int par2)
    {
        if (this.hopperItemStacks[par1] != null)
        {
            final ItemStack itemstack;

            if (this.hopperItemStacks[par1].stackSize <= par2)
            {
                itemstack = this.hopperItemStacks[par1];
                this.hopperItemStacks[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.hopperItemStacks[par1].splitStack(par2);

                if (this.hopperItemStacks[par1].stackSize == 0)
                {
                    this.hopperItemStacks[par1] = null;
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
        if (this.hopperItemStacks[par1] != null)
        {
            final ItemStack itemstack = this.hopperItemStacks[par1];
            this.hopperItemStacks[par1] = null;
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
        this.hopperItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return this.isInvNameLocalized() ? this.inventoryName : "container.hopper";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized()
    {
        return this.inventoryName != null && !this.inventoryName.isEmpty();
    }

    public void setInventoryName(final String par1Str)
    {
        this.inventoryName = par1Str;
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
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
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
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            --this.transferCooldown;

            if (!this.isCoolingDown())
            {
                this.setTransferCooldown(0);
                this.updateHopper();
            }
        }
    }

    public boolean updateHopper()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            if (!this.isCoolingDown() && BlockHopper.getIsBlockNotPoweredFromMetadata(this.getBlockMetadata()))
            {
                boolean flag = this.insertItemToInventory();
                flag = suckItemsIntoHopper(this) || flag;

                if (flag)
                {
                    this.setTransferCooldown(this.worldObj.spigotConfig.hopperTransfer); // Spigot
                    this.onInventoryChanged();
                    return true;
                }
            }

            // Spigot start
            if (!this.isCoolingDown())
            {
                this.setTransferCooldown(this.worldObj.spigotConfig.hopperCheck);
            }
            // Spigot end
            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * Inserts one item from the hopper into the inventory the hopper is pointing at.
     */
    private boolean insertItemToInventory()
    {
        final IInventory iinventory = this.getOutputInventory();

        if (iinventory == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < this.getSizeInventory(); ++i)
            {
                if (this.getStackInSlot(i) != null)
                {
                    final ItemStack itemstack = this.getStackInSlot(i).copy();
                    // CraftBukkit start - Call event when pushing items into other inventories
                    final CraftItemStack oitemstack = CraftItemStack.asCraftMirror(this.decrStackSize(i, 1));
                    Inventory destinationInventory;

                    // Have to special case large chests as they work oddly
                    if (iinventory instanceof InventoryLargeChest)
                    {
                        destinationInventory = new org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
                    }
                    else
                    {
                        // Cauldron start - support mod inventories, with no owners
                        try {
                            if (iinventory.getOwner() != null) {
                                destinationInventory = iinventory.getOwner().getInventory();
                            } else {
                                // TODO: create a mod inventory for passing to the event, instead of null
                                destinationInventory = null;
                            }
                         } catch (final AbstractMethodError e) { // fixes openblocks AbstractMethodError
                            if (iinventory instanceof net.minecraft.tileentity.TileEntity) {
                                final org.bukkit.inventory.InventoryHolder holder = net.minecraftforge.cauldron.CauldronUtils.getOwner((net.minecraft.tileentity.TileEntity)iinventory);
                                if (holder != null) {
                                    destinationInventory = holder.getInventory();
                                } else {
                                    destinationInventory = null;
                                }
                            } else {
                                destinationInventory = null;
                            }
                        }
                        // Cauldron end
                    }

                    final InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.getOwner().getInventory(), oitemstack.clone(), destinationInventory, true);
                    this.getWorldObj().getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled())
                    {
                        this.setInventorySlotContents(i, itemstack);
                        this.setTransferCooldown(8); // Delay hopper checks
                        return false;
                    }

                    final ItemStack itemstack1 = insertStack(iinventory, CraftItemStack.asNMSCopy(event.getItem()), Facing.oppositeSide[BlockHopper.getDirectionFromMetadata(this.getBlockMetadata())]);

                    if (itemstack1 == null || itemstack1.stackSize == 0)
                    {
                        if (event.getItem().equals(oitemstack))
                        {
                            iinventory.onInventoryChanged();
                        }
                        else
                        {
                            this.setInventorySlotContents(i, itemstack);
                        }

                        // CraftBukkit end
                        return true;
                    }

                    this.setInventorySlotContents(i, itemstack);
                }
            }

            return false;
        }
    }

    /**
     * Sucks one item into the given hopper from an inventory or EntityItem above it.
     */
    public static boolean suckItemsIntoHopper(final Hopper par0Hopper)
    {
        final IInventory iinventory = getInventoryAboveHopper(par0Hopper);

        if (iinventory != null)
        {
            final byte b0 = 0;

            if (iinventory instanceof ISidedInventory && b0 > -1)
            {
                final ISidedInventory isidedinventory = (ISidedInventory)iinventory;
                final int[] aint = isidedinventory.getAccessibleSlotsFromSide(b0);

                for (int i = 0; i < aint.length; ++i)
                {
                    if (insertStackFromInventory(par0Hopper, iinventory, aint[i], b0))
                    {
                        return true;
                    }
                }
            }
            else
            {
                final int j = iinventory.getSizeInventory();

                for (int k = 0; k < j; ++k)
                {
                    if (insertStackFromInventory(par0Hopper, iinventory, k, b0))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            final EntityItem entityitem = getEntityAbove(par0Hopper.getWorldObj(), par0Hopper.getXPos(), par0Hopper.getYPos() + 1.0D, par0Hopper.getZPos());

            if (entityitem != null)
            {
                return insertStackFromEntity(par0Hopper, entityitem);
            }
        }

        return false;
    }

    private static boolean insertStackFromInventory(final Hopper par0Hopper, final IInventory par1IInventory, final int par2, final int par3)
    {
        final ItemStack itemstack = par1IInventory.getStackInSlot(par2);

        if (itemstack != null && canExtractItemFromInventory(par1IInventory, itemstack, par2, par3))
        {
            final ItemStack itemstack1 = itemstack.copy();
            // CraftBukkit start - Call event on collection of items from inventories into the hopper
            final CraftItemStack oitemstack = CraftItemStack.asCraftMirror(par1IInventory.decrStackSize(par2, 1));
            Inventory sourceInventory;

            // Have to special case large chests as they work oddly
            if (par1IInventory instanceof InventoryLargeChest)
            {
                sourceInventory = new org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryDoubleChest((InventoryLargeChest) par1IInventory);
            }
            else
            {
                // Cauldron start - support mod inventories, with no owners
                try
                {
                    if (par1IInventory.getOwner() != null)
                    {
                        sourceInventory = par1IInventory.getOwner().getInventory();
                    } 
                    else
                    {
                        // TODO: create a mod inventory for passing to the event, instead of null
                        sourceInventory = null;
                    }
                }
                catch (final AbstractMethodError e)
                {
                    sourceInventory = null;
                }
                // Cauldron end
            }

            final InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack.clone(), par0Hopper.getOwner().getInventory(), false);
            par0Hopper.getWorldObj().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled())
            {
                par1IInventory.setInventorySlotContents(par2, itemstack1);

                if (par0Hopper instanceof TileEntityHopper)
                {
                    ((TileEntityHopper) par0Hopper).setTransferCooldown(8); // Delay hopper checks
                }
                else if (par0Hopper instanceof EntityMinecartHopper)
                {
                    ((EntityMinecartHopper) par0Hopper).setTransferTicker(4); // Delay hopper minecart checks
                }

                return false;
            }

            final ItemStack itemstack2 = insertStack(par0Hopper, CraftItemStack.asNMSCopy(event.getItem()), -1);

            if (itemstack2 == null || itemstack2.stackSize == 0)
            {
                if (event.getItem().equals(oitemstack))
                {
                    par1IInventory.onInventoryChanged();
                }
                else
                {
                    par1IInventory.setInventorySlotContents(par2, itemstack1);
                }

                // CraftBukkit end
                return true;
            }

            par1IInventory.setInventorySlotContents(par2, itemstack1);
        }

        return false;
    }

    public static boolean insertStackFromEntity(final IInventory par0IInventory, final EntityItem par1EntityItem)
    {
        boolean flag = false;

        if (par1EntityItem == null)
        {
            return false;
        }
        else
        {
            // CraftBukkit start
            // Cauldron start - vanilla compatibility
            if (par0IInventory.getOwner() != null && par1EntityItem.getBukkitEntity() != null)
            {
                final InventoryPickupItemEvent event = new InventoryPickupItemEvent(par0IInventory.getOwner().getInventory(), (org.bukkit.entity.Item) par1EntityItem.getBukkitEntity());
                par1EntityItem.worldObj.getServer().getPluginManager().callEvent(event);
    
                if (event.isCancelled())
                {
                    return false;
                }
            }
            // Cauldron end
            // CraftBukkit end
            final ItemStack itemstack = par1EntityItem.getEntityItem().copy();
            final ItemStack itemstack1 = insertStack(par0IInventory, itemstack, -1);

            if (itemstack1 != null && itemstack1.stackSize != 0)
            {
                par1EntityItem.setEntityItemStack(itemstack1);
            }
            else
            {
                flag = true;
                par1EntityItem.setDead();
            }

            return flag;
        }
    }

    /**
     * Inserts a stack into an inventory. Args: Inventory, stack, side. Returns leftover items.
     */
    public static ItemStack insertStack(final IInventory par0IInventory, ItemStack par1ItemStack, final int par2)
    {
        ItemStack par1ItemStack1 = par1ItemStack;
        if (par0IInventory instanceof ISidedInventory && par2 > -1)
        {
            final ISidedInventory isidedinventory = (ISidedInventory)par0IInventory;
            final int[] aint = isidedinventory.getAccessibleSlotsFromSide(par2);

            for (int j = 0; j < aint.length && par1ItemStack1 != null && par1ItemStack1.stackSize > 0; ++j)
            {
                par1ItemStack1 = func_102014_c(par0IInventory, par1ItemStack1, aint[j], par2);
            }
        }
        else
        {
            final int k = par0IInventory.getSizeInventory();

            for (int l = 0; l < k && par1ItemStack1 != null && par1ItemStack1.stackSize > 0; ++l)
            {
                par1ItemStack1 = func_102014_c(par0IInventory, par1ItemStack1, l, par2);
            }
        }

        if (par1ItemStack1 != null && par1ItemStack1.stackSize == 0)
        {
            par1ItemStack1 = null;
        }

        return par1ItemStack1;
    }

    /**
     * Args: inventory, item, slot, side
     */
    private static boolean canInsertItemToInventory(final IInventory par0IInventory, final ItemStack par1ItemStack, final int par2, final int par3)
    {
        return !par0IInventory.isItemValidForSlot(par2, par1ItemStack) ? false : !(par0IInventory instanceof ISidedInventory) || ((ISidedInventory)par0IInventory).canInsertItem(par2, par1ItemStack, par3);
    }

    private static boolean canExtractItemFromInventory(final IInventory par0IInventory, final ItemStack par1ItemStack, final int par2, final int par3)
    {
        return !(par0IInventory instanceof ISidedInventory) || ((ISidedInventory)par0IInventory).canExtractItem(par2, par1ItemStack, par3);
    }

    private static ItemStack func_102014_c(final IInventory par0IInventory, ItemStack par1ItemStack, final int par2, final int par3)
    {
        ItemStack par1ItemStack1 = par1ItemStack;
        final ItemStack itemstack1 = par0IInventory.getStackInSlot(par2);

        if (canInsertItemToInventory(par0IInventory, par1ItemStack1, par2, par3))
        {
            boolean flag = false;

            if (itemstack1 == null)
            {
                par0IInventory.setInventorySlotContents(par2, par1ItemStack1);
                par1ItemStack1 = null;
                flag = true;
            }
            else if (areItemStacksEqualItem(itemstack1, par1ItemStack1))
            {
                final int k = par1ItemStack1.getMaxStackSize() - itemstack1.stackSize;
                final int l = Math.min(par1ItemStack1.stackSize, k);
                par1ItemStack1.stackSize -= l;
                itemstack1.stackSize += l;
                flag = l > 0;
            }

            if (flag)
            {
                if (par0IInventory instanceof TileEntityHopper)
                {
                    ((TileEntityHopper)par0IInventory).setTransferCooldown(8);
                    par0IInventory.onInventoryChanged();
                }

                par0IInventory.onInventoryChanged();
            }
        }

        return par1ItemStack1;
    }

    /**
     * Gets the inventory the hopper is pointing at.
     */
    private IInventory getOutputInventory()
    {
        final int i = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());
        return getInventoryAtLocation(this.getWorldObj(), (double)(this.xCoord + Facing.offsetsXForSide[i]), (double)(this.yCoord + Facing.offsetsYForSide[i]), (double)(this.zCoord + Facing.offsetsZForSide[i]));
    }

    /**
     * Looks for anything, that can hold items (like chests, furnaces, etc.) one block above the given hopper.
     */
    public static IInventory getInventoryAboveHopper(final Hopper par0Hopper)
    {
        return getInventoryAtLocation(par0Hopper.getWorldObj(), par0Hopper.getXPos(), par0Hopper.getYPos() + 1.0D, par0Hopper.getZPos());
    }

    public static EntityItem getEntityAbove(final World par0World, final double par1, final double par3, final double par5)
    {
        final List list = par0World.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(par1, par3, par5, par1 + 1.0D, par3 + 1.0D, par5 + 1.0D), IEntitySelector.selectAnything);
        return !list.isEmpty() ? (EntityItem)list.get(0) : null;
    }

    /**
     * Gets an inventory at the given location to extract items into or take items from. Can find either a tile entity
     * or regular entity implementing IInventory.
     */
    public static IInventory getInventoryAtLocation(final World par0World, final double par1, final double par3, final double par5)
    {
        IInventory iinventory = null;
        final int i = MathHelper.floor_double(par1);
        final int j = MathHelper.floor_double(par3);
        final int k = MathHelper.floor_double(par5);
        final TileEntity tileentity = par0World.getBlockTileEntity(i, j, k);

        if (tileentity != null && tileentity instanceof IInventory)
        {
            iinventory = (IInventory)tileentity;

            if (iinventory instanceof TileEntityChest)
            {
                final int l = par0World.getBlockId(i, j, k);
                final Block block = Block.blocksList[l];

                if (block instanceof BlockChest)
                {
                    iinventory = ((BlockChest)block).getInventory(par0World, i, j, k);
                }
            }
        }

        if (iinventory == null)
        {
            final List list = par0World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB(par1, par3, par5, par1 + 1.0D, par3 + 1.0D, par5 + 1.0D), IEntitySelector.selectInventories);

            if (list != null && !list.isEmpty())
            {
                iinventory = (IInventory)list.get(par0World.rand.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private static boolean areItemStacksEqualItem(final ItemStack par0ItemStack, final ItemStack par1ItemStack)
    {
        return par0ItemStack.itemID != par1ItemStack.itemID ? false : (par0ItemStack.getItemDamage() != par1ItemStack.getItemDamage() ? false : (par0ItemStack.stackSize > par0ItemStack.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(par0ItemStack, par1ItemStack)));
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos()
    {
        return (double)this.xCoord;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos()
    {
        return (double)this.yCoord;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos()
    {
        return (double)this.zCoord;
    }

    public void setTransferCooldown(final int par1)
    {
        this.transferCooldown = par1;
    }

    public boolean isCoolingDown()
    {
        return this.transferCooldown > 0;
    }
}
