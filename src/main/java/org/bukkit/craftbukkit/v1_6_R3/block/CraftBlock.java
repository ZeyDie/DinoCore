package org.bukkit.craftbukkit.v1_6_R3.block;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.cauldron.block.CraftCustomContainer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.v1_6_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// Cauldron start
// Cauldron end

public class CraftBlock implements Block {
    private final CraftChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    // Cauldron start - add support for custom biomes
    private static final Biome[] BIOME_MAPPING = new Biome[net.minecraft.world.biome.BiomeGenBase.biomeList.length];
    private static final net.minecraft.world.biome.BiomeGenBase[] BIOMEBASE_MAPPING = new net.minecraft.world.biome.BiomeGenBase[net.minecraft.world.biome.BiomeGenBase.biomeList.length];
    // Cauldron end

    public CraftBlock(final CraftChunk chunk, final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk = chunk;
    }

    public World getWorld() {
        return chunk.getWorld();
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public Location getLocation(final Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(x);
            loc.setY(y);
            loc.setZ(z);
            loc.setYaw(0);
            loc.setPitch(0);
        }

        return loc;
    }

    public BlockVector getVector() {
        return new BlockVector(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setData(final byte data) {
        chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data, 3);
    }

    public void setData(final byte data, final boolean applyPhysics) {
        if (applyPhysics) {
            chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data, 3);
        } else {
            chunk.getHandle().worldObj.setBlockMetadataWithNotify(x, y, z, data, 2);
        }
    }

    public byte getData() {
        return (byte) chunk.getHandle().getBlockMetadata(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public void setType(final Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(final int type) {
        return chunk.getHandle().worldObj.setBlock(x, y, z, type, getData(), 3);
    }

    public boolean setTypeId(final int type, final boolean applyPhysics) {
        if (applyPhysics) {
            return setTypeId(type);
        } else {
            return chunk.getHandle().worldObj.setBlock(x, y, z, type, getData(), 2);
        }
    }

    public boolean setTypeIdAndData(final int type, final byte data, final boolean applyPhysics) {
        if (applyPhysics) {
            return chunk.getHandle().worldObj.setBlock(x, y, z, type, data, 3);
        } else {
            final boolean success = chunk.getHandle().worldObj.setBlock(x, y, z, type, data, 2);
            if (success) {
                chunk.getHandle().worldObj.markBlockForUpdate(x, y, z);
            }
            return success;
        }
    }

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    public int getTypeId() {
        return chunk.getHandle().getBlockID(this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public byte getLightLevel() {
        return (byte) chunk.getHandle().worldObj.getBlockLightValue(this.x, this.y, this.z);
    }

    public byte getLightFromSky() {
        return (byte) chunk.getHandle().getSavedLightValue(net.minecraft.world.EnumSkyBlock.Sky, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }

    public byte getLightFromBlocks() {
        return (byte) chunk.getHandle().getSavedLightValue(net.minecraft.world.EnumSkyBlock.Block, this.x & 0xF, this.y & 0xFF, this.z & 0xF);
    }


    public Block getFace(final BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getFace(final BlockFace face, final int distance) {
        return getRelative(face, distance);
    }

    public Block getRelative(final int modX, final int modY, final int modZ) {
        return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
    }

    public Block getRelative(final BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getRelative(final BlockFace face, final int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    public BlockFace getFace(final Block block) {
        final BlockFace[] values = BlockFace.values();

        for (final BlockFace face : values) {
            if ((this.getX() + face.getModX() == block.getX()) &&
                    (this.getY() + face.getModY() == block.getY()) &&
                    (this.getZ() + face.getModZ() == block.getZ())
            ) {
                return face;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "CraftBlock{" + "chunk=" + chunk + ",x=" + x + ",y=" + y + ",z=" + z + ",type=" + getType() + ",data=" + getData() + '}';
    }

    /**
     * Notch uses a 0-5 to mean DOWN, UP, NORTH, SOUTH, WEST, EAST
     * in that order all over. This method is convenience to convert for us.
     *
     * @return BlockFace the BlockFace represented by this number
     */
    public static BlockFace notchToBlockFace(final int notch) {
        switch (notch) {
            case 0:
                return BlockFace.DOWN;
            case 1:
                return BlockFace.UP;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.SOUTH;
            case 4:
                return BlockFace.WEST;
            case 5:
                return BlockFace.EAST;
            default:
                return BlockFace.SELF;
        }
    }

    public static int blockFaceToNotch(final BlockFace face) {
        switch (face) {
            case DOWN:
                return 0;
            case UP:
                return 1;
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
            default:
                return 7; // Good as anything here, but technically invalid
        }
    }

    public BlockState getState() {
        final Material material = getType();
        // Cauldron start - if null, check for TE that implements IInventory
        if (material == null) {
            final TileEntity te = ((CraftWorld) this.getWorld()).getHandle().getBlockTileEntity(this.getX(), this.getY(), this.getZ());
            if (te != null && te instanceof IInventory) {
                // In order to allow plugins to properly grab the container location, we must pass a class that extends CraftBlockState and implements InventoryHolder.
                // Note: This will be returned when TileEntity.getOwner() is called
                return new CraftCustomContainer(this);
            }
            // pass default state
            return new CraftBlockState(this);
        }
        // Cauldron end
        switch (material) {
            case SIGN:
            case SIGN_POST:
            case WALL_SIGN:
                return new CraftSign(this);
            case CHEST:
            case TRAPPED_CHEST:
                return new CraftChest(this);
            case BURNING_FURNACE:
            case FURNACE:
                return new CraftFurnace(this);
            case DISPENSER:
                return new CraftDispenser(this);
            case DROPPER:
                return new CraftDropper(this);
            case HOPPER:
                return new CraftHopper(this);
            case MOB_SPAWNER:
                return new CraftCreatureSpawner(this);
            case NOTE_BLOCK:
                return new CraftNoteBlock(this);
            case JUKEBOX:
                return new CraftJukebox(this);
            case BREWING_STAND:
                return new CraftBrewingStand(this);
            case SKULL:
                return new CraftSkull(this);
            case COMMAND:
                return new CraftCommandBlock(this);
            case BEACON:
                return new CraftBeacon(this);
            default:
                // Cauldron start
                final TileEntity te = ((CraftWorld) this.getWorld()).getHandle().getBlockTileEntity(this.getX(), this.getY(), this.getZ());
                if (te != null && te instanceof IInventory) {
                    // In order to allow plugins to properly grab the container location, we must pass a class that extends CraftBlockState and implements InventoryHolder.
                    // Note: This will be returned when TileEntity.getOwner() is called
                    return new CraftCustomContainer(this);
                }
                // pass default state
                // Cauldron end
                return new CraftBlockState(this);
        }
    }

    public Biome getBiome() {
        return getWorld().getBiome(x, z);
    }

    public void setBiome(final Biome bio) {
        getWorld().setBiome(x, z, bio);
    }

    public static Biome biomeBaseToBiome(final net.minecraft.world.biome.BiomeGenBase base) {
        if (base == null) {
            return null;
        }

        return BIOME_MAPPING[base.biomeID];
    }

    public static net.minecraft.world.biome.BiomeGenBase biomeToBiomeBase(final Biome bio) {
        if (bio == null) {
            return null;
        }
        return BIOMEBASE_MAPPING[bio.ordinal()];
    }

    public double getTemperature() {
        return getWorld().getTemperature(x, z);
    }

    public double getHumidity() {
        return getWorld().getHumidity(x, z);
    }

    public boolean isBlockPowered() {
        return chunk.getHandle().worldObj.getBlockPowerInput(x, y, z) > 0;
    }

    public boolean isBlockIndirectlyPowered() {
        return chunk.getHandle().worldObj.isBlockIndirectlyGettingPowered(x, y, z);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CraftBlock)) return false;
        final CraftBlock other = (CraftBlock) o;

        return this.x == other.x && this.y == other.y && this.z == other.z && this.getWorld().equals(other.getWorld());
    }

    @Override
    public int hashCode() {
        return this.y << 24 ^ this.x ^ this.z ^ this.getWorld().hashCode();
    }

    public boolean isBlockFacePowered(final BlockFace face) {
        return chunk.getHandle().worldObj.getIndirectPowerOutput(x, y, z, blockFaceToNotch(face));
    }

    public boolean isBlockFaceIndirectlyPowered(final BlockFace face) {
        final int power = chunk.getHandle().worldObj.getIndirectPowerLevelTo(x, y, z, blockFaceToNotch(face));

        final Block relative = getRelative(face);
        if (relative.getType() == Material.REDSTONE_WIRE) {
            return Math.max(power, relative.getData()) > 0;
        }

        return power > 0;
    }

    public int getBlockPower(final BlockFace face) {
        int power = 0;
        final net.minecraft.block.BlockRedstoneWire wire = net.minecraft.block.Block.redstoneWire;
        final net.minecraft.world.World world = chunk.getHandle().worldObj;
        if ((face == BlockFace.DOWN || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y - 1, z, 0))
            power = wire.getMaxCurrentStrength(world, x, y - 1, z, power);
        if ((face == BlockFace.UP || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y + 1, z, 1))
            power = wire.getMaxCurrentStrength(world, x, y + 1, z, power);
        if ((face == BlockFace.EAST || face == BlockFace.SELF) && world.getIndirectPowerOutput(x + 1, y, z, 2))
            power = wire.getMaxCurrentStrength(world, x + 1, y, z, power);
        if ((face == BlockFace.WEST || face == BlockFace.SELF) && world.getIndirectPowerOutput(x - 1, y, z, 3))
            power = wire.getMaxCurrentStrength(world, x - 1, y, z, power);
        if ((face == BlockFace.NORTH || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y, z - 1, 4))
            power = wire.getMaxCurrentStrength(world, x, y, z - 1, power);
        if ((face == BlockFace.SOUTH || face == BlockFace.SELF) && world.getIndirectPowerOutput(x, y, z + 1, 5))
            power = wire.getMaxCurrentStrength(world, x, y, z - 1, power);
        return power > 0 ? power : (face == BlockFace.SELF ? isBlockIndirectlyPowered() : isBlockFaceIndirectlyPowered(face)) ? 15 : 0;
    }

    public int getBlockPower() {
        return getBlockPower(BlockFace.SELF);
    }

    public boolean isEmpty() {
        // Cauldron start - support custom air blocks (Railcraft player aura tracking block)
        //return getType() == Material.AIR;
        if (getType() == Material.AIR) return true;
        if (!(getWorld() instanceof CraftWorld)) return false;
        return ((CraftWorld) getWorld()).getHandle().isAirBlock(getX(), getY(), getZ());
        // Cauldron end
    }

    public boolean isLiquid() {
        return (getType() == Material.WATER) || (getType() == Material.STATIONARY_WATER) || (getType() == Material.LAVA) || (getType() == Material.STATIONARY_LAVA);
    }

    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(net.minecraft.block.Block.blocksList[this.getTypeId()].blockMaterial.getMaterialMobility());
    }

    private boolean itemCausesDrops(final ItemStack item) {
        final net.minecraft.block.Block block = net.minecraft.block.Block.blocksList[this.getTypeId()];
        final net.minecraft.item.Item itemType = item != null ? net.minecraft.item.Item.itemsList[item.getTypeId()] : null;
        return block != null && (block.blockMaterial.isToolNotRequired() || (itemType != null && itemType.canHarvestBlock(block)));
    }

    public boolean breakNaturally() {
        // Order matters here, need to drop before setting to air so skulls can get their data
        final net.minecraft.block.Block block = net.minecraft.block.Block.blocksList[this.getTypeId()];
        final byte data = getData();
        boolean result = false;

        if (block != null) {
            block.dropBlockAsItemWithChance(chunk.getHandle().worldObj, x, y, z, data, 1.0F, 0);
            result = true;
        }

        setTypeId(Material.AIR.getId());
        return result;
    }

    public boolean breakNaturally(final ItemStack item) {
        if (itemCausesDrops(item)) {
            return breakNaturally();
        } else {
            return setTypeId(Material.AIR.getId());
        }
    }

    public Collection<ItemStack> getDrops() {
        final List<ItemStack> drops = new ArrayList<ItemStack>();

        final net.minecraft.block.Block block = net.minecraft.block.Block.blocksList[this.getTypeId()];
        if (block != null) {
            final byte data = getData();
            // based on nms.Block.dropNaturally
            final int count = block.quantityDroppedWithBonus(0, chunk.getHandle().worldObj.rand);
            for (int i = 0; i < count; ++i) {
                final int item = block.idDropped(data, chunk.getHandle().worldObj.rand, 0);
                if (item > 0) {
                    // Skulls are special, their data is based on the tile entity
                    if (net.minecraft.block.Block.skull.blockID == this.getTypeId()) {
                        final net.minecraft.item.ItemStack nmsStack = new net.minecraft.item.ItemStack(item, 1, block.getDamageValue(chunk.getHandle().worldObj, x, y, z));
                        final net.minecraft.tileentity.TileEntitySkull tileentityskull = (net.minecraft.tileentity.TileEntitySkull) chunk.getHandle().worldObj.getBlockTileEntity(x, y, z);

                        if (tileentityskull.getSkullType() == 3 && tileentityskull.getExtraType() != null && !tileentityskull.getExtraType().isEmpty()) {
                            nmsStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                            nmsStack.getTagCompound().setString("SkullOwner", tileentityskull.getExtraType());
                        }

                        drops.add(CraftItemStack.asBukkitCopy(nmsStack));
                    } else {
                        drops.add(new ItemStack(item, 1, (short) block.damageDropped(data)));
                    }
                }
            }
        }
        return drops;
    }

    public Collection<ItemStack> getDrops(final ItemStack item) {
        if (itemCausesDrops(item)) {
            return getDrops();
        } else {
            return Collections.emptyList();
        }
    }

    /* Build biome index based lookup table for BiomeBase to Biome mapping */
    public static void initMappings() { // Cauldron - initializer to initMappings() method; called in CraftServer
        //BIOME_MAPPING = new Biome[net.minecraft.world.biome.BiomeGenBase.biomeList.length]; // Cauldron - move up
        //BIOMEBASE_MAPPING = new net.minecraft.world.biome.BiomeGenBase[Biome.values().length]; // Cauldron - move up
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.swampland.biomeID] = Biome.SWAMPLAND;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.forest.biomeID] = Biome.FOREST;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.taiga.biomeID] = Biome.TAIGA;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.desert.biomeID] = Biome.DESERT;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.plains.biomeID] = Biome.PLAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.hell.biomeID] = Biome.HELL;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.sky.biomeID] = Biome.SKY;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.river.biomeID] = Biome.RIVER;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHills.biomeID] = Biome.EXTREME_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.ocean.biomeID] = Biome.OCEAN;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.frozenOcean.biomeID] = Biome.FROZEN_OCEAN;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.frozenRiver.biomeID] = Biome.FROZEN_RIVER;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.icePlains.biomeID] = Biome.ICE_PLAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.iceMountains.biomeID] = Biome.ICE_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mushroomIsland.biomeID] = Biome.MUSHROOM_ISLAND;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.mushroomIslandShore.biomeID] = Biome.MUSHROOM_SHORE;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.beach.biomeID] = Biome.BEACH;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.desertHills.biomeID] = Biome.DESERT_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.forestHills.biomeID] = Biome.FOREST_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.taigaHills.biomeID] = Biome.TAIGA_HILLS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.extremeHillsEdge.biomeID] = Biome.SMALL_MOUNTAINS;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungle.biomeID] = Biome.JUNGLE;
        BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.jungleHills.biomeID] = Biome.JUNGLE_HILLS;
        /* Sanity check - we should have a record for each record in the BiomeBase.a table */
        /* Helps avoid missed biomes when we upgrade bukkit to new code with new biomes */
        for (int i = 0; i < BIOME_MAPPING.length; i++) {
            if ((net.minecraft.world.biome.BiomeGenBase.biomeList[i] != null) && (BIOME_MAPPING[i] == null)) {
                // Cauldron start - add support for mod biomes
                //throw new IllegalArgumentException("Missing Biome mapping for BiomeBase[" + i + "]");
                final String name = net.minecraft.world.biome.BiomeGenBase.biomeList[i].biomeName;
                final int id = net.minecraft.world.biome.BiomeGenBase.biomeList[i].biomeID;

                System.out.println("Adding biome mapping " + net.minecraft.world.biome.BiomeGenBase.biomeList[i].biomeID + " " + name + " at BiomeBase[" + i + "]");
                net.minecraftforge.common.EnumHelper.addBukkitBiome(name); // Forge
                BIOME_MAPPING[net.minecraft.world.biome.BiomeGenBase.biomeList[i].biomeID] = ((Biome) Enum.valueOf(Biome.class, name));
                // Cauldron end           
            }
            if (BIOME_MAPPING[i] != null) {  /* Build reverse mapping for setBiome */
                BIOMEBASE_MAPPING[BIOME_MAPPING[i].ordinal()] = net.minecraft.world.biome.BiomeGenBase.biomeList[i];
            }
        }
    }

    // Cauldron start - if cauldron.dump-materials is true, dump all materials with their corresponding id's
    public static void dumpMaterials() {
        if (MinecraftServer.getServer().cauldronConfig.dumpMaterials.getValue()) {
            FMLLog.info("Cauldron Dump Materials is ENABLED. Starting dump...");
            for (int i = 0; i < 32000; i++) {
                final Material material = Material.getMaterial(i);
                if (material != null) {
                    FMLLog.info("Found material " + material + " with ID " + i);
                }
            }
            FMLLog.info("Cauldron Dump Materials complete.");
            FMLLog.info("To disable these dumps, set cauldron.dump-materials to false in bukkit.yml.");
        }
    }
    // Cauldron end

    public void setMetadata(final String metadataKey, final MetadataValue newMetadataValue) {
        chunk.getCraftWorld().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(final String metadataKey) {
        return chunk.getCraftWorld().getBlockMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(final String metadataKey) {
        return chunk.getCraftWorld().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(final String metadataKey, final Plugin owningPlugin) {
        chunk.getCraftWorld().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }
}
