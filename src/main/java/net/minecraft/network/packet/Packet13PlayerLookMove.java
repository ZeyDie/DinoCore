package net.minecraft.network.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet13PlayerLookMove extends Packet10Flying
{
    public Packet13PlayerLookMove()
    {
        this.rotating = true;
        this.moving = true;
    }

    public Packet13PlayerLookMove(final double par1, final double par3, final double par5, final double par7, final float par9, final float par10, final boolean par11)
    {
        this.xPosition = par1;
        this.yPosition = par3;
        this.stance = par5;
        this.zPosition = par7;
        this.yaw = par9;
        this.pitch = par10;
        this.onGround = par11;
        this.rotating = true;
        this.moving = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.xPosition = par1DataInput.readDouble();
        this.yPosition = par1DataInput.readDouble();
        this.stance = par1DataInput.readDouble();
        this.zPosition = par1DataInput.readDouble();
        this.yaw = par1DataInput.readFloat();
        this.pitch = par1DataInput.readFloat();
        super.readPacketData(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeDouble(this.xPosition);
        par1DataOutput.writeDouble(this.yPosition);
        par1DataOutput.writeDouble(this.stance);
        par1DataOutput.writeDouble(this.zPosition);
        par1DataOutput.writeFloat(this.yaw);
        par1DataOutput.writeFloat(this.pitch);
        super.writePacketData(par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 41;
    }
}
