package net.minecraft.item;

// CraftBukkit start

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
// CraftBukkit end

public class ItemHangingEntity extends Item
{
    private final Class hangingEntityClass;

    public ItemHangingEntity(final int par1, final Class par2Class)
    {
        super(par1);
        this.hangingEntityClass = par2Class;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        if (par7 == 0)
        {
            return false;
        }
        else if (par7 == 1)
        {
            return false;
        }
        else
        {
            final int i1 = Direction.facingToDirection[par7];
            final EntityHanging entityhanging = this.createHangingEntity(par3World, par4, par5, par6, i1);

            if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
            {
                return false;
            }
            else
            {
                if (entityhanging != null && entityhanging.onValidSurface())
                {
                    if (!par3World.isRemote)
                    {
                        // CraftBukkit start
                        final Player who = (par2EntityPlayer == null) ? null : (Player) par2EntityPlayer.getBukkitEntity();
                        final org.bukkit.block.Block blockClicked = par3World.getWorld().getBlockAt(par4, par5, par6);
                        final org.bukkit.block.BlockFace blockFace = org.bukkit.craftbukkit.v1_6_R3.block.CraftBlock.notchToBlockFace(par7);
                        final HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) entityhanging.getBukkitEntity(), who, blockClicked, blockFace);
                        par3World.getServer().getPluginManager().callEvent(event);
                        PaintingPlaceEvent paintingEvent = null;

                        if (entityhanging instanceof EntityPainting)
                        {
                            // Fire old painting event until it can be removed
                            paintingEvent = new PaintingPlaceEvent((org.bukkit.entity.Painting) entityhanging.getBukkitEntity(), who, blockClicked, blockFace);
                            paintingEvent.setCancelled(event.isCancelled());
                            par3World.getServer().getPluginManager().callEvent(paintingEvent);
                        }

                        if (event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled()))
                        {
                            return false;
                        }

                        // CraftBukkit end
                        par3World.spawnEntityInWorld(entityhanging);
                    }

                    --par1ItemStack.stackSize;
                }

                return true;
            }
        }
    }

    /**
     * Create the hanging entity associated to this item.
     */
    private EntityHanging createHangingEntity(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        return (EntityHanging)(this.hangingEntityClass == EntityPainting.class ? new EntityPainting(par1World, par2, par3, par4, par5) : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(par1World, par2, par3, par4, par5) : null));
    }
}
