package net.minecraft.network.packet;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet1Login extends Packet
{
    /** The player's entity ID */
    public int clientEntityId;
    public WorldType terrainType;
    public boolean hardcoreMode;
    public EnumGameType gameType;

    /** -1: The Nether, 0: The Overworld, 1: The End */
    public int dimension;

    /** The difficulty setting byte. */
    public byte difficultySetting;

    /** Defaults to 128 */
    public byte worldHeight;

    /** The maximum players. */
    public byte maxPlayers;

    private boolean vanillaCompatible;
    
    public Packet1Login()
    {
        this.vanillaCompatible = FMLNetworkHandler.vanillaLoginPacketCompatibility();
    }

    public Packet1Login(final int par1, final WorldType par2WorldType, final EnumGameType par3EnumGameType, final boolean par4, final int par5, final int par6, final int par7, final int par8)
    {
        this.clientEntityId = par1;
        this.terrainType = par2WorldType;
        this.dimension = par5;
        this.difficultySetting = (byte)par6;
        this.gameType = par3EnumGameType;
        this.worldHeight = (byte)par7;
        this.maxPlayers = (byte)par8;
        this.hardcoreMode = par4;
        this.vanillaCompatible = false;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.clientEntityId = par1DataInput.readInt();
        final String s = readString(par1DataInput, 16);
        this.terrainType = WorldType.parseWorldType(s);

        if (this.terrainType == null)
        {
            this.terrainType = WorldType.DEFAULT;
        }

        final byte b0 = par1DataInput.readByte();
        this.hardcoreMode = (b0 & 8) == 8;
        final int i = b0 & -9;
        this.gameType = EnumGameType.getByID(i);

        if (vanillaCompatible)
        {
            this.dimension = par1DataInput.readByte();
        }
        else
        {
            this.dimension = par1DataInput.readInt();
        }

        this.difficultySetting = par1DataInput.readByte();
        this.worldHeight = par1DataInput.readByte();
        this.maxPlayers = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.clientEntityId);
        writeString(this.terrainType == null ? "" : this.terrainType.getWorldTypeName(), par1DataOutput);
        int i = this.gameType.getID();

        if (this.hardcoreMode)
        {
            i |= 8;
        }

        par1DataOutput.writeByte(i);

        if (vanillaCompatible)
        {
            par1DataOutput.writeByte(this.dimension);
        }
        else
        {
            par1DataOutput.writeInt(this.dimension);
        }

        par1DataOutput.writeByte(this.difficultySetting);
        par1DataOutput.writeByte(this.worldHeight);
        par1DataOutput.writeByte(this.maxPlayers);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleLogin(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        int i = 0;

        if (this.terrainType != null)
        {
            i = this.terrainType.getWorldTypeName().length();
        }

        return 6 + 2 * i + 4 + 4 + 1 + 1 + 1 + (vanillaCompatible ? 0 : 3);
    }
}
