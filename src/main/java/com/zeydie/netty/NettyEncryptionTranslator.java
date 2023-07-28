package com.zeydie.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public final class NettyEncryptionTranslator {
    @NotNull
    private final Cipher cipher;
    private byte[] bytesA = new byte[0];
    private byte[] bytesB = new byte[0];

    public NettyEncryptionTranslator(@NotNull final Cipher cipher) {
        this.cipher = cipher;
    }

    private byte[] readBytes(@NotNull final ByteBuf byteBuf) {
        final int var2 = byteBuf.readableBytes();

        if (this.bytesA.length < var2)
            this.bytesA = new byte[var2];

        byteBuf.readBytes(this.bytesA, 0, var2);

        return this.bytesA;
    }

    @NotNull
    public ByteBuf writeIndex(
            @NotNull final ChannelHandlerContext channelHandlerContext,
            @NotNull final ByteBuf byteBuf
    ) throws ShortBufferException {
        final int readableBytes = byteBuf.readableBytes();
        final byte[] bytes = this.readBytes(byteBuf);
        final ByteBuf buffer = channelHandlerContext.alloc().heapBuffer(this.cipher.getOutputSize(readableBytes));

        buffer.writerIndex(this.cipher.update(bytes, 0, readableBytes, buffer.array(), buffer.arrayOffset()));

        return buffer;
    }

    public void writeBytes(
            @NotNull final ByteBuf var1,
            @NotNull final ByteBuf var2
    ) throws ShortBufferException {
        final int readableBytes = var1.readableBytes();
        final byte[] bytes = this.readBytes(var1);
        final int size = this.cipher.getOutputSize(readableBytes);

        if (this.bytesB.length < size)
            this.bytesB = new byte[size];

        var2.writeBytes(this.bytesB, 0, this.cipher.update(bytes, 0, readableBytes, this.bytesB));
    }
}
