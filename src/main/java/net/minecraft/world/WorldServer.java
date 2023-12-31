package net.minecraft.world;

import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.iterator.TLongShortIterator;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEventData;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.WorldEvent;
import org.bukkit.WeatherType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_6_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_6_R3.util.LongHash;
import org.bukkit.craftbukkit.v1_6_R3.util.LongObjectHashMap;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;
import java.util.*;

import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;

// CraftBukkit start
// CraftBukkit end

public class WorldServer extends World {
    private final MinecraftServer mcServer;
    public EntityTracker theEntityTracker; // CraftBukkit - private final -> public
    private final PlayerManager thePlayerManager;
    private LongObjectHashMap<Set<NextTickListEntry>> tickEntriesByChunk; // Spigot - switch to something better for chunk-wise access
    private Set<NextTickListEntry> pendingTickListEntriesHashSet; // Cauldron - vanilla compatibility
    private TreeSet<NextTickListEntry> tickEntryQueue; // Spigot
    public ChunkProviderServer theChunkProviderServer;

    /**
     * set by CommandServerSave{all,Off,On}
     */
    public boolean canNotSave;

    /**
     * is false if there are no players
     */
    public boolean allPlayersSleeping;
    private int updateEntityTick;

    /**
     * the teleporter to use when the entity is being transferred into the dimension
     */
    private final Teleporter worldTeleporter;
    private final SpawnerAnimals animalSpawner = new SpawnerAnimals();

    /**
     * Double buffer of ServerBlockEventList[] for holding pending BlockEventData's
     */
    private ServerBlockEventList[] blockEventCache = {new ServerBlockEventList((ServerBlockEvent) null), new ServerBlockEventList((ServerBlockEvent) null)};

    /**
     * The index into the blockEventCache; either 0, or 1, toggled in sendBlockEventPackets  where all BlockEvent are
     * applied locally and send to clients.
     */
    private int blockEventCacheIndex;
    public static final WeightedRandomChestContent[] bonusChestContent = {new WeightedRandomChestContent(Item.stick.itemID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.planks.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.wood.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Item.axeStone.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.axeWood.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.pickaxeStone.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.pickaxeWood.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.appleRed.itemID, 0, 2, 3, 5), new WeightedRandomChestContent(Item.bread.itemID, 0, 2, 3, 3)};
    private List<NextTickListEntry> pendingTickEntries = new ArrayList<NextTickListEntry>(); // Spigot
    private int nextPendingTickEntry; // Spigot

    /**
     * An IntHashMap of entity IDs (integers) to their Entity objects.
     */
    private IntHashMap entityIdMap;

    /**
     * Stores the recently processed (lighting) chunks
     */
    protected Set<ChunkCoordIntPair> doneChunks = new HashSet<ChunkCoordIntPair>();
    public List<Teleporter> customTeleporters = new ArrayList<Teleporter>();

    public WorldServer(final MinecraftServer par1MinecraftServer, final ISaveHandler par2ISaveHandler, final String par3Str, final int par4, final WorldSettings par5WorldSettings, final Profiler par6Profiler, final ILogAgent par7ILogAgent) {
        super(par2ISaveHandler, par3Str, par5WorldSettings, WorldProvider.getProviderForDimension(par4), par6Profiler, par7ILogAgent);
        this.dimension = par4;
        this.mcServer = par1MinecraftServer;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this, par1MinecraftServer.getConfigurationManager().getViewDistance());

        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        if (this.tickEntriesByChunk == null) {
            this.pendingTickListEntriesHashSet = new HashSet<>(); // Cauldron - vanilla compatibility
            this.tickEntriesByChunk = new LongObjectHashMap<Set<NextTickListEntry>>();
        }

        if (this.tickEntryQueue == null) {

            //TODO ZoomCodeStart
            this.tickEntryQueue = Sets.newTreeSet();
            //TODO ZoomCodeEnd
            //TODO ZoomCodeClear
            //this.tickEntryQueue = new TreeSet<NextTickListEntry>();
        }

        this.worldTeleporter = new Teleporter(this);
        this.worldScoreboard = new ServerScoreboard(par1MinecraftServer);
        ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData) this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardsavedata == null) {
            scoreboardsavedata = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardsavedata);
        }

        if (!(this instanceof WorldServerMulti)) //Forge: We fix the global mapStorage, which causes us to share scoreboards early. So don't associate the save data with the temporary scoreboard
        {
            scoreboardsavedata.func_96499_a(this.worldScoreboard);
        }
        ((ServerScoreboard) this.worldScoreboard).func_96547_a(scoreboardsavedata);
        DimensionManager.setWorld(par4, this);
    }

    public WorldServer(final MinecraftServer minecraftServer, final ISaveHandler saveHandler, final String par2String, final WorldProvider provider, final WorldSettings par4WorldSettings, final Profiler theProfiler, final ILogAgent worldLogAgent) {
        super(saveHandler, par2String, provider, par4WorldSettings, theProfiler, worldLogAgent);
        this.dimension = provider.dimensionId;
        this.pvpMode = minecraftServer.isPVPEnabled();
        this.mcServer = minecraftServer;
        this.theEntityTracker = null;
        this.thePlayerManager = null;
        this.worldTeleporter = null;
    }

    // CraftBukkit start
    public final int dimension;

    public WorldServer(final MinecraftServer minecraftserver, final ISaveHandler isavehandler, final String s, final int i, final WorldSettings worldsettings, final Profiler profiler, final ILogAgent ilogagent, final org.bukkit.World.Environment env, final org.bukkit.generator.ChunkGenerator gen) {
        super(isavehandler, s, worldsettings, WorldProvider.getProviderForDimension(i), profiler, ilogagent, gen, env);
        this.dimension = i;
        this.pvpMode = minecraftserver.isPVPEnabled();
        // CraftBukkit end
        this.mcServer = minecraftserver;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this, spigotConfig.viewDistance); // Spigot

        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        // Spigot start
        if (this.tickEntriesByChunk == null) {
            this.pendingTickListEntriesHashSet = new HashSet<>(); // Cauldron - vanilla compatibility
            this.tickEntriesByChunk = new LongObjectHashMap<Set<NextTickListEntry>>();
        }

        if (this.tickEntryQueue == null) {

            //TODO ZoomCodeStart
            this.tickEntryQueue = Sets.newTreeSet();
            //TODO ZoomCodeEnd
            //TODO ZoomCodeClear
            //this.tickEntryQueue = new TreeSet<NextTickListEntry>();
        }

        // Spigot end
        this.worldTeleporter = new CraftTravelAgent(this); // CraftBukkit
        this.worldScoreboard = new ServerScoreboard(minecraftserver);
        ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData) this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardsavedata == null) {
            scoreboardsavedata = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardsavedata);
        }

        if (!(this instanceof WorldServerMulti)) //Forge: We fix the global mapStorage, which causes us to share scoreboards early. So don't associate the save data with the temporary scoreboard 
        {
            scoreboardsavedata.func_96499_a(this.worldScoreboard);
        }
        ((ServerScoreboard) this.worldScoreboard).func_96547_a(scoreboardsavedata);
        DimensionManager.setWorld(i, this);
    }

    // Cauldron start - wrapper to get CB support
    public WorldServer(final MinecraftServer par1MinecraftServer, final ISaveHandler par2ISaveHandler, final String par3Str, final int par4, final ILogAgent ilogagent, final WorldSettings par5WorldSettings, final Profiler par6Profiler) {
        this(par1MinecraftServer, par2ISaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, ilogagent, null, null);
    }
    // Cauldron end

    // CraftBukkit start
    @Override

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getBlockTileEntity(final int i, final int j, final int k) {
        TileEntity result = super.getBlockTileEntity(i, j, k);
        final int type = getBlockId(i, j, k);

        if (type == Block.chest.blockID) {
            if (!(result instanceof TileEntityChest)) {
                // Cauldron - allow non-vanilla tile entities on chests for Terrafirmacraft, fixes #724
                //result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.furnaceIdle.blockID) {
            if (!(result instanceof TileEntityFurnace)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.dropper.blockID) {
            if (!(result instanceof TileEntityDropper)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.dispenser.blockID) {
            if (!(result instanceof TileEntityDispenser)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.jukebox.blockID) {
            if (!(result instanceof TileEntityRecordPlayer)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.music.blockID) {
            if (!(result instanceof TileEntityNote)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.mobSpawner.blockID) {
            if (!(result instanceof TileEntityMobSpawner)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if ((type == Block.signPost.blockID) || (type == Block.signWall.blockID)) {
            if (!(result instanceof TileEntitySign)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.enderChest.blockID) {
            if (!(result instanceof TileEntityEnderChest)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.brewingStand.blockID) {
            if (!(result instanceof TileEntityBrewingStand)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.beacon.blockID) {
            if (!(result instanceof TileEntityBeacon)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        } else if (type == Block.hopperBlock.blockID) {
            if (!(result instanceof TileEntityHopper)) {
                result = fixTileEntity(i, j, k, type, result);
            }
        }

        return result;
    }

    private TileEntity fixTileEntity(final int x, final int y, final int z, final int type, final TileEntity found) {
        this.getServer().getLogger().severe("Block at " + x + "," + y + "," + z + " is " + org.bukkit.Material.getMaterial(type).toString() + " but has " + found + ". "
                + "Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.");

        if (Block.blocksList[type] instanceof BlockContainer) {
            final TileEntity replacement = ((BlockContainer) Block.blocksList[type]).createNewTileEntity(this);
            replacement.worldObj = this;
            this.setBlockTileEntity(x, y, z, replacement);
            return replacement;
        } else {
            this.getServer().getLogger().severe("Don't know how to fix for this type... Can't do anything! :(");
            return found;
        }
    }

    private boolean canSpawn(final int x, final int z) {
        if (this.generator != null) {
            return this.generator.canSpawn(this.getWorld(), x, z);
        } else {
            return this.provider.canCoordinateBeSpawn(x, z);
        }
    }
    // CraftBukkit end

    /**
     * Runs a single tick for the world
     */
    @Override
    public void tick() {
        ProfilerSection.DIMENSION_BLOCKTICK.start(this.provider.dimensionId); // Cauldron - mobius hook
        super.tick();

        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3) {
            this.difficultySetting = 3;
        }

        this.provider.worldChunkMgr.cleanupCache();

        if (this.areAllPlayersAsleep()) {
            if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
                final long i = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(i - i % 24000L);
            }

            this.wakeAllPlayers();
        }

        this.theProfiler.startSection("mobSpawner");
        // CraftBukkit start - Only call spawner if we have players online and the world allows for mobs or animals
        final long time = this.worldInfo.getWorldTotalTime();

        if (this.getGameRules().getGameRuleBooleanValue("doMobSpawning") && (this.spawnHostileMobs || this.spawnPeacefulMobs) && (this instanceof WorldServer && !this.playerEntities.isEmpty())) {
            timings.mobSpawn.startTiming(); // Spigot
            this.animalSpawner.findChunksForSpawning(this, this.spawnHostileMobs && (this.ticksPerMonsterSpawns != 0 && time % this.ticksPerMonsterSpawns == 0L), this.spawnPeacefulMobs && (this.ticksPerAnimalSpawns != 0 && time % this.ticksPerAnimalSpawns == 0L), this.worldInfo.getWorldTotalTime() % 400L == 0L);
            timings.mobSpawn.stopTiming(); // Spigot
        }

        // CraftBukkit end
        timings.doChunkUnload.startTiming(); // Spigot
        this.theProfiler.endStartSection("chunkSource");
        this.chunkProvider.unloadQueuedChunks();
        final int j = this.calculateSkylightSubtracted(1.0F);

        if (j != this.skylightSubtracted) {
            this.skylightSubtracted = j;
        }

        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
        }

        timings.doChunkUnload.stopTiming(); // Spigot
        this.theProfiler.endStartSection("tickPending");
        timings.doTickPending.startTiming(); // Spigot
        this.tickUpdates(false);
        timings.doTickPending.stopTiming(); // Spigot
        this.theProfiler.endStartSection("tickTiles");
        timings.doTickTiles.startTiming(); // Spigot
        this.tickBlocksAndAmbiance();
        timings.doTickTiles.stopTiming(); // Spigot
        this.theProfiler.endStartSection("chunkMap");
        timings.doChunkMap.startTiming(); // Spigot
        this.thePlayerManager.updatePlayerInstances();
        timings.doChunkMap.stopTiming(); // Spigot
        this.theProfiler.endStartSection("village");
        timings.doVillages.startTiming(); // Spigot
        this.villageCollectionObj.tick();
        this.villageSiegeObj.tick();
        timings.doVillages.stopTiming(); // Spigot
        this.theProfiler.endStartSection("portalForcer");
        timings.doPortalForcer.startTiming(); // Spigot
        this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
        for (final Teleporter tele : customTeleporters) {
            tele.removeStalePortalLocations(getTotalWorldTime());
        }
        timings.doPortalForcer.stopTiming(); // Spigot
        this.theProfiler.endSection();
        timings.doSounds.startTiming(); // Spigot
        this.sendAndApplyBlockEvents();
        timings.doSounds.stopTiming(); // Spigot
        ProfilerSection.DIMENSION_BLOCKTICK.stop(this.provider.dimensionId); // Cauldron - mobius hook
        // Cauldron start - if enabled, this can cause issues with IC2 TE's and BoP WorldGen
        if (this.getServer() != null && this.getServer().chunkGCEnabled) {
            timings.doChunkGC.startTiming(); // Spigot
            this.getWorld().processChunkGC(); // CraftBukkit
            timings.doChunkGC.stopTiming(); // Spigot
        }
        // Cauldron end
    }

    /**
     * only spawns creatures allowed by the chunkProvider
     */
    public SpawnListEntry spawnRandomCreature(final EnumCreatureType par1EnumCreatureType, final int par2, final int par3, final int par4) {
        List<WeightedRandomItem> list = (List<WeightedRandomItem>) this.getChunkProvider().getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
        list = ForgeEventFactory.getPotentialSpawns(this, par1EnumCreatureType, par2, par3, par4, list);
        return list != null && !list.isEmpty() ? (SpawnListEntry) WeightedRandom.getRandomItem(this.rand, list) : null;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    @Override
    public void updateAllPlayersSleepingFlag() {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();
        final Iterator<EntityPlayer> iterator = this.playerEntities.iterator();

        while (iterator.hasNext()) {
            final EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (!entityplayer.isPlayerSleeping() && !entityplayer.fauxSleeping)   // CraftBukkit
            {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void wakeAllPlayers() {
        this.allPlayersSleeping = false;
        final Iterator<EntityPlayer> iterator = this.playerEntities.iterator();

        while (iterator.hasNext()) {
            final EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.isPlayerSleeping()) {
                entityplayer.wakeUpPlayer(false, false, true);
            }
        }

        this.resetRainAndThunder();
    }

    private void resetRainAndThunder() {
        // CraftBukkit start
        final WeatherChangeEvent weather = new WeatherChangeEvent(this.getWorld(), false);
        this.getServer().getPluginManager().callEvent(weather);
        final ThunderChangeEvent thunder = new ThunderChangeEvent(this.getWorld(), false);
        this.getServer().getPluginManager().callEvent(thunder);

        if (!weather.isCancelled()) {
            this.worldInfo.setRainTime(0);
            this.worldInfo.setRaining(false);
        }

        if (!thunder.isCancelled()) {
            this.worldInfo.setThunderTime(0);
            this.worldInfo.setThundering(false);
        }

        // CraftBukkit end
        if (!weather.isCancelled() && !thunder.isCancelled()) provider.resetRainAndThunder(); // Cauldron
    }

    public boolean areAllPlayersAsleep() {
        if (this.allPlayersSleeping && !this.isRemote) {
            final Iterator<EntityPlayer> iterator = this.playerEntities.iterator();
            // CraftBukkit - This allows us to assume that some people are in bed but not really, allowing time to pass in spite of AFKers
            boolean foundActualSleepers = false;
            EntityPlayer entityplayer;

            do {
                if (!iterator.hasNext()) {
                    return foundActualSleepers; // CraftBukkit
                }

                entityplayer = (EntityPlayer) iterator.next();

                // CraftBukkit start
                if (entityplayer.isPlayerFullyAsleep()) {
                    foundActualSleepers = true;
                }
            }
            while (entityplayer.isPlayerFullyAsleep() || entityplayer.fauxSleeping);

            // CraftBukkit end
            return false;
        } else {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setSpawnLocation() {
        if (this.worldInfo.getSpawnY() <= 0) {
            this.worldInfo.setSpawnY(64);
        }

        int i = this.worldInfo.getSpawnX();
        int j = this.worldInfo.getSpawnZ();
        int k = 0;

        while (this.getFirstUncoveredBlock(i, j) == 0) {
            i += this.rand.nextInt(8) - this.rand.nextInt(8);
            j += this.rand.nextInt(8) - this.rand.nextInt(8);
            ++k;

            if (k == 10000) {
                break;
            }
        }

        this.worldInfo.setSpawnX(i);
        this.worldInfo.setSpawnZ(j);
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    @Override
    protected void tickBlocksAndAmbiance() {
        super.tickBlocksAndAmbiance();
        int i = 0;
        int j = 0;
        // CraftBukkit start
        // Iterator iterator = this.chunkTickList.iterator();

        // Spigot start
        for (final TLongShortIterator iter = activeChunkSet_CB.iterator(); iter.hasNext(); ) {
            iter.advance();
            final long chunkCoord = iter.key();
            final int chunkX = World.keyToX(chunkCoord);
            final int chunkZ = World.keyToZ(chunkCoord);
            // If unloaded, or in process of being unloaded, drop it
            if ((!this.chunkExists(chunkX, chunkZ)) || (this.theChunkProviderServer.chunksToUnload.contains(chunkX, chunkZ))) {
                activeChunkSet.remove(new ChunkCoordIntPair(chunkX, chunkZ)); // Cauldron - vanilla compatibility
                iter.remove();
                continue;
            }
            // Spigot end
            // ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
            final int k = chunkX * 16;
            final int l = chunkZ * 16;

            this.theProfiler.startSection("getChunk");
            final Chunk chunk = this.getChunkFromChunkCoords(chunkX, chunkZ);
            // CraftBukkit end

            this.moodSoundAndLightCheck(k, l, chunk);
            this.theProfiler.endStartSection("tickChunk");
            chunk.updateSkylight();
            this.theProfiler.endStartSection("thunder");
            int i1;
            int j1;
            int k1;
            int l1;

            if (provider.canDoLightning(chunk) && this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                i1 = this.updateLCG >> 2;
                j1 = k + (i1 & 15);
                k1 = l + (i1 >> 8 & 15);
                l1 = this.getPrecipitationHeight(j1, k1);
                if (this.canLightningStrikeAt(j1, l1, k1)) {
                    this.addWeatherEffect(new EntityLightningBolt(this, (double) j1, (double) l1, (double) k1));
                }
            }

            this.theProfiler.endStartSection("iceandsnow");
            int i2;

            if (provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                i1 = this.updateLCG >> 2;
                j1 = i1 & 15;
                k1 = i1 >> 8 & 15;
                l1 = this.getPrecipitationHeight(j1 + k, k1 + l);

                if (this.isBlockFreezableNaturally(j1 + k, l1 - 1, k1 + l)) {
                    // CraftBukkit start
                    final BlockState blockState = this.getWorld().getBlockAt(j1 + k, l1 - 1, k1 + l).getState();
                    blockState.setTypeId(Block.ice.blockID);
                    final BlockFormEvent iceBlockForm = new BlockFormEvent(blockState.getBlock(), blockState);
                    this.getServer().getPluginManager().callEvent(iceBlockForm);
                    if (!iceBlockForm.isCancelled()) {
                        blockState.update(true);
                    }
                    // CraftBukkit end
                }

                if (this.isRaining() && this.canSnowAt(j1 + k, l1, k1 + l)) {
                    // CraftBukkit start
                    final BlockState blockState = this.getWorld().getBlockAt(j1 + k, l1, k1 + l).getState();
                    blockState.setTypeId(Block.snow.blockID);

                    final BlockFormEvent snow = new BlockFormEvent(blockState.getBlock(), blockState);
                    this.getServer().getPluginManager().callEvent(snow);

                    if (!snow.isCancelled()) {
                        blockState.update(true);
                    }
                    // CraftBukkit end
                }

                if (this.isRaining()) {
                    final BiomeGenBase biomegenbase = this.getBiomeGenForCoords(j1 + k, k1 + l);

                    if (biomegenbase.canSpawnLightningBolt()) {
                        i2 = this.getBlockId(j1 + k, l1 - 1, k1 + l);
                        if (i2 != 0) {
                            Block.blocksList[i2].fillWithRain(this, j1 + k, l1 - 1, k1 + l);
                        }
                    }
                }
            }

            this.theProfiler.endStartSection("tickTiles");
            final ExtendedBlockStorage[] aextendedblockstorage = chunk.getBlockStorageArray();

            j1 = aextendedblockstorage.length;

            for (k1 = 0; k1 < j1; ++k1) {
                final ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[k1];

                if (extendedblockstorage != null && extendedblockstorage.getNeedsRandomTick()) {
                    for (int j2 = 0; j2 < 3; ++j2) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        i2 = this.updateLCG >> 2;
                        final int k2 = i2 & 15;
                        final int l2 = i2 >> 8 & 15;
                        final int i3 = i2 >> 16 & 15;
                        final int j3 = extendedblockstorage.getExtBlockID(k2, i3, l2);

                        ++j;
                        final Block block = Block.blocksList[j3];

                        if (block != null && block.getTickRandomly()) {
                            ++i;
                            this.growthOdds = (iter.value() < 1) ? this.modifiedOdds : 100; // Spigot - grow fast if no players are in this chunk (value = player count)
                            block.updateTick(this, k2 + k, i3 + extendedblockstorage.getYLocation(), l2 + l, this.rand);
                        }
                    }
                }
            }

            this.theProfiler.endSection();
        }
    }

    /**
     * Returns true if the given block will receive a scheduled tick in this tick. Args: X, Y, Z, blockID
     */
    @Override
    public boolean isBlockTickScheduledThisTick(final int par1, final int par2, final int par3, final int par4) {
        // Spigot start
        final int te_cnt = this.pendingTickEntries.size();

        for (int idx = this.nextPendingTickEntry; idx < te_cnt; idx++) {
            final NextTickListEntry ent = this.pendingTickEntries.get(idx);

            if ((ent.xCoord == par1) && (ent.yCoord == par2) && (ent.zCoord == par3) && Block.isAssociatedBlockID(ent.blockID, par4)) {
                return true;
            }
        }

        return false;
        // Spigot end
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     */
    @Override
    public void scheduleBlockUpdate(final int par1, final int par2, final int par3, final int par4, final int par5) {
        this.scheduleBlockUpdateWithPriority(par1, par2, par3, par4, par5, 0);
    }

    @Override
    public void scheduleBlockUpdateWithPriority(final int par1, final int par2, final int par3, final int par4, int par5, final int par6) {
        int par51 = par5;
        final NextTickListEntry nextticklistentry = new NextTickListEntry(par1, par2, par3, par4);
        //Keeping here as a note for future when it may be restored.
        //boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(nextticklistentry.xCoord >> 4, nextticklistentry.zCoord >> 4));
        //byte b0 = isForced ? 0 : 8;
        byte b0 = 0;

        if (this.scheduledUpdatesAreImmediate && par4 > 0) {
            if (Block.blocksList[par4].func_82506_l()) {
                b0 = 8;

                if (this.checkChunksExist(nextticklistentry.xCoord - b0, nextticklistentry.yCoord - b0, nextticklistentry.zCoord - b0, nextticklistentry.xCoord + b0, nextticklistentry.yCoord + b0, nextticklistentry.zCoord + b0)) {
                    final int k1 = this.getBlockId(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);

                    if (k1 == nextticklistentry.blockID && k1 > 0) {
                        Block.blocksList[k1].updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, this.rand);
                    }
                }

                return;
            }

            par51 = 1;
        }

        if (this.checkChunksExist(par1 - b0, par2 - b0, par3 - b0, par1 + b0, par2 + b0, par3 + b0)) {
            if (par4 > 0) {
                nextticklistentry.setScheduledTime((long) par51 + this.worldInfo.getWorldTotalTime());
                nextticklistentry.setPriority(par6);
            }

            // Spigot start
            addNextTickIfNeeded(nextticklistentry);
            // Spigot end
        }
    }

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    @Override
    public void scheduleBlockUpdateFromLoad(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final NextTickListEntry nextticklistentry = new NextTickListEntry(par1, par2, par3, par4);
        nextticklistentry.setPriority(par6);

        if (par4 > 0) {
            nextticklistentry.setScheduledTime((long) par5 + this.worldInfo.getWorldTotalTime());
        }

        // Spigot start
        addNextTickIfNeeded(nextticklistentry);
        // Spigot end
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    /* Cauldron removed so we tick entities even if nobody is on and no persistent chunks exist
    public void updateEntities()
    {
        if (this.playerEntities.isEmpty() && getPersistentChunks().isEmpty()) // Cauldron Use Forge logic here
        {
            if (this.updateEntityTick++ >= 1200)
            {
                return;
            }
        }
        else
        {
            this.resetUpdateEntityTick();
        }

        super.updateEntities();
    }
    */

    /**
     * Resets the updateEntityTick field to 0
     */
    public void resetUpdateEntityTick() {
        this.updateEntityTick = 0;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    @Override
    public boolean tickUpdates(final boolean par1) {
        // Spigot start
        int i = this.tickEntryQueue.size();
        this.nextPendingTickEntry = 0;
        {
            // Spigot end
            if (i > 1000) {
                // CraftBukkit start - If the server has too much to process over time, try to alleviate that
                if (i > 20 * 1000) {
                    i = i / 20;
                } else {
                    i = 1000;
                }

                // CraftBukkit end
            }

            this.theProfiler.startSection("cleaning");
            NextTickListEntry nextticklistentry;

            for (int j = 0; j < i; ++j) {
                //TODO ZoomCodeStart
                try {
                    nextticklistentry = this.tickEntryQueue.first(); // Spigot

                    if (!par1 && nextticklistentry.scheduledTime > this.worldInfo.getWorldTotalTime()) {
                        break;
                    }

                    // Spigot start
                    this.removeNextTickIfNeeded(nextticklistentry);
                    this.pendingTickEntries.add(nextticklistentry);
                    // Spigot end
                } catch (final Exception exception) {
                    continue;
                }
                //TODO ZoomCodeEnd
                //TODO ZoomCodeClear
                /*nextticklistentry = (NextTickListEntry) this.tickEntryQueue.first(); // Spigot

                if (!par1 && nextticklistentry.scheduledTime > this.worldInfo.getWorldTotalTime()) {
                    break;
                }

                // Spigot start
                this.removeNextTickIfNeeded(nextticklistentry);
                this.pendingTickEntries.add(nextticklistentry);
                // Spigot end*/
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("ticking");

            // Spigot start
            for (int j = 0, te_cnt = this.pendingTickEntries.size(); j < te_cnt; j++) {
                nextticklistentry = pendingTickEntries.get(j);
                this.nextPendingTickEntry = j + 1; // treat this as dequeued
                // Spigot end
                final byte b0 = 0;

                if (this.checkChunksExist(nextticklistentry.xCoord - b0, nextticklistentry.yCoord - b0, nextticklistentry.zCoord - b0, nextticklistentry.xCoord + b0, nextticklistentry.yCoord + b0, nextticklistentry.zCoord + b0)) {
                    final int k = this.getBlockId(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);

                    if (k > 0 && Block.isAssociatedBlockID(k, nextticklistentry.blockID)) {
                        try {
                            Block.blocksList[k].updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, this.rand);
                        } catch (final Throwable throwable) {
                            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while ticking a block");
                            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being ticked");
                            int l;

                            try {
                                l = this.getBlockMetadata(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);
                            } catch (final Throwable throwable1) {
                                l = -1;
                            }

                            CrashReportCategory.addBlockCrashInfo(crashreportcategory, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, k, l);
                            throw new ReportedException(crashreport);
                        }
                    }
                } else {
                    this.scheduleBlockUpdate(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, nextticklistentry.blockID, 0);
                }
            }

            this.theProfiler.endSection();
            // Spigot start
            this.pendingTickEntries.clear();
            this.nextPendingTickEntry = 0;
            return !this.tickEntryQueue.isEmpty();
            // Spigot end
        }
    }

    @Override
    public List<NextTickListEntry> getPendingBlockUpdates(final Chunk par1Chunk, final boolean par2) {
        // Spigot start
        return this.getNextTickEntriesForChunk(par1Chunk, par2);
        // Spigot end
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    @Override
    public void updateEntityWithOptionalForce(final Entity par1Entity, final boolean par2) {
        /* CraftBukkit start - We prevent spawning in general, so this butchering is not needed
        if (!this.server.getSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)) {
            entity.die();
        }
        // CraftBukkit end */
        if (!this.mcServer.getCanSpawnNPCs() && par1Entity instanceof INpc) {
            par1Entity.setDead();
        }

        if (!(par1Entity.riddenByEntity instanceof EntityPlayer)) {
            super.updateEntityWithOptionalForce(par1Entity, par2);
        }
    }

    /**
     * direct call to super.updateEntityWithOptionalForce
     */
    public void uncheckedUpdateEntity(final Entity par1Entity, final boolean par2) {
        try {
            super.updateEntityWithOptionalForce(par1Entity, par2);
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Forcefully ticking entity");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being force ticked");
            par1Entity.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    @Override
    protected IChunkProvider createChunkProvider() {
        final IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.provider);
        // Cauldron start - if provider is vanilla, proceed to create a bukkit compatible chunk generator
        if (this.provider.getClass().toString().length() <= 3 || this.provider.getClass().toString().contains("net.minecraft")) {
            // CraftBukkit start
            final org.bukkit.craftbukkit.v1_6_R3.generator.InternalChunkGenerator gen;

            if (this.generator != null) {
                gen = new org.bukkit.craftbukkit.v1_6_R3.generator.CustomChunkGenerator(this, this.getSeed(), this.generator);
            } else if (this.provider instanceof WorldProviderHell) {
                gen = new org.bukkit.craftbukkit.v1_6_R3.generator.NetherChunkGenerator(this, this.getSeed());
            } else if (this.provider instanceof WorldProviderEnd) {
                gen = new org.bukkit.craftbukkit.v1_6_R3.generator.SkyLandsChunkGenerator(this, this.getSeed());
            } else {
                gen = new org.bukkit.craftbukkit.v1_6_R3.generator.NormalChunkGenerator(this, this.getSeed());
            }
            this.theChunkProviderServer = new ChunkProviderServer(this, ichunkloader, gen);
            // CraftBukkit end
        } else // custom provider, load normally for forge compatibility
        {
            this.theChunkProviderServer = new ChunkProviderServer(this, ichunkloader, this.provider.createChunkGenerator());
        }
        // Cauldron end
        return this.theChunkProviderServer;
    }

    /**
     * pars: min x,y,z , max x,y,z
     */
    public List<TileEntity> getAllTileEntityInBox(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final List<TileEntity> arraylist = new ArrayList<>();

        // CraftBukkit start - Get tile entities from chunks instead of world
        for (int chunkX = (par1 >> 4); chunkX <= ((par4 - 1) >> 4); chunkX++) {
            for (int chunkZ = (par3 >> 4); chunkZ <= ((par6 - 1) >> 4); chunkZ++) {
                final Chunk chunk = getChunkFromChunkCoords(chunkX, chunkZ);

                if (chunk == null) {
                    continue;
                }

                for (final Object te : chunk.chunkTileEntityMap.values()) {
                    final TileEntity tileentity = (TileEntity) te;

                    if ((tileentity.xCoord >= par1) && (tileentity.yCoord >= par2) && (tileentity.zCoord >= par3) && (tileentity.xCoord < par4) && (tileentity.yCoord < par5) && (tileentity.zCoord < par6)) {
                        arraylist.add(tileentity);
                    }
                }
            }
        }

        // CraftBukkit end
        return arraylist;
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    @Override
    public boolean canMineBlock(final EntityPlayer par1EntityPlayer, final int par2, final int par3, final int par4) {
        return super.canMineBlock(par1EntityPlayer, par2, par3, par4);
    }

    @Override
    public boolean canMineBlockBody(final EntityPlayer par1EntityPlayer, final int par2, final int par3, final int par4) {
        return !this.mcServer.isBlockProtected(this, par2, par3, par4, par1EntityPlayer);
    }

    @Override
    protected void initialize(final WorldSettings par1WorldSettings) {
        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        // Spigot start
        if (this.tickEntriesByChunk == null) {
            this.pendingTickListEntriesHashSet = new HashSet<>(); // Cauldron - vanilla compatibility
            this.tickEntriesByChunk = new LongObjectHashMap<Set<NextTickListEntry>>();
        }

        if (this.tickEntryQueue == null) {

            //TODO ZoomCodeStart
            this.tickEntryQueue = Sets.newTreeSet();
            //TODO ZoomCodeEnd
            //TODO ZoomCodeClear
            //this.tickEntryQueue = new TreeSet<NextTickListEntry>();
        }

        // Spigot end
        this.createSpawnPosition(par1WorldSettings);
        super.initialize(par1WorldSettings);
    }

    /**
     * creates a spawn position at random within 256 blocks of 0,0
     */
    protected void createSpawnPosition(final WorldSettings par1WorldSettings) {
        if (!this.provider.canRespawnHere()) {
            this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
        } else {
            this.findingSpawnPoint = true;
            final WorldChunkManager worldchunkmanager = this.provider.worldChunkMgr;
            final List<BiomeGenBase> list = worldchunkmanager.getBiomesToSpawnIn();
            final Random random = new Random(this.getSeed());
            final ChunkPosition chunkposition = worldchunkmanager.findBiomePosition(0, 0, 256, list, random);
            int i = 0;
            final int j = this.provider.getAverageGroundLevel();
            int k = 0;

            // CraftBukkit start
            if (this.generator != null) {
                final Random rand = new Random(this.getSeed());
                final org.bukkit.Location spawn = this.generator.getFixedSpawnLocation(((WorldServer) this).getWorld(), rand);

                if (spawn != null) {
                    if (spawn.getWorld() != ((WorldServer) this).getWorld()) {
                        throw new IllegalStateException("Cannot set spawn point for " + this.worldInfo.getWorldName() + " to be in another world (" + spawn.getWorld().getName() + ")");
                    } else {
                        this.worldInfo.setSpawnPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
                        this.findingSpawnPoint = false;
                        return;
                    }
                }
            }

            // CraftBukkit end

            if (chunkposition != null) {
                i = chunkposition.x;
                k = chunkposition.z;
            } else {
                this.getWorldLogAgent().logWarning("Unable to find spawn biome");
            }

            int l = 0;

            while (!this.canSpawn(i, k))   // CraftBukkit - use our own canSpawn
            {
                i += random.nextInt(64) - random.nextInt(64);
                k += random.nextInt(64) - random.nextInt(64);
                ++l;

                if (l == 1000) {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(i, j, k);
            this.findingSpawnPoint = false;

            if (par1WorldSettings.isBonusChestEnabled()) {
                this.createBonusChest();
            }
        }
    }

    /**
     * Creates the bonus chest in the world.
     */
    protected void createBonusChest() {
        final WorldGeneratorBonusChest worldgeneratorbonuschest = new WorldGeneratorBonusChest(ChestGenHooks.getItems(BONUS_CHEST, rand), ChestGenHooks.getCount(BONUS_CHEST, rand));

        for (int i = 0; i < 10; ++i) {
            final int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            final int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            final int l = this.getTopSolidOrLiquidBlock(j, k) + 1;

            if (worldgeneratorbonuschest.generate(this, this.rand, j, l, k)) {
                break;
            }
        }
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    public ChunkCoordinates getEntrancePortalLocation() {
        return this.provider.getEntrancePortalLocation();
    }

    /**
     * Saves all chunks to disk while updating progress bar.
     */
    public void saveAllChunks(final boolean par1, final IProgressUpdate par2IProgressUpdate) throws MinecraftException {
        if (this.chunkProvider.canSave()) {
            if (par2IProgressUpdate != null) {
                par2IProgressUpdate.displayProgressMessage("Saving level");
            }

            //TODO ZeyCodeStart
            this.timings.syncLevelSave.startTiming();
            //TODO ZeyCodeEnd

            this.saveLevel();

            //TODO ZeyCodeStart
            this.timings.syncLevelSave.startTiming();
            //TODO ZeyCodeEnd

            if (par2IProgressUpdate != null) {
                par2IProgressUpdate.resetProgresAndWorkingMessage("Saving chunks");
            }

            //TODO ZeyCodeStart
            this.timings.syncChunkSave.startTiming();
            //TODO ZeyCodeEnd

            this.chunkProvider.saveChunks(par1, par2IProgressUpdate);

            //TODO ZeyCodeStart
            this.timings.syncChunkSave.stopTiming();
            //TODO ZeyCodeEnd

            MinecraftForge.EVENT_BUS.post(new WorldEvent.Save(this));
        }
    }

    /**
     * saves chunk data - currently only called during execution of the Save All command
     */
    public void saveChunkData() {
        if (this.chunkProvider.canSave()) {
            this.chunkProvider.saveExtraData();
        }
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
        this.mapStorage.saveAllData();
        this.perWorldStorage.saveAllData();
    }

    @Override
    protected void onEntityAdded(final Entity par1Entity) {
        super.onEntityAdded(par1Entity);
        this.entityIdMap.addKey(par1Entity.entityId, par1Entity);
        final Entity[] aentity = par1Entity.getParts();

        if (aentity != null) {
            for (int i = 0; i < aentity.length; ++i) {
                this.entityIdMap.addKey(aentity[i].entityId, aentity[i]);
            }
        }
    }

    @Override
    public void onEntityRemoved(final Entity par1Entity) {
        super.onEntityRemoved(par1Entity);
        this.entityIdMap.removeObject(par1Entity.entityId);
        final Entity[] aentity = par1Entity.getParts();

        if (aentity != null) {
            for (int i = 0; i < aentity.length; ++i) {
                this.entityIdMap.removeObject(aentity[i].entityId);
            }
        }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    @Override
    public Entity getEntityByID(final int par1) {
        return (Entity) this.entityIdMap.lookup(par1);
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    @Override
    public boolean addWeatherEffect(final Entity par1Entity) {
        // Cauldron start - vanilla compatibility
        if (par1Entity instanceof net.minecraft.entity.effect.EntityLightningBolt) {
            // CraftBukkit start
            final LightningStrikeEvent lightning = new LightningStrikeEvent(this.getWorld(), (org.bukkit.entity.LightningStrike) par1Entity.getBukkitEntity());
            this.getServer().getPluginManager().callEvent(lightning);

            if (lightning.isCancelled()) {
                return false;
            }
        }
        // Cauldron end
        if (super.addWeatherEffect(par1Entity)) {
            this.mcServer.getConfigurationManager().sendToAllNear(par1Entity.posX, par1Entity.posY, par1Entity.posZ, 512.0D, this.dimension, new Packet71Weather(par1Entity));
            // CraftBukkit end
            return true;
        } else {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    @Override
    public void setEntityState(final Entity par1Entity, final byte par2) {
        final Packet38EntityStatus packet38entitystatus = new Packet38EntityStatus(par1Entity.entityId, par2);
        this.getEntityTracker().sendPacketToAllAssociatedPlayers(par1Entity, packet38entitystatus);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    @Override
    public Explosion newExplosion(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final boolean par9, final boolean par10) {
        // CraftBukkit start
        final Explosion explosion = super.newExplosion(par1Entity, par2, par4, par6, par8, par9, par10);

        if (explosion.wasCanceled) {
            return explosion;
        }

        /* Remove
        explosion.a = flag;
        explosion.b = flag1;
        explosion.a();
        explosion.a(false);
        */
        // CraftBukkit end - TODO: Check if explosions are still properly implemented

        if (!par10) {
            explosion.affectedBlockPositions.clear();
        }

        final Iterator<EntityPlayer> iterator = this.playerEntities.iterator();

        while (iterator.hasNext()) {
            final EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.getDistanceSq(par2, par4, par6) < 4096.0D) {
                ((EntityPlayerMP) entityplayer).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(par2, par4, par6, par8, explosion.affectedBlockPositions, (Vec3) explosion.func_77277_b().get(entityplayer)));
            }
        }

        return explosion;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    @Override
    public void addBlockEvent(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final BlockEventData blockeventdata = new BlockEventData(par1, par2, par3, par4, par5, par6);
        final Iterator<BlockEventData> iterator = this.blockEventCache[this.blockEventCacheIndex].iterator();
        BlockEventData blockeventdata1;

        do {
            if (!iterator.hasNext()) {
                this.blockEventCache[this.blockEventCacheIndex].add(blockeventdata);
                return;
            }

            blockeventdata1 = (BlockEventData) iterator.next();
        }
        while (!blockeventdata1.equals(blockeventdata));
    }

    /**
     * Send and apply locally all pending BlockEvents to each player with 64m radius of the event.
     */
    private void sendAndApplyBlockEvents() {
        while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
            final int i = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;
            final Iterator<BlockEventData> iterator = this.blockEventCache[i].iterator();

            while (iterator.hasNext()) {
                final BlockEventData blockeventdata = (BlockEventData) iterator.next();

                if (this.onBlockEventReceived(blockeventdata)) {
                    this.mcServer.getConfigurationManager().sendToAllNear((double) blockeventdata.getX(), (double) blockeventdata.getY(), (double) blockeventdata.getZ(), 64.0D, this.provider.dimensionId, new Packet54PlayNoteBlock(blockeventdata.getX(), blockeventdata.getY(), blockeventdata.getZ(), blockeventdata.getBlockID(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
                }
            }

            this.blockEventCache[i].clear();
        }
    }

    /**
     * Called to apply a pending BlockEvent to apply to the current world.
     */
    private boolean onBlockEventReceived(final BlockEventData par1BlockEventData) {
        final int i = this.getBlockId(par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ());
        return i == par1BlockEventData.getBlockID() ? Block.blocksList[i].onBlockEventReceived(this, par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ(), par1BlockEventData.getEventID(), par1BlockEventData.getEventParameter()) : false;
    }

    /**
     * Syncs all changes to disk and wait for completion.
     */
    public void flush() {
        this.saveHandler.flush();
    }

    /**
     * Updates all weather states.
     */
    @Override
    protected void updateWeather() {
        final boolean flag = this.isRaining();
        super.updateWeather();

        if (flag != this.isRaining()) {
            // CraftBukkit start - Only send weather packets to those affected
            for (int i = 0; i < this.playerEntities.size(); ++i) {
                if (((EntityPlayerMP) this.playerEntities.get(i)).worldObj == this) {
                    ((EntityPlayerMP) this.playerEntities.get(i)).setPlayerWeather((!flag ? WeatherType.DOWNFALL : WeatherType.CLEAR), false);
                }
            }

            // CraftBukkit end
        }
    }

    /**
     * Gets the MinecraftServer.
     */
    public MinecraftServer getMinecraftServer() {
        return this.mcServer;
    }

    /**
     * Gets the EntityTracker
     */
    public EntityTracker getEntityTracker() {
        return this.theEntityTracker;
    }

    public PlayerManager getPlayerManager() {
        return this.thePlayerManager;
    }

    public Teleporter getDefaultTeleporter() {
        return this.worldTeleporter;
    }

    public File getChunkSaveLocation() {
        return ((AnvilChunkLoader) theChunkProviderServer.currentChunkLoader).chunkSaveLocation;
    }

    // Spigot start
    private void addNextTickIfNeeded(final NextTickListEntry ent) {
        final long coord = LongHash.toLong(ent.xCoord >> 4, ent.zCoord >> 4);
        Set<NextTickListEntry> chunkset = this.tickEntriesByChunk.get(coord);

        if (chunkset == null) {
            chunkset = new HashSet<NextTickListEntry>();
            this.tickEntriesByChunk.put(coord, chunkset);
        } else if (chunkset.contains(ent)) {
            return;
        }

        chunkset.add(ent);

        this.tickEntryQueue.add(ent);
        this.pendingTickListEntriesHashSet.add(ent); // Cauldron - vanilla compatibility
    }

    private void removeNextTickIfNeeded(final NextTickListEntry ent) {
        final long coord = LongHash.toLong(ent.xCoord >> 4, ent.zCoord >> 4);
        final Set<NextTickListEntry> chunkset = this.tickEntriesByChunk.get(coord);

        if (chunkset != null) {
            chunkset.remove(ent);

            if (chunkset.isEmpty()) {
                this.tickEntriesByChunk.remove(coord);
            }
        }

        this.tickEntryQueue.remove(ent);
        this.pendingTickListEntriesHashSet.remove(ent); // Cauldron - vanilla compatibility
    }

    private List<NextTickListEntry> getNextTickEntriesForChunk(final Chunk chunk, final boolean remove) {
        final long coord = LongHash.toLong(chunk.xPosition, chunk.zPosition);
        final Set<NextTickListEntry> chunkset = this.tickEntriesByChunk.get(coord);
        List<NextTickListEntry> list = null;

        if (chunkset != null) {
            list = new ArrayList<NextTickListEntry>(chunkset);

            if (remove) {
                this.tickEntriesByChunk.remove(coord);
                this.tickEntryQueue.removeAll(list);
                chunkset.clear();
            }
        }

        // See if any on list of ticks being processed now
        if (this.nextPendingTickEntry < this.pendingTickEntries.size()) {
            final int xmin = (chunk.xPosition << 4);
            final int xmax = xmin + 16;
            final int zmin = (chunk.zPosition << 4);
            final int zmax = zmin + 16;
            final int te_cnt = this.pendingTickEntries.size();

            for (int i = this.nextPendingTickEntry; i < te_cnt; i++) {
                final NextTickListEntry ent = this.pendingTickEntries.get(i);

                if ((ent.xCoord >= xmin) && (ent.xCoord < xmax) && (ent.zCoord >= zmin) && (ent.zCoord < zmax)) {
                    if (list == null) {
                        list = new ArrayList<NextTickListEntry>();
                    }

                    list.add(ent);
                }
            }
        }

        return list;
    }

    public boolean isEmpty(final int a, final int b, final int c) {
        return isAirBlock(a, b, c);
    }

    public int getTypeId(final int a, final int b, final int c) {
        return getBlockId(a, b, c);
    }
}
