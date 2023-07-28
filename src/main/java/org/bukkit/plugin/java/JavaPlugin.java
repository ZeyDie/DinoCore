package org.bukkit.plugin.java;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Java plugin
 */
public abstract class JavaPlugin extends PluginBase {
    private boolean isEnabled = false;
    private boolean initialized = false;
    private PluginLoader loader = null;
    private Server server = null;
    private File file = null;
    private PluginDescriptionFile description = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private boolean naggable = true;
    private EbeanServer ebean = null;
    private FileConfiguration newConfig = null;
    private File configFile = null;
    private PluginLogger logger = null;

    public JavaPlugin() {}

    /**
     * Returns the folder that the plugin data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder.
     */
    public final File getDataFolder() {
        return dataFolder;
    }

    /**
     * Gets the associated PluginLoader responsible for this plugin
     *
     * @return PluginLoader that controls this plugin
     */
    public final PluginLoader getPluginLoader() {
        return loader;
    }

    /**
     * Returns the Server instance currently running this plugin
     *
     * @return Server running this plugin
     */
    public final Server getServer() {
        return server;
    }

    /**
     * Returns a value indicating whether or not this plugin is currently enabled
     *
     * @return true if this plugin is enabled, otherwise false
     */
    public final boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Returns the file which contains this plugin
     *
     * @return File containing this plugin
     */
    protected File getFile() {
        return file;
    }

    /**
     * Returns the plugin.yaml file containing the details for this plugin
     *
     * @return Contents of the plugin.yaml file
     */
    public final PluginDescriptionFile getDescription() {
        return description;
    }

    public FileConfiguration getConfig() {
        if (newConfig == null) {
            reloadConfig();
        }
        return newConfig;
    }

    public void reloadConfig() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

            newConfig.setDefaults(defConfig);
        }
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (final IOException ex) {
            logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    public void saveResource(String resourcePath, final boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        String resourcePath1 = resourcePath.replace('\\', '/');
        final InputStream in = getResource(resourcePath1);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath1 + "' cannot be found in " + file);
        }

        final File outFile = new File(dataFolder, resourcePath1);
        final int lastIndex = resourcePath1.lastIndexOf('/');
        final File outDir = new File(dataFolder, resourcePath1.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                final OutputStream out = new FileOutputStream(outFile);
                final byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (final IOException ex) {
            logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public InputStream getResource(final String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            final URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (final IOException ex) {
            return null;
        }
    }

    /**
     * Returns the ClassLoader which holds this plugin
     *
     * @return ClassLoader holding this plugin
     */
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the enabled state of this plugin
     *
     * @param enabled true if enabled, otherwise false
     */
    protected final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    /**
     * Initializes this plugin with the given variables.
     * <p>
     * This method should never be called manually.
     *
     * @param loader PluginLoader that is responsible for this plugin
     * @param server Server instance that is running this plugin
     * @param description PluginDescriptionFile containing metadata on this plugin
     * @param dataFolder Folder containing the plugin's data
     * @param file File containing this plugin
     * @param classLoader ClassLoader which holds this plugin
     */
    protected final void initialize(final PluginLoader loader, final Server server, final PluginDescriptionFile description, final File dataFolder, final File file, final ClassLoader classLoader) {
        if (!initialized) {
            this.initialized = true;
            this.loader = loader;
            this.server = server;
            this.file = file;
            this.description = description;
            this.dataFolder = dataFolder;
            this.classLoader = classLoader;
            this.configFile = new File(dataFolder, "config.yml");
            this.logger = new PluginLogger(this);

            if (description.isDatabaseEnabled()) {
                final ServerConfig db = new ServerConfig();

                db.setDefaultServer(false);
                db.setRegister(false);
                db.setClasses(getDatabaseClasses());
                db.setName(description.getName());
                server.configureDbConfig(db);

                final DataSourceConfig ds = db.getDataSourceConfig();

                ds.setUrl(replaceDatabaseString(ds.getUrl()));
                dataFolder.mkdirs();

                final ClassLoader previous = Thread.currentThread().getContextClassLoader();

                Thread.currentThread().setContextClassLoader(classLoader);
                ebean = EbeanServerFactory.create(db);
                Thread.currentThread().setContextClassLoader(previous);
            }
        }
    }

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>();
    }

    private String replaceDatabaseString(String input) {
        String input2 = input.replaceAll("\\{DIR\\}", dataFolder.getPath().replaceAll("\\\\", "/") + "/");
        String input1 = input2.replaceAll("\\{NAME\\}", description.getName().replaceAll("[^\\w_-]", ""));
        return input1;
    }

    /**
     * Gets the initialization status of this plugin
     *
     * @return true if this plugin is initialized, otherwise false
     */
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return null;
    }

    /**
     * Gets the command with the given name, specific to this plugin. Commands
     * need to be registered in the {@link PluginDescriptionFile#getCommands()
     * PluginDescriptionFile} to exist at runtime.
     *
     * @param name name or alias of the command
     * @return the plugin command if found, otherwise null
     */
    public PluginCommand getCommand(final String name) {
        final String alias = name.toLowerCase();
        PluginCommand command = getServer().getPluginCommand(alias);

        if ((command != null) && (command.getPlugin() != this)) {
            command = getServer().getPluginCommand(description.getName().toLowerCase() + ":" + alias);
        }

        if ((command != null) && (command.getPlugin() == this)) {
            return command;
        } else {
            return null;
        }
    }

    public void onLoad() {}

    public void onDisable() {}

    public void onEnable() {}

    public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
        return null;
    }

    public final boolean isNaggable() {
        return naggable;
    }

    public final void setNaggable(final boolean canNag) {
        this.naggable = canNag;
    }

    public EbeanServer getDatabase() {
        return ebean;
    }

    protected void installDDL() {
        final SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        final DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(false, gen.generateCreateDdl());
    }

    protected void removeDDL() {
        final SpiEbeanServer serv = (SpiEbeanServer) getDatabase();
        final DdlGenerator gen = serv.getDdlGenerator();

        gen.runScript(true, gen.generateDropDdl());
    }

    public final Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return description.getFullName();
    }
}
