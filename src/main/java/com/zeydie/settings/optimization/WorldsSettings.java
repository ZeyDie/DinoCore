package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class WorldsSettings extends AbstractSettings {
    @NotNull
    private WorldsSettingsGson worldsSettingsGson = new WorldsSettingsGson();

    public static @NotNull WorldsSettingsGson getInstance() {
        return MinecraftServer.getServer().worldsSettings.getSettings();
    }

    @Override
    public @NotNull String getConfigName() {
        return "worlds";
    }

    @Override
    public @NotNull WorldsSettingsGson getSettings() {
        return this.worldsSettingsGson;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.worldsSettingsGson = (WorldsSettingsGson) object;
    }

    @Data
    public static final class WorldsSettingsGson implements ITickRunnable {
        private boolean enable = true;
        private int tickRate = 100;
        private boolean waterBiomes = false;
        @NotNull
        private List<String> whitelistUnloadingWorlds = new ArrayList<>();
        @NotNull
        private DebugSettings debugSettings = new DebugSettings();
    }
}
