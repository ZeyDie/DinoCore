package net.minecraftforge.cauldron.configuration;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.cauldron.command.CauldronCommand;
import org.bukkit.configuration.file.YamlConfiguration;

public class CauldronConfig extends ConfigBase {
    private final String HEADER = "This is the main configuration file for Cauldron.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to Cauldron,\n"
            + "join us at the IRC or drop by our forums and leave a post.\n"
            + "\n"
            + "IRC: #cauldron @ irc.esper.net ( http://webchat.esper.net/?channel=cauldron )\n"
            + "Forums: http://cauldron.minecraftforge.net/\n";

    /* ======================================================================== */

    // Logging options
    public final BoolSetting dumpMaterials = new BoolSetting(this, "settings.dump-materials", false, "Dumps all materials with their corresponding id's");
    public final BoolSetting disableWarnings = new BoolSetting(this, "logging.disabled-warnings", false, "Disable warning messages to server admins");
    public final BoolSetting worldLeakDebug = new BoolSetting(this, "logging.world-leak-debug", false, "Log worlds that appear to be leaking (buggy)");
    public final BoolSetting connectionLogging = new BoolSetting(this, "logging.connection", false, "Log connections");
    public final BoolSetting tickIntervalLogging = new BoolSetting(this, "logging.tick-intervals", false, "Log when skip interval handlers are ticked");
    public final BoolSetting chunkLoadLogging = new BoolSetting(this, "logging.chunk-load", false, "Log when chunks are loaded (dev)");
    public final BoolSetting chunkUnloadLogging = new BoolSetting(this, "logging.chunk-unload", false, "Log when chunks are unloaded (dev)");
    public final BoolSetting entitySpawnLogging = new BoolSetting(this, "logging.entity-spawn", false, "Log when living entities are spawned (dev)");
    public final BoolSetting entityDespawnLogging = new BoolSetting(this, "logging.entity-despawn", false, "Log when living entities are despawned (dev)");
    public final BoolSetting entityDeathLogging = new BoolSetting(this, "logging.entity-death", false, "Log when an entity is destroyed (dev)");
    public final BoolSetting logWithStackTraces = new BoolSetting(this, "logging.detailed-logging", false, "Add stack traces to dev logging");
    public final BoolSetting dumpChunksOnDeadlock = new BoolSetting(this, "logging.dump-chunks-on-deadlock", false, "Dump chunks in the event of a deadlock (helps to debug the deadlock)");
    public final BoolSetting dumpHeapOnDeadlock = new BoolSetting(this, "logging.dump-heap-on-deadlock", false, "Dump the heap in the event of a deadlock (helps to debug the deadlock)");
    public final BoolSetting dumpThreadsOnWarn = new BoolSetting(this, "logging.dump-threads-on-warn", false, "Dump the the server thread on deadlock warning (delps to debug the deadlock)");
    public final BoolSetting logEntityCollisionChecks = new BoolSetting(this, "logging.entity-collision-checks", false, "Whether to log entity collision/count checks");
    public final BoolSetting logEntitySpeedRemoval = new BoolSetting(this, "logging.entity-speed-removal", false, "Whether to log entity removals due to speed");
    public final IntSetting largeCollisionLogSize = new IntSetting(this, "logging.collision-warn-size", 200, "Number of colliding entities in one spot before logging a warning. Set to 0 to disable");
    public final IntSetting largeEntityCountLogSize = new IntSetting(this, "logging.entity-count-warn-size", 0, "Number of entities in one dimension logging a warning. Set to 0 to disable");

    // General settings
    //TODO ZoomCodeReplace load-chunk-on-request true on false
    public final BoolSetting loadChunkOnRequest = new BoolSetting(this, "settings.load-chunk-on-request", false, "Forces Chunk Loading on 'Provide' requests (speedup for mods that don't check if a chunk is loaded");
    //TODO ZoomCodeReplace load-chunk-on-forge-tick false on true
    public final BoolSetting loadChunkOnForgeTick = new BoolSetting(this, "settings.load-chunk-on-forge-tick", true, "Forces Chunk Loading during Forge Server Tick events");
    public final BoolSetting checkEntityBoundingBoxes = new BoolSetting(this, "settings.check-entity-bounding-boxes", false, "Removes an entity that exceeds the max bounding box size.");
    public final BoolSetting checkEntityMaxSpeeds = new BoolSetting(this, "settings.check-entity-max-speeds", false, "Removes any entity that exceeds max speed.");
    public final IntSetting largeBoundingBoxLogSize = new IntSetting(this, "settings.entity-bounding-box-max-size", 1000, "Max size of an entity's bounding box before removing it (either being too large or bugged and 'moving' too fast)");
    public final IntSetting entityMaxSpeed = new IntSetting(this, "settings.entity-max-speed", 100, "Square of the max speed of an entity before removing it");

    // Debug settings
    public final BoolSetting enableThreadContentionMonitoring = new BoolSetting(this, "debug.thread-contention-monitoring", false, "Set true to enable Java's thread contention monitoring for thread dumps");

    // Server options
    public final BoolSetting infiniteWaterSource = new BoolSetting(this, "world-settings.default.infinite-water-source", true, "Vanilla water source behavior - is infinite");
    public final BoolSetting flowingLavaDecay = new BoolSetting(this, "world-settings.default.flowing-lava-decay", false, "Lava behaves like vanilla water when source block is removed");
    public final BoolSetting fakePlayerLogin = new BoolSetting(this, "fake-players.do-login", false, "Raise login events for fake players");

    // Plug-in options
    public final BoolSetting remapPluginFile = new BoolSetting(this, "plugin-settings.default.remap-plugin-file", false, "Remap the plugin file (dev)");

    /* ======================================================================== */

    public CauldronConfig(final String fileName, final String commandName) {
        super(fileName, commandName);
        init();
    }

    public void init() {
        settings.put(dumpMaterials.path, dumpMaterials);
        settings.put(disableWarnings.path, disableWarnings);
        settings.put(worldLeakDebug.path, worldLeakDebug);
        settings.put(connectionLogging.path, connectionLogging);
        settings.put(tickIntervalLogging.path, tickIntervalLogging);
        settings.put(chunkLoadLogging.path, chunkLoadLogging);
        settings.put(chunkUnloadLogging.path, chunkUnloadLogging);
        settings.put(entitySpawnLogging.path, entitySpawnLogging);
        settings.put(entityDespawnLogging.path, entityDespawnLogging);
        settings.put(entityDeathLogging.path, entityDeathLogging);
        settings.put(logWithStackTraces.path, logWithStackTraces);
        settings.put(dumpChunksOnDeadlock.path, dumpChunksOnDeadlock);
        settings.put(dumpHeapOnDeadlock.path, dumpHeapOnDeadlock);
        settings.put(dumpThreadsOnWarn.path, dumpThreadsOnWarn);
        settings.put(logEntityCollisionChecks.path, logEntityCollisionChecks);
        settings.put(logEntitySpeedRemoval.path, logEntitySpeedRemoval);
        settings.put(largeCollisionLogSize.path, largeCollisionLogSize);
        settings.put(largeEntityCountLogSize.path, largeEntityCountLogSize);
        settings.put(loadChunkOnRequest.path, loadChunkOnRequest);
        settings.put(loadChunkOnForgeTick.path, loadChunkOnForgeTick);
        settings.put(checkEntityBoundingBoxes.path, checkEntityBoundingBoxes);
        settings.put(checkEntityMaxSpeeds.path, checkEntityMaxSpeeds);
        settings.put(largeBoundingBoxLogSize.path, largeBoundingBoxLogSize);
        settings.put(enableThreadContentionMonitoring.path, enableThreadContentionMonitoring);
        settings.put(infiniteWaterSource.path, infiniteWaterSource);
        settings.put(flowingLavaDecay.path, flowingLavaDecay);
        settings.put(fakePlayerLogin.path, fakePlayerLogin);
        settings.put(remapPluginFile.path, remapPluginFile);
        load();
    }

    public void addCommands() {
        commands.put(this.commandName, new CauldronCommand());
    }

    public void load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            String header = HEADER + "\n";
            for (final Setting toggle : settings.values()) {
                if (!toggle.description.isEmpty())
                    header += "Setting: " + toggle.path + " Default: " + toggle.def + "   # " + toggle.description + "\n";

                config.addDefault(toggle.path, toggle.def);
                settings.get(toggle.path).setValue(config.getString(toggle.path));
            }
            config.options().header(header);
            config.options().copyDefaults(true);

            version = getInt("config-version", 1);
            set("config-version", 1);

            this.saveWorldConfigs();
            this.save();
        } catch (final Exception ex) {
            MinecraftServer.getServer().logSevere("Could not load " + this.configFile);
            ex.printStackTrace();
        }
    }
}
