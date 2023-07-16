package com.zeydie.netty.handlers;

import com.zeydie.netty.server.NettyServerListenThread;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class DSLChannelInitializerHandler extends ChannelInitializer<SocketChannel> {
    @NotNull
    private final NettyServerListenThread nettyServerListenThread;

    public DSLChannelInitializerHandler(@NotNull final NettyServerListenThread nettyServerListenThread) {
        this.nettyServerListenThread = nettyServerListenThread;
    }

    @Override
    public void exceptionCaught(@NotNull final ChannelHandlerContext channelHandlerContext, @NotNull final Throwable throwable) {
        channelHandlerContext.close();
    }

    @Override
    protected void initChannel(@NotNull final SocketChannel socketChannel) {
        final SocketChannelConfig socketChannelConfig = socketChannel.config();

        socketChannelConfig.setOption(ChannelOption.IP_TOS, 24);
        socketChannelConfig.setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);

        this.nettyServerListenThread.addPendingConnection(new NetLoginHandler(MinecraftServer.getServer(), socketChannel, "Connection"));
    }
}