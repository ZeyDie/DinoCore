package net.minecraft.world;

import com.google.common.collect.ImmutableSetMultimap;
import com.zeydie.settings.optimization.CoreSettings;
import com.zeydie.settings.optimization.MultiThreadSettings;
import com.zeydie.threads.runnables.WeatherEffectsRunnable;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.cauldron.CauldronHooks;
import net.minecraftforge.cauldron.configuration.CauldronWorldConfig;
import net.minecraftforge.cauldron.configuration.TileEntityWorldConfig;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.*;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.SpigotTimings;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.generator.ChunkGenerator;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

// CraftBukkit start
// CraftBukkit end
// Cauldron start
// Cauldron end

public abstract class World implements IBlockAccess {
    /**
     * Used in the getEntitiesWithinAABB functions to expand the search area for entities.
     * Modders should change this variable to a higher value if it is less then the radius
     * of one of there entities.
     */
    public static double MAX_ENTITY_RADIUS = 2.0D;

    public final MapStorage perWorldStorage;

    /**
     * boolean; if true updates scheduled by scheduleBlockUpdate happen immediately
     */
    public boolean scheduledUpdatesAreImmediate;

    /**
     * A list of all Entities in all currently-loaded chunks
     */
    //TODO ZoomCodeStart
    public List loadedEntityList = new CopyOnWriteArrayList();
    //TODO ZoomCodeEnd
    //TODO ZoomCodeClear
    //public List loadedEntityList = new ArrayList();
    public List unloadedEntityList = new ArrayList(); // Cauldron public for reporting purposes

    /**
     * A list of all TileEntities in all currently-loaded chunks
     */
    public List loadedTileEntityList = new ArrayList(); // CraftBukkit - ArrayList -> HashSet // Cauldron - keep vanilla for mod compatibility
    private List addedTileEntityList = new ArrayList();

    /**
     * Entities marked for removal.
     */
    public List entityRemoval = new ArrayList(); // Cauldron public for reporting purposes

    /**
     * Array list of players in the world.
     */
    public List<EntityPlayer> playerEntities = new ArrayList();

    /**
     * a list of all the lightning entities
     */
    public List weatherEffects = new ArrayList();
    private long cloudColour = 16777215L;

    /**
     * How much light is subtracted from full daylight
     */
    public int skylightSubtracted;

    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int updateLCG = (new Random()).nextInt();

    /**
     * magic number used to generate fast random numbers for 3d distribution within a chunk
     */
    protected final int DIST_HASH_MAGIC = 1013904223;
    public float prevRainingStrength;
    public float rainingStrength;
    public float prevThunderingStrength;
    public float thunderingStrength;

    /**
     * Set to 2 whenever a lightning bolt is generated in SSP. Decrements if > 0 in updateWeather(). Value appears to be
     * unused.
     */
    public int lastLightningBolt;
    public boolean restoringBlockStates = false; // CraftBukkit

    /**
     * Option > Difficulty setting (0 - 3)
     */
    public int difficultySetting;

    /**
     * RNG for World.
     */
    public Random rand = new Random();

    /**
     * The WorldProvider instance that World uses.
     */
    public WorldProvider provider; // CraftBukkit - remove final
    protected List worldAccesses = new ArrayList();

    /**
     * Handles chunk operations and caching
     */
    public IChunkProvider chunkProvider; // CraftBukkit - protected -> public
    protected final ISaveHandler saveHandler;

    /**
     * holds information about a world (size on disk, time, spawn point, seed, ...)
     */
    public WorldInfo worldInfo; // CraftBukkit - protected -> public

    /**
     * Boolean that is set to true when trying to find a spawn point
     */
    public boolean findingSpawnPoint;
    public MapStorage mapStorage;
    public VillageCollection villageCollectionObj;
    protected final VillageSiege villageSiegeObj = new VillageSiege(this);
    public final Profiler theProfiler;

    /**
     * The world-local pool of vectors
     */
    private final Vec3Pool vecPool = new Vec3Pool(300, 2000);
    private final Calendar theCalendar = Calendar.getInstance();
    public Scoreboard worldScoreboard = new Scoreboard(); // CraftBukkit - protected -> public

    /**
     * The log agent for this world.
     */
    private final ILogAgent worldLogAgent;
    private ArrayList<AxisAlignedBB> collidingBoundingBoxes = new ArrayList();
    private boolean scanningTileEntities;
    // CraftBukkit start - public, longhashset

    /**
     * indicates if enemies are spawned or not
     */
    public boolean spawnHostileMobs = true;

    /**
     * A flag indicating whether we should spawn peaceful mobs.
     */
    public boolean spawnPeacefulMobs = true;

    /**
     * Positions to update
     */
    protected gnu.trove.map.hash.TLongShortHashMap activeChunkSet_CB; // Spigot
    public Set activeChunkSet = new HashSet(); // vanilla compatibility
    public long ticksPerAnimalSpawns;
    public long ticksPerMonsterSpawns;
    private int tickPosition;
    // CraftBukkit end

    /**
     * number of ticks until the next random ambients play
     */
    private int ambientTickCountdown;

    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    int[] lightUpdateBlockList;

    /**
     * This is set to true for client worlds, and false for server worlds.
     */
    public boolean isRemote;
    // Cauldron start - block place
    public boolean captureBlockStates = false;
    public boolean captureTreeGeneration = false;
    public ArrayList<BlockState> capturedBlockStates = new ArrayList<BlockState>();
    public ArrayList<EntityItem> capturedItems = new ArrayList<EntityItem>();
    public int entitiesTicked;
    public int tilesTicked;
    public CauldronWorldConfig cauldronConfig;
    public TileEntityWorldConfig tileentityConfig;
    // preload world crash report classes to fix NCDFE masking StackOverflow/memory error, see #721
    private static boolean preloadedCrashClasses = false;

    {
        if (!preloadedCrashClasses) {
            // generate a temporary crash report
            final Throwable throwable = new Throwable();
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");

            // loads all the required classes - including net.minecraft.crash.CallableBlockType (package private)
            crashreportcategory.addCrashSectionCallable("Source block type", (Callable) (new CallableLvl1(this, 0)));
            CrashReportCategory.addBlockCrashInfo(crashreportcategory, 0, 0, 0, 0, -1);

            preloadedCrashClasses = true;
        }
    }
    // Cauldron end
    // Spigot start

    public static long chunkToKey(final int x, final int z) {
        long k = ((((long) x) & 0xFFFF0000L) << 16) | ((((long) x) & 0x0000FFFFL) << 0);
        k |= ((((long) z) & 0xFFFF0000L) << 32) | ((((long) z) & 0x0000FFFFL) << 16);
        return k;
    }

    public static int keyToX(final long k) {
        return (int) (((k >> 16) & 0xFFFF0000) | (k & 0x0000FFFF));
    }

    public static int keyToZ(final long k) {
        return (int) (((k >> 32) & 0xFFFF0000L) | ((k >> 16) & 0x0000FFFF));
    }
    // Spigot end

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    public BiomeGenBase getBiomeGenForCoords(final int par1, final int par2) {
        return provider.getBiomeGenForCoords(par1, par2);
    }

    public BiomeGenBase getBiomeGenForCoordsBody(final int par1, final int par2) {
        if (this.blockExists(par1, 0, par2)) {
            final Chunk chunk = this.getChunkFromBlockCoords(par1, par2);

            if (chunk != null) {
                return chunk.getBiomeGenForWorldCoords(par1 & 15, par2 & 15, this.provider.worldChunkMgr);
            }
        }

        return this.provider.worldChunkMgr.getBiomeGenAt(par1, par2);
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.provider.worldChunkMgr;
    }

    // CraftBukkit start
    private final CraftWorld world;
    public boolean pvpMode;
    public boolean keepSpawnInMemory = false; // Cauldron - default to false to give forge's keepLoaded higher priority
    public ChunkGenerator generator;
    private byte chunkTickRadius; // Spigot
    public org.spigotmc.SpigotWorldConfig spigotConfig; // Spigot
    public SpigotTimings.WorldTimingsHandler timings; // Spigot

    public CraftWorld getWorld() {
        return this.world;
    }

    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @SideOnly(Side.CLIENT)
    public World(final ISaveHandler par1ISaveHandler, final String par2Str, final WorldProvider par3WorldProvider, final WorldSettings par4WorldSettings, final Profiler par5Profiler, final ILogAgent par6ILogAgent) {
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.lightUpdateBlockList = new int[32768];
        this.saveHandler = par1ISaveHandler;
        this.theProfiler = par5Profiler;
        this.worldInfo = new WorldInfo(par4WorldSettings, par2Str);
        this.provider = par3WorldProvider;
        perWorldStorage = new MapStorage((ISaveHandler) null);
        this.worldLogAgent = par6ILogAgent;
        this.world = null; // Cauldron
    }

    // Broken up so that the WorldClient gets the chance to set the mapstorage object before the dimension initializes
    @SideOnly(Side.CLIENT)
    protected void finishSetup() {
        final VillageCollection villagecollection = (VillageCollection) this.mapStorage.loadData(VillageCollection.class, "villages");

        if (villagecollection == null) {
            this.villageCollectionObj = new VillageCollection(this);
            this.mapStorage.setData("villages", this.villageCollectionObj);
        } else {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.func_82566_a(this);
        }
        // Guarantee the dimension ID was not reset by the provider
        final int providerDim = this.provider.dimensionId;
        this.provider.registerWorld(this);
        this.provider.dimensionId = providerDim;
        this.chunkProvider = this.createChunkProvider();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    // Changed signature
    public World(final ISaveHandler idatamanager, final String s, final WorldSettings worldsettings, final WorldProvider worldprovider, final Profiler profiler, final ILogAgent ilogagent, final ChunkGenerator gen, final org.bukkit.World.Environment env) {
        this.spigotConfig = new org.spigotmc.SpigotWorldConfig(s); // Spigot
        // Cauldron start
        this.cauldronConfig = new CauldronWorldConfig(s, MinecraftServer.getServer().cauldronConfig);
        this.tileentityConfig = new TileEntityWorldConfig(s, MinecraftServer.getServer().tileEntityConfig);
        // Cauldron end
        this.generator = gen;
        this.worldInfo = idatamanager.loadWorldInfo(); // Spigot
        this.world = new CraftWorld((WorldServer) this, gen, env);
        this.ticksPerAnimalSpawns = this.getServer().getTicksPerAnimalSpawns(); // CraftBukkit
        this.ticksPerMonsterSpawns = this.getServer().getTicksPerMonsterSpawns(); // CraftBukkit
        this.chunkTickRadius = (byte) ((this.getServer().getViewDistance() < 7) ? this.getServer().getViewDistance() : 7); // CraftBukkit - don't tick chunks we don't load for player
        // CraftBukkit end
        // Spigot start
        this.chunkTickRadius = (byte) ((this.getServer().getViewDistance() < 7) ? this.getServer().getViewDistance() : 7);
        this.activeChunkSet_CB = new gnu.trove.map.hash.TLongShortHashMap(spigotConfig.chunksPerTick * 5, 0.7f, Long.MIN_VALUE, Short.MIN_VALUE);
        this.activeChunkSet_CB.setAutoCompactionFactor(0);
        // Spigot end
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.lightUpdateBlockList = new int[32768];
        this.isRemote = false;
        this.saveHandler = idatamanager;
        this.theProfiler = profiler;

        // Cauldron start
        // Provides a solution for different worlds getting different copies of the same data, potentially rewriting the data or causing race conditions/stale data
        // Buildcraft has suffered from the issue this fixes.  If you load the same data from two different worlds they can get two different copies of the same object, thus the last saved gets final say.
        if (DimensionManager.getWorld(0) != null) // if overworld has loaded, use its mapstorage
        {
            this.mapStorage = DimensionManager.getWorld(0).mapStorage;
        } else // if we are loading overworld, create a new mapstorage
        {
            this.mapStorage = new MapStorage(idatamanager);
        }
        // Cauldron end
        this.worldLogAgent = ilogagent;
        // this.worldInfo = idatamanager.loadWorldInfo(); // Spigot - Moved up

        if (worldprovider != null) {
            this.provider = worldprovider;
        } else if (this.worldInfo != null && this.worldInfo.getDimension() != 0) {
            this.provider = WorldProvider.getProviderForDimension(this.worldInfo.getDimension());
        } else {
            this.provider = WorldProvider.getProviderForDimension(0);
        }

        if (this.worldInfo == null) {
            this.worldInfo = new WorldInfo(worldsettings, s);
            this.worldInfo.setDimension(this.provider.dimensionId); // Cauldron - Save dimension to level.dat
        } else {
            this.worldInfo.setWorldName(s);
            // Cauldron start - Use saved dimension from level.dat. Fixes issues with MultiVerse
            if (this.worldInfo.getDimension() != 0)
                this.provider.dimensionId = this.worldInfo.getDimension();
            else {
                this.worldInfo.setDimension(this.provider.dimensionId);
            }
            // Cauldron end
        }

        // Cauldron start - Guarantee provider dimension is not reset. This is required for mods that rely on the provider ID to match the client dimension. Without this, IC2 will send the wrong ID to clients.
        final int providerId = this.provider.dimensionId;
        this.provider.registerWorld(this);
        this.provider.dimensionId = providerId;
        this.chunkProvider = this.createChunkProvider();
        // Cauldron start - add Forge
        if (this instanceof WorldServer) {
            this.perWorldStorage = new MapStorage(new WorldSpecificSaveHandler((WorldServer) this, idatamanager));
        } else {
            this.perWorldStorage = new MapStorage((ISaveHandler) null);
        }
        // Cauldron end
        timings = new SpigotTimings.WorldTimingsHandler(this); // Spigot - code below can generate new world and access timings
        if (!this.worldInfo.isInitialized()) {
            try {
                this.initialize(worldsettings);
            } catch (final Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

                try {
                    this.addWorldInfoToCrashReport(crashreport);
                } catch (final Throwable throwable1) {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.worldInfo.setServerInitialized(true);
        }
        //this.provider.dimensionId = providerId; // Cauldron - Fix for TerrainControl injecting their own WorldProvider

        final VillageCollection villagecollection = (VillageCollection) perWorldStorage.loadData(VillageCollection.class, "villages");

        if (villagecollection == null) {
            this.villageCollectionObj = new VillageCollection(this);
            this.perWorldStorage.setData("villages", this.villageCollectionObj);
        } else {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.func_82566_a(this);
        }

        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        this.getServer().addWorld(this.world); // CraftBukkit
    }

    public World(final ISaveHandler par1ISaveHandler, final String par2Str, final WorldSettings par3WorldSettings, final WorldProvider par4WorldProvider, final Profiler par5Profiler, final ILogAgent par6ILogAgent) {
        this.spigotConfig = new org.spigotmc.SpigotWorldConfig(par2Str); // Spigot
        // Cauldron start
        this.cauldronConfig = new CauldronWorldConfig(par2Str, MinecraftServer.getServer().cauldronConfig);
        this.tileentityConfig = new TileEntityWorldConfig(par2Str, MinecraftServer.getServer().tileEntityConfig);
        // Cauldron end
        this.world = null; // CraftWorld not used
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.lightUpdateBlockList = new int[32768];
        this.isRemote = false;
        this.saveHandler = par1ISaveHandler;
        this.theProfiler = par5Profiler;

        // Cauldron start
        // Provides a solution for different worlds getting different copies of the same data, potentially rewriting the data or causing race conditions/stale data
        // Buildcraft has suffered from the issue this fixes.  If you load the same data from two different worlds they can get two different copies of the same object, thus the last saved gets final say.
        if (DimensionManager.getWorld(0) != null) // if overworld has loaded, use its mapstorage
        {
            this.mapStorage = DimensionManager.getWorld(0).mapStorage;
        } else // if we are loading overworld, create a new mapstorage
        {
            this.mapStorage = new MapStorage(par1ISaveHandler);
        }
        // Cauldron end
        this.worldLogAgent = par6ILogAgent;
        this.worldInfo = par1ISaveHandler.loadWorldInfo();

        if (par4WorldProvider != null) {
            this.provider = par4WorldProvider;
        } else if (this.worldInfo != null && this.worldInfo.getDimension() != 0) {
            this.provider = WorldProvider.getProviderForDimension(this.worldInfo.getDimension());
        } else {
            this.provider = WorldProvider.getProviderForDimension(0);
        }

        if (this.worldInfo == null) {
            this.worldInfo = new WorldInfo(par3WorldSettings, par2Str);
        } else {
            this.worldInfo.setWorldName(par2Str);
        }

        this.provider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();
        if (this instanceof WorldServer) {
            this.perWorldStorage = new MapStorage(new WorldSpecificSaveHandler((WorldServer) this, par1ISaveHandler));
        } else {
            this.perWorldStorage = new MapStorage((ISaveHandler) null);
        }

        if (!this.worldInfo.isInitialized()) {
            try {
                this.initialize(par3WorldSettings);
            } catch (final Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

                try {
                    this.addWorldInfoToCrashReport(crashreport);
                } catch (final Throwable throwable1) {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.worldInfo.setServerInitialized(true);
        }

        final VillageCollection villagecollection = (VillageCollection) perWorldStorage.loadData(VillageCollection.class, "villages");

        if (villagecollection == null) {
            this.villageCollectionObj = new VillageCollection(this);
            this.perWorldStorage.setData("villages", this.villageCollectionObj);
        } else {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.func_82566_a(this);
        }

        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        timings = new SpigotTimings.WorldTimingsHandler(this); // Spigot
    }
    // Cauldron end    

    private static MapStorage s_mapStorage;
    private static ISaveHandler s_savehandler;

    //Provides a solution for different worlds getting different copies of the same data, potentially rewriting the data or causing race conditions/stale data
    //Buildcraft has suffered from the issue this fixes.  If you load the same data from two different worlds they can get two different copies of the same object, thus the last saved gets final say.
    private MapStorage getMapStorage(final ISaveHandler savehandler) {
        if (s_savehandler != savehandler || s_mapStorage == null) {
            s_mapStorage = new MapStorage(savehandler);
            s_savehandler = savehandler;
        }
        return s_mapStorage;
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected abstract IChunkProvider createChunkProvider();

    protected void initialize(final WorldSettings par1WorldSettings) {
        this.worldInfo.setServerInitialized(true);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setSpawnLocation() {
        this.setSpawnLocation(8, 64, 8);
    }

    /**
     * Returns the block ID of the first block at this (x,z) location with air above it, searching from sea level
     * upwards.
     */
    public int getFirstUncoveredBlock(final int par1, final int par2) {
        int k;

        for (k = 63; !this.isAirBlock(par1, k + 1, par2); ++k) {
            ;
        }

        return this.getBlockId(par1, k, par2);
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getBlockId(final int par1, final int par2, final int par3) {
        // Cauldron start - tree generation
        if (captureTreeGeneration) {
            for (final BlockState blockstate : capturedBlockStates) {
                if (blockstate.getX() == par1 && blockstate.getY() == par2 && blockstate.getZ() == par3) {
                    return blockstate.getTypeId();
                }
            }
        }
        // Cauldron end
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000) {
            if (par2 < 0) {
                return 0;
            } else if (par2 >= 256) {
                return 0;
            } else {
                Chunk chunk = null;

                try {
                    chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                    return chunk.getBlockID(par1 & 15, par2, par3 & 15);
                } catch (final Throwable throwable) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception getting block type in world");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
                    crashreportcategory.addCrashSection("Found chunk", Boolean.valueOf(chunk == null));
                    crashreportcategory.addCrashSection("Location", CrashReportCategory.getLocationInfo(par1, par2, par3));
                    throw new ReportedException(crashreport);
                }
            }
        } else {
            return 0;
        }
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isAirBlock(final int par1, final int par2, final int par3) {
        final int id = getBlockId(par1, par2, par3);
        return id == 0 || Block.blocksList[id] == null || Block.blocksList[id].isAirBlock(this, par1, par2, par3);
    }

    /**
     * Checks if a block at a given position should have a tile entity.
     */
    public boolean blockHasTileEntity(final int par1, final int par2, final int par3) {
        final int l = this.getBlockId(par1, par2, par3);
        final int meta = this.getBlockMetadata(par1, par2, par3);
        return Block.blocksList[l] != null && Block.blocksList[l].hasTileEntity(meta);
    }

    /**
     * Returns the render type of the block at the given coordinate.
     */
    public int blockGetRenderType(final int par1, final int par2, final int par3) {
        final int l = this.getBlockId(par1, par2, par3);
        return Block.blocksList[l] != null ? Block.blocksList[l].getRenderType() : -1;
    }

    /**
     * Returns whether a block exists at world coordinates x, y, z
     */
    public boolean blockExists(final int par1, final int par2, final int par3) {
        return par2 >= 0 && par2 < 256 ? this.chunkExists(par1 >> 4, par3 >> 4) : false;
    }

    /**
     * Checks if any of the chunks within distance (argument 4) blocks of the given block exist
     */
    public boolean doChunksNearChunkExist(final int par1, final int par2, final int par3, final int par4) {
        return this.checkChunksExist(par1 - par4, par2 - par4, par3 - par4, par1 + par4, par2 + par4, par3 + par4);
    }

    /**
     * Checks between a min and max all the chunks inbetween actually exist. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public boolean checkChunksExist(int par1, final int par2, int par3, int par4, final int par5, int par6) {
        int par11 = par1;
        int par31 = par3;
        int par41 = par4;
        int par61 = par6;
        if (par5 >= 0 && par2 < 256) {
            par11 >>= 4;
            par31 >>= 4;
            par41 >>= 4;
            par61 >>= 4;

            for (int k1 = par11; k1 <= par41; ++k1) {
                for (int l1 = par31; l1 <= par61; ++l1) {
                    // CraftBukkit - check unload queue too so we don't leak a chunk
                    if (!this.chunkExists(k1, l1) || ((WorldServer) this).theChunkProviderServer.chunksToUnload.contains(k1, l1)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    public boolean chunkExists(final int par1, final int par2) // Cauldron - protected -> public for repackaging
    {
        return this.chunkProvider.chunkExists(par1, par2);
    }

    /**
     * Returns a chunk looked up by block coordinates. Args: x, z
     */
    public Chunk getChunkFromBlockCoords(final int par1, final int par2) {
        return this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    public Chunk getChunkFromChunkCoords(final int par1, final int par2) {
        return this.chunkProvider.provideChunk(par1, par2);
    }

    /**
     * Sets the block ID and metadata at a given location. Args: X, Y, Z, new block ID, new metadata, flags. Flag 1 will
     * cause a block update. Flag 2 will send the change to clients (you almost always want this). Flag 4 prevents the
     * block from being re-rendered, if this is a client world. Flags can be added together.
     */
    public boolean setBlock(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        // Cauldron start - tree generation
        if (this.captureTreeGeneration) {
            BlockState blockstate = null;

            for (final BlockState previous : capturedBlockStates) {
                if (previous.getX() == par1 && previous.getY() == par2 && previous.getZ() == par3) {
                    blockstate = previous;
                    break;
                }
            }
            if (blockstate != null) {
                capturedBlockStates.remove(blockstate);
            } else {
                blockstate = org.bukkit.craftbukkit.v1_6_R3.block.CraftBlockState.getBlockState(this, par1, par2, par3, par6);
            }
            blockstate.setTypeId(par4);
            blockstate.setRawData((byte) par5);
            this.capturedBlockStates.add(blockstate);
            return true;
        }
        // Cauldron end
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000) {
            if (par2 < 0) {
                return false;
            } else if (par2 >= 256) {
                return false;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                int k1 = 0;

                if ((par6 & 1) != 0) {
                    k1 = chunk.getBlockID(par1 & 15, par2, par3 & 15);
                }
                // Cauldron start - capture blockstates
                org.bukkit.block.BlockState blockstate = null;
                if (this.captureBlockStates) {
                    blockstate = org.bukkit.craftbukkit.v1_6_R3.block.CraftBlockState.getBlockState(this, par1, par2, par3, par6);
                    this.capturedBlockStates.add(blockstate);
                }

                final boolean flag = chunk.setBlockIDWithMetadata(par1 & 15, par2, par3 & 15, par4, par5);

                if (!flag && this.captureBlockStates) {
                    this.capturedBlockStates.remove(blockstate);
                }
                // Cauldron end

                this.theProfiler.startSection("checkLight");
                this.updateAllLightTypes(par1, par2, par3);
                this.theProfiler.endSection();

                // Cauldron start
                if (flag && !this.captureBlockStates) // Don't notify clients or update physics while capturing blockstates
                {
                    // Modularize client and physic updates
                    this.markAndNotifyBlock(par1, par2, par3, k1, par4, par6);
                }
                // Cauldron end

                return flag;
            }
        } else {
            return false;
        }
    }

    // Cauldron start - Split off from original setBlock(int par1, int par2, int par3, int par4, int par5, int par6) method in order to directly send client and physic updates
    public void markAndNotifyBlock(final int x, final int y, final int z, final int oldId, final int newId, final int flag) {
        if ((flag & 2) != 0) // notify clients
        {
            this.markBlockForUpdate(x, y, z);
        }
        if ((flag & 1) != 0) // update physics
        {
            this.notifyBlockChange(x, y, z, oldId);
            final Block block = Block.blocksList[newId];
            if (block != null && block.hasComparatorInputOverride()) {
                this.func_96440_m(x, y, z, newId);
            }
        }
    }
    // Cauldron end

    /**
     * Returns the block's material.
     */
    public Material getBlockMaterial(final int par1, final int par2, final int par3) {
        final int l = this.getBlockId(par1, par2, par3);
        return l == 0 || Block.blocksList[l] == null ? Material.air : Block.blocksList[l].blockMaterial; // Cauldron
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getBlockMetadata(int par1, final int par2, int par3) {
        // Cauldron start - tree generation
        int par11 = par1;
        int par31 = par3;
        if (captureTreeGeneration) {
            for (final BlockState blockstate : capturedBlockStates) {
                if (blockstate.getX() == par11 && blockstate.getY() == par2 && blockstate.getZ() == par31) {
                    return blockstate.getRawData();
                }
            }
        }
        // Cauldron end
        if (par11 >= -30000000 && par31 >= -30000000 && par11 < 30000000 && par31 < 30000000) {
            if (par2 < 0) {
                return 0;
            } else if (par2 >= 256) {
                return 0;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(par11 >> 4, par31 >> 4);
                par11 &= 15;
                par31 &= 15;
                return chunk.getBlockMetadata(par11, par2, par31);
            }
        } else {
            return 0;
        }
    }

    /**
     * Sets the blocks metadata and if set will then notify blocks that this block changed, depending on the flag. Args:
     * x, y, z, metadata, flag. See setBlock for flag description
     */
    public boolean setBlockMetadataWithNotify(final int par1, final int par2, final int par3, final int par4, final int par5) {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000) {
            if (par2 < 0) {
                return false;
            } else if (par2 >= 256) {
                return false;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                final int j1 = par1 & 15;
                final int k1 = par3 & 15;
                final boolean flag = chunk.setBlockMetadata(j1, par2, k1, par4);

                if (flag) {
                    final int l1 = chunk.getBlockID(j1, par2, k1);

                    if ((par5 & 2) != 0 && (!this.isRemote || (par5 & 4) == 0)) {
                        this.markBlockForUpdate(par1, par2, par3);
                    }

                    if (!this.isRemote && (par5 & 1) != 0) {
                        this.notifyBlockChange(par1, par2, par3, l1);
                        final Block block = Block.blocksList[l1];

                        if (block != null && block.hasComparatorInputOverride()) {
                            this.func_96440_m(par1, par2, par3, l1);
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    /**
     * Sets a block to 0 and notifies relevant systems with the block change  Args: x, y, z
     */
    public boolean setBlockToAir(final int par1, final int par2, final int par3) {
        return this.setBlock(par1, par2, par3, 0, 0, 3);
    }

    /**
     * Destroys a block and optionally drops items. Args: X, Y, Z, dropItems
     */
    public boolean destroyBlock(final int par1, final int par2, final int par3, final boolean par4) {
        final int l = this.getBlockId(par1, par2, par3);

        if (l > 0) {
            final int i1 = this.getBlockMetadata(par1, par2, par3);
            this.playAuxSFX(2001, par1, par2, par3, l + (i1 << 12));

            if (par4) {
                Block.blocksList[l].dropBlockAsItem(this, par1, par2, par3, i1, 0);
            }

            return this.setBlock(par1, par2, par3, 0, 0, 3);
        } else {
            return false;
        }
    }

    /**
     * Sets a block and notifies relevant systems with the block change  Args: x, y, z, blockID
     */
    public boolean setBlock(final int par1, final int par2, final int par3, final int par4) {
        return this.setBlock(par1, par2, par3, par4, 0, 3);
    }

    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it only
     * if the ID or MD changes), including the tile entity description packet if applicable. Args: x, y, z
     */
    public void markBlockForUpdate(final int par1, final int par2, final int par3) {
        for (int l = 0; l < this.worldAccesses.size(); ++l) {
            ((IWorldAccess) this.worldAccesses.get(l)).markBlockForUpdate(par1, par2, par3);
        }
    }

    /**
     * The block type change and need to notify other systems  Args: x, y, z, blockID
     */
    public void notifyBlockChange(final int par1, final int par2, final int par3, final int par4) {
        this.notifyBlocksOfNeighborChange(par1, par2, par3, par4);
    }

    /**
     * marks a vertical line of blocks as dirty
     */
    public void markBlocksDirtyVertical(final int par1, final int par2, int par3, int par4) {
        int par41 = par4;
        int par31 = par3;
        int i1;

        if (par31 > par41) {
            i1 = par41;
            par41 = par31;
            par31 = i1;
        }

        if (!this.provider.hasNoSky) {
            for (i1 = par31; i1 <= par41; ++i1) {
                this.updateLightByType(EnumSkyBlock.Sky, par1, i1, par2);
            }
        }

        this.markBlockRangeForRenderUpdate(par1, par31, par2, par1, par41, par2);
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        for (int k1 = 0; k1 < this.worldAccesses.size(); ++k1) {
            ((IWorldAccess) this.worldAccesses.get(k1)).markBlockRangeForRenderUpdate(par1, par2, par3, par4, par5, par6);
        }
    }

    /**
     * Notifies neighboring blocks that this specified block changed  Args: x, y, z, blockID
     */
    public void notifyBlocksOfNeighborChange(final int par1, final int par2, final int par3, final int par4) {
        this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
        this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
        this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
    }

    /**
     * Calls notifyBlockOfNeighborChange on adjacent blocks, except the one on the given side. Args: X, Y, Z,
     * changingBlockID, side
     */
    public void notifyBlocksOfNeighborChange(final int par1, final int par2, final int par3, final int par4, final int par5) {
        if (par5 != 4) {
            this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
        }

        if (par5 != 5) {
            this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
        }

        if (par5 != 0) {
            this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
        }

        if (par5 != 1) {
            this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
        }

        if (par5 != 2) {
            this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
        }

        if (par5 != 3) {
            this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
        }
    }

    /**
     * Notifies a block that one of its neighbor change to the specified type Args: x, y, z, blockID
     */
    public void notifyBlockOfNeighborChange(final int par1, final int par2, final int par3, final int par4) {
        if (!this.isRemote) {
            final int i1 = this.getBlockId(par1, par2, par3);
            final Block block = Block.blocksList[i1];

            if (block != null) {
                try {
                    // CraftBukkit start
                    final CraftWorld world = ((WorldServer) this).getWorld();

                    if (world != null) {
                        final BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(par1, par2, par3), par4);
                        this.getServer().getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            return;
                        }
                    }

                    // CraftBukkit end
                    block.onNeighborBlockChange(this, par1, par2, par3, par4);
                } catch (final Throwable throwable) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                    int j1;

                    try {
                        j1 = this.getBlockMetadata(par1, par2, par3);
                    } catch (final Throwable throwable1) {
                        j1 = -1;
                    }

                    crashreportcategory.addCrashSectionCallable("Source block type", new CallableLvl1(this, par4));
                    CrashReportCategory.addBlockCrashInfo(crashreportcategory, par1, par2, par3, i1, j1);
                    throw new ReportedException(crashreport);
                }
            }
        }
    }

    /**
     * Returns true if the given block will receive a scheduled tick in this tick. Args: X, Y, Z, blockID
     */
    public boolean isBlockTickScheduledThisTick(final int par1, final int par2, final int par3, final int par4) {
        return false;
    }

    /**
     * Checks if the specified block is able to see the sky
     */
    public boolean canBlockSeeTheSky(final int par1, final int par2, final int par3) {
        return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).canBlockSeeTheSky(par1 & 15, par2, par3 & 15);
    }

    /**
     * Does the same as getBlockLightValue_do but without checking if its not a normal block
     */
    public int getFullBlockLightValue(final int par1, int par2, final int par3) {
        int par21 = par2;
        if (par21 < 0) {
            return 0;
        } else {
            if (par21 >= 256) {
                par21 = 255;
            }

            return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).getBlockLightValue(par1 & 15, par21, par3 & 15, 0);
        }
    }

    /**
     * Gets the light value of a block location
     */
    public int getBlockLightValue(final int par1, final int par2, final int par3) {
        return this.getBlockLightValue_do(par1, par2, par3, true);
    }

    /**
     * Gets the light value of a block location. This is the actual function that gets the value and has a bool flag
     * that indicates if its a half step block to get the maximum light value of a direct neighboring block (left,
     * right, forward, back, and up)
     */
    public int getBlockLightValue_do(int par1, int par2, int par3, final boolean par4) {
        int par21 = par2;
        int par11 = par1;
        int par31 = par3;
        if (par11 >= -30000000 && par31 >= -30000000 && par11 < 30000000 && par31 < 30000000) {
            if (par4) {
                final int l = this.getBlockId(par11, par21, par31);

                if (Block.useNeighborBrightness[l]) {
                    int i1 = this.getBlockLightValue_do(par11, par21 + 1, par31, false);
                    final int j1 = this.getBlockLightValue_do(par11 + 1, par21, par31, false);
                    final int k1 = this.getBlockLightValue_do(par11 - 1, par21, par31, false);
                    final int l1 = this.getBlockLightValue_do(par11, par21, par31 + 1, false);
                    final int i2 = this.getBlockLightValue_do(par11, par21, par31 - 1, false);

                    if (j1 > i1) {
                        i1 = j1;
                    }

                    if (k1 > i1) {
                        i1 = k1;
                    }

                    if (l1 > i1) {
                        i1 = l1;
                    }

                    if (i2 > i1) {
                        i1 = i2;
                    }

                    return i1;
                }
            }

            if (par21 < 0) {
                return 0;
            } else {
                if (par21 >= 256) {
                    par21 = 255;
                }

                final Chunk chunk = this.getChunkFromChunkCoords(par11 >> 4, par31 >> 4);
                par11 &= 15;
                par31 &= 15;
                return chunk.getBlockLightValue(par11, par21, par31, this.skylightSubtracted);
            }
        } else {
            return 15;
        }
    }

    /**
     * Returns the y coordinate with a block in it at this x, z coordinate
     */
    public int getHeightValue(final int par1, final int par2) {
        if (par1 >= -30000000 && par2 >= -30000000 && par1 < 30000000 && par2 < 30000000) {
            if (!this.chunkExists(par1 >> 4, par2 >> 4)) {
                return 0;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
                return chunk.getHeightValue(par1 & 15, par2 & 15);
            }
        } else {
            return 0;
        }
    }

    /**
     * Gets the heightMapMinimum field of the given chunk, or 0 if the chunk is not loaded. Coords are in blocks. Args:
     * X, Z
     */
    public int getChunkHeightMapMinimum(final int par1, final int par2) {
        if (par1 >= -30000000 && par2 >= -30000000 && par1 < 30000000 && par2 < 30000000) {
            if (!this.chunkExists(par1 >> 4, par2 >> 4)) {
                return 0;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
                return chunk.heightMapMinimum;
            }
        } else {
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Brightness for SkyBlock.Sky is clear white and (through color computing it is assumed) DEPENDENT ON DAYTIME.
     * Brightness for SkyBlock.Block is yellowish and independent.
     */
    public int getSkyBlockTypeBrightness(final EnumSkyBlock par1EnumSkyBlock, final int par2, int par3, final int par4) {
        int par31 = par3;
        if (this.provider.hasNoSky && par1EnumSkyBlock == EnumSkyBlock.Sky) {
            return 0;
        } else {
            if (par31 < 0) {
                par31 = 0;
            }

            if (par31 >= 256) {
                return par1EnumSkyBlock.defaultLightValue;
            } else if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000) {
                final int l = par2 >> 4;
                final int i1 = par4 >> 4;

                if (!this.chunkExists(l, i1)) {
                    return par1EnumSkyBlock.defaultLightValue;
                } else if (Block.useNeighborBrightness[this.getBlockId(par2, par31, par4)]) {
                    int j1 = this.getSavedLightValue(par1EnumSkyBlock, par2, par31 + 1, par4);
                    final int k1 = this.getSavedLightValue(par1EnumSkyBlock, par2 + 1, par31, par4);
                    final int l1 = this.getSavedLightValue(par1EnumSkyBlock, par2 - 1, par31, par4);
                    final int i2 = this.getSavedLightValue(par1EnumSkyBlock, par2, par31, par4 + 1);
                    final int j2 = this.getSavedLightValue(par1EnumSkyBlock, par2, par31, par4 - 1);

                    if (k1 > j1) {
                        j1 = k1;
                    }

                    if (l1 > j1) {
                        j1 = l1;
                    }

                    if (i2 > j1) {
                        j1 = i2;
                    }

                    if (j2 > j1) {
                        j1 = j2;
                    }

                    return j1;
                } else {
                    final Chunk chunk = this.getChunkFromChunkCoords(l, i1);
                    return chunk.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par31, par4 & 15);
                }
            } else {
                return par1EnumSkyBlock.defaultLightValue;
            }
        }
    }

    /**
     * Returns saved light value without taking into account the time of day.  Either looks in the sky light map or
     * block light map based on the enumSkyBlock arg.
     */
    public int getSavedLightValue(final EnumSkyBlock par1EnumSkyBlock, final int par2, int par3, final int par4) {
        int par31 = par3;
        if (par31 < 0) {
            par31 = 0;
        }

        if (par31 >= 256) {
            par31 = 255;
        }

        if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000) {
            final int l = par2 >> 4;
            final int i1 = par4 >> 4;

            if (!this.chunkExists(l, i1)) {
                return par1EnumSkyBlock.defaultLightValue;
            } else {
                final Chunk chunk = this.getChunkFromChunkCoords(l, i1);
                return chunk.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par31, par4 & 15);
            }
        } else {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    /**
     * Sets the light value either into the sky map or block map depending on if enumSkyBlock is set to sky or block.
     * Args: enumSkyBlock, x, y, z, lightValue
     */
    public void setLightValue(final EnumSkyBlock par1EnumSkyBlock, final int par2, final int par3, final int par4, final int par5) {
        if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000) {
            if (par3 >= 0) {
                if (par3 < 256) {
                    if (this.chunkExists(par2 >> 4, par4 >> 4)) {
                        final Chunk chunk = this.getChunkFromChunkCoords(par2 >> 4, par4 >> 4);
                        chunk.setLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15, par5);

                        for (int i1 = 0; i1 < this.worldAccesses.size(); ++i1) {
                            ((IWorldAccess) this.worldAccesses.get(i1)).markBlockForRenderUpdate(par2, par3, par4);
                        }
                    }
                }
            }
        }
    }

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    public void markBlockForRenderUpdate(final int par1, final int par2, final int par3) {
        for (int l = 0; l < this.worldAccesses.size(); ++l) {
            ((IWorldAccess) this.worldAccesses.get(l)).markBlockForRenderUpdate(par1, par2, par3);
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    public int getLightBrightnessForSkyBlocks(final int par1, final int par2, final int par3, final int par4) {
        final int i1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
        int j1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);

        if (j1 < par4) {
            j1 = par4;
        }

        return i1 << 20 | j1 << 4;
    }

    @SideOnly(Side.CLIENT)
    public float getBrightness(final int par1, final int par2, final int par3, final int par4) {
        int i1 = this.getBlockLightValue(par1, par2, par3);

        if (i1 < par4) {
            i1 = par4;
        }

        return this.provider.lightBrightnessTable[i1];
    }

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     */
    public float getLightBrightness(final int par1, final int par2, final int par3) {
        return this.provider.lightBrightnessTable[this.getBlockLightValue(par1, par2, par3)];
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime() {
        return provider.isDaytime();
    }

    /**
     * Performs a raycast against all blocks in the world except liquids.
     */
    public MovingObjectPosition clip(final Vec3 par1Vec3, final Vec3 par2Vec3) {
        return this.rayTraceBlocks_do_do(par1Vec3, par2Vec3, false, false);
    }

    /**
     * Performs a raycast against all blocks in the world, and optionally liquids.
     */
    public MovingObjectPosition clip(final Vec3 par1Vec3, final Vec3 par2Vec3, final boolean par3) {
        return this.rayTraceBlocks_do_do(par1Vec3, par2Vec3, par3, false);
    }

    public MovingObjectPosition rayTraceBlocks_do_do(final Vec3 par1Vec3, final Vec3 par2Vec3, final boolean par3, final boolean par4) {
        if (!Double.isNaN(par1Vec3.xCoord) && !Double.isNaN(par1Vec3.yCoord) && !Double.isNaN(par1Vec3.zCoord)) {
            if (!Double.isNaN(par2Vec3.xCoord) && !Double.isNaN(par2Vec3.yCoord) && !Double.isNaN(par2Vec3.zCoord)) {
                final int i = MathHelper.floor_double(par2Vec3.xCoord);
                final int j = MathHelper.floor_double(par2Vec3.yCoord);
                final int k = MathHelper.floor_double(par2Vec3.zCoord);
                int l = MathHelper.floor_double(par1Vec3.xCoord);
                int i1 = MathHelper.floor_double(par1Vec3.yCoord);
                int j1 = MathHelper.floor_double(par1Vec3.zCoord);
                int k1 = this.getBlockId(l, i1, j1);
                final int l1 = this.getBlockMetadata(l, i1, j1);
                final Block block = Block.blocksList[k1];

                if (block != null && (!par4 || block == null || block.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null) && k1 > 0 && block.canCollideCheck(l1, par3)) {
                    final MovingObjectPosition movingobjectposition = block.collisionRayTrace(this, l, i1, j1, par1Vec3, par2Vec3);

                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(par1Vec3.xCoord) || Double.isNaN(par1Vec3.yCoord) || Double.isNaN(par1Vec3.zCoord)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return null;
                    }

                    boolean flag2 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag3 = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    final double d6 = par2Vec3.xCoord - par1Vec3.xCoord;
                    final double d7 = par2Vec3.yCoord - par1Vec3.yCoord;
                    final double d8 = par2Vec3.zCoord - par1Vec3.zCoord;

                    if (flag2) {
                        d3 = (d0 - par1Vec3.xCoord) / d6;
                    }

                    if (flag3) {
                        d4 = (d1 - par1Vec3.yCoord) / d7;
                    }

                    if (flag4) {
                        d5 = (d2 - par1Vec3.zCoord) / d8;
                    }

                    final boolean flag5 = false;
                    final byte b0;

                    if (d3 < d4 && d3 < d5) {
                        if (i > l) {
                            b0 = 4;
                        } else {
                            b0 = 5;
                        }

                        par1Vec3.xCoord = d0;
                        par1Vec3.yCoord += d7 * d3;
                        par1Vec3.zCoord += d8 * d3;
                    } else if (d4 < d5) {
                        if (j > i1) {
                            b0 = 0;
                        } else {
                            b0 = 1;
                        }

                        par1Vec3.xCoord += d6 * d4;
                        par1Vec3.yCoord = d1;
                        par1Vec3.zCoord += d8 * d4;
                    } else {
                        if (k > j1) {
                            b0 = 2;
                        } else {
                            b0 = 3;
                        }

                        par1Vec3.xCoord += d6 * d5;
                        par1Vec3.yCoord += d7 * d5;
                        par1Vec3.zCoord = d2;
                    }

                    final Vec3 vec32 = this.getWorldVec3Pool().getVecFromPool(par1Vec3.xCoord, par1Vec3.yCoord, par1Vec3.zCoord);
                    l = (int) (vec32.xCoord = (double) MathHelper.floor_double(par1Vec3.xCoord));

                    if (b0 == 5) {
                        --l;
                        ++vec32.xCoord;
                    }

                    i1 = (int) (vec32.yCoord = (double) MathHelper.floor_double(par1Vec3.yCoord));

                    if (b0 == 1) {
                        --i1;
                        ++vec32.yCoord;
                    }

                    j1 = (int) (vec32.zCoord = (double) MathHelper.floor_double(par1Vec3.zCoord));

                    if (b0 == 3) {
                        --j1;
                        ++vec32.zCoord;
                    }

                    final int i2 = this.getBlockId(l, i1, j1);
                    final int j2 = this.getBlockMetadata(l, i1, j1);
                    final Block block1 = Block.blocksList[i2];

                    if ((!par4 || block1 == null || block1.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null) && i2 > 0 && block1.canCollideCheck(j2, par3)) {
                        final MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(this, l, i1, j1, par1Vec3, par2Vec3);

                        if (movingobjectposition1 != null) {
                            vec32.myVec3LocalPool.release(vec32); // CraftBukkit
                            return movingobjectposition1;
                        }
                    }

                    vec32.myVec3LocalPool.release(vec32); // CraftBukkit
                }

                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, volume (relative to 1.0), and frequency (or pitch,
     * also relative to 1.0).
     */
    public void playSoundAtEntity(final Entity par1Entity, String par2Str, final float par3, final float par4) {
        final PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(par1Entity, par2Str, par3, par4);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        String par2Str1 = event.name;
        if (par1Entity != null && par2Str1 != null) {
            for (int i = 0; i < this.worldAccesses.size(); ++i) {
                ((IWorldAccess) this.worldAccesses.get(i)).playSound(par2Str1, par1Entity.posX, par1Entity.posY - (double) par1Entity.yOffset, par1Entity.posZ, par3, par4);
            }
        }
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(final EntityPlayer par1EntityPlayer, String par2Str, final float par3, final float par4) {
        final PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(par1EntityPlayer, par2Str, par3, par4);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        String par2Str1 = event.name;
        if (par1EntityPlayer != null && par2Str1 != null) {
            for (int i = 0; i < this.worldAccesses.size(); ++i) {
                ((IWorldAccess) this.worldAccesses.get(i)).playSoundToNearExcept(par1EntityPlayer, par2Str1, par1EntityPlayer.posX, par1EntityPlayer.posY - (double) par1EntityPlayer.yOffset, par1EntityPlayer.posZ, par3, par4);
            }
        }
    }

    /**
     * Play a sound effect. Many many parameters for this function. Not sure what they do, but a classic call is :
     * (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 'random.door_open', 1.0F, world.rand.nextFloat() * 0.1F +
     * 0.9F with i,j,k position of the block.
     */
    public void playSoundEffect(final double par1, final double par3, final double par5, final String par7Str, final float par8, final float par9) {
        if (par7Str != null) {
            for (int i = 0; i < this.worldAccesses.size(); ++i) {
                ((IWorldAccess) this.worldAccesses.get(i)).playSound(par7Str, par1, par3, par5, par8, par9);
            }
        }
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(final double par1, final double par3, final double par5, final String par7Str, final float par8, final float par9, final boolean par10) {
    }

    /**
     * Plays a record at the specified coordinates of the specified name. Args: recordName, x, y, z
     */
    public void playRecord(final String par1Str, final int par2, final int par3, final int par4) {
        for (int l = 0; l < this.worldAccesses.size(); ++l) {
            ((IWorldAccess) this.worldAccesses.get(l)).playRecord(par1Str, par2, par3, par4);
        }
    }

    /**
     * Spawns a particle.  Args particleName, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(final String par1Str, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            ((IWorldAccess) this.worldAccesses.get(i)).spawnParticle(par1Str, par2, par4, par6, par8, par10, par12);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(final Entity par1Entity) {
        this.weatherEffects.add(par1Entity);
        return true;
    }

    // CraftBukkit start - Used for entities other than creatures

    /**
     * Called to place all entities as part of a world
     */
    public boolean spawnEntityInWorld(final Entity par1Entity) {
        return this.addEntity(par1Entity, SpawnReason.DEFAULT); // Set reason as DEFAULT
    }

    public boolean addEntity(final Entity entity, SpawnReason spawnReason)   // Changed signature, added SpawnReason
    {
        // Cauldron start - do not drop any items while restoring blockstates. Fixes dupes in mods such as Flans
        SpawnReason spawnReason1 = spawnReason;
        if (entity == null || (entity instanceof EntityItem && this.restoringBlockStates)) {
            return false;
        }
        // Cauldron end

        final int i = MathHelper.floor_double(entity.posX / 16.0D);
        final int j = MathHelper.floor_double(entity.posZ / 16.0D);
        boolean flag = entity.forceSpawn;

        if (entity instanceof EntityPlayer) {
            flag = true;
        }

        // CraftBukkit start
        org.bukkit.event.Cancellable event = null;
        // Cauldron start - workaround for handling CraftBukkit's SpawnReason with customspawners and block spawners
        if (entity.spawnReason != null && entity.spawnReason.equals("natural")) {
            spawnReason1 = SpawnReason.NATURAL;
        } else if (entity.spawnReason != null && entity.spawnReason.equals("spawner")) {
            spawnReason1 = SpawnReason.SPAWNER;
        }
        // Cauldron end

        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayerMP)) {
            // Cauldron start - add custom entity support
            final boolean isAnimal = entity instanceof EntityAnimal || entity instanceof EntityWaterMob || entity instanceof EntityGolem || entity.isCreatureType(EnumCreatureType.creature, false);
            final boolean isMonster = entity instanceof EntityMob || entity instanceof EntityGhast || entity instanceof EntitySlime || entity.isCreatureType(EnumCreatureType.monster, false);
            // Cauldron end

            if (spawnReason1 != SpawnReason.CUSTOM) {
                if (isAnimal && !spawnPeacefulMobs || isMonster && !spawnHostileMobs) {
                    entity.isDead = true;
                    return false;
                }
            }

            event = CraftEventFactory.callCreatureSpawnEvent((EntityLivingBase) entity, spawnReason1);
        } else if (entity instanceof EntityItem) {
            event = CraftEventFactory.callItemSpawnEvent((EntityItem) entity);
        } else if (entity.getBukkitEntity() instanceof org.bukkit.entity.Projectile) {
            // Not all projectiles extend EntityProjectile, so check for Bukkit interface instead
            event = CraftEventFactory.callProjectileLaunchEvent(entity);
        }
        // Spigot start
        else if (entity instanceof EntityXPOrb) {
            final EntityXPOrb xp = (EntityXPOrb) entity;
            final double radius = this.spigotConfig.expMerge;
            if (radius > 0) {
                final List<Entity> entities = this.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(radius, radius, radius));
                for (final Entity e : entities) {
                    if (e instanceof EntityXPOrb) {
                        final EntityXPOrb loopItem = (EntityXPOrb) e;
                        if (!loopItem.isDead) {
                            xp.xpValue += loopItem.xpValue;
                            loopItem.setDead();
                        }
                    }
                }
            }
        } // Spigot end

        if (event != null && (event.isCancelled() || entity.isDead)) {
            entity.isDead = true;
            return false;
        }

        // CraftBukkit end

        if (!flag && !this.chunkExists(i, j)) {
            entity.isDead = true; // CraftBukkit
            return false;
        } else {
            if (entity instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer) entity;
                this.playerEntities.add(entityplayer);
                this.updateAllPlayersSleepingFlag();
            }

            if (MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entity, this)) && !flag) {
                return false;
            }

            this.getChunkFromChunkCoords(i, j).addEntity(entity);
            this.loadedEntityList.add(entity);
            this.onEntityAdded(entity);
            net.minecraftforge.cauldron.CauldronHooks.logEntitySpawn(this, entity, spawnReason1);
            return true;
        }
    }

    protected void onEntityAdded(final Entity par1Entity) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            ((IWorldAccess) this.worldAccesses.get(i)).onEntityCreate(par1Entity);
        }
        par1Entity.valid = true; // CraftBukkit
    }

    public void onEntityRemoved(final Entity par1Entity) {
        for (int i = 0; i < this.worldAccesses.size(); ++i) {
            ((IWorldAccess) this.worldAccesses.get(i)).onEntityDestroy(par1Entity);
        }
        par1Entity.valid = false; // CraftBukkit
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(final Entity par1Entity) {
        if (par1Entity.riddenByEntity != null) {
            par1Entity.riddenByEntity.mountEntity((Entity) null);
        }

        if (par1Entity.ridingEntity != null) {
            par1Entity.mountEntity((Entity) null);
        }

        par1Entity.setDead();

        if (par1Entity instanceof EntityPlayer) {
            this.playerEntities.remove(par1Entity);
            this.updateAllPlayersSleepingFlag();
        }
    }

    /**
     * Do NOT use this method to remove normal entities- use normal removeEntity
     */
    public void removePlayerEntityDangerously(final Entity par1Entity) {
        //TODO ZoomCodeStart
        if (CoreSettings.getInstance().getSettings().isAsynchronousWarnings())
            //TODO ZoomCodeEnd

            if (Thread.currentThread() != MinecraftServer.getServer().primaryThread) {
                throw new IllegalStateException("Asynchronous entity remove!");    // Spigot
            }
        par1Entity.setDead();

        if (par1Entity instanceof EntityPlayer) {
            this.playerEntities.remove(par1Entity);
            this.updateAllPlayersSleepingFlag();
        }

        final int i = par1Entity.chunkCoordX;
        final int j = par1Entity.chunkCoordZ;

        if (par1Entity.addedToChunk && this.chunkExists(i, j)) {
            this.getChunkFromChunkCoords(i, j).removeEntity(par1Entity);
        }

        // CraftBukkit start - Decrement loop variable field if we've already ticked this entity
        final int index = this.loadedEntityList.indexOf(par1Entity);

        if (index != -1) {
            if (index <= this.tickPosition) {
                this.tickPosition--;
            }

            this.loadedEntityList.remove(index);
        }

        // CraftBukkit end
        this.onEntityRemoved(par1Entity);
    }

    /**
     * Adds a IWorldAccess to the list of worldAccesses
     */
    public void addWorldAccess(final IWorldAccess par1IWorldAccess) {
        this.worldAccesses.add(par1IWorldAccess);
    }

    /**
     * Returns a list of bounding boxes that collide with aabb excluding the passed in entity's collision. Args: entity,
     * aabb
     */
    public List getCollidingBoundingBoxes(final Entity paramEntity, final AxisAlignedBB paramAxisAlignedBB) {
        this.collidingBoundingBoxes.clear();
        final int i = MathHelper.floor_double(paramAxisAlignedBB.minX);
        final int j = MathHelper.floor_double(paramAxisAlignedBB.maxX + 1.0D);
        final int k = MathHelper.floor_double(paramAxisAlignedBB.minY);
        final int m = MathHelper.floor_double(paramAxisAlignedBB.maxY + 1.0D);
        final int n = MathHelper.floor_double(paramAxisAlignedBB.minZ);
        final int i1 = MathHelper.floor_double(paramAxisAlignedBB.maxZ + 1.0D);
        for (int i2 = i; i2 < j; i2++) {
            for (int i3 = n; i3 < i1; i3++) {
                if (blockExists(i2, 64, i3)) {
                    for (int i4 = k - 1; i4 < m; i4++) {
                        final Block localBlock = Block.blocksList[getBlockId(i2, i4, i3)];
                        if (localBlock != null) {
                            localBlock.addCollisionBoxesToList(this, i2, i4, i3, paramAxisAlignedBB, this.collidingBoundingBoxes, paramEntity);
                        }
                    }
                }
            }
        }
        final double d = 0.25D;
        final List localList = getEntitiesWithinAABBExcludingEntity(paramEntity, paramAxisAlignedBB.expand(d, d, d));
        for (int i5 = 0; i5 < localList.size(); i5++) {
            AxisAlignedBB localAxisAlignedBB = ((Entity) localList.get(i5)).getBoundingBox();
            if ((localAxisAlignedBB != null) && (localAxisAlignedBB.intersectsWith(paramAxisAlignedBB))) {
                this.collidingBoundingBoxes.add(localAxisAlignedBB);
            }
            localAxisAlignedBB = paramEntity.getCollisionBox((Entity) localList.get(i5));
            if ((localAxisAlignedBB != null) && (localAxisAlignedBB.intersectsWith(paramAxisAlignedBB))) {
                this.collidingBoundingBoxes.add(localAxisAlignedBB);
            }
        }
        return this.collidingBoundingBoxes;
        /*this.collidingBoundingBoxes.clear();
        int i = MathHelper.floor_double(par2AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par2AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par2AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);

        if (CauldronHooks.checkBoundingBoxSize(par1Entity, par2AxisAlignedBB))
            return new ArrayList(); // Removing misbehaved entities

        // Spigot start
        int ystart = Math.max((k - 1), 0);

        for (int chunkx = (i >> 4); chunkx <= ((j - 1) >> 4); chunkx++) {
            int cx = chunkx << 4;

            for (int chunkz = (i1 >> 4); chunkz <= ((j1 - 1) >> 4); chunkz++) {
                if (!this.chunkExists(chunkx, chunkz)) {
                    continue;
                }

                int cz = chunkz << 4;
                Chunk chunk = this.getChunkFromChunkCoords(chunkx, chunkz);
                // Compute ranges within chunk
                int xstart = (i < cx) ? cx : i;
                int xend = (j < (cx + 16)) ? j : (cx + 16);
                int zstart = (i1 < cz) ? cz : i1;
                int zend = (j1 < (cz + 16)) ? j1 : (cz + 16);

                // Loop through blocks within chunk
                for (int x = xstart; x < xend; x++) {
                    for (int z = zstart; z < zend; z++) {
                        for (int y = ystart; y < l; y++) {
                            int blkid = chunk.getBlockID(x - cx, y, z - cz);

                            if (blkid > 0) {
                                Block block = Block.blocksList[blkid];

                                if (block != null) {
                                    block.addCollisionBoxesToList(this, x, y, z, par2AxisAlignedBB, this.collidingBoundingBoxes, par1Entity);
                                }
                            }
                        }
                    }
                }
            }
        }

        /*for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (this.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        lastBoundingBoxCoords = new ChunkCoordinates(k1, i2, l1);
                        Block block = Block.blocksList[this.getBlockId(k1, i2, l1)];

                        if (block != null)
                        {
                            block.addCollisionBoxesToList(this, k1, i2, l1, par2AxisAlignedBB, this.collidingBoundingBoxes, par1Entity);
                        }
                    }
                }
            }
        }*/// Spigot end

        /*double d0 = 0.25D;
        List list = this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB.expand(d0, d0, d0));

        net.minecraftforge.cauldron.CauldronHooks.logEntitySize(this, par1Entity, list); // Cauldron add logging for entity collisions

        for (int j2 = 0; j2 < list.size(); ++j2) {
            AxisAlignedBB axisalignedbb1 = ((Entity) list.get(j2)).getBoundingBox();

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB)) {
                this.collidingBoundingBoxes.add(axisalignedbb1);
            }

            axisalignedbb1 = par1Entity.getCollisionBox((Entity) list.get(j2));

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB)) {
                this.collidingBoundingBoxes.add(axisalignedbb1);
            }
        }

        return this.collidingBoundingBoxes; */
    }

    /**
     * calculates and returns a list of colliding bounding boxes within a given AABB
     */
    public List getCollidingBlockBounds(final AxisAlignedBB par1AxisAlignedBB) {
        this.collidingBoundingBoxes.clear();
        final int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        final int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.blockExists(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        final Block block = Block.blocksList[this.getBlockId(k1, i2, l1)];

                        if (block != null) {
                            block.addCollisionBoxesToList(this, k1, i2, l1, par1AxisAlignedBB, this.collidingBoundingBoxes, (Entity) null);
                        }
                    }
                }
            }
        }

        return this.collidingBoundingBoxes;
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int calculateSkylightSubtracted(final float par1) {
        final float f1 = this.getCelestialAngle(par1);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float) ((double) f2 * (1.0D - (double) (this.getRainStrength(par1) * 5.0F) / 16.0D));
        f2 = (float) ((double) f2 * (1.0D - (double) (this.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        f2 = 1.0F - f2;
        return (int) (f2 * 11.0F);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Removes a worldAccess from the worldAccesses object
     */
    public void removeWorldAccess(final IWorldAccess par1IWorldAccess) {
        this.worldAccesses.remove(par1IWorldAccess);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the sun brightness - checks time of day, rain and thunder
     */
    public float getSunBrightness(final float par1) {
        final float f1 = this.getCelestialAngle(par1);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.2F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float) ((double) f2 * (1.0D - (double) (this.getRainStrength(par1) * 5.0F) / 16.0D));
        f2 = (float) ((double) f2 * (1.0D - (double) (this.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        return f2 * 0.8F + 0.2F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Calculates the color for the skybox
     */
    public Vec3 getSkyColor(final Entity par1Entity, final float par2) {
        return provider.getSkyColor(par1Entity, par2);
    }

    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColorBody(final Entity par1Entity, final float par2) {
        final float f1 = this.getCelestialAngle(par2);
        float f2 = MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        final int i = MathHelper.floor_double(par1Entity.posX);
        final int j = MathHelper.floor_double(par1Entity.posZ);

        final int multiplier = ForgeHooksClient.getSkyBlendColour(this, i, j);

        float f4 = (float) (multiplier >> 16 & 255) / 255.0F;
        float f5 = (float) (multiplier >> 8 & 255) / 255.0F;
        float f6 = (float) (multiplier & 255) / 255.0F;
        f4 *= f2;
        f5 *= f2;
        f6 *= f2;
        final float f7 = this.getRainStrength(par2);
        float f8;
        float f9;

        if (f7 > 0.0F) {
            f8 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.6F;
            f9 = 1.0F - f7 * 0.75F;
            f4 = f4 * f9 + f8 * (1.0F - f9);
            f5 = f5 * f9 + f8 * (1.0F - f9);
            f6 = f6 * f9 + f8 * (1.0F - f9);
        }

        f8 = this.getWeightedThunderStrength(par2);

        if (f8 > 0.0F) {
            f9 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.2F;
            final float f10 = 1.0F - f8 * 0.75F;
            f4 = f4 * f10 + f9 * (1.0F - f10);
            f5 = f5 * f10 + f9 * (1.0F - f10);
            f6 = f6 * f10 + f9 * (1.0F - f10);
        }

        if (this.lastLightningBolt > 0) {
            f9 = (float) this.lastLightningBolt - par2;

            if (f9 > 1.0F) {
                f9 = 1.0F;
            }

            f9 *= 0.45F;
            f4 = f4 * (1.0F - f9) + 0.8F * f9;
            f5 = f5 * (1.0F - f9) + 0.8F * f9;
            f6 = f6 * (1.0F - f9) + 1.0F * f9;
        }

        return this.getWorldVec3Pool().getVecFromPool((double) f4, (double) f5, (double) f6);
    }

    /**
     * calls calculateCelestialAngle
     */
    public float getCelestialAngle(final float par1) {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), par1);
    }

    @SideOnly(Side.CLIENT)
    public int getMoonPhase() {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    /**
     * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
     */
    public float getCurrentMoonPhaseFactor() {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    /**
     * Return getCelestialAngle()*2*PI
     */
    public float getCelestialAngleRadians(final float par1) {
        final float f1 = this.getCelestialAngle(par1);
        return f1 * (float) Math.PI * 2.0F;
    }

    @SideOnly(Side.CLIENT)
    public Vec3 getCloudColour(final float par1) {
        return provider.drawClouds(par1);
    }

    @SideOnly(Side.CLIENT)
    public Vec3 drawCloudsBody(final float par1) {
        final float f1 = this.getCelestialAngle(par1);
        float f2 = MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        float f3 = (float) (this.cloudColour >> 16 & 255L) / 255.0F;
        float f4 = (float) (this.cloudColour >> 8 & 255L) / 255.0F;
        float f5 = (float) (this.cloudColour & 255L) / 255.0F;
        final float f6 = this.getRainStrength(par1);
        float f7;
        float f8;

        if (f6 > 0.0F) {
            f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            f8 = 1.0F - f6 * 0.95F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        f3 *= f2 * 0.9F + 0.1F;
        f4 *= f2 * 0.9F + 0.1F;
        f5 *= f2 * 0.85F + 0.15F;
        f7 = this.getWeightedThunderStrength(par1);

        if (f7 > 0.0F) {
            f8 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            final float f9 = 1.0F - f7 * 0.95F;
            f3 = f3 * f9 + f8 * (1.0F - f9);
            f4 = f4 * f9 + f8 * (1.0F - f9);
            f5 = f5 * f9 + f8 * (1.0F - f9);
        }

        return this.getWorldVec3Pool().getVecFromPool((double) f3, (double) f4, (double) f5);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns vector(ish) with R/G/B for fog
     */
    public Vec3 getFogColor(final float par1) {
        final float f1 = this.getCelestialAngle(par1);
        return this.provider.getFogColor(f1, par1);
    }

    /**
     * Gets the height to which rain/snow will fall. Calculates it if not already stored.
     */
    public int getPrecipitationHeight(final int par1, final int par2) {
        return this.getChunkFromBlockCoords(par1, par2).getPrecipitationHeight(par1 & 15, par2 & 15);
    }

    /**
     * Finds the highest block on the x, z coordinate that is solid and returns its y coord. Args x, z
     */
    public int getTopSolidOrLiquidBlock(int par1, int par2) {
        int par11 = par1;
        int par21 = par2;
        final Chunk chunk = this.getChunkFromBlockCoords(par11, par21);
        final int x = par11;
        final int z = par21;
        int k = chunk.getTopFilledSegment() + 15;
        par11 &= 15;

        for (par21 &= 15; k > 0; --k) {
            final int l = chunk.getBlockID(par11, k, par21);

            if (l != 0 && Block.blocksList[l].blockMaterial.blocksMovement() && Block.blocksList[l].blockMaterial != Material.leaves && !Block.blocksList[l].isBlockFoliage(this, x, k, z)) {
                return k + 1;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * How bright are stars in the sky
     */
    public float getStarBrightness(final float par1) {
        return provider.getStarBrightness(par1);
    }

    @SideOnly(Side.CLIENT)
    public float getStarBrightnessBody(final float par1) {
        final float f1 = this.getCelestialAngle(par1);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        return f2 * f2 * 0.5F;
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     */
    public void scheduleBlockUpdate(final int par1, final int par2, final int par3, final int par4, final int par5) {
    }

    public void scheduleBlockUpdateWithPriority(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
    }

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    public void scheduleBlockUpdateFromLoad(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities() {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");
        int i;
        Entity entity;
        CrashReport crashreport;
        CrashReportCategory crashreportcategory;
        // Cauldron start
        entitiesTicked = 0;
        tilesTicked = 0;
        // Cauldron end

        //TODO ZoomCodeStart
        if (MultiThreadSettings.getInstance().getSettings().getWorldSettings().isEnable())
            MinecraftServer.getServer().addWorldRunnable(new WeatherEffectsRunnable(this));
        else
            //TODO ZoomCodeEnd

            for (i = 0; i < this.weatherEffects.size(); ++i) {
                entity = (Entity) this.weatherEffects.get(i);
                // CraftBukkit start - Fixed an NPE
                if (entity == null) {
                    continue;
                }

                // CraftBukkit end

                try {
                    ++entity.ticksExisted;
                    entity.onUpdate();
                } catch (final Throwable throwable) {
                    crashreport = CrashReport.makeCrashReport(throwable, "Ticking entity");
                    crashreportcategory = crashreport.makeCategory("Entity being ticked");

                    if (entity == null) {
                        crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                    } else {
                        entity.addEntityCrashInfo(crashreportcategory);
                    }

                    if (ForgeDummyContainer.removeErroringEntities) {
                        FMLLog.severe(crashreport.getCompleteReport());
                        removeEntity(entity);
                    } else {
                        throw new ReportedException(crashreport);
                    }
                }

                if (entity.isDead) {
                    this.weatherEffects.remove(i--);
                }
            }

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int j;
        int k;

        for (i = 0; i < this.unloadedEntityList.size(); ++i) {
            entity = (Entity) this.unloadedEntityList.get(i);
            j = entity.chunkCoordX;
            k = entity.chunkCoordZ;

            if (entity.addedToChunk && this.chunkExists(j, k)) {
                this.getChunkFromChunkCoords(j, k).removeEntity(entity);
            }
        }

        for (i = 0; i < this.unloadedEntityList.size(); ++i) {
            this.onEntityRemoved((Entity) this.unloadedEntityList.get(i));
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");
        org.spigotmc.ActivationRange.activateEntities(this); // Spigot
        timings.entityTick.startTiming(); // Spigot

        // CraftBukkit start - Use field for loop variable
        for (this.tickPosition = 0; this.tickPosition < this.loadedEntityList.size(); ++this.tickPosition) {
            entity = (Entity) this.loadedEntityList.get(this.tickPosition);

            if (entity.ridingEntity != null) {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity) {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            this.theProfiler.startSection("tick");

            if (!entity.isDead) {
                try {
                    SpigotTimings.tickEntityTimer.startTiming(); // Spigot
                    // Cauldron start - mobius hooks
                    ProfilerSection.ENTITY_UPDATETIME.start(entity);
                    this.updateEntity(entity);
                    ProfilerSection.ENTITY_UPDATETIME.stop(entity);
                    // Cauldron end
                    SpigotTimings.tickEntityTimer.stopTiming(); // Spigot
                } catch (final Throwable throwable1) {
                    crashreport = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                    crashreportcategory = crashreport.makeCategory("Entity being ticked");
                    entity.addEntityCrashInfo(crashreportcategory);

                    if (ForgeDummyContainer.removeErroringEntities) {
                        FMLLog.severe(crashreport.getCompleteReport());
                        removeEntity(entity);
                    } else {
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("remove");

            if (entity.isDead) {
                j = entity.chunkCoordX;
                k = entity.chunkCoordZ;

                if (entity.addedToChunk && this.chunkExists(j, k)) {
                    this.getChunkFromChunkCoords(j, k).removeEntity(entity);
                }

                this.loadedEntityList.remove(this.tickPosition--); // CraftBukkit - Use field for loop variable
                this.onEntityRemoved(entity);
            }

            this.theProfiler.endSection();
        }

        timings.entityTick.stopTiming(); // Spigot
        this.theProfiler.endStartSection("tileEntities");
        timings.tileEntityTick.startTiming(); // Spigot
        this.scanningTileEntities = true;

        final Iterator iterator = this.loadedTileEntityList.iterator();

        while (iterator.hasNext()) {
            final TileEntity tileentity = (TileEntity) iterator.next();

            // Spigot start
            if (tileentity == null) {
                getServer().getLogger().severe("Cauldron has detected a null entity and has removed it, preventing a crash");
                iterator.remove();
                continue;
            }

            // Spigot end

            if (!tileentity.isInvalid() && tileentity.hasWorldObj() && CauldronHooks.canTileEntityTick(tileentity, this) && this.blockExists(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord)) {
                try {
                    tileentity.tickTimer.startTiming(); // Spigot
                    tilesTicked++;
                    ProfilerSection.TILEENT_UPDATETIME.start(tileentity); // mobius hook
                    tileentity.updateEntity();
                    ProfilerSection.TILEENT_UPDATETIME.stop(tileentity); // mobius hook
                    tileentity.tickTimer.stopTiming(); // Spigot
                } catch (final Throwable throwable2) {
                    tileentity.tickTimer.stopTiming(); // Spigot
                    crashreport = CrashReport.makeCrashReport(throwable2, "Ticking tile entity");
                    crashreportcategory = crashreport.makeCategory("Tile entity being ticked");
                    tileentity.func_85027_a(crashreportcategory);
                    if (ForgeDummyContainer.removeErroringTileEntities) {
                        FMLLog.severe(crashreport.getCompleteReport());
                        tileentity.invalidate();
                        setBlockToAir(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
                    } else {
                        throw new ReportedException(crashreport);
                    }
                }
            }

            if (tileentity.isInvalid()) {
                iterator.remove();

                if (this.chunkExists(tileentity.xCoord >> 4, tileentity.zCoord >> 4)) {
                    final Chunk chunk = this.getChunkFromChunkCoords(tileentity.xCoord >> 4, tileentity.zCoord >> 4);

                    if (chunk != null) {
                        chunk.cleanChunkBlockTileEntity(tileentity.xCoord & 15, tileentity.yCoord, tileentity.zCoord & 15);
                    }
                }
            }
        }

        timings.tileEntityTick.stopTiming(); // Spigot
        timings.tileEntityPending.startTiming(); // Spigot
        this.scanningTileEntities = false;

        if (!this.entityRemoval.isEmpty()) {
            for (final Object tile : entityRemoval) {
                ((TileEntity) tile).onChunkUnload();
            }
            this.loadedTileEntityList.removeAll(this.entityRemoval);
            this.entityRemoval.clear();
        }

        this.theProfiler.endStartSection("pendingTileEntities");

        if (!this.addedTileEntityList.isEmpty()) {
            for (int l = 0; l < this.addedTileEntityList.size(); ++l) {
                final TileEntity tileentity1 = (TileEntity) this.addedTileEntityList.get(l);

                if (!tileentity1.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity1)) {
                        this.loadedTileEntityList.add(tileentity1);
                    }
                } else {
                    if (this.chunkExists(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4)) {
                        final Chunk chunk1 = this.getChunkFromChunkCoords(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4);

                        if (chunk1 != null) {
                            chunk1.cleanChunkBlockTileEntity(tileentity1.xCoord & 15, tileentity1.yCoord, tileentity1.zCoord & 15);
                        }
                    }
                }
            }

            this.addedTileEntityList.clear();
        }

        timings.tileEntityPending.stopTiming(); // Spigot
        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public void addTileEntity(final Collection par1Collection) {
        final Collection dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList; // Cauldron - List -> Collection for CB loadedTileEntityList type change
        for (final Object entity : par1Collection) {
            if (CauldronHooks.canUpdate((TileEntity) entity)) {
                dest.add(entity);
            }
        }
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded. Args: entity
     */
    public void updateEntity(final Entity par1Entity) {
        this.updateEntityWithOptionalForce(par1Entity, true);
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void updateEntityWithOptionalForce(final Entity par1Entity, final boolean par2) {
        final int i = MathHelper.floor_double(par1Entity.posX);
        final int j = MathHelper.floor_double(par1Entity.posZ);

        final boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(i >> 4, j >> 4));
        final byte b0 = isForced ? (byte) 0 : 32;
        boolean canUpdate = !par2 || this.checkChunksExist(i - b0, 0, j - b0, i + b0, 0, j + b0);
        boolean forceUpdate = false; // Cauldron
        if (!canUpdate) {
            final EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(par1Entity);
            MinecraftForge.EVENT_BUS.post(event);
            canUpdate = event.canUpdate;
            forceUpdate = canUpdate; // Cauldron
        }
        // Spigot start
        if (!isForced && !forceUpdate && !org.spigotmc.ActivationRange.checkIfActive(par1Entity)) // Cauldron - ignore if forge event forced update or entity is in forced chunk
        {
            par1Entity.ticksExisted++;
            par1Entity.inactiveTick();
            return;
        }
        // Spigot end
        if (canUpdate) {
            par1Entity.tickTimer.startTiming();
            entitiesTicked++;
            par1Entity.lastTickPosX = par1Entity.posX;
            par1Entity.lastTickPosY = par1Entity.posY;
            par1Entity.lastTickPosZ = par1Entity.posZ;
            par1Entity.prevRotationYaw = par1Entity.rotationYaw;
            par1Entity.prevRotationPitch = par1Entity.rotationPitch;

            if (par2 && par1Entity.addedToChunk) {
                ++par1Entity.ticksExisted;

                if (par1Entity.ridingEntity != null) {
                    par1Entity.updateRidden();
                } else {
                    par1Entity.onUpdate();
                }
            }

            this.theProfiler.startSection("chunkCheck");

            if (Double.isNaN(par1Entity.posX) || Double.isInfinite(par1Entity.posX)) {
                par1Entity.posX = par1Entity.lastTickPosX;
            }

            if (Double.isNaN(par1Entity.posY) || Double.isInfinite(par1Entity.posY)) {
                par1Entity.posY = par1Entity.lastTickPosY;
            }

            if (Double.isNaN(par1Entity.posZ) || Double.isInfinite(par1Entity.posZ)) {
                par1Entity.posZ = par1Entity.lastTickPosZ;
            }

            if (Double.isNaN((double) par1Entity.rotationPitch) || Double.isInfinite((double) par1Entity.rotationPitch)) {
                par1Entity.rotationPitch = par1Entity.prevRotationPitch;
            }

            if (Double.isNaN((double) par1Entity.rotationYaw) || Double.isInfinite((double) par1Entity.rotationYaw)) {
                par1Entity.rotationYaw = par1Entity.prevRotationYaw;
            }

            final int k = MathHelper.floor_double(par1Entity.posX / 16.0D);
            final int l = MathHelper.floor_double(par1Entity.posY / 16.0D);
            final int i1 = MathHelper.floor_double(par1Entity.posZ / 16.0D);

            if (!par1Entity.addedToChunk || par1Entity.chunkCoordX != k || par1Entity.chunkCoordY != l || par1Entity.chunkCoordZ != i1) {
                if (par1Entity.addedToChunk && this.chunkExists(par1Entity.chunkCoordX, par1Entity.chunkCoordZ)) {
                    this.getChunkFromChunkCoords(par1Entity.chunkCoordX, par1Entity.chunkCoordZ).removeEntityAtIndex(par1Entity, par1Entity.chunkCoordY);
                }

                if (this.chunkExists(k, i1)) {
                    par1Entity.addedToChunk = true;
                    this.getChunkFromChunkCoords(k, i1).addEntity(par1Entity);
                } else {
                    par1Entity.addedToChunk = false;
                }
            }

            this.theProfiler.endSection();

            if (par2 && par1Entity.addedToChunk && par1Entity.riddenByEntity != null) {
                if (!par1Entity.riddenByEntity.isDead && par1Entity.riddenByEntity.ridingEntity == par1Entity) {
                    this.updateEntity(par1Entity.riddenByEntity);
                } else {
                    par1Entity.riddenByEntity.ridingEntity = null;
                    par1Entity.riddenByEntity = null;
                }
            }
            par1Entity.tickTimer.stopTiming(); // Spigot
        }
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB
     */
    public boolean checkNoEntityCollision(final AxisAlignedBB par1AxisAlignedBB) {
        return this.checkNoEntityCollision(par1AxisAlignedBB, (Entity) null);
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB, excluding the given entity
     */
    public boolean checkNoEntityCollision(final AxisAlignedBB par1AxisAlignedBB, final Entity par2Entity) {
        final List list = this.getEntitiesWithinAABBExcludingEntity((Entity) null, par1AxisAlignedBB);

        for (int i = 0; i < list.size(); ++i) {
            final Entity entity1 = (Entity) list.get(i);

            if (!entity1.isDead && entity1.preventEntitySpawning && entity1 != par2Entity) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean checkBlockCollision(final AxisAlignedBB par1AxisAlignedBB) {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (par1AxisAlignedBB.minX < 0.0D) {
            --i;
        }

        if (par1AxisAlignedBB.minY < 0.0D) {
            --k;
        }

        if (par1AxisAlignedBB.minZ < 0.0D) {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns if any of the blocks within the aabb are liquids. Args: aabb
     */
    public boolean isAnyLiquid(final AxisAlignedBB par1AxisAlignedBB) {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (par1AxisAlignedBB.minX < 0.0D) {
            --i;
        }

        if (par1AxisAlignedBB.minY < 0.0D) {
            --k;
        }

        if (par1AxisAlignedBB.minZ < 0.0D) {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockMaterial.isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns whether or not the given bounding box is on fire or not
     */
    public boolean isBoundingBoxBurning(final AxisAlignedBB par1AxisAlignedBB) {
        final int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        final int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (this.checkChunksExist(i, k, i1, j, l, j1)) {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        final int j2 = this.getBlockId(k1, l1, i2);

                        if (j2 == Block.fire.blockID || j2 == Block.lavaMoving.blockID || j2 == Block.lavaStill.blockID) {
                            return true;
                        } else {
                            final Block block = Block.blocksList[j2];
                            if (block != null && block.isBlockBurning(this, k1, l1, i2)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public boolean handleMaterialAcceleration(final AxisAlignedBB par1AxisAlignedBB, final Material par2Material, final Entity par3Entity) {
        final int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        final int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (!this.checkChunksExist(i, k, i1, j, l, j1)) {
            return false;
        } else {
            boolean flag = false;
            Vec3 vec3 = this.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        final Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                        if (block != null && block.blockMaterial == par2Material) {
                            final double d0 = (double) ((float) (l1 + 1) - BlockFluid.getFluidHeightPercent(this.getBlockMetadata(k1, l1, i2)));

                            if ((double) l >= d0) {
                                flag = true;
                                block.velocityToAddToEntity(this, k1, l1, i2, par3Entity, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.lengthVector() > 0.0D && par3Entity.isPushedByWater()) {
                vec3 = vec3.normalize();
                final double d1 = 0.014D;
                par3Entity.motionX += vec3.xCoord * d1;
                par3Entity.motionY += vec3.yCoord * d1;
                par3Entity.motionZ += vec3.zCoord * d1;
            }

            vec3.myVec3LocalPool.release(vec3); // CraftBukkit - pop it - we're done
            return flag;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean isMaterialInBB(final AxisAlignedBB par1AxisAlignedBB, final Material par2Material) {
        final int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        final int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockMaterial == par2Material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if the given AABB is in the material given. Used while swimming.
     */
    public boolean isAABBInMaterial(final AxisAlignedBB par1AxisAlignedBB, final Material par2Material) {
        final int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        final int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        final int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockMaterial == par2Material) {
                        final int j2 = this.getBlockMetadata(k1, l1, i2);
                        double d0 = (double) (l1 + 1);

                        if (j2 < 8) {
                            d0 = (double) (l1 + 1) - (double) j2 / 8.0D;
                        }

                        if (d0 >= par1AxisAlignedBB.minY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Creates an explosion. Args: entity, x, y, z, strength
     */
    public Explosion createExplosion(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final boolean par9) {
        return this.newExplosion(par1Entity, par2, par4, par6, par8, false, par9);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final boolean par9, final boolean par10) {
        final Explosion explosion = new Explosion(this, par1Entity, par2, par4, par6, par8);
        explosion.isFlaming = par9;
        explosion.isSmoking = par10;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float getBlockDensity(final Vec3 par1Vec3, final AxisAlignedBB par2AxisAlignedBB) {
        final double d0 = 1.0D / ((par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * 2.0D + 1.0D);
        final double d1 = 1.0D / ((par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * 2.0D + 1.0D);
        final double d2 = 1.0D / ((par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * 2.0D + 1.0D);
        int i = 0;
        int j = 0;
        final Vec3 vec32 = par1Vec3.myVec3LocalPool.getVecFromPool(0, 0, 0); // CraftBukkit

        for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                    final double d3 = par2AxisAlignedBB.minX + (par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * (double) f;
                    final double d4 = par2AxisAlignedBB.minY + (par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * (double) f1;
                    final double d5 = par2AxisAlignedBB.minZ + (par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * (double) f2;

                    if (this.clip(vec32.func_72439_b_CodeFix_Public(d3, d4, d5), par1Vec3) == null)   // CraftBukkit
                    {
                        ++i;
                    }

                    ++j;
                }
            }
        }

        vec32.myVec3LocalPool.release(vec32); // CraftBukkit
        return (float) i / (float) j;
    }

    /**
     * If the block in the given direction of the given coordinate is fire, extinguish it. Args: Player, X,Y,Z,
     * blockDirection
     */
    public boolean extinguishFire(final EntityPlayer par1EntityPlayer, int par2, int par3, int par4, final int par5) {
        int par31 = par3;
        int par41 = par4;
        int par21 = par2;
        if (par5 == 0) {
            --par31;
        }

        if (par5 == 1) {
            ++par31;
        }

        if (par5 == 2) {
            --par41;
        }

        if (par5 == 3) {
            ++par41;
        }

        if (par5 == 4) {
            --par21;
        }

        if (par5 == 5) {
            ++par21;
        }

        if (this.getBlockId(par21, par31, par41) == Block.fire.blockID) {
            this.playAuxSFXAtEntity(par1EntityPlayer, 1004, par21, par31, par41, 0);
            this.setBlockToAir(par21, par31, par41);
            return true;
        } else {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * This string is 'All: (number of loaded entities)' Viewable by press ing F3
     */
    public String getDebugLoadedEntities() {
        return "All: " + this.loadedEntityList.size();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the name of the current chunk provider, by calling chunkprovider.makeString()
     */
    public String getProviderName() {
        return this.chunkProvider.makeString();
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getBlockTileEntity(final int par1, final int par2, final int par3) {
        if (par2 >= 0 && par2 < 256) {
            TileEntity tileentity = null;
            int l;
            TileEntity tileentity1;

            if (this.scanningTileEntities) {
                for (l = 0; l < this.addedTileEntityList.size(); ++l) {
                    tileentity1 = (TileEntity) this.addedTileEntityList.get(l);

                    if (!tileentity1.isInvalid() && tileentity1.xCoord == par1 && tileentity1.yCoord == par2 && tileentity1.zCoord == par3) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            if (tileentity == null) {
                final Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);

                if (chunk != null) {
                    tileentity = chunk.getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
                }
            }

            if (tileentity == null) {
                for (l = 0; l < this.addedTileEntityList.size(); ++l) {
                    tileentity1 = (TileEntity) this.addedTileEntityList.get(l);

                    if (!tileentity1.isInvalid() && tileentity1.xCoord == par1 && tileentity1.yCoord == par2 && tileentity1.zCoord == par3) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            return tileentity;
        } else {
            return null;
        }
    }

    /**
     * Sets the TileEntity for a given block in X, Y, Z coordinates
     */
    public void setBlockTileEntity(final int par1, final int par2, final int par3, final TileEntity par4TileEntity) {
        if (par4TileEntity == null || par4TileEntity.isInvalid()) {
            return;
        }

        if (CauldronHooks.canUpdate(par4TileEntity)) {
            if (scanningTileEntities) {
                final Iterator iterator = addedTileEntityList.iterator();
                while (iterator.hasNext()) {
                    final TileEntity tileentity1 = (TileEntity) iterator.next();

                    if (tileentity1.xCoord == par1 && tileentity1.yCoord == par2 && tileentity1.zCoord == par3) {
                        tileentity1.invalidate();
                        iterator.remove();
                    }
                }
                addedTileEntityList.add(par4TileEntity);
            } else {
                loadedTileEntityList.add(par4TileEntity);
            }
        }

        final Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
        if (chunk != null) {
            chunk.setChunkBlockTileEntity(par1 & 15, par2, par3 & 15, par4TileEntity);
        }
        //notify tile changes
        func_96440_m(par1, par2, par3, 0);
    }

    /**
     * Removes the TileEntity for a given block in X,Y,Z coordinates
     */
    public void removeBlockTileEntity(final int par1, final int par2, final int par3) {
        final Chunk chunk = getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
        if (chunk != null) {
            chunk.removeChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
        }
        //notify tile changes
        func_96440_m(par1, par2, par3, 0);
    }

    /**
     * adds tile entity to despawn list (renamed from markEntityForDespawn)
     */
    public void markTileEntityForDespawn(final TileEntity par1TileEntity) {
        this.entityRemoval.add(par1TileEntity);
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean isBlockOpaqueCube(final int par1, final int par2, final int par3) {
        final Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return block == null ? false : block.isOpaqueCube();
    }

    /**
     * Indicate if a material is a normal solid opaque cube.
     */
    public boolean isBlockNormalCube(final int par1, final int par2, final int par3) {
        final Block block = Block.blocksList[getBlockId(par1, par2, par3)];
        return block != null && block.isBlockNormalCube(this, par1, par2, par3);
    }

    public boolean isBlockFullCube(final int par1, final int par2, final int par3) {
        final int l = this.getBlockId(par1, par2, par3);

        if (l != 0 && Block.blocksList[l] != null) {
            final AxisAlignedBB axisalignedbb = Block.blocksList[l].getCollisionBoundingBoxFromPool(this, par1, par2, par3);
            return axisalignedbb != null && axisalignedbb.getAverageEdgeLength() >= 1.0D;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the block at the given coordinate has a solid (buildable) top surface.
     */
    public boolean doesBlockHaveSolidTopSurface(final int par1, final int par2, final int par3) {
        return isBlockSolidOnSide(par1, par2, par3, ForgeDirection.UP);
    }

    /**
     * Performs check to see if the block is a normal, solid block, or if the metadata of the block indicates that its
     * facing puts its solid side upwards. (inverted stairs, for example)
     */
    @Deprecated //DO NOT USE THIS!!! USE doesBlockHaveSolidTopSurface
    public boolean isBlockTopFacingSurfaceSolid(final Block par1Block, final int par2) {
        // -.-  Mojang PLEASE make this location sensitive, you have no reason not to.
        return par1Block == null ? false : (par1Block.blockMaterial.isOpaque() && par1Block.renderAsNormalBlock() ? true : (par1Block instanceof BlockStairs ? (par2 & 4) == 4 : (par1Block instanceof BlockHalfSlab ? (par2 & 8) == 8 : (par1Block instanceof BlockHopper ? true : (par1Block instanceof BlockSnow ? (par2 & 7) == 7 : false)))));
    }

    /**
     * Checks if the block is a solid, normal cube. If the chunk does not exist, or is not loaded, it returns the
     * boolean parameter.
     */
    public boolean isBlockNormalCubeDefault(final int par1, final int par2, final int par3, final boolean par4) {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000) {
            final Chunk chunk = this.chunkProvider.provideChunk(par1 >> 4, par3 >> 4);

            if (chunk != null && !chunk.isEmpty()) {
                final Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];
                return block == null ? false : isBlockNormalCube(par1, par2, par3);
            } else {
                return par4;
            }
        } else {
            return par4;
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void calculateInitialSkylight() {
        final int i = this.calculateSkylightSubtracted(1.0F);

        if (i != this.skylightSubtracted) {
            this.skylightSubtracted = i;
        }
    }

    /**
     * Set which types of mobs are allowed to spawn (peaceful vs hostile).
     */
    public void setAllowedSpawnTypes(final boolean par1, final boolean par2) {
        provider.setAllowedSpawnTypes(par1, par2);
    }

    /**
     * Runs a single tick for the world
     */
    public void tick() {
        this.updateWeather();
    }

    /**
     * Called from World constructor to set rainingStrength and thunderingStrength
     */
    private void calculateInitialWeather() {
        provider.calculateInitialWeather();
    }

    public void calculateInitialWeatherBody() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0F;

            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather() {
        provider.updateWeather();
    }

    public void updateWeatherBody() {
        if (!this.provider.hasNoSky) {
            int i = this.worldInfo.getThunderTime();

            if (i <= 0) {
                if (this.worldInfo.isThundering()) {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                } else {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                --i;
                this.worldInfo.setThunderTime(i);

                if (i <= 0) {
                    // CraftBukkit start
                    final ThunderChangeEvent thunder = new ThunderChangeEvent(this.getWorld(), !this.worldInfo.isThundering());
                    this.getServer().getPluginManager().callEvent(thunder);
                    if (!thunder.isCancelled()) {
                        this.worldInfo.setThundering(!this.worldInfo.isThundering());
                    }
                    // CraftBukkit end
                }
            }

            int j = this.worldInfo.getRainTime();

            if (j <= 0) {
                if (this.worldInfo.isRaining()) {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                } else {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                --j;
                this.worldInfo.setRainTime(j);

                if (j <= 0) {
                    // CraftBukkit start
                    final WeatherChangeEvent weather = new WeatherChangeEvent(this.getWorld(), !this.worldInfo.isRaining());
                    this.getServer().getPluginManager().callEvent(weather);
                    if (!weather.isCancelled()) {
                        this.worldInfo.setRaining(!this.worldInfo.isRaining());
                    }
                    // CraftBukkit end
                }
            }

            this.prevRainingStrength = this.rainingStrength;

            if (this.worldInfo.isRaining()) {
                this.rainingStrength = (float) ((double) this.rainingStrength + 0.01D);
            } else {
                this.rainingStrength = (float) ((double) this.rainingStrength - 0.01D);
            }

            if (this.rainingStrength < 0.0F) {
                this.rainingStrength = 0.0F;
            }

            if (this.rainingStrength > 1.0F) {
                this.rainingStrength = 1.0F;
            }

            this.prevThunderingStrength = this.thunderingStrength;

            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = (float) ((double) this.thunderingStrength + 0.01D);
            } else {
                this.thunderingStrength = (float) ((double) this.thunderingStrength - 0.01D);
            }

            if (this.thunderingStrength < 0.0F) {
                this.thunderingStrength = 0.0F;
            }

            if (this.thunderingStrength > 1.0F) {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    public void toggleRain() {
        provider.toggleRain();
    }

    // Spigot start
    protected float modifiedOdds = 100.0F;
    public float growthOdds = 100.0F;

    protected void setActivePlayerChunksAndCheckLight() {
        // Cauldron start - add persistent chunks to be ticked for growth
        activeChunkSet.clear();
        activeChunkSet_CB.clear();
        for (final ChunkCoordIntPair chunk : getPersistentChunks().keySet()) {
            this.activeChunkSet.add(chunk);
            final long key = chunkToKey(chunk.chunkXPos, chunk.chunkZPos);
            activeChunkSet_CB.put(key, (short) 0);
            if (!this.chunkExists(chunk.chunkXPos, chunk.chunkZPos)) {
                ((WorldServer) this).theChunkProviderServer.loadChunk(chunk.chunkXPos, chunk.chunkZPos);
            }
        }
        // Cauldron end

        // this.chunkTickList.clear(); // CraftBukkit - removed
        this.theProfiler.startSection("buildList");
        int i;
        EntityPlayer entityplayer;
        final int j;
        final int k;
        final int optimalChunks = spigotConfig.chunksPerTick;

        if (optimalChunks <= 0) // Cauldron tick chunks even if no players are logged in
        {
            return;
        }

        // Keep chunks with growth inside of the optimal chunk range
        final int chunksPerPlayer = Math.min(200, Math.max(1, (int) (((optimalChunks - playerEntities.size()) / (double) playerEntities.size()) + 0.5)));
        int randRange = 3 + chunksPerPlayer / 30;
        // Limit to normal tick radius - including view distance
        randRange = (randRange > chunkTickRadius) ? chunkTickRadius : randRange;
        // Cauldron start - validate view radius
        if (randRange < 1) {
            throw new IllegalArgumentException("Too small view radius! edit server.properties and change view-distance to a value > 0.");
        }
        // Cauldron end
        // odds of growth happening vs growth happening in vanilla
        this.growthOdds = this.modifiedOdds = Math.max(35, Math.min(100, ((chunksPerPlayer + 1) * 100.0F) / 15.0F));
        // Spigot end
        for (i = 0; i < this.playerEntities.size(); ++i) {
            entityplayer = (EntityPlayer) this.playerEntities.get(i);
            final int chunkX = MathHelper.floor_double(entityplayer.posX / 16.0D);
            final int chunkZ = MathHelper.floor_double(entityplayer.posZ / 16.0D);
            // Spigot start - Always update the chunk the player is on
            final long key = chunkToKey(chunkX, chunkZ);
            final int existingPlayers = Math.max(0, activeChunkSet_CB.get(key)); //filter out -1's
            activeChunkSet_CB.put(key, (short) (existingPlayers + 1));
            activeChunkSet.add(new ChunkCoordIntPair(chunkX, chunkZ)); // Cauldron - vanilla compatibility

            // Check and see if we update the chunks surrounding the player this tick
            for (int chunk = 0; chunk < chunksPerPlayer; chunk++) {
                final int dx = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(randRange);
                final int dz = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(randRange);
                final long hash = chunkToKey(dx + chunkX, dz + chunkZ);

                if (!activeChunkSet_CB.contains(hash) && this.chunkExists(dx + chunkX, dz + chunkZ)) {
                    activeChunkSet_CB.put(hash, (short) -1); //no players
                    activeChunkSet.add(new ChunkCoordIntPair(dx + chunkX, dz + chunkZ)); // Cauldron - vanilla compatibility
                }
            }
        }
        /*for (ChunkCoordIntPair coord : getPersistentChunks().keySet())
        {
            long key = chunkToKey(coord.chunkXPos, coord.chunkZPos);
            activeChunkSet.put(key, (short) - 1); //no players
        }*/

        // Spigot End
        this.theProfiler.endSection();

        if (this.ambientTickCountdown > 0) {
            --this.ambientTickCountdown;
        }

        this.theProfiler.startSection("playerCheckLight");

        if (spigotConfig.randomLightUpdates && !this.playerEntities.isEmpty()) // Spigot
        {
            i = this.rand.nextInt(this.playerEntities.size());
            entityplayer = (EntityPlayer) this.playerEntities.get(i);
            j = MathHelper.floor_double(entityplayer.posX) + this.rand.nextInt(11) - 5;
            k = MathHelper.floor_double(entityplayer.posY) + this.rand.nextInt(11) - 5;
            final int j1 = MathHelper.floor_double(entityplayer.posZ) + this.rand.nextInt(11) - 5;
            this.updateAllLightTypes(j, k, j1);
        }

        this.theProfiler.endSection();
    }

    protected void moodSoundAndLightCheck(final int par1, final int par2, final Chunk par3Chunk) {
        this.theProfiler.endStartSection("moodSound");

        if (this.ambientTickCountdown == 0 && !this.isRemote) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            final int k = this.updateLCG >> 2;
            int l = k & 15;
            int i1 = k >> 8 & 15;
            final int j1 = k >> 16 & 255; // CraftBukkit - 127 -> 255
            final int k1 = par3Chunk.getBlockID(l, j1, i1);
            l += par1;
            i1 += par2;

            if (k1 == 0 && this.getFullBlockLightValue(l, j1, i1) <= this.rand.nextInt(8) && this.getSavedLightValue(EnumSkyBlock.Sky, l, j1, i1) <= 0) {
                final EntityPlayer entityplayer = this.getClosestPlayer((double) l + 0.5D, (double) j1 + 0.5D, (double) i1 + 0.5D, 8.0D);

                if (entityplayer != null && entityplayer.getDistanceSq((double) l + 0.5D, (double) j1 + 0.5D, (double) i1 + 0.5D) > 4.0D) {
                    this.playSoundEffect((double) l + 0.5D, (double) j1 + 0.5D, (double) i1 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        this.theProfiler.endStartSection("checkLight");
        par3Chunk.enqueueRelightChecks();
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    protected void tickBlocksAndAmbiance() {
        this.setActivePlayerChunksAndCheckLight();
    }

    /**
     * checks to see if a given block is both water and is cold enough to freeze
     */
    public boolean isBlockFreezable(final int par1, final int par2, final int par3) {
        return this.canBlockFreeze(par1, par2, par3, false);
    }

    /**
     * checks to see if a given block is both water and has at least one immediately adjacent non-water block
     */
    public boolean isBlockFreezableNaturally(final int par1, final int par2, final int par3) {
        return this.canBlockFreeze(par1, par2, par3, true);
    }

    /**
     * checks to see if a given block is both water, and cold enough to freeze - if the par4 boolean is set, this will
     * only return true if there is a non-water block immediately adjacent to the specified block
     */
    public boolean canBlockFreeze(final int par1, final int par2, final int par3, final boolean par4) {
        return provider.canBlockFreeze(par1, par2, par3, par4);
    }

    public boolean canBlockFreezeBody(final int par1, final int par2, final int par3, final boolean par4) {
        final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(par1, par3);
        final float f = biomegenbase.getFloatTemperature();

        if (f > 0.15F) {
            return false;
        } else {
            if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10) {
                final int l = this.getBlockId(par1, par2, par3);

                if ((l == Block.waterStill.blockID || l == Block.waterMoving.blockID) && this.getBlockMetadata(par1, par2, par3) == 0) {
                    if (!par4) {
                        return true;
                    }

                    boolean flag1 = true;

                    if (flag1 && this.getBlockMaterial(par1 - 1, par2, par3) != Material.water) {
                        flag1 = false;
                    }

                    if (flag1 && this.getBlockMaterial(par1 + 1, par2, par3) != Material.water) {
                        flag1 = false;
                    }

                    if (flag1 && this.getBlockMaterial(par1, par2, par3 - 1) != Material.water) {
                        flag1 = false;
                    }

                    if (flag1 && this.getBlockMaterial(par1, par2, par3 + 1) != Material.water) {
                        flag1 = false;
                    }

                    if (!flag1) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * Tests whether or not snow can be placed at a given location
     */
    public boolean canSnowAt(final int par1, final int par2, final int par3) {
        return provider.canSnowAt(par1, par2, par3);
    }

    public boolean canSnowAtBody(final int par1, final int par2, final int par3) {
        final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(par1, par3);
        final float f = biomegenbase.getFloatTemperature();

        if (f > 0.15F) {
            return false;
        } else {
            if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10) {
                final int l = this.getBlockId(par1, par2 - 1, par3);
                final int i1 = this.getBlockId(par1, par2, par3);

                if (i1 == 0 && Block.snow.canPlaceBlockAt(this, par1, par2, par3) && l != 0 && l != Block.ice.blockID && Block.blocksList[l].blockMaterial.blocksMovement()) {
                    return true;
                }
            }

            return false;
        }
    }

    public void updateAllLightTypes(final int par1, final int par2, final int par3) {
        if (!this.provider.hasNoSky) {
            this.updateLightByType(EnumSkyBlock.Sky, par1, par2, par3);
        }

        this.updateLightByType(EnumSkyBlock.Block, par1, par2, par3);
    }

    private int computeLightValue(final int par1, final int par2, final int par3, final EnumSkyBlock par4EnumSkyBlock) {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky && this.canBlockSeeTheSky(par1, par2, par3)) {
            return 15;
        } else {
            final int l = this.getBlockId(par1, par2, par3);
            final Block block = Block.blocksList[l];
            final int blockLight = (block == null ? 0 : block.getLightValue(this, par1, par2, par3));
            int i1 = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : blockLight;
            int j1 = (block == null ? 0 : block.getLightOpacity(this, par1, par2, par3));

            if (j1 >= 15 && blockLight > 0) {
                j1 = 1;
            }

            if (j1 < 1) {
                j1 = 1;
            }

            if (j1 >= 15) {
                return 0;
            } else if (i1 >= 14) {
                return i1;
            } else {
                for (int k1 = 0; k1 < 6; ++k1) {
                    final int l1 = par1 + Facing.offsetsXForSide[k1];
                    final int i2 = par2 + Facing.offsetsYForSide[k1];
                    final int j2 = par3 + Facing.offsetsZForSide[k1];
                    final int k2 = this.getSavedLightValue(par4EnumSkyBlock, l1, i2, j2) - j1;

                    if (k2 > i1) {
                        i1 = k2;
                    }

                    if (i1 >= 14) {
                        return i1;
                    }
                }

                return i1;
            }
        }
    }

    public void updateLightByType(final EnumSkyBlock par1EnumSkyBlock, final int par2, final int par3, final int par4) {
        if (this.doChunksNearChunkExist(par2, par3, par4, 17)) {
            int l = 0;
            int i1 = 0;
            this.theProfiler.startSection("getBrightness");
            final int j1 = this.getSavedLightValue(par1EnumSkyBlock, par2, par3, par4);
            final int k1 = this.computeLightValue(par2, par3, par4, par1EnumSkyBlock);
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;
            int l3;

            if (k1 > j1) {
                this.lightUpdateBlockList[i1++] = 133152;
            } else if (k1 < j1) {
                this.lightUpdateBlockList[i1++] = 133152 | j1 << 18;

                while (l < i1) {
                    l1 = this.lightUpdateBlockList[l++];
                    i2 = (l1 & 63) - 32 + par2;
                    j2 = (l1 >> 6 & 63) - 32 + par3;
                    k2 = (l1 >> 12 & 63) - 32 + par4;
                    l2 = l1 >> 18 & 15;
                    i3 = this.getSavedLightValue(par1EnumSkyBlock, i2, j2, k2);

                    if (i3 == l2) {
                        this.setLightValue(par1EnumSkyBlock, i2, j2, k2, 0);

                        if (l2 > 0) {
                            j3 = MathHelper.abs_int(i2 - par2);
                            l3 = MathHelper.abs_int(j2 - par3);
                            k3 = MathHelper.abs_int(k2 - par4);

                            if (j3 + l3 + k3 < 17) {
                                for (int i4 = 0; i4 < 6; ++i4) {
                                    final int j4 = i2 + Facing.offsetsXForSide[i4];
                                    final int k4 = j2 + Facing.offsetsYForSide[i4];
                                    final int l4 = k2 + Facing.offsetsZForSide[i4];
                                    final Block block = Block.blocksList[getBlockId(j4, k4, l4)];
                                    final int blockOpacity = (block == null ? 0 : block.getLightOpacity(this, j4, k4, l4));
                                    final int i5 = Math.max(1, blockOpacity);
                                    i3 = this.getSavedLightValue(par1EnumSkyBlock, j4, k4, l4);

                                    if (i3 == l2 - i5 && i1 < this.lightUpdateBlockList.length) {
                                        this.lightUpdateBlockList[i1++] = j4 - par2 + 32 | k4 - par3 + 32 << 6 | l4 - par4 + 32 << 12 | l2 - i5 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                l = 0;
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("checkedPosition < toCheckCount");

            while (l < i1) {
                l1 = this.lightUpdateBlockList[l++];
                i2 = (l1 & 63) - 32 + par2;
                j2 = (l1 >> 6 & 63) - 32 + par3;
                k2 = (l1 >> 12 & 63) - 32 + par4;
                l2 = this.getSavedLightValue(par1EnumSkyBlock, i2, j2, k2);
                i3 = this.computeLightValue(i2, j2, k2, par1EnumSkyBlock);

                if (i3 != l2) {
                    this.setLightValue(par1EnumSkyBlock, i2, j2, k2, i3);

                    if (i3 > l2) {
                        j3 = Math.abs(i2 - par2);
                        l3 = Math.abs(j2 - par3);
                        k3 = Math.abs(k2 - par4);
                        final boolean flag = i1 < this.lightUpdateBlockList.length - 6;

                        if (j3 + l3 + k3 < 17 && flag) {
                            if (this.getSavedLightValue(par1EnumSkyBlock, i2 - 1, j2, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - 1 - par2 + 32 + (j2 - par3 + 32 << 6) + (k2 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, i2 + 1, j2, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 + 1 - par2 + 32 + (j2 - par3 + 32 << 6) + (k2 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, i2, j2 - 1, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - par2 + 32 + (j2 - 1 - par3 + 32 << 6) + (k2 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, i2, j2 + 1, k2) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - par2 + 32 + (j2 + 1 - par3 + 32 << 6) + (k2 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, i2, j2, k2 - 1) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - par2 + 32 + (j2 - par3 + 32 << 6) + (k2 - 1 - par4 + 32 << 12);
                            }

                            if (this.getSavedLightValue(par1EnumSkyBlock, i2, j2, k2 + 1) < i3) {
                                this.lightUpdateBlockList[i1++] = i2 - par2 + 32 + (j2 - par3 + 32 << 6) + (k2 + 1 - par4 + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.theProfiler.endSection();
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(final boolean par1) {
        return false;
    }

    public List getPendingBlockUpdates(final Chunk par1Chunk, final boolean par2) {
        return null;
    }

    /**
     * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
     */
    public List getEntitiesWithinAABBExcludingEntity(final Entity par1Entity, final AxisAlignedBB par2AxisAlignedBB) {
        return this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB, (IEntitySelector) null);
    }

    public List getEntitiesWithinAABBExcludingEntity(final Entity par1Entity, final AxisAlignedBB par2AxisAlignedBB, final IEntitySelector par3IEntitySelector) {
        final ArrayList arraylist = new ArrayList();
        final int i = MathHelper.floor_double((par2AxisAlignedBB.minX - MAX_ENTITY_RADIUS) / 16.0D);
        final int j = MathHelper.floor_double((par2AxisAlignedBB.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        final int k = MathHelper.floor_double((par2AxisAlignedBB.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        final int l = MathHelper.floor_double((par2AxisAlignedBB.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.chunkExists(i1, j1)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(par1Entity, par2AxisAlignedBB, arraylist, par3IEntitySelector);
                }
            }
        }

        return arraylist;
    }

    /**
     * Returns all entities of the specified class type which intersect with the AABB. Args: entityClass, aabb
     */
    public List getEntitiesWithinAABB(final Class par1Class, final AxisAlignedBB par2AxisAlignedBB) {
        return this.selectEntitiesWithinAABB(par1Class, par2AxisAlignedBB, (IEntitySelector) null);
    }

    public List selectEntitiesWithinAABB(final Class par1Class, final AxisAlignedBB par2AxisAlignedBB, final IEntitySelector par3IEntitySelector) {
        final int i = MathHelper.floor_double((par2AxisAlignedBB.minX - MAX_ENTITY_RADIUS) / 16.0D);
        final int j = MathHelper.floor_double((par2AxisAlignedBB.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        final int k = MathHelper.floor_double((par2AxisAlignedBB.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        final int l = MathHelper.floor_double((par2AxisAlignedBB.maxZ + MAX_ENTITY_RADIUS) / 16.0D);
        final ArrayList arraylist = new ArrayList();

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.chunkExists(i1, j1)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(par1Class, par2AxisAlignedBB, arraylist, par3IEntitySelector);
                }
            }
        }

        return arraylist;
    }

    public Entity findNearestEntityWithinAABB(final Class par1Class, final AxisAlignedBB par2AxisAlignedBB, final Entity par3Entity) {
        final List list = this.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB);
        Entity entity1 = null;
        double d0 = Double.MAX_VALUE;

        for (int i = 0; i < list.size(); ++i) {
            final Entity entity2 = (Entity) list.get(i);

            if (entity2 != par3Entity) {
                final double d1 = par3Entity.getDistanceSqToEntity(entity2);

                if (d1 <= d0) {
                    entity1 = entity2;
                    d0 = d1;
                }
            }
        }

        return entity1;
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public abstract Entity getEntityByID(int i);

    @SideOnly(Side.CLIENT)

    /**
     * Accessor for world Loaded Entity List
     */
    public List getLoadedEntityList() {
        return this.loadedEntityList;
    }

    /**
     * Args: X, Y, Z, tile entity Marks the chunk the tile entity is in as modified. This is essential as chunks that
     * are not marked as modified may be rolled back when exiting the game.
     */
    public void markTileEntityChunkModified(final int par1, final int par2, final int par3, final TileEntity par4TileEntity) {
        if (this.blockExists(par1, par2, par3)) {
            this.getChunkFromBlockCoords(par1, par3).setChunkModified();
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world. Args: entityClass
     */
    public int countEntities(final Class par1Class) {
        int i = 0;

        for (int j = 0; j < this.loadedEntityList.size(); ++j) {
            final Entity entity = (Entity) this.loadedEntityList.get(j);

            // CraftBukkit start - Split out persistent check, don't apply it to special persistent mobs
            if (entity instanceof EntityLiving) {
                final EntityLiving entityliving = (EntityLiving) entity;

                if (entityliving.func_70692_ba_CodeFix_Public() && entityliving.isNoDespawnRequired())   // Should be isPersistent
                {
                    continue;
                }
            }

            if (par1Class.isAssignableFrom(entity.getClass())) {
                ++i;
            }

            // CraftBukkit end
        }

        return i;
    }

    /**
     * adds entities to the loaded entities list, and loads thier skins.
     */
    public void addLoadedEntities(final List par1List) {
        // CraftBukkit start
        Entity entity = null;

        for (int i = 0; i < par1List.size(); ++i) {
            entity = (Entity) par1List.get(i);

            if (entity == null) {
                continue;
            }

            // CraftBukkit end
            if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entity, this))) {
                loadedEntityList.add(entity);
                this.onEntityAdded(entity);
            }
        }
    }

    /**
     * Adds a list of entities to be unloaded on the next pass of World.updateEntities()
     */
    public void unloadEntities(final List par1List) {
        this.unloadedEntityList.addAll(par1List);
    }

    /**
     * Returns true if the given Entity can be placed on the given side of the given block position.
     */
    public boolean canPlaceEntityOnSide(final int par1, final int par2, final int par3, final int par4, final boolean par5, final int par6, final Entity par7Entity, final ItemStack par8ItemStack) {
        final int j1 = this.getBlockId(par2, par3, par4);
        Block block = Block.blocksList[j1];
        final Block block1 = Block.blocksList[par1];
        if (block1 == null) return false; // Cauldron
        AxisAlignedBB axisalignedbb = block1.getCollisionBoundingBoxFromPool(this, par2, par3, par4);

        if (par5) {
            axisalignedbb = null;
        }

        final boolean defaultReturn; // CraftBukkit - store the default action

        if (axisalignedbb != null && !this.checkNoEntityCollision(axisalignedbb, par7Entity)) {
            defaultReturn = false; // CraftBukkit
        } else {
            if (block != null && (block == Block.waterMoving || block == Block.waterStill || block == Block.lavaMoving || block == Block.lavaStill || block == Block.fire || block.blockMaterial.isReplaceable())) {
                block = null;
            }

            // CraftBukkit

            if (block != null && block.isBlockReplaceable(this, par2, par3, par4)) {
                block = null;
            }

            defaultReturn = block != null && block.blockMaterial == Material.circuits && block1 == Block.anvil ? true : par1 > 0 && block == null && block1.canPlaceBlockOnSide(this, par2, par3, par4, par6, par8ItemStack);
        }

        // CraftBukkit start
        final BlockCanBuildEvent event = new BlockCanBuildEvent(this.getWorld().getBlockAt(par2, par3, par4), par1, defaultReturn);
        this.getServer().getPluginManager().callEvent(event);
        return event.isBuildable();
    }

    public PathEntity getPathEntityToEntity(final Entity par1Entity, final Entity par2Entity, final float par3, final boolean par4, final boolean par5, final boolean par6, final boolean par7) {
        this.theProfiler.startSection("pathfind");
        final int i = MathHelper.floor_double(par1Entity.posX);
        final int j = MathHelper.floor_double(par1Entity.posY + 1.0D);
        final int k = MathHelper.floor_double(par1Entity.posZ);
        final int l = (int) (par3 + 16.0F);
        final int i1 = i - l;
        final int j1 = j - l;
        final int k1 = k - l;
        final int l1 = i + l;
        final int i2 = j + l;
        final int j2 = k + l;
        final ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2, 0);
        final PathEntity pathentity = (new PathFinder(chunkcache, par4, par5, par6, par7)).createEntityPathTo(par1Entity, par2Entity, par3);
        this.theProfiler.endSection();
        return pathentity;
    }

    public PathEntity getEntityPathToXYZ(final Entity par1Entity, final int par2, final int par3, final int par4, final float par5, final boolean par6, final boolean par7, final boolean par8, final boolean par9) {
        this.theProfiler.startSection("pathfind");
        final int l = MathHelper.floor_double(par1Entity.posX);
        final int i1 = MathHelper.floor_double(par1Entity.posY);
        final int j1 = MathHelper.floor_double(par1Entity.posZ);
        final int k1 = (int) (par5 + 8.0F);
        final int l1 = l - k1;
        final int i2 = i1 - k1;
        final int j2 = j1 - k1;
        final int k2 = l + k1;
        final int l2 = i1 + k1;
        final int i3 = j1 + k1;
        final ChunkCache chunkcache = new ChunkCache(this, l1, i2, j2, k2, l2, i3, 0);
        final PathEntity pathentity = (new PathFinder(chunkcache, par6, par7, par8, par9)).createEntityPathTo(par1Entity, par2, par3, par4, par5);
        this.theProfiler.endSection();
        return pathentity;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public int isBlockProvidingPowerTo(final int par1, final int par2, final int par3, final int par4) {
        final int i1 = this.getBlockId(par1, par2, par3);
        return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingStrongPower(this, par1, par2, par3, par4);
    }

    /**
     * Returns the highest redstone signal strength powering the given block. Args: X, Y, Z.
     */
    public int getBlockPowerInput(final int par1, final int par2, final int par3) {
        final byte b0 = 0;
        int l = Math.max(b0, this.isBlockProvidingPowerTo(par1, par2 - 1, par3, 0));

        if (l >= 15) {
            return l;
        } else {
            l = Math.max(l, this.isBlockProvidingPowerTo(par1, par2 + 1, par3, 1));

            if (l >= 15) {
                return l;
            } else {
                l = Math.max(l, this.isBlockProvidingPowerTo(par1, par2, par3 - 1, 2));

                if (l >= 15) {
                    return l;
                } else {
                    l = Math.max(l, this.isBlockProvidingPowerTo(par1, par2, par3 + 1, 3));

                    if (l >= 15) {
                        return l;
                    } else {
                        l = Math.max(l, this.isBlockProvidingPowerTo(par1 - 1, par2, par3, 4));

                        if (l >= 15) {
                            return l;
                        } else {
                            l = Math.max(l, this.isBlockProvidingPowerTo(par1 + 1, par2, par3, 5));
                            return l >= 15 ? l : l;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the indirect signal strength being outputted by the given block in the *opposite* of the given direction.
     * Args: X, Y, Z, direction
     */
    public boolean getIndirectPowerOutput(final int par1, final int par2, final int par3, final int par4) {
        return this.getIndirectPowerLevelTo(par1, par2, par3, par4) > 0;
    }

    /**
     * Gets the power level from a certain block face.  Args: x, y, z, direction
     */
    public int getIndirectPowerLevelTo(final int par1, final int par2, final int par3, final int par4) {
        final Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];

        if (block == null) {
            return 0;
        }

        if (!block.shouldCheckWeakPower(this, par1, par2, par3, par4)) {
            return this.getBlockPowerInput(par1, par2, par3);
        } else {
            return block.isProvidingWeakPower(this, par1, par2, par3, par4);
        }
    }

    /**
     * Used to see if one of the blocks next to you or your block is getting power from a neighboring block. Used by
     * items like TNT or Doors so they don't have redstone going straight into them.  Args: x, y, z
     */
    public boolean isBlockIndirectlyGettingPowered(final int par1, final int par2, final int par3) {
        return this.getIndirectPowerLevelTo(par1, par2 - 1, par3, 0) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2 + 1, par3, 1) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2, par3 - 1, 2) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2, par3 + 1, 3) > 0 ? true : (this.getIndirectPowerLevelTo(par1 - 1, par2, par3, 4) > 0 ? true : this.getIndirectPowerLevelTo(par1 + 1, par2, par3, 5) > 0))));
    }

    public int getStrongestIndirectPower(final int par1, final int par2, final int par3) {
        int l = 0;

        for (int i1 = 0; i1 < 6; ++i1) {
            final int j1 = this.getIndirectPowerLevelTo(par1 + Facing.offsetsXForSide[i1], par2 + Facing.offsetsYForSide[i1], par3 + Facing.offsetsZForSide[i1], i1);

            if (j1 >= 15) {
                return 15;
            }

            if (j1 > l) {
                l = j1;
            }
        }

        return l;
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     * Args: entity, dist
     */
    public EntityPlayer getClosestPlayerToEntity(final Entity par1Entity, final double par2) {
        return this.getClosestPlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par2);
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not
     * limit the distance). Args: x, y, z, dist
     */
    public EntityPlayer getClosestPlayer(final double par1, final double par3, final double par5, final double par7) {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer1 = (EntityPlayer) this.playerEntities.get(i);
            // CraftBukkit start - Fixed an NPE
            if (entityplayer1 == null || entityplayer1.isDead) {
                continue;
            }
            // CraftBukkit end
            final double d5 = entityplayer1.getDistanceSq(par1, par3, par5);

            if ((par7 < 0.0D || d5 < par7 * par7) && (d4 == -1.0D || d5 < d4)) {
                d4 = d5;
                entityplayer = entityplayer1;
            }
        }

        return entityplayer;
    }

    /**
     * Returns the closest vulnerable player to this entity within the given radius, or null if none is found
     */
    public EntityPlayer getClosestVulnerablePlayerToEntity(final Entity par1Entity, final double par2) {
        return this.getClosestVulnerablePlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par2);
    }

    /**
     * Returns the closest vulnerable player within the given radius, or null if none is found.
     */
    public EntityPlayer getClosestVulnerablePlayer(final double par1, final double par3, final double par5, final double par7) {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.playerEntities.size(); ++i) {
            final EntityPlayer entityplayer1 = (EntityPlayer) this.playerEntities.get(i);
            // CraftBukkit start - Fixed an NPE
            if (entityplayer1 == null || entityplayer1.isDead) {
                continue;
            }
            // CraftBukkit end

            if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive()) {
                final double d5 = entityplayer1.getDistanceSq(par1, par3, par5);
                double d6 = par7;

                if (entityplayer1.isSneaking()) {
                    d6 = par7 * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible()) {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F) {
                        f = 0.1F;
                    }

                    d6 *= (double) (0.7F * f);
                }

                if ((par7 < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4)) {
                    d4 = d5;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    /**
     * Find a player by name in this world.
     */
    public EntityPlayer getPlayerEntityByName(final String par1Str) {
        for (int i = 0; i < this.playerEntities.size(); ++i) {
            if (par1Str.equals(((EntityPlayer) this.playerEntities.get(i)).getCommandSenderName())) {
                return (EntityPlayer) this.playerEntities.get(i);
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket() {
    }

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void checkSessionLock() throws MinecraftException {
        this.saveHandler.checkSessionLock();
    }

    @SideOnly(Side.CLIENT)
    public void func_82738_a(final long par1) {
        this.worldInfo.incrementTotalWorldTime(par1);
    }

    /**
     * Retrieve the world seed from level.dat
     */
    public long getSeed() {
        return provider.getSeed();
    }

    public long getTotalWorldTime() {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime() {
        return provider.getWorldTime();
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(final long par1) {
        provider.setWorldTime(par1);
    }

    /**
     * Returns the coordinates of the spawn point
     */
    public ChunkCoordinates getSpawnPoint() {
        return provider.getSpawnPoint();
    }

    @SideOnly(Side.CLIENT)
    public void setSpawnLocation(final int par1, final int par2, final int par3) {
        provider.setSpawnPoint(par1, par2, par3);
    }

    @SideOnly(Side.CLIENT)

    /**
     * spwans an entity and loads surrounding chunks
     */
    public void joinEntityInSurroundings(final Entity par1Entity) {
        final int i = MathHelper.floor_double(par1Entity.posX / 16.0D);
        final int j = MathHelper.floor_double(par1Entity.posZ / 16.0D);
        final byte b0 = 2;

        for (int k = i - b0; k <= i + b0; ++k) {
            for (int l = j - b0; l <= j + b0; ++l) {
                this.getChunkFromChunkCoords(k, l);
            }
        }

        if (!this.loadedEntityList.contains(par1Entity)) {
            if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(par1Entity, this))) {
                loadedEntityList.add(par1Entity);
            }
        }
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(final EntityPlayer par1EntityPlayer, final int par2, final int par3, final int par4) {
        return provider.canMineBlock(par1EntityPlayer, par2, par3, par4);
    }

    public boolean canMineBlockBody(final EntityPlayer par1EntityPlayer, final int par2, final int par3, final int par4) {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(final Entity par1Entity, final byte par2) {
    }

    /**
     * gets the IChunkProvider this world uses.
     */
    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void addBlockEvent(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        if (par4 > 0) {
            Block.blocksList[par4].onBlockEventReceived(this, par1, par2, par3, par5, par6);
        }
    }

    /**
     * Returns this world's current save handler
     */
    public ISaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    /**
     * Gets the World's WorldInfo instance
     */
    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    /**
     * Gets the GameRules instance.
     */
    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag() {
    }

    // CraftBukkit start
    // Calls the method that checks to see if players are sleeping
    // Called by CraftPlayer.setPermanentSleeping()
    public void checkSleepStatus() {
        if (!this.isRemote) {
            this.updateAllPlayersSleepingFlag();
        }
    }
    // CraftBukkit end

    public float getWeightedThunderStrength(final float par1) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * par1) * this.getRainStrength(par1);
    }

    /**
     * Not sure about this actually. Reverting this one myself.
     */
    public float getRainStrength(final float par1) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * par1;
    }

    @SideOnly(Side.CLIENT)
    public void setRainStrength(final float par1) {
        this.prevRainingStrength = par1;
        this.rainingStrength = par1;
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean isThundering() {
        return (double) this.getWeightedThunderStrength(1.0F) > 0.9D;
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean isRaining() {
        return (double) this.getRainStrength(1.0F) > 0.2D;
    }

    public boolean canLightningStrikeAt(final int par1, final int par2, final int par3) {
        if (!this.isRaining()) {
            return false;
        } else if (!this.canBlockSeeTheSky(par1, par2, par3)) {
            return false;
        } else if (this.getPrecipitationHeight(par1, par3) > par2) {
            return false;
        } else {
            final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(par1, par3);
            return biomegenbase.getEnableSnow() ? false : biomegenbase.canSpawnLightningBolt();
        }
    }

    /**
     * Checks to see if the biome rainfall values for a given x,y,z coordinate set are extremely high
     */
    public boolean isBlockHighHumidity(final int par1, final int par2, final int par3) {
        return provider.isBlockHighHumidity(par1, par2, par3);
    }

    /**
     * Assigns the given String id to the given MapDataBase using the MapStorage, removing any existing ones of the same
     * id.
     */
    public void setItemData(final String par1Str, final WorldSavedData par2WorldSavedData) {
        this.mapStorage.setData(par1Str, par2WorldSavedData);
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk using the MapStorage, instantiating
     * the given Class, or returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadItemData(final Class par1Class, final String par2Str) {
        return this.mapStorage.loadData(par1Class, par2Str);
    }

    /**
     * Returns an unique new data id from the MapStorage for the given prefix and saves the idCounts map to the
     * 'idcounts' file.
     */
    public int getUniqueDataId(final String par1Str) {
        return this.mapStorage.getUniqueDataId(par1Str);
    }

    public void func_82739_e(final int par1, final int par2, final int par3, final int par4, final int par5) {
        for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1) {
            ((IWorldAccess) this.worldAccesses.get(j1)).broadcastSound(par1, par2, par3, par4, par5);
        }
    }

    /**
     * See description for playAuxSFX.
     */
    public void playAuxSFX(final int par1, final int par2, final int par3, final int par4, final int par5) {
        this.playAuxSFXAtEntity((EntityPlayer) null, par1, par2, par3, par4, par5);
    }

    /**
     * See description for playAuxSFX.
     */
    public void playAuxSFXAtEntity(final EntityPlayer par1EntityPlayer, final int par2, final int par3, final int par4, final int par5, final int par6) {
        try {
            for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1) {
                ((IWorldAccess) this.worldAccesses.get(j1)).playAuxSFX(par1EntityPlayer, par2, par3, par4, par5, par6);
            }
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
            crashreportcategory.addCrashSection("Block coordinates", CrashReportCategory.getLocationInfo(par3, par4, par5));
            crashreportcategory.addCrashSection("Event source", par1EntityPlayer);
            crashreportcategory.addCrashSection("Event type", Integer.valueOf(par2));
            crashreportcategory.addCrashSection("Event data", Integer.valueOf(par6));
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Returns current world height.
     */
    public int getHeight() {
        return provider.getHeight();
    }

    /**
     * Returns current world height.
     */
    public int getActualHeight() {
        return provider.getActualHeight();
    }

    public IUpdatePlayerListBox getMinecartSoundUpdater(final EntityMinecart par1EntityMinecart) {
        return null;
    }

    /**
     * puts the World Random seed to a specific state dependant on the inputs
     */
    public Random setRandomSeed(final int par1, final int par2, final int par3) {
        final long l = (long) par1 * 341873128712L + (long) par2 * 132897987541L + this.getWorldInfo().getSeed() + (long) par3;
        this.rand.setSeed(l);
        return this.rand;
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(final String par1Str, final int par2, final int par3, final int par4) {
        return this.getChunkProvider().findClosestStructure(this, par1Str, par2, par3, par4);
    }

    @SideOnly(Side.CLIENT)

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns horizon height for use in rendering the sky.
     */
    public double getHorizon() {
        return provider.getHorizon();
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(final CrashReport par1CrashReport) {
        final CrashReportCategory crashreportcategory = par1CrashReport.makeCategoryDepth("Affected level", 1);
        crashreportcategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashreportcategory.addCrashSectionCallable("All players", new CallableLvl2(this));
        crashreportcategory.addCrashSectionCallable("Chunk stats", new CallableLvl3(this));

        try {
            this.worldInfo.addToCrashReport(crashreportcategory);
        } catch (final Throwable throwable) {
            crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
        }

        return crashreportcategory;
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void destroyBlockInWorldPartially(final int par1, final int par2, final int par3, final int par4, final int par5) {
        for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1) {
            final IWorldAccess iworldaccess = (IWorldAccess) this.worldAccesses.get(j1);
            iworldaccess.destroyBlockPartially(par1, par2, par3, par4, par5);
        }
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3Pool getWorldVec3Pool() {
        return this.vecPool;
    }

    /**
     * returns a calendar object containing the current date
     */
    public Calendar getCurrentDate() {
        if (this.getTotalWorldTime() % 600L == 0L) {
            this.theCalendar.setTimeInMillis(MinecraftServer.getSystemTimeMillis());
        }

        return this.theCalendar;
    }

    @SideOnly(Side.CLIENT)
    public void func_92088_a(final double par1, final double par3, final double par5, final double par7, final double par9, final double par11, final NBTTagCompound par13NBTTagCompound) {
    }

    public Scoreboard getScoreboard() {
        return this.worldScoreboard;
    }

    public void func_96440_m(final int par1, final int par2, final int par3, final int par4) {
        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int j1 = par1 + dir.offsetX;
            int y = par2 + dir.offsetY;
            int k1 = par3 + dir.offsetZ;
            int l1 = getBlockId(j1, y, k1);
            Block block = Block.blocksList[l1];

            if (block != null) {
                block.onNeighborTileChange(this, j1, y, k1, par1, par2, par3);

                if (Block.isNormalCube(l1)) {
                    j1 += dir.offsetX;
                    y += dir.offsetY;
                    k1 += dir.offsetZ;
                    l1 = getBlockId(j1, y, k1);
                    block = Block.blocksList[l1];
                    if (block != null && block.weakTileChanges()) {
                        block.onNeighborTileChange(this, j1, y, k1, par1, par2, par3);
                    }
                }
            }
        }
    }

    public ILogAgent getWorldLogAgent() {
        return this.worldLogAgent;
    }

    /**
     * returns a float value that can be used to determine how likely something is to go awry in the area. It increases
     * based on how long the player is within the vicinity, the lunar phase, and game difficulty. The value can be up to
     * 1.5 on the highest difficulty, 1.0 otherwise.
     */
    public float getLocationTensionFactor(final double par1, final double par3, final double par5) {
        return this.getTensionFactorForBlock(MathHelper.floor_double(par1), MathHelper.floor_double(par3), MathHelper.floor_double(par5));
    }

    /**
     * returns a float value that can be used to determine how likely something is to go awry in the area. It increases
     * based on how long the player is within the vicinity, the lunar phase, and game difficulty. The value can be up to
     * 1.5 on the highest difficulty, 1.0 otherwise.
     */
    public float getTensionFactorForBlock(final int par1, final int par2, final int par3) {
        float f = 0.0F;
        final boolean flag = this.difficultySetting == 3;

        if (this.blockExists(par1, par2, par3)) {
            final float f1 = this.getCurrentMoonPhaseFactor();
            f += MathHelper.clamp_float((float) this.getChunkFromBlockCoords(par1, par3).inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
            f += f1 * 0.25F;
        }

        if (this.difficultySetting < 2) {
            f *= (float) this.difficultySetting / 2.0F;
        }

        return MathHelper.clamp_float(f, 0.0F, flag ? 1.5F : 1.0F);
    }

    public boolean func_72916_c_CodeFix_Public(final int a, final int b) {
        return chunkExists(a, b);
    }

    /**
     * Adds a single TileEntity to the world.
     *
     * @param entity The TileEntity to be added.
     */
    public void addTileEntity(final TileEntity entity) {
        final Collection dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList; // Cauldron - List -> Collection for CB loadedTileEntityList type change
        if (CauldronHooks.canUpdate(entity)) {
            dest.add(entity);
        }
    }

    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     *
     * @param x    Block X Position
     * @param y    Block Y Position
     * @param z    Block Z Position
     * @param side The Side in question
     * @return True if the side is solid
     */
    public boolean isBlockSolidOnSide(final int x, final int y, final int z, final ForgeDirection side) {
        return isBlockSolidOnSide(x, y, z, side, false);
    }

    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     *
     * @param x        Block X Position
     * @param y        Block Y Position
     * @param z        Block Z Position
     * @param side     The Side in question
     * @param _default The defult to return if the block doesn't exist.
     * @return True if the side is solid
     */
    @Override
    public boolean isBlockSolidOnSide(final int x, final int y, final int z, final ForgeDirection side, final boolean _default) {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
            return _default;
        }

        final Chunk chunk = this.chunkProvider.provideChunk(x >> 4, z >> 4);
        if (chunk == null || chunk.isEmpty()) {
            return _default;
        }

        final Block block = Block.blocksList[getBlockId(x, y, z)];
        if (block == null) {
            return false;
        }

        return block.isBlockSolidOnSide(this, x, y, z, side);
    }

    /**
     * Get the persistent chunks for this world
     *
     * @return
     */
    public ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
        return ForgeChunkManager.getPersistentChunksFor(this);
    }

    /**
     * Readded as it was removed, very useful helper function
     *
     * @param x X position
     * @param y Y Position
     * @param z Z Position
     * @return The blocks light opacity
     */
    public int getBlockLightOpacity(final int x, final int y, final int z) {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
            return 0;
        }

        if (y < 0 || y >= 256) {
            return 0;
        }

        return getChunkFromChunkCoords(x >> 4, z >> 4).getBlockLightOpacity(x & 15, y, z & 15);
    }

    /**
     * Returns a count of entities that classify themselves as the specified creature type.
     */
    public int countEntities(final EnumCreatureType type, final boolean forSpawnCount) {
        int count = 0;
        for (int x = 0; x < loadedEntityList.size(); x++) {
            if (((Entity) loadedEntityList.get(x)).isCreatureType(type, forSpawnCount)) {
                count++;
            }
        }
        return count;
    }

    // Cauldron start
    public boolean isActiveChunk(final int x, final int z) {
        return getPersistentChunks().containsKey(new ChunkCoordIntPair(x, z)) || activeChunkSet_CB.containsKey(chunkToKey(x, z));
    }

    public boolean isActiveChunk(final long key) {
        return isActiveChunk(keyToX(key), keyToZ(key));
    }

    public boolean isActiveBlockCoord(final int x, final int z) {
        return isActiveChunk(x >> 4, z >> 4);
    }

    public boolean inActiveChunk(final Entity entity) {
        return isActiveBlockCoord(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));
    }

    // this method is used by ForgeMultipart and Immibis's Microblocks
    public boolean canPlaceMultipart(final Block block, final int x, final int y, final int z) {
        BlockPlaceEvent placeEvent = null;
        if (ItemStack.currentPlayer != null) {
            placeEvent = org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callBlockPlaceEvent(this, ItemStack.currentPlayer, org.bukkit.craftbukkit.v1_6_R3.block.CraftBlockState.getBlockState(this, x, y, z, 3), x, y, z);
        }

        if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
            return false;
        }

        return true;
    }
    // Cauldron end
}
