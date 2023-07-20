package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import lombok.Data;
import net.minecraft.server.MinecraftServer;

public final class MultiThreadSettings extends AbstractSettings {
    public MultiThreadSettingsGson multiThreadSettingsGson = new MultiThreadSettingsGson();

    public static MultiThreadSettingsGson getInstance() {
        return MinecraftServer.getServer().multiThreadSettings.getSettings();
    }

    @Override
    public String getConfigName() {
        return "multithreading";
    }

    @Override
    public MultiThreadSettingsGson getSettings() {
        return this.multiThreadSettingsGson;
    }

    @Override
    public void setSettings(final Object object) {
        this.multiThreadSettingsGson = (MultiThreadSettingsGson) object;
    }

    @Data
    public static final class MultiThreadSettingsGson {
        private MobsSettings mobsSettings = new MobsSettings();
        private WorldSettings worldSettings = new WorldSettings();

        @Data
        public static final class MobsSettings implements ITickRunnable {
            private DebugSettings debugSettings = new DebugSettings();

            private boolean enable = true;
            private int pools = 2;
            private int tickRate = 1;

            private AABBForEntity aabbForEntity = new AABBForEntity();

            @Data
            public static final class AABBForEntity {
                private boolean shuffleLists = false;
                private int maximumEntities = 16;
                private boolean ignorePlayers = true;
            }
        }

        @Data
        public static final class WorldSettings implements ITickRunnable {
            private DebugSettings debugSettings = new DebugSettings();

            private boolean enable = true;
            private int pools = 1;
            private int tickRate = 1;
        }
    }
}
