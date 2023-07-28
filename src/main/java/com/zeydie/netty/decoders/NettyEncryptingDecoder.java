package com.zeydie.netty.decoders;

import com.zeydie.netty.NettyEncryptionTranslator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import java.util.List;

public final class NettyEncryptingDecoder extends ByteToMessageDecoder {
    @NotNull
    private final NettyEncryptionTranslator translator;

    public NettyEncryptingDecoder(@NotNull final Cipher cipher) {
        this.translator = new NettyEncryptionTranslator(cipher);
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
        try {
            paramList.add(this.translator.writeIndex(paramChannelHandlerContext, paramByteBuf));
        } catch (final ShortBufferException e) {
            e.printStackTrace();
        }
    }
}
