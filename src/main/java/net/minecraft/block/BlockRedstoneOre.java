package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.Random;

public class BlockRedstoneOre extends Block
{
    private boolean glowing;

    public BlockRedstoneOre(final int par1, final boolean par2)
    {
        super(par1, Material.rock);

        if (par2)
        {
            this.setTickRandomly(true);
        }

        this.glowing = par2;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World par1World)
    {
        return 30;
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer)
    {
        this.glow(par1World, par2, par3, par4);
        super.onBlockClicked(par1World, par2, par3, par4, par5EntityPlayer);
    }

    /**
     * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
     */
    public void onEntityWalking(final World par1World, final int par2, final int par3, final int par4, final Entity par5Entity)
    {
        // CraftBukkit start
        if (par5Entity instanceof EntityPlayer)
        {
            final org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callPlayerInteractEvent((EntityPlayer) par5Entity, org.bukkit.event.block.Action.PHYSICAL, par2, par3, par4, -1, null);

            if (!event.isCancelled())
            {
                this.glow(par1World, par2, par3, par4);
                super.onEntityWalking(par1World, par2, par3, par4, par5Entity);
            }
        }
        else
        {
            final EntityInteractEvent event = new EntityInteractEvent(par5Entity.getBukkitEntity(), par1World.getWorld().getBlockAt(par2, par3, par4));
            par1World.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
            {
                this.glow(par1World, par2, par3, par4);
                super.onEntityWalking(par1World, par2, par3, par4, par5Entity);
            }
        }
        // CraftBukkit end
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer, final int par6, final float par7, final float par8, final float par9)
    {
        this.glow(par1World, par2, par3, par4);
        return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
    }

    /**
     * The redstone ore glows.
     */
    private void glow(final World par1World, final int par2, final int par3, final int par4)
    {
        this.sparkle(par1World, par2, par3, par4);

        if (this.blockID == Block.oreRedstone.blockID)
        {
            par1World.setBlock(par2, par3, par4, Block.oreRedstoneGlowing.blockID);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (this.blockID == Block.oreRedstoneGlowing.blockID)
        {
            par1World.setBlock(par2, par3, par4, Block.oreRedstone.blockID);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Item.redstone.itemID;
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(final int par1, final Random par2Random)
    {
        return this.quantityDropped(par2Random) + par2Random.nextInt(par1 + 1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(final Random par1Random)
    {
        return 4 + par1Random.nextInt(2);
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
        // CraftBukkit start - Delegated to getExpDrop
    }

    public int getExpDrop(final World world, final int l, final int i1)
    {
        if (this.idDropped(l, world.rand, i1) != this.blockID)
        {
            final int j1 = 1 + world.rand.nextInt(5);
            return j1;
        }

        return 0;
        // CraftBukkit end
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (this.glowing)
        {
            this.sparkle(par1World, par2, par3, par4);
        }
    }

    /**
     * The redstone ore sparkles.
     */
    private void sparkle(final World par1World, final int par2, final int par3, final int par4)
    {
        final Random random = par1World.rand;
        final double d0 = 0.0625D;

        for (int l = 0; l < 6; ++l)
        {
            double d1 = (double)((float)par2 + random.nextFloat());
            double d2 = (double)((float)par3 + random.nextFloat());
            double d3 = (double)((float)par4 + random.nextFloat());

            if (l == 0 && !par1World.isBlockOpaqueCube(par2, par3 + 1, par4))
            {
                d2 = (double)(par3 + 1) + d0;
            }

            if (l == 1 && !par1World.isBlockOpaqueCube(par2, par3 - 1, par4))
            {
                d2 = (double)(par3 + 0) - d0;
            }

            if (l == 2 && !par1World.isBlockOpaqueCube(par2, par3, par4 + 1))
            {
                d3 = (double)(par4 + 1) + d0;
            }

            if (l == 3 && !par1World.isBlockOpaqueCube(par2, par3, par4 - 1))
            {
                d3 = (double)(par4 + 0) - d0;
            }

            if (l == 4 && !par1World.isBlockOpaqueCube(par2 + 1, par3, par4))
            {
                d1 = (double)(par2 + 1) + d0;
            }

            if (l == 5 && !par1World.isBlockOpaqueCube(par2 - 1, par3, par4))
            {
                d1 = (double)(par2 + 0) - d0;
            }

            if (d1 < (double)par2 || d1 > (double)(par2 + 1) || d2 < 0.0D || d2 > (double)(par3 + 1) || d3 < (double)par4 || d3 > (double)(par4 + 1))
            {
                par1World.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(final int par1)
    {
        return new ItemStack(Block.oreRedstone);
    }
}
