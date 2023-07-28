package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockLeavesBase extends Block
{
    /**
     * Used to determine how to display leaves based on the graphics level. May also be used in rendering for
     * transparency, not sure.
     */
    public boolean graphicsLevel;

    protected BlockLeavesBase(final int par1, final Material par2Material, final boolean par3)
    {
        super(par1, par2Material);
        this.graphicsLevel = par3;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return !this.graphicsLevel && i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
