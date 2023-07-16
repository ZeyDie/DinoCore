package com.zeydie.netty.common;

import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@Getter
@Setter
public final class CustomSocket extends Socket {
    @NotNull
    private final InetAddress inetAddress;
    private final int port;

    public CustomSocket(@NotNull final SocketChannel socketChannel) {
        this(socketChannel.remoteAddress());
    }

    public CustomSocket(@NotNull final InetSocketAddress inetSocketAddress) {
        this.inetAddress = inetSocketAddress.getAddress();
        this.port = inetSocketAddress.getPort();
    }

    public CustomSocket(@NotNull final Socket socket) {
        this.inetAddress = socket.getInetAddress();
        this.port = socket.getPort();
    }

    @NotNull
    public String getIP() {
        return this.getInetAddress().getHostAddress();
    }

    @NotNull
    @Override
    public SocketAddress getRemoteSocketAddress() {
        return new InetSocketAddress(this.getInetAddress(), this.getPort());
    }
}
