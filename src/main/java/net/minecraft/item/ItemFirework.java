package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemFirework extends Item
{
    public ItemFirework(final int par1)
    {
        super(par1);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        if (!par3World.isRemote)
        {
            final EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(par3World, (double)((float)par4 + par8), (double)((float)par5 + par9), (double)((float)par6 + par10), par1ItemStack);
            par3World.spawnEntityInWorld(entityfireworkrocket);

            if (!par2EntityPlayer.capabilities.isCreativeMode)
            {
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4)
    {
        if (par1ItemStack.hasTagCompound())
        {
            final NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound().getCompoundTag("Fireworks");

            if (nbttagcompound != null)
            {
                if (nbttagcompound.hasKey("Flight"))
                {
                    par3List.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + nbttagcompound.getByte("Flight"));
                }

                final NBTTagList nbttaglist = nbttagcompound.getTagList("Explosions");

                if (nbttaglist != null && nbttaglist.tagCount() > 0)
                {
                    for (int i = 0; i < nbttaglist.tagCount(); ++i)
                    {
                        final NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
                        final ArrayList arraylist = new ArrayList();
                        ItemFireworkCharge.func_92107_a(nbttagcompound1, arraylist);

                        if (!arraylist.isEmpty())
                        {
                            for (int j = 1; j < arraylist.size(); ++j)
                            {
                                arraylist.set(j, "  " + (String)arraylist.get(j));
                            }

                            par3List.addAll(arraylist);
                        }
                    }
                }
            }
        }
    }
}
