package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet204ClientInfo extends Packet
{
    private String language;
    private int renderDistance;
    private int chatVisisble;
    private boolean chatColours;
    private int gameDifficulty;
    private boolean showCape;

    public Packet204ClientInfo() {}

    @SideOnly(Side.CLIENT)
    public Packet204ClientInfo(final String par1Str, final int par2, final int par3, final boolean par4, final int par5, final boolean par6)
    {
        this.language = par1Str;
        this.renderDistance = par2;
        this.chatVisisble = par3;
        this.chatColours = par4;
        this.gameDifficulty = par5;
        this.showCape = par6;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.language = readString(par1DataInput, 7);
        this.renderDistance = par1DataInput.readByte();
        final byte b0 = par1DataInput.readByte();
        this.chatVisisble = b0 & 7;
        this.chatColours = (b0 & 8) == 8;
        this.gameDifficulty = par1DataInput.readByte();
        this.showCape = par1DataInput.readBoolean();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        writeString(this.language, par1DataOutput);
        par1DataOutput.writeByte(this.renderDistance);
        par1DataOutput.writeByte(this.chatVisisble | (this.chatColours ? 1 : 0) << 3);
        par1DataOutput.writeByte(this.gameDifficulty);
        par1DataOutput.writeBoolean(this.showCape);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleClientInfo(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 7;
    }

    public String getLanguage()
    {
        return this.language;
    }

    public int getRenderDistance()
    {
        return this.renderDistance;
    }

    public int getChatVisibility()
    {
        return this.chatVisisble;
    }

    public boolean getChatColours()
    {
        return this.chatColours;
    }

    public int getDifficulty()
    {
        return this.gameDifficulty;
    }

    public boolean getShowCape()
    {
        return this.showCape;
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
        return true;
    }
}
