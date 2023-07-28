package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

@SideOnly(Side.CLIENT)
public class NpcMerchant implements IMerchant
{
    /** Instance of Merchants Inventory. */
    private InventoryMerchant theMerchantInventory;

    /** This merchant's current player customer. */
    private EntityPlayer customer;

    /** The MerchantRecipeList instance. */
    private MerchantRecipeList recipeList;

    public NpcMerchant(final EntityPlayer par1EntityPlayer)
    {
        this.customer = par1EntityPlayer;
        this.theMerchantInventory = new InventoryMerchant(par1EntityPlayer, this);
    }

    public EntityPlayer getCustomer()
    {
        return this.customer;
    }

    public void setCustomer(final EntityPlayer par1EntityPlayer) {}

    public MerchantRecipeList getRecipes(final EntityPlayer par1EntityPlayer)
    {
        return this.recipeList;
    }

    public void setRecipes(final MerchantRecipeList par1MerchantRecipeList)
    {
        this.recipeList = par1MerchantRecipeList;
    }

    public void useRecipe(final MerchantRecipe par1MerchantRecipe) {}

    public void func_110297_a_(final ItemStack par1ItemStack) {}
}
