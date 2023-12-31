package net.minecraft.network.packet;

import net.minecraft.entity.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet17Sleep extends Packet
{
    public int entityID;
    public int bedX;
    public int bedY;
    public int bedZ;
    public int field_73622_e;

    public Packet17Sleep() {}

    public Packet17Sleep(final Entity par1Entity, final int par2, final int par3, final int par4, final int par5)
    {
        this.field_73622_e = par2;
        this.bedX = par3;
        this.bedY = par4;
        this.bedZ = par5;
        this.entityID = par1Entity.entityId;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityID = par1DataInput.readInt();
        this.field_73622_e = par1DataInput.readByte();
        this.bedX = par1DataInput.readInt();
        this.bedY = par1DataInput.readByte();
        this.bedZ = par1DataInput.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityID);
        par1DataOutput.writeByte(this.field_73622_e);
        par1DataOutput.writeInt(this.bedX);
        par1DataOutput.writeByte(this.bedY);
        par1DataOutput.writeInt(this.bedZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleSleep(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 14;
    }
}
