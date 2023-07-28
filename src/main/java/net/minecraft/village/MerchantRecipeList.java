package net.minecraft.village;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MerchantRecipeList extends ArrayList
{
    public MerchantRecipeList() {}

    public MerchantRecipeList(final NBTTagCompound par1NBTTagCompound)
    {
        this.readRecipiesFromTags(par1NBTTagCompound);
    }

    /**
     * can par1,par2 be used to in crafting recipe par3
     */
    public MerchantRecipe canRecipeBeUsed(final ItemStack par1ItemStack, final ItemStack par2ItemStack, final int par3)
    {
        if (par3 > 0 && par3 < this.size())
        {
            final MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(par3);
            return par1ItemStack.itemID == merchantrecipe.getItemToBuy().itemID && (par2ItemStack == null && !merchantrecipe.hasSecondItemToBuy() || merchantrecipe.hasSecondItemToBuy() && par2ItemStack != null && merchantrecipe.getSecondItemToBuy().itemID == par2ItemStack.itemID) && par1ItemStack.stackSize >= merchantrecipe.getItemToBuy().stackSize && (!merchantrecipe.hasSecondItemToBuy() || par2ItemStack.stackSize >= merchantrecipe.getSecondItemToBuy().stackSize) ? merchantrecipe : null;
        }
        else
        {
            for (int j = 0; j < this.size(); ++j)
            {
                final MerchantRecipe merchantrecipe1 = (MerchantRecipe)this.get(j);

                if (par1ItemStack.itemID == merchantrecipe1.getItemToBuy().itemID && par1ItemStack.stackSize >= merchantrecipe1.getItemToBuy().stackSize && (!merchantrecipe1.hasSecondItemToBuy() && par2ItemStack == null || merchantrecipe1.hasSecondItemToBuy() && par2ItemStack != null && merchantrecipe1.getSecondItemToBuy().itemID == par2ItemStack.itemID && par2ItemStack.stackSize >= merchantrecipe1.getSecondItemToBuy().stackSize))
                {
                    return merchantrecipe1;
                }
            }

            return null;
        }
    }

    /**
     * checks if there is a recipie for the same ingredients already on the list, and replaces it. otherwise, adds it
     */
    public void addToListWithCheck(final MerchantRecipe par1MerchantRecipe)
    {
        for (int i = 0; i < this.size(); ++i)
        {
            final MerchantRecipe merchantrecipe1 = (MerchantRecipe)this.get(i);

            if (par1MerchantRecipe.hasSameIDsAs(merchantrecipe1))
            {
                if (par1MerchantRecipe.hasSameItemsAs(merchantrecipe1))
                {
                    this.set(i, par1MerchantRecipe);
                }

                return;
            }
        }

        this.add(par1MerchantRecipe);
    }

    public void writeRecipiesToStream(final DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte((byte)(this.size() & 255));

        for (int i = 0; i < this.size(); ++i)
        {
            final MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            Packet.writeItemStack(merchantrecipe.getItemToBuy(), par1DataOutputStream);
            Packet.writeItemStack(merchantrecipe.getItemToSell(), par1DataOutputStream);
            final ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
            par1DataOutputStream.writeBoolean(itemstack != null);

            if (itemstack != null)
            {
                Packet.writeItemStack(itemstack, par1DataOutputStream);
            }

            par1DataOutputStream.writeBoolean(merchantrecipe.func_82784_g());
        }
    }

    @SideOnly(Side.CLIENT)
    public static MerchantRecipeList readRecipiesFromStream(final DataInputStream par0DataInputStream) throws IOException
    {
        final MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        final int i = par0DataInputStream.readByte() & 255;

        for (int j = 0; j < i; ++j)
        {
            final ItemStack itemstack = Packet.readItemStack(par0DataInputStream);
            final ItemStack itemstack1 = Packet.readItemStack(par0DataInputStream);
            ItemStack itemstack2 = null;

            if (par0DataInputStream.readBoolean())
            {
                itemstack2 = Packet.readItemStack(par0DataInputStream);
            }

            final boolean flag = par0DataInputStream.readBoolean();
            final MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1);

            if (flag)
            {
                merchantrecipe.func_82785_h();
            }

            merchantrecipelist.add(merchantrecipe);
        }

        return merchantrecipelist;
    }

    public void readRecipiesFromTags(final NBTTagCompound par1NBTTagCompound)
    {
        final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Recipes");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            this.add(new MerchantRecipe(nbttagcompound1));
        }
    }

    public NBTTagCompound getRecipiesAsTags()
    {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        final NBTTagList nbttaglist = new NBTTagList("Recipes");

        for (int i = 0; i < this.size(); ++i)
        {
            final MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i);
            nbttaglist.appendTag(merchantrecipe.writeToTags());
        }

        nbttagcompound.setTag("Recipes", nbttaglist);
        return nbttagcompound;
    }
}
