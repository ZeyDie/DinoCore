package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class AuthSettings extends AbstractSettings {
    @NotNull
    public AuthSettings.AuthSettingsData authSettingsData = new AuthSettingsData();

    public static @NotNull AuthSettings getInstance() {
        return MinecraftServer.getServer().authSettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "authorization";
    }

    @Override
    public @NotNull AuthSettings.AuthSettingsData getSettings() {
        return this.authSettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.authSettingsData = (AuthSettingsData) object;
    }

    @Data
    public static final class AuthSettingsData {
        private boolean debug;
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

        @NotNull
        public String getAuthRequest() {
            return String.format("%s/%s/%s?%s", this.url, this.path, this.file, this.query).replaceAll("//", "/");
        }
    }
}
