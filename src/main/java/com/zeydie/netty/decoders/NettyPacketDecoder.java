package com.zeydie.netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet252SharedKey;
import net.minecraft.network.packet.PacketCount;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;

public final class NettyPacketDecoder extends ByteToMessageDecoder {
    @NotNull
    private final TcpConnection tcpConnection;

    public NettyPacketDecoder(@NotNull final TcpConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    @Override
    public void exceptionCaught(@NotNull final ChannelHandlerContext channelHandlerContext, @NotNull final Throwable throwable) {
        channelHandlerContext.close();
    }

    @Override
    protected void decode(
            @NotNull final ChannelHandlerContext paramChannelHandlerContext,
            @NotNull final ByteBuf paramByteBuf,
            @NotNull final List<Object> paramList
    ) {
        paramByteBuf.markReaderIndex();

        final short s = paramByteBuf.readUnsignedByte();
        final Object object = Packet.packetIdToClassMap.lookup(s);

        try {
            if (object == null)
                throw new IOException("Bad packet id " + s);
            final Packet packet = ((Class<Packet>) object).newInstance();

            packet.readPacketData(new ByteBufInputStream(paramByteBuf));

            PacketCount.countPacket(s, packet.getPacketSize());
            Packet.receivedID++;
            Packet.receivedSize += packet.getPacketSize();

            paramList.add(packet);

            if (packet instanceof Packet252SharedKey) {
                final Packet252SharedKey packet252SharedKey = (Packet252SharedKey) packet;
                final PrivateKey privateKey = this.tcpConnection.field_74463_A;

                if (privateKey != null)
                    this.tcpConnection.sharedKeyForEncryption = packet252SharedKey.getSharedKey(privateKey);

                this.tcpConnection.decryptInputStream();
            }
        } catch (final IOException | IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace();
        }
    }
}