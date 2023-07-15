package ru.zoom4ikdan4ik.mods;

import com.sk89q.warmroast.WarmRoast;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import ru.zoom4ikdan4ik.settings.optimization.CoreSettings;

@Mod(modid = "WarmRoast", name = "WarmRoast")
public final class WarmRoastMod {

    @Mod.EventHandler
    public final void serverStarted(final FMLServerStartedEvent event) {
        if (CoreSettings.getInstance().enableWarmRoast) {
            FMLLog.info("Starting WarmRoast...");

            try {
                WarmRoast.main(CoreSettings.getInstance().warmRoastParams);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
