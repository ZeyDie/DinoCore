package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class WorldsSettings extends AbstractSettings {
    @NotNull
    private WorldsSettings.WorldsSettingsData worldsSettingsData = new WorldsSettingsData();

    public static @NotNull WorldsSettings getInstance() {
        return MinecraftServer.getServer().worldsSettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "worlds";
    }

    @Override
    public @NotNull WorldsSettings.WorldsSettingsData getSettings() {
        return this.worldsSettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.worldsSettingsData = (WorldsSettingsData) object;
    }

    @Data
    public static final class WorldsSettingsData implements ITickRunnable {
        @NotNull
        private DebugSettings debugSettings = new DebugSettings();
        private int tickRate = 100;

        private boolean enable = true;
        private boolean waterBiomes = false;
        @NotNull
        private WorldsSettingsDataUnloading worldsSettingsDataUnloading = new WorldsSettingsDataUnloading();
    }

    @Data
    public static final class WorldsSettingsDataUnloading implements ITickRunnable {
        @NotNull
        private DebugSettings debugSettings = new DebugSettings();
        private int tickRate = 6000;

        private boolean unloadingWorlds = false;
        @NotNull
        private Map<String, WorldUnloadingData> worldData = this.getDefaultWorldData();


        private @NotNull Map<String, WorldUnloadingData> getDefaultWorldData() {
            final Map<String, WorldUnloadingData> data = new HashMap<>();

            data.put("world", new WorldUnloadingData(true, true, true));

            return data;
        }

        @Data
        @AllArgsConstructor
        public static final class WorldUnloadingData {
            private boolean enable = true;
            private boolean keepLoaded = false;
            private boolean unloadIfNoPlayers = true;
        }
    }
}
