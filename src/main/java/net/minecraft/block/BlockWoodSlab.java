package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.List;
import java.util.Random;

public class BlockWoodSlab extends BlockHalfSlab
{
    /** The type of tree this slab came from. */
    public static final String[] woodType = {"oak", "spruce", "birch", "jungle"};

    public BlockWoodSlab(final int par1, final boolean par2)
    {
        super(par1, par2, Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return Block.planks.getIcon(par1, par2 & 7);
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.woodSingleSlab.blockID;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(final int par1)
    {
        return new ItemStack(Block.woodSingleSlab.blockID, 2, par1 & 7);
    }

    /**
     * Returns the slab block name with step type.
     */
    public String getFullSlabName(int par1)
    {
        int par11 = par1;
        if (par11 < 0 || par11 >= woodType.length)
        {
            par11 = 0;
        }

        return super.getUnlocalizedName() + "." + woodType[par11];
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        if (par1 != Block.woodDoubleSlab.blockID)
        {
            for (int j = 0; j < 4; ++j)
            {
                par3List.add(new ItemStack(par1, 1, j));
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister) {}
}
