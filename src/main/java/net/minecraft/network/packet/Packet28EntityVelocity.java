package net.minecraft.network.packet;

import net.minecraft.entity.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet28EntityVelocity extends Packet {
    public int entityId;
    public int motionX;
    public int motionY;
    public int motionZ;

    public Packet28EntityVelocity() {
    }

    public Packet28EntityVelocity(final Entity par1Entity) {
        this(par1Entity.entityId, par1Entity.motionX, par1Entity.motionY, par1Entity.motionZ);
    }

    public Packet28EntityVelocity(final int par1, double par2, double par4, double par6) {
        double par21 = par2;
        double par41 = par4;
        double par61 = par6;
        this.entityId = par1;
        final double d3 = 3.9D;

        if (par21 < -d3) {
            par21 = -d3;
        }

        if (par41 < -d3) {
            par41 = -d3;
        }

        if (par61 < -d3) {
            par61 = -d3;
        }

        if (par21 > d3) {
            par21 = d3;
        }

        if (par41 > d3) {
            par41 = d3;
        }

        if (par61 > d3) {
            par61 = d3;
        }

        this.motionX = (int) (par21 * 8000.0D);
        this.motionY = (int) (par41 * 8000.0D);
        this.motionZ = (int) (par61 * 8000.0D);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException {
        this.entityId = par1DataInput.readInt();
        this.motionX = par1DataInput.readShort();
        this.motionY = par1DataInput.readShort();
        this.motionZ = par1DataInput.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeShort(this.motionX);
        par1DataOutput.writeShort(this.motionY);
        par1DataOutput.writeShort(this.motionZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler) {
        par1NetHandler.handleEntityVelocity(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize() {
        return 10;
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket() {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(final Packet par1Packet) {
        final Packet28EntityVelocity packet28entityvelocity = (Packet28EntityVelocity) par1Packet;
        return packet28entityvelocity.entityId == this.entityId;
    }
}
