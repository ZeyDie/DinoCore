package net.minecraft.network;

import com.zeydie.legacy.core.network.TcpConnectionReader;
import com.zeydie.legacy.core.network.TcpConnectionWriter;
import com.zeydie.netty.common.CustomSocket;
import com.zeydie.netty.decoders.NettyEncryptingDecoder;
import com.zeydie.netty.decoders.NettyPacketDecoderLegacy;
import com.zeydie.netty.encoders.NettyEncryptingEncoder;
import com.zeydie.netty.encoders.NettyPacketEncoder;
import com.zeydie.netty.wrappers.NettyPacketWrapper;
import com.zeydie.settings.optimization.CoreSettings;
import com.zeydie.settings.optimization.NettySettings;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet252SharedKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.SpigotConfig;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpConnection

        //TODO ZeyCodeStart
        extends ChannelInboundHandlerAdapter
        //TODO ZeyCodeEnd

        implements INetworkManager {

    //TODO ZoomCodeStart
    private final ExecutorService service = Executors.newFixedThreadPool(SpigotConfig.nettyThreads);
    //TODO ZoomCodeEnd

    public static AtomicInteger field_74471_a = new AtomicInteger();
    public static AtomicInteger field_74469_b = new AtomicInteger();

    /**
     * The object used for synchronization on the send queue.
     */
    private final Object sendQueueLock;

    /**
     * Log agent for TCP connection
     */
    private final ILogAgent tcpConLogAgent;

    /**
     * The socket used by this network manager.
     */
    private Socket networkSocket;

    /**
     * The InetSocketAddress of the remote endpoint
     */
    private SocketAddress remoteSocketAddress; // Spigot - remove final

    /**
     * The input stream connected to the socket.
     */
    private volatile DataInputStream socketInputStream;

    /**
     * The output stream connected to the socket.
     */
    //TODO ZoomCodeReplace private on public
    public volatile DataOutputStream socketOutputStream;

    /**
     * Whether the network is currently operational.
     */
    //TODO ZoomCodeReplace private on public
    public volatile boolean isRunning;

    /**
     * Whether this network manager is currently terminating (and should ignore further errors).
     */
    //TODO ZoomCodeReplace private on public
    public volatile boolean isTerminating;

    /**
     * Linked list of packets that have been read and are awaiting processing.
     */
    private final Queue<Packet> readPackets;

    /**
     * Linked list of packets awaiting sending.
     */
    private final List<Packet> dataPackets;

    //TODO ZoomCodeStart
    private final List<Packet> fastDataPackets;
    //TODO ZoomCodeEnd

    /**
     * Linked list of packets with chunk data that are awaiting sending.
     */
    private List<Packet> chunkDataPackets;

    /**
     * A reference to the NetHandler object.
     */
    private NetHandler theNetHandler;

    /**
     * Whether this server is currently terminating. If this is a client, this is always false.
     */
    //TODO ZoomCodeReplace private on public
    public boolean isServerTerminating;

    /**
     * The thread used for writing.
     */
    private Thread writeThread;

    /**
     * The thread used for reading.
     */
    private Thread readThread;

    /**
     * A String indicating why the network has shutdown.
     */
    private String terminationReason;

    /**
     * Contains shutdown description (internal error, etc.) as string array
     */
    private Object[] shutdownDescription;
    private int field_74490_x;

    /**
     * The length in bytes of the packets in both send queues (data and chunkData).
     */
    private int sendQueueByteLength;
    public static int[] field_74470_c = new int[256];
    public static int[] field_74467_d = new int[256];
    public int field_74468_e;
    boolean isInputBeingDecrypted;
    boolean isOutputEncrypted;
    //TODO ZeyCodeModified from private to public
    public SecretKey sharedKeyForEncryption;

    //TODO ZeyCodeModified from private to public
    public PrivateKey field_74463_A;

    /**
     * Delay for sending pending chunk data packets (as opposed to pending non-chunk data packets)
     */
    private int chunkDataPacketsDelay;

    //TODO ZeyCodeStart
    private final CoreSettings.CoreSettingsData coreSettingsData;
    private final NettySettings.NettySettingsData nettySettingsData;
    public SocketChannel socketChannel;

    public TcpConnection(
            final SocketChannel socketChannel,
            final String terminationReason,
            final NetHandler netHandler
    ) {
        this(socketChannel, terminationReason, netHandler, null);
    }

    public TcpConnection(
            final SocketChannel socketChannel,
            final String terminationReason,
            final NetHandler netHandler,
            final PrivateKey privateKey
    ) {
        this.coreSettingsData = CoreSettings.getInstance().getSettings();
        this.nettySettingsData = NettySettings.getInstance().getSettings();

        this.sendQueueLock = null;
        this.tcpConLogAgent = null;

        this.socketChannel = socketChannel;
        this.networkSocket = new CustomSocket(socketChannel);
        this.remoteSocketAddress = socketChannel.remoteAddress();
        this.terminationReason = terminationReason;
        this.theNetHandler = netHandler;
        this.field_74463_A = privateKey;

        this.readPackets = new ConcurrentLinkedQueue<>();

        this.dataPackets = Collections.synchronizedList(new ArrayList());
        this.fastDataPackets = Collections.synchronizedList(new ArrayList());

        socketChannel.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(30))
                .addLast("wrapper", new NettyPacketWrapper())
                .addLast("encoder", new NettyPacketEncoder(this))
                .addLast("decoder_legacy", new NettyPacketDecoderLegacy(this))
                .addLast("handler", this);
    }

    @Override
    public void channelRead(final ChannelHandlerContext channelHandlerContext, final Object o) {
        final Packet packet = (Packet) o;

        if (NettySettings.getInstance().getSettings().isAsynchronousPackets() || (packet.canProcessAsync() && this.theNetHandler.canProcessPacketsAsync())) {
            this.field_74490_x = 0;

            packet.processPacket(this.theNetHandler);
        } else
            this.readPackets.add(packet);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext channelHandlerContext, final Throwable throwable) {
        this.networkShutdown("disconnect.genericReason", "Internal exception: " + throwable.getMessage());
    }
    //TODO ZeyCodeEnd

    @SideOnly(Side.CLIENT)
    public TcpConnection(ILogAgent par1ILogAgent, Socket par2Socket, String par3Str, NetHandler par4NetHandler) throws IOException {
        this(par1ILogAgent, par2Socket, par3Str, par4NetHandler, (PrivateKey) null);
    }

    public TcpConnection(ILogAgent par1ILogAgent, Socket par2Socket, String par3Str, NetHandler par4NetHandler, PrivateKey par5PrivateKey) throws IOException {
        this.sendQueueLock = new Object();
        this.isRunning = true;
        this.readPackets = new ConcurrentLinkedQueue();
        this.dataPackets = Collections.synchronizedList(new ArrayList());

        //TODO ZoomCodeStart
        this.coreSettingsData = null;
        this.nettySettingsData = null;
        this.fastDataPackets = Collections.synchronizedList(new ArrayList());
        //TODO ZoomCodeEnd

        this.chunkDataPackets = Collections.synchronizedList(new ArrayList());
        this.terminationReason = "";
        this.chunkDataPacketsDelay = 50;
        this.field_74463_A = par5PrivateKey;
        this.networkSocket = par2Socket;
        this.tcpConLogAgent = par1ILogAgent;
        this.remoteSocketAddress = par2Socket.getRemoteSocketAddress();
        this.theNetHandler = par4NetHandler;

        try {
            par2Socket.setSoTimeout(30000);
            par2Socket.setTrafficClass(24);
        } catch (SocketException socketexception) {
            System.err.println(socketexception.getMessage());
        }

        this.socketInputStream = new DataInputStream(par2Socket.getInputStream());
        this.socketOutputStream = new DataOutputStream(new BufferedOutputStream(par2Socket.getOutputStream(), 5120));

        //TODO ZoomCodeStart
        if (this.coreSettingsData.isExecutorServiceConnections()) {
            this.service.execute(new TcpConnectionReader(this));
            this.service.execute(new TcpConnectionWriter(this));

            return;
        }
        //TODO ZoomCodeEnd

        this.readThread = new TcpReaderThread(this, par3Str + " read thread");
        this.writeThread = new TcpWriterThread(this, par3Str + " write thread");

        this.readThread.setPriority(Thread.MIN_PRIORITY);
        this.writeThread.setPriority(Thread.MIN_PRIORITY);

        this.readThread.start();
        this.writeThread.start();
    }

    @SideOnly(Side.CLIENT)
    public void closeConnections() {
        //TODO ZoomCodeStart
        if (this.nettySettingsData.isEnable()) return;
        //TODO ZoomCodeEnd

        this.wakeThreads();

        //TODO ZoomCodeStart
        if (this.coreSettingsData.isExecutorServiceConnections())
            return;
        //TODO ZoomCodeEnd

        this.writeThread = null;
        this.readThread = null;
    }

    /**
     * Sets the NetHandler for this NetworkManager. Server-only.
     */
    public void setNetHandler(NetHandler par1NetHandler) {
        this.theNetHandler = par1NetHandler;
    }

    /**
     * Adds the packet to the correct send queue (chunk data packets go to a separate queue).
     */
    public void addToSendQueue(Packet par1Packet) {
        //TODO ZoomCodeStart
        if (this.nettySettingsData.isEnable()) {
            this.socketChannel.writeAndFlush(par1Packet);
            return;
        }
        //TODO ZoomCodeEnd

        if (!this.isServerTerminating) {
            Object object = this.sendQueueLock;

            synchronized (this.sendQueueLock) {
                this.sendQueueByteLength += par1Packet.getPacketSize() + 1;
                this.dataPackets.add(par1Packet);
            }
        }
    }

    //TODO ZoomCodeStart
    public final void addToSendQueueFast(@NotNull final Packet packet) {
        if (this.nettySettingsData.isEnable()) {
            this.socketChannel.writeAndFlush(packet);
            return;
        }

        if (!this.isServerTerminating) {
            synchronized (this.sendQueueLock) {
                this.sendQueueByteLength += packet.getPacketSize() + 1;
                this.fastDataPackets.add(packet);
            }
        }
    }
    //TODO ZoomCodeEnd

    /**
     * Sends a data packet if there is one to send, or sends a chunk data packet if there is one and the counter is up,
     * or does nothing.
     */
    //TODO ZoomCodeReplace private on public
    public boolean sendPacket() {
        boolean flag = false;

        try {
            Packet packet;
            int i;
            int[] aint;

            //TODO ZoomCodeStart
            if (this.field_74468_e == 0 || !this.fastDataPackets.isEmpty() && MinecraftServer.getSystemTimeMillis() - ((Packet) this.fastDataPackets.get(0)).creationTimeMillis >= (long) this.field_74468_e) {
                packet = this.readFastPacket();

                if (packet != null) {
                    Packet.writePacket(packet, this.socketOutputStream);
                    aint = field_74467_d;
                    i = packet.getPacketId();
                    aint[i] += packet.getPacketSize() + 1;
                    flag = true;
                }
            }
            //TODO ZoomCodeEnd

            if (this.field_74468_e == 0 || !this.dataPackets.isEmpty() && MinecraftServer.getSystemTimeMillis() - ((Packet) this.dataPackets.get(0)).creationTimeMillis >= (long) this.field_74468_e) {
                packet = this.func_74460_a(false);

                if (packet != null) {
                    Packet.writePacket(packet, this.socketOutputStream);

                    if (packet instanceof Packet252SharedKey && !this.isOutputEncrypted) {
                        if (!this.theNetHandler.isServerHandler()) {
                            this.sharedKeyForEncryption = ((Packet252SharedKey) packet).getSharedKey();
                        }

                        this.encryptOuputStream();
                    }

                    aint = field_74467_d;
                    i = packet.getPacketId();
                    aint[i] += packet.getPacketSize() + 1;
                    flag = true;
                }
            }

            if (this.chunkDataPacketsDelay-- <= 0 && (this.field_74468_e == 0 || !this.chunkDataPackets.isEmpty() && MinecraftServer.getSystemTimeMillis() - ((Packet) this.chunkDataPackets.get(0)).creationTimeMillis >= (long) this.field_74468_e)) {
                packet = this.func_74460_a(true);

                if (packet != null) {
                    Packet.writePacket(packet, this.socketOutputStream);
                    aint = field_74467_d;
                    i = packet.getPacketId();
                    aint[i] += packet.getPacketSize() + 1;
                    this.chunkDataPacketsDelay = 0;
                    flag = true;
                }
            }

            return flag;
        } catch (Exception exception) {
            if (!this.isTerminating) {
                this.onNetworkError(exception);
            }

            return false;
        }
    }

    private Packet func_74460_a(boolean par1) {
        Packet packet = null;
        List list = par1 ? this.chunkDataPackets : this.dataPackets;
        Object object = this.sendQueueLock;

        synchronized (this.sendQueueLock) {
            while (!list.isEmpty() && packet == null) {
                packet = (Packet) list.remove(0);
                this.sendQueueByteLength -= packet.getPacketSize() + 1;

                if (this.func_74454_a(packet, par1)) {
                    packet = null;
                }
            }

            ProfilerSection.PACKET_OUTBOUND.start(packet); // Cauldron - mobius hook

            return packet;
        }
    }

    //TODO ZoomCodeStart
    private @Nullable Packet readFastPacket() {
        final List list = this.fastDataPackets;
        Packet packet = null;

        synchronized (this.sendQueueLock) {
            while (!list.isEmpty() && packet == null) {
                packet = (Packet) list.remove(0);
                this.sendQueueByteLength -= packet.getPacketSize() + 1;

                if (this.containsSameEntityIDAs(packet)) {
                    packet = null;
                }
            }

            ProfilerSection.PACKET_OUTBOUND.start(packet); // Cauldron - mobius hook

            return packet;
        }
    }

    private boolean containsSameEntityIDAs(@NotNull final Packet packet) {
        if (!packet.isRealPacket()) {
            return false;
        } else {
            List list = this.fastDataPackets;
            Iterator iterator = list.iterator();
            Packet packet1;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                packet1 = (Packet) iterator.next();
            }
            while (packet1.getPacketId() != packet.getPacketId());

            return packet.containsSameEntityIDAs(packet1);
        }
    }
    //TODO ZoomCodeEnd

    private boolean func_74454_a(Packet par1Packet, boolean par2) {
        if (!par1Packet.isRealPacket()) {
            return false;
        } else {
            List list = par2 ? this.chunkDataPackets : this.dataPackets;
            Iterator iterator = list.iterator();
            Packet packet1;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                packet1 = (Packet) iterator.next();
            }
            while (packet1.getPacketId() != par1Packet.getPacketId());

            return par1Packet.containsSameEntityIDAs(packet1);
        }
    }

    /**
     * Wakes reader and writer threads
     */
    public void wakeThreads() {

        //TODO ZoomCodeStart
        if (this.nettySettingsData.isEnable()) return;
        if (this.coreSettingsData.isExecutorServiceConnections())
            return;
        //TODO ZoomCodeEnd

        if (this.readThread != null) {
            this.readThread.interrupt();
        }

        if (this.writeThread != null) {
            this.writeThread.interrupt();
        }
    }

    /**
     * Reads a single packet from the input stream and adds it to the read queue. If no packet is read, it shuts down
     * the network.
     */
    //TODO ZoomCodeReplace private on public
    public boolean readPacket() {
        boolean flag = false;

        try {
            Packet packet = Packet.readPacket(this.tcpConLogAgent, this.socketInputStream, this.theNetHandler.isServerHandler(), this.networkSocket);
            ProfilerSection.PACKET_INBOUND.start(packet); // Cauldron - mobius hook
            if (packet != null) {
                if (packet instanceof Packet252SharedKey && !this.isInputBeingDecrypted) {
                    if (this.theNetHandler.isServerHandler()) {
                        this.sharedKeyForEncryption = ((Packet252SharedKey) packet).getSharedKey(this.field_74463_A);
                    }

                    this.decryptInputStream();
                }

                int[] aint = field_74470_c;
                int i = packet.getPacketId();
                aint[i] += packet.getPacketSize() + 1;

                if (!this.isServerTerminating) {
                    if (packet.canProcessAsync() && this.theNetHandler.canProcessPacketsAsync()) {
                        this.field_74490_x = 0;

                        packet.processPacket(this.theNetHandler);
                    } else {
                        this.readPackets.add(packet);
                    }
                }

                flag = true;
            } else {
                this.networkShutdown("disconnect.endOfStream", new Object[0]);
            }

            return flag;
        } catch (Exception exception) {
            if (!this.isTerminating) {
                this.onNetworkError(exception);
            }

            return false;
        }
    }

    /**
     * Used to report network errors and causes a network shutdown.
     */
    //TODO ZoomCodeReplace private on public
    public void onNetworkError(Exception par1Exception) {
        par1Exception.printStackTrace(); // CraftBukkit - Remove console spam
        this.networkShutdown("disconnect.genericReason", "Internal exception: " + par1Exception.toString());
    }

    /**
     * Shuts down the network with the specified reason. Closes all streams and sockets, spawns NetworkMasterThread to
     * stop reading and writing threads.
     */
    public void networkShutdown(String par1Str, Object... par2ArrayOfObj) {
        //TODO ZeyCodeStart
        if (this.nettySettingsData.isEnable()) {
            this.terminationReason = par1Str;
            this.shutdownDescription = par2ArrayOfObj;
            this.socketChannel.close();
        } else
            //TODO ZeyCodeEnd

            if (this.isRunning) {
                this.isTerminating = true;
                this.terminationReason = par1Str;
                this.shutdownDescription = par2ArrayOfObj;
                this.isRunning = false;

                //TODO ZoomCodeStart
                if (!this.coreSettingsData.isExecutorServiceConnections())
                    //TODO ZoomCodeEnd

                    (new TcpMasterThread(this)).start();

                try {
                    this.socketInputStream.close();
                } catch (Throwable throwable) {
                    ;
                }

                try {
                    this.socketOutputStream.close();
                } catch (Throwable throwable1) {
                    ;
                }

                try {
                    this.networkSocket.close();
                } catch (Throwable throwable2) {
                    ;
                }

                this.socketInputStream = null;
                this.socketOutputStream = null;
                this.networkSocket = null;
            }
    }

    /**
     * Checks timeouts and processes all pending read packets.
     */
    public void processReadPackets() {

        //TODO ZoomCodeStart
        if (!this.coreSettingsData.isIgnoreSendQueueByteLength())
            //TODO ZoomCodeEnd

            if (this.sendQueueByteLength > 2097152) {
                // Cauldron start - better debug
                MinecraftServer.getServer().logInfo("Send queue byte length exceeded 2097152 bytes. " + this.sendQueueByteLength + " bytes were queued.");
                MinecraftServer.getServer().logInfo("Dumping current data packets in queue...");
                for (Object dataPacket : this.dataPackets) {
                    if (dataPacket instanceof Packet250CustomPayload) {
                        Packet250CustomPayload packet = (Packet250CustomPayload) dataPacket;
                        MinecraftServer.getServer().logInfo("[Packet: " + packet + "][Channel: " + packet.channel + "][size: " + packet.getPacketSize() + "]");
                    }
                }
                // Cauldron end
                this.networkShutdown("disconnect.overflow", new Object[0]);
            }

        if (this.readPackets.isEmpty()) {
            if (this.field_74490_x++ == 1200) {
                this.networkShutdown("disconnect.timeout", new Object[0]);
            }
        } else {
            this.field_74490_x = 0;
        }

        int i = 1000;

        while (i-- >= 0) {
            Packet packet = this.readPackets.poll();

            if (packet != null && !this.theNetHandler.isConnectionClosed()) {
                packet.processPacket(this.theNetHandler);
            }
        }

        this.wakeThreads();

        //TODO ZeyCodeStart
        if (this.nettySettingsData.isEnable())
            this.isTerminating = (this.socketChannel == null || !this.socketChannel.isActive());
        //TODO ZeyCodeEnd

        if (this.isTerminating && this.readPackets.isEmpty()) {
            this.theNetHandler.handleErrorMessage(this.terminationReason, this.shutdownDescription);
            FMLNetworkHandler.onConnectionClosed(this, this.theNetHandler.getPlayer());
        }
    }

    /**
     * Return the InetSocketAddress of the remote endpoint
     */
    public SocketAddress getSocketAddress() {
        return this.remoteSocketAddress;
    }

    /**
     * Shuts down the server. (Only actually used on the server)
     */
    public void serverShutdown() {
        //TODO ZeyCodeStart
        if (this.nettySettingsData.isEnable())
            this.socketChannel.close();
        else
            //TODO ZeyCodeEnd

            if (!this.isServerTerminating) {
                this.wakeThreads();
                this.isServerTerminating = true;

                //TODO ZoomCodeStart
                if (this.coreSettingsData.isExecutorServiceConnections())
                    return;
                //TODO ZoomCodeEnd

                this.readThread.interrupt();
                (new TcpMonitorThread(this)).start();
            }
    }

    //TODO ZeyCodeModified from private to public
    public void decryptInputStream() throws IOException {
        //TODO ZoomCodeStart
        if (this.nettySettingsData.isEnable()) {
            this.socketChannel
                    .pipeline()
                    .addFirst("decrypt",
                            new NettyEncryptingDecoder(
                                    cipher(
                                            2,
                                            this.sharedKeyForEncryption
                                    )
                            )
                    );
            return;
        }
        //TODO ZoomCodeEnd

        this.isInputBeingDecrypted = true;
        InputStream inputstream = this.networkSocket.getInputStream();
        this.socketInputStream = new DataInputStream(CryptManager.decryptInputStream(this.sharedKeyForEncryption, inputstream));
    }

    /**
     * flushes the stream and replaces it with an encryptedOutputStream
     */
    //TODO ZeyCodeModified from private to public
    public void encryptOuputStream() throws IOException {
        //TODO ZoomCodeStart
        if (this.nettySettingsData.isEnable()) {
            this.socketChannel
                    .pipeline()
                    .addFirst("encrypt",
                            new NettyEncryptingEncoder(
                                    cipher(
                                            1,
                                            this.sharedKeyForEncryption
                                    )
                            )
                    );
            return;
        }
        //TODO ZoomCodeEnd

        this.socketOutputStream.flush();
        this.isOutputEncrypted = true;

        BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(CryptManager.encryptOuputStream(this.sharedKeyForEncryption, this.networkSocket.getOutputStream()), 5120);
        this.socketOutputStream = new DataOutputStream(bufferedoutputstream);
    }

    /**
     * returns 0 for memoryConnections
     */
    public int packetSize() {
        return this.chunkDataPackets.size();
    }

    public Socket getSocket() {
        return this.networkSocket;
    }

    /**
     * Whether the network is operational.
     */
    static boolean isRunning(TcpConnection par0TcpConnection) {
        return par0TcpConnection.isRunning;
    }

    /**
     * Is the server terminating? Client side aways returns false.
     */
    static boolean isServerTerminating(TcpConnection par0TcpConnection) {
        return par0TcpConnection.isServerTerminating;
    }

    /**
     * Static accessor to readPacket.
     */
    static boolean readNetworkPacket(TcpConnection par0TcpConnection) {
        return par0TcpConnection.readPacket();
    }

    /**
     * Static accessor to sendPacket.
     */
    static boolean sendNetworkPacket(TcpConnection par0TcpConnection) {
        return par0TcpConnection.sendPacket();
    }

    static DataOutputStream getOutputStream(TcpConnection par0TcpConnection) {
        return par0TcpConnection.socketOutputStream;
    }

    /**
     * Gets whether the Network manager is terminating.
     */
    static boolean isTerminating(TcpConnection par0TcpConnection) {
        return par0TcpConnection.isTerminating;
    }

    /**
     * Sends the network manager an error
     */
    static void sendError(TcpConnection par0TcpConnection, Exception par1Exception) {
        par0TcpConnection.onNetworkError(par1Exception);
    }

    /**
     * Returns the read thread.
     */
    static Thread getReadThread(TcpConnection par0TcpConnection) {
        return par0TcpConnection.readThread;
    }

    /**
     * Returns the write thread.
     */
    static Thread getWriteThread(TcpConnection par0TcpConnection) {
        return par0TcpConnection.writeThread;
    }

    public void setSocketAddress(SocketAddress address) {
        remoteSocketAddress = address;    // Spigot
    }

    //TODO ZeyCodeStart
    @NotNull
    public static Cipher cipher(final int paramInt, @NotNull final Key paramKey) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");

            cipher.init(paramInt, paramKey, new IvParameterSpec(paramKey.getEncoded()));

            return cipher;
        } catch (GeneralSecurityException generalSecurityException) {
            throw new RuntimeException(generalSecurityException);
        }
    }
    //TODO ZeyCodeEnd
}