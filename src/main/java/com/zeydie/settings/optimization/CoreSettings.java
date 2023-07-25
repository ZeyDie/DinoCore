package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class CoreSettings extends AbstractSettings {
    @NotNull
    public CoreSettings.CoreSettingsData coreSettingsData = new CoreSettingsData();

    public static @NotNull CoreSettings getInstance() {
        return MinecraftServer.getServer().coreSettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "core";
    }

    @Override
    public @NotNull CoreSettings.CoreSettingsData getSettings() {
        return this.coreSettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.coreSettingsData = (CoreSettingsData) object;
    }

    @Data
    public static final class CoreSettingsData {
        private boolean debug = true;
        private int tps = 20;
        private boolean autoSaveAllWorlds = false;
        private boolean reloadCommand = true;
        private boolean executorServiceConnections = false;
        private boolean shuffleConnections = false;
        private boolean asynchronousWarnings = false;
        private boolean fastBukkit = true;
        private boolean unbindPort = true;
        private boolean pvp1_12_2 = false;
        private boolean ignoreSendQueueByteLength = true;
        private boolean addNBTBlockPlacePlayer = true;
    }
}
