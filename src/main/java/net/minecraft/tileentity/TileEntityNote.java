package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity
{
    /** Note to play */
    public byte note;

    /** stores the latest redstone state */
    public boolean previousRedstoneState;

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setByte("note", this.note);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.note = par1NBTTagCompound.getByte("note");

        if (this.note < 0)
        {
            this.note = 0;
        }

        if (this.note > 24)
        {
            this.note = 24;
        }
    }

    /**
     * change pitch by -> (currentPitch + 1) % 25
     */
    public void changePitch()
    {
        this.note = (byte)((this.note + 1) % 25);
        this.onInventoryChanged();
    }

    /**
     * plays the stored note
     */
    public void triggerNote(final World par1World, final int par2, final int par3, final int par4)
    {
        if (par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.air)
        {
            final Material material = par1World.getBlockMaterial(par2, par3 - 1, par4);
            byte b0 = 0;

            if (material == Material.rock)
            {
                b0 = 1;
            }

            if (material == Material.sand)
            {
                b0 = 2;
            }

            if (material == Material.glass)
            {
                b0 = 3;
            }

            if (material == Material.wood)
            {
                b0 = 4;
            }

            // CraftBukkit start
            final org.bukkit.event.block.NotePlayEvent event = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callNotePlayEvent(this.worldObj, par2, par3, par4, b0, this.note);

            if (!event.isCancelled())
            {
                this.worldObj.addBlockEvent(par2, par3, par4, Block.music.blockID, event.getInstrument().getType(), event.getNote().getId());
            }

            // CraftBukkit end
        }
    }

    // Cauldron start
    @Override
    public boolean canUpdate()
    {
        return false;
    }
    // Cauldron end
}
