package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.common.ForgeDirection.*;

// CraftBukkit start
// CraftBukkit end

public abstract class BlockButton extends Block
{
    /** Whether this button is sensible to arrows, used by wooden buttons. */
    protected boolean sensible;

    protected BlockButton(final int par1, final boolean par2)
    {
        super(par1, Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.sensible = par2;
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
     * How many world ticks before ticking
     */
    public int tickRate(final World par1World)
    {
        return this.sensible ? 30 : 20;
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
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final ForgeDirection dir = ForgeDirection.getOrientation(par5);
        return (dir == NORTH && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH)) ||
               (dir == SOUTH && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH)) ||
               (dir == WEST  && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST)) ||
               (dir == EAST  && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST));
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        return (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST)) ||
               (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST)) ||
               (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH)) ||
               (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH));
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9)
    {
        int j1 = par1World.getBlockMetadata(par2, par3, par4);
        final int k1 = j1 & 8;
        j1 &= 7;


        final ForgeDirection dir = ForgeDirection.getOrientation(par5);

        if (dir == NORTH && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH))
        {
            j1 = 4;
        }
        else if (dir == SOUTH && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH))
        {
            j1 = 3;
        }
        else if (dir == WEST && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST))
        {
            j1 = 2;
        }
        else if (dir == EAST && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST))
        {
            j1 = 1;
        }
        else
        {
            j1 = this.getOrientation(par1World, par2, par3, par4);
        }

        return j1 + k1;
    }

    /**
     * Get side which this button is facing.
     */
    private int getOrientation(final World par1World, final int par2, final int par3, final int par4)
    {
        if (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST)) return 1;
        if (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST)) return 2;
        if (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH)) return 3;
        if (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH)) return 4;
        return 1;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (this.redundantCanPlaceBlockAt(par1World, par2, par3, par4))
        {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4) & 7;
            boolean flag = false;

            if (!par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST) && i1 == 1)
            {
                flag = true;
            }

            if (!par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST) && i1 == 2)
            {
                flag = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH) && i1 == 3)
            {
                flag = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH) && i1 == 4)
            {
                flag = true;
            }

            if (flag)
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                par1World.setBlockToAir(par2, par3, par4);
            }
        }
    }

    /**
     * This method is redundant, check it out...
     */
    private boolean redundantCanPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
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
        final int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        this.func_82534_e(l);
    }

    private void func_82534_e(final int par1)
    {
        final int j = par1 & 7;
        final boolean flag = (par1 & 8) > 0;
        final float f = 0.375F;
        final float f1 = 0.625F;
        final float f2 = 0.1875F;
        float f3 = 0.125F;

        if (flag)
        {
            f3 = 0.0625F;
        }

        if (j == 1)
        {
            this.setBlockBounds(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
        }
        else if (j == 2)
        {
            this.setBlockBounds(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
        }
        else if (j == 3)
        {
            this.setBlockBounds(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
        }
        else if (j == 4)
        {
            this.setBlockBounds(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
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
        final int i1 = par1World.getBlockMetadata(par2, par3, par4);
        final int j1 = i1 & 7;
        final int k1 = 8 - (i1 & 8);

        if (k1 == 0)
        {
            return true;
        }
        else
        {
            // CraftBukkit start
            final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);
            final int old = (k1 != 8) ? 15 : 0;
            final int current = (k1 == 8) ? 15 : 0;
            final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
            par1World.getServer().getPluginManager().callEvent(eventRedstone);

            if ((eventRedstone.getNewCurrent() > 0) != (k1 == 8))
            {
                return true;
            }
            // CraftBukkit end
            par1World.setBlockMetadataWithNotify(par2, par3, par4, j1 + k1, 3);
            par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, 0.6F);
            this.func_82536_d(par1World, par2, par3, par4, j1);
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
            return true;
        }
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        if ((par6 & 8) > 0)
        {
            final int j1 = par6 & 7;
            this.func_82536_d(par1World, par2, par3, par4, j1);
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
        return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 8) > 0 ? 15 : 0;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

        if ((i1 & 8) == 0)
        {
            return 0;
        }
        else
        {
            final int j1 = i1 & 7;
            return j1 == 5 && par5 == 1 ? 15 : (j1 == 4 && par5 == 2 ? 15 : (j1 == 3 && par5 == 3 ? 15 : (j1 == 2 && par5 == 4 ? 15 : (j1 == 1 && par5 == 5 ? 15 : 0))));
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (!par1World.isRemote)
        {
            final int l = par1World.getBlockMetadata(par2, par3, par4);

            if ((l & 8) != 0)
            {
                // CraftBukkit start
                final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);
                final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
                par1World.getServer().getPluginManager().callEvent(eventRedstone);

                if (eventRedstone.getNewCurrent() > 0)
                {
                    return;
                }
                // CraftBukkit end
                if (this.sensible)
                {
                    this.func_82535_o(par1World, par2, par3, par4);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, l & 7, 3);
                    final int i1 = l & 7;
                    this.func_82536_d(par1World, par2, par3, par4, i1);
                    par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, 0.5F);
                    par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
                }
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        final float f = 0.1875F;
        final float f1 = 0.125F;
        final float f2 = 0.125F;
        this.setBlockBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(final World par1World, final int par2, final int par3, final int par4, final Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            if (this.sensible)
            {
                if ((par1World.getBlockMetadata(par2, par3, par4) & 8) == 0)
                {
                    this.func_82535_o(par1World, par2, par3, par4);
                }
            }
        }
    }

    protected void func_82535_o(final World par1World, final int par2, final int par3, final int par4)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        final int i1 = l & 7;
        final boolean flag = (l & 8) != 0;
        this.func_82534_e(l);
        final List list = par1World.getEntitiesWithinAABB(EntityArrow.class, AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ));
        final boolean flag1 = !list.isEmpty();

        // CraftBukkit start - Call interact event when arrows turn on wooden buttons
        if (flag != flag1 && flag1)
        {
            final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);
            boolean allowed = false;

            // If all of the events are cancelled block the button press, else allow
            for (final Object object : list)
            {
                if (object != null)
                {
                    final EntityInteractEvent event = new EntityInteractEvent(((Entity) object).getBukkitEntity(), block);
                    par1World.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled())
                    {
                        allowed = true;
                        break;
                    }
                }
            }

            if (!allowed)
            {
                return;
            }
        }
        // CraftBukkit end
        if (flag1 && !flag)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 | 8, 3);
            this.func_82536_d(par1World, par2, par3, par4, i1);
            par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (!flag1 && flag)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1, 3);
            this.func_82536_d(par1World, par2, par3, par4, i1);
            par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (flag1)
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
        }
    }

    private void func_82536_d(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);

        if (par5 == 1)
        {
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
        }
        else if (par5 == 2)
        {
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
        }
        else if (par5 == 3)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
        }
        else if (par5 == 4)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
        else
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister) {}
}
