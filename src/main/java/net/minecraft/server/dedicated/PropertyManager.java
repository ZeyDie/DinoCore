package net.minecraft.server.dedicated;

import joptsimple.OptionSet;
import net.minecraft.logging.ILogAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager
{
    public final Properties properties = new Properties(); // CraftBukkit - private -> public

    /** Reference to the logger. */
    private final ILogAgent logger;
    private final File associatedFile;

    public PropertyManager(final File par1File, final ILogAgent par2ILogAgent)
    {
        this.associatedFile = par1File;
        this.logger = par2ILogAgent;

        if (par1File.exists())
        {
            FileInputStream fileinputstream = null;

            try
            {
                fileinputstream = new FileInputStream(par1File);
                this.properties.load(fileinputstream);
            }
            catch (final Exception exception)
            {
                par2ILogAgent.logWarningException("Failed to load " + par1File, exception);
                this.logMessageAndSave();
            }
            finally
            {
                if (fileinputstream != null)
                {
                    try
                    {
                        fileinputstream.close();
                    }
                    catch (final IOException ioexception)
                    {
                        ;
                    }
                }
            }
        }
        else
        {
            par2ILogAgent.logWarning(par1File + " does not exist");
            this.logMessageAndSave();
        }
    }

    // CraftBukkit start
    private OptionSet options = null;

    public PropertyManager(final OptionSet options, final ILogAgent ilogagent)
    {
        this((File) options.valueOf("config"), ilogagent);
        this.options = options;
    }

    private <T> T getOverride(final String name, final T value)
    {
        if ((this.options != null) && (this.options.has(name)) && !name.equals("online-mode"))    // Spigot
        {
            return (T) this.options.valueOf(name);
        }

        return value;
    }
    // CraftBukkit end

    /**
     * logs an info message then calls saveSettingsToFile Yes this appears to be a potential stack overflow - these 2
     * functions call each other repeatdly if an exception occurs.
     */
    public void logMessageAndSave()
    {
        this.logger.logInfo("Generating new properties file");
        this.saveProperties();
    }

    /**
     * Writes the properties to the properties file.
     */
    public void saveProperties()
    {
        FileOutputStream fileoutputstream = null;

        try
        {
            // CraftBukkit start - Don't attempt writing to file if it's read only
            if (this.associatedFile.exists() && !this.associatedFile.canWrite())
            {
                return;
            }

            // CraftBukkit end
            fileoutputstream = new FileOutputStream(this.associatedFile);
            this.properties.store(fileoutputstream, "Minecraft server properties");
        }
        catch (final Exception exception)
        {
            this.logger.logWarningException("Failed to save " + this.associatedFile, exception);
            this.logMessageAndSave();
        }
        finally
        {
            if (fileoutputstream != null)
            {
                try
                {
                    fileoutputstream.close();
                }
                catch (final IOException ioexception)
                {
                    ;
                }
            }
        }
    }

    /**
     * Returns this PropertyManager's file object used for property saving.
     */
    public File getPropertiesFile()
    {
        return this.associatedFile;
    }

    /**
     * Gets a property. If it does not exist, set it to the specified value.
     */
    public String getProperty(final String par1Str, final String par2Str)
    {
        if (!this.properties.containsKey(par1Str))
        {
            this.properties.setProperty(par1Str, par2Str);
            this.saveProperties();
        }

        return this.getOverride(par1Str, this.properties.getProperty(par1Str, par2Str)); // CraftBukkit
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    public int getIntProperty(final String par1Str, final int par2)
    {
        try
        {
            return this.getOverride(par1Str, Integer.parseInt(this.getProperty(par1Str, "" + par2))); // CraftBukkit
        }
        catch (final Exception exception)
        {
            this.properties.setProperty(par1Str, "" + par2);
            return this.getOverride(par1Str, par2); // CraftBukkit
        }
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified value.
     */
    public boolean getBooleanProperty(final String par1Str, final boolean par2)
    {
        try
        {
            return this.getOverride(par1Str, Boolean.parseBoolean(this.getProperty(par1Str, "" + par2))); // CraftBukkit
        }
        catch (final Exception exception)
        {
            this.properties.setProperty(par1Str, "" + par2);
            return this.getOverride(par1Str, par2); // CraftBukkit
        }
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(final String par1Str, final Object par2Obj)
    {
        this.properties.setProperty(par1Str, "" + par2Obj);
    }
}
