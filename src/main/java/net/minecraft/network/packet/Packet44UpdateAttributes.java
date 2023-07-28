package net.minecraft.network.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class Packet44UpdateAttributes extends Packet
{
    private int field_111005_a;
    private final List field_111004_b = new ArrayList();

    public Packet44UpdateAttributes() {}

    public Packet44UpdateAttributes(final int par1, final Collection par2Collection)
    {
        this.field_111005_a = par1;
        final Iterator iterator = par2Collection.iterator();

        while (iterator.hasNext())
        {
            final AttributeInstance attributeinstance = (AttributeInstance)iterator.next();
            this.field_111004_b.add(new Packet44UpdateAttributesSnapshot(this, attributeinstance.func_111123_a().getAttributeUnlocalizedName(), attributeinstance.getBaseValue(), attributeinstance.func_111122_c()));
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(final DataInput par1DataInput) throws IOException
    {
        this.field_111005_a = par1DataInput.readInt();
        final int i = par1DataInput.readInt();

        for (int j = 0; j < i; ++j)
        {
            final String s = readString(par1DataInput, 64);
            final double d0 = par1DataInput.readDouble();
            final ArrayList arraylist = new ArrayList();
            final short short1 = par1DataInput.readShort();

            for (int k = 0; k < short1; ++k)
            {
                final UUID uuid = new UUID(par1DataInput.readLong(), par1DataInput.readLong());
                arraylist.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", par1DataInput.readDouble(), par1DataInput.readByte()));
            }

            this.field_111004_b.add(new Packet44UpdateAttributesSnapshot(this, s, d0, arraylist));
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(final DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.field_111005_a);
        par1DataOutput.writeInt(this.field_111004_b.size());
        final Iterator iterator = this.field_111004_b.iterator();

        while (iterator.hasNext())
        {
            final Packet44UpdateAttributesSnapshot packet44updateattributessnapshot = (Packet44UpdateAttributesSnapshot)iterator.next();
            writeString(packet44updateattributessnapshot.func_142040_a(), par1DataOutput);
            par1DataOutput.writeDouble(packet44updateattributessnapshot.func_142041_b());
            par1DataOutput.writeShort(packet44updateattributessnapshot.func_142039_c().size());
            final Iterator iterator1 = packet44updateattributessnapshot.func_142039_c().iterator();

            while (iterator1.hasNext())
            {
                final AttributeModifier attributemodifier = (AttributeModifier)iterator1.next();
                par1DataOutput.writeLong(attributemodifier.getID().getMostSignificantBits());
                par1DataOutput.writeLong(attributemodifier.getID().getLeastSignificantBits());
                par1DataOutput.writeDouble(attributemodifier.getAmount());
                par1DataOutput.writeByte(attributemodifier.getOperation());
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final NetHandler par1NetHandler)
    {
        par1NetHandler.func_110773_a(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8 + this.field_111004_b.size() * 24;
    }

    @SideOnly(Side.CLIENT)
    public int func_111002_d()
    {
        return this.field_111005_a;
    }

    @SideOnly(Side.CLIENT)
    public List func_111003_f()
    {
        return this.field_111004_b;
    }
}
