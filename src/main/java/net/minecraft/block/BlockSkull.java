package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.ArrayList;
import java.util.Random;

// CraftBukkit start
// CraftBukkit end

public class BlockSkull extends BlockContainer
{
    protected BlockSkull(final int par1)
    {
        super(par1, Material.circuits);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
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
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4)
    {
        final int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 7;

        switch (l)
        {
            case 1:
            default:
                this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case 2:
                this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case 3:
                this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case 4:
                this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 5:
                this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
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
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLivingBase, final ItemStack par6ItemStack)
    {
        final int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World par1World)
    {
        return new TileEntitySkull();
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Item.skull.itemID;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(final World par1World, final int par2, final int par3, final int par4)
    {
        final TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
        return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull)tileentity).getSkullType() : super.getDamageValue(par1World, par2, par3, par4);
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(final int par1)
    {
        return par1;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(final World par1World, final int par2, final int par3, final int par4, int par5, final EntityPlayer par6EntityPlayer)
    {
        int par51 = par5;
        if (par6EntityPlayer.capabilities.isCreativeMode)
        {
            par51 |= 8;
            par1World.setBlockMetadataWithNotify(par2, par3, par4, par51, 4);
        }

        dropBlockAsItem(par1World, par2, par3, par4, par51, 0);

        super.onBlockHarvested(par1World, par2, par3, par4, par51, par6EntityPlayer);
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(final World par1World, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(final World world, final int x, final int y, final int z, final int metadata, final int fortune)
    {
        final ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        if ((metadata & 8) == 0)
        {
            final ItemStack itemstack = new ItemStack(Item.skull.itemID, 1, this.getDamageValue(world, x, y, z));
            final TileEntitySkull tileentityskull = (TileEntitySkull)world.getBlockTileEntity(x, y, z);

            if (tileentityskull == null)
            {
                return drops;
            }
            if (tileentityskull.getSkullType() == 3 && tileentityskull.getExtraType() != null && !tileentityskull.getExtraType().isEmpty())
            {
                itemstack.setTagCompound(new NBTTagCompound());
                itemstack.getTagCompound().setString("SkullOwner", tileentityskull.getExtraType());
            }
            drops.add(itemstack);
        }
        return drops;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Item.skull.itemID;
    }

    /**
     * This method attempts to create a wither at the given location and skull
     */
    public void makeWither(final World par1World, final int par2, final int par3, final int par4, final TileEntitySkull par5TileEntitySkull)
    {
        if (par5TileEntitySkull.getSkullType() == 1 && par3 >= 2 && par1World.difficultySetting > 0 && !par1World.isRemote)
        {
            final int l = Block.slowSand.blockID;
            int i1;
            final EntityWither entitywither;
            int j1;

            for (i1 = -2; i1 <= 0; ++i1)
            {
                if (par1World.getBlockId(par2, par3 - 1, par4 + i1) == l && par1World.getBlockId(par2, par3 - 1, par4 + i1 + 1) == l && par1World.getBlockId(par2, par3 - 2, par4 + i1 + 1) == l && par1World.getBlockId(par2, par3 - 1, par4 + i1 + 2) == l && this.func_82528_d(par1World, par2, par3, par4 + i1, 1) && this.func_82528_d(par1World, par2, par3, par4 + i1 + 1, 1) && this.func_82528_d(par1World, par2, par3, par4 + i1 + 2, 1))
                {
                    // CraftBukkit start - Use BlockStateListPopulator
                    final BlockStateListPopulator blockList = new BlockStateListPopulator(par1World.getWorld());
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 + i1, 8, 2);
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 + i1 + 1, 8, 2);
                    par1World.setBlockMetadataWithNotify(par2, par3, par4 + i1 + 2, 8, 2);
                    blockList.setTypeId(par2, par3, par4 + i1, 0);
                    blockList.setTypeId(par2, par3, par4 + i1 + 1, 0);
                    blockList.setTypeId(par2, par3, par4 + i1 + 2, 0);
                    blockList.setTypeId(par2, par3 - 1, par4 + i1, 0);
                    blockList.setTypeId(par2, par3 - 1, par4 + i1 + 1, 0);
                    blockList.setTypeId(par2, par3 - 1, par4 + i1 + 2, 0);
                    blockList.setTypeId(par2, par3 - 2, par4 + i1 + 1, 0);

                    if (!par1World.isRemote)
                    {
                        entitywither = new EntityWither(par1World);
                        entitywither.setLocationAndAngles((double)par2 + 0.5D, (double)par3 - 1.45D, (double)(par4 + i1) + 1.5D, 90.0F, 0.0F);
                        entitywither.renderYawOffset = 90.0F;
                        entitywither.func_82206_m();

                        if (par1World.addEntity(entitywither, SpawnReason.BUILD_WITHER))
                        {
                            blockList.updateList();
                        }
                    }

                    for (j1 = 0; j1 < 120; ++j1)
                    {
                        par1World.spawnParticle("snowballpoof", (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9D, (double)(par4 + i1 + 1) + par1World.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    // CraftBukkit end
                    return;
                }
            }

            for (i1 = -2; i1 <= 0; ++i1)
            {
                if (par1World.getBlockId(par2 + i1, par3 - 1, par4) == l && par1World.getBlockId(par2 + i1 + 1, par3 - 1, par4) == l && par1World.getBlockId(par2 + i1 + 1, par3 - 2, par4) == l && par1World.getBlockId(par2 + i1 + 2, par3 - 1, par4) == l && this.func_82528_d(par1World, par2 + i1, par3, par4, 1) && this.func_82528_d(par1World, par2 + i1 + 1, par3, par4, 1) && this.func_82528_d(par1World, par2 + i1 + 2, par3, par4, 1))
                {
                    // CraftBukkit start - Use BlockStateListPopulator
                    final BlockStateListPopulator blockList = new BlockStateListPopulator(par1World.getWorld());
                    par1World.setBlockMetadataWithNotify(par2 + i1, par3, par4, 8, 2);
                    par1World.setBlockMetadataWithNotify(par2 + i1 + 1, par3, par4, 8, 2);
                    par1World.setBlockMetadataWithNotify(par2 + i1 + 2, par3, par4, 8, 2);
                    blockList.setTypeId(par2 + i1, par3, par4, 0);
                    blockList.setTypeId(par2 + i1 + 1, par3, par4, 0);
                    blockList.setTypeId(par2 + i1 + 2, par3, par4, 0);
                    blockList.setTypeId(par2 + i1, par3 - 1, par4, 0);
                    blockList.setTypeId(par2 + i1 + 1, par3 - 1, par4, 0);
                    blockList.setTypeId(par2 + i1 + 2, par3 - 1, par4, 0);
                    blockList.setTypeId(par2 + i1 + 1, par3 - 2, par4, 0);

                    if (!par1World.isRemote)
                    {
                        entitywither = new EntityWither(par1World);
                        entitywither.setLocationAndAngles((double)(par2 + i1) + 1.5D, (double)par3 - 1.45D, (double)par4 + 0.5D, 0.0F, 0.0F);
                        entitywither.func_82206_m();

                        if (par1World.addEntity(entitywither, SpawnReason.BUILD_WITHER))
                        {
                            blockList.updateList();
                        }
                    }

                    for (j1 = 0; j1 < 120; ++j1)
                    {
                        par1World.spawnParticle("snowballpoof", (double)(par2 + i1 + 1) + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9D, (double)par4 + par1World.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }
                    // CraftBukkit end
                    return;
                }
            }
        }
    }

    private boolean func_82528_d(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (par1World.getBlockId(par2, par3, par4) != this.blockID)
        {
            return false;
        }
        else
        {
            final TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
            return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull)tileentity).getSkullType() == par5 : false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister) {}

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return Block.slowSand.getBlockTextureFromSide(par1);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers.
     */
    public String getItemIconName()
    {
        return this.getTextureName() + "_" + ItemSkull.field_94587_a[0];
    }
}
