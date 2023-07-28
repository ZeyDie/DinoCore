package net.minecraftforge.cauldron.configuration;

import net.minecraft.server.MinecraftServer;

import java.util.List;

public class WorldConfig
{
    private final String worldName;
    public ConfigBase baseConfig;
    private boolean verbose;

    public WorldConfig(final String worldName, final ConfigBase configFile)
    {
        this.worldName = worldName.toLowerCase();
        this.baseConfig = configFile;
        if (worldName.toLowerCase().contains("dummy")) return;
    }

    public void save()
    {
        baseConfig.save();
    }

    private void log(final String s)
    {
        if ( verbose )
        {
            MinecraftServer.getServer().logInfo( s );
        }
    }

    public void set(final String path, final Object val)
    {
        baseConfig.config.set( path, val );
    }

    public boolean isBoolean(final String path)
    {
        return baseConfig.config.isBoolean(path);
    }

    public boolean getBoolean(final String path, final boolean def)
    {
        if (baseConfig.settings.get("world-settings.default." + path) == null)
        {
            baseConfig.settings.put("world-settings.default." + path, new BoolSetting(baseConfig, "world-settings.default." + path, def, ""));
        }

        baseConfig.config.addDefault( "world-settings.default." + path, def );
        return baseConfig.config.getBoolean( "world-settings." + worldName + "." + path, baseConfig.config.getBoolean( "world-settings.default." + path ) );
    }

    private double getDouble(final String path, final double def)
    {
        baseConfig.config.addDefault( "world-settings.default." + path, def );
        return baseConfig.config.getDouble( "world-settings." + worldName + "." + path, baseConfig.config.getDouble( "world-settings.default." + path ) );
    }

    public int getInt(final String path, final int def)
    {
        if (baseConfig.settings.get("world-settings.default." + path) == null)
        {
            baseConfig.settings.put("world-settings.default." + path, new IntSetting(baseConfig, "world-settings.default." + path, def, ""));
        }

        baseConfig.config.addDefault( "world-settings.default." + path, def );
        return baseConfig.config.getInt( "world-settings." + worldName + "." + path, baseConfig.config.getInt( "world-settings.default." + path ) );
    }

    private <T> List getList(final String path, final T def)
    {
        baseConfig.config.addDefault( "world-settings.default." + path, def );
        return (List<T>) baseConfig.config.getList( "world-settings." + worldName + "." + path, baseConfig.config.getList( "world-settings.default." + path ) );
    }

    private String getString(final String path, final String def)
    {
        baseConfig.config.addDefault( "world-settings.default." + path, def );
        return baseConfig.config.getString( "world-settings." + worldName + "." + path, baseConfig.config.getString( "world-settings.default." + path ) );
    }
}
