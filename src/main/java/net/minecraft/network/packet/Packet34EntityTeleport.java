package net.minecraft.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet34EntityTeleport extends Packet
{
    /** ID of the entity. */
    public int entityId;

    /** X position of the entity. */
    public int xPosition;

    /** Y position of the entity. */
    public int yPosition;

    /** Z position of the entity. */
    public int zPosition;

    /** Yaw of the entity. */
    public byte yaw;

    /** Pitch of the entity. */
    public byte pitch;

    public Packet34EntityTeleport() {}

    public Packet34EntityTeleport(final Entity par1Entity)
    {
        this.entityId = par1Entity.entityId;
        this.xPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.yaw = (byte)((int)(par1Entity.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1Entity.rotationPitch * 256.0F / 360.0F));
    }

    public Packet34EntityTeleport(final int par1, final int par2, final int par3, final int par4, final byte par5, final byte par6)
    {
        this.entityId = par1;
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        this.yaw = par5;
        this.pitch = par6;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readInt();
        this.zPosition = par1DataInput.readInt();
        this.yaw = par1DataInput.readByte();
        this.pitch = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.writeInt(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.write(this.yaw);
        par1DataOutput.write(this.pitch);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityTeleport(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 34;
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
        final Packet34EntityTeleport packet34entityteleport = (Packet34EntityTeleport)par1Packet;
        return packet34entityteleport.entityId == this.entityId;
    }
}
