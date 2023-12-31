package net.minecraft.item.crafting;

import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;

public class RecipesArmorDyes extends ShapelessRecipes implements IRecipe   // CraftBukkit - added extends
{
    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipesArmorDyes()
    {
        super(new ItemStack(Item.helmetLeather, 0, 0), Collections.singletonList(new ItemStack(Item.dyePowder, 0, 5)));
    }
    // CraftBukkit end

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(final InventoryCrafting par1InventoryCrafting, final World par2World)
    {
        ItemStack itemstack = null;
        final ArrayList arraylist = new ArrayList();

        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i)
        {
            final ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(i);

            if (itemstack1 != null)
            {
                if (itemstack1.getItem() instanceof ItemArmor)
                {
                    final ItemArmor itemarmor = (ItemArmor)itemstack1.getItem();

                    if (itemarmor.getArmorMaterial() != EnumArmorMaterial.CLOTH || itemstack != null)
                    {
                        return false;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.itemID != Item.dyePowder.itemID)
                    {
                        return false;
                    }

                    arraylist.add(itemstack1);
                }
            }
        }

        return itemstack != null && !arraylist.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(final InventoryCrafting par1InventoryCrafting)
    {
        ItemStack itemstack = null;
        final int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmor itemarmor = null;
        int k;
        int l;
        float f;
        float f1;
        int i1;

        for (k = 0; k < par1InventoryCrafting.getSizeInventory(); ++k)
        {
            final ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(k);

            if (itemstack1 != null)
            {
                if (itemstack1.getItem() instanceof ItemArmor)
                {
                    itemarmor = (ItemArmor)itemstack1.getItem();

                    if (itemarmor.getArmorMaterial() != EnumArmorMaterial.CLOTH || itemstack != null)
                    {
                        return null;
                    }

                    itemstack = itemstack1.copy();
                    itemstack.stackSize = 1;

                    if (itemarmor.hasColor(itemstack1))
                    {
                        l = itemarmor.getColor(itemstack);
                        f = (float)(l >> 16 & 255) / 255.0F;
                        f1 = (float)(l >> 8 & 255) / 255.0F;
                        final float f2 = (float)(l & 255) / 255.0F;
                        i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int)((float)aint[0] + f * 255.0F);
                        aint[1] = (int)((float)aint[1] + f1 * 255.0F);
                        aint[2] = (int)((float)aint[2] + f2 * 255.0F);
                        ++j;
                    }
                }
                else
                {
                    if (itemstack1.itemID != Item.dyePowder.itemID)
                    {
                        return null;
                    }

                    final float[] afloat = EntitySheep.fleeceColorTable[BlockColored.getBlockFromDye(itemstack1.getItemDamage())];
                    final int j1 = (int)(afloat[0] * 255.0F);
                    final int k1 = (int)(afloat[1] * 255.0F);
                    i1 = (int)(afloat[2] * 255.0F);
                    i += Math.max(j1, Math.max(k1, i1));
                    aint[0] += j1;
                    aint[1] += k1;
                    aint[2] += i1;
                    ++j;
                }
            }
        }

        if (itemarmor == null)
        {
            return null;
        }
        else
        {
            k = aint[0] / j;
            int l1 = aint[1] / j;
            l = aint[2] / j;
            f = (float)i / (float)j;
            f1 = (float)Math.max(k, Math.max(l1, l));
            k = (int)((float)k * f / f1);
            l1 = (int)((float)l1 * f / f1);
            l = (int)((float)l * f / f1);
            i1 = (k << 8) + l1;
            i1 = (i1 << 8) + l;
            itemarmor.func_82813_b(itemstack, i1);
            return itemstack;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 10;
    }

    public ItemStack getRecipeOutput()
    {
        return null;
    }
}
