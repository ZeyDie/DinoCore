package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.Hopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import java.util.List;

public class EntityMinecartHopper extends EntityMinecartContainer implements Hopper
{
    /** Whether this hopper minecart is being blocked by an activator rail. */
    private boolean isBlocked = true;
    private int transferTicker = -1;

    public EntityMinecartHopper(final World par1World)
    {
        super(par1World);
    }

    public EntityMinecartHopper(final World par1World, final double par2, final double par4, final double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 5;
    }

    public Block getDefaultDisplayTile()
    {
        return Block.hopperBlock;
    }

    public int getDefaultDisplayTileOffset()
    {
        return 1;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 5;
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(final EntityPlayer par1EntityPlayer)
    {
        if(MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, par1EntityPlayer))) 
        {
            return true;
        }
        if (!this.worldObj.isRemote)
        {
            par1EntityPlayer.displayGUIHopperMinecart(this);
        }

        return true;
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(final int par1, final int par2, final int par3, final boolean par4)
    {
        final boolean flag1 = !par4;

        if (flag1 != this.getBlocked())
        {
            this.setBlocked(flag1);
        }
    }

    /**
     * Get whether this hopper minecart is being blocked by an activator rail.
     */
    public boolean getBlocked()
    {
        return this.isBlocked;
    }

    /**
     * Set whether this hopper minecart is being blocked by an activator rail.
     */
    public void setBlocked(final boolean par1)
    {
        this.isBlocked = par1;
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorldObj()
    {
        return this.worldObj;
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos()
    {
        return this.posX;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos()
    {
        return this.posY;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos()
    {
        return this.posZ;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked())
        {
            --this.transferTicker;

            if (!this.canTransfer())
            {
                this.setTransferTicker(0);

                if (this.func_96112_aD())
                {
                    this.setTransferTicker(4);
                    this.onInventoryChanged();
                }
            }
        }
    }

    public boolean func_96112_aD()
    {
        if (TileEntityHopper.suckItemsIntoHopper(this))
        {
            return true;
        }
        else
        {
            final List list = this.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.25D, 0.0D, 0.25D), IEntitySelector.selectAnything);

            if (!list.isEmpty())
            {
                TileEntityHopper.insertStackFromEntity(this, (EntityItem)list.get(0));
            }

            return false;
        }
    }

    public void killMinecart(final DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);
        this.dropItemWithOffset(Block.hopperBlock.blockID, 1, 0.0F);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("TransferCooldown", this.transferTicker);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.transferTicker = par1NBTTagCompound.getInteger("TransferCooldown");
    }

    /**
     * Sets the transfer ticker, used to determine the delay between transfers.
     */
    public void setTransferTicker(final int par1)
    {
        this.transferTicker = par1;
    }

    /**
     * Returns whether the hopper cart can currently transfer an item.
     */
    public boolean canTransfer()
    {
        return this.transferTicker > 0;
    }
}
