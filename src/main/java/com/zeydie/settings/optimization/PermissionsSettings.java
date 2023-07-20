package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;

public final class PermissionsSettings extends AbstractSettings {
    public PermissionsSettingsGson permissionsSettingsGson = new PermissionsSettingsGson();

    public static PermissionsSettingsGson getInstance() {
        return MinecraftServer.getServer().permissionsSettings.getSettings();
    }

    @Override
    public String getConfigName() {
        return "permissions";
    }

    @Override
    public PermissionsSettingsGson getSettings() {
        return this.permissionsSettingsGson;
    }

    @Override
    public void setSettings(final Object object) {
        this.permissionsSettingsGson = (PermissionsSettingsGson) object;
    }

    @Data
    public static final class PermissionsSettingsGson {
        private boolean opsOnlyFromConsole = true;
    }
}
