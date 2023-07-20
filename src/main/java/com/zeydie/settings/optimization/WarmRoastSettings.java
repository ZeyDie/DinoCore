package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import lombok.Data;
import net.minecraft.server.MinecraftServer;

public final class WarmRoastSettings extends AbstractSettings {
    public WarmRoastSettingsGson warmRoastSettingsGson = new WarmRoastSettingsGson();

    public static WarmRoastSettingsGson getInstance() {
        return MinecraftServer.getServer().warmRoastSettings.getSettings();
    }

    @Override
    public String getConfigName() {
        return "warmroast";
    }

    @Override
    public WarmRoastSettingsGson getSettings() {
        return this.warmRoastSettingsGson;
    }

    @Override
    public void setSettings(final Object object) {
        this.warmRoastSettingsGson = (WarmRoastSettingsGson) object;
    }

    @Data
    public static final class WarmRoastSettingsGson {
        private boolean enableWarmRoast = false;
        private String[] warmRoastParams = new String[]{"--port", "25565"};
    }
}
