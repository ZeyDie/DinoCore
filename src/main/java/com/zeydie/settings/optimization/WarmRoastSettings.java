package com.zeydie.settings.optimization;

import com.zeydie.settings.AbstractSettings;
import com.zeydie.settings.interfaces.IGson;
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
    public void setSettings(final IGson object) {
        this.warmRoastSettingsGson = (WarmRoastSettingsGson) object;
    }

    @Data
    public static final class WarmRoastSettingsGson implements IGson {
        private boolean enableWarmRoast = true;
        private String[] warmRoastParams = new String[]{"--port", "25565"};
    }
}
