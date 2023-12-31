package net.minecraftforge.cauldron.potion;

import net.minecraft.item.crafting.IRecipe;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * Bukkit API wrapper for non-vanilla IRecipe classes
 */
public class CustomModRecipe implements Recipe {
    private IRecipe iRecipe;

    public CustomModRecipe(final IRecipe iRecipe) {
        this.iRecipe = iRecipe;
    }

    @Override
    public ItemStack getResult() {
        return CraftItemStack.asCraftMirror(iRecipe.getRecipeOutput());
    }

    public IRecipe getHandle() {
        return iRecipe;
    }
}
