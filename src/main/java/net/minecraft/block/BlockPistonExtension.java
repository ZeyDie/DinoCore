package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockPistonExtension extends Block
{
    @SideOnly(Side.CLIENT)

    /** The texture for the 'head' of the piston. Sticky or normal. */
    private Icon headTexture;

    public BlockPistonExtension(final int par1)
    {
        super(par1, Material.piston);
        this.setStepSound(soundStoneFootstep);
        this.setHardness(0.5F);
    }

    @SideOnly(Side.CLIENT)
    public void setHeadTexture(final Icon par1Icon)
    {
        this.headTexture = par1Icon;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(final World par1World, final int par2, final int par3, final int par4, final int par5, final EntityPlayer par6EntityPlayer)
    {
        if (par6EntityPlayer.capabilities.isCreativeMode)
        {
            final int i1 = getDirectionMeta(par5);
            final int j1 = par1World.getBlockId(par2 - Facing.offsetsXForSide[i1], par3 - Facing.offsetsYForSide[i1], par4 - Facing.offsetsZForSide[i1]);

            if (j1 == Block.pistonBase.blockID || j1 == Block.pistonStickyBase.blockID)
            {
                par1World.setBlockToAir(par2 - Facing.offsetsXForSide[i1], par3 - Facing.offsetsYForSide[i1], par4 - Facing.offsetsZForSide[i1]);
            }
        }

        super.onBlockHarvested(par1World, par2, par3, par4, par5, par6EntityPlayer);
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(final World par1World, int par2, int par3, int par4, final int par5, int par6)
    {
        int par21 = par2;
        int par31 = par3;
        int par41 = par4;
        int par61 = par6;
        super.breakBlock(par1World, par21, par31, par41, par5, par61);
        // CraftBukkit start
        if ((par61 & 7) >= Facing.oppositeSide.length)
        {
            return;  // fix a piston AIOOBE issue
        }
        // CraftBukkit end
        final int j1 = Facing.oppositeSide[getDirectionMeta(par61)];
        par21 += Facing.offsetsXForSide[j1];
        par31 += Facing.offsetsYForSide[j1];
        par41 += Facing.offsetsZForSide[j1];
        final int k1 = par1World.getBlockId(par21, par31, par41);

        if (k1 == Block.pistonBase.blockID || k1 == Block.pistonStickyBase.blockID)
        {
            par61 = par1World.getBlockMetadata(par21, par31, par41);

            if (BlockPistonBase.isExtended(par61))
            {
                Block.blocksList[k1].dropBlockAsItem(par1World, par21, par31, par41, par61, 0);
                par1World.setBlockToAir(par21, par31, par41);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void clearHeadTexture()
    {
        this.headTexture = null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        final int k = getDirectionMeta(par2);
        return par1 == k ? (this.headTexture != null ? this.headTexture : ((par2 & 8) != 0 ? BlockPistonBase.getPistonBaseIcon("piston_top_sticky") : BlockPistonBase.getPistonBaseIcon("piston_top_normal"))) : (k < 6 && par1 == Facing.oppositeSide[k] ? BlockPistonBase.getPistonBaseIcon("piston_top_normal") : BlockPistonBase.getPistonBaseIcon("piston_side"));
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister) {}

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 17;
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
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 0;
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(final World par1World, final int par2, final int par3, final int par4, final AxisAlignedBB par5AxisAlignedBB, final List par6List, final Entity par7Entity)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        final float f = 0.25F;
        final float f1 = 0.375F;
        final float f2 = 0.625F;
        final float f3 = 0.25F;
        final float f4 = 0.75F;

        switch (getDirectionMeta(l))
        {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        final float f = 0.25F;

        switch (getDirectionMeta(l))
        {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = getDirectionMeta(par1World.getBlockMetadata(par2, par3, par4));
        // CraftBukkit start
        if ((i1 & 7) >= Facing.oppositeSide.length)
        {
            return;    // fix a piston AIOOBE issue
        }
        // CraftBukkit end
        final int j1 = par1World.getBlockId(par2 - Facing.offsetsXForSide[i1], par3 - Facing.offsetsYForSide[i1], par4 - Facing.offsetsZForSide[i1]);

        if (j1 != Block.pistonBase.blockID && j1 != Block.pistonStickyBase.blockID)
        {
            par1World.setBlockToAir(par2, par3, par4);
        }
        else
        {
            Block.blocksList[j1].onNeighborBlockChange(par1World, par2 - Facing.offsetsXForSide[i1], par3 - Facing.offsetsYForSide[i1], par4 - Facing.offsetsZForSide[i1], par5);
        }
    }

    public static int getDirectionMeta(final int par0)
    {
        return par0 & 7;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        return (l & 8) != 0 ? Block.pistonStickyBase.blockID : Block.pistonBase.blockID;
    }
}
