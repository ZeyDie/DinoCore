package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.Recipe;

public interface CraftRecipe extends Recipe {
    void addToCraftingManager();
}
