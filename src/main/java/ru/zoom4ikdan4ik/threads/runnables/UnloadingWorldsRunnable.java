package ru.zoom4ikdan4ik.threads.runnables;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UnloadingWorldsRunnable implements Runnable {

    @Override
    public final void run() {
        synchronized (DimensionManager.worlds) {
            final List<Integer> ids = new ArrayList<>();

            for (final Map.Entry<Integer, WorldServer> entry : DimensionManager.worlds.entrySet())
                if (entry.getKey() != 0) {
                    final WorldServer worldServer = entry.getValue();

                    if (worldServer.playerEntities.isEmpty() && !worldServer.loadedEntityList.isEmpty())
                        ids.add(entry.getKey());
                }

            for (Integer integer : ids) {
                FMLLog.info("Unloading dimension: %d", integer);

                DimensionManager.unloadWorld(integer);
            }
        }
    }
}
