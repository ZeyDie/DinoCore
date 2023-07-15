package ru.zoom4ikdan4ik.settings.auth;

import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;

public final class AuthSettings extends AbstractSettings {
    public AuthSettingsGson authSettingsGson = new AuthSettingsGson();

    @Override
    public final String getConfigName() {
        return "authorization";
    }

    @Override
    public final void setSettings(final IGson object) {
        this.authSettingsGson = (AuthSettingsGson) object;
    }

    @Override
    public final AuthSettingsGson getSettings() {
        return this.authSettingsGson;
    }

    public static AuthSettingsGson getInstance() {
        return MinecraftServer.getServer().authSettings.getSettings();
    }

    public final class AuthSettingsGson implements IGson {
        public boolean enable = true;
        public String url = "http://session.minecraft.net";
        public String path = "game";
        public String file = "checkserver.jsp";
        public String query = "user=%s&serverId=%s";
        public String success = "YES";
        public boolean debug = false;

        public final String getAuthRequest() {
            return String.format("%s/%s/%s?%s", this.url, this.path, this.file, this.query).replaceAll("//", "/");
        }
    }
}
