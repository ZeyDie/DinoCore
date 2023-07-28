package org.bukkit.craftbukkit.v1_6_R3.inventory;


import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class CraftFurnaceRecipe extends FurnaceRecipe implements CraftRecipe {
    public CraftFurnaceRecipe(final ItemStack result, final ItemStack source) {
        super(result, source.getType(), source.getDurability());
    }

    public static CraftFurnaceRecipe fromBukkitRecipe(final FurnaceRecipe recipe) {
        if (recipe instanceof CraftFurnaceRecipe) {
            return (CraftFurnaceRecipe) recipe;
        }
        return new CraftFurnaceRecipe(recipe.getResult(), recipe.getInput());
    }

    public void addToCraftingManager() {
        final ItemStack result = this.getResult();
        final ItemStack input = this.getInput();
        net.minecraft.item.crafting.FurnaceRecipes.smelting().addSmelting(input.getTypeId(), CraftItemStack.asNMSCopy(result), 0.1f);
    }
}
