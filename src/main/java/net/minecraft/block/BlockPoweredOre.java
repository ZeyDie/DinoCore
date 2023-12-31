package net.minecraft.block;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;

public class BlockPoweredOre extends BlockOreStorage
{
    public BlockPoweredOre(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        return 15;
    }
}
