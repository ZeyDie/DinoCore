package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase
{
    protected ItemEmptyMap(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer)
    {
        final ItemStack itemstack1 = new ItemStack(Item.map, 1, par2World.getUniqueDataId("map"));
        final String s = "map_" + itemstack1.getItemDamage();
        final MapData mapdata = new MapData(s);
        par2World.setItemData(s, mapdata);
        mapdata.scale = 0;
        final int i = 128 * (1 << mapdata.scale);
        mapdata.xCenter = (int)(Math.round(par3EntityPlayer.posX / (double)i) * (long)i);
        mapdata.zCenter = (int)(Math.round(par3EntityPlayer.posZ / (double)i) * (long)i);
        mapdata.dimension = (byte)par2World.provider.dimensionId;
        mapdata.markDirty();
        org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callEvent(new org.bukkit.event.server.MapInitializeEvent(mapdata.mapView)); // CraftBukkit
        --par1ItemStack.stackSize;

        if (par1ItemStack.stackSize <= 0)
        {
            return itemstack1;
        }
        else
        {
            if (!par3EntityPlayer.inventory.addItemStackToInventory(itemstack1.copy()))
            {
                par3EntityPlayer.dropPlayerItem(itemstack1);
            }

            return par1ItemStack;
        }
    }
}
