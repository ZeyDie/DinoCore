package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockTrapDoor extends Block
{
    /** Set this to allow trapdoors to remain free-floating */
    public static boolean disableValidation = false;

    protected BlockTrapDoor(final int par1, final Material par2Material)
    {
        super(par1, par2Material);
        final float f = 0.5F;
        final float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabRedstone);
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
        return !isTrapdoorOpen(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 0;
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
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        this.setBlockBoundsForBlockRender(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        final float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.5F - f / 2.0F, 0.0F, 1.0F, 0.5F + f / 2.0F, 1.0F);
    }

    public void setBlockBoundsForBlockRender(final int par1)
    {
        final float f = 0.1875F;

        if ((par1 & 8) != 0)
        {
            this.setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if (isTrapdoorOpen(par1))
        {
            if ((par1 & 3) == 0)
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }

            if ((par1 & 3) == 1)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }

            if ((par1 & 3) == 2)
            {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((par1 & 3) == 3)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer) {}

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        if (this.blockMaterial == Material.iron)
        {
            return true;
        }
        else
        {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 ^ 4, 2);
            par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
            return true;
        }
    }

    public void onPoweredBlockChange(final World par1World, final int par2, final int par3, final int par4, final boolean par5)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        final boolean flag1 = (l & 4) > 0;

        if (flag1 != par5)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, l ^ 4, 2);
            par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
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
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            int j1 = par2;
            int k1 = par4;

            if ((i1 & 3) == 0)
            {
                k1 = par4 + 1;
            }

            if ((i1 & 3) == 1)
            {
                --k1;
            }

            if ((i1 & 3) == 2)
            {
                j1 = par2 + 1;
            }

            if ((i1 & 3) == 3)
            {
                --j1;
            }

            if (!(isValidSupportBlock(par1World.getBlockId(j1, par3, k1)) || par1World.isBlockSolidOnSide(j1, par3, k1, ForgeDirection.getOrientation((i1 & 3) + 2))))
            {
                par1World.setBlockToAir(par2, par3, par4);
                this.dropBlockAsItem(par1World, par2, par3, par4, i1, 0);
            }

            // CraftBukkit start
            if (par5 == 0 || par5 > 0 && Block.blocksList[par5] != null && Block.blocksList[par5].canProvidePower())
            {
                final org.bukkit.World bworld = par1World.getWorld();
                final org.bukkit.block.Block block = bworld.getBlockAt(par2, par3, par4);
                final int power = block.getBlockPower();
                final int oldPower = (par1World.getBlockMetadata(par2, par3, par4) & 4) > 0 ? 15 : 0;

                if (oldPower == 0 ^ power == 0 || (Block.blocksList[par5] != null && Block.blocksList[par5].canProvidePower()))
                {
                    final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, oldPower, power);
                    par1World.getServer().getPluginManager().callEvent(eventRedstone);
                    this.onPoweredBlockChange(par1World, par2, par3, par4, eventRedstone.getNewCurrent() > 0);
                }
            // CraftBukkit end
            }
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(final World par1World, final int par2, final int par3, final int par4, final Vec3 par5Vec3, final Vec3 par6Vec3)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9)
    {
        int j1 = 0;

        if (par5 == 2)
        {
            j1 = 0;
        }

        if (par5 == 3)
        {
            j1 = 1;
        }

        if (par5 == 4)
        {
            j1 = 2;
        }

        if (par5 == 5)
        {
            j1 = 3;
        }

        if (par5 != 1 && par5 != 0 && par7 > 0.5F)
        {
            j1 |= 8;
        }

        return j1;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(final World par1World, int par2, final int par3, int par4, final int par5)
    {
        int par41 = par4;
        int par21 = par2;
        if (disableValidation)
        {
            return true;
        }
        if (par5 == 0)
        {
            return false;
        }
        else if (par5 == 1)
        {
            return false;
        }
        else
        {
            if (par5 == 2)
            {
                ++par41;
            }

            if (par5 == 3)
            {
                --par41;
            }

            if (par5 == 4)
            {
                ++par21;
            }

            if (par5 == 5)
            {
                --par21;
            }

            return isValidSupportBlock(par1World.getBlockId(par21, par3, par41)) || par1World.isBlockSolidOnSide(par21, par3, par41, ForgeDirection.UP);
        }
    }

    public static boolean isTrapdoorOpen(final int par0)
    {
        return (par0 & 4) != 0;
    }

    /**
     * Checks if the block ID is a valid support block for the trap door to connect with. If it is not the trapdoor is
     * dropped into the world.
     */
    private static boolean isValidSupportBlock(final int par0)
    {
        if (disableValidation)
        {
            return true;
        }
        if (par0 <= 0)
        {
            return false;
        }
        else
        {
            final Block block = Block.blocksList[par0];
            return block != null && block.blockMaterial.isOpaque() && block.renderAsNormalBlock() || block == Block.glowStone || block instanceof BlockHalfSlab || block instanceof BlockStairs;
        }
    }
}
