package org.spigotmc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;

public class SpigotWorldConfig {

    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public SpigotWorldConfig(String worldName) {
        this.worldName = worldName;
        this.config = SpigotConfig.config;
        if (worldName.toLowerCase().contains("dummy")) return;
        try {
            init();
        } catch (Throwable t) {
            log("Something bad happened while trying init the spigot config for [" + worldName + "]");
            t.printStackTrace();
        }
    }

    public void init() {
        this.verbose = getBoolean("verbose", false);

        if (verbose)
            log("-------- World Settings For [" + worldName + "] --------");
        SpigotConfig.readConfig(SpigotWorldConfig.class, this);
    }

    private void log(String s) {
        if (verbose) {
            Bukkit.getLogger().info(s);
        }
    }

    private void set(String path, Object val) {
        config.set("world-settings.default." + path, val);
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getBoolean("world-settings." + worldName + "." + path, config.getBoolean("world-settings.default." + path));
    }

    private double getDouble(String path, double def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getDouble("world-settings." + worldName + "." + path, config.getDouble("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getInt("world-settings." + worldName + "." + path, config.getInt("world-settings.default." + path));
    }

    private <T> List getList(String path, T def) {
        config.addDefault("world-settings.default." + path, def);
        return (List<T>) config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getString("world-settings." + worldName + "." + path, config.getString("world-settings.default." + path));
    }

    public int chunksPerTick;

    private void chunksPerTick() {

        //TODO ZoomCodeReplace 650 on 80
        chunksPerTick = getInt("chunks-per-tick", 80);
        log("Chunks to Grow per Tick: " + chunksPerTick);
    }

    // Crop growth rates
    public int cactusModifier;
    public int caneModifier;
    public int melonModifier;
    public int mushroomModifier;
    public int pumpkinModifier;
    public int saplingModifier;
    public int wheatModifier;

    private void growthModifiers() {

        //TODO ZoomCodeReplace 100 on 800
        cactusModifier = getInt("growth.cactus-modifier", 800);
        log("Cactus Growth Modifier: " + cactusModifier + "%");

        //TODO ZoomCodeReplace 100 on 800
        caneModifier = getInt("growth.cane-modifier", 800);
        log("Cane Growth Modifier: " + caneModifier + "%");

        //TODO ZoomCodeReplace 100 on 800
        melonModifier = getInt("growth.melon-modifier", 800);
        log("Melon Growth Modifier: " + melonModifier + "%");

        //TODO ZoomCodeReplace 100 on 800
        mushroomModifier = getInt("growth.mushroom-modifier", 800);
        log("Mushroom Growth Modifier: " + mushroomModifier + "%");

        //TODO ZoomCodeReplace 100 on 800
        pumpkinModifier = getInt("growth.pumpkin-modifier", 800);
        log("Pumpkin Growth Modifier: " + pumpkinModifier + "%");

        //TODO ZoomCodeReplace 100 on 800
        saplingModifier = getInt("growth.sapling-modifier", 800);
        log("Sapling Growth Modifier: " + saplingModifier + "%");

        //TODO ZoomCodeReplace 100 on 800
        wheatModifier = getInt("growth.wheat-modifier", 800);
        log("Wheat Growth Modifier: " + wheatModifier + "%");
    }

    public double itemMerge;

    private void itemMerge() {
        //TODO ZoomCodeReplace 2.5 on 6
        itemMerge = getDouble("merge-radius.item", 6);
        log("Item Merge Radius: " + itemMerge);
    }

    public double expMerge;

    private void expMerge() {
        //TODO ZoomCodeReplace 3.0 on 4
        expMerge = getDouble("merge-radius.exp", 4);
        log("Experience Merge Radius: " + expMerge);
    }

    public int viewDistance;

    private void viewDistance() {
        viewDistance = getInt("view-distance", Bukkit.getViewDistance());
        log("View Distance: " + viewDistance);
    }

    public boolean antiXray = true;
    public int engineMode = 1;
    public List<Integer> blocks = Arrays.asList(new Integer[]
            {
                    1, 5, 14, 15, 16, 21, 48, 49, 54, 56, 73, 74, 82, 129, 130
            });
    public AntiXray antiXrayInstance;

    private void antiXray() {
        antiXray = false; // Cauldron disable this for now getBoolean( "anti-xray.enabled", antiXray );
        log("Anti X-Ray: " + antiXray);

        engineMode = getInt("anti-xray.engine-mode", engineMode);
        log("\tEngine Mode: " + engineMode);

        if (SpigotConfig.version < 3) {
            set("anti-xray.blocks", blocks);
        }
        blocks = getList("anti-xray.blocks", blocks);
        log("\tBlocks: " + blocks);

        antiXrayInstance = new AntiXray(this);
    }

    public byte mobSpawnRange;

    private void mobSpawnRange() {

        //TODO ZoomCodeReplace 4 on 3
        mobSpawnRange = (byte) getInt("mob-spawn-range", 3);
        log("Mob Spawn Range: " + mobSpawnRange);
    }

    //TODO ZoomCodeReplace 32 on 0
    public int animalActivationRange = 0;
    //TODO ZoomCodeReplace 32 on 10
    public int monsterActivationRange = 10;
    //TODO ZoomCodeReplace 16 on 0
    public int miscActivationRange = 0;

    private void activationRange() {
        animalActivationRange = getInt("entity-activation-range.animals", animalActivationRange);
        monsterActivationRange = getInt("entity-activation-range.monsters", monsterActivationRange);
        miscActivationRange = getInt("entity-activation-range.misc", miscActivationRange);
        log("Entity Activation Range: An " + animalActivationRange + " / Mo " + monsterActivationRange + " / Mi " + miscActivationRange);
    }

    public int playerTrackingRange = 48;
    public int animalTrackingRange = 48;
    public int monsterTrackingRange = 48;
    public int miscTrackingRange = 32;
    public int maxTrackingRange = 64;

    private void trackingRange() {
        playerTrackingRange = getInt("entity-tracking-range.players", playerTrackingRange);
        animalTrackingRange = getInt("entity-tracking-range.animals", animalTrackingRange);
        monsterTrackingRange = getInt("entity-tracking-range.monsters", monsterTrackingRange);
        miscTrackingRange = getInt("entity-tracking-range.misc", miscTrackingRange);
        maxTrackingRange = getInt("entity-tracking-range.other", maxTrackingRange);
        log("Entity Tracking Range: Pl " + playerTrackingRange + " / An " + animalTrackingRange + " / Mo " + monsterTrackingRange + " / Mi " + miscTrackingRange + " / Other " + maxTrackingRange);
    }

    //TODO ZoomCodeReplace 8 on 24
    public int hopperTransfer = 24;
    public int hopperCheck = 24;

    private void hoppers() {
        // Set the tick delay between hopper item movements
        hopperTransfer = getInt("ticks-per.hopper-transfer", hopperTransfer);
        // Set the tick delay between checking for items after the associated
        // container is empty. Default to the hopperTransfer value to prevent
        // hopper sorting machines from becoming out of sync.
        hopperCheck = getInt("ticks-per.hopper-check", hopperTransfer);
        log("Hopper Transfer: " + hopperTransfer + " Hopper Check: " + hopperCheck);
    }

    public boolean randomLightUpdates;

    private void lightUpdates() {
        //TODO ZeyCodeReplace false on true
        randomLightUpdates = getBoolean("random-light-updates", true);
        log("Random Lighting Updates: " + randomLightUpdates);
    }

    public boolean saveMineshaftStructureInfo;

    private void mineshaftStructureInfo() {
        saveMineshaftStructureInfo = getBoolean("save-mineshaft-structure-info", false);
        log("Mineshaft Structure Info Saving: " + saveMineshaftStructureInfo);
        /*if ( !saveStructureInfo )
        {
            log( "*** WARNING *** You have selected to NOT save structure info. This may cause structures such as fortresses to not spawn mobs when updating to 1.7!" );
            log( "*** WARNING *** Please use this option with caution, SpigotMC is not responsible for any issues this option may cause in the future!" );
        }*/
    }

    public int itemDespawnRate;

    private void itemDespawnRate() {
        itemDespawnRate = getInt("item-despawn-rate", 6000);
        log("Item Despawn Rate: " + itemDespawnRate);
    }

    public int arrowDespawnRate;

    private void arrowDespawnRate() {
        arrowDespawnRate = getInt("arrow-despawn-rate", 1200);
        log("Arrow Despawn Rate: " + arrowDespawnRate);
    }
}
