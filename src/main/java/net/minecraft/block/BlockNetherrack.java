package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockNetherrack extends Block
{
    public BlockNetherrack(final int par1)
    {
        super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    // CraftBukkit start

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World world, final int i, final int j, final int k, final int l)
    {
        if (Block.blocksList[l] != null && Block.blocksList[l].canProvidePower())
        {
            final org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
            final int power = block.getBlockPower();
            final BlockRedstoneEvent event = new BlockRedstoneEvent(block, power, power);
            world.getServer().getPluginManager().callEvent(event);
        }
    }
    // CraftBukkit end
}
