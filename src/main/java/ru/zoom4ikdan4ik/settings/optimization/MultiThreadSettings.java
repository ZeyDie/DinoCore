package ru.zoom4ikdan4ik.settings.optimization;

import net.minecraft.server.MinecraftServer;
import ru.zoom4ikdan4ik.settings.AbstractSettings;
import ru.zoom4ikdan4ik.settings.interfaces.IGson;
import ru.zoom4ikdan4ik.settings.interfaces.ITickRunnable;

public final class MultiThreadSettings extends AbstractSettings {
    public MultiThreadSettingsGson multiThreadSettingsGson = new MultiThreadSettingsGson();

    @Override
    public final String getConfigName() {
        return "multithreading";
    }

    @Override
    public final void setSettings(final IGson object) {
        this.multiThreadSettingsGson = (MultiThreadSettingsGson) object;
    }

    @Override
    public final MultiThreadSettingsGson getSettings() {
        return this.multiThreadSettingsGson;
    }

    public static MultiThreadSettingsGson getInstance() {
        return MinecraftServer.getServer().multiThreadSettings.getSettings();
    }

    public static final class MultiThreadSettingsGson implements IGson {
        public MobsSettings mobsSettings = new MobsSettings();
        public WorldSettings worldSettings = new WorldSettings();

        public final MobsSettings getMobsSettings() {
            return this.mobsSettings;
        }

        public final WorldSettings getWorldSettings() {
            return this.worldSettings;
        }

        public static final class MobsSettings implements ITickRunnable {
            public DebugSettings debugSettings = new DebugSettings();

            public boolean enable = true;
            public int pools = 2;
            public int tickRate = 1;

            public AABBForEntity aabbForEntity = new AABBForEntity();

            public final int getTickRate() {
                return this.tickRate;
            }

            @Override
            public final DebugSettings getDebugSettings() {
                return this.debugSettings;
            }

            public final class AABBForEntity {
                public boolean shuffleLists = false;
                public int maximumEntities = 16;
                public boolean ignorePlayers = true;
            }
        }

        public static final class WorldSettings implements ITickRunnable {
            public DebugSettings debugSettings = new DebugSettings();

            public boolean enable = true;
            public int pools = 1;
            public int tickRate = 1;

            public final int getTickRate() {
                return this.tickRate;
            }

            @Override
            public final DebugSettings getDebugSettings() {
                return this.debugSettings;
            }
        }
    }
}
