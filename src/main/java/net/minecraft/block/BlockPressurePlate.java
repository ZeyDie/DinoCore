package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.Iterator;
import java.util.List;

public class BlockPressurePlate extends BlockBasePressurePlate
{
    /** The mob type that can trigger this pressure plate. */
    private EnumMobType triggerMobType;

    protected BlockPressurePlate(final int par1, final String par2Str, final Material par3Material, final EnumMobType par4EnumMobType)
    {
        super(par1, par2Str, par3Material);
        this.triggerMobType = par4EnumMobType;
    }

    /**
     * Argument is weight (0-15). Return the metadata to be set because of it.
     */
    protected int getMetaFromWeight(final int par1)
    {
        return par1 > 0 ? 1 : 0;
    }

    /**
     * Argument is metadata. Returns power level (0-15)
     */
    protected int getPowerSupply(final int par1)
    {
        return par1 == 1 ? 15 : 0;
    }

    /**
     * Returns the current state of the pressure plate. Returns a value between 0 and 15 based on the number of items on
     * it.
     */
    protected int getPlateState(final World par1World, final int par2, final int par3, final int par4)
    {
        List list = null;

        if (this.triggerMobType == EnumMobType.everything)
        {
            list = par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, this.getSensitiveAABB(par2, par3, par4));
        }

        if (this.triggerMobType == EnumMobType.mobs)
        {
            list = par1World.getEntitiesWithinAABB(EntityLivingBase.class, this.getSensitiveAABB(par2, par3, par4));
        }

        if (this.triggerMobType == EnumMobType.players)
        {
            list = par1World.getEntitiesWithinAABB(EntityPlayer.class, this.getSensitiveAABB(par2, par3, par4));
        }

        if (list != null && !list.isEmpty())
        {
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final Entity entity = (Entity)iterator.next();
                // CraftBukkit start - Call interact event when turning on a pressure plate
                if (this.getPowerSupply(par1World.getBlockMetadata(par2, par3, par4)) == 0)
                {
                    final org.bukkit.World bworld = par1World.getWorld();
                    final org.bukkit.plugin.PluginManager manager = par1World.getServer().getPluginManager();
                    final org.bukkit.event.Cancellable cancellable;

                    if (entity instanceof EntityPlayer)
                    {
                        cancellable = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callPlayerInteractEvent((EntityPlayer) entity, org.bukkit.event.block.Action.PHYSICAL, par2, par3, par4, -1, null);
                    }
                    else
                    {
                        cancellable = new EntityInteractEvent(entity.getBukkitEntity(), bworld.getBlockAt(par2, par3, par4));
                        manager.callEvent((EntityInteractEvent) cancellable);
                    }

                    // We only want to block turning the plate on if all events are cancelled
                    if (cancellable.isCancelled())
                    {
                        continue;
                    }
                }
                // CraftBukkit end

                if (!entity.doesEntityNotTriggerPressurePlate())
                {
                    return 15;
                }
            }
        }

        return 0;
    }
}
