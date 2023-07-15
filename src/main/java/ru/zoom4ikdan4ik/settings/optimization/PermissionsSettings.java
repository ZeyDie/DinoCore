package ru.zoom4ikdan4ik.settings.optimization;

import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;

public final class PermissionsSettings extends AbstractSettings {
    public PermissionsSettingsGson permissionsSettingsGson = new PermissionsSettingsGson();

    @Override
    public final String getConfigName() {
        return "permissions";
    }

    @Override
    public final void setSettings(final IGson object) {
        this.permissionsSettingsGson = (PermissionsSettingsGson) object;
    }

    @Override
    public final PermissionsSettingsGson getSettings() {
        return this.permissionsSettingsGson;
    }

    public static PermissionsSettingsGson getInstance() {
        return MinecraftServer.getServer().permissionsSettings.getSettings();
    }

    public static final class PermissionsSettingsGson implements IGson {
        public boolean opsOnlyFromConsole = true;
    }
}
