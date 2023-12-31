package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;

import java.util.Random;

public class BlockRedstoneLight extends Block
{
    /** Whether this lamp block is the powered version. */
    private final boolean powered;

    public BlockRedstoneLight(final int par1, final boolean par2)
    {
        super(par1, Material.redstoneLight);
        this.powered = par2;

        if (par2)
        {
            this.setLightValue(1.0F);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4)
    {
        if (!par1World.isRemote)
        {
            if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 4);
            }
            else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(par1World, par2, par3, par4, 0, 15).getNewCurrent() != 15)
                {
                    return;
                }
                // CraftBukkit end
                par1World.setBlock(par2, par3, par4, Block.redstoneLampActive.blockID, 0, 2);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        if (!par1World.isRemote)
        {
            if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 4);
            }
            else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
            {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(par1World, par2, par3, par4, 0, 15).getNewCurrent() != 15)
                {
                    return;
                }
                // CraftBukkit end
                par1World.setBlock(par2, par3, par4, Block.redstoneLampActive.blockID, 0, 2);
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(final World par1World, final int par2, final int par3, final int par4, final Random par5Random)
    {
        if (!par1World.isRemote && this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
        {
            // CraftBukkit start
            if (CraftEventFactory.callRedstoneChange(par1World, par2, par3, par4, 15, 0).getNewCurrent() != 0)
            {
                return;
            }
            // CraftBukkit end
            par1World.setBlock(par2, par3, par4, Block.redstoneLampIdle.blockID, 0, 2);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(final int par1, final Random par2Random, final int par3)
    {
        return Block.redstoneLampIdle.blockID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(final World par1World, final int par2, final int par3, final int par4)
    {
        return Block.redstoneLampIdle.blockID;
    }
}
