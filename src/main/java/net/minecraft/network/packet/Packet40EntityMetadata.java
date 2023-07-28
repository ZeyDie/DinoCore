package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.DataWatcher;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class Packet40EntityMetadata extends Packet
{
    public int entityId;
    private List metadata;

    public Packet40EntityMetadata() {}

    public Packet40EntityMetadata(final int par1, final DataWatcher par2DataWatcher, final boolean par3)
    {
        this.entityId = par1;

        if (par3)
        {
            this.metadata = par2DataWatcher.getAllWatched();
        }
        else
        {
            this.metadata = par2DataWatcher.unwatchAndReturnAllWatched();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.metadata = DataWatcher.readWatchableObjects(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        DataWatcher.writeObjectsInListToStream(this.metadata, par1DataOutput);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityMetadata(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 5;
    }

    @SideOnly(Side.CLIENT)
    public List getMetadata()
    {
        return this.metadata;
    }
}
