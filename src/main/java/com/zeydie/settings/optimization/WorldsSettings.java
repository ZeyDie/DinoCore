package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.IGson;
import com.zeydie.settings.interfaces.ITickRunnable;
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
    public void setSettings(final IGson object) {
        this.worldsSettingsGson = (WorldsSettingsGson) object;
    }

    public static final class WorldsSettingsGson implements IGson, ITickRunnable {
        public boolean enable = true;
        public int tickRate = 100;

        @Override
        public int getTickRate() {
            return this.tickRate;
        }

        @Override
        public DebugSettings getDebugSettings() {
            return new DebugSettings();
        }
    }
}
