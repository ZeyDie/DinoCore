package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;

// CraftBukkit start
// CraftBukkit end

final class DispenserBehaviorDye extends BehaviorDefaultDispenseItem
{
    private boolean field_96461_b = true;

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    protected ItemStack dispenseStack(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        if (par2ItemStack.getItemDamage() == 15)
        {
            final EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
            final World world = par1IBlockSource.getWorld();
            final int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
            final int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
            final int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
            // CraftBukkit start
            final ItemStack itemstack1 = par2ItemStack.splitStack(1);
            final org.bukkit.block.Block block = world.getWorld().getBlockAt(par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt());
            final CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);
            final BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));

            if (!BlockDispenser.eventFired)
            {
                world.getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled())
            {
                par2ItemStack.stackSize++;
                return par2ItemStack;
            }

            if (!event.getItem().equals(craftItem))
            {
                par2ItemStack.stackSize++;
                // Chain to handler for new item
                final ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                final IBehaviorDispenseItem ibehaviordispenseitem = (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry.getObject(eventStack.getItem());

                if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider && ibehaviordispenseitem != this)
                {
                    ibehaviordispenseitem.dispense(par1IBlockSource, eventStack);
                    return par2ItemStack;
                }
            }
            // CraftBukkit end
            if (ItemDye.func_96604_a(itemstack1, world, i, j, k))   // CraftBukkit - itemstack -> itemstack1
            {
                if (!world.isRemote)
                {
                    world.playAuxSFX(2005, i, j, k, 0);
                }
            }
            else
            {
                this.field_96461_b = false;
            }

            return par2ItemStack;
        }
        else
        {
            return super.dispenseStack(par1IBlockSource, par2ItemStack);
        }
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(final IBlockSource par1IBlockSource)
    {
        if (this.field_96461_b)
        {
            par1IBlockSource.getWorld().playAuxSFX(1000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
        }
        else
        {
            par1IBlockSource.getWorld().playAuxSFX(1001, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
        }
    }
}
