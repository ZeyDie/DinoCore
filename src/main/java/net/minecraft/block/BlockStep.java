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

public class BlockStep extends BlockHalfSlab
{
    /** The list of the types of step blocks. */
    public static final String[] blockStepTypes = {"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
    @SideOnly(Side.CLIENT)
    private Icon theIcon;

    public BlockStep(final int par1, final boolean par2)
    {
        super(par1, par2, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, final int par2)
    {
        int par11 = par1;
        final int k = par2 & 7;

        if (this.isDoubleSlab && (par2 & 8) != 0)
        {
            par11 = 1;
        }

        return k == 0 ? (par11 != 1 && par11 != 0 ? this.theIcon : this.blockIcon) : (k == 1 ? Block.sandStone.getBlockTextureFromSide(par11) : (k == 2 ? Block.planks.getBlockTextureFromSide(par11) : (k == 3 ? Block.cobblestone.getBlockTextureFromSide(par11) : (k == 4 ? Block.brick.getBlockTextureFromSide(par11) : (k == 5 ? Block.stoneBrick.getIcon(par11, 0) : (k == 6 ? Block.netherBrick.getBlockTextureFromSide(1) : (k == 7 ? Block.blockNetherQuartz.getBlockTextureFromSide(par11) : this.blockIcon)))))));
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("stone_slab_top");
        this.theIcon = par1IconRegister.registerIcon("stone_slab_side");
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.stoneSingleSlab.blockID;
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(final int par1)
    {
        return new ItemStack(Block.stoneSingleSlab.blockID, 2, par1 & 7);
    }

    /**
     * Returns the slab block name with step type.
     */
    public String getFullSlabName(int par1)
    {
        int par11 = par1;
        if (par11 < 0 || par11 >= blockStepTypes.length)
        {
            par11 = 0;
        }

        return super.getUnlocalizedName() + "." + blockStepTypes[par11];
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        if (par1 != Block.stoneDoubleSlab.blockID)
        {
            for (int j = 0; j <= 7; ++j)
            {
                if (j != 2)
                {
                    par3List.add(new ItemStack(par1, 1, j));
                }
            }
        }
    }
}
