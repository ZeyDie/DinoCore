package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemEditableBook extends Item
{
    public ItemEditableBook(final int par1)
    {
        super(par1);
        this.setMaxStackSize(1);
    }

    public static boolean validBookTagContents(final NBTTagCompound par0NBTTagCompound)
    {
        if (!ItemWritableBook.validBookTagPages(par0NBTTagCompound))
        {
            return false;
        }
        else if (!par0NBTTagCompound.hasKey("title"))
        {
            return false;
        }
        else
        {
            final String s = par0NBTTagCompound.getString("title");
            return s != null && s.length() <= 16 ? par0NBTTagCompound.hasKey("author") : false;
        }
    }

    public String getItemDisplayName(final ItemStack par1ItemStack)
    {
        if (par1ItemStack.hasTagCompound())
        {
            final NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
            final NBTTagString nbttagstring = (NBTTagString)nbttagcompound.getTag("title");

            if (nbttagstring != null)
            {
                return nbttagstring.toString();
            }
        }

        return super.getItemDisplayName(par1ItemStack);
    }

    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4)
    {
        if (par1ItemStack.hasTagCompound())
        {
            final NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
            final NBTTagString nbttagstring = (NBTTagString)nbttagcompound.getTag("author");

            if (nbttagstring != null)
            {
                par3List.add(EnumChatFormatting.GRAY + String.format(StatCollector.translateToLocalFormatted("book.byAuthor", new Object[] {nbttagstring.data}), new Object[0]));
            }
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer)
    {
        par3EntityPlayer.displayGUIBook(par1ItemStack);
        return par1ItemStack;
    }

    /**
     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
     */
    public boolean getShareTag()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(final ItemStack par1ItemStack)
    {
        return true;
    }
}
