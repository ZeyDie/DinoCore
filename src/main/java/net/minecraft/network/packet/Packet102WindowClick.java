package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet102WindowClick extends Packet
{
    /** The id of the window which was clicked. 0 for player inventory. */
    public int window_Id;

    /** The clicked slot (-999 is outside of inventory) */
    public int inventorySlot;

    /** 1 when right-clicking and otherwise 0 */
    public int mouseClick;

    /** A unique number for the action, used for transaction handling */
    public short action;

    /** Item stack for inventory */
    public ItemStack itemStack;
    public int holdingShift;

    public Packet102WindowClick() {}

    @SideOnly(Side.CLIENT)
    public Packet102WindowClick(final int par1, final int par2, final int par3, final int par4, final ItemStack par5ItemStack, final short par6)
    {
        this.window_Id = par1;
        this.inventorySlot = par2;
        this.mouseClick = par3;
        this.itemStack = par5ItemStack != null ? par5ItemStack.copy() : null;
        this.action = par6;
        this.holdingShift = par4;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleWindowClick(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.window_Id = par1DataInput.readByte();
        this.inventorySlot = par1DataInput.readShort();
        this.mouseClick = par1DataInput.readByte();
        this.action = par1DataInput.readShort();
        this.holdingShift = par1DataInput.readByte();
        this.itemStack = readItemStack(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.window_Id);
        par1DataOutput.writeShort(this.inventorySlot);
        par1DataOutput.writeByte(this.mouseClick);
        par1DataOutput.writeShort(this.action);
        par1DataOutput.writeByte(this.holdingShift);
        writeItemStack(this.itemStack, par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 11;
    }
}
