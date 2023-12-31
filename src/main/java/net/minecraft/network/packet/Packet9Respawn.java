package net.minecraft.network.packet;

import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet9Respawn extends Packet
{
    public int respawnDimension;

    /**
     * The difficulty setting. 0 through 3 for peaceful, easy, normal, hard. The client always sends 1.
     */
    public int difficulty;

    /** Defaults to 128 */
    public int worldHeight;
    public EnumGameType gameType;
    public WorldType terrainType;

    public Packet9Respawn() {}

    public Packet9Respawn(final int par1, final byte par2, final WorldType par3WorldType, final int par4, final EnumGameType par5EnumGameType)
    {
        this.respawnDimension = par1;
        this.difficulty = par2;
        this.worldHeight = par4;
        this.gameType = par5EnumGameType;
        this.terrainType = par3WorldType;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleRespawn(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.respawnDimension = par1DataInput.readInt();
        this.difficulty = par1DataInput.readByte();
        this.gameType = EnumGameType.getByID(par1DataInput.readByte());
        this.worldHeight = par1DataInput.readShort();
        final String s = readString(par1DataInput, 16);
        this.terrainType = WorldType.parseWorldType(s);

        if (this.terrainType == null)
        {
            this.terrainType = WorldType.DEFAULT;
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.respawnDimension);
        par1DataOutput.writeByte(this.difficulty);
        par1DataOutput.writeByte(this.gameType.getID());
        par1DataOutput.writeShort(this.worldHeight);
        writeString(this.terrainType.getWorldTypeName(), par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8 + (this.terrainType == null ? 0 : this.terrainType.getWorldTypeName().length());
    }
}
