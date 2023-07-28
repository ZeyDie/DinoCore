package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional
{
    public BlockFenceGate(final int par1)
    {
        super(par1, Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return Block.planks.getBlockTextureFromSide(par1);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        return !par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid() ? false : super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        return isFenceGateOpen(l) ? null : (l != 2 && l != 0 ? AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + 0.375F), (double)par3, (double)par4, (double)((float)par2 + 0.625F), (double)((float)par3 + 1.5F), (double)(par4 + 1)) : AxisAlignedBB.getAABBPool().getAABB((double)par2, (double)par3, (double)((float)par4 + 0.375F), (double)(par2 + 1), (double)((float)par3 + 1.5F), (double)((float)par4 + 0.625F)));
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final int l = getDirection(par1IBlockAccess.getBlockMetadata(par2, par3, par4));

        if (l != 2 && l != 0)
        {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }
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

    public boolean getBlocksMovement(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        return isFenceGateOpen(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 21;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLivingBase, final ItemStack par6ItemStack)
    {
        final int l = (MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        int i1 = par1World.getBlockMetadata(par2, par3, par4);

        if (isFenceGateOpen(i1))
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 & -5, 2);
        }
        else
        {
            final int j1 = (MathHelper.floor_double((double)(par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
            final int k1 = getDirection(i1);

            if (k1 == (j1 + 2) % 4)
            {
                i1 = j1;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 | 4, 2);
        }

        par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
        return true;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!par1World.isRemote)
        {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            final boolean flag = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);

            if (flag || par5 > 0 && Block.blocksList[par5].canProvidePower())
            {
                if (flag && !isFenceGateOpen(i1))
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 | 4, 2);
                    par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
                }
                else if (!flag && isFenceGateOpen(i1))
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 & -5, 2);
                    par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
                }
            }
        }
    }

    /**
     * Returns if the fence gate is open according to its metadata.
     */
    public static boolean isFenceGateOpen(final int par0)
    {
        return (par0 & 4) != 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister) {}
}
