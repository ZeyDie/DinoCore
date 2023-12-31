package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet19EntityAction extends Packet
{
    /** Player ID. */
    public int entityId;

    /**
     * 1=start sneaking, 2=stop sneaking, 3=wake up, 4=start sprinting, 5=stop sprinting, 6 = horse jump?, 7 = open
     * horse GUI
     */
    public int action;
    public int auxData;

    public Packet19EntityAction() {}

    @SideOnly(Side.CLIENT)
    public Packet19EntityAction(final Entity par1Entity, final int par2)
    {
        this(par1Entity, par2, 0);
    }

    @SideOnly(Side.CLIENT)
    public Packet19EntityAction(final Entity par1Entity, final int par2, final int par3)
    {
        this.entityId = par1Entity.entityId;
        this.action = par2;
        this.auxData = par3;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.action = par1DataInput.readByte();
        this.auxData = par1DataInput.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeByte(this.action);
        par1DataOutput.writeInt(this.auxData);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityAction(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 9;
    }
}
