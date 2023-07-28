package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

public class CraftShapedRecipe extends ShapedRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    private net.minecraft.item.crafting.ShapedRecipes recipe;

    public CraftShapedRecipe(final ItemStack result) {
        super(result);
    }

    public CraftShapedRecipe(final ItemStack result, final net.minecraft.item.crafting.ShapedRecipes recipe) {
        this(result);
        this.recipe = recipe;
    }

    public static CraftShapedRecipe fromBukkitRecipe(final ShapedRecipe recipe) {
        if (recipe instanceof CraftShapedRecipe) {
            return (CraftShapedRecipe) recipe;
        }
        final CraftShapedRecipe ret = new CraftShapedRecipe(recipe.getResult());
        final String[] shape = recipe.getShape();
        ret.shape(shape);
        final Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();
        for (final char c : ingredientMap.keySet()) {
            final ItemStack stack = ingredientMap.get(c);
            if (stack != null) {
                ret.setIngredient(c, stack.getType(), stack.getDurability());
            }
        }
        return ret;
    }

    public void addToCraftingManager() {
        final Object[] data;
        final String[] shape = this.getShape();
        final Map<Character, ItemStack> ingred = this.getIngredientMap();
        int datalen = shape.length;
        datalen += ingred.size() * 2;
        int i = 0;
        data = new Object[datalen];
        for (; i < shape.length; i++) {
            data[i] = shape[i];
        }
        for (final char c : ingred.keySet()) {
            final ItemStack mdata = ingred.get(c);
            if (mdata == null) continue;
            data[i] = c;
            i++;
            final int id = mdata.getTypeId();
            final short dmg = mdata.getDurability();
            data[i] = new net.minecraft.item.ItemStack(id, 1, dmg);
            i++;
        }
        net.minecraft.item.crafting.CraftingManager.getInstance().addRecipe(CraftItemStack.asNMSCopy(this.getResult()), data);
    }
}
