package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Random;

// CraftBukkit start
// CraftBukkit end

public class BlockMycelium extends Block
{
    @SideOnly(Side.CLIENT)
    private Icon field_94422_a;
    @SideOnly(Side.CLIENT)
    private Icon field_94421_b;

    protected BlockMycelium(final int par1)
    {
        super(par1, Material.grass);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par1 == 1 ? this.field_94422_a : (par1 == 0 ? Block.dirt.getBlockTextureFromSide(par1) : this.blockIcon);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (!par1World.isRemote)
        {
            if (par1World.getBlockLightValue(par2, par3 + 1, par4) < 4 && par1World.getBlockLightOpacity(par2, par3 + 1, par4) > 2)
            {
                // CraftBukkit start
                final org.bukkit.World bworld = par1World.getWorld();
                final BlockState blockState = bworld.getBlockAt(par2, par3, par4).getState();
                blockState.setTypeId(Block.dirt.blockID);
                final BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
                par1World.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled())
                {
                    blockState.update(true);
                }

                // CraftBukkit end
            }
            else if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9)
            {
                final int numGrowth = Math.min(4, Math.max(20, (int)(4 * 100.0F / par1World.growthOdds)));  // Spigot

                for (int l = 0; l < numGrowth; ++l)   // Spigot
                {
                    final int i1 = par2 + par5Random.nextInt(3) - 1;
                    final int j1 = par3 + par5Random.nextInt(5) - 3;
                    final int k1 = par4 + par5Random.nextInt(3) - 1;
                    final int l1 = par1World.getBlockId(i1, j1 + 1, k1);

                    if (par1World.getBlockId(i1, j1, k1) == Block.dirt.blockID && par1World.getBlockLightValue(i1, j1 + 1, k1) >= 4 && par1World.getBlockLightOpacity(i1, j1 + 1, k1) <= 2)
                    {
                        // CraftBukkit start
                        final org.bukkit.World bworld = par1World.getWorld();
                        final BlockState blockState = bworld.getBlockAt(i1, j1, k1).getState();
                        blockState.setTypeId(this.blockID);
                        final BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(par2, par3, par4), blockState);
                        par1World.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled())
                        {
                            blockState.update(true);
                        }

                        // CraftBukkit end
                    }
                }
            }
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
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5)
    {
        if (par5 == 1)
        {
            return this.field_94422_a;
        }
        else if (par5 == 0)
        {
            return Block.dirt.getBlockTextureFromSide(par5);
        }
        else
        {
            final Material material = par1IBlockAccess.getBlockMaterial(par2, par3 + 1, par4);
            return material != Material.snow && material != Material.craftedSnow ? this.blockIcon : this.field_94421_b;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
        this.field_94422_a = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.field_94421_b = par1IconRegister.registerIcon("grass_side_snowed");
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        super.randomDisplayTick(par1World, par2, par3, par4, par5Random);

        if (par5Random.nextInt(10) == 0)
        {
            par1World.spawnParticle("townaura", (double)((float)par2 + par5Random.nextFloat()), (double)((float)par3 + 1.1F), (double)((float)par4 + par5Random.nextFloat()), 0.0D, 0.0D, 0.0D);
        }
    }
}
