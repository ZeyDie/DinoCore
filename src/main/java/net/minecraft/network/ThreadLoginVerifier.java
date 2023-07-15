package net.minecraft.network;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.util.CryptManager;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import ru.zoom4ikdan4ik.settings.auth.AuthSettings;

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

    ThreadLoginVerifier(NetLoginHandler pendingconnection, CraftServer server) {
        super("Login Verifier - " + pendingconnection.getUsernameAndAddress());
        this.server = server;
        // CraftBukkit end
        this.loginHandler = pendingconnection;
    }

    private boolean auth() throws java.io.IOException {
        String s = (new BigInteger(Objects.requireNonNull(CryptManager.getServerIdHash(NetLoginHandler.getServerId(this.loginHandler), NetLoginHandler.getLoginMinecraftServer(this.loginHandler).getKeyPair().getPublic(), NetLoginHandler.getSharedKey(this.loginHandler))))).toString(16);

        //TODO ZoomCodeStart
        final AuthSettings.AuthSettingsGson authSettingsGson = AuthSettings.getInstance();

        if (authSettingsGson.enable) {
            final String username = URLEncoder.encode(NetLoginHandler.getClientUsername(this.loginHandler), "UTF-8");
            final String serverID = URLEncoder.encode(s, "UTF-8");

            try {
                //TODO ZoomCodeStart
                return com.mojang.authlib.yggdrasil.CompatBridge.checkServer(username, serverID) != null;
                //TODO ZoomCodeEnd
                //TODO ZoomCodeClear
                //return com.mojang.authlib.yggdrasil.LegacyBridge.checkServer(username, serverID);
            } catch (Throwable exception) {
                final String site = String.format(authSettingsGson.getAuthRequest(), username, serverID);

                if (authSettingsGson.debug)
                    FMLLog.info("Connection to %s...", site);

                final URL url = new URL(site);
                final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openConnection(NetLoginHandler.getLoginMinecraftServer(this.loginHandler).getServerProxy()).getInputStream()));
                final String s1 = bufferedreader.readLine();

                bufferedreader.close();

                if (authSettingsGson.debug)
                    FMLLog.info("Result: %s", s1);

                if (!authSettingsGson.success.equals(s1)) {
                    this.loginHandler.raiseErrorAndDisconnect("Failed to verify username!");
                    return false;
                }
            }

            return this.loginHandler.getSocket() != null;
        }
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
                final Waitable<PlayerPreLoginEvent.Result> waitable = new ru.zoom4ikdan4ik.legacy.core.waitables.WaitablePlayerPreLogin(this, event);
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

        //TODO ZoomCodeStart
        finally {
            this.stop();
        }
        //TODO ZoomCodeEnd
    }
}
