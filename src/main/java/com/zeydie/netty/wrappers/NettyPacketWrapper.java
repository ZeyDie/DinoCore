package com.zeydie.netty.wrappers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;

public final class NettyPacketWrapper extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(
            @NotNull final ChannelHandlerContext paramChannelHandlerContext,
            @NotNull final ByteBuf paramByteBuf1,
            @NotNull final ByteBuf paramByteBuf2
    ) {
        paramByteBuf2.writeByte(42);
        paramByteBuf2.writeInt(paramByteBuf1.readableBytes());
        paramByteBuf2.writeBytes(paramByteBuf1);
    }
}
