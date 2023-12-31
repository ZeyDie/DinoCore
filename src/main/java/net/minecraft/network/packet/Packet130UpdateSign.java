package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet130UpdateSign extends Packet
{
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public String[] signLines;

    public Packet130UpdateSign()
    {
        this.isChunkDataPacket = true;
    }

    public Packet130UpdateSign(final int par1, final int par2, final int par3, final String[] par4ArrayOfStr)
    {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.signLines = new String[] {par4ArrayOfStr[0], par4ArrayOfStr[1], par4ArrayOfStr[2], par4ArrayOfStr[3]};
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readShort();
        this.zPosition = par1DataInput.readInt();
        this.signLines = new String[4];

        for (int i = 0; i < 4; ++i)
        {
            this.signLines[i] = readString(par1DataInput, 15);
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.writeShort(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);

        for (int i = 0; i < 4; ++i)
        {
            writeString(this.signLines[i], par1DataOutput);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleUpdateSign(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        int i = 0;

        for (int j = 0; j < 4; ++j)
        {
            i += this.signLines[j].length();
        }

        return i;
    }
}
