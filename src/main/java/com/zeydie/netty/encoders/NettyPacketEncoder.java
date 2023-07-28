package com.zeydie.netty.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet252SharedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public final class NettyPacketEncoder extends MessageToMessageEncoder<Packet> {
    @NotNull
    private final TcpConnection tcpConnection;

    public NettyPacketEncoder(@NotNull final TcpConnection tcpConnection) {
        this.tcpConnection = tcpConnection;
    }

    @Override
    protected void encode(
            @NotNull final ChannelHandlerContext channelHandlerContext,
            @NotNull final Packet packet,
            @NotNull final List<Object> list
    ) {
        final ByteBuf byteBuf = channelHandlerContext.alloc().heapBuffer(packet.getPacketSize());

        try {
            Packet.writePacket(packet, new ByteBufOutputStream(byteBuf));

            if (packet instanceof Packet252SharedKey) {
                final Packet252SharedKey packet252SharedKey = (Packet252SharedKey) packet;

                if (this.tcpConnection.field_74463_A == null)
                    this.tcpConnection.sharedKeyForEncryption = packet252SharedKey.getSharedKey();

                this.tcpConnection.encryptOuputStream();
            }

            list.add(byteBuf);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
