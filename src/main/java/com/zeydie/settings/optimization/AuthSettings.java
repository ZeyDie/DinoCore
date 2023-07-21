package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class AuthSettings extends AbstractSettings {
    @NotNull
    public AuthSettingsGson authSettingsGson = new AuthSettingsGson();

    public static @NotNull AuthSettingsGson getInstance() {
        return MinecraftServer.getServer().authSettings.getSettings();
    }

    @Override
    public @NotNull String getConfigName() {
        return "authorization";
    }

    @Override
    public @NotNull AuthSettingsGson getSettings() {
        return this.authSettingsGson;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.authSettingsGson = (AuthSettingsGson) object;
    }

    @Data
    public static final class AuthSettingsGson {
        private boolean enable = true;
        @NotNull
        private String url = "http://session.minecraft.net";
        @NotNull
        private String path = "game";
        @NotNull
        private String file = "checkserver.jsp";
        @NotNull
        private String query = "user=%s&serverId=%s";
        @NotNull
        private String success = "YES";
        private boolean debug = false;

        @NotNull
        public String getAuthRequest() {
            return String.format("%s/%s/%s?%s", this.url, this.path, this.file, this.query).replaceAll("//", "/");
        }
    }
}
