package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer
{
    public BlockNote(final int par1)
    {
        super(par1, Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final boolean flag = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
        final TileEntityNote tileentitynote = (TileEntityNote)par1World.getBlockTileEntity(par2, par3, par4);

        if (tileentitynote != null && tileentitynote.previousRedstoneState != flag)
        {
            if (flag)
            {
                tileentitynote.triggerNote(par1World, par2, par3, par4);
            }

            tileentitynote.previousRedstoneState = flag;
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            final TileEntityNote tileentitynote = (TileEntityNote)par1World.getBlockTileEntity(par2, par3, par4);

            if (tileentitynote != null)
            {
                tileentitynote.changePitch();
                tileentitynote.triggerNote(par1World, par2, par3, par4);
            }

            return true;
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer)
    {
        if (!par1World.isRemote)
        {
            final TileEntityNote tileentitynote = (TileEntityNote)par1World.getBlockTileEntity(par2, par3, par4);

            if (tileentitynote != null)
            {
                tileentitynote.triggerNote(par1World, par2, par3, par4);
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        return new TileEntityNote();
    }

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
     * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
     */
    public boolean onBlockEventReceived(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final float f = (float)Math.pow(2.0D, (double)(par6 - 12) / 12.0D);
        String s = "harp";

        if (par5 == 1)
        {
            s = "bd";
        }

        if (par5 == 2)
        {
            s = "snare";
        }

        if (par5 == 3)
        {
            s = "hat";
        }

        if (par5 == 4)
        {
            s = "bassattack";
        }

        par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "note." + s, 3.0F, f);
        par1World.spawnParticle("note", (double)par2 + 0.5D, (double)par3 + 1.2D, (double)par4 + 0.5D, (double)par6 / 24.0D, 0.0D, 0.0D);
        return true;
    }
}
