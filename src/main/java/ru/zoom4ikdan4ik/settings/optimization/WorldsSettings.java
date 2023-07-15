package ru.zoom4ikdan4ik.settings.optimization;

import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;
import ru.zoom4ikdan4ik.settings.interfaces.ITickRunnable;

public final class WorldsSettings extends AbstractSettings {
    private WorldsSettingsGson worldsSettingsGson = new WorldsSettingsGson();

    @Override
    public final String getConfigName() {
        return "worlds";
    }

    @Override
    public final void setSettings(final IGson object) {
        this.worldsSettingsGson = (WorldsSettingsGson) object;
    }

    @Override
    public final WorldsSettingsGson getSettings() {
        return this.worldsSettingsGson;
    }

    public static WorldsSettingsGson getInstance() {
        return MinecraftServer.getServer().worldsSettings.getSettings();
    }

    public static final class WorldsSettingsGson implements IGson, ITickRunnable {
        public boolean enable = true;
        public int tickRate = 100;

        @Override
        public final int getTickRate() {
            return this.tickRate;
        }

        @Override
        public DebugSettings getDebugSettings() {
            return new DebugSettings();
        }
    }
}
