package org.bukkit.craftbukkit.v1_6_R3.inventory;


import net.minecraftforge.cauldron.potion.CustomModRecipe;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.Java15Compat;


public class CraftInventoryCrafting extends CraftInventory implements CraftingInventory {
    private final net.minecraft.inventory.IInventory resultInventory;

    public CraftInventoryCrafting(final net.minecraft.inventory.InventoryCrafting inventory, final net.minecraft.inventory.IInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }

    public net.minecraft.inventory.IInventory getResultInventory() {
        return resultInventory;
    }

    public net.minecraft.inventory.IInventory getMatrixInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return getResultInventory().getSizeInventory() + getMatrixInventory().getSizeInventory();
    }

    @Override
    public void setContents(final ItemStack[] items) {
        final int resultLen = getResultInventory().getContents().length;
        final int len = getMatrixInventory().getContents().length + resultLen;
        if (len > items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + len + " or less");
        }
        setContents(items[0], Java15Compat.Arrays_copyOfRange(items, 1, items.length));
    }

    @Override
    public ItemStack[] getContents() {
        final ItemStack[] items = new ItemStack[getSize()];
        final net.minecraft.item.ItemStack[] mcResultItems = getResultInventory().getContents();

        int i = 0;
        for (i = 0; i < mcResultItems.length; i++ ) {
            items[i] = CraftItemStack.asCraftMirror(mcResultItems[i]);
        }

        final net.minecraft.item.ItemStack[] mcItems = getMatrixInventory().getContents();

        for (int j = 0; j < mcItems.length; j++) {
            items[i + j] = CraftItemStack.asCraftMirror(mcItems[j]);
        }

        return items;
    }

    public void setContents(final ItemStack result, final ItemStack[] contents) {
        setResult(result);
        setMatrix(contents);
    }

    @Override
    public CraftItemStack getItem(final int index) {
        if (index < getResultInventory().getSizeInventory()) {
            final net.minecraft.item.ItemStack item = getResultInventory().getStackInSlot(index);
            return item == null ? null : CraftItemStack.asCraftMirror(item);
        } else {
            final net.minecraft.item.ItemStack item = getMatrixInventory().getStackInSlot(index - getResultInventory().getSizeInventory());
            return item == null ? null : CraftItemStack.asCraftMirror(item);
        }
    }

    @Override
    public void setItem(final int index, final ItemStack item) {
        if (index < getResultInventory().getSizeInventory()) {
            getResultInventory().setInventorySlotContents(index, (item == null ? null : CraftItemStack.asNMSCopy(item)));
        } else {
            getMatrixInventory().setInventorySlotContents((index - getResultInventory().getSizeInventory()), (item == null ? null : CraftItemStack.asNMSCopy(item)));
        }
    }

    public ItemStack[] getMatrix() {
        final ItemStack[] items = new ItemStack[getSize()];
        final net.minecraft.item.ItemStack[] matrix = getMatrixInventory().getContents();

        for (int i = 0; i < matrix.length; i++ ) {
            items[i] = CraftItemStack.asCraftMirror(matrix[i]);
        }

        return items;
    }

    public ItemStack getResult() {
        final net.minecraft.item.ItemStack item = getResultInventory().getStackInSlot(0);
        if(item != null) return CraftItemStack.asCraftMirror(item);
        return null;
    }

    public void setMatrix(final ItemStack[] contents) {
        if (getMatrixInventory().getContents().length > contents.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getMatrixInventory().getContents().length + " or less");
        }

        final net.minecraft.item.ItemStack[] mcItems = getMatrixInventory().getContents();

        for (int i = 0; i < mcItems.length; i++ ) {
            if (i < contents.length) {
                final ItemStack item = contents[i];
                if (item == null || item.getTypeId() <= 0) {
                    mcItems[i] = null;
                } else {
                    mcItems[i] = CraftItemStack.asNMSCopy(item);
                }
            } else {
                mcItems[i] = null;
            }
        }
    }

    public void setResult(final ItemStack item) {
        final net.minecraft.item.ItemStack[] contents = getResultInventory().getContents();
        if (item == null || item.getTypeId() <= 0) {
            contents[0] = null;
        } else {
            contents[0] = CraftItemStack.asNMSCopy(item);
        }
    }

    public Recipe getRecipe() {
        final net.minecraft.item.crafting.IRecipe recipe = ((net.minecraft.inventory.InventoryCrafting)getInventory()).currentRecipe;
        // Cauldron start - handle custom recipe classes without Bukkit API equivalents
        try {
            return recipe == null ? null : recipe.toBukkitRecipe();
        } catch (final AbstractMethodError ex) {
            // No Bukkit wrapper provided
            return new CustomModRecipe(recipe);
        }
        // Cauldron end
    }
}
