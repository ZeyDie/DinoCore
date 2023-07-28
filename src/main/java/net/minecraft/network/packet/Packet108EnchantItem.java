package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet108EnchantItem extends Packet
{
    public int windowId;

    /**
     * The position of the enchantment on the enchantment table window, starting with 0 as the topmost one.
     */
    public int enchantment;

    public Packet108EnchantItem() {}

    @SideOnly(Side.CLIENT)
    public Packet108EnchantItem(final int par1, final int par2)
    {
        this.windowId = par1;
        this.enchantment = par2;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleEnchantItem(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.windowId = par1DataInput.readByte();
        this.enchantment = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.windowId);
        par1DataOutput.writeByte(this.enchantment);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2;
    }
}
