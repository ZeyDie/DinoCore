package com.zeydie.threads;

import com.zeydie.settings.optimization.WorldsSettings;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.Map;

public final class UnloadingThread extends AbstractThread {
    public UnloadingThread(final int id) {
        super(id, WorldsSettings.getInstance().getSettings(), false);

        this.addRunnable(new Runnable() {
            @Override
            public void run() {
                final WorldsSettings.WorldsSettingsData worldsSettingsData = WorldsSettings.getInstance().getSettings();

                if (worldsSettingsData.isEnable()) {
                    final WorldsSettings.WorldsSettingsDataUnloading worldsSettingsDataUnloading = worldsSettingsData.getWorldsSettingsDataUnloading();

                    if (!worldsSettingsDataUnloading.isUnloadingWorlds()) return;

                    final Map<String, WorldsSettings.WorldsSettingsDataUnloading.WorldUnloadingData> worldDataMap = worldsSettingsDataUnloading.getWorldData();

                    boolean settingsChanged = false;

                    for (final WorldServer worldServer : DimensionManager.getWorlds()) {
                        final String worldName = worldServer.getWorldInfo().getWorldName();

                        if (!worldDataMap.containsKey(worldName)) {
                            worldDataMap.put(worldName, new WorldsSettings.WorldsSettingsDataUnloading.WorldUnloadingData(true, false, true));
                            settingsChanged = true;
                        }
                    }

                    if (settingsChanged) WorldsSettings.getInstance().save();

                    for (final Map.Entry<String, WorldsSettings.WorldsSettingsDataUnloading.WorldUnloadingData> worldDataEntry : worldDataMap.entrySet()) {
                        final String worldName = worldDataEntry.getKey();
                        final WorldsSettings.WorldsSettingsDataUnloading.WorldUnloadingData worldUnloadingData = worldDataEntry.getValue();

                        if (worldUnloadingData.isEnable()) {
                            final WorldServer worldServer = DimensionManager.getWorld(worldName);

                            if (worldServer == null) continue;

                            final int dimension = worldServer.dimension;

                            if (worldUnloadingData.isKeepLoaded()) {
                                DimensionManager.initDimension(dimension);

                                continue;
                            }

                            if (worldUnloadingData.isUnloadIfNoPlayers() && worldServer.playerEntities.isEmpty())
                                DimensionManager.unloadWorld(dimension);
                        }
                    }
                }
            }
        });
    }
}
