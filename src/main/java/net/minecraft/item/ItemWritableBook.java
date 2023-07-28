package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

public class ItemWritableBook extends Item
{
    public ItemWritableBook(final int par1)
    {
        super(par1);
        this.setMaxStackSize(1);
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

    public static boolean validBookTagPages(final NBTTagCompound par0NBTTagCompound)
    {
        if (par0NBTTagCompound == null)
        {
            return false;
        }
        else if (!par0NBTTagCompound.hasKey("pages"))
        {
            return false;
        }
        else
        {
            final NBTTagList nbttaglist = (NBTTagList)par0NBTTagCompound.getTag("pages");

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                final NBTTagString nbttagstring = (NBTTagString)nbttaglist.tagAt(i);

                if (nbttagstring.data == null)
                {
                    return false;
                }

                if (nbttagstring.data.length() > 256)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
