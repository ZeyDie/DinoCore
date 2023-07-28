package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

import java.util.Random;

public class BlockCactus extends Block implements IPlantable
{
    @SideOnly(Side.CLIENT)
    private Icon cactusTopIcon;
    @SideOnly(Side.CLIENT)
    private Icon cactusBottomIcon;

    protected BlockCactus(final int par1)
    {
        super(par1, Material.cactus);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (par1World.isAirBlock(par2, par3 + 1, par4))
        {
            int l;

            for (l = 1; par1World.getBlockId(par2, par3 - l, par4) == this.blockID; ++l)
            {
                ;
            }

            if (l < 3)
            {
                final int i1 = par1World.getBlockMetadata(par2, par3, par4);

                if (i1 >= (byte) range(3, (par1World.growthOdds / par1World.spigotConfig.cactusModifier * 15) + 0.5F, 15)) // Spigot
                {
                    org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.handleBlockGrowEvent(par1World, par2, par3 + 1, par4, this.blockID, 0); // CraftBukkit
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 4);
                    this.onNeighborBlockChange(par1World, par2, par3 + 1, par4, this.blockID);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 + 1, 4);
                }
            }
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        final float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + f), (double)par3, (double)((float)par4 + f), (double)((float)(par2 + 1) - f), (double)((float)(par3 + 1) - f), (double)((float)(par4 + 1) - f));
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
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(final World par1World, final int par2, final int par3, final int par4)
    {
        final float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + f), (double)par3, (double)((float)par4 + f), (double)((float)(par2 + 1) - f), (double)(par3 + 1), (double)((float)(par4 + 1) - f));
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par1 == 1 ? this.cactusTopIcon : (par1 == 0 ? this.cactusBottomIcon : this.blockIcon);
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
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 13;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        return !super.canPlaceBlockAt(par1World, par2, par3, par4) ? false : this.canBlockStay(par1World, par2, par3, par4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!this.canBlockStay(par1World, par2, par3, par4))
        {
            par1World.destroyBlock(par2, par3, par4, true);
        }
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(final World par1World, final int par2, final int par3, final int par4)
    {
        if (par1World.getBlockMaterial(par2 - 1, par3, par4).isSolid())
        {
            return false;
        }
        else if (par1World.getBlockMaterial(par2 + 1, par3, par4).isSolid())
        {
            return false;
        }
        else if (par1World.getBlockMaterial(par2, par3, par4 - 1).isSolid())
        {
            return false;
        }
        else if (par1World.getBlockMaterial(par2, par3, par4 + 1).isSolid())
        {
            return false;
        }
        else
        {
            final int l = par1World.getBlockId(par2, par3 - 1, par4);
            return blocksList[l] != null && blocksList[l].canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this);
        }
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(final World par1World, final int par2, final int par3, final int par4, final Entity par5Entity)
    {
        // CraftBukkit start - EntityDamageByBlock event
        if (par5Entity instanceof EntityLivingBase)
        {
            final org.bukkit.block.Block damager = par1World.getWorld().getBlockAt(par2, par3, par4);
            final org.bukkit.entity.Entity damagee = (par5Entity == null) ? null : par5Entity.getBukkitEntity();
            final EntityDamageByBlockEvent event = new EntityDamageByBlockEvent(damager, damagee, org.bukkit.event.entity.EntityDamageEvent.DamageCause.CONTACT, 1.0D);
            par1World.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
            {
                damagee.setLastDamageCause(event);
                par5Entity.attackEntityFrom(DamageSource.cactus, (float) event.getDamage());
            }

            return;
        }

        // CraftBukkit end
        par5Entity.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
        this.cactusTopIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.cactusBottomIcon = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
    }

    @Override
    public EnumPlantType getPlantType(final World world, final int x, final int y, final int z)
    {
        return EnumPlantType.Desert;
    }

    @Override
    public int getPlantID(final World world, final int x, final int y, final int z)
    {
        return blockID;
    }

    @Override
    public int getPlantMetadata(final World world, final int x, final int y, final int z)
    {
        return -1;
    }
}
