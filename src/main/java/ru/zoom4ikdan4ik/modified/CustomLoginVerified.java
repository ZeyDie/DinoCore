package ru.zoom4ikdan4ik.modified;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.network.NetLoginHandler;
import ru.zoom4ikdan4ik.settings.auth.AuthSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public final class CustomLoginVerified {

    public static boolean auth(final NetLoginHandler netLoginHandler, final String request) throws IOException {
        final AuthSettings.AuthSettingsGson authSettingsGson = AuthSettings.getInstance();

        final String username = URLEncoder.encode(NetLoginHandler.getClientUsername(netLoginHandler), "UTF-8");
        final String serverID = URLEncoder.encode(request, "UTF-8");

        try {
            return com.mojang.authlib.yggdrasil.LegacyBridge.checkServer(username, serverID);
        } catch (Throwable exception) {
            final String site = String.format(authSettingsGson.getAuthRequest(), username, serverID);

            if (authSettingsGson.debug)
                FMLLog.info("Connection to %s...", site);

            final URL url = new URL(site);
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openConnection(NetLoginHandler.getLoginMinecraftServer(netLoginHandler).getServerProxy()).getInputStream()));
            final String result = bufferedreader.readLine();

            bufferedreader.close();

            if (authSettingsGson.debug)
                FMLLog.info("Result: %s", result);

            if (!authSettingsGson.success.equals(result)) {
                netLoginHandler.raiseErrorAndDisconnect("Failed to verify username!");

                return false;
            }

            return netLoginHandler.getSocket() != null;
        }
    }
}
