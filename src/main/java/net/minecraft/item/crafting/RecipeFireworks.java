package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;

public class RecipeFireworks extends ShapelessRecipes implements IRecipe   // CraftBukkit - added extends
{
    private ItemStack field_92102_a;

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeFireworks()
    {
        super(new ItemStack(Item.firework, 0, 0), Collections.singletonList(new ItemStack(Item.gunpowder, 0, 5)));
    }
    // CraftBukkit end

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(final InventoryCrafting par1InventoryCrafting, final World par2World)
    {
        this.field_92102_a = null;
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;

        for (int k1 = 0; k1 < par1InventoryCrafting.getSizeInventory(); ++k1)
        {
            final ItemStack itemstack = par1InventoryCrafting.getStackInSlot(k1);

            if (itemstack != null)
            {
                if (itemstack.itemID == Item.gunpowder.itemID)
                {
                    ++j;
                }
                else if (itemstack.itemID == Item.fireworkCharge.itemID)
                {
                    ++l;
                }
                else if (itemstack.itemID == Item.dyePowder.itemID)
                {
                    ++k;
                }
                else if (itemstack.itemID == Item.paper.itemID)
                {
                    ++i;
                }
                else if (itemstack.itemID == Item.glowstone.itemID)
                {
                    ++i1;
                }
                else if (itemstack.itemID == Item.diamond.itemID)
                {
                    ++i1;
                }
                else if (itemstack.itemID == Item.fireballCharge.itemID)
                {
                    ++j1;
                }
                else if (itemstack.itemID == Item.feather.itemID)
                {
                    ++j1;
                }
                else if (itemstack.itemID == Item.goldNugget.itemID)
                {
                    ++j1;
                }
                else
                {
                    if (itemstack.itemID != Item.skull.itemID)
                    {
                        return false;
                    }

                    ++j1;
                }
            }
        }

        i1 += k + j1;

        if (j <= 3 && i <= 1)
        {
            final NBTTagCompound nbttagcompound;
            final NBTTagCompound nbttagcompound1;

            if (j >= 1 && i == 1 && i1 == 0)
            {
                this.field_92102_a = new ItemStack(Item.firework);

                nbttagcompound = new NBTTagCompound();
                if (l > 0)
                {
                    nbttagcompound1 = new NBTTagCompound("Fireworks");
                    final NBTTagList nbttaglist = new NBTTagList("Explosions");

                    for (int l1 = 0; l1 < par1InventoryCrafting.getSizeInventory(); ++l1)
                    {
                        final ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(l1);

                        if (itemstack1 != null && itemstack1.itemID == Item.fireworkCharge.itemID && itemstack1.hasTagCompound() && itemstack1.getTagCompound().hasKey("Explosion"))
                        {
                            nbttaglist.appendTag(itemstack1.getTagCompound().getCompoundTag("Explosion"));
                        }
                    }

                    nbttagcompound1.setTag("Explosions", nbttaglist);
                    nbttagcompound1.setByte("Flight", (byte)j);
                    nbttagcompound.setTag("Fireworks", nbttagcompound1);
                }

                this.field_92102_a.setTagCompound(nbttagcompound);
                return true;
            }
            else if (j == 1 && i == 0 && l == 0 && k > 0 && j1 <= 1)
            {
                this.field_92102_a = new ItemStack(Item.fireworkCharge);
                nbttagcompound = new NBTTagCompound();
                nbttagcompound1 = new NBTTagCompound("Explosion");
                byte b0 = 0;
                final ArrayList arraylist = new ArrayList();

                for (int i2 = 0; i2 < par1InventoryCrafting.getSizeInventory(); ++i2)
                {
                    final ItemStack itemstack2 = par1InventoryCrafting.getStackInSlot(i2);

                    if (itemstack2 != null)
                    {
                        if (itemstack2.itemID == Item.dyePowder.itemID)
                        {
                            arraylist.add(Integer.valueOf(ItemDye.dyeColors[itemstack2.getItemDamage()]));
                        }
                        else if (itemstack2.itemID == Item.glowstone.itemID)
                        {
                            nbttagcompound1.setBoolean("Flicker", true);
                        }
                        else if (itemstack2.itemID == Item.diamond.itemID)
                        {
                            nbttagcompound1.setBoolean("Trail", true);
                        }
                        else if (itemstack2.itemID == Item.fireballCharge.itemID)
                        {
                            b0 = 1;
                        }
                        else if (itemstack2.itemID == Item.feather.itemID)
                        {
                            b0 = 4;
                        }
                        else if (itemstack2.itemID == Item.goldNugget.itemID)
                        {
                            b0 = 2;
                        }
                        else if (itemstack2.itemID == Item.skull.itemID)
                        {
                            b0 = 3;
                        }
                    }
                }

                final int[] aint = new int[arraylist.size()];

                for (int j2 = 0; j2 < aint.length; ++j2)
                {
                    aint[j2] = ((Integer)arraylist.get(j2)).intValue();
                }

                nbttagcompound1.setIntArray("Colors", aint);
                nbttagcompound1.setByte("Type", b0);
                nbttagcompound.setTag("Explosion", nbttagcompound1);
                this.field_92102_a.setTagCompound(nbttagcompound);
                return true;
            }
            else if (j == 0 && i == 0 && l == 1 && k > 0 && k == i1)
            {
                final ArrayList arraylist1 = new ArrayList();

                for (int k2 = 0; k2 < par1InventoryCrafting.getSizeInventory(); ++k2)
                {
                    final ItemStack itemstack3 = par1InventoryCrafting.getStackInSlot(k2);

                    if (itemstack3 != null)
                    {
                        if (itemstack3.itemID == Item.dyePowder.itemID)
                        {
                            arraylist1.add(Integer.valueOf(ItemDye.dyeColors[itemstack3.getItemDamage()]));
                        }
                        else if (itemstack3.itemID == Item.fireworkCharge.itemID)
                        {
                            this.field_92102_a = itemstack3.copy();
                            this.field_92102_a.stackSize = 1;
                        }
                    }
                }

                final int[] aint1 = new int[arraylist1.size()];

                for (int l2 = 0; l2 < aint1.length; ++l2)
                {
                    aint1[l2] = ((Integer)arraylist1.get(l2)).intValue();
                }

                if (this.field_92102_a != null && this.field_92102_a.hasTagCompound())
                {
                    final NBTTagCompound nbttagcompound2 = this.field_92102_a.getTagCompound().getCompoundTag("Explosion");

                    if (nbttagcompound2 == null)
                    {
                        return false;
                    }
                    else
                    {
                        nbttagcompound2.setIntArray("FadeColors", aint1);
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(final InventoryCrafting par1InventoryCrafting)
    {
        return this.field_92102_a.copy();
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
        return this.field_92102_a;
    }
}
