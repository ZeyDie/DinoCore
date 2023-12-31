package net.minecraft.network.packet;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDummyContainer;

import java.io.*;

public class Packet52MultiBlockChange extends Packet
{
    /** Chunk X position. */
    public int xPosition;

    /** Chunk Z position. */
    public int zPosition;

    /** The metadata for each block changed. */
    public byte[] metadataArray;

    /** The size of the arrays. */
    public int size;
    private static byte[] field_73449_e = new byte[0];

    public Packet52MultiBlockChange()
    {
        this.isChunkDataPacket = true;
    }

    public Packet52MultiBlockChange(final int par1, final int par2, final short[] par3ArrayOfShort, final int par4, final World par5World)
    {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.zPosition = par2;
        this.size = par4;
        final int l = 4 * par4;
        final Chunk chunk = par5World.getChunkFromChunkCoords(par1, par2);

        try
        {
            if (par4 >= ForgeDummyContainer.clumpingThreshold)
            {
                if (field_73449_e.length < l)
                {
                    field_73449_e = new byte[l];
                }
            }
            else
            {
                final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(l);
                final DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

                for (int i1 = 0; i1 < par4; ++i1)
                {
                    final int j1 = par3ArrayOfShort[i1] >> 12 & 15;
                    final int k1 = par3ArrayOfShort[i1] >> 8 & 15;
                    final int l1 = par3ArrayOfShort[i1] & 255;
                    dataoutputstream.writeShort(par3ArrayOfShort[i1]);
                    dataoutputstream.writeShort((short)((chunk.getBlockID(j1, l1, k1) & 4095) << 4 | chunk.getBlockMetadata(j1, l1, k1) & 15));
                }

                this.metadataArray = bytearrayoutputstream.toByteArray();

                if (this.metadataArray.length != l)
                {
                    throw new RuntimeException("Expected length " + l + " doesn\'t match received length " + this.metadataArray.length);
                }
            }
        }
        catch (final IOException ioexception)
        {
            this.field_98193_m.logSevereException("Couldn\'t create chunk packet", ioexception);
            this.metadataArray = null;
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.xPosition = par1DataInput.readInt();
        this.zPosition = par1DataInput.readInt();
        this.size = par1DataInput.readShort() & 65535;
        final int i = par1DataInput.readInt();

        if (i > 0)
        {
            this.metadataArray = new byte[i];
            par1DataInput.readFully(this.metadataArray);
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.writeShort((short)this.size);

        if (this.metadataArray != null)
        {
            par1DataOutput.writeInt(this.metadataArray.length);
            par1DataOutput.write(this.metadataArray);
        }
        else
        {
            par1DataOutput.writeInt(0);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleMultiBlockChange(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 10 + this.size * 4;
    }
}
