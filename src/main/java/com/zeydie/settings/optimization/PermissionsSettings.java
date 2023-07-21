package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class PermissionsSettings extends AbstractSettings {
    @NotNull
    public PermissionsSettings.PermissionsSettingsData permissionsSettingsData = new PermissionsSettingsData();

    public static @NotNull PermissionsSettings getInstance() {
        return MinecraftServer.getServer().permissionsSettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "permissions";
    }

    @Override
    public @NotNull PermissionsSettings.PermissionsSettingsData getSettings() {
        return this.permissionsSettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.permissionsSettingsData = (PermissionsSettingsData) object;
    }

    @Data
    public static final class PermissionsSettingsData {
        private boolean opsOnlyFromConsole = true;
    }
}
