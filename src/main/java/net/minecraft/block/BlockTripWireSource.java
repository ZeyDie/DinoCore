package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Random;

import static net.minecraftforge.common.ForgeDirection.*;

public class BlockTripWireSource extends Block
{
    public BlockTripWireSource(final int par1)
    {
        super(par1, Material.circuits);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        return null;
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
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 29;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World par1World)
    {
        return 10;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final ForgeDirection dir = ForgeDirection.getOrientation(par5);
        return (dir == NORTH && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH)) ||
               (dir == SOUTH && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH)) ||
               (dir == WEST  && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST )) ||
               (dir == EAST  && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST ));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        return par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST ) ||
               par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST ) ||
               par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH) ||
               par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH);
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9)
    {
        byte b0 = 0;

        if (par5 == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH, true))
        {
            b0 = 2;
        }

        if (par5 == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH, true))
        {
            b0 = 0;
        }

        if (par5 == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST, true))
        {
            b0 = 1;
        }

        if (par5 == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST, true))
        {
            b0 = 3;
        }

        return b0;
    }

    /**
     * Called after a block is placed
     */
    public void onPostBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        this.func_72143_a(par1World, par2, par3, par4, this.blockID, par5, false, -1, 0);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (par5 != this.blockID)
        {
            if (this.func_72144_l(par1World, par2, par3, par4))
            {
                final int i1 = par1World.getBlockMetadata(par2, par3, par4);
                final int j1 = i1 & 3;
                boolean flag = false;

                if (!par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST) && j1 == 3)
                {
                    flag = true;
                }

                if (!par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST) && j1 == 1)
                {
                    flag = true;
                }

                if (!par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH) && j1 == 0)
                {
                    flag = true;
                }

                if (!par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH) && j1 == 2)
                {
                    flag = true;
                }

                if (flag)
                {
                    this.dropBlockAsItem(par1World, par2, par3, par4, i1, 0);
                    par1World.setBlockToAir(par2, par3, par4);
                }
            }
        }
    }

    public void func_72143_a(final World par1World, final int par2, final int par3, final int par4, final int par5, int par6, final boolean par7, final int par8, final int par9)
    {
        final int l1 = par6 & 3;
        final boolean flag1 = (par6 & 4) == 4;
        final boolean flag2 = (par6 & 8) == 8;
        boolean flag3 = par5 == Block.tripWireSource.blockID;
        boolean flag4 = false;
        final boolean flag5 = !par1World.isBlockSolidOnSide(par2, par3 - 1, par4, UP);
        final int i2 = Direction.offsetX[l1];
        final int j2 = Direction.offsetZ[l1];
        int k2 = 0;
        final int[] aint = new int[42];
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;

        for (i3 = 1; i3 < 42; ++i3)
        {
            l2 = par2 + i2 * i3;
            k3 = par4 + j2 * i3;
            j3 = par1World.getBlockId(l2, par3, k3);

            if (j3 == Block.tripWireSource.blockID)
            {
                l3 = par1World.getBlockMetadata(l2, par3, k3);

                if ((l3 & 3) == Direction.rotateOpposite[l1])
                {
                    k2 = i3;
                }

                break;
            }

            if (j3 != Block.tripWire.blockID && i3 != par8)
            {
                aint[i3] = -1;
                flag3 = false;
            }
            else
            {
                l3 = i3 == par8 ? par9 : par1World.getBlockMetadata(l2, par3, k3);
                final boolean flag6 = (l3 & 8) != 8;
                final boolean flag7 = (l3 & 1) == 1;
                final boolean flag8 = (l3 & 2) == 2;
                flag3 &= flag8 == flag5;
                flag4 |= flag6 && flag7;
                aint[i3] = l3;

                if (i3 == par8)
                {
                    par1World.scheduleBlockUpdate(par2, par3, par4, par5, this.tickRate(par1World));
                    flag3 &= flag6;
                }
            }
        }

        flag3 &= k2 > 1;
        flag4 &= flag3;
        i3 = (flag3 ? 4 : 0) | (flag4 ? 8 : 0);
        int par61 = l1 | i3;

        if (k2 > 0)
        {
            l2 = par2 + i2 * k2;
            k3 = par4 + j2 * k2;
            j3 = Direction.rotateOpposite[l1];
            par1World.setBlockMetadataWithNotify(l2, par3, k3, j3 | i3, 3);
            this.notifyNeighborOfChange(par1World, l2, par3, k3, j3);
            this.playSoundEffect(par1World, l2, par3, k3, flag3, flag4, flag1, flag2);
        }

        // CraftBukkit start
        final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);
        final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
        par1World.getServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0)
        {
            return;
        }
        // CraftBukkit end
        this.playSoundEffect(par1World, par2, par3, par4, flag3, flag4, flag1, flag2);

        if (par5 > 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par61, 3);

            if (par7)
            {
                this.notifyNeighborOfChange(par1World, par2, par3, par4, l1);
            }
        }

        if (flag1 != flag3)
        {
            for (l2 = 1; l2 < k2; ++l2)
            {
                k3 = par2 + i2 * l2;
                j3 = par4 + j2 * l2;
                l3 = aint[l2];

                if (l3 >= 0)
                {
                    if (flag3)
                    {
                        l3 |= 4;
                    }
                    else
                    {
                        l3 &= -5;
                    }

                    par1World.setBlockMetadataWithNotify(k3, par3, j3, l3, 3);
                }
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        this.func_72143_a(par1World, par2, par3, par4, this.blockID, par1World.getBlockMetadata(par2, par3, par4), true, -1, 0);
    }

    /**
     * only of the conditions are right
     */
    private void playSoundEffect(final World par1World, final int par2, final int par3, final int par4, final boolean par5, final boolean par6, final boolean par7, final boolean par8)
    {
        if (par6 && !par8)
        {
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.1D, (double)par4 + 0.5D, "random.click", 0.4F, 0.6F);
        }
        else if (!par6 && par8)
        {
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.1D, (double)par4 + 0.5D, "random.click", 0.4F, 0.5F);
        }
        else if (par5 && !par7)
        {
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.1D, (double)par4 + 0.5D, "random.click", 0.4F, 0.7F);
        }
        else if (!par5 && par7)
        {
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.1D, (double)par4 + 0.5D, "random.bowhit", 0.4F, 1.2F / (par1World.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void notifyNeighborOfChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);

        if (par5 == 3)
        {
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
        }
        else if (par5 == 1)
        {
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
        }
        else if (par5 == 0)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
        }
        else if (par5 == 2)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
    }

    private boolean func_72144_l(final World par1World, final int par2, final int par3, final int par4)
    {
        if (!this.canPlaceBlockAt(par1World, par2, par3, par4))
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 3;
        final float f = 0.1875F;

        if (l == 3)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        }
        else if (l == 1)
        {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        }
        else if (l == 0)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        }
        else if (l == 2)
        {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        final boolean flag = (par6 & 4) == 4;
        final boolean flag1 = (par6 & 8) == 8;

        if (flag || flag1)
        {
            this.func_72143_a(par1World, par2, par3, par4, 0, par6, false, -1, 0);
        }

        if (flag1)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            final int j1 = par6 & 3;

            if (j1 == 3)
            {
                par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            }
            else if (j1 == 1)
            {
                par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            }
            else if (j1 == 0)
            {
                par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            }
            else if (j1 == 2)
            {
                par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
            }
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) == 8 ? 15 : 0;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

        if ((i1 & 8) != 8)
        {
            return 0;
        }
        else
        {
            final int j1 = i1 & 3;
            return j1 == 2 && par5 == 2 ? 15 : (j1 == 0 && par5 == 3 ? 15 : (j1 == 1 && par5 == 4 ? 15 : (j1 == 3 && par5 == 5 ? 15 : 0)));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }
}
