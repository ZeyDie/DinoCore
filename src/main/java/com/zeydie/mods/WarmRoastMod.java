package com.zeydie.mods;

import com.sk89q.warmroast.WarmRoast;
import com.zeydie.settings.optimization.WarmRoastSettings;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;

@Mod(modid = "WarmRoast", name = "WarmRoast")
public final class WarmRoastMod {
    @Mod.EventHandler
    public void serverStarted(final FMLServerStartedEvent event) {
        final WarmRoastSettings.WarmRoastSettingsGson warmRoastSettingsGson = WarmRoastSettings.getInstance();

        if (warmRoastSettingsGson.isEnableWarmRoast()) {
            FMLLog.info("Starting WarmRoast...");

            try {
                WarmRoast.main(warmRoastSettingsGson.getWarmRoastParams());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
