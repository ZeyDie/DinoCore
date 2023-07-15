package com.zeydie.netty.handlers;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import org.jetbrains.annotations.NotNull;

public final class TCPChannelInitializerHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(@NotNull final SocketChannel socketChannel) {
        try {
            final SocketChannelConfig socketChannelConfig = socketChannel.config();

            socketChannelConfig.setOption(ChannelOption.IP_TOS, 24);
            socketChannelConfig.setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
        } catch (ChannelException exception) {
            exception.printStackTrace();
        }
    }
}