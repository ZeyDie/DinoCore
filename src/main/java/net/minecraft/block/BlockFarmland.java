package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.Random;

// CraftBukkit start
// CraftBukkit end

public class BlockFarmland extends Block
{
    @SideOnly(Side.CLIENT)
    private Icon field_94441_a;
    @SideOnly(Side.CLIENT)
    private Icon field_94440_b;

    protected BlockFarmland(final int par1)
    {
        super(par1, Material.ground);
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)(par2 + 0), (double)(par3 + 0), (double)(par4 + 0), (double)(par2 + 1), (double)(par3 + 1), (double)(par4 + 1));
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

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par1 == 1 ? (par2 > 0 ? this.field_94441_a : this.field_94440_b) : Block.dirt.getBlockTextureFromSide(par1);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (!this.isWaterNearby(par1World, par2, par3, par4) && !par1World.canLightningStrikeAt(par2, par3 + 1, par4))
        {
            final int l = par1World.getBlockMetadata(par2, par3, par4);

            if (l > 0)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, l - 1, 2);
            }
            else if (!this.isCropsNearby(par1World, par2, par3, par4))
            {
                // CraftBukkit start
                final org.bukkit.block.Block block = par1World.getWorld().getBlockAt(par2, par3, par4);

                if (CraftEventFactory.callBlockFadeEvent(block, Block.dirt.blockID).isCancelled())
                {
                    return;
                }

                // CraftBukkit end
                par1World.setBlock(par2, par3, par4, Block.dirt.blockID);
            }
        }
        else
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 7, 2);
        }
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    public void onFallenUpon(final World par1World, final int par2, final int par3, final int par4, final Entity par5Entity, final float par6)
    {
        if (!par1World.isRemote && par1World.rand.nextFloat() < par6 - 0.5F)
        {
            if (!(par5Entity instanceof EntityPlayer) && !par1World.getGameRules().getGameRuleBooleanValue("mobGriefing"))
            {
                return;
            }

            // Cauldron start - validate data before sending event
            // CraftBukkit start - Interact soil
            org.bukkit.event.Cancellable cancellable = null;

            if (par5Entity instanceof EntityPlayer)
            {
                cancellable = CraftEventFactory.callPlayerInteractEvent((EntityPlayer) par5Entity, org.bukkit.event.block.Action.PHYSICAL, par2, par3, par4, -1, null);
            }
            else if (par1World != null && par1World.getWorld() != null && par5Entity != null)
            {
                cancellable = new EntityInteractEvent(par5Entity.getBukkitEntity(), par1World.getWorld().getBlockAt(par2, par3, par4));
                par1World.getServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
            }

            if (cancellable != null && cancellable.isCancelled())
            {
                return;
            }
            // CraftBukkit end
            // Cauldron end
            par1World.setBlock(par2, par3, par4, Block.dirt.blockID);
        }
    }

    /**
     * returns true if there is at least one cropblock nearby (x-1 to x+1, y+1, z-1 to z+1)
     */
    private boolean isCropsNearby(final World par1World, final int par2, final int par3, final int par4)
    {
        final byte b0 = 0;

        for (int l = par2 - b0; l <= par2 + b0; ++l)
        {
            for (int i1 = par4 - b0; i1 <= par4 + b0; ++i1)
            {
                final int j1 = par1World.getBlockId(l, par3 + 1, i1);

                final Block plant = blocksList[j1];
                if (plant instanceof IPlantable && canSustainPlant(par1World, par2, par3, par4, ForgeDirection.UP, (IPlantable)plant))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * returns true if there's water nearby (x-4 to x+4, y to y+1, k-4 to k+4)
     */
    private boolean isWaterNearby(final World par1World, final int par2, final int par3, final int par4)
    {
        for (int l = par2 - 4; l <= par2 + 4; ++l)
        {
            for (int i1 = par3; i1 <= par3 + 1; ++i1)
            {
                for (int j1 = par4 - 4; j1 <= par4 + 4; ++j1)
                {
                    if (par1World.getBlockMaterial(l, i1, j1) == Material.water)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        final Material material = par1World.getBlockMaterial(par2, par3 + 1, par4);

        if (material.isSolid())
        {
            par1World.setBlock(par2, par3, par4, Block.dirt.blockID);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.dirt.idDropped(0, par2Random, par3);
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Block.dirt.blockID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.field_94441_a = par1IconRegister.registerIcon(this.getTextureName() + "_wet");
        this.field_94440_b = par1IconRegister.registerIcon(this.getTextureName() + "_dry");
    }
}
