package net.minecraft.network;

import com.zeydie.settings.optimization.CoreSettings;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public abstract class NetworkListenThread {
    /**
     * Reference to the MinecraftServer object.
     */
    private final MinecraftServer mcServer;
    private final List connections = Collections.synchronizedList(new ArrayList());

    /**
     * Whether the network listener object is listening.
     */
    public volatile boolean isListening;

    public NetworkListenThread(final MinecraftServer par1MinecraftServer) throws IOException {
        this.mcServer = par1MinecraftServer;
        this.isListening = true;
    }

    /**
     * adds this connection to the list of currently connected players
     */
    public void addPlayer(final NetServerHandler par1NetServerHandler) {
        this.connections.add(par1NetServerHandler);
    }

    public void stopListening() {
        this.isListening = false;
    }

    /**
     * processes packets and pending connections
     */
    public void networkTick() {

        //TODO ZoomCodeStart
        if (CoreSettings.getInstance().getSettings().isShuffleConnections())
            Collections.shuffle(this.connections);
        //TODO ZoomCodeEnd

        for (int i = 0; i < this.connections.size(); ++i) {
            final NetServerHandler netserverhandler = (NetServerHandler) this.connections.get(i);

            try {
                netserverhandler.networkTick();
            } catch (final Exception exception) {
                if (netserverhandler.netManager instanceof MemoryConnection) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                    crashreportcategory.addCrashSectionCallable("Connection", new CallableConnectionName(this, netserverhandler));
                    throw new ReportedException(crashreport);
                }

                FMLLog.log(Level.SEVERE, exception, "A critical server error occured handling a packet, kicking %s", netserverhandler.getPlayer().entityId);
                this.mcServer.getLogAgent().logWarningException("Failed to handle packet for " + netserverhandler.playerEntity.getEntityName() + "/" + netserverhandler.playerEntity.getPlayerIP() + ": " + exception, exception);
                netserverhandler.kickPlayerFromServer("Internal server error");
            }

            if (netserverhandler.disconnected) {
                this.connections.remove(i--);
            }

            netserverhandler.netManager.wakeThreads();
        }
    }

    public MinecraftServer getServer() {
        return this.mcServer;
    }
}
