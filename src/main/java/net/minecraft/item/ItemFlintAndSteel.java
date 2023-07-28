package net.minecraft.item;

// CraftBukkit start

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
// CraftBukkit end

public class ItemFlintAndSteel extends Item
{
    public ItemFlintAndSteel(final int par1)
    {
        super(par1);
        this.maxStackSize = 1;
        this.setMaxDamage(64);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, final int par7, final float par8, final float par9, final float par10)
    {
        int par51 = par5;
        int par61 = par6;
        int par41 = par4;
        final int clickedX = par41;  // CraftBukkit
        final int clickedY = par51;
        final int clickedZ = par61;

        if (par7 == 0)
        {
            --par51;
        }

        if (par7 == 1)
        {
            ++par51;
        }

        if (par7 == 2)
        {
            --par61;
        }

        if (par7 == 3)
        {
            ++par61;
        }

        if (par7 == 4)
        {
            --par41;
        }

        if (par7 == 5)
        {
            ++par41;
        }

        if (!par2EntityPlayer.canPlayerEdit(par41, par51, par61, par7, par1ItemStack))
        {
            return false;
        }
        else
        {
            if (par3World.isAirBlock(par41, par51, par61))
            {
                // CraftBukkit start - Store the clicked block
                if (CraftEventFactory.callBlockIgniteEvent(par3World, par41, par51, par61, org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, par2EntityPlayer).isCancelled())
                {
                    par1ItemStack.damageItem(1, par2EntityPlayer);
                    return false;
                }

                final CraftBlockState blockState = CraftBlockState.getBlockState(par3World, par41, par51, par61);
                // CraftBukkit end
                par3World.playSoundEffect((double) par41 + 0.5D, (double) par51 + 0.5D, (double) par61 + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                par3World.setBlock(par41, par51, par61, Block.fire.blockID);
                // CraftBukkit start
                final org.bukkit.event.block.BlockPlaceEvent placeEvent = CraftEventFactory.callBlockPlaceEvent(par3World, par2EntityPlayer, blockState, clickedX, clickedY, clickedZ);

                if (placeEvent.isCancelled() || !placeEvent.canBuild())
                {
                    placeEvent.getBlockPlaced().setTypeIdAndData(0, (byte) 0, false);
                    return false;
                }

                // CraftBukkit end
            }

            par1ItemStack.damageItem(1, par2EntityPlayer);
            return true;
        }
    }
}
