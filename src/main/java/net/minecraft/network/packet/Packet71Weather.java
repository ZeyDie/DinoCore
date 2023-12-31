package net.minecraft.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.MathHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet71Weather extends Packet
{
    public int entityID;
    public int posX;
    public int posY;
    public int posZ;
    public int isLightningBolt;

    public Packet71Weather() {}

    public Packet71Weather(final Entity par1Entity)
    {
        this.entityID = par1Entity.entityId;
        this.posX = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.posY = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.posZ = MathHelper.floor_double(par1Entity.posZ * 32.0D);

        if (par1Entity instanceof EntityLightningBolt)
        {
            this.isLightningBolt = 1;
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityID = par1DataInput.readInt();
        this.isLightningBolt = par1DataInput.readByte();
        this.posX = par1DataInput.readInt();
        this.posY = par1DataInput.readInt();
        this.posZ = par1DataInput.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityID);
        par1DataOutput.writeByte(this.isLightningBolt);
        par1DataOutput.writeInt(this.posX);
        par1DataOutput.writeInt(this.posY);
        par1DataOutput.writeInt(this.posZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleWeather(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 17;
    }
}
