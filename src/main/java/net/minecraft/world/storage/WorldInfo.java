package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WorldInfo {
    /**
     * Holds the seed of the currently world.
     */
    private long randomSeed;
    private WorldType terrainType;
    private String generatorOptions;

    /**
     * The spawn zone position X coordinate.
     */
    private int spawnX;

    /**
     * The spawn zone position Y coordinate.
     */
    private int spawnY;

    /**
     * The spawn zone position Z coordinate.
     */
    private int spawnZ;

    /**
     * Total time for this world.
     */
    private long totalTime;

    /**
     * The current world time in ticks, ranging from 0 to 23999.
     */
    private long worldTime;

    /**
     * The last time the player was in this world.
     */
    private long lastTimePlayed;

    /**
     * The size of entire save of current world on the disk, isn't exactly.
     */
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;

    /**
     * The name of the save defined at world creation.
     */
    private String levelName;

    /**
     * Introduced in beta 1.3, is the save version for future control.
     */
    private int saveVersion;

    /**
     * True if it's raining, false otherwise.
     */
    private boolean raining;

    /**
     * Number of ticks until next rain.
     */
    private int rainTime;

    /**
     * Is thunderbolts failing now?
     */
    private boolean thundering;

    /**
     * Number of ticks untils next thunderbolt.
     */
    private int thunderTime;

    /**
     * The Game Type.
     */
    private EnumGameType theGameType;

    /**
     * Whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    private boolean mapFeaturesEnabled;

    /**
     * Hardcore mode flag
     */
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private GameRules theGameRules;
    private Map<String, NBTBase> additionalProperties;

    //TODO ZeyCodeStart
    private final String NBT_ID = "owners";
    private final String NBT_POS = "pos";
    private final String NBT_PLAYER = "player";

    private final Map<int[], String> blocksPlayers = new HashMap<>();

    public void updateBlocksPlayers(
            @NotNull final EntityPlayer entityPlayer,
            final boolean remove,
            final int x,
            final int y,
            final int z
    ) {
        this.updateBlocksPlayers(entityPlayer.getEntityName(), remove, x, y, z);
    }

    public void updateBlocksPlayers(
            @NotNull final String player,
            final boolean remove,
            final int x,
            final int y,
            final int z
    ) {
        final int[] array = {x, y, z};

        if (this.containsBlock(array))
            this.blocksPlayers.remove(array);

        if (!remove)
            this.blocksPlayers.put(array, player);
    }

    public @Nullable String getPlayerOfBlock(
            final int x,
            final int y,
            final int z
    ) {
        return this.blocksPlayers.get(new int[]{x, y, z});
    }

    public boolean containsBlock(
            final int x,
            final int y,
            final int z
    ) {
        return this.containsBlock(new int[]{x, y, z});
    }

    public boolean containsBlock(final int[] array) {
        return this.blocksPlayers.containsKey(array);
    }
    //TODO ZeyCodeEnd

    protected WorldInfo() {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
    }

    public WorldInfo(final NBTTagCompound par1NBTTagCompound) {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1NBTTagCompound.getLong("RandomSeed");

        if (par1NBTTagCompound.hasKey("generatorName")) {
            final String s = par1NBTTagCompound.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(s);

            if (this.terrainType == null) {
                this.terrainType = WorldType.DEFAULT;
            } else if (this.terrainType.isVersioned()) {
                int i = 0;

                if (par1NBTTagCompound.hasKey("generatorVersion")) {
                    i = par1NBTTagCompound.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
            }

            if (par1NBTTagCompound.hasKey("generatorOptions")) {
                this.generatorOptions = par1NBTTagCompound.getString("generatorOptions");
            }
        }

        this.theGameType = EnumGameType.getByID(par1NBTTagCompound.getInteger("GameType"));

        if (par1NBTTagCompound.hasKey("MapFeatures")) {
            this.mapFeaturesEnabled = par1NBTTagCompound.getBoolean("MapFeatures");
        } else {
            this.mapFeaturesEnabled = true;
        }

        this.spawnX = par1NBTTagCompound.getInteger("SpawnX");
        this.spawnY = par1NBTTagCompound.getInteger("SpawnY");
        this.spawnZ = par1NBTTagCompound.getInteger("SpawnZ");
        this.totalTime = par1NBTTagCompound.getLong("Time");

        if (par1NBTTagCompound.hasKey("DayTime")) {
            this.worldTime = par1NBTTagCompound.getLong("DayTime");
        } else {
            this.worldTime = this.totalTime;
        }

        this.lastTimePlayed = par1NBTTagCompound.getLong("LastPlayed");
        this.sizeOnDisk = par1NBTTagCompound.getLong("SizeOnDisk");
        this.levelName = par1NBTTagCompound.getString("LevelName");
        this.saveVersion = par1NBTTagCompound.getInteger("version");
        this.rainTime = par1NBTTagCompound.getInteger("rainTime");
        this.raining = par1NBTTagCompound.getBoolean("raining");
        this.thunderTime = par1NBTTagCompound.getInteger("thunderTime");
        this.thundering = par1NBTTagCompound.getBoolean("thundering");
        this.hardcore = par1NBTTagCompound.getBoolean("hardcore");
        this.dimension = par1NBTTagCompound.getInteger("dimension"); // Cauldron

        if (par1NBTTagCompound.hasKey("initialized")) {
            this.initialized = par1NBTTagCompound.getBoolean("initialized");
        } else {
            this.initialized = true;
        }

        if (par1NBTTagCompound.hasKey("allowCommands")) {
            this.allowCommands = par1NBTTagCompound.getBoolean("allowCommands");
        } else {
            this.allowCommands = this.theGameType == EnumGameType.CREATIVE;
        }

        if (par1NBTTagCompound.hasKey("Player")) {
            this.playerTag = par1NBTTagCompound.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }

        if (par1NBTTagCompound.hasKey("GameRules")) {
            this.theGameRules.readGameRulesFromNBT(par1NBTTagCompound.getCompoundTag("GameRules"));
        }

        //TODO ZeyCodeStart
        if (par1NBTTagCompound.hasKey(NBT_ID)) {
            final NBTTagList nbtTagList = par1NBTTagCompound.getTagList(NBT_ID);

            for (int i = 0; i < nbtTagList.tagCount(); i++) {
                final NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtTagList.tagAt(i);

                final int[] array = nbtTagCompound.getIntArray(NBT_POS);
                final String player = nbtTagCompound.getString(NBT_PLAYER);

                this.updateBlocksPlayers(player, false, array[0], array[1], array[2]);
            }
        }
        //TODO ZeyCodeEnd
    }

    public WorldInfo(final WorldSettings par1WorldSettings, final String par2Str) {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1WorldSettings.getSeed();
        this.theGameType = par1WorldSettings.getGameType();
        this.mapFeaturesEnabled = par1WorldSettings.isMapFeaturesEnabled();
        this.levelName = par2Str;
        this.hardcore = par1WorldSettings.getHardcoreEnabled();
        this.terrainType = par1WorldSettings.getTerrainType();
        this.generatorOptions = par1WorldSettings.func_82749_j();
        this.allowCommands = par1WorldSettings.areCommandsAllowed();
        this.initialized = false;
    }

    public WorldInfo(final WorldInfo par1WorldInfo) {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1WorldInfo.randomSeed;
        this.terrainType = par1WorldInfo.terrainType;
        this.generatorOptions = par1WorldInfo.generatorOptions;
        this.theGameType = par1WorldInfo.theGameType;
        this.mapFeaturesEnabled = par1WorldInfo.mapFeaturesEnabled;
        this.spawnX = par1WorldInfo.spawnX;
        this.spawnY = par1WorldInfo.spawnY;
        this.spawnZ = par1WorldInfo.spawnZ;
        this.totalTime = par1WorldInfo.totalTime;
        this.worldTime = par1WorldInfo.worldTime;
        this.lastTimePlayed = par1WorldInfo.lastTimePlayed;
        this.sizeOnDisk = par1WorldInfo.sizeOnDisk;
        this.playerTag = par1WorldInfo.playerTag;
        this.dimension = par1WorldInfo.dimension;
        this.levelName = par1WorldInfo.levelName;
        this.saveVersion = par1WorldInfo.saveVersion;
        this.rainTime = par1WorldInfo.rainTime;
        this.raining = par1WorldInfo.raining;
        this.thunderTime = par1WorldInfo.thunderTime;
        this.thundering = par1WorldInfo.thundering;
        this.hardcore = par1WorldInfo.hardcore;
        this.allowCommands = par1WorldInfo.allowCommands;
        this.initialized = par1WorldInfo.initialized;
        this.theGameRules = par1WorldInfo.theGameRules;
        this.dimension = par1WorldInfo.dimension; // Cauldron
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound getNBTTagCompound() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.updateTagCompound(nbttagcompound, this.playerTag);
        return nbttagcompound;
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
     */
    public NBTTagCompound cloneNBTCompound(final NBTTagCompound par1NBTTagCompound) {
        final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        this.updateTagCompound(nbttagcompound1, par1NBTTagCompound);
        return nbttagcompound1;
    }

    private void updateTagCompound(final NBTTagCompound par1NBTTagCompound, final NBTTagCompound par2NBTTagCompound) {
        par1NBTTagCompound.setLong("RandomSeed", this.randomSeed);
        par1NBTTagCompound.setString("generatorName", this.terrainType.getWorldTypeName());
        par1NBTTagCompound.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
        par1NBTTagCompound.setString("generatorOptions", this.generatorOptions);
        par1NBTTagCompound.setInteger("GameType", this.theGameType.getID());
        par1NBTTagCompound.setBoolean("MapFeatures", this.mapFeaturesEnabled);
        par1NBTTagCompound.setInteger("SpawnX", this.spawnX);
        par1NBTTagCompound.setInteger("SpawnY", this.spawnY);
        par1NBTTagCompound.setInteger("SpawnZ", this.spawnZ);
        par1NBTTagCompound.setLong("Time", this.totalTime);
        par1NBTTagCompound.setLong("DayTime", this.worldTime);
        par1NBTTagCompound.setLong("SizeOnDisk", this.sizeOnDisk);
        par1NBTTagCompound.setLong("LastPlayed", MinecraftServer.getSystemTimeMillis());
        par1NBTTagCompound.setString("LevelName", this.levelName);
        par1NBTTagCompound.setInteger("version", this.saveVersion);
        par1NBTTagCompound.setInteger("rainTime", this.rainTime);
        par1NBTTagCompound.setBoolean("raining", this.raining);
        par1NBTTagCompound.setInteger("thunderTime", this.thunderTime);
        par1NBTTagCompound.setBoolean("thundering", this.thundering);
        par1NBTTagCompound.setBoolean("hardcore", this.hardcore);
        par1NBTTagCompound.setBoolean("allowCommands", this.allowCommands);
        par1NBTTagCompound.setBoolean("initialized", this.initialized);
        par1NBTTagCompound.setCompoundTag("GameRules", this.theGameRules.writeGameRulesToNBT());
        par1NBTTagCompound.setInteger("dimension", this.dimension); // Cauldron

        if (par2NBTTagCompound != null) {
            par1NBTTagCompound.setCompoundTag("Player", par2NBTTagCompound);
        }

        //TODO ZeyCodeStart
        final NBTTagList nbtTagList = new NBTTagList();

        for (final Map.Entry<int[], String> entry : this.blocksPlayers.entrySet()) {
            final NBTTagCompound entryTag = new NBTTagCompound();

            entryTag.setIntArray(NBT_POS, entry.getKey());
            entryTag.setString(NBT_PLAYER, entry.getValue());

            nbtTagList.appendTag(entryTag);
        }

        par1NBTTagCompound.setTag(NBT_ID, nbtTagList);
        //TODO ZeyCodeEnd
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed() {
        return this.randomSeed;
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX() {
        return this.spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY() {
        return this.spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ() {
        return this.spawnZ;
    }

    public long getWorldTotalTime() {
        return this.totalTime;
    }

    /**
     * Get current world time
     */
    public long getWorldTime() {
        return this.worldTime;
    }

    @SideOnly(Side.CLIENT)
    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound() {
        return this.playerTag;
    }

    // Cauldron start

    /**
     * Sets the Dimension.
     */
    public void setDimension(final int dim) {
        this.dimension = dim;
    }

    public int getDimension() {
        return this.dimension;
    }
    // Cauldron end

    /**
     * Returns vanilla MC dimension (-1,0,1). For custom dimension compatibility, always prefer
     * WorldProvider.dimensionID accessed from World.provider.dimensionID
     */
    public int getVanillaDimension() {
        return this.dimension;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Set the x spawn position to the passed in value
     */
    public void setSpawnX(final int par1) {
        this.spawnX = par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the y spawn position
     */
    public void setSpawnY(final int par1) {
        this.spawnY = par1;
    }

    public void incrementTotalWorldTime(final long par1) {
        this.totalTime = par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Set the z spawn position to the passed in value
     */
    public void setSpawnZ(final int par1) {
        this.spawnZ = par1;
    }

    /**
     * Set current world time
     */
    public void setWorldTime(final long par1) {
        this.worldTime = par1;
    }

    /**
     * Sets the spawn zone position. Args: x, y, z
     */
    public void setSpawnPosition(final int par1, final int par2, final int par3) {
        this.spawnX = par1;
        this.spawnY = par2;
        this.spawnZ = par3;
    }

    /**
     * Get current world name
     */
    public String getWorldName() {
        return this.levelName;
    }

    public void setWorldName(final String par1Str) {
        this.levelName = par1Str;
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion() {
        return this.saveVersion;
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(final int par1) {
        this.saveVersion = par1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Return the last time the player was in this world.
     */
    public long getLastTimePlayed() {
        return this.lastTimePlayed;
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering() {
        return this.thundering;
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(final boolean par1) {
        this.thundering = par1;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime() {
        return this.thunderTime;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(final int par1) {
        this.thunderTime = par1;
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining() {
        return this.raining;
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(final boolean par1) {
        this.raining = par1;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime() {
        return this.rainTime;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(final int par1) {
        this.rainTime = par1;
    }

    /**
     * Gets the GameType.
     */
    public EnumGameType getGameType() {
        return this.theGameType;
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    public boolean isMapFeaturesEnabled() {
        return this.mapFeaturesEnabled;
    }

    /**
     * Sets the GameType.
     */
    public void setGameType(final EnumGameType par1EnumGameType) {
        this.theGameType = par1EnumGameType;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcoreModeEnabled() {
        return this.hardcore;
    }

    public WorldType getTerrainType() {
        return this.terrainType;
    }

    public void setTerrainType(final WorldType par1WorldType) {
        this.terrainType = par1WorldType;
    }

    public String getGeneratorOptions() {
        return this.generatorOptions;
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed() {
        return this.allowCommands;
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setServerInitialized(final boolean par1) {
        this.initialized = par1;
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance() {
        return this.theGameRules;
    }

    /**
     * Adds this WorldInfo instance to the crash report.
     */
    public void addToCrashReport(final CrashReportCategory par1CrashReportCategory) {
        par1CrashReportCategory.addCrashSectionCallable("Level seed", new CallableLevelSeed(this));
        par1CrashReportCategory.addCrashSectionCallable("Level generator", new CallableLevelGenerator(this));
        par1CrashReportCategory.addCrashSectionCallable("Level generator options", new CallableLevelGeneratorOptions(this));
        par1CrashReportCategory.addCrashSectionCallable("Level spawn location", new CallableLevelSpawnLocation(this));
        par1CrashReportCategory.addCrashSectionCallable("Level time", new CallableLevelTime(this));
        par1CrashReportCategory.addCrashSectionCallable("Level dimension", new CallableLevelDimension(this));
        par1CrashReportCategory.addCrashSectionCallable("Level storage version", new CallableLevelStorageVersion(this));
        par1CrashReportCategory.addCrashSectionCallable("Level weather", new CallableLevelWeather(this));
        par1CrashReportCategory.addCrashSectionCallable("Level game mode", new CallableLevelGamemode(this));
    }

    /**
     * Return the terrain type of a world
     */
    static WorldType getTerrainTypeOfWorld(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.terrainType;
    }

    /**
     * Return the map feautures enabled of a world
     */
    static boolean getMapFeaturesEnabled(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.mapFeaturesEnabled;
    }

    static String getWorldGeneratorOptions(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.generatorOptions;
    }

    static int getSpawnXCoordinate(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.spawnX;
    }

    static int getSpawnYCoordinate(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.spawnY;
    }

    static int getSpawnZCoordinate(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.spawnZ;
    }

    static long func_85126_g(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.totalTime;
    }

    static long getWorldTime(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.worldTime;
    }

    static int func_85122_i(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.dimension;
    }

    static int getSaveVersion(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.saveVersion;
    }

    static int getRainTime(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.rainTime;
    }

    /**
     * Returns wether it's raining or not.
     */
    static boolean getRaining(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.raining;
    }

    static int getThunderTime(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.thunderTime;
    }

    /**
     * Returns wether it's thundering or not.
     */
    static boolean getThundering(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.thundering;
    }

    static EnumGameType getGameType(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.theGameType;
    }

    static boolean func_85117_p(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.hardcore;
    }

    static boolean func_85131_q(final WorldInfo par0WorldInfo) {
        return par0WorldInfo.allowCommands;
    }

    /**
     * Allow access to additional mod specific world based properties
     * Used by FML to store mod list associated with a world, and maybe an id map
     * Used by Forge to store the dimensions available to a world
     *
     * @param additionalProperties
     */
    public void setAdditionalProperties(final Map<String, NBTBase> additionalProperties) {
        // one time set for this
        if (this.additionalProperties == null) {
            this.additionalProperties = additionalProperties;
        }
    }

    public NBTBase getAdditionalProperty(final String additionalProperty) {
        return this.additionalProperties != null ? this.additionalProperties.get(additionalProperty) : null;
    }
}
