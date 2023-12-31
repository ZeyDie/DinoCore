package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet35EntityHeadRotation extends Packet
{
    public int entityId;
    public byte headRotationYaw;

    public Packet35EntityHeadRotation() {}

    public Packet35EntityHeadRotation(final int par1, final byte par2)
    {
        this.entityId = par1;
        this.headRotationYaw = par2;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.headRotationYaw = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeByte(this.headRotationYaw);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityHeadRotation(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 5;
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket()
    {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(final Packet par1Packet)
    {
        final Packet35EntityHeadRotation packet35entityheadrotation = (Packet35EntityHeadRotation)par1Packet;
        return packet35entityheadrotation.entityId == this.entityId;
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
