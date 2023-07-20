package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import lombok.Data;
import net.minecraft.server.MinecraftServer;

public final class WorldsSettings extends AbstractSettings {
    private WorldsSettingsGson worldsSettingsGson = new WorldsSettingsGson();

    public static WorldsSettingsGson getInstance() {
        return MinecraftServer.getServer().worldsSettings.getSettings();
    }

    @Override
    public String getConfigName() {
        return "worlds";
    }

    @Override
    public WorldsSettingsGson getSettings() {
        return this.worldsSettingsGson;
    }

    @Override
    public void setSettings(final Object object) {
        this.worldsSettingsGson = (WorldsSettingsGson) object;
    }

    @Data
    public static final class WorldsSettingsGson implements ITickRunnable {
        private boolean enable = true;
        private int tickRate = 100;
        private boolean waterBiomes = false;
        private DebugSettings debugSettings = new DebugSettings();
    }
}
