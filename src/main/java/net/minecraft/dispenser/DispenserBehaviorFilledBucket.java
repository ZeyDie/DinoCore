package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;

// CraftBukkit start
// CraftBukkit end

final class DispenserBehaviorFilledBucket extends BehaviorDefaultDispenseItem
{
    private final BehaviorDefaultDispenseItem defaultDispenserItemBehavior = new BehaviorDefaultDispenseItem();

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        ItemBucket itembucket = (ItemBucket)par2ItemStack.getItem();
        final int i = par1IBlockSource.getXInt();
        final int j = par1IBlockSource.getYInt();
        final int k = par1IBlockSource.getZInt();
        final EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        // CraftBukkit start
        final World world = par1IBlockSource.getWorld();
        final int x = i + enumfacing.getFrontOffsetX();
        final int y = j + enumfacing.getFrontOffsetY();
        final int z = k + enumfacing.getFrontOffsetZ();

        if (world.isAirBlock(x, y, z) || !world.getBlockMaterial(x, y, z).isSolid())
        {
            final org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
            final CraftItemStack craftItem = CraftItemStack.asCraftMirror(par2ItemStack);
            final BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(x, y, z));

            if (!BlockDispenser.eventFired)
            {
                world.getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled())
            {
                return par2ItemStack;
            }

            if (!event.getItem().equals(craftItem))
            {
                // Chain to handler for new item
                final ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                final IBehaviorDispenseItem ibehaviordispenseitem = (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry.getObject(eventStack.getItem());

                if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider && ibehaviordispenseitem != this)
                {
                    ibehaviordispenseitem.dispense(par1IBlockSource, eventStack);
                    return par2ItemStack;
                }
            }

            itembucket = (ItemBucket) CraftItemStack.asNMSCopy(event.getItem()).getItem();
        }

        // CraftBukkit end

        if (itembucket.tryPlaceContainedLiquid(par1IBlockSource.getWorld(), i + enumfacing.getFrontOffsetX(), j + enumfacing.getFrontOffsetY(), k + enumfacing.getFrontOffsetZ()))
        {
            // CraftBukkit start - Handle stacked buckets
            final Item item = Item.bucketEmpty;

            if (--par2ItemStack.stackSize == 0)
            {
                par2ItemStack.itemID = item.itemID;
                par2ItemStack.stackSize = 1;
            }
            else if (((TileEntityDispenser) par1IBlockSource.getBlockTileEntity()).addItem(new ItemStack(item)) < 0)
            {
                this.defaultDispenserItemBehavior.dispense(par1IBlockSource, new ItemStack(item));
            }

            // CraftBukkit end
            return par2ItemStack;
        }
        else
        {
            return this.defaultDispenserItemBehavior.dispense(par1IBlockSource, par2ItemStack);
        }
    }
}
