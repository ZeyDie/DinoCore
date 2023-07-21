package net.minecraft.network;

import com.zeydie.legacy.core.waitables.WaitablePlayerPreLogin;
import com.zeydie.modified.CustomLoginVerified;
import com.zeydie.settings.optimization.AuthSettings;
import net.minecraft.util.CryptManager;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

// CraftBukkit start
// CraftBukkit end

//TODO ZoomCodeReplace private on public
public class ThreadLoginVerifier extends Thread {
    /**
     * The login handler that spawned this thread.
     */
    final NetLoginHandler loginHandler;

    // CraftBukkit start
    //TODO ZoomCodeReplace private on public
    public CraftServer server;

    // Cauldron start - vanilla compatibility
    ThreadLoginVerifier(NetLoginHandler pendingconnection) {
        this(pendingconnection, (CraftServer) org.bukkit.Bukkit.getServer());
    }
    // Cauldron end

    public ThreadLoginVerifier(NetLoginHandler pendingconnection, CraftServer server) {
        super("Login Verifier - " + pendingconnection.getUsernameAndAddress());
        this.server = server;
        // CraftBukkit end
        this.loginHandler = pendingconnection;
    }

    private boolean auth() throws java.io.IOException {
        String s = (new BigInteger(Objects.requireNonNull(CryptManager.getServerIdHash(NetLoginHandler.getServerId(this.loginHandler), NetLoginHandler.getLoginMinecraftServer(this.loginHandler).getKeyPair().getPublic(), NetLoginHandler.getSharedKey(this.loginHandler))))).toString(16);

        //TODO ZoomCodeStart
        if (AuthSettings.getInstance().getSettings().isEnable())
            return CustomLoginVerified.auth(this.loginHandler, s);
        //TODO ZoomCodeEnd

        URL url = new URL("http://session.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(NetLoginHandler.getClientUsername(this.loginHandler), "UTF-8") + "&serverId=" + URLEncoder.encode(s, "UTF-8"));
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openConnection(NetLoginHandler.getLoginMinecraftServer(this.loginHandler).getServerProxy()).getInputStream()));
        String s1 = bufferedreader.readLine();
        bufferedreader.close();

        if (!"YES".equals(s1)) {
            this.loginHandler.raiseErrorAndDisconnect("Failed to verify username!");
            return false;
        }

        // CraftBukkit start
        return this.loginHandler.getSocket() != null;
    }

    public void run() {
        try {
            if (org.spigotmc.SpamHaus.filterIp(loginHandler)) return; // Spigot
            if (server.getOnlineMode() && !auth()) return; // Spigot

            AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(NetLoginHandler.getClientUsername(this.loginHandler), this.loginHandler.getSocket().getInetAddress());
            this.server.getPluginManager().callEvent(asyncEvent);

            if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
                final PlayerPreLoginEvent event = new PlayerPreLoginEvent(NetLoginHandler.getClientUsername(this.loginHandler), this.loginHandler.getSocket().getInetAddress());

                if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                    event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
                }

                //TODO ZoomCodeStart
                final Waitable<PlayerPreLoginEvent.Result> waitable = new WaitablePlayerPreLogin(this, event);
                //TODO ZoomCodeEnd
                //TODO ZoomCodeClear
                /*Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                    @Override
                    protected PlayerPreLoginEvent.Result evaluate() {
                        ThreadLoginVerifier.this.server.getPluginManager().callEvent(event);
                        return event.getResult();
                    }
                };*/

                NetLoginHandler.getLoginMinecraftServer(this.loginHandler).processQueue.add(waitable);

                if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                    this.loginHandler.raiseErrorAndDisconnect(event.getKickMessage());
                    return;
                }
            } else {
                if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                    this.loginHandler.raiseErrorAndDisconnect(asyncEvent.getKickMessage());
                    return;
                }
            }

            // CraftBukkit end
            NetLoginHandler.func_72531_a(this.loginHandler, true);
            // CraftBukkit start
        } catch (java.io.IOException exception) {
            this.loginHandler.raiseErrorAndDisconnect("Failed to verify username, session authentication server unavailable!");
        } catch (Exception exception) {
            this.loginHandler.raiseErrorAndDisconnect("Failed to verify username!");
            server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + NetLoginHandler.getClientUsername(this.loginHandler), exception);
            // CraftBukkit end
        }
    }
}
