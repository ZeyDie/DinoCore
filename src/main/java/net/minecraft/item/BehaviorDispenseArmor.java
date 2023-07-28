package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySelectorArmoredMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;

import java.util.List;

// CraftBukkit start
// CraftBukkit end

final class BehaviorDispenseArmor extends BehaviorDefaultDispenseItem
{
    BehaviorDispenseArmor() {}

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    protected ItemStack dispenseStack(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        final EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        final int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
        final int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
        final int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
        final AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
        final List list = par1IBlockSource.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new EntitySelectorArmoredMob(par2ItemStack));

        if (!list.isEmpty())
        {
            final EntityLivingBase entitylivingbase = (EntityLivingBase)list.get(0);
            final int l = entitylivingbase instanceof EntityPlayer ? 1 : 0;
            final int i1 = EntityLiving.getArmorPosition(par2ItemStack);
            // CraftBukkit start
            final ItemStack itemstack1 = par2ItemStack.splitStack(1);
            final World world = par1IBlockSource.getWorld();
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
            itemstack1.stackSize = 1;
            entitylivingbase.setCurrentItemOrArmor(i1, itemstack1);  //BUGFIX Forge: Vanilla bug fix associated with fixed setCurrentItemOrArmor indexs for players.

            if (entitylivingbase instanceof EntityLiving)
            {
                ((EntityLiving)entitylivingbase).setEquipmentDropChance(i1, 2.0F);
            }

            // --itemstack.count; // CraftBukkit - handled above
            return par2ItemStack;
        }
        else
        {
            return super.dispenseStack(par1IBlockSource, par2ItemStack);
        }
    }
}
