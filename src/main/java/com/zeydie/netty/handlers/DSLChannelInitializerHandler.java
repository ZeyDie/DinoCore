package com.zeydie.netty.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import net.minecraft.server.ServerListenThread;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class DSLChannelInitializerHandler extends ChannelInitializer<SocketChannel> {
    @NotNull
    private final ServerListenThread serverListenThread;

    public DSLChannelInitializerHandler(@NotNull final ServerListenThread serverListenThread) {
        this.serverListenThread = serverListenThread;
    }

    @Override
    protected void initChannel(@NotNull final SocketChannel socketChannel) {
        final SocketChannelConfig socketChannelConfig = socketChannel.config();

        socketChannelConfig.setOption(ChannelOption.IP_TOS, 24);
        socketChannelConfig.setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);

        try {
            this.serverListenThread.createConnectionNetty(socketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
