package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet11PlayerPosition extends Packet10Flying
{
    public Packet11PlayerPosition()
    {
        this.moving = true;
    }

    @SideOnly(Side.CLIENT)
    public Packet11PlayerPosition(final double par1, final double par3, final double par5, final double par7, final boolean par9)
    {
        this.xPosition = par1;
        this.yPosition = par3;
        this.stance = par5;
        this.zPosition = par7;
        this.onGround = par9;
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
        super.writePacketData(par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 33;
    }
}
