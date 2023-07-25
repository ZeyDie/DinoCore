package com.zeydie.netty.server;

import com.zeydie.netty.handlers.DSLChannelInitializerHandler;
import cpw.mods.fml.common.FMLLog;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetworkListenThread;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.SpigotConfig;

import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;

public final class NettyServerListenThread extends Thread {
    private final NetworkListenThread networkListenThread;
    private final int port;

    private final Map<InetAddress, Long> recentConnections = new HashMap<>();
    private final List<NetLoginHandler> pendingConnections = Collections.synchronizedList(new ArrayList<NetLoginHandler>());

    private static ChannelFuture channelFuture;

    public NettyServerListenThread(
            @NotNull final NetworkListenThread networkListenThread,
            final int port
    ) {
        super("Netty Listen thread");

        this.networkListenThread = networkListenThread;
        this.port = port;

        channelFuture = new ServerBootstrap()
                .group(
                        new NioEventLoopGroup(1),
                        new NioEventLoopGroup(SpigotConfig.nettyThreads)
                )
                .channel(NioServerSocketChannel.class)
                .childHandler(new DSLChannelInitializerHandler(this))
                .option(ChannelOption.SO_BACKLOG, 1024 * 20)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .bind(this.port)
                .sync()
                .syncUninterruptibly();
    }

    @Override
    public void run() {
    }

    public void processPendingConnections() {
        synchronized (this.pendingConnections) {
            for (int i = 0; i < this.pendingConnections.size(); ++i) {
                final NetLoginHandler netLoginHandler = this.pendingConnections.get(i);

                try {
                    netLoginHandler.tryLogin();
                } catch (Exception exception) {
                    netLoginHandler.raiseErrorAndDisconnect("Internal server error");
                    FMLLog.log(Level.SEVERE, exception, "Error handling login related packet - connection from %s refused", netLoginHandler.getUsernameAndAddress());
                    this.networkListenThread.getServer().getLogAgent().logWarningException("Failed to handle packet for " + netLoginHandler.getUsernameAndAddress() + ": " + exception, exception);
                }

                if (netLoginHandler.connectionComplete)
                    this.pendingConnections.remove(i--);

                netLoginHandler.myTCPConnection.wakeThreads();
            }
        }
    }

    public void addPendingConnection(@NotNull final NetLoginHandler netLoginHandler) {
        synchronized (this.pendingConnections) {
            this.pendingConnections.add(netLoginHandler);
        }
    }

    public void func_71769_a(@NotNull final InetAddress inetAddress) {
        synchronized (this.recentConnections) {
            this.recentConnections.remove(inetAddress);
        }
    }
}
