package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.ITickRunnable;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class MultiThreadSettings extends AbstractSettings {
    @NotNull
    public MultiThreadSettingsGson multiThreadSettingsGson = new MultiThreadSettingsGson();

    public static @NotNull MultiThreadSettingsGson getInstance() {
        return MinecraftServer.getServer().multiThreadSettings.getSettings();
    }

    @Override
    public @NotNull String getConfigName() {
        return "multithreading";
    }

    @Override
    public @NotNull MultiThreadSettingsGson getSettings() {
        return this.multiThreadSettingsGson;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.multiThreadSettingsGson = (MultiThreadSettingsGson) object;
    }

    @Data
    public static final class MultiThreadSettingsGson {
        @NotNull
        private MobsSettings mobsSettings = new MobsSettings();
        @NotNull
        private WorldSettings worldSettings = new WorldSettings();

        @Data
        public static final class MobsSettings implements ITickRunnable {
            @NotNull
            private DebugSettings debugSettings = new DebugSettings();

            private boolean enable = true;
            private int pools = 2;
            private int tickRate = 1;

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

            private boolean enable = true;
            private int pools = 1;
            private int tickRate = 1;
        }
    }
}
