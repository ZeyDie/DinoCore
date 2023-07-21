package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class WarmRoastSettings extends AbstractSettings {
    @NotNull
    public WarmRoastSettings.WarmRoastSettingsData warmRoastSettingsData = new WarmRoastSettingsData();

    public static @NotNull WarmRoastSettings getInstance() {
        return MinecraftServer.getServer().warmRoastSettings;
    }

    @Override
    public @NotNull String getConfigName() {
        return "warmroast";
    }

    @Override
    public @NotNull WarmRoastSettings.WarmRoastSettingsData getSettings() {
        return this.warmRoastSettingsData;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.warmRoastSettingsData = (WarmRoastSettingsData) object;
    }

    @Data
    public static final class WarmRoastSettingsData {
        private boolean enableWarmRoast = false;
        @NotNull
        private String[] warmRoastParams = new String[]{"--port", "25565"};
    }
}
