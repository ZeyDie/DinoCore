package com.zeydie.netty.encoders;

import com.zeydie.netty.NettyEncryptionTranslator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public final class NettyEncryptingEncoder extends MessageToByteEncoder<ByteBuf> {
    @NotNull
    private final NettyEncryptionTranslator translator;
    private boolean firstSkipped = false;

    public NettyEncryptingEncoder(@NotNull final Cipher cipher) {
        this.translator = new NettyEncryptionTranslator(cipher);
    }

    @Override
    protected void encode(
            @NotNull final ChannelHandlerContext paramChannelHandlerContext,
            @NotNull final ByteBuf paramByteBuf1,
            @NotNull final ByteBuf paramByteBuf2
    ) {
        if (this.firstSkipped)
            try {
                this.translator.writeBytes(paramByteBuf1, paramByteBuf2);
            } catch (final ShortBufferException e) {
                throw new RuntimeException(e);
            }
        else {
            this.firstSkipped = true;

            paramByteBuf2.writeBytes(paramByteBuf1);
        }
    }
}
