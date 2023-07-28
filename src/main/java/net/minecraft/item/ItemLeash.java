package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.bukkit.event.hanging.HangingPlaceEvent;

import java.util.Iterator;
import java.util.List;

public class ItemLeash extends Item
{
    public ItemLeash(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        final int i1 = par3World.getBlockId(par4, par5, par6);

        if (Block.blocksList[i1] != null && Block.blocksList[i1].getRenderType() == 11)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                func_135066_a(par2EntityPlayer, par3World, par4, par5, par6);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean func_135066_a(final EntityPlayer par0EntityPlayer, final World par1World, final int par2, final int par3, final int par4)
    {
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForBlock(par1World, par2, par3, par4);
        boolean flag = false;
        final double d0 = 7.0D;
        final List list = par1World.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)par2 - d0, (double)par3 - d0, (double)par4 - d0, (double)par2 + d0, (double)par3 + d0, (double)par4 + d0));

        if (list != null)
        {
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final EntityLiving entityliving = (EntityLiving)iterator.next();

                if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == par0EntityPlayer)
                {
                    if (entityleashknot == null)
                    {
                        entityleashknot = EntityLeashKnot.func_110129_a(par1World, par2, par3, par4);
                        // CraftBukkit start
                        final HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) entityleashknot.getBukkitEntity(), par0EntityPlayer != null ? (org.bukkit.entity.Player) par0EntityPlayer.getBukkitEntity() : null, par1World.getWorld().getBlockAt(par2, par3, par4), org.bukkit.block.BlockFace.SELF);
                        par1World.getServer().getPluginManager().callEvent(event);

                        if (event.isCancelled())
                        {
                            entityleashknot.setDead();
                            return false;
                        }
                        // CraftBukkit end
                    }

                    // CraftBukkit start
                    if (org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callPlayerLeashEntityEvent(entityliving, entityleashknot, par0EntityPlayer).isCancelled())
                    {
                        continue;
                    }
                    // CraftBukkit end
                    entityliving.setLeashedToEntity(entityleashknot, true);
                    flag = true;
                }
            }
        }

        return flag;
    }
}
