package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet200Statistic extends Packet
{
    public int statisticId;
    public int amount;

    public Packet200Statistic() {}

    public Packet200Statistic(final int par1, final int par2)
    {
        this.statisticId = par1;
        this.amount = par2;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleStatistic(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.statisticId = par1DataInput.readInt();
        this.amount = par1DataInput.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.statisticId);
        par1DataOutput.writeInt(this.amount);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 6;
    }

    /**
     * If this returns true, the packet may be processed on any thread; otherwise it is queued for the main thread to
     * handle.
     */
    public boolean canProcessAsync()
    {
        return true;
    }
}
