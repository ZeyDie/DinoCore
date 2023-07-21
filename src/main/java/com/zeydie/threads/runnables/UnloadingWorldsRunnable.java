package com.zeydie.threads.runnables;

import com.zeydie.settings.optimization.WorldsSettings;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UnloadingWorldsRunnable implements Runnable {
    @Override
    public void run() {
        synchronized (DimensionManager.worlds) {
            final List<Integer> ids = new ArrayList<>();

            for (final Map.Entry<Integer, WorldServer> entry : DimensionManager.worlds.entrySet()) {
                final int id = entry.getKey();
                final WorldServer worldServer = entry.getValue();
                final String worldName = worldServer.getWorldInfo().getWorldName();

                if (worldName.equals(MinecraftServer.getServer().getFolderName()))
                    continue;

                final List<String> whitelistUnloadingWorlds = WorldsSettings.getInstance().getWhitelistUnloadingWorlds();

                if (whitelistUnloadingWorlds.contains(worldName))
                    continue;

                if (worldServer.playerEntities.isEmpty())
                    ids.add(id);
            }

            for (final int integer : ids) {
                FMLLog.info("Unloading dimension: %d", integer);

                DimensionManager.unloadWorld(integer);
            }
        }
    }
}
