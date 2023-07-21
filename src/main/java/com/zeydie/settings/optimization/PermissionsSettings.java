package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class PermissionsSettings extends AbstractSettings {
    @NotNull
    public PermissionsSettingsGson permissionsSettingsGson = new PermissionsSettingsGson();

    public static @NotNull PermissionsSettingsGson getInstance() {
        return MinecraftServer.getServer().permissionsSettings.getSettings();
    }

    @Override
    public @NotNull String getConfigName() {
        return "permissions";
    }

    @Override
    public @NotNull PermissionsSettingsGson getSettings() {
        return this.permissionsSettingsGson;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.permissionsSettingsGson = (PermissionsSettingsGson) object;
    }

    @Data
    public static final class PermissionsSettingsGson {
        private boolean opsOnlyFromConsole = true;
    }
}
