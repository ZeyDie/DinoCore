package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockComparator extends BlockRedstoneLogic implements ITileEntityProvider
{
    public BlockComparator(final int par1, final boolean par2)
    {
        super(par1, par2);
        this.isBlockContainer = true;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Item.comparator.itemID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Item.comparator.itemID;
    }

    protected int func_94481_j_(final int par1)
    {
        return 2;
    }

    protected BlockRedstoneLogic func_94485_e()
    {
        return Block.redstoneComparatorActive;
    }

    protected BlockRedstoneLogic func_94484_i()
    {
        return Block.redstoneComparatorIdle;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 37;
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        final boolean flag = this.isRepeaterPowered || (par2 & 8) != 0;
        return par1 == 0 ? (flag ? Block.torchRedstoneActive.getBlockTextureFromSide(par1) : Block.torchRedstoneIdle.getBlockTextureFromSide(par1)) : (par1 == 1 ? (flag ? Block.redstoneComparatorActive.blockIcon : this.blockIcon) : Block.stoneDoubleSlab.getBlockTextureFromSide(1));
    }

    protected boolean func_96470_c(final int par1)
    {
        return this.isRepeaterPowered || (par1 & 8) != 0;
    }

    protected int func_94480_d(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        return this.getTileEntityComparator(par1IBlockAccess, par2, par3, par4).getOutputSignal();
    }

    private int getOutputStrength(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        return !this.func_94490_c(par5) ? this.getInputStrength(par1World, par2, par3, par4, par5) : Math.max(this.getInputStrength(par1World, par2, par3, par4, par5) - this.func_94482_f(par1World, par2, par3, par4, par5), 0);
    }

    public boolean func_94490_c(final int par1)
    {
        return (par1 & 4) == 4;
    }

    protected boolean isGettingInput(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = this.getInputStrength(par1World, par2, par3, par4, par5);

        if (i1 >= 15)
        {
            return true;
        }
        else if (i1 == 0)
        {
            return false;
        }
        else
        {
            final int j1 = this.func_94482_f(par1World, par2, par3, par4, par5);
            return j1 == 0 ? true : i1 >= j1;
        }
    }

    /**
     * Returns the signal strength at one input of the block. Args: world, X, Y, Z, side
     */
    protected int getInputStrength(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        int i1 = super.getInputStrength(par1World, par2, par3, par4, par5);
        final int j1 = getDirection(par5);
        int k1 = par2 + Direction.offsetX[j1];
        int l1 = par4 + Direction.offsetZ[j1];
        int i2 = par1World.getBlockId(k1, par3, l1);

        if (i2 > 0)
        {
            if (Block.blocksList[i2].hasComparatorInputOverride())
            {
                i1 = Block.blocksList[i2].getComparatorInputOverride(par1World, k1, par3, l1, Direction.rotateOpposite[j1]);
            }
            else if (i1 < 15 && Block.isNormalCube(i2))
            {
                k1 += Direction.offsetX[j1];
                l1 += Direction.offsetZ[j1];
                i2 = par1World.getBlockId(k1, par3, l1);

                if (i2 > 0 && Block.blocksList[i2].hasComparatorInputOverride())
                {
                    i1 = Block.blocksList[i2].getComparatorInputOverride(par1World, k1, par3, l1, Direction.rotateOpposite[j1]);
                }
            }
        }

        return i1;
    }

    /**
     * Returns the blockTileEntity at given coordinates.
     */
    public TileEntityComparator getTileEntityComparator(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        return (TileEntityComparator)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        final int i1 = par1World.getBlockMetadata(par2, par3, par4);
        final boolean flag = this.isRepeaterPowered | (i1 & 8) != 0;
        final boolean flag1 = !this.func_94490_c(i1);
        int j1 = flag1 ? 4 : 0;
        j1 |= flag ? 8 : 0;
        par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, flag1 ? 0.55F : 0.5F);
        par1World.setBlockMetadataWithNotify(par2, par3, par4, j1 | i1 & 3, 2);
        this.func_96476_c(par1World, par2, par3, par4, par1World.rand);
        return true;
    }

    protected void func_94479_f(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!par1World.isBlockTickScheduledThisTick(par2, par3, par4, this.blockID))
        {
            final int i1 = par1World.getBlockMetadata(par2, par3, par4);
            final int j1 = this.getOutputStrength(par1World, par2, par3, par4, i1);
            final int k1 = this.getTileEntityComparator(par1World, par2, par3, par4).getOutputSignal();

            if (j1 != k1 || this.func_96470_c(i1) != this.isGettingInput(par1World, par2, par3, par4, i1))
            {
                if (this.func_83011_d(par1World, par2, par3, par4, i1))
                {
                    par1World.scheduleBlockUpdateWithPriority(par2, par3, par4, this.blockID, this.func_94481_j_(0), -1);
                }
                else
                {
                    par1World.scheduleBlockUpdateWithPriority(par2, par3, par4, this.blockID, this.func_94481_j_(0), 0);
                }
            }
        }
    }

    private void func_96476_c(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        final int i1 = this.getOutputStrength(par1World, par2, par3, par4, l);
        final int j1 = this.getTileEntityComparator(par1World, par2, par3, par4).getOutputSignal();
        this.getTileEntityComparator(par1World, par2, par3, par4).setOutputSignal(i1);

        if (j1 != i1 || !this.func_94490_c(l))
        {
            final boolean flag = this.isGettingInput(par1World, par2, par3, par4, l);
            final boolean flag1 = this.isRepeaterPowered || (l & 8) != 0;

            if (flag1 && !flag)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, l & -9, 2);
            }
            else if (!flag1 && flag)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, l | 8, 2);
            }

            this.func_94483_i_(par1World, par2, par3, par4);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (this.isRepeaterPowered)
        {
            final int l = par1World.getBlockMetadata(par2, par3, par4);
            par1World.setBlock(par2, par3, par4, this.func_94484_i().blockID, l | 8, 4);
        }

        this.func_96476_c(par1World, par2, par3, par4, par5Random);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
        par1World.setBlockTileEntity(par2, par3, par4, this.createNewTileEntity(par1World));
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        par1World.removeBlockTileEntity(par2, par3, par4);
        this.func_94483_i_(par1World, par2, par3, par4);
    }

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
     * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
     */
    public boolean onBlockEventReceived(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        final TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        return new TileEntityComparator();
    }
    
    @Override
    public void onNeighborTileChange(final World world, final int x, final int y, final int z, final int tileX, final int tileY, final int tileZ)
    {
        if(y == tileY)
            onNeighborBlockChange(world, x, y, z, world.getBlockId(tileX, tileY, tileZ));
    }
    
    @Override
    public boolean weakTileChanges()
    {
        return true;
    }
}
