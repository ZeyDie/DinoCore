package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class WarmRoastSettings extends AbstractSettings {
    @NotNull
    public WarmRoastSettingsGson warmRoastSettingsGson = new WarmRoastSettingsGson();

    public static @NotNull WarmRoastSettingsGson getInstance() {
        return MinecraftServer.getServer().warmRoastSettings.getSettings();
    }

    @Override
    public @NotNull String getConfigName() {
        return "warmroast";
    }

    @Override
    public @NotNull WarmRoastSettingsGson getSettings() {
        return this.warmRoastSettingsGson;
    }

    @Override
    public void setSettings(@NotNull final Object object) {
        this.warmRoastSettingsGson = (WarmRoastSettingsGson) object;
    }

    @Data
    public static final class WarmRoastSettingsGson {
        private boolean enableWarmRoast = false;
        @NotNull
        private String[] warmRoastParams = new String[]{"--port", "25565"};
    }
}
