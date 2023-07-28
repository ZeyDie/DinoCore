package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

public class CraftShapelessRecipe extends ShapelessRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    private net.minecraft.item.crafting.ShapelessRecipes recipe;

    public CraftShapelessRecipe(final ItemStack result) {
        super(result);
    }

    public CraftShapelessRecipe(final ItemStack result, final net.minecraft.item.crafting.ShapelessRecipes recipe) {
        this(result);
        this.recipe = recipe;
    }

    public static CraftShapelessRecipe fromBukkitRecipe(final ShapelessRecipe recipe) {
        if (recipe instanceof CraftShapelessRecipe) {
            return (CraftShapelessRecipe) recipe;
        }
        final CraftShapelessRecipe ret = new CraftShapelessRecipe(recipe.getResult());
        for (final ItemStack ingred : recipe.getIngredientList()) {
            ret.addIngredient(ingred.getType(), ingred.getDurability());
        }
        return ret;
    }

    public void addToCraftingManager() {
        final List<ItemStack> ingred = this.getIngredientList();
        final Object[] data = new Object[ingred.size()];
        int i = 0;
        for (final ItemStack mdata : ingred) {
            final int id = mdata.getTypeId();
            final short dmg = mdata.getDurability();
            data[i] = new net.minecraft.item.ItemStack(id, 1, dmg);
            i++;
        }
        net.minecraft.item.crafting.CraftingManager.getInstance().addShapelessRecipe(CraftItemStack.asNMSCopy(this.getResult()), data);
    }
}
