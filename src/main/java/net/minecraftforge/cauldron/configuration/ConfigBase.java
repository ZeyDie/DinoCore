package net.minecraftforge.cauldron.configuration;

import com.zeydie.DefaultPaths;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigBase
{
    protected final File configFile;
    protected final String commandName;
    
    /* ======================================================================== */

    protected YamlConfiguration config;
    protected int version;
    protected Map<String, Command> commands;
    protected Map<String, Setting> settings = new HashMap<String, Setting>();

    /* ======================================================================== */

    public ConfigBase(final String fileName, final String commandName)
    {
        //TODO ZoomCodeStart
        this.configFile = DefaultPaths.getDefaultFile(fileName);
        //TODO ZoomCodeEnd
        //TODO ZoomCodeClear
        //this.configFile = new File(fileName);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.commandName = commandName;
        this.commands = new HashMap<String, Command>();
        this.addCommands();
    }

    protected abstract void addCommands();

    public Map<String, Setting> getSettings()
    {
        return settings;
    }

    public void registerCommands()
    {
        for (final Map.Entry<String, Command> entry : commands.entrySet())
        {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), this.commandName, entry.getValue());
        }
    }

    public void save()
    {
        try
        {
            config.save(configFile);
        }
        catch (final IOException ex)
        {
            MinecraftServer.getServer().logSevere("Could not save " + configFile);
            ex.printStackTrace();
        }
    }

    public void saveWorldConfigs()
    {
        for (int i = 0; i < MinecraftServer.getServer().worlds.size(); ++i)
        {
            final WorldServer worldserver = MinecraftServer.getServer().worlds.get(i);

            if (worldserver != null)
            {
                if (worldserver.cauldronConfig != null)
                {
                    worldserver.cauldronConfig.save();
                }
                if (worldserver.tileentityConfig != null)
                {
                    worldserver.tileentityConfig.save();
                }
            }
        }
    }

    protected abstract void load();
 
    public void set(final String path, final Object val)
    {
        config.set(path, val);
    }

    public boolean isSet(final String path)
    {
        return config.isSet(path);
    }

    public boolean isInt(final String path)
    {
        return config.isInt(path);
    }

    public boolean isBoolean(final String path)
    {
        return config.isBoolean(path);
    }

    public boolean getBoolean(final String path)
    {
        return config.getBoolean(path);
    }

    public boolean getBoolean(final String path, final boolean def)
    {
        return getBoolean(path, def, true);
    }

    public boolean getBoolean(final String path, final boolean def, final boolean useDefault)
    {
        if (useDefault)
        {
            config.addDefault(path, def);
        }
        return config.getBoolean(path, def);
    }

    public int getInt(final String path)
    {
        return config.getInt(path);
    }

    public int getInt(final String path, final int def)
    {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private <T> List getList(final String path, final T def)
    {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    public String getString(final String path, final String def)
    {
        return getString(path, def, true);
    }

    public String getString(final String path, final String def, final boolean useDefault)
    {
        if (useDefault)
        {
            config.addDefault(path, def);
        }
        return config.getString(path, def);
    }

    public String getFakePlayer(final String className, final String defaultName)
    {
        return getString("fake-players." + className + ".username", defaultName);
    }
}
