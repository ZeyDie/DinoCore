package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Random;

public class BlockSign extends BlockContainer
{
    private Class signEntityClass;

    /** Whether this is a freestanding sign or a wall-mounted sign */
    private boolean isFreestanding;

    protected BlockSign(final int par1, final Class par2Class, final boolean par3)
    {
        super(par1, Material.wood);
        this.isFreestanding = par3;
        this.signEntityClass = par2Class;
        final float f = 0.25F;
        final float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
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
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        return null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        if (!this.isFreestanding)
        {
            final int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
            final float f = 0.28125F;
            final float f1 = 0.78125F;
            final float f2 = 0.0F;
            final float f3 = 1.0F;
            final float f4 = 0.125F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

            if (l == 2)
            {
                this.setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
            }

            if (l == 3)
            {
                this.setBlockBounds(f2, f, 0.0F, f3, f1, f4);
            }

            if (l == 4)
            {
                this.setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
            }

            if (l == 5)
            {
                this.setBlockBounds(0.0F, f, f2, f4, f1, f3);
            }
        }
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return -1;
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
        return true;
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
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        try
        {
            return (TileEntity)this.signEntityClass.newInstance();
        }
        catch (final Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Item.sign.itemID;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        boolean flag = false;

        if (this.isFreestanding)
        {
            if (!par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid())
            {
                flag = true;
            }
        }
        else
        {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            flag = true;

            if (i1 == 2 && par1World.getBlockMaterial(par2, par3, par4 + 1).isSolid())
            {
                flag = false;
            }

            if (i1 == 3 && par1World.getBlockMaterial(par2, par3, par4 - 1).isSolid())
            {
                flag = false;
            }

            if (i1 == 4 && par1World.getBlockMaterial(par2 + 1, par3, par4).isSolid())
            {
                flag = false;
            }

            if (i1 == 5 && par1World.getBlockMaterial(par2 - 1, par3, par4).isSolid())
            {
                flag = false;
            }
        }

        if (flag)
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
        }

        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        // CraftBukkit start
        if (Block.blocksList[par5] != null && Block.blocksList[par5].canProvidePower())
        {
            final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);
            final int power = block.getBlockPower();
            final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, power, power);
            par1World.getServer().getPluginManager().callEvent(eventRedstone);
        }
        // CraftBukkit end
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Item.sign.itemID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister) {}
}
