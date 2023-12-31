package net.minecraft.server.dedicated;

import com.zeydie.settings.optimization.CoreSettings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.logging.ILogAgent;
import net.minecraft.logging.LogAgent;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import org.bukkit.craftbukkit.v1_6_R3.LoggerOutputStream;
import org.bukkit.craftbukkit.v1_6_R3.SpigotTimings;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

// CraftBukkit start
// CraftBukkit end

public class DedicatedServer extends MinecraftServer implements IServer {
    private final List pendingCommandList = Collections.synchronizedList(new ArrayList());
    private final ILogAgent field_98131_l;
    private RConThreadQuery theRConThreadQuery;
    private RConThreadMain theRConThreadMain;
    public PropertyManager settings; // CraftBukkit - private -> public
    private boolean canSpawnStructures;
    private EnumGameType gameType;
    private NetworkListenThread networkThread;
    private boolean guiIsEnabled;

    // Cauldron start - vanilla compatibility
    public DedicatedServer(final File par1File) {
        super(par1File);
        this.field_98131_l = new LogAgent("Minecraft-Server", (String) null, (new File(par1File, "server.log")).getAbsolutePath());
        new DedicatedServerSleepThread(this);
    }

    // Cauldron end
    // CraftBukkit start - Signature changed
    public DedicatedServer(final joptsimple.OptionSet options) {
        super(options);
        // CraftBukkit end
        this.field_98131_l = new LogAgent("Minecraft-Server", (String) null, (String) null); // CraftBukkit - null last argument
        new DedicatedServerSleepThread(this);
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws java.net.UnknownHostException   // CraftBukkit - throws UnknownHostException
    {
        final DedicatedServerCommandThread dedicatedservercommandthread = new DedicatedServerCommandThread(this);
        dedicatedservercommandthread.setDaemon(true);
        dedicatedservercommandthread.start();
        // CraftBukkit start
        System.setOut(new PrintStream(new LoggerOutputStream(this.getLogAgent().func_120013_a(), Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(this.getLogAgent().func_120013_a(), Level.SEVERE), true));
        // CraftBukkit end
        this.getLogAgent().logInfo("Starting minecraft server version 1.6.4");

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            this.getLogAgent().logWarning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        FMLCommonHandler.instance().onServerStart(this);

        this.getLogAgent().logInfo("Loading properties");
        this.settings = new PropertyManager(this.options, this.getLogAgent()); // CraftBukkit - CLI argument support

        if (this.isSinglePlayer()) {
            this.setHostname("127.0.0.1");
        } else {
            this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
            this.setHostname(this.settings.getProperty("server-ip", ""));
        }

        this.setCanSpawnAnimals(this.settings.getBooleanProperty("spawn-animals", true));
        this.setCanSpawnNPCs(this.settings.getBooleanProperty("spawn-npcs", true));
        this.setAllowPvp(this.settings.getBooleanProperty("pvp", true));
        this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));
        this.setTexturePack(this.settings.getProperty("texture-pack", ""));
        this.setMOTD(this.settings.getProperty("motd", "A Minecraft Server"));
        this.setForceGamemode(this.settings.getBooleanProperty("force-gamemode", false));
        this.func_143006_e(this.settings.getIntProperty("player-idle-timeout", 0));

        if (this.settings.getIntProperty("difficulty", 1) < 0) {
            this.settings.setProperty("difficulty", Integer.valueOf(0));
        } else if (this.settings.getIntProperty("difficulty", 1) > 3) {
            this.settings.setProperty("difficulty", Integer.valueOf(3));
        }

        this.canSpawnStructures = this.settings.getBooleanProperty("generate-structures", true);
        final int i = this.settings.getIntProperty("gamemode", EnumGameType.SURVIVAL.getID());
        this.gameType = WorldSettings.getGameTypeById(i);
        this.getLogAgent().logInfo("Default game type: " + this.gameType);
        InetAddress inetaddress = null;

        if (!this.getServerHostname().isEmpty()) {
            inetaddress = InetAddress.getByName(this.getServerHostname());
        }

        if (this.getServerPort() < 0) {
            this.setServerPort(this.settings.getIntProperty("server-port", 25565));
        }
        // Spigot start
        this.setConfigurationManager(new DedicatedPlayerList(this));
        org.spigotmc.SpigotConfig.init();
        org.spigotmc.SpigotConfig.registerCommands();
        // Spigot end
        // Cauldron start
        this.cauldronConfig.registerCommands();
        this.tileEntityConfig.registerCommands();
        // Cauldron end

        this.getLogAgent().logInfo("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());
        this.getLogAgent().logInfo("Starting Minecraft server on " + (this.getServerHostname().isEmpty() ? "*" : this.getServerHostname()) + ":" + this.getServerPort());

        try {
            this.networkThread = new DedicatedServerListenThread(this, inetaddress, this.getServerPort());
        } catch (final Throwable ioexception)     // CraftBukkit - IOException -> Throwable
        {
            this.getLogAgent().logWarning("**** FAILED TO BIND TO PORT!");
            this.getLogAgent().logWarningFormatted("The exception was: {0}", new Object[]{ioexception.toString()});
            this.getLogAgent().logWarning("Perhaps a server is already running on that port?");

            //TODO ZoomCodeStart
            ioexception.printStackTrace();

            final CoreSettings.CoreSettingsData coreSettingsData = this.coreSettings.getSettings();

            if (coreSettingsData.isUnbindPort()) {
                try {
                    final int serverPort = this.getServerPort();

                    FMLLog.info("Core will unbinding port %d for start again...", serverPort);
                    Runtime.getRuntime().exec(String.format("fuser -k %d/tcp", serverPort));

                    this.networkThread = new DedicatedServerListenThread(this, inetaddress, serverPort);
                } catch (final IOException exception) {
                    exception.printStackTrace();

                    return false;
                }
            } else
                //TODO ZoomCodeEnd

                return false;
        }

        // Cauldron start - moved to FMLServerAboutToStart
        // server.loadPlugins();
        // server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.STARTUP);
        // Cauldron End

        if (!this.isServerInOnlineMode()) {
            this.getLogAgent().logWarning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            this.getLogAgent().logWarning("The server will make no attempt to authenticate usernames. Beware.");
            this.getLogAgent().logWarning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            this.getLogAgent().logWarning("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        FMLCommonHandler.instance().onServerStarted();

        // this.setConfigurationManager(new DedicatedPlayerList(this)); // CraftBukkit - moved up
        this.anvilConverterForAnvilFile = new AnvilSaveConverter(server.getWorldContainer()); // CraftBukkit - moved from MinecraftServer constructor
        final long j = System.nanoTime();

        if (this.getFolderName() == null) {
            this.setFolderName(this.settings.getProperty("level-name", "world"));
        }

        final String s = this.settings.getProperty("level-seed", "");
        final String s1 = this.settings.getProperty("level-type", "DEFAULT");
        final String s2 = this.settings.getProperty("generator-settings", "");
        long k = (new Random()).nextLong();

        if (!s.isEmpty()) {
            try {
                final long l = Long.parseLong(s);

                if (l != 0L) {
                    k = l;
                }
            } catch (final NumberFormatException numberformatexception) {
                k = (long) s.hashCode();
            }
        }

        WorldType worldtype = WorldType.parseWorldType(s1);

        if (worldtype == null) {
            worldtype = WorldType.DEFAULT;
        }

        this.setBuildLimit(this.settings.getIntProperty("max-build-height", 256));
        this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
        this.setBuildLimit(MathHelper.clamp_int(this.getBuildLimit(), 64, 256));
        this.settings.setProperty("max-build-height", Integer.valueOf(this.getBuildLimit()));
        if (!FMLCommonHandler.instance().handleServerAboutToStart(this)) {
            return false;
        }
        this.getLogAgent().logInfo("Preparing level \"" + this.getFolderName() + "\"");
        this.loadAllWorlds(this.getFolderName(), this.getFolderName(), k, worldtype, s2);
        final long i1 = System.nanoTime() - j;
        final String s3 = String.format("%.3fs", new Object[]{Double.valueOf((double) i1 / 1.0E9D)});
        this.getLogAgent().logInfo("Done (" + s3 + ")! For help, type \"help\" or \"?\"");

        if (this.settings.getBooleanProperty("enable-query", false)) {
            this.getLogAgent().logInfo("Starting GS4 status listener");
            this.theRConThreadQuery = new RConThreadQuery(this);
            this.theRConThreadQuery.startThread();
        }

        if (this.settings.getBooleanProperty("enable-rcon", false)) {
            this.getLogAgent().logInfo("Starting remote control listener");
            this.theRConThreadMain = new RConThreadMain(this);
            this.theRConThreadMain.startThread();
            this.remoteConsole = new org.bukkit.craftbukkit.v1_6_R3.command.CraftRemoteConsoleCommandSender(); // CraftBukkit
        }

        // CraftBukkit start
        if (this.server.getBukkitSpawnRadius() > -1) {
            this.getLogAgent().logInfo("'settings.spawn-radius' in bukkit.yml has been moved to 'spawn-protection' in server.properties. I will move your config for you.");
            this.settings.properties.remove("spawn-protection");
            this.settings.getIntProperty("spawn-protection", this.server.getBukkitSpawnRadius());
            this.server.removeBukkitSpawnRadius();
            this.settings.saveProperties();
        }

        return FMLCommonHandler.instance().handleServerStarting(this);
    }

    public PropertyManager getPropertyManager() {
        return this.settings;
    }
    // CraftBukkit end

    public boolean canStructuresSpawn() {
        return this.canSpawnStructures;
    }

    public EnumGameType getGameType() {
        return this.gameType;
    }

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public int getDifficulty() {
        return Math.max(0, Math.min(3, this.settings.getIntProperty("difficulty", 1))); // CraftBukkit - clamp values
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore() {
        return this.settings.getBooleanProperty("hardcore", false);
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(final CrashReport par1CrashReport) {
        while (this.isServerRunning()) {
            this.executePendingCommands();

            try {
                Thread.sleep(10L);
            } catch (final InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }
        }
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport) {
        CrashReport par1CrashReport1 = super.addServerInfoToCrashReport(par1CrashReport);
        par1CrashReport1.getCategory().addCrashSectionCallable("Is Modded", new CallableType(this));
        par1CrashReport1.getCategory().addCrashSectionCallable("Type", new CallableServerType(this));
        return par1CrashReport1;
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void systemExitNow() {
        System.exit(0);
    }

    public void updateTimeLightAndEntities() {
        try {
            super.updateTimeLightAndEntities();
            this.executePendingCommands();
        } catch (final Throwable t) {}
    }

    public boolean getAllowNether() {
        return this.settings.getBooleanProperty("allow-nether", true);
    }

    public boolean allowSpawnMonsters() {
        return this.settings.getBooleanProperty("spawn-monsters", true);
    }

    public void addServerStatsToSnooper(final PlayerUsageSnooper par1PlayerUsageSnooper) {
        par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(this.getDedicatedPlayerList().isWhiteListEnabled()));
        par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(this.getDedicatedPlayerList().getWhiteListedPlayers().size()));
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled() {
        return this.settings.getBooleanProperty("snooper-enabled", true);
    }

    public void addPendingCommand(final String par1Str, final ICommandSender par2ICommandSender) {
        this.pendingCommandList.add(new ServerCommand(par1Str, par2ICommandSender));
    }

    public void executePendingCommands() {
        SpigotTimings.serverCommandTimer.startTiming(); // Spigot
        while (!this.pendingCommandList.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) this.pendingCommandList.remove(0);
            // CraftBukkit start - ServerCommand for preprocessing
            final ServerCommandEvent event = new ServerCommandEvent(this.console, servercommand.command);
            this.server.getPluginManager().callEvent(event);
            servercommand = new ServerCommand(event.getCommand(), servercommand.sender);
            // this.getCommandHandler().a(servercommand.source, servercommand.command); // Called in dispatchServerCommand
            this.server.dispatchServerCommand(this.console, servercommand);
            // CraftBukkit end
        }
        SpigotTimings.serverCommandTimer.stopTiming(); // Spigot
    }

    public boolean isDedicatedServer() {
        return true;
    }

    public DedicatedPlayerList getDedicatedPlayerList() {
        return (DedicatedPlayerList) super.getConfigurationManager();
    }

    public NetworkListenThread getNetworkThread() {
        return this.networkThread;
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    public int getIntProperty(final String par1Str, final int par2) {
        return this.settings.getIntProperty(par1Str, par2);
    }

    /**
     * Gets a string property. If it does not exist, set it to the specified value.
     */
    public String getStringProperty(final String par1Str, final String par2Str) {
        return this.settings.getProperty(par1Str, par2Str);
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified value.
     */
    public boolean getBooleanProperty(final String par1Str, final boolean par2) {
        return this.settings.getBooleanProperty(par1Str, par2);
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(final String par1Str, final Object par2Obj) {
        this.settings.setProperty(par1Str, par2Obj);
    }

    /**
     * Saves all of the server properties to the properties file.
     */
    public void saveProperties() {
        this.settings.saveProperties();
    }

    /**
     * Returns the filename where server properties are stored
     */
    public String getSettingsFilename() {
        final File file1 = this.settings.getPropertiesFile();
        return file1 != null ? file1.getAbsolutePath() : "No settings file";
    }

    @SideOnly(Side.SERVER)
    public void func_120011_ar() {
        MinecraftServerGui.func_120016_a(this);
        this.guiIsEnabled = true;
    }

    public boolean getGuiEnabled() {
        return this.guiIsEnabled;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(final EnumGameType par1EnumGameType, final boolean par2) {
        return "";
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled() {
        return this.settings.getBooleanProperty("enable-command-block", false);
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize() {
        return this.settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
    }

    /**
     * Returns true if a player does not have permission to edit the block at the given coordinates.
     */
    public boolean isBlockProtected(final World par1World, final int par2, final int par3, final int par4, final EntityPlayer par5EntityPlayer) {
        if (par1World.provider.dimensionId != 0) {
            return false;
        } else if (this.getDedicatedPlayerList().getOps().isEmpty()) {
            return false;
        } else if (this.getDedicatedPlayerList().isPlayerOpped(par5EntityPlayer.getCommandSenderName())) {
            return false;
        } else if (this.getSpawnProtectionSize() <= 0) {
            return false;
        } else {
            final ChunkCoordinates chunkcoordinates = par1World.getSpawnPoint();
            final int l = MathHelper.abs_int(par2 - chunkcoordinates.posX);
            final int i1 = MathHelper.abs_int(par4 - chunkcoordinates.posZ);
            final int j1 = Math.max(l, i1);
            return j1 <= this.getSpawnProtectionSize();
        }
    }

    public ILogAgent getLogAgent() {
        return this.field_98131_l;
    }

    public int func_110455_j() {
        return this.settings.getIntProperty("op-permission-level", 4);
    }

    public void func_143006_e(final int par1) {
        super.func_143006_e(par1);
        this.settings.setProperty("player-idle-timeout", Integer.valueOf(par1));
        this.saveProperties();
    }

    public ServerConfigurationManager getConfigurationManager() {
        return this.getDedicatedPlayerList();
    }
}
