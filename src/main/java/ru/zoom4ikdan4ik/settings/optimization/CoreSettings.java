package ru.zoom4ikdan4ik.settings.optimization;

import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;

public final class CoreSettings extends AbstractSettings {
    public CoreSettingsGson coreSettingsGson = new CoreSettingsGson();

    @Override
    public final String getConfigName() {
        return "core";
    }

    @Override
    public final void setSettings(final IGson object) {
        this.coreSettingsGson = (CoreSettingsGson) object;
    }

    @Override
    public final CoreSettingsGson getSettings() {
        return this.coreSettingsGson;
    }

    public static CoreSettingsGson getInstance() {
        return MinecraftServer.getServer().coreSettings.getSettings();
    }

    public static final class CoreSettingsGson implements IGson {
        public boolean executorServiceConnections = false;
        public boolean shuffleConnections = true;
        public boolean disableAsynchronousWarnings = true;
        public boolean enableFastBukkit = true;
        public boolean unbindBindedPort = true;
        public boolean pvpFromNewVersions = false;
        public boolean ignoreSendQueueByteLength = true;
        public boolean enableWarmRoast = true;
        public String[] warmRoastParams = new String[]{"--port", "23000"};
    }
}
