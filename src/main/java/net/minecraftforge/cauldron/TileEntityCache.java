package net.minecraftforge.cauldron;

import net.minecraft.tileentity.TileEntity;

public class TileEntityCache {

    public Class<? extends TileEntity> tileEntityClass;
    public boolean tickNoPlayers = false;
    public int tickInterval = 1;
    public String configPath;
    public String worldName;

    public TileEntityCache(final Class<? extends TileEntity> tileEntityClass, final String worldName, final String configPath, final boolean tickNoPlayers, final int tickInterval)
    {
        this.tileEntityClass = tileEntityClass;
        this.worldName = worldName;
        this.tickNoPlayers = tickNoPlayers;
        this.tickInterval = tickInterval;
        this.configPath = configPath;
    }
}
