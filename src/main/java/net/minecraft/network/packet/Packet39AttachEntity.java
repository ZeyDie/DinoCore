package net.minecraft.network.packet;

import net.minecraft.entity.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet39AttachEntity extends Packet
{
    /** 0 for riding, 1 for leashed. */
    public int attachState;
    public int ridingEntityId;
    public int vehicleEntityId;

    public Packet39AttachEntity() {}

    public Packet39AttachEntity(final int par1, final Entity par2Entity, final Entity par3Entity)
    {
        this.attachState = par1;
        this.ridingEntityId = par2Entity.entityId;
        this.vehicleEntityId = par3Entity != null ? par3Entity.entityId : -1;
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.ridingEntityId = par1DataInput.readInt();
        this.vehicleEntityId = par1DataInput.readInt();
        this.attachState = par1DataInput.readUnsignedByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.ridingEntityId);
        par1DataOutput.writeInt(this.vehicleEntityId);
        par1DataOutput.writeByte(this.attachState);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleAttachEntity(this);
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
        final Packet39AttachEntity packet39attachentity = (Packet39AttachEntity)par1Packet;
        return packet39attachentity.ridingEntityId == this.ridingEntityId;
    }
}
