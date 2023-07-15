package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.IGson;
import lombok.Data;
import net.minecraft.server.MinecraftServer;

public final class AuthSettings extends AbstractSettings {
    public AuthSettingsGson authSettingsGson = new AuthSettingsGson();

    public static AuthSettingsGson getInstance() {
        return MinecraftServer.getServer().authSettings.getSettings();
    }

    @Override
    public String getConfigName() {
        return "authorization";
    }

    @Override
    public AuthSettingsGson getSettings() {
        return this.authSettingsGson;
    }

    @Override
    public void setSettings(final IGson object) {
        this.authSettingsGson = (AuthSettingsGson) object;
    }

    @Data
    public static final class AuthSettingsGson implements IGson {
        private boolean enable = true;
        private String url = "http://session.minecraft.net";
        private String path = "game";
        private String file = "checkserver.jsp";
        private String query = "user=%s&serverId=%s";
        private String success = "YES";
        private boolean debug = false;

        public String getAuthRequest() {
            return String.format("%s/%s/%s?%s", this.url, this.path, this.file, this.query).replaceAll("//", "/");
        }
    }
}
