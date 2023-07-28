package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;

import java.util.ArrayList;
import java.util.Random;

public class BlockVine extends Block implements IShearable
{
    public BlockVine(final int par1)
    {
        super(par1, Material.vine);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 20;
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
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final float f = 0.0625F;
        final int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = l > 0;

        if ((l & 2) != 0)
        {
            f4 = Math.max(f4, 0.0625F);
            f1 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((l & 8) != 0)
        {
            f1 = Math.min(f1, 0.9375F);
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((l & 4) != 0)
        {
            f6 = Math.max(f6, 0.0625F);
            f3 = 0.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if ((l & 1) != 0)
        {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (!flag && this.canBePlacedOn(par1IBlockAccess.getBlockId(par2, par3 + 1, par4)))
        {
            f2 = Math.min(f2, 0.9375F);
            f5 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
        }

        this.setBlockBounds(f1, f2, f3, f4, f5, f6);
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
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        switch (par5)
        {
            case 1:
                return this.canBePlacedOn(par1World.getBlockId(par2, par3 + 1, par4));
            case 2:
                return this.canBePlacedOn(par1World.getBlockId(par2, par3, par4 + 1));
            case 3:
                return this.canBePlacedOn(par1World.getBlockId(par2, par3, par4 - 1));
            case 4:
                return this.canBePlacedOn(par1World.getBlockId(par2 + 1, par3, par4));
            case 5:
                return this.canBePlacedOn(par1World.getBlockId(par2 - 1, par3, par4));
            default:
                return false;
        }
    }

    /**
     * returns true if a vine can be placed on that block (checks for render as normal block and if it is solid)
     */
    private boolean canBePlacedOn(final int par1)
    {
        if (par1 == 0)
        {
            return false;
        }
        else
        {
            final Block block = Block.blocksList[par1];
            return block.renderAsNormalBlock() && block.blockMaterial.blocksMovement();
        }
    }

    /**
     * Returns if the vine can stay in the world. It also changes the metadata according to neighboring blocks.
     */
    private boolean canVineStay(final World par1World, final int par2, final int par3, final int par4)
    {
        final int l = par1World.getBlockMetadata(par2, par3, par4);
        int i1 = l;

        if (l > 0)
        {
            for (int j1 = 0; j1 <= 3; ++j1)
            {
                final int k1 = 1 << j1;

                if ((l & k1) != 0 && !this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[j1], par3, par4 + Direction.offsetZ[j1])) && (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID || (par1World.getBlockMetadata(par2, par3 + 1, par4) & k1) == 0))
                {
                    i1 &= ~k1;
                }
            }
        }

        if (i1 == 0 && !this.canBePlacedOn(par1World.getBlockId(par2, par3 + 1, par4)))
        {
            return false;
        }
        else
        {
            if (i1 != l)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, i1, 2);
            }

            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    public int getRenderColor(final int par1)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        return par1IBlockAccess.getBiomeGenForCoords(par2, par4).getBiomeFoliageColor();
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!par1World.isRemote && !this.canVineStay(par1World, par2, par3, par4))
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (!par1World.isRemote && par1World.rand.nextInt(4) == 0)
        {
            final byte b0 = 4;
            int l = 5;
            boolean flag = false;
            int i1;
            int j1;
            int k1;
            label138:

            for (i1 = par2 - b0; i1 <= par2 + b0; ++i1)
            {
                for (j1 = par4 - b0; j1 <= par4 + b0; ++j1)
                {
                    for (k1 = par3 - 1; k1 <= par3 + 1; ++k1)
                    {
                        if (par1World.getBlockId(i1, k1, j1) == this.blockID)
                        {
                            --l;

                            if (l <= 0)
                            {
                                flag = true;
                                break label138;
                            }
                        }
                    }
                }
            }

            i1 = par1World.getBlockMetadata(par2, par3, par4);
            j1 = par1World.rand.nextInt(6);
            k1 = Direction.facingToDirection[j1];
            int l1;
            int i2;

            if (j1 == 1 && par3 < 255 && par1World.isAirBlock(par2, par3 + 1, par4))
            {
                if (flag)
                {
                    return;
                }

                l1 = par1World.rand.nextInt(16) & i1;

                if (l1 > 0)
                {
                    for (i2 = 0; i2 <= 3; ++i2)
                    {
                        if (!this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[i2], par3 + 1, par4 + Direction.offsetZ[i2])))
                        {
                            l1 &= ~(1 << i2);
                        }
                    }

                    if (l1 > 0)
                    {
                        // CraftBukkit start - Call BlockSpreadEvent
                        final org.bukkit.block.Block source = par1World.getWorld().getBlockAt(par2, par3, par4);
                        final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3 + 1, par4);
                        CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, l1);
                        // CraftBukkit end
                    }
                }
            }
            else
            {
                final int j2;

                if (j1 >= 2 && j1 <= 5 && (i1 & 1 << k1) == 0)
                {
                    if (flag)
                    {
                        return;
                    }

                    l1 = par1World.getBlockId(par2 + Direction.offsetX[k1], par3, par4 + Direction.offsetZ[k1]);

                    if (l1 != 0 && Block.blocksList[l1] != null)
                    {
                        if (Block.blocksList[l1].blockMaterial.isOpaque() && Block.blocksList[l1].renderAsNormalBlock())
                        {
                            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 | 1 << k1, 2);
                        }
                    }
                    else
                    {
                        i2 = k1 + 1 & 3;
                        j2 = k1 + 3 & 3;
                        // CraftBukkit start - Call BlockSpreadEvent
                        final org.bukkit.block.Block source = par1World.getWorld().getBlockAt(par2, par3, par4);
                        org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2 + Direction.offsetX[k1], par3, par4 + Direction.offsetZ[k1]);

                        if ((i1 & 1 << i2) != 0 && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[k1] + Direction.offsetX[i2], par3, par4 + Direction.offsetZ[k1] + Direction.offsetZ[i2])))
                        {
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, 1 << i2);
                        }
                        else if ((i1 & 1 << j2) != 0 && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[k1] + Direction.offsetX[j2], par3, par4 + Direction.offsetZ[k1] + Direction.offsetZ[j2])))
                        {
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, 1 << j2);
                        }
                        else if ((i1 & 1 << i2) != 0 && par1World.isAirBlock(par2 + Direction.offsetX[k1] + Direction.offsetX[i2], par3, par4 + Direction.offsetZ[k1] + Direction.offsetZ[i2]) && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[i2], par3, par4 + Direction.offsetZ[i2])))
                        {
                            block = par1World.getWorld().getBlockAt(par2 + Direction.offsetX[k1] + Direction.offsetX[i2], par3, par4 + Direction.offsetZ[k1] + Direction.offsetZ[i2]);
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, 1 << (k1 + 2 & 3));
                        }
                        else if ((i1 & 1 << j2) != 0 && par1World.isAirBlock(par2 + Direction.offsetX[k1] + Direction.offsetX[j2], par3, par4 + Direction.offsetZ[k1] + Direction.offsetZ[j2]) && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[j2], par3, par4 + Direction.offsetZ[j2])))
                        {
                            block = par1World.getWorld().getBlockAt(par2 + Direction.offsetX[k1] + Direction.offsetX[j2], par3, par4 + Direction.offsetZ[k1] + Direction.offsetZ[j2]);
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, 1 << (k1 + 2 & 3));
                        }
                        else if (this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[k1], par3 + 1, par4 + Direction.offsetZ[k1])))
                        {
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, 0);
                        }
                        // CraftBukkit end
                    }
                }
                else if (par3 > 1)
                {
                    l1 = par1World.getBlockId(par2, par3 - 1, par4);

                    if (l1 == 0)
                    {
                        i2 = par1World.rand.nextInt(16) & i1;

                        if (i2 > 0)
                        {
                            // CraftBukkit start - Call BlockSpreadEvent
                            final org.bukkit.block.Block source = par1World.getWorld().getBlockAt(par2, par3, par4);
                            final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3 - 1, par4);
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this.blockID, i2);
                            // CraftBukkit end
                        }
                    }
                    else if (l1 == this.blockID)
                    {
                        i2 = par1World.rand.nextInt(16) & i1;
                        j2 = par1World.getBlockMetadata(par2, par3 - 1, par4);

                        if (j2 != (j2 | i2))
                        {
                            par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, j2 | i2, 2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int par9)
    {
        byte b0 = 0;

        switch (par5)
        {
            case 2:
                b0 = 1;
                break;
            case 3:
                b0 = 4;
                break;
            case 4:
                b0 = 8;
                break;
            case 5:
                b0 = 2;
        }

        return b0 != 0 ? b0 : par9;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 0;
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(final World par1World, final EntityPlayer par2EntityPlayer, final int par3, final int par4, final int par5, final int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }

    @Override
    public boolean isShearable(final ItemStack item, final World world, final int x, final int y, final int z)
    {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(final ItemStack item, final World world, final int x, final int y, final int z, final int fortune)
    {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1, 0));
        return ret;
    }

    @Override
    public boolean isLadder(final World world, final int x, final int y, final int z, final EntityLivingBase entity)
    {
        return true;
    }
}
