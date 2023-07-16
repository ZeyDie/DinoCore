package com.zeydie.netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet254ServerPing;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class NettyPacketDecoderLegacy extends ByteToMessageDecoder {
    @NotNull
    private final TcpConnection tcpConnection;

    public NettyPacketDecoderLegacy(@NotNull final TcpConnection tcpConnection) {
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
        if (paramByteBuf.readableBytes() >= 1) {
            int index = paramByteBuf.readerIndex();
            short unsignedByte = paramByteBuf.readUnsignedByte();

            try {
                switch (unsignedByte) {
                    case 42: {
                        paramByteBuf.readerIndex(index);

                        paramChannelHandlerContext.pipeline().replace(this, "unwrapper", new LengthFieldBasedFrameDecoder(650000, 1, 4, 0, 5));
                        paramChannelHandlerContext.pipeline().addAfter("unwrapper", "decoder", new NettyPacketDecoder(this.tcpConnection));

                        break;
                    }
                    case 254: {
                        if (paramByteBuf.readableBytes() < 29)
                            paramByteBuf.readerIndex(index);
                        else {
                            try {
                                paramByteBuf.readByte();
                                paramByteBuf.readByte();

                                final ByteBufInputStream var6 = new ByteBufInputStream(paramByteBuf);
                                Packet.readString(var6, 255);

                                final short var7 = paramByteBuf.readShort();

                                if (paramByteBuf.readableBytes() < var7)
                                    paramByteBuf.readerIndex(index);
                                else {
                                    Packet254ServerPing packet254ServerPing = new Packet254ServerPing();
                                    packet254ServerPing.readSuccessfully = paramByteBuf.readByte();

                                    if (packet254ServerPing.readSuccessfully >= 73) {
                                        packet254ServerPing.field_140052_b = Packet.readString(var6, 255);
                                        packet254ServerPing.field_140053_c = paramByteBuf.readInt();
                                    }

                                    paramList.add(packet254ServerPing);
                                }
                            } catch (Throwable var9) {
                                this.exceptionCaught(paramChannelHandlerContext, new RuntimeException("Ping protocol error"));
                            }
                        }
                        break;
                    }
                    default:
                        this.exceptionCaught(paramChannelHandlerContext, new RuntimeException("Handshake protocol error " + unsignedByte));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
