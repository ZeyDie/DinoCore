package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet70GameEvent extends Packet
{
    /**
     * The client prints clientMessage[eventType] to chat when this packet is received.
     */
    public static final String[] clientMessage = {"tile.bed.notValid", null, null, "gameMode.changed"};

    /** 0: Invalid bed, 1: Rain starts, 2: Rain stops, 3: Game mode changed. */
    public int eventType;

    /**
     * When reason==3, the game mode to set.  See EnumGameType for a list of values.
     */
    public int gameMode;

    public Packet70GameEvent() {}

    public Packet70GameEvent(final int par1, final int par2)
    {
        this.eventType = par1;
        this.gameMode = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.eventType = par1DataInput.readByte();
        this.gameMode = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.eventType);
        par1DataOutput.writeByte(this.gameMode);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleGameEvent(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2;
    }
}
