package org.bukkit.craftbukkit.v1_6_R3.inventory;

import net.minecraftforge.cauldron.CauldronUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;


public class CraftInventory implements Inventory {
    protected final net.minecraft.inventory.IInventory inventory;

    public CraftInventory(final net.minecraft.inventory.IInventory inventory) {
        this.inventory = inventory;
    }

    public net.minecraft.inventory.IInventory getInventory() {
        return inventory;
    }

    public int getSize() {
        return getInventory().getSizeInventory();
    }

    public String getName() {
        return getInventory().getInvName();
    }

    public ItemStack getItem(final int index) {
        final net.minecraft.item.ItemStack item = getInventory().getStackInSlot(index);
        return item == null ? null : CraftItemStack.asCraftMirror(item);
    }

    public ItemStack[] getContents() {
        final ItemStack[] items = new ItemStack[getSize()];
        // Cauldron start - fixes appeng TileDrive AbstractMethodError
        net.minecraft.item.ItemStack[] mcItems = null;
        try {
            mcItems = getInventory().getContents();
        } catch (final AbstractMethodError e) {
            return new ItemStack[0]; // return empty list
        }
        // Cauldron end

        final int size = Math.min(items.length, mcItems.length);
        for (int i = 0; i < size; i++) {
            items[i] = mcItems[i] == null ? null : CraftItemStack.asCraftMirror(mcItems[i]);
        }

        return items;
    }

    public void setContents(final ItemStack[] items) {
        if (getInventory().getContents().length < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getInventory().getContents().length + " or less");
        }

        final net.minecraft.item.ItemStack[] mcItems = getInventory().getContents();

        for (int i = 0; i < mcItems.length; i++) {
            if (i >= items.length) {
                mcItems[i] = null;
            } else {
                mcItems[i] = CraftItemStack.asNMSCopy(items[i]);
            }
        }
    }

    public void setItem(final int index, final ItemStack item) {
        getInventory().setInventorySlotContents(index, ((item == null || item.getTypeId() == 0) ? null : CraftItemStack.asNMSCopy(item)));
    }

    public boolean contains(final int materialId) {
        for (final ItemStack item : getContents()) {
            if (item != null && item.getTypeId() == materialId) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(final Material material) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId());
    }

    public boolean contains(final ItemStack item) {
        if (item == null) {
            return false;
        }
        for (final ItemStack i : getContents()) {
            if (item.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(final int materialId, int amount) {
        int amount1 = amount;
        if (amount1 <= 0) {
            return true;
        }
        for (final ItemStack item : getContents()) {
            if (item != null && item.getTypeId() == materialId) {
                if ((amount1 -= item.getAmount()) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(final Material material, final int amount) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId(), amount);
    }

    public boolean contains(final ItemStack item, int amount) {
        int amount1 = amount;
        if (item == null) {
            return false;
        }
        if (amount1 <= 0) {
            return true;
        }
        for (final ItemStack i : getContents()) {
            if (item.equals(i) && --amount1 <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAtLeast(final ItemStack item, int amount) {
        int amount1 = amount;
        if (item == null) {
            return false;
        }
        if (amount1 <= 0) {
            return true;
        }
        for (final ItemStack i : getContents()) {
            if (item.isSimilar(i) && (amount1 -= i.getAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    public HashMap<Integer, ItemStack> all(final int materialId) {
        final HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();

        final ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            final ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId) {
                slots.put(i, item);
            }
        }
        return slots;
    }

    public HashMap<Integer, ItemStack> all(final Material material) {
        Validate.notNull(material, "Material cannot be null");
        return all(material.getId());
    }

    public HashMap<Integer, ItemStack> all(final ItemStack item) {
        final HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();
        if (item != null) {
            final ItemStack[] inventory = getContents();
            for (int i = 0; i < inventory.length; i++) {
                if (item.equals(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }
        return slots;
    }

    public int first(final int materialId) {
        final ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            final ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId) {
                return i;
            }
        }
        return -1;
    }

    public int first(final Material material) {
        Validate.notNull(material, "Material cannot be null");
        return first(material.getId());
    }

    public int first(final ItemStack item) {
        return first(item, true);
    }

    private int first(final ItemStack item, final boolean withAmount) {
        if (item == null) {
            return -1;
        }
        final ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) {
                return i;
            }
        }
        return -1;
    }

    public int firstEmpty() {
        final ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(final int materialId) {
        final ItemStack[] inventory = getContents();
        for (int i = 0; i < inventory.length; i++) {
            final ItemStack item = inventory[i];
            if (item != null && item.getTypeId() == materialId && item.getAmount() < item.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(final Material material) {
        Validate.notNull(material, "Material cannot be null");
        return firstPartial(material.getId());
    }

    private int firstPartial(final ItemStack item) {
        final ItemStack[] inventory = getContents();
        final ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            final ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(filteredItem)) {
                return i;
            }
        }
        return -1;
    }

    public HashMap<Integer, ItemStack> addItem(final ItemStack... items) {
        Validate.noNullElements(items, "Item cannot be null");
        final HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */

        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                final int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    final int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > getMaxItemStack()) {
                            final CraftItemStack stack = CraftItemStack.asCraftCopy(item);
                            stack.setAmount(getMaxItemStack());
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - getMaxItemStack());
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    final ItemStack partialItem = getItem(firstPartial);

                    final int amount = item.getAmount();
                    final int partialAmount = partialItem.getAmount();
                    final int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    public HashMap<Integer, ItemStack> removeItem(final ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        final HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        // TODO: optimization

        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                final int first = first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    final ItemStack itemStack = getItem(first);
                    final int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        clear(first);
                    } else {
                        // split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    private int getMaxItemStack() {
        return getInventory().getInventoryStackLimit();
    }

    public void remove(final int materialId) {
        final ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getTypeId() == materialId) {
                clear(i);
            }
        }
    }

    public void remove(final Material material) {
        Validate.notNull(material, "Material cannot be null");
        remove(material.getId());
    }

    public void remove(final ItemStack item) {
        final ItemStack[] items = getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].equals(item)) {
                clear(i);
            }
        }
    }

    public void clear(final int index) {
        setItem(index, null);
    }

    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            clear(i);
        }
    }

    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    public ListIterator<ItemStack> iterator(int index) {
        int index1 = index;
        if (index1 < 0) {
            index1 += getSize() + 1; // ie, with -1, previous() will return the last element
        }
        return new InventoryIterator(this, index1);
    }

    public List<HumanEntity> getViewers() {
        // Cauldron start
        try {
            return this.inventory.getViewers();
        } catch (final AbstractMethodError e) {
            return new java.util.ArrayList<HumanEntity>();
        }
        // Cauldron end
    }

    public String getTitle() {
        return inventory.getInvName();
    }

    public InventoryType getType() {
        // Thanks to Droppers extending Dispensers, order is important.
        if (inventory instanceof net.minecraft.inventory.InventoryCrafting) {
            return inventory.getSizeInventory() >= 9 ? InventoryType.WORKBENCH : InventoryType.CRAFTING;
        } else if (inventory instanceof net.minecraft.entity.player.InventoryPlayer) {
            return InventoryType.PLAYER;
        } else if (inventory instanceof net.minecraft.tileentity.TileEntityDropper) {
            return InventoryType.DROPPER;
        } else if (inventory instanceof net.minecraft.tileentity.TileEntityDispenser) {
            return InventoryType.DISPENSER;
        } else if (inventory instanceof net.minecraft.tileentity.TileEntityFurnace) {
            return InventoryType.FURNACE;
        } else if (inventory instanceof net.minecraft.inventory.SlotEnchantmentTable) {
            return InventoryType.ENCHANTING;
        } else if (inventory instanceof net.minecraft.tileentity.TileEntityBrewingStand) {
            return InventoryType.BREWING;
        } else if (inventory instanceof CraftInventoryCustom.MinecraftInventory) {
            return ((CraftInventoryCustom.MinecraftInventory) inventory).getType();
        } else if (inventory instanceof net.minecraft.inventory.InventoryEnderChest) {
            return InventoryType.ENDER_CHEST;
        } else if (inventory instanceof net.minecraft.inventory.InventoryMerchant) {
            return InventoryType.MERCHANT;
        } else if (inventory instanceof net.minecraft.tileentity.TileEntityBeacon) {
            return InventoryType.BEACON;
        } else if (inventory instanceof net.minecraft.inventory.ContainerRepairINNER1) {
            return InventoryType.ANVIL;
        } else if (inventory instanceof net.minecraft.tileentity.Hopper) {
            return InventoryType.HOPPER;
        } else {
            return InventoryType.CHEST;
        }
    }

    public InventoryHolder getHolder() {
        // Cauldron start - fixes openblocks AbstractMethodError
        try {
            return inventory.getOwner();
        } catch (final AbstractMethodError e) {
            if (inventory instanceof net.minecraft.tileentity.TileEntity) {
                return CauldronUtils.getOwner((net.minecraft.tileentity.TileEntity)inventory);
            } else {
                return null;                
            }
        }
        // Cauldron end
    }

    public int getMaxStackSize() {
        return inventory.getInventoryStackLimit();
    }

    public void setMaxStackSize(final int size) {
        inventory.setMaxStackSize(size);
    }
}
