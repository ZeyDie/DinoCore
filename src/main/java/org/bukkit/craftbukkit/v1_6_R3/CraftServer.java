package org.bukkit.craftbukkit.v1_6_R3;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.FMLLog;
import jline.console.ConsoleReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.cauldron.apiimpl.CauldronPluginInterface;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.Warning.WarningState;
import org.bukkit.World.Environment;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.conversations.Conversable;
import org.bukkit.craftbukkit.v1_6_R3.command.CraftSimpleCommandMap;
import org.bukkit.craftbukkit.v1_6_R3.help.SimpleHelpMap;
import org.bukkit.craftbukkit.v1_6_R3.inventory.*;
import org.bukkit.craftbukkit.v1_6_R3.map.CraftMapView;
import org.bukkit.craftbukkit.v1_6_R3.metadata.EntityMetadataStore;
import org.bukkit.craftbukkit.v1_6_R3.metadata.PlayerMetadataStore;
import org.bukkit.craftbukkit.v1_6_R3.metadata.WorldMetadataStore;
import org.bukkit.craftbukkit.v1_6_R3.potion.CraftPotionBrewer;
import org.bukkit.craftbukkit.v1_6_R3.scheduler.CraftScheduler;
import org.bukkit.craftbukkit.v1_6_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.craftbukkit.v1_6_R3.updater.AutoUpdater;
import org.bukkit.craftbukkit.v1_6_R3.updater.BukkitDLUpdaterService;
import org.bukkit.craftbukkit.v1_6_R3.util.DatFileFilter;
import org.bukkit.craftbukkit.v1_6_R3.util.Versioning;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.util.StringUtil;
import org.bukkit.util.permissions.DefaultPermissions;
import org.spigotmc.SpigotConfig;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Cauldron start
// Cauldron end

public final class CraftServer implements Server {
    private final String serverName = "DiverseCauldron-MCPC-Plus"; // keep MCPC-Plus to support plugins that are still checking for this name
    private final String serverVersion;
    private final String bukkitVersion = Versioning.getBukkitVersion();
    private final ServicesManager servicesManager = new SimpleServicesManager();
    private final CraftScheduler scheduler = new CraftScheduler();
    private final CraftSimpleCommandMap craftCommandMap = new CraftSimpleCommandMap(this); // Cauldron
    private final SimpleCommandMap commandMap = new SimpleCommandMap(this);
    private final SimpleHelpMap helpMap = new SimpleHelpMap(this);
    private final StandardMessenger messenger = new StandardMessenger();
    private final PluginManager pluginManager = new SimplePluginManager(this, commandMap);
    protected final net.minecraft.server.MinecraftServer console;
    protected final net.minecraft.server.dedicated.DedicatedPlayerList playerList;
    private final Map<String, World> worlds = new LinkedHashMap<String, World>();
    public YamlConfiguration configuration = MinecraftServer.configuration; // Cauldron
    private final Yaml yaml = new Yaml(new SafeConstructor());
    private final Map<String, OfflinePlayer> offlinePlayers = new MapMaker().softValues().makeMap();
    private final AutoUpdater updater;
    private final EntityMetadataStore entityMetadata = new EntityMetadataStore();
    private final PlayerMetadataStore playerMetadata = new PlayerMetadataStore();
    private final WorldMetadataStore worldMetadata = new WorldMetadataStore();
    private int monsterSpawn = -1;
    private int animalSpawn = -1;
    private int waterAnimalSpawn = -1;
    private int ambientSpawn = -1;
    public boolean chunkGCEnabled = false; // Cauldron
    public int chunkGCPeriod = -1;
    public int chunkGCLoadThresh = 0;
    private File container;
    private WarningState warningState = WarningState.DEFAULT;
    private final BooleanWrapper online = new BooleanWrapper();
    public CraftScoreboardManager scoreboardManager;
    public boolean playerCommandState;
    private boolean printSaveWarning;

    private final class BooleanWrapper {
        private boolean value = true;
    }

    static {
        ConfigurationSerialization.registerClass(CraftOfflinePlayer.class);
        CraftItemFactory.instance();
    }

    public CraftServer(final net.minecraft.server.MinecraftServer console, final net.minecraft.server.management.ServerConfigurationManager playerList) {
        this.console = console;
        this.playerList = (net.minecraft.server.dedicated.DedicatedPlayerList) playerList;
        this.serverVersion = CraftServer.class.getPackage().getImplementationVersion();
        online.value = console.getPropertyManager().getBooleanProperty("online-mode", true);

        Bukkit.setServer(this);
        new CauldronPluginInterface().install(); // Cauldron

        // Register all the Enchantments and PotionTypes now so we can stop new registration immediately after
        net.minecraft.enchantment.Enchantment.sharpness.getClass();
        //org.bukkit.enchantments.Enchantment.stopAcceptingRegistrations(); // Cauldron - allow registrations

        Potion.setPotionBrewer(new CraftPotionBrewer());
        net.minecraft.potion.Potion.blindness.getClass();
        //PotionEffectType.stopAcceptingRegistrations(); // Cauldron - allow registrations
        // Ugly hack :(

        if (!MinecraftServer.useConsole) { // Cauldron
            getLogger().info("Console input is disabled due to --noconsole command argument");
        }

        // Cauldron start - moved to MinecraftServer so FML/Forge can access during server startup
        //configuration = YamlConfiguration.loadConfiguration(getConfigFile());
        //configuration.options().copyDefaults(true);
        //configuration.setDefaults(YamlConfiguration.loadConfiguration(getClass().getClassLoader().getResourceAsStream("configurations/bukkit.yml")));
        //saveConfig();
        // Cauldron end
        ((SimplePluginManager) pluginManager).useTimings(configuration.getBoolean("settings.plugin-profiling"));
        monsterSpawn = configuration.getInt("spawn-limits.monsters");
        animalSpawn = configuration.getInt("spawn-limits.animals");
        waterAnimalSpawn = configuration.getInt("spawn-limits.water-animals");
        ambientSpawn = configuration.getInt("spawn-limits.ambient");
        console.autosavePeriod = configuration.getInt("ticks-per.autosave");
        warningState = WarningState.value(configuration.getString("settings.deprecated-verbose"));
        chunkGCEnabled = configuration.getBoolean("chunk-gc.enabled"); // Cauldron
        chunkGCPeriod = configuration.getInt("chunk-gc.period-in-ticks");
        chunkGCLoadThresh = configuration.getInt("chunk-gc.load-threshold");

        updater = new AutoUpdater(new BukkitDLUpdaterService(configuration.getString("auto-updater.host")), getLogger(), configuration.getString("auto-updater.preferred-channel"));
        updater.setEnabled(false); // Spigot
        updater.setSuggestChannels(configuration.getBoolean("auto-updater.suggest-channels"));
        updater.getOnBroken().addAll(configuration.getStringList("auto-updater.on-broken"));
        updater.getOnUpdate().addAll(configuration.getStringList("auto-updater.on-update"));
        updater.check(serverVersion);

        // Spigot Start - Moved to old location of new DedicatedPlayerList in DedicatedServer
        // loadPlugins();
        // enablePlugins(PluginLoadOrder.STARTUP);
        // Spigot End
    }

    private File getConfigFile() {
        return (File) console.options.valueOf("bukkit-settings");
    }

    private void saveConfig() {
        try {
            configuration.save(getConfigFile());
        } catch (final IOException ex) {
            Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, "Could not save " + getConfigFile(), ex);
        }
    }

    public void loadPlugins() {
        pluginManager.registerInterface(JavaPluginLoader.class);

        final File pluginFolder = (File) console.options.valueOf("plugins");

        if (pluginFolder.exists()) {
            final Plugin[] plugins = pluginManager.loadPlugins(pluginFolder);
            for (final Plugin plugin : plugins) {
                try {
                    final String message = String.format("Loading %s", plugin.getDescription().getFullName());
                    plugin.getLogger().info(message);
                    plugin.onLoad();
                } catch (final Throwable ex) {
                    Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, ex.getMessage() + " initializing " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                }
            }
        } else {
            pluginFolder.mkdir();
        }
    }

    public void enablePlugins(final PluginLoadOrder type) {
        // Cauldron start - initialize mod wrappers
        org.bukkit.craftbukkit.v1_6_R3.block.CraftBlock.initMappings();
        org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity.initMappings();
        // Cauldron end
        if (type == PluginLoadOrder.STARTUP) {
            helpMap.clear();
            helpMap.initializeGeneralTopics();
        }

        final Plugin[] plugins = pluginManager.getPlugins();

        for (final Plugin plugin : plugins) {
            if ((!plugin.isEnabled()) && (plugin.getDescription().getLoad() == type)) {
                loadPlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            commandMap.registerServerAliases();
            loadCustomPermissions();
            DefaultPermissions.registerCorePermissions();
            helpMap.initializeCommands();
        }
    }

    public void disablePlugins() {
        pluginManager.disablePlugins();
    }

    private void loadPlugin(final Plugin plugin) {
        try {
            pluginManager.enablePlugin(plugin);

            final List<Permission> perms = plugin.getDescription().getPermissions();

            for (final Permission perm : perms) {
                try {
                    pluginManager.addPermission(perm);
                } catch (final IllegalArgumentException ex) {
                    getLogger().log(Level.WARNING, "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
                }
            }
        } catch (final Throwable ex) {
            Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, ex.getMessage() + " loading " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
    }

    public String getName() {
        return serverName;
    }

    public String getVersion() {
        return serverVersion + " (MC: " + console.getMinecraftVersion() + ")";
    }

    public String getBukkitVersion() {
        return bukkitVersion;
    }

    @SuppressWarnings("unchecked")
    public Player[] getOnlinePlayers() {
        final List<net.minecraft.entity.player.EntityPlayerMP> online = playerList.playerEntityList;
        final Player[] players = new Player[online.size()];

        for (int i = 0; i < players.length; i++) {
            players[i] = online.get(i).playerNetServerHandler.getPlayerB(); // Cauldron
        }

        return players;
    }

    public Player getPlayer(final String name) {
        Validate.notNull(name, "Name cannot be null");

        final Player[] players = getOnlinePlayers();

        Player found = null;
        final String lowerName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (final Player player : players) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
                final int curDelta = player.getName().length() - lowerName.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) break;
            }
        }
        return found;
    }

    public Player getPlayerExact(final String name) {
        Validate.notNull(name, "Name cannot be null");

        final String lname = name.toLowerCase();

        for (final Player player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(lname)) {
                return player;
            }
        }

        return null;
    }

    public int broadcastMessage(final String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public Player getPlayer(final net.minecraft.entity.player.EntityPlayerMP entity) {
        return entity.playerNetServerHandler.getPlayerB();
    }

    public List<Player> matchPlayer(final String partialName) {
        Validate.notNull(partialName, "PartialName cannot be null");

        final List<Player> matchedPlayers = new ArrayList<Player>();

        for (final Player iterPlayer : this.getOnlinePlayers()) {
            final String iterPlayerName = iterPlayer.getName();

            if (partialName.equalsIgnoreCase(iterPlayerName)) {
                // Exact match
                matchedPlayers.clear();
                matchedPlayers.add(iterPlayer);
                break;
            }
            if (iterPlayerName.toLowerCase().contains(partialName.toLowerCase())) {
                // Partial match
                matchedPlayers.add(iterPlayer);
            }
        }

        return matchedPlayers;
    }

    public int getMaxPlayers() {
        return playerList.getMaxPlayers();
    }

    // NOTE: These are dependent on the corrisponding call in MinecraftServer
    // so if that changes this will need to as well
    public int getPort() {
        return this.getConfigInt("server-port", 25565);
    }

    public int getViewDistance() {
        //TODO ZoomCodeReplace 10 on 4
        return this.getConfigInt("view-distance", 4);
    }

    public String getIp() {
        return this.getConfigString("server-ip", "");
    }

    public String getServerName() {
        return this.getConfigString("server-name", "Unknown Server");
    }

    public String getServerId() {
        return this.getConfigString("server-id", "unnamed");
    }

    public String getWorldType() {
        return this.getConfigString("level-type", "DEFAULT");
    }

    public boolean getGenerateStructures() {
        return this.getConfigBoolean("generate-structures", true);
    }

    public boolean getAllowEnd() {
        return this.configuration.getBoolean("settings.allow-end");
    }

    public boolean getAllowNether() {
        return this.getConfigBoolean("allow-nether", true);
    }

    public boolean getWarnOnOverload() {
        return this.configuration.getBoolean("settings.warn-on-overload");
    }

    public boolean getQueryPlugins() {
        return this.configuration.getBoolean("settings.query-plugins");
    }

    public boolean hasWhitelist() {
        return this.getConfigBoolean("white-list", false);
    }

    // NOTE: Temporary calls through to server.properies until its replaced
    private String getConfigString(final String variable, final String defaultValue) {
        return this.console.getPropertyManager().getProperty(variable, defaultValue);
    }

    private int getConfigInt(final String variable, final int defaultValue) {
        return this.console.getPropertyManager().getIntProperty(variable, defaultValue);
    }

    private boolean getConfigBoolean(final String variable, final boolean defaultValue) {
        return this.console.getPropertyManager().getBooleanProperty(variable, defaultValue);
    }

    // End Temporary calls

    public String getUpdateFolder() {
        return this.configuration.getString("settings.update-folder", "update");
    }

    public File getUpdateFolderFile() {
        return new File((File) console.options.valueOf("plugins"), this.configuration.getString("settings.update-folder", "update"));
    }

    public int getPingPacketLimit() {
        return this.configuration.getInt("settings.ping-packet-limit", 100);
    }

    public long getConnectionThrottle() {
        return this.configuration.getInt("settings.connection-throttle");
    }

    public int getTicksPerAnimalSpawns() {
        return this.configuration.getInt("ticks-per.animal-spawns");
    }

    public int getTicksPerMonsterSpawns() {
        return this.configuration.getInt("ticks-per.monster-spawns");
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public CraftScheduler getScheduler() {
        return scheduler;
    }

    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    public List<World> getWorlds() {
        return new ArrayList<World>(worlds.values());
    }

    public net.minecraft.server.dedicated.DedicatedPlayerList getHandle() {
        return playerList;
    }

    // NOTE: Should only be called from DedicatedServer.ah()
    public boolean dispatchServerCommand(final CommandSender sender, final net.minecraft.command.ServerCommand serverCommand) {
        if (sender instanceof Conversable) {
            final Conversable conversable = (Conversable)sender;

            if (conversable.isConversing()) {
                conversable.acceptConversationInput(serverCommand.command);
                return true;
            }
        }
        try {
            this.playerCommandState = true;
            // Cauldron start - handle bukkit/vanilla console commands
            final int space = serverCommand.command.indexOf(" ");
            // if bukkit command exists then execute it over vanilla
            if (this.getCommandMap().getCommand(serverCommand.command.substring(0, space != -1 ? space : serverCommand.command.length())) != null)
            {
                return this.dispatchCommand(sender, serverCommand.command);
            }
            else { // process vanilla console command
                craftCommandMap.setVanillaConsoleSender(serverCommand.sender);
                return this.dispatchVanillaCommand(sender, serverCommand.command);
            }
            // Cauldron end
        } catch (final Exception ex) {
            getLogger().log(Level.WARNING, "Unexpected exception while parsing console command \"" + serverCommand.command + '"', ex);
            return false;
        } finally {
            this.playerCommandState = false;
        }
    }

    public boolean dispatchCommand(final CommandSender sender, final String commandLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(commandLine, "CommandLine cannot be null");

        if (commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        // Cauldron start - handle vanilla commands called from plugins
        if(sender instanceof ConsoleCommandSender) {
            craftCommandMap.setVanillaConsoleSender(this.console);
        }
            
        return this.dispatchVanillaCommand(sender, commandLine);
        // Cauldron end
    }
    
    // Cauldron start
    // used to process vanilla commands
    public boolean dispatchVanillaCommand(final CommandSender sender, final String commandLine) {
        if (craftCommandMap.dispatch(sender, commandLine)) {
            return true;
        }

        //TODO ZoomCodeClear
        /*if (sender instanceof Player) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
        } else {
            sender.sendMessage("Unknown command. Type \"help\" for help.");
        }*/
        //TODO ZoomCodeStart
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', SpigotConfig.unknownCommandMessage));
        //TODO ZoomCodeEnd


        return false;
    }
    // Cauldron end    

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(getConfigFile());
        final net.minecraft.server.dedicated.PropertyManager config = new net.minecraft.server.dedicated.PropertyManager(console.options, console.getLogAgent());

        ((net.minecraft.server.dedicated.DedicatedServer) console).settings = config;

        ((SimplePluginManager) pluginManager).useTimings(configuration.getBoolean("settings.plugin-profiling")); // Spigot
        final boolean animals = config.getBooleanProperty("spawn-animals", console.getCanSpawnAnimals());
        final boolean monsters = config.getBooleanProperty("spawn-monsters", console.worlds.get(0).difficultySetting > 0);
        final int difficulty = config.getIntProperty("difficulty", console.worlds.get(0).difficultySetting);

        online.value = config.getBooleanProperty("online-mode", console.isServerInOnlineMode());
        console.setCanSpawnAnimals(config.getBooleanProperty("spawn-animals", console.getCanSpawnAnimals()));
        console.setAllowPvp(config.getBooleanProperty("pvp", console.isPVPEnabled()));
        console.setAllowFlight(config.getBooleanProperty("allow-flight", console.isFlightAllowed()));
        console.setMOTD(config.getProperty("motd", console.getMOTD()));
        monsterSpawn = configuration.getInt("spawn-limits.monsters");
        animalSpawn = configuration.getInt("spawn-limits.animals");
        waterAnimalSpawn = configuration.getInt("spawn-limits.water-animals");
        ambientSpawn = configuration.getInt("spawn-limits.ambient");
        warningState = WarningState.value(configuration.getString("settings.deprecated-verbose"));
        printSaveWarning = false;
        console.autosavePeriod = configuration.getInt("ticks-per.autosave");
        chunkGCEnabled = configuration.getBoolean("chunk-gc.enabled"); // Cauldron
        chunkGCPeriod = configuration.getInt("chunk-gc.period-in-ticks");
        chunkGCLoadThresh = configuration.getInt("chunk-gc.load-threshold");

        playerList.getBannedIPs().loadBanList();
        playerList.getBannedPlayers().loadBanList();

        org.spigotmc.SpigotConfig.init(); // Spigot

        for (final net.minecraft.world.WorldServer world : console.worlds) {
            world.difficultySetting = difficulty;
            world.setAllowedSpawnTypes(monsters, animals);
            if (this.getTicksPerAnimalSpawns() < 0) {
                world.ticksPerAnimalSpawns = 400;
            } else {
                world.ticksPerAnimalSpawns = this.getTicksPerAnimalSpawns();
            }

            if (this.getTicksPerMonsterSpawns() < 0) {
                world.ticksPerMonsterSpawns = 1;
            } else {
                world.ticksPerMonsterSpawns = this.getTicksPerMonsterSpawns();
            }
            world.spigotConfig.init(); // Spigot
        }

        pluginManager.clearPlugins();
        commandMap.clearCommands();
        resetRecipes();
        org.spigotmc.SpigotConfig.registerCommands(); // Spigot

        int pollCount = 0;

        // Wait for at most 2.5 seconds for plugins to close their threads
        while (pollCount < 50 && !getScheduler().getActiveWorkers().isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (final InterruptedException e) {}
            pollCount++;
        }

        final List<BukkitWorker> overdueWorkers = getScheduler().getActiveWorkers();
        for (final BukkitWorker worker : overdueWorkers) {
            final Plugin plugin = worker.getOwner();
            String author = "<NoAuthorGiven>";
            if (!plugin.getDescription().getAuthors().isEmpty()) {
                author = plugin.getDescription().getAuthors().get(0);
            }
            getLogger().log(Level.SEVERE, String.format(
                "Nag author: '%s' of '%s' about the following: %s",
                author,
                plugin.getDescription().getName(),
                "This plugin is not properly shutting down its async tasks when it is being reloaded.  This may cause conflicts with the newly loaded version of the plugin"
            ));
        }
        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);
        enablePlugins(PluginLoadOrder.POSTWORLD);
    }

    @SuppressWarnings({ "unchecked", "finally" })
    private void loadCustomPermissions() {
        final File file = new File(configuration.getString("settings.permissions-file"));
        final FileInputStream stream;

        try {
            stream = new FileInputStream(file);
        } catch (final FileNotFoundException ex) {
            try {
                file.createNewFile();
            } finally {
                return;
            }
        }

        Map<String, Map<String, Object>> perms;

        try {
            perms = (Map<String, Map<String, Object>>) yaml.load(stream);
        } catch (final MarkedYAMLException ex) {
            getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML: " + ex.toString());
            return;
        } catch (final Throwable ex) {
            getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML.", ex);
            return;
        } finally {
            try {
                stream.close();
            } catch (final IOException ex) {}
        }

        if (perms == null) {
            getLogger().log(Level.INFO, "Server permissions file " + file + " is empty, ignoring it");
            return;
        }

        final List<Permission> permsList = Permission.loadPermissions(perms, "Permission node '%s' in " + file + " is invalid", Permission.DEFAULT_PERMISSION);

        for (final Permission perm : permsList) {
            try {
                pluginManager.addPermission(perm);
            } catch (final IllegalArgumentException ex) {
                getLogger().log(Level.SEVERE, "Permission in " + file + " was already defined", ex);
            }
        }
    }

    @Override
    public String toString() {
        return "CraftServer{" + "serverName=" + serverName + ",serverVersion=" + serverVersion + ",minecraftVersion=" + console.getMinecraftVersion() + '}';
    }

    public World createWorld(final String name, final World.Environment environment) {
        return WorldCreator.name(name).environment(environment).createWorld();
    }

    public World createWorld(final String name, final World.Environment environment, final long seed) {
        return WorldCreator.name(name).environment(environment).seed(seed).createWorld();
    }

    public World createWorld(final String name, final Environment environment, final ChunkGenerator generator) {
        return WorldCreator.name(name).environment(environment).generator(generator).createWorld();
    }

    public World createWorld(final String name, final Environment environment, final long seed, final ChunkGenerator generator) {
        return WorldCreator.name(name).environment(environment).seed(seed).generator(generator).createWorld();
    }

    public World createWorld(final WorldCreator creator) {
        Validate.notNull(creator, "Creator may not be null");

        final String name = creator.name();
        final ChunkGenerator generator = creator.generator();
        final File folder = new File(getWorldContainer(), name);
        final World world = getWorld(name);
        final net.minecraft.world.WorldType type = net.minecraft.world.WorldType.parseWorldType(creator.type().getName());
        final boolean generateStructures = creator.generateStructures();

        if ((folder.exists()) && (!folder.isDirectory())) {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        }

        if (world != null) {
            return world;
        }

        final boolean hardcore = false;
        final WorldSettings worldSettings = new WorldSettings(creator.seed(), net.minecraft.world.EnumGameType.getByID(getDefaultGameMode().getValue()), generateStructures, hardcore, type);
        final net.minecraft.world.WorldServer worldserver = DimensionManager.initDimension(creator, worldSettings);

        pluginManager.callEvent(new WorldInitEvent(worldserver.getWorld()));
        net.minecraftforge.cauldron.CauldronHooks.craftWorldLoading = true;
        System.out.print("Preparing start region for level " + (console.worlds.size() - 1) + " (Dimension: " + worldserver.provider.dimensionId + ", Seed: " + worldserver.getSeed() + ")"); // Cauldron - log dimension

        if (worldserver.getWorld().getKeepSpawnInMemory()) {
            final short short1 = 196;
            long i = System.currentTimeMillis();
            for (int j = -short1; j <= short1; j += 16) {
                for (int k = -short1; k <= short1; k += 16) {
                    final long l = System.currentTimeMillis();

                    if (l < i) {
                        i = l;
                    }

                    if (l > i + 1000L) {
                        final int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                        final int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;

                        System.out.println("Preparing spawn area for " + worldserver.getWorld().getName() + ", " + (j1 * 100 / i1) + "%");
                        i = l;
                    }

                    final net.minecraft.util.ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
                    worldserver.theChunkProviderServer.loadChunk(chunkcoordinates.posX + j >> 4, chunkcoordinates.posZ + k >> 4);
                }
            }
        }
        pluginManager.callEvent(new WorldLoadEvent(worldserver.getWorld()));
        net.minecraftforge.cauldron.CauldronHooks.craftWorldLoading = false;
        return worldserver.getWorld();
    }

    public boolean unloadWorld(final String name, final boolean save) {
        return unloadWorld(getWorld(name), save);
    }

    public boolean unloadWorld(final World world, final boolean save) {
        if (world == null) {
            return false;
        }

        final net.minecraft.world.WorldServer handle = ((CraftWorld) world).getHandle();

        if (!(console.worlds.contains(handle))) {
            return false;
        }

        if (!handle.playerEntities.isEmpty()) {
            return false;
        }

        final WorldUnloadEvent e = new WorldUnloadEvent(handle.getWorld());
        pluginManager.callEvent(e);

        if (e.isCancelled()) {
            return false;
        }

        if (save) {
            try {
                handle.saveAllChunks(true, null);
                handle.flush();
                final WorldSaveEvent event = new WorldSaveEvent(handle.getWorld());
                getPluginManager().callEvent(event);
            } catch (final net.minecraft.world.MinecraftException ex) {
                getLogger().log(Level.SEVERE, null, ex);
                FMLLog.log(Level.SEVERE, ex, "Failed to save world " + handle.getWorld().getName() + " while unloading it.");
            }
        }
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(handle)); // Cauldron - fire unload event before removing world
        worlds.remove(world.getName().toLowerCase());
        DimensionManager.setWorld(handle.provider.dimensionId, null); // Cauldron - remove world from DimensionManager
        return true;
    }

    public net.minecraft.server.MinecraftServer getServer() {
        return console;
    }

    public World getWorld(final String name) {
        Validate.notNull(name, "Name cannot be null");

        return worlds.get(name.toLowerCase());
    }

    public World getWorld(final UUID uid) {
        for (final World world : worlds.values()) {
            if (world.getUID().equals(uid)) {
                return world;
            }
        }
        return null;
    }

    public void addWorld(final World world) {
        // Check if a World already exists with the UID.
        if (getWorld(world.getUID()) != null) {
            System.out.println("World " + world.getName() + " is a duplicate of another world and has been prevented from loading. Please delete the uid.dat file from " + world.getName() + "'s world directory if you want to be able to load the duplicate world.");
            return;
        }
        worlds.put(world.getName().toLowerCase(), world);
    }

    public Logger getLogger() {
        return console.getLogAgent().func_120013_a();
    }

    public ConsoleReader getReader() {
        return console.reader;
    }

    public PluginCommand getPluginCommand(final String name) {
        final Command command = commandMap.getCommand(name);

        if (command instanceof PluginCommand) {
            return (PluginCommand) command;
        } else {
            return null;
        }
    }

    public void savePlayers() {
        checkSaveState();
        playerList.saveAllPlayerData();
    }

    public void configureDbConfig(final ServerConfig config) {
        Validate.notNull(config, "Config cannot be null");

        final DataSourceConfig ds = new DataSourceConfig();
        ds.setDriver(configuration.getString("database.driver"));
        ds.setUrl(configuration.getString("database.url"));
        ds.setUsername(configuration.getString("database.username"));
        ds.setPassword(configuration.getString("database.password"));
        ds.setIsolationLevel(TransactionIsolation.getLevel(configuration.getString("database.isolation")));

        if (ds.getDriver().contains("sqlite")) {
            config.setDatabasePlatform(new SQLitePlatform());
            config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        config.setDataSourceConfig(ds);
    }

    public boolean addRecipe(final Recipe recipe) {
        final CraftRecipe toAdd;
        if (recipe instanceof CraftRecipe) {
            toAdd = (CraftRecipe) recipe;
        } else {
            if (recipe instanceof ShapedRecipe) {
                toAdd = CraftShapedRecipe.fromBukkitRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                toAdd = CraftShapelessRecipe.fromBukkitRecipe((ShapelessRecipe) recipe);
            } else if (recipe instanceof FurnaceRecipe) {
                toAdd = CraftFurnaceRecipe.fromBukkitRecipe((FurnaceRecipe) recipe);
            } else {
                return false;
            }
        }
        toAdd.addToCraftingManager();
        //net.minecraft.item.crafting.CraftingManager.getInstance().sort(); // Cauldron - mod recipes not necessarily sortable
        return true;
    }

    public List<Recipe> getRecipesFor(final ItemStack result) {
        Validate.notNull(result, "Result cannot be null");

        final List<Recipe> results = new ArrayList<Recipe>();
        final Iterator<Recipe> iter = recipeIterator();
        while (iter.hasNext()) {
            final Recipe recipe = iter.next();
            final ItemStack stack = recipe.getResult();
            if (stack.getType() != result.getType()) {
                continue;
            }
            if (result.getDurability() == -1 || result.getDurability() == stack.getDurability()) {
                results.add(recipe);
            }
        }
        return results;
    }

    public Iterator<Recipe> recipeIterator() {
        return new RecipeIterator();
    }

    public void clearRecipes() {
        net.minecraft.item.crafting.CraftingManager.getInstance().recipes.clear();
        net.minecraft.item.crafting.FurnaceRecipes.smelting().smeltingList.clear();
    }

    public void resetRecipes() {
        net.minecraft.item.crafting.CraftingManager.getInstance().recipes = new net.minecraft.item.crafting.CraftingManager().recipes;
        net.minecraft.item.crafting.FurnaceRecipes.smelting().smeltingList = new net.minecraft.item.crafting.FurnaceRecipes().smeltingList;
    }

    public Map<String, String[]> getCommandAliases() {
        final ConfigurationSection section = configuration.getConfigurationSection("aliases");
        final Map<String, String[]> result = new LinkedHashMap<String, String[]>();

        if (section != null) {
            for (final String key : section.getKeys(false)) {
                final List<String> commands;

                if (section.isList(key)) {
                    commands = section.getStringList(key);
                } else {
                    commands = ImmutableList.of(section.getString(key));
                }

                result.put(key, commands.toArray(new String[0]));
            }
        }

        return result;
    }

    public void removeBukkitSpawnRadius() {
        configuration.set("settings.spawn-radius", null);
        saveConfig();
    }

    public int getBukkitSpawnRadius() {
        return configuration.getInt("settings.spawn-radius", -1);
    }

    public String getShutdownMessage() {
        return configuration.getString("settings.shutdown-message");
    }

    public int getSpawnRadius() {
        return ((net.minecraft.server.dedicated.DedicatedServer) console).settings.getIntProperty("spawn-protection", 16);
    }

    public void setSpawnRadius(final int value) {
        configuration.set("settings.spawn-radius", value);
        saveConfig();
    }

    public boolean getOnlineMode() {
        return online.value;
    }

    public boolean getAllowFlight() {
        return console.isFlightAllowed();
    }

    public boolean isHardcore() {
        return console.isHardcore();
    }

    public boolean useExactLoginLocation() {
        return configuration.getBoolean("settings.use-exact-login-location");
    }

    public ChunkGenerator getGenerator(final String world) {
        ConfigurationSection section = configuration.getConfigurationSection("worlds");
        ChunkGenerator result = null;

        if (section != null) {
            section = section.getConfigurationSection(world);

            if (section != null) {
                final String name = section.getString("generator");

                if ((name != null) && (!name.isEmpty())) {
                    final String[] split = name.split(":", 2);
                    final String id = (split.length > 1) ? split[1] : null;
                    final Plugin plugin = pluginManager.getPlugin(split[0]);

                    if (plugin == null) {
                        getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + split[0] + "' does not exist");
                    } else if (!plugin.isEnabled()) {
                        getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + plugin.getDescription().getFullName() + "' is not enabled yet (is it load:STARTUP?)");
                    } else {
                        try {
                            result = plugin.getDefaultWorldGenerator(world, id);
                            if (result == null) {
                                getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + plugin.getDescription().getFullName() + "' lacks a default world generator");
                            }
                        } catch (final Throwable t) {
                            plugin.getLogger().log(Level.SEVERE, "Could not set generator for default world '" + world + "': Plugin '" + plugin.getDescription().getFullName(), t);
                        }
                    }
                }
            }
        }

        return result;
    }

    public CraftMapView getMap(final short id) {
        final net.minecraft.world.storage.MapStorage collection = console.worlds.get(0).mapStorage;
        final net.minecraft.world.storage.MapData worldmap = (net.minecraft.world.storage.MapData) collection.loadData(net.minecraft.world.storage.MapData.class, "map_" + id);
        if (worldmap == null) {
            return null;
        }
        return worldmap.mapView;
    }

    public CraftMapView createMap(final World world) {
        Validate.notNull(world, "World cannot be null");

        final net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(net.minecraft.item.Item.map, 1, -1);
        final net.minecraft.world.storage.MapData worldmap = net.minecraft.item.Item.map.getMapData(stack, ((CraftWorld) world).getHandle());
        return worldmap.mapView;
    }

    public void shutdown() {
        console.initiateShutdown();
    }

    public int broadcast(final String message, final String permission) {
        int count = 0;
        final Set<Permissible> permissibles = getPluginManager().getPermissionSubscriptions(permission);

        for (final Permissible permissible : permissibles) {
            if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                final CommandSender user = (CommandSender) permissible;
                user.sendMessage(message);
                count++;
            }
        }

        return count;
    }

    public OfflinePlayer getOfflinePlayer(final String name) {
        return getOfflinePlayer(name, false); // Spigot
    }

    public OfflinePlayer getOfflinePlayer(String name, final boolean search) {
        String name1 = name;
        Validate.notNull(name1, "Name cannot be null");

        OfflinePlayer result = getPlayerExact(name1);
        final String lname = name1.toLowerCase();

        if (result == null) {
            result = offlinePlayers.get(lname);

            if (result == null) {
                if (search) {
                    final net.minecraft.world.storage.SaveHandler storage = (net.minecraft.world.storage.SaveHandler) console.worlds.get(0).getSaveHandler();
                    for (final String dat : storage.getPlayerDir().list(new DatFileFilter())) {
                        final String datName = dat.substring(0, dat.length() - 4);
                        if (datName.equalsIgnoreCase(name1)) {
                            name1 = datName;
                            break;
                        }
                    }
                }

                result = new CraftOfflinePlayer(this, name1);
                offlinePlayers.put(lname, result);
            }
        } else {
            offlinePlayers.remove(lname);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getIPBans() {
        return playerList.getBannedIPs().getBannedList().keySet();
    }

    public void banIP(final String address) {
        Validate.notNull(address, "Address cannot be null.");

        final net.minecraft.server.management.BanEntry entry = new net.minecraft.server.management.BanEntry(address);
        playerList.getBannedIPs().put(entry);
        playerList.getBannedIPs().saveToFileWithHeader();
    }

    public void unbanIP(final String address) {
        playerList.getBannedIPs().remove(address);
        playerList.getBannedIPs().saveToFileWithHeader();
    }

    public Set<OfflinePlayer> getBannedPlayers() {
        final Set<OfflinePlayer> result = new HashSet<OfflinePlayer>();

        for (final Object name : playerList.getBannedPlayers().getBannedList().keySet()) {
            result.add(getOfflinePlayer((String) name));
        }

        return result;
    }

    public void setWhitelist(final boolean value) {
        playerList.whiteListEnforced = value;
        console.getPropertyManager().setProperty("white-list", value);
    }

    public Set<OfflinePlayer> getWhitelistedPlayers() {
        final Set<OfflinePlayer> result = new LinkedHashSet<OfflinePlayer>();

        for (final Object name : playerList.getWhiteListedPlayers()) {
            if (((String) name).isEmpty() || ((String)name).startsWith("#")) {
                continue;
            }
            result.add(getOfflinePlayer((String) name));
        }

        return result;
    }

    public Set<OfflinePlayer> getOperators() {
        final Set<OfflinePlayer> result = new HashSet<OfflinePlayer>();

        for (final Object name : playerList.getOps()) {
            result.add(getOfflinePlayer((String) name));
        }

        return result;
    }

    public void reloadWhitelist() {
        playerList.loadWhiteList();
    }

    public GameMode getDefaultGameMode() {
        return GameMode.getByValue(console.worlds.get(0).getWorldInfo().getGameType().getID());
    }

    public void setDefaultGameMode(final GameMode mode) {
        Validate.notNull(mode, "Mode cannot be null");

        for (final World world : getWorlds()) {
            ((CraftWorld) world).getHandle().worldInfo.setGameType(net.minecraft.world.EnumGameType.getByID(mode.getValue()));
        }
    }

    public ConsoleCommandSender getConsoleSender() {
        return console.console;
    }

    public EntityMetadataStore getEntityMetadata() {
        return entityMetadata;
    }

    public PlayerMetadataStore getPlayerMetadata() {
        return playerMetadata;
    }

    public WorldMetadataStore getWorldMetadata() {
        return worldMetadata;
    }

    public void detectListNameConflict(final net.minecraft.entity.player.EntityPlayerMP entityPlayer) {
        // Collisions will make for invisible people
        for (int i = 0; i < getHandle().playerEntityList.size(); ++i) {
            final net.minecraft.entity.player.EntityPlayerMP testEntityPlayer = (net.minecraft.entity.player.EntityPlayerMP) getHandle().playerEntityList.get(i);

            // We have a problem!
            if (testEntityPlayer != entityPlayer && testEntityPlayer.listName.equals(entityPlayer.listName)) {
                final String oldName = entityPlayer.listName;
                final int spaceLeft = 16 - oldName.length();

                if (spaceLeft <= 1) { // We also hit the list name length limit!
                    entityPlayer.listName = oldName.subSequence(0, oldName.length() - 2 - spaceLeft) + String.valueOf(System.currentTimeMillis() % 99);
                } else {
                    entityPlayer.listName = oldName + String.valueOf(System.currentTimeMillis() % 99);
                }

                return;
            }
        }
    }

    public File getWorldContainer() {
        // Cauldron start - return the proper container
        if (DimensionManager.getWorld(0) != null)
        {
            return ((SaveHandler)DimensionManager.getWorld(0).getSaveHandler()).getWorldDirectory();
        }
        // Cauldron end
        if (container == null) {
            container = new File(configuration.getString("settings.world-container", "."));
        }

        return container;
    }

    public OfflinePlayer[] getOfflinePlayers() {
        final net.minecraft.world.storage.SaveHandler storage = (net.minecraft.world.storage.SaveHandler) console.worlds.get(0).getSaveHandler();
        final String[] files = storage.getPlayerDir().list(new DatFileFilter());
        final Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();

        for (final String file : files) {
            players.add(getOfflinePlayer(file.substring(0, file.length() - 4))); // Spigot
        }
        players.addAll(Arrays.asList(getOnlinePlayers()));

        return players.toArray(new OfflinePlayer[0]);
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void sendPluginMessage(final Plugin source, final String channel, final byte[] message) {
        StandardMessenger.validatePluginMessage(getMessenger(), source, channel, message);

        for (final Player player : getOnlinePlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    public Set<String> getListeningPluginChannels() {
        final Set<String> result = new HashSet<String>();

        for (final Player player : getOnlinePlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }

        return result;
    }

    public void onPlayerJoin(final Player player) {
        if ((updater.isEnabled()) && (updater.getCurrent() != null) && (player.hasPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE))) {
            if ((updater.getCurrent().isBroken()) && (updater.getOnBroken().contains(AutoUpdater.WARN_OPERATORS))) {
                player.sendMessage(ChatColor.DARK_RED + "The version of CraftBukkit that this server is running is known to be broken. Please consider updating to the latest version at dl.bukkit.org.");
            } else if ((updater.isUpdateAvailable()) && (updater.getOnUpdate().contains(AutoUpdater.WARN_OPERATORS))) {
                player.sendMessage(ChatColor.DARK_PURPLE + "The version of CraftBukkit that this server is running is out of date. Please consider updating to the latest version at dl.bukkit.org.");
            }
        }
    }

    public Inventory createInventory(final InventoryHolder owner, final InventoryType type) {
        // TODO: Create the appropriate type, rather than Custom?
        return new CraftInventoryCustom(owner, type);
    }

    public Inventory createInventory(final InventoryHolder owner, final int size) throws IllegalArgumentException {
        Validate.isTrue(size % 9 == 0, "Chests must have a size that is a multiple of 9!");
        return new CraftInventoryCustom(owner, size);
    }

    public Inventory createInventory(final InventoryHolder owner, final int size, final String title) throws IllegalArgumentException {
        Validate.isTrue(size % 9 == 0, "Chests must have a size that is a multiple of 9!");
        return new CraftInventoryCustom(owner, size, title);
    }

    public HelpMap getHelpMap() {
        return helpMap;
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }
    
    // Cauldron start
    public CraftSimpleCommandMap getCraftCommandMap() {
        return craftCommandMap;
    }
    // Cauldron end
    
    public int getMonsterSpawnLimit() {
        return monsterSpawn;
    }

    public int getAnimalSpawnLimit() {
        return animalSpawn;
    }

    public int getWaterAnimalSpawnLimit() {
        return waterAnimalSpawn;
    }

    public int getAmbientSpawnLimit() {
        return ambientSpawn;
    }

    public boolean isPrimaryThread() {
        return Thread.currentThread().equals(console.primaryThread);
    }

    public String getMotd() {
        return console.getMOTD();
    }

    public WarningState getWarningState() {
        return warningState;
    }

    public List<String> tabComplete(final net.minecraft.command.ICommandSender sender, final String message) {
        if (!(sender instanceof net.minecraft.entity.player.EntityPlayerMP)) {
            return ImmutableList.of();
        }

        final Player player = ((net.minecraft.entity.player.EntityPlayerMP) sender).getBukkitEntity();
        if (message.startsWith("/")) {
            return tabCompleteCommand(player, message);
        } else {
            return tabCompleteChat(player, message);
        }
    }

    public List<String> tabCompleteCommand(final Player player, final String message) {
        List<String> completions = null;
        try {
            completions = (org.spigotmc.SpigotConfig.tabComplete) ? getCommandMap().tabComplete(player, message.substring(1)) : null;
        } catch (final CommandException ex) {
            player.sendMessage(ChatColor.RED + "An internal error occurred while attempting to tab-complete this command");
            getLogger().log(Level.SEVERE, "Exception when " + player.getName() + " attempted to tab complete " + message, ex);
        }

        return completions == null ? ImmutableList.<String>of() : completions;
    }

    public List<String> tabCompleteChat(final Player player, final String message) {
        final Player[] players = getOnlinePlayers();
        final List<String> completions = new ArrayList<String>();
        final PlayerChatTabCompleteEvent event = new PlayerChatTabCompleteEvent(player, message, completions);
        final String token = event.getLastToken();
        for (final Player p : players) {
            if (player.canSee(p) && StringUtil.startsWithIgnoreCase(p.getName(), token)) {
                completions.add(p.getName());
            }
        }
        pluginManager.callEvent(event);

        final Iterator<?> it = completions.iterator();
        while (it.hasNext()) {
            final Object current = it.next();
            if (!(current instanceof String)) {
                // Sanity
                it.remove();
            }
        }
        Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
        return completions;
    }

    public CraftItemFactory getItemFactory() {
        return CraftItemFactory.instance();
    }

    public CraftScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public void checkSaveState() {
        if (this.playerCommandState || this.printSaveWarning || this.console.autosavePeriod <= 0) {
            return;
        }
        this.printSaveWarning = true;
        getLogger().log(Level.WARNING, "A manual (plugin-induced) save has been detected while server is configured to auto-save. This may affect performance.", warningState == WarningState.ON ? new Throwable() : null);
    }
}
