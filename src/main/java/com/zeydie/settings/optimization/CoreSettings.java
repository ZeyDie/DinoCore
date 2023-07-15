package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.IGson;
import lombok.Data;
import net.minecraft.server.MinecraftServer;

public final class CoreSettings extends AbstractSettings {
    public CoreSettingsGson coreSettingsGson = new CoreSettingsGson();

    public static CoreSettingsGson getInstance() {
        return MinecraftServer.getServer().coreSettings.getSettings();
    }

    @Override
    public String getConfigName() {
        return "core";
    }

    @Override
    public CoreSettingsGson getSettings() {
        return this.coreSettingsGson;
    }

    @Override
    public void setSettings(final IGson object) {
        this.coreSettingsGson = (CoreSettingsGson) object;
    }

    @Data
    public static final class CoreSettingsGson implements IGson {
        private boolean debug = true;
        private boolean executorServiceConnections = false;
        private boolean shuffleConnections = true;
        private boolean disableAsynchronousWarnings = true;
        private boolean enableFastBukkit = true;
        private boolean unbindBindedPort = true;
        private boolean pvpFromNewVersions = false;
        private boolean ignoreSendQueueByteLength = true;
        private boolean enableWarmRoast = true;
        private String[] warmRoastParams = new String[]{"--port", "25565"};
    }
}
