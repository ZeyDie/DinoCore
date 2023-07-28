package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPistonMoving extends BlockContainer
{
    public BlockPistonMoving(final int par1)
    {
        super(par1, Material.piston);
        this.setHardness(-1.0F);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        return null;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4) {}

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);

        if (tileentity instanceof TileEntityPiston)
        {
            ((TileEntityPiston)tileentity).clearPistonTileEntity();
        }
        else
        {
            super.breakBlock(par1World, par2, par3, par4, par5, par6);
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        return false;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        if (!par1World.isRemote && par1World.getBlockTileEntity(par2, par3, par4) == null)
        {
            par1World.setBlockToAir(par2, par3, par4);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return 0;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final int par7)
    {
        if (!par1World.isRemote)
        {
            final TileEntityPiston tileentitypiston = this.getTileEntityAtLocation(par1World, par2, par3, par4);

            if (tileentitypiston != null)
            {
                Block.blocksList[tileentitypiston.getStoredBlockID()].dropBlockAsItem(par1World, par2, par3, par4, tileentitypiston.getBlockMetadata(), 0);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!par1World.isRemote)
        {
            par1World.getBlockTileEntity(par2, par3, par4);
        }
    }

    /**
     * gets a new TileEntityPiston created with the arguments provided.
     */
    public static TileEntity getTileEntity(final int par0, final int par1, final int par2, final boolean par3, final boolean par4)
    {
        return new TileEntityPiston(par0, par1, par2, par3, par4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        final TileEntityPiston tileentitypiston = this.getTileEntityAtLocation(par1World, par2, par3, par4);

        if (tileentitypiston == null)
        {
            return null;
        }
        else
        {
            float f = tileentitypiston.getProgress(0.0F);

            if (tileentitypiston.isExtending())
            {
                f = 1.0F - f;
            }

            return this.getAxisAlignedBB(par1World, par2, par3, par4, tileentitypiston.getStoredBlockID(), f, tileentitypiston.getPistonOrientation());
        }
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final TileEntityPiston tileentitypiston = this.getTileEntityAtLocation(par1IBlockAccess, par2, par3, par4);

        if (tileentitypiston != null)
        {
            final Block block = Block.blocksList[tileentitypiston.getStoredBlockID()];

            if (block == null || block == this)
            {
                return;
            }

            block.setBlockBoundsBasedOnState(par1IBlockAccess, par2, par3, par4);
            float f = tileentitypiston.getProgress(0.0F);

            if (tileentitypiston.isExtending())
            {
                f = 1.0F - f;
            }

            final int l = tileentitypiston.getPistonOrientation();
            this.minX = block.getBlockBoundsMinX() - (double)((float)Facing.offsetsXForSide[l] * f);
            this.minY = block.getBlockBoundsMinY() - (double)((float)Facing.offsetsYForSide[l] * f);
            this.minZ = block.getBlockBoundsMinZ() - (double)((float)Facing.offsetsZForSide[l] * f);
            this.maxX = block.getBlockBoundsMaxX() - (double)((float)Facing.offsetsXForSide[l] * f);
            this.maxY = block.getBlockBoundsMaxY() - (double)((float)Facing.offsetsYForSide[l] * f);
            this.maxZ = block.getBlockBoundsMaxZ() - (double)((float)Facing.offsetsZForSide[l] * f);
        }
    }

    public AxisAlignedBB getAxisAlignedBB(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final int par7)
    {
        if (par5 != 0 && par5 != this.blockID)
        {
            final AxisAlignedBB axisalignedbb = Block.blocksList[par5].getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);

            if (axisalignedbb == null)
            {
                return null;
            }
            else
            {
                if (Facing.offsetsXForSide[par7] < 0)
                {
                    axisalignedbb.minX -= (double)((float)Facing.offsetsXForSide[par7] * par6);
                }
                else
                {
                    axisalignedbb.maxX -= (double)((float)Facing.offsetsXForSide[par7] * par6);
                }

                if (Facing.offsetsYForSide[par7] < 0)
                {
                    axisalignedbb.minY -= (double)((float)Facing.offsetsYForSide[par7] * par6);
                }
                else
                {
                    axisalignedbb.maxY -= (double)((float)Facing.offsetsYForSide[par7] * par6);
                }

                if (Facing.offsetsZForSide[par7] < 0)
                {
                    axisalignedbb.minZ -= (double)((float)Facing.offsetsZForSide[par7] * par6);
                }
                else
                {
                    axisalignedbb.maxZ -= (double)((float)Facing.offsetsZForSide[par7] * par6);
                }

                return axisalignedbb;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * gets the piston tile entity at the specified location
     */
    private TileEntityPiston getTileEntityAtLocation(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final TileEntity tileentity = par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
        return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("piston_top_normal");
    }
}
