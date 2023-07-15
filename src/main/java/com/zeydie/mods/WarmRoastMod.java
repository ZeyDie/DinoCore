package com.zeydie.mods;

import com.sk89q.warmroast.WarmRoast;
import com.zeydie.settings.optimization.CoreSettings;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;

@Mod(modid = "WarmRoast", name = "WarmRoast")
public final class WarmRoastMod {

    @Mod.EventHandler
    public void serverStarted(final FMLServerStartedEvent event) {
        if (CoreSettings.getInstance().isEnableWarmRoast()) {
            FMLLog.info("Starting WarmRoast...");

            try {
                WarmRoast.main(CoreSettings.getInstance().getWarmRoastParams());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
