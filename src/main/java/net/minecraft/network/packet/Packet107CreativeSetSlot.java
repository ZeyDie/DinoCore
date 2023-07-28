package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet107CreativeSetSlot extends Packet
{
    public int slot;
    public ItemStack itemStack;

    public Packet107CreativeSetSlot() {}

    @SideOnly(Side.CLIENT)
    public Packet107CreativeSetSlot(final int par1, final ItemStack par2ItemStack)
    {
        this.slot = par1;
        this.itemStack = par2ItemStack != null ? par2ItemStack.copy() : null;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleCreativeSetSlot(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.slot = par1DataInput.readShort();
        this.itemStack = readItemStack(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeShort(this.slot);
        writeItemStack(this.itemStack, par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }
}
