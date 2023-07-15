package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zeydie.netty.common.CustomSocket;
import com.zeydie.netty.handlers.DSLChannelInitializerHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetworkListenThread;
import org.jetbrains.annotations.NotNull;
import ru.zoom4ikdan4ik.settings.optimization.BotsSettings;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

public class ServerListenThread extends Thread {
    //TODO ZeyCodeStart
    @Getter
    //TODO ZeyCodeEnd
    private final List pendingConnections = Collections.synchronizedList(new ArrayList());

    /**
     * This map stores a list of InetAddresses and the last time which they connected at
     */
    private final HashMap recentConnections = new HashMap();
    private final ServerSocket myServerSocket;
    private final InetAddress myServerAddress;
    private final int myPort;
    long connectionThrottle; // CraftBukkit
    private int connectionCounter;
    //TODO ZeyCodeStart
    @Getter
    //TODO ZeyCodeEnd
    private final NetworkListenThread myNetworkListenThread;

    //TODO ZeyCodeStart
    private final ServerListenThread tcpDedicatedServerListenThread;

    public ServerListenThread(
            @NotNull final NetworkListenThread par1NetworkListenThread,
            final int port,
            @NotNull final InetAddress inetAddress
    )
            throws IOException {
        super("Listen thread Netty");

        this.myNetworkListenThread = par1NetworkListenThread;
        this.myPort = port;
        this.myServerSocket = null;
        this.myServerAddress = null;

        FMLLog.info("Register listen thread netty " + inetAddress + ":" + port);

        new ServerBootstrap()
                .group(
                        new NioEventLoopGroup(
                                0,
                                new ThreadFactoryBuilder()
                                        .setNameFormat("Netty Boss IO #%d")
                                        .setDaemon(true)
                                        .build()
                        ),
                        new NioEventLoopGroup(
                                0,
                                new ThreadFactoryBuilder()
                                        .setNameFormat("Netty WorkGroup IO #%d")
                                        .setDaemon(true)
                                        .build()
                        )
                )
                .channel(NioServerSocketChannel.class)
                .childHandler(new DSLChannelInitializerHandler(this))
                .option(ChannelOption.SO_BACKLOG, 1024 * 20)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .bind(this.myPort)
                .syncUninterruptibly();

        this.tcpDedicatedServerListenThread = null;
        //this.tcpDedicatedServerListenThread = new ServerListenThread(this.myNetworkListenThread, inetAddress, this.myPort + 100);
        //this.tcpDedicatedServerListenThread.start();
    }

    public void createConnectionNetty(@NotNull final SocketChannel socketChannel) throws IOException {
        final CustomSocket customSocket = new CustomSocket(socketChannel);
        final InetAddress inetAddress = customSocket.getInetAddress();
        final long currentTime = System.currentTimeMillis();

        FMLLog.info("Connection " + inetAddress);

        final BotsSettings.BotsGson botsGson = BotsSettings.getInstance().update();

        if (botsGson.enableAntiBots && botsGson.contains(inetAddress)) {
            customSocket.close();

            return;
        }

        if (this.myNetworkListenThread.getServer().server == null) {
            customSocket.close();

            return;
        }

        this.connectionThrottle = this.myNetworkListenThread.getServer().server.getConnectionThrottle();

        synchronized (this.recentConnections) {
            if (this.recentConnections.containsKey(inetAddress) && !"127.0.0.1".equals(inetAddress.getHostAddress()) && currentTime - (Long) this.recentConnections.get(inetAddress) < this.connectionThrottle) {
                this.recentConnections.put(inetAddress, currentTime);
                customSocket.close();
                return;
            }

            this.recentConnections.put(inetAddress, currentTime);
        }

        FMLLog.info("Connection #" + (this.connectionCounter + 1));

        this.addPendingConnection(new NetLoginHandler(this.myNetworkListenThread.getServer(), socketChannel, "Connection #" + this.connectionCounter++));
    }

    /*public void createConnectionTCP(@NotNull final Socket socket) throws IOException {
        final InetAddress inetAddress = socket.getInetAddress();
        final long currentTime = System.currentTimeMillis();

        final BotsSettings.BotsGson botsGson = BotsSettings.getInstance().update();

        if (botsGson.enableAntiBots && botsGson.contains(inetAddress)) {
            socket.close();

            return;
        }

        if (this.myNetworkListenThread.getServer().server == null) {
            socket.close();

            return;
        }

        this.connectionThrottle = this.myNetworkListenThread.getServer().server.getConnectionThrottle();

        synchronized (this.recentConnections) {
            if (this.recentConnections.containsKey(inetAddress) && !"127.0.0.1".equals(inetAddress.getHostAddress()) && currentTime - (Long) this.recentConnections.get(inetAddress) < this.connectionThrottle) {
                this.recentConnections.put(inetAddress, currentTime);
                socket.close();
                return;
            }

            this.recentConnections.put(inetAddress, currentTime);
        }

        this.addPendingConnection(new NetLoginHandler(this.myNetworkListenThread.getServer(), socket, "Connection #" + this.connectionCounter++));
    }*/
    //TODO ZeyCodeEnd

    /*public ServerListenThread(NetworkListenThread par1NetworkListenThread, InetAddress par2InetAddress, int par3) throws IOException   // CraftBukkit - added throws
    {
        super("Listen thread");
        this.myNetworkListenThread = par1NetworkListenThread;
        this.myPort = par3;

        this.myServerSocket = new ServerSocket(par3, 0, par2InetAddress);
        this.myServerAddress = par2InetAddress == null ? this.myServerSocket.getInetAddress() : par2InetAddress;
        this.myServerSocket.setPerformancePreferences(0, 2, 1);

        //TODO ZeyCodeStart
        this.tcpDedicatedServerListenThread = null;
        //TODO ZeyCodeEnd
    }*/

    public void processPendingConnections() {
        List list = this.pendingConnections;

        synchronized (this.pendingConnections) {
            for (int i = 0; i < this.pendingConnections.size(); ++i) {
                NetLoginHandler netloginhandler = (NetLoginHandler) this.pendingConnections.get(i);

                try {

                    netloginhandler.tryLogin();
                } catch (Exception exception) {
                    netloginhandler.raiseErrorAndDisconnect("Internal server error");
                    FMLLog.log(Level.SEVERE, exception, "Error handling login related packet - connection from %s refused", netloginhandler.getUsernameAndAddress());
                    this.myNetworkListenThread.getServer().getLogAgent().logWarningException("Failed to handle packet for " + netloginhandler.getUsernameAndAddress() + ": " + exception, exception);
                }

                if (netloginhandler.connectionComplete) {
                    this.pendingConnections.remove(i--);
                }

                netloginhandler.myTCPConnection.wakeThreads();
            }
        }
    }

    public void run() {
        //TODO ZeyCodeStart
        if (this.myServerSocket == null || this.myServerAddress == null) return;
        //TODO ZeyCodeEnd

        while (this.myNetworkListenThread.isListening) {
            try {
                Socket socket = this.myServerSocket.accept();

                //TODO ZeyCodeStart
                //this.createConnectionTCP(socket);
                //TODO ZeyCodeEnd
                //TODO ZeyCodeClear
                // CraftBukkit start - Connection throttle
                /*InetAddress address = socket.getInetAddress();
                long currentTime = System.currentTimeMillis();

                //TODO ZoomCodeStart
                final BotsSettings.BotsGson botsGson = BotsSettings.getInstance().update();

                if (botsGson.enableAntiBots && botsGson.contains(address)) {
                    socket.close();

                    continue;
                }
                //TODO ZoomCodeEnd

                if (this.myNetworkListenThread.getServer().server == null) {
                    socket.close();
                    continue;
                }

                connectionThrottle = this.myNetworkListenThread.getServer().server.getConnectionThrottle();

                synchronized (this.recentConnections) {
                    if (this.recentConnections.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - ((Long) this.recentConnections.get(address)).longValue() < connectionThrottle) {
                        this.recentConnections.put(address, currentTime);
                        socket.close();
                        continue;
                    }

                    this.recentConnections.put(address, currentTime);
                }

                // CraftBukkit end
                NetLoginHandler netloginhandler = new NetLoginHandler(this.myNetworkListenThread.getServer(), socket, "Connection #" + this.connectionCounter++);

                this.addPendingConnection(netloginhandler);*/
            } catch (IOException ioexception) {
                this.myNetworkListenThread.getServer().getLogAgent().logWarning("DSCT: " + ioexception.getMessage()); // CraftBukkit
            }
        }

        this.myNetworkListenThread.getServer().getLogAgent().logInfo("Closing listening thread");
    }

    private void addPendingConnection(NetLoginHandler par1NetLoginHandler) {
        if (par1NetLoginHandler == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        } else {
            List list = this.pendingConnections;

            synchronized (this.pendingConnections) {
                this.pendingConnections.add(par1NetLoginHandler);
            }
        }
    }

    public void func_71769_a(InetAddress par1InetAddress) {
        if (par1InetAddress != null) {
            HashMap hashmap = this.recentConnections;

            synchronized (this.recentConnections) {
                this.recentConnections.remove(par1InetAddress);
            }
        }
    }

    public void func_71768_b() {
        //TODO ZeyCodeStart
        if (this.myServerSocket != null)
            //TODO ZeyCodeEnd

            try {
                this.myServerSocket.close();
            } catch (Throwable ignored) {
            }
    }

    @SideOnly(Side.CLIENT)
    public int getMyPort() {
        return this.myPort;
    }
}
