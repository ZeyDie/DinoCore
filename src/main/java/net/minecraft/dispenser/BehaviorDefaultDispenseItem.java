package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;

// CraftBukkit start
// CraftBukkit end

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem
{
    /**
     * Dispenses the specified ItemStack from a dispenser.
     */
    public final ItemStack dispense(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        final ItemStack itemstack1 = this.dispenseStack(par1IBlockSource, par2ItemStack);
        this.playDispenseSound(par1IBlockSource);
        this.spawnDispenseParticles(par1IBlockSource, BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata()));
        return itemstack1;
    }

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    protected ItemStack dispenseStack(final IBlockSource par1IBlockSource, final ItemStack par2ItemStack)
    {
        final EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        final IPosition iposition = BlockDispenser.getIPositionFromBlockSource(par1IBlockSource);
        final ItemStack itemstack1 = par2ItemStack.splitStack(1);
        // CraftBukkit start
        if (!doDispense(par1IBlockSource.getWorld(), itemstack1, 6, enumfacing, par1IBlockSource))
        {
            par2ItemStack.stackSize++;
        }
        // CraftBukkit end
        return par2ItemStack;
    }

    // Cauldron start - vanilla compatibility
    public static void doDispense(final World par0World, final ItemStack par1ItemStack, final int par2, final EnumFacing par3EnumFacing, final IPosition par4IPosition)
    {
        final double d0 = par4IPosition.getX();
        final double d1 = par4IPosition.getY();
        final double d2 = par4IPosition.getZ();
        final EntityItem entityitem = new EntityItem(par0World, d0, d1 - 0.3D, d2, par1ItemStack);
        final double d3 = par0World.rand.nextDouble() * 0.1D + 0.2D;
        entityitem.motionX = (double)par3EnumFacing.getFrontOffsetX() * d3;
        entityitem.motionY = 0.20000000298023224D;
        entityitem.motionZ = (double)par3EnumFacing.getFrontOffsetZ() * d3;
        entityitem.motionX += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        entityitem.motionY += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        entityitem.motionZ += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        par0World.spawnEntityInWorld(entityitem);
        // TODO: add CB event?
    }
    // Cauldron end

    // CraftBukkit start - void -> boolean return, IPosition -> ISourceBlock last argument
    public static boolean doDispense(final World par0World, final ItemStack par1ItemStack, final int par2, final EnumFacing par3EnumFacing, final IBlockSource par4IPosition)
    {
        final IPosition iposition = BlockDispenser.getIPositionFromBlockSource(par4IPosition);
        // CraftBukkit end
        final double d0 = iposition.getX();
        final double d1 = iposition.getY();
        final double d2 = iposition.getZ();
        final EntityItem entityitem = new EntityItem(par0World, d0, d1 - 0.3D, d2, par1ItemStack);
        final double d3 = par0World.rand.nextDouble() * 0.1D + 0.2D;
        entityitem.motionX = (double)par3EnumFacing.getFrontOffsetX() * d3;
        entityitem.motionY = 0.20000000298023224D;
        entityitem.motionZ = (double)par3EnumFacing.getFrontOffsetZ() * d3;
        entityitem.motionX += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        entityitem.motionY += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        entityitem.motionZ += par0World.rand.nextGaussian() * 0.007499999832361937D * (double)par2;
        // CraftBukkit start
        final org.bukkit.block.Block block = par0World.getWorld().getBlockAt(par4IPosition.getXInt(), par4IPosition.getYInt(), par4IPosition.getZInt());
        final CraftItemStack craftItem = CraftItemStack.asCraftMirror(par1ItemStack);
        final BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(entityitem.motionX, entityitem.motionY, entityitem.motionZ));

        if (!BlockDispenser.eventFired)
        {
            par0World.getServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled())
        {
            return false;
        }

        entityitem.setEntityItemStack(CraftItemStack.asNMSCopy(event.getItem()));
        entityitem.motionX = event.getVelocity().getX();
        entityitem.motionY = event.getVelocity().getY();
        entityitem.motionZ = event.getVelocity().getZ();

        if (!event.getItem().equals(craftItem))
        {
            // Chain to handler for new item
            final ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            final IBehaviorDispenseItem ibehaviordispenseitem = (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry.getObject(eventStack.getItem());

            if (ibehaviordispenseitem != IBehaviorDispenseItem.itemDispenseBehaviorProvider && ibehaviordispenseitem.getClass() != BehaviorDefaultDispenseItem.class)
            {
                ibehaviordispenseitem.dispense(par4IPosition, eventStack);
            }
            else
            {
                par0World.spawnEntityInWorld(entityitem);
            }

            return false;
        }

        par0World.spawnEntityInWorld(entityitem);
        return true;
        // CraftBukkit end
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(final IBlockSource par1IBlockSource)
    {
        par1IBlockSource.getWorld().playAuxSFX(1000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
    }

    /**
     * Order clients to display dispense particles from the specified block and facing.
     */
    protected void spawnDispenseParticles(final IBlockSource par1IBlockSource, final EnumFacing par2EnumFacing)
    {
        par1IBlockSource.getWorld().playAuxSFX(2000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), this.func_82488_a(par2EnumFacing));
    }

    private int func_82488_a(final EnumFacing par1EnumFacing)
    {
        return par1EnumFacing.getFrontOffsetX() + 1 + (par1EnumFacing.getFrontOffsetZ() + 1) * 3;
    }
}
