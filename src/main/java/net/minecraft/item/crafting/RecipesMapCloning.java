package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Collections;

public class RecipesMapCloning extends ShapelessRecipes implements IRecipe   // CraftBukkit - added extends
{
    // CraftBukkit start - Delegate to new parent class
    public RecipesMapCloning()
    {
        super(new ItemStack(Item.map, 0, -1), Collections.singletonList(new ItemStack(Item.emptyMap, 0, 0)));
    }
    // CraftBukkit end

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(final InventoryCrafting par1InventoryCrafting, final World par2World)
    {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < par1InventoryCrafting.getSizeInventory(); ++j)
        {
            final ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(j);

            if (itemstack1 != null)
            {
                if (itemstack1.itemID == Item.map.itemID)
                {
                    if (itemstack != null)
                    {
                        return false;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.itemID != Item.emptyMap.itemID)
                    {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(final InventoryCrafting par1InventoryCrafting)
    {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < par1InventoryCrafting.getSizeInventory(); ++j)
        {
            final ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(j);

            if (itemstack1 != null)
            {
                if (itemstack1.itemID == Item.map.itemID)
                {
                    if (itemstack != null)
                    {
                        return null;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.itemID != Item.emptyMap.itemID)
                    {
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1)
        {
            final ItemStack itemstack2 = new ItemStack(Item.map, i + 1, itemstack.getItemDamage());

            if (itemstack.hasDisplayName())
            {
                itemstack2.setItemName(itemstack.getDisplayName());
            }

            return itemstack2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 9;
    }

    public ItemStack getRecipeOutput()
    {
        return null;
    }
}
