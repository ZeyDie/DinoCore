package net.minecraft.server.integrated;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.crash.CrashReport;
import net.minecraft.logging.ILogAgent;
import net.minecraft.logging.LogAgent;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.util.CryptManager;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.io.IOException;

// Cauldron start
// Cauldron end

@SideOnly(Side.CLIENT)
public class IntegratedServer extends MinecraftServer
{
    /** The Minecraft instance. */
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private final ILogAgent serverLogAgent;

    /** Instance of IntegratedServerListenThread. */
    private IntegratedServerListenThread theServerListeningThread;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;

    public IntegratedServer(final Minecraft par1Minecraft, final String par2Str, final String par3Str, final WorldSettings par4WorldSettings)
    {
        super(new File(par1Minecraft.mcDataDir, "saves"));
        this.serverLogAgent = new LogAgent("Minecraft-Server", " [SERVER]", (new File(par1Minecraft.mcDataDir, "output-server.log")).getAbsolutePath());
        this.setServerOwner(par1Minecraft.getSession().getUsername());
        this.setFolderName(par2Str);
        this.setWorldName(par3Str);
        this.setDemo(par1Minecraft.isDemo());
        this.canCreateBonusChest(par4WorldSettings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigurationManager(new IntegratedPlayerList(this));
        this.mc = par1Minecraft;
        this.serverProxy = par1Minecraft.getProxy();
        this.theWorldSettings = par4WorldSettings;

        try
        {
            this.theServerListeningThread = new IntegratedServerListenThread(this);
        }
        catch (final IOException ioexception)
        {
            throw new Error();
        }
    }

    protected void loadAllWorlds(final String par1Str, final String par2Str, final long par3, final WorldType par5WorldType, final String par6Str)
    {
        this.convertMapIfNeeded(par1Str);
        final ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);

        final WorldServer overWorld = (isDemo() ? new DemoWorldServer(this, isavehandler, par2Str, 0, theProfiler, getLogAgent()) : new WorldServer(this, isavehandler, par2Str, 0, theWorldSettings, theProfiler, getLogAgent()));
        for (final int dim : DimensionManager.getStaticDimensionIDs())
        {
            final WorldServer world = (dim == 0 ? overWorld : new WorldServerMulti(this, isavehandler, par2Str, dim, theWorldSettings, overWorld, theProfiler, getLogAgent()));
            world.addWorldAccess(new WorldManager(this, world));

            if (!this.isSinglePlayer())
            {
                world.getWorldInfo().setGameType(this.getGameType());
            }

            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        }

        this.getConfigurationManager().setPlayerManager(new WorldServer[]{ overWorld });
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer()
    {
        this.serverLogAgent.logInfo("Starting integrated minecraft server version 1.6.4");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        this.serverLogAgent.logInfo("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());
        if (!FMLCommonHandler.instance().handleServerAboutToStart(this)) { return false; }
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());
        this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
        return FMLCommonHandler.instance().handleServerStarting(this);
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick() throws MinecraftException // Cauldron
    {
        final boolean flag = this.isGamePaused;
        this.isGamePaused = this.theServerListeningThread.isGamePaused();

        if (!flag && this.isGamePaused)
        {
            this.serverLogAgent.logInfo("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (!this.isGamePaused)
        {
            super.tick();
        }
    }

    public boolean canStructuresSpawn()
    {
        return false;
    }

    public EnumGameType getGameType()
    {
        return this.theWorldSettings.getGameType();
    }

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public int getDifficulty()
    {
        return this.mc.gameSettings.difficulty;
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    protected File getDataDirectory()
    {
        return this.mc.mcDataDir;
    }

    public boolean isDedicatedServer()
    {
        return false;
    }

    /**
     * Gets the IntergratedServerListenThread.
     */
    public IntegratedServerListenThread getServerListeningThread()
    {
        return this.theServerListeningThread;
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(final CrashReport par1CrashReport)
    {
        this.mc.crashed(par1CrashReport);
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
    {
        CrashReport par1CrashReport1 = super.addServerInfoToCrashReport(par1CrashReport);
        par1CrashReport1.getCategory().addCrashSectionCallable("Type", new CallableType3(this));
        par1CrashReport1.getCategory().addCrashSectionCallable("Is Modded", new CallableIsModded(this));
        return par1CrashReport1;
    }

    public void addServerStatsToSnooper(final PlayerUsageSnooper par1PlayerUsageSnooper)
    {
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
        par1PlayerUsageSnooper.addData("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(final EnumGameType par1EnumGameType, final boolean par2)
    {
        try
        {
            final String s = this.theServerListeningThread.func_71755_c();
            this.getLogAgent().logInfo("Started on " + s);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), s);
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(par1EnumGameType);
            this.getConfigurationManager().setCommandsAllowedForAll(par2);
            return s;
        }
        catch (final IOException ioexception)
        {
            return null;
        }
    }

    public ILogAgent getLogAgent()
    {
        return this.serverLogAgent;
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer() throws MinecraftException // Cauldron
    {
        super.stopServer();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown()
    {
        super.initiateShutdown();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public boolean getPublic()
    {
        return this.isPublic;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(final EnumGameType par1EnumGameType)
    {
        this.getConfigurationManager().setGameType(par1EnumGameType);
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return true;
    }

    public int func_110455_j()
    {
        return 4;
    }

    public NetworkListenThread getNetworkThread()
    {
        return this.getServerListeningThread();
    }

    // Cauldron start
    @Override
    public PropertyManager getPropertyManager()
    {
        return null;
    }
    // Cauldron end
}
