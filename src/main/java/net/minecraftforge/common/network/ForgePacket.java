package net.minecraftforge.common.network;

import com.google.common.base.Throwables;
import com.google.common.collect.MapMaker;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.FMLNetworkException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;
import net.minecraftforge.fluids.FluidIdMapPacket;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

public abstract class ForgePacket
{
    public static final String CHANNEL_ID = "FORGE";
    enum Type
    {
        /**
         * Registers a dimension for a provider on client
         */
        REGISTERDIMENSION(DimensionRegisterPacket.class),
        /**
         * The Fluid ID map to send to the client
         */
        FLUID_IDMAP(FluidIdMapPacket.class);

        private Class<? extends ForgePacket> packetType;
        private ConcurrentMap<INetworkManager, ForgePacket> partTracker;

        private Type(final Class<? extends ForgePacket> clazz)
        {
            this.packetType = clazz;
        }

        ForgePacket make()
        {
            try
            {
                return this.packetType.newInstance();
            }
            catch (final Exception e)
            {
                Throwables.propagateIfPossible(e);
                FMLLog.log(Level.SEVERE, e, "A bizarre critical error occured during packet encoding");
                throw new FMLNetworkException(e);
            }
        }

        private ForgePacket consumePart(final INetworkManager network, final byte[] data)
        {
            if (partTracker == null)
            {
                partTracker = new MapMaker().weakKeys().weakValues().makeMap();
            }
            if (!partTracker.containsKey(network))
            {
                partTracker.put(network, make());
            }

            final ForgePacket pkt = partTracker.get(network);

            final ByteArrayDataInput bdi = ByteStreams.newDataInput(data);
            final int chunkIdx = UnsignedBytes.toInt(bdi.readByte());
            final int chunkTotal = UnsignedBytes.toInt(bdi.readByte());
            final int chunkLength = bdi.readInt();

            if (pkt.partials == null)
            {
                pkt.partials = new byte[chunkTotal][];
            }

            pkt.partials[chunkIdx] = new byte[chunkLength];
            bdi.readFully(pkt.partials[chunkIdx]);
            for (int i = 0; i < pkt.partials.length; i++)
            {
                if (pkt.partials[i] == null)
                {
                    return null;
                }
            }

            return pkt;
        }
    }

    private Type type;
    private byte[][] partials;

    public static Packet250CustomPayload[] makePacketSet(final ForgePacket packet)
    {
        final byte[] packetData = packet.generatePacket();

        if (packetData.length < 32000)
        {
            return new Packet250CustomPayload[]
            {
                new Packet250CustomPayload(CHANNEL_ID, 
                    Bytes.concat(new byte[]
                    {
                        UnsignedBytes.checkedCast(0), //IsMultipart: False
                        UnsignedBytes.checkedCast(packet.getID())
                    },
                    packetData))
            };
        }
        else
        {
            final byte[][] chunks = new byte[packetData.length / 32000 + 1][];
            for (int i = 0; i < packetData.length / 32000 + 1; i++)
            {
                final int len = Math.min(32000, packetData.length - i* 32000);
                chunks[i] = Bytes.concat(new byte[]
                    {
                        UnsignedBytes.checkedCast(1),              //IsMultipart: True
                        UnsignedBytes.checkedCast(packet.getID()), //Packet ID
                        UnsignedBytes.checkedCast(i),              //Part Number
                        UnsignedBytes.checkedCast(chunks.length),  //Total Parts
                    },
                    Ints.toByteArray(len),                         //Length
                    Arrays.copyOfRange(packetData, i * 32000, len + i * 32000));
            }

            final Packet250CustomPayload[] ret = new Packet250CustomPayload[chunks.length];
            for (int i = 0; i < chunks.length; i++)
            {
                ret[i] = new Packet250CustomPayload(CHANNEL_ID, chunks[i]);
            }
            return ret;
        }
    }

    public static ForgePacket readPacket(final INetworkManager network, final byte[] payload)
    {
        final boolean multipart = UnsignedBytes.toInt(payload[0]) == 1;
        final int type = UnsignedBytes.toInt(payload[1]);
        final Type eType = Type.values()[type];
        final byte[] data = Arrays.copyOfRange(payload, 2, payload.length);

        if (multipart)
        {
            final ForgePacket pkt = eType.consumePart(network, data);
            if (pkt != null)
            {
                return pkt.consumePacket(Bytes.concat(pkt.partials));
            }
            return null;
        }
        else
        {
            return eType.make().consumePacket(data);
        }
    }

    public ForgePacket()
    {
        for (final Type t : Type.values())
        {
            if (t.packetType == getClass())
            {
                type = t;
                continue;
            }
        }
        if (type == null)
        {
            throw new RuntimeException("ForgePacket constructor called on ungregistered type.");
        }
    }

    public byte getID()
    {
        return UnsignedBytes.checkedCast(type.ordinal());
    }

    public abstract byte[] generatePacket();

    public abstract ForgePacket consumePacket(byte[] data);

    public abstract void execute(INetworkManager network, EntityPlayer player);
}
