package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Random;

public class BlockCommandBlock extends BlockContainer
{
    public BlockCommandBlock(final int par1)
    {
        super(par1, Material.iron);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        return new TileEntityCommandBlock();
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!par1World.isRemote)
        {
            final boolean flag = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            final boolean flag1 = (i1 & 1) != 0;
            // CraftBukkit start
            final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);
            final int old = flag1 ? 15 : 0;
            final int current = flag ? 15 : 0;
            final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
            par1World.getServer().getPluginManager().callEvent(eventRedstone);
            // CraftBukkit end

            if (eventRedstone.getNewCurrent() > 0 && !(eventRedstone.getOldCurrent() > 0))   // CraftBukkit
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 | 1, 4);
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
            }
            else if (!(eventRedstone.getNewCurrent() > 0) && eventRedstone.getOldCurrent() > 0)     // CraftBukkit
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 & -2, 4);
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        final TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);

        if (tileentity != null && tileentity instanceof TileEntityCommandBlock)
        {
            final TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)tileentity;
            tileentitycommandblock.setSignalStrength(tileentitycommandblock.executeCommandOnPowered(par1World));
            par1World.func_96440_m(par2, par3, par4, this.blockID);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World par1World)
    {
        return 1;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        final TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)par1World.getBlockTileEntity(par2, par3, par4);

        if (tileentitycommandblock != null)
        {
            par5EntityPlayer.displayGUIEditSign(tileentitycommandblock);
        }

        return true;
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
        return tileentity != null && tileentity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileentity).getSignalStrength() : 0;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLivingBase, final ItemStack par6ItemStack)
    {
        final TileEntityCommandBlock tileentitycommandblock = (TileEntityCommandBlock)par1World.getBlockTileEntity(par2, par3, par4);

        if (par6ItemStack.hasDisplayName())
        {
            tileentitycommandblock.setCommandSenderName(par6ItemStack.getDisplayName());
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 0;
    }
}
