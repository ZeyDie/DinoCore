package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public abstract class BlockHalfSlab extends Block
{
    protected final boolean isDoubleSlab;

    public BlockHalfSlab(final int par1, final boolean par2, final Material par3Material)
    {
        super(par1, par3Material);
        this.isDoubleSlab = par2;

        if (par2)
        {
            opaqueCubeLookup[par1] = true;
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        this.setLightOpacity(255);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        if (this.isDoubleSlab)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            final boolean flag = (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0;

            if (flag)
            {
                this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        if (this.isDoubleSlab)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(final World par1World, final int par2, final int par3, final int par4, final AxisAlignedBB par5AxisAlignedBB, final List par6List, final Entity par7Entity)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return this.isDoubleSlab;
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9)
    {
        return this.isDoubleSlab ? par9 : (par5 != 0 && (par5 == 1 || (double)par7 <= 0.5D) ? par9 : par9 | 8);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return this.isDoubleSlab ? 2 : 1;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(final int par1)
    {
        return par1 & 7;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return this.isDoubleSlab;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        if (this.isDoubleSlab)
        {
            return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
        }
        else if (par5 != 1 && par5 != 0 && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5))
        {
            return false;
        }
        else
        {
            final int i1 = par2 + Facing.offsetsXForSide[Facing.oppositeSide[par5]];
            final int j1 = par3 + Facing.offsetsYForSide[Facing.oppositeSide[par5]];
            final int k1 = par4 + Facing.offsetsZForSide[Facing.oppositeSide[par5]];
            final boolean flag = (par1IBlockAccess.getBlockMetadata(i1, j1, k1) & 8) != 0;
            return flag ? (par5 == 0 ? true : (par5 == 1 && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) ? true : !isBlockSingleSlab(par1IBlockAccess.getBlockId(par2, par3, par4)) || (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) == 0)) : (par5 == 1 ? true : (par5 == 0 && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5) ? true : !isBlockSingleSlab(par1IBlockAccess.getBlockId(par2, par3, par4)) || (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) != 0));
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Takes a block ID, returns true if it's the same as the ID for a stone or wooden single slab.
     */
    private static boolean isBlockSingleSlab(final int par0)
    {
        return par0 == Block.stoneSingleSlab.blockID || par0 == Block.woodSingleSlab.blockID;
    }

    /**
     * Returns the slab block name with step type.
     */
    public abstract String getFullSlabName(int i);

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(final World par1World, final int par2, final int par3, final int par4)
    {
        return super.getDamageValue(par1World, par2, par3, par4) & 7;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return isBlockSingleSlab(this.blockID) ? this.blockID : (this.blockID == Block.stoneDoubleSlab.blockID ? Block.stoneSingleSlab.blockID : (this.blockID == Block.woodDoubleSlab.blockID ? Block.woodSingleSlab.blockID : Block.stoneSingleSlab.blockID));
    }
}
