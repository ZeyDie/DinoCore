package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;

// CraftBukkit start
// CraftBukkit end

final class DispenserBehaviorMobEgg extends BehaviorDefaultDispenseItem
{
    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        final EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        final double d0 = par1IBlockSource.getX() + (double)enumfacing.getFrontOffsetX();
        final double d1 = (double)((float)par1IBlockSource.getYInt() + 0.2F);
        final double d2 = par1IBlockSource.getZ() + (double)enumfacing.getFrontOffsetZ();
        // CraftBukkit start
        final World world = par1IBlockSource.getWorld();
        ItemStack itemstack1 = par2ItemStack.splitStack(1);
        final org.bukkit.block.Block block = world.getWorld().getBlockAt(par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt());
        final CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);
        final BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1, d2));

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

        itemstack1 = CraftItemStack.asNMSCopy(event.getItem());
        final Entity entity = ItemMonsterPlacer.spawnCreature(par1IBlockSource.getWorld(), par2ItemStack.getItemDamage(), event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());

        if (entity instanceof EntityLivingBase && par2ItemStack.hasDisplayName())
        {
            ((EntityLiving)entity).setCustomNameTag(par2ItemStack.getDisplayName());
        }

        // itemstack1.splitStack(1); // Handled during event processing
        // CraftBukkit end
        return par2ItemStack;
    }
}
