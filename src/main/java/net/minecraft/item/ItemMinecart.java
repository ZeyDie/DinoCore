package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemMinecart extends Item
{
    private static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDispenseMinecart();
    public int minecartType;

    public ItemMinecart(final int par1, final int par2)
    {
        super(par1);
        this.maxStackSize = 1;
        this.minecartType = par2;
        this.setCreativeTab(CreativeTabs.tabTransport);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        final int i1 = par3World.getBlockId(par4, par5, par6);

        if (BlockRailBase.isRailBlock(i1))
        {
            if (!par3World.isRemote)
            {
                // CraftBukkit start - Minecarts
                final org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callPlayerInteractEvent(par2EntityPlayer, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, par4, par5, par6, par7, par1ItemStack);

                if (event.isCancelled())
                {
                    return false;
                }

                // CraftBukkit end
                final EntityMinecart entityminecart = EntityMinecart.createMinecart(par3World, (double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.minecartType);

                if (par1ItemStack.hasDisplayName())
                {
                    entityminecart.setMinecartName(par1ItemStack.getDisplayName());
                }

                par3World.spawnEntityInWorld(entityminecart);
            }

            --par1ItemStack.stackSize;
            return true;
        }
        else
        {
            return false;
        }
    }
}
