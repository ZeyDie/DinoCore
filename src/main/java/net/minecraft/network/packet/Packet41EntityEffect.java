package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.potion.PotionEffect;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet41EntityEffect extends Packet
{
    public int entityId;
    public byte effectId;

    /** The effect's amplifier. */
    public byte effectAmplifier;
    public short duration;

    public Packet41EntityEffect() {}

    public Packet41EntityEffect(final int par1, final PotionEffect par2PotionEffect)
    {
        this.entityId = par1;
        this.effectId = (byte)(par2PotionEffect.getPotionID() & 255);
        this.effectAmplifier = (byte)(par2PotionEffect.getAmplifier() & 255);

        if (par2PotionEffect.getDuration() > 32767)
        {
            this.duration = 32767;
        }
        else
        {
            this.duration = (short)par2PotionEffect.getDuration();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.effectId = par1DataInput.readByte();
        this.effectAmplifier = par1DataInput.readByte();
        this.duration = par1DataInput.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeByte(this.effectId);
        par1DataOutput.writeByte(this.effectAmplifier);
        par1DataOutput.writeShort(this.duration);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityEffect(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if duration is at maximum, false otherwise.
     */
    public boolean isDurationMax()
    {
        return this.duration == 32767;
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
        final Packet41EntityEffect packet41entityeffect = (Packet41EntityEffect)par1Packet;
        return packet41entityeffect.entityId == this.entityId && packet41entityeffect.effectId == this.effectId;
    }
}
