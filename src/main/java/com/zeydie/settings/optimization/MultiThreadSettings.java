package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class MultiThreadSettings extends AbstractSettings {
    @NotNull
    public MultiThreadSettings.MultiThreadSettingsData multiThreadSettingsData = new MultiThreadSettingsData();

    public static @NotNull MultiThreadSettings getInstance() {
        return MinecraftServer.getServer().multiThreadSettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "multithreading";
    }

    @Override
    public @NotNull MultiThreadSettings.MultiThreadSettingsData getSettings() {
        return this.multiThreadSettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.multiThreadSettingsData = (MultiThreadSettingsData) object;
    }

    @Data
    public static final class MultiThreadSettingsData {
        @NotNull
        private MobsSettings mobsSettings = new MobsSettings();
        @NotNull
        private WorldSettings worldSettings = new WorldSettings();

        @Data
        public static final class MobsSettings implements ITickRunnable {
            @NotNull
            private DebugSettings debugSettings = new DebugSettings();
            private int tickRate = 1;

            private boolean enable = true;
            private int pools = 2;

            @NotNull
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
            @NotNull
            private DebugSettings debugSettings = new DebugSettings();
            private int tickRate = 1;

            private boolean enable = true;
            private int pools = 1;
        }
    }
}
