package net.minecraft.server;

import com.zeydie.settings.optimization.CoreSettings;
import com.zeydie.settings.optimization.NettySettings;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetworkListenThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
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

    public ServerListenThread(final NetworkListenThread par1NetworkListenThread, final InetAddress par2InetAddress, final int par3) throws IOException   // CraftBukkit - added throws
    {
        super("Listen thread");
        this.myNetworkListenThread = par1NetworkListenThread;
        this.myPort = par3;

        //TODO ZeyCodeStart
        if (NettySettings.getInstance().getSettings().isEnable()) {
            this.myServerSocket = null;
            this.myServerAddress = null;

            return;
        }
        //TODO ZeyCodeEnd

        this.myServerSocket = new ServerSocket(par3, 0, par2InetAddress);
        this.myServerAddress = par2InetAddress == null ? this.myServerSocket.getInetAddress() : par2InetAddress;
        this.myServerSocket.setPerformancePreferences(0, 2, 1);
    }

    public void processPendingConnections() {
        final List list = this.pendingConnections;

        synchronized (this.pendingConnections) {
            for (int i = 0; i < this.pendingConnections.size(); ++i) {
                final NetLoginHandler netloginhandler = (NetLoginHandler) this.pendingConnections.get(i);

                try {
                    netloginhandler.tryLogin();
                } catch (final Exception exception) {
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
                final Socket socket = this.myServerSocket.accept();

                // CraftBukkit start - Connection throttle
                final InetAddress address = socket.getInetAddress();
                final long currentTime = System.currentTimeMillis();

                //TODO ZeyCodeStart
                if (CoreSettings.getInstance().getSettings().isDebug())
                    FMLLog.info("Connection " + address.getHostAddress() + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                //TODO ZeyCodeEnd

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
                final NetLoginHandler netloginhandler = new NetLoginHandler(this.myNetworkListenThread.getServer(), socket, "Connection #" + this.connectionCounter++);

                this.addPendingConnection(netloginhandler);
            } catch (final IOException ioexception) {
                this.myNetworkListenThread.getServer().getLogAgent().logWarning("DSCT: " + ioexception.getMessage()); // CraftBukkit
            }
        }

        this.myNetworkListenThread.getServer().getLogAgent().logInfo("Closing listening thread");
    }

    private void addPendingConnection(final NetLoginHandler par1NetLoginHandler) {
        if (par1NetLoginHandler == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        } else {
            final List list = this.pendingConnections;

            synchronized (this.pendingConnections) {
                this.pendingConnections.add(par1NetLoginHandler);
            }
        }
    }

    public void func_71769_a(final InetAddress par1InetAddress) {
        if (par1InetAddress != null) {
            final HashMap hashmap = this.recentConnections;

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
            } catch (final Throwable ignored) {
            }
    }

    @SideOnly(Side.CLIENT)
    public int getMyPort() {
        return this.myPort;
    }
}
