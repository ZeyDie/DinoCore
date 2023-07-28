package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Iterator;
import java.util.Random;

public class BlockBed extends BlockDirectional
{
    /** Maps the foot-of-bed block to the head-of-bed block. */
    public static final int[][] footBlockToHeadBlockMap = {{0, 1}, { -1, 0}, {0, -1}, {1, 0}};
    @SideOnly(Side.CLIENT)
    private Icon[] field_94472_b;
    @SideOnly(Side.CLIENT)
    private Icon[] bedSideIcons;
    @SideOnly(Side.CLIENT)
    private Icon[] bedTopIcons;

    public BlockBed(final int par1)
    {
        super(par1, Material.cloth);
        this.setBounds();
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, int par2, final int par3, int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        int par21 = par2;
        int par41 = par4;
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            int i1 = par1World.getBlockMetadata(par21, par3, par41);

            if (!isBlockHeadOfBed(i1))
            {
                final int j1 = getDirection(i1);
                par21 += footBlockToHeadBlockMap[j1][0];
                par41 += footBlockToHeadBlockMap[j1][1];

                if (par1World.getBlockId(par21, par3, par41) != this.blockID)
                {
                    return true;
                }

                i1 = par1World.getBlockMetadata(par21, par3, par41);
            }

            if (par1World.provider.canRespawnHere() && par1World.getBiomeGenForCoords(par21, par41) != BiomeGenBase.hell)
            {
                if (isBedOccupied(i1))
                {
                    EntityPlayer entityplayer1 = null;
                    final Iterator iterator = par1World.playerEntities.iterator();

                    while (iterator.hasNext())
                    {
                        final EntityPlayer entityplayer2 = (EntityPlayer)iterator.next();

                        if (entityplayer2.isPlayerSleeping())
                        {
                            final ChunkCoordinates chunkcoordinates = entityplayer2.playerLocation;

                            if (chunkcoordinates.posX == par21 && chunkcoordinates.posY == par3 && chunkcoordinates.posZ == par41)
                            {
                                entityplayer1 = entityplayer2;
                            }
                        }
                    }

                    if (entityplayer1 != null)
                    {
                        par5EntityPlayer.addChatMessage("tile.bed.occupied");
                        return true;
                    }

                    setBedOccupied(par1World, par21, par3, par41, false);
                }

                final EnumStatus enumstatus = par5EntityPlayer.sleepInBedAt(par21, par3, par41);

                if (enumstatus == EnumStatus.OK)
                {
                    setBedOccupied(par1World, par21, par3, par41, true);
                    return true;
                }
                else
                {
                    if (enumstatus == EnumStatus.NOT_POSSIBLE_NOW)
                    {
                        par5EntityPlayer.addChatMessage("tile.bed.noSleep");
                    }
                    else if (enumstatus == EnumStatus.NOT_SAFE)
                    {
                        par5EntityPlayer.addChatMessage("tile.bed.notSafe");
                    }

                    return true;
                }
            }
            else
            {
                double d0 = (double) par21 + 0.5D;
                double d1 = (double)par3 + 0.5D;
                double d2 = (double) par41 + 0.5D;
                par1World.setBlockToAir(par21, par3, par41);
                final int k1 = getDirection(i1);
                par21 += footBlockToHeadBlockMap[k1][0];
                par41 += footBlockToHeadBlockMap[k1][1];

                if (par1World.getBlockId(par21, par3, par41) == this.blockID)
                {
                    par1World.setBlockToAir(par21, par3, par41);
                    d0 = (d0 + (double) par21 + 0.5D) / 2.0D;
                    d1 = (d1 + (double)par3 + 0.5D) / 2.0D;
                    d2 = (d2 + (double) par41 + 0.5D) / 2.0D;
                }

                par1World.newExplosion((Entity)null, (double)((float) par21 + 0.5F), (double)((float)par3 + 0.5F), (double)((float) par41 + 0.5F), 5.0F, true, true);
                return true;
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        if (par1 == 0)
        {
            return Block.planks.getBlockTextureFromSide(par1);
        }
        else
        {
            final int k = getDirection(par2);
            final int l = Direction.bedDirection[k][par1];
            final int i1 = isBlockHeadOfBed(par2) ? 1 : 0;
            return (i1 != 1 || l != 2) && (i1 != 0 || l != 3) ? (l != 5 && l != 4 ? this.bedTopIcons[i1] : this.bedSideIcons[i1]) : this.field_94472_b[i1];
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.bedTopIcons = new Icon[] {par1IconRegister.registerIcon(this.getTextureName() + "_feet_top"), par1IconRegister.registerIcon(this.getTextureName() + "_head_top")};
        this.field_94472_b = new Icon[] {par1IconRegister.registerIcon(this.getTextureName() + "_feet_end"), par1IconRegister.registerIcon(this.getTextureName() + "_head_end")};
        this.bedSideIcons = new Icon[] {par1IconRegister.registerIcon(this.getTextureName() + "_feet_side"), par1IconRegister.registerIcon(this.getTextureName() + "_head_side")};
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 14;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
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
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        this.setBounds();
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        final int i1 = par1World.getBlockMetadata(par2, par3, par4);
        final int j1 = getDirection(i1);

        if (isBlockHeadOfBed(i1))
        {
            if (par1World.getBlockId(par2 - footBlockToHeadBlockMap[j1][0], par3, par4 - footBlockToHeadBlockMap[j1][1]) != this.blockID)
            {
                par1World.setBlockToAir(par2, par3, par4);
            }
        }
        else if (par1World.getBlockId(par2 + footBlockToHeadBlockMap[j1][0], par3, par4 + footBlockToHeadBlockMap[j1][1]) != this.blockID)
        {
            par1World.setBlockToAir(par2, par3, par4);

            if (!par1World.isRemote)
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, i1, 0);
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return isBlockHeadOfBed(par1) ? 0 : Item.bed.itemID;
    }

    /**
     * Set the bounds of the bed block.
     */
    private void setBounds()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    /**
     * Returns whether or not this bed block is the head of the bed.
     */
    public static boolean isBlockHeadOfBed(final int par0)
    {
        return (par0 & 8) != 0;
    }

    /**
     * Return whether or not the bed is occupied.
     */
    public static boolean isBedOccupied(final int par0)
    {
        return (par0 & 4) != 0;
    }

    /**
     * Sets whether or not the bed is occupied.
     */
    public static void setBedOccupied(final World par0World, final int par1, final int par2, final int par3, final boolean par4)
    {
        int l = par0World.getBlockMetadata(par1, par2, par3);

        if (par4)
        {
            l |= 4;
        }
        else
        {
            l &= -5;
        }

        par0World.setBlockMetadataWithNotify(par1, par2, par3, l, 4);
    }

    /**
     * Gets the nearest empty chunk coordinates for the player to wake up from a bed into.
     */
    public static ChunkCoordinates getNearestEmptyChunkCoordinates(final World par0World, final int par1, final int par2, final int par3, int par4)
    {
        int par41 = par4;
        final int i1 = par0World.getBlockMetadata(par1, par2, par3);
        final int j1 = BlockDirectional.getDirection(i1);

        for (int k1 = 0; k1 <= 1; ++k1)
        {
            final int l1 = par1 - footBlockToHeadBlockMap[j1][0] * k1 - 1;
            final int i2 = par3 - footBlockToHeadBlockMap[j1][1] * k1 - 1;
            final int j2 = l1 + 2;
            final int k2 = i2 + 2;

            for (int l2 = l1; l2 <= j2; ++l2)
            {
                for (int i3 = i2; i3 <= k2; ++i3)
                {
                    if (par0World.doesBlockHaveSolidTopSurface(l2, par2 - 1, i3) && !par0World.getBlockMaterial(l2, par2, i3).isOpaque() && !par0World.getBlockMaterial(l2, par2 + 1, i3).isOpaque())
                    {
                        if (par41 <= 0)
                        {
                            return new ChunkCoordinates(l2, par2, i3);
                        }

                        --par41;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final int par7)
    {
        if (!isBlockHeadOfBed(par5))
        {
            super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, 0);
        }
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Item.bed.itemID;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(final World par1World, int par2, final int par3, int par4, final int par5, final EntityPlayer par6EntityPlayer)
    {
        int par21 = par2;
        int par41 = par4;
        if (par6EntityPlayer.capabilities.isCreativeMode && isBlockHeadOfBed(par5))
        {
            final int i1 = getDirection(par5);
            par21 -= footBlockToHeadBlockMap[i1][0];
            par41 -= footBlockToHeadBlockMap[i1][1];

            if (par1World.getBlockId(par21, par3, par41) == this.blockID)
            {
                par1World.setBlockToAir(par21, par3, par41);
            }
        }
    }
}
