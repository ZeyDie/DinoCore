/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.*;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

// Cauldron start
// Cauldron end

public class GameRegistry
{
    private static Multimap<ModContainer, BlockProxy> blockRegistry = ArrayListMultimap.create();
    private static Set<IWorldGenerator> worldGenerators = Sets.newHashSet();
    private static List<IFuelHandler> fuelHandlers = Lists.newArrayList();
    private static List<ICraftingHandler> craftingHandlers = Lists.newArrayList();
    private static List<IPickupNotifier> pickupHandlers = Lists.newArrayList();
    private static List<IPlayerTracker> playerTrackers = Lists.newArrayList();
    // Cauldron start
    private static Map<String, Boolean> configWorldGenCache = new HashMap<String, Boolean>();
    private static Map<String, String> worldGenMap = new HashMap<String, String>();
    // Cauldron end

    /**
     * Register a world generator - something that inserts new block types into the world
     *
     * @param generator
     */
    public static void registerWorldGenerator(final IWorldGenerator generator)
    {
        // Cauldron start - mod id's are not available during generateWorld so we must capture them here
        String modId = Loader.instance().activeModContainer().getModId();
        modId = modId.replaceAll("[^A-Za-z0-9]", ""); // remove all non-digits/alphanumeric
        modId.replace(" ", "_");
        worldGenerators.add(generator);
        worldGenMap.put(generator.getClass().getName(), modId);
        // Cauldron end
    }

    /**
     * Callback hook for world gen - if your mod wishes to add extra mod related generation to the world
     * call this
     *
     * @param chunkX
     * @param chunkZ
     * @param world
     * @param chunkGenerator
     * @param chunkProvider
     */
    public static void generateWorld(final int chunkX, final int chunkZ, final World world, final IChunkProvider chunkGenerator, final IChunkProvider chunkProvider)
    {
        final long worldSeed = world.getSeed();
        final Random fmlRandom = new Random(worldSeed);
        final long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        final long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        final long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed;

        // Cauldron start
        boolean before = true;
        if (world instanceof net.minecraft.world.WorldServer) // ignore fake worlds
        {
            before = ((net.minecraft.world.WorldServer)world).theChunkProviderServer.loadChunkOnProvideRequest; // store value
            ((net.minecraft.world.WorldServer)world).theChunkProviderServer.loadChunkOnProvideRequest = true; // load chunks on provide requests
        }
        for (final IWorldGenerator generator : worldGenerators)
        {
            if (!configWorldGenCache.containsKey(generator.getClass().getName()))
            {
                final String modId = worldGenMap.get(generator.getClass().getName());
                String generatorName = "";
                generatorName = modId + "-" + generator.getClass().getSimpleName();
                final boolean generatorEnabled = world.cauldronConfig.getBoolean("worldgen-" + generatorName, true);
                configWorldGenCache.put(generator.getClass().getName(), generatorEnabled);
            }
            if (configWorldGenCache.get(generator.getClass().getName()))
            {
                fmlRandom.setSeed(chunkSeed);
                generator.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            }
        }

        if (world instanceof net.minecraft.world.WorldServer) // ignore fake worlds
        {
            ((net.minecraft.world.WorldServer)world).theChunkProviderServer.loadChunkOnProvideRequest = before; // reset
        }
        // Cauldron end
    }

    /**
     * Internal method for creating an @Block instance
     * @param container
     * @param type
     * @param annotation
     * @throws Exception
     */
    public static Object buildBlock(final ModContainer container, final Class<?> type, final Block annotation) throws Exception
    {
        final Object o = type.getConstructor(int.class).newInstance(findSpareBlockId());
        registerBlock((net.minecraft.block.Block) o);
        return o;
    }

    /**
     * Private and not yet working properly
     *
     * @return a block id
     */
    private static int findSpareBlockId()
    {
        return BlockTracker.nextBlockId();
    }

    /**
     * Register an item with the item registry with a custom name : this allows for easier server->client resolution
     *
     * @param item The item to register
     * @param name The mod-unique name of the item
     */
    public static void registerItem(final net.minecraft.item.Item item, final String name)
    {
        registerItem(item, name, null);
    }

    /**
     * Register the specified Item with a mod specific name : overrides the standard type based name
     * @param item The item to register
     * @param name The mod-unique name to register it as - null will remove a custom name
     * @param modId An optional modId that will "own" this block - generally used by multi-mod systems
     * where one mod should "own" all the blocks of all the mods, null defaults to the active mod
     */
    public static void registerItem(final net.minecraft.item.Item item, final String name, final String modId)
    {
        GameRegistry.registerMaterial(item, name, modId); // Cauldron - register bukkit material
        GameData.setName(item, name, modId);
    }

    /**
     * Register a block with the world
     *
     */
    @Deprecated
    public static void registerBlock(final net.minecraft.block.Block block)
    {
        registerBlock(block, ItemBlock.class);
    }


    /**
     * Register a block with the specified mod specific name : overrides the standard type based name
     * @param block The block to register
     * @param name The mod-unique name to register it as
     */
    public static void registerBlock(final net.minecraft.block.Block block, final String name)
    {
        registerBlock(block, ItemBlock.class, name);
    }

    /**
     * Register a block with the world, with the specified item class
     *
     * Deprecated in favour of named versions
     *
     * @param block The block to register
     * @param itemclass The item type to register with it
     */
    @Deprecated
    public static void registerBlock(final net.minecraft.block.Block block, final Class<? extends ItemBlock> itemclass)
    {
        registerBlock(block, itemclass, null);
    }
    /**
     * Register a block with the world, with the specified item class and block name
     * @param block The block to register
     * @param itemclass The item type to register with it
     * @param name The mod-unique name to register it with
     */
    public static void registerBlock(final net.minecraft.block.Block block, final Class<? extends ItemBlock> itemclass, final String name)
    {
        registerBlock(block, itemclass, name, null);
    }
    /**
     * Register a block with the world, with the specified item class, block name and owning modId
     * @param block The block to register
     * @param itemclass The iterm type to register with it
     * @param name The mod-unique name to register it with
     * @param modId The modId that will own the block name. null defaults to the active modId
     */
    public static void registerBlock(final net.minecraft.block.Block block, final Class<? extends ItemBlock> itemclass, final String name, final String modId)
    {
        if (Loader.instance().isInState(LoaderState.CONSTRUCTING))
        {
            FMLLog.warning("The mod %s is attempting to register a block whilst it it being constructed. This is bad modding practice - please use a proper mod lifecycle event.", Loader.instance().activeModContainer());
        }
        try
        {
            assert block != null : "registerBlock: block cannot be null";
            assert itemclass != null : "registerBlock: itemclass cannot be null";
            final int blockItemId = block.blockID - 256;
            Constructor<? extends ItemBlock> itemCtor;
            Item i;
            try
            {
                itemCtor = itemclass.getConstructor(int.class);
                i = itemCtor.newInstance(blockItemId);
            }
            catch (final NoSuchMethodException e)
            {
                itemCtor = itemclass.getConstructor(int.class, net.minecraft.block.Block.class);
                i = itemCtor.newInstance(blockItemId, block);
            }
            GameRegistry.registerItem(i,name, modId);
        }
        catch (final Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Caught an exception during block registration");
            throw new LoaderException(e);
        }
        blockRegistry.put(Loader.instance().activeModContainer(), (BlockProxy) block);
    }

    // Cauldron start - register bukkit material names for modded items/blocks
    /**
     * Register the specified Material with a mod specific name : overrides the standard type XID name
     * @param item The material to register
     * @param name The material-unique name to register it as - null will default to modId_itemId
     * @param modId An optional modId that will "own" this block - generally used by multi-mod systems
     * where one mod should "own" all the blocks of all the mods, null defaults to the active mod
     */
    public static void registerMaterial(final net.minecraft.item.Item item, final String name, String modId)
    {
        String modId1 = modId;
        if (name != null)
        {
            if (modId1 == null)
                modId1 = Loader.instance().activeModContainer().getModId();
            final String materialName = modId1 + "_" + name;
            org.bukkit.Material.setMaterialName(item.itemID, materialName, false);
        }
        else
        {
            if (modId1 == null)
                modId1 = Loader.instance().activeModContainer().getModId();
            final String materialName = modId1 + "_" + String.valueOf(item.itemID);
            org.bukkit.Material.setMaterialName(item.itemID, materialName, false);
        }
    }
    // Cauldron end

    public static void addRecipe(final ItemStack output, final Object... params)
    {
        addShapedRecipe(output, params);
    }

    public static IRecipe addShapedRecipe(final ItemStack output, final Object... params)
    {
        return CraftingManager.getInstance().addRecipe(output, params);
    }

    public static void addShapelessRecipe(final ItemStack output, final Object... params)
    {
        CraftingManager.getInstance().addShapelessRecipe(output, params);
    }

    public static void addRecipe(final IRecipe recipe)
    {
        CraftingManager.getInstance().getRecipeList().add(recipe);
    }

    public static void addSmelting(final int input, final ItemStack output, final float xp)
    {
        FurnaceRecipes.smelting().addSmelting(input, output, xp);
    }

    public static void registerTileEntity(final Class<? extends TileEntity> tileEntityClass, final String id)
    {
        TileEntity.addMapping(tileEntityClass, id);
    }

    /**
     * Register a tile entity, with alternative TileEntity identifiers. Use with caution!
     * This method allows for you to "rename" the 'id' of the tile entity.
     *
     * @param tileEntityClass The tileEntity class to register
     * @param id The primary ID, this will be the ID that the tileentity saves as
     * @param alternatives A list of alternative IDs that will also map to this class. These will never save, but they will load
     */
    public static void registerTileEntityWithAlternatives(final Class<? extends TileEntity> tileEntityClass, final String id, final String... alternatives)
    {
        TileEntity.addMapping(tileEntityClass, id);
        final Map<String,Class> teMappings = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "70326_a", "nameToClassMap", "a");
        for (final String s: alternatives)
        {
            if (!teMappings.containsKey(s))
            {
                teMappings.put(s, tileEntityClass);
            }
        }
    }

    public static void addBiome(final BiomeGenBase biome)
    {
        WorldType.DEFAULT.addNewBiome(biome);
    }

    public static void removeBiome(final BiomeGenBase biome)
    {
        WorldType.DEFAULT.removeBiome(biome);
    }

    public static void registerFuelHandler(final IFuelHandler handler)
    {
        fuelHandlers.add(handler);
    }
    public static int getFuelValue(final ItemStack itemStack)
    {
        int fuelValue = 0;
        for (final IFuelHandler handler : fuelHandlers)
        {
            fuelValue = Math.max(fuelValue, handler.getBurnTime(itemStack));
        }
        return fuelValue;
    }

    public static void registerCraftingHandler(final ICraftingHandler handler)
    {
        craftingHandlers.add(handler);
    }

    public static void onItemCrafted(final EntityPlayer player, final ItemStack item, final IInventory craftMatrix)
    {
        for (final ICraftingHandler handler : craftingHandlers)
        {
            handler.onCrafting(player, item, craftMatrix);
        }
    }

    public static void onItemSmelted(final EntityPlayer player, final ItemStack item)
    {
        for (final ICraftingHandler handler : craftingHandlers)
        {
            handler.onSmelting(player, item);
        }
    }

    public static void registerPickupHandler(final IPickupNotifier handler)
    {
        pickupHandlers.add(handler);
    }

    public static void onPickupNotification(final EntityPlayer player, final EntityItem item)
    {
        for (final IPickupNotifier notify : pickupHandlers)
        {
            notify.notifyPickup(item, player);
        }
    }

    public static void registerPlayerTracker(final IPlayerTracker tracker)
	{
		playerTrackers.add(tracker);
	}

	public static void onPlayerLogin(final EntityPlayer player)
	{
        for (final IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerLogin(player);
            }
            catch (final Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerLogin event with player tracker %s", tracker.getClass().getName());
            }
	}

	public static void onPlayerLogout(final EntityPlayer player)
	{
        for (final IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerLogout(player);
            }
            catch (final Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerLogout event with player tracker %s", tracker.getClass().getName());
            }
	}

    // Cauldron start - wrapper for mods to call our new method
    public static void onPlayerChangedDimension(final EntityPlayer player)
    {
        onPlayerChangedDimension(player, player.worldObj.getWorld()); // use same world for fromWorld as this is the best we can do
    }
    // Cauldron end

    public static void onPlayerChangedDimension(final EntityPlayer player, final CraftWorld fromWorld)
    {
        // Cauldron start - needed for mods that do not use ServerConfigurationManager. This allows us to notify plugins that a player changed dimensions
        final PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player) player.getBukkitEntity(), fromWorld);
        Bukkit.getServer().getPluginManager().callEvent(event);
        // Cauldron end
        for (final IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerChangedDimension(player);
                // Cauldron start - update compassTarget to new world when changing dimensions or it will leave a reference to the last world object causing a memory leak
                // This is required for mods that implement their own dimension transfer methods which bypass ServerConfigurationManager
                final EntityPlayerMP playermp = (EntityPlayerMP)player;
                playermp.compassTarget = new Location(playermp.worldObj.getWorld(), playermp.posX, playermp.posY, playermp.posZ);
                // Cauldron end
            }
            catch (final Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerChangedDimension event with player tracker %s", tracker.getClass()
                        .getName());
            }
	}

	public static void onPlayerRespawn(final EntityPlayer player)
	{
        for (final IPlayerTracker tracker : playerTrackers)
            try
            {
                tracker.onPlayerRespawn(player);
            }
            catch (final Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "A critical error occured handling the onPlayerRespawn event with player tracker %s", tracker.getClass().getName());
            }
	}


	/**
	 * Look up a mod block in the global "named item list"
	 * @param modId The modid owning the block
	 * @param name The name of the block itself
	 * @return The block or null if not found
	 */
	public static net.minecraft.block.Block findBlock(final String modId, final String name)
	{
	    return GameData.findBlock(modId, name);
	}

	/**
	 * Look up a mod item in the global "named item list"
	 * @param modId The modid owning the item
	 * @param name The name of the item itself
	 * @return The item or null if not found
	 */
	public static net.minecraft.item.Item findItem(final String modId, final String name)
    {
        return GameData.findItem(modId, name);
    }

	/**
	 * Manually register a custom item stack with FML for later tracking. It is automatically scoped with the active modid
	 *
	 * @param name The name to register it under
	 * @param itemStack The itemstack to register
	 */
	public static void registerCustomItemStack(final String name, final ItemStack itemStack)
	{
	    GameData.registerCustomItemStack(name, itemStack);
	}
	/**
	 * Lookup an itemstack based on mod and name. It will create "default" itemstacks from blocks and items if no
	 * explicit itemstack is found.
	 *
	 * If it is built from a block, the metadata is by default the "wildcard" value.
	 *
	 * Custom itemstacks can be dumped from minecraft by setting the system property fml.dumpRegistry to true
	 * (-Dfml.dumpRegistry=true on the command line will work)
	 *
	 * @param modId The modid of the stack owner
	 * @param name The name of the stack
	 * @param stackSize The size of the stack returned
	 * @return The custom itemstack or null if no such itemstack was found
	 */
	public static ItemStack findItemStack(final String modId, final String name, final int stackSize)
	{
	    final ItemStack foundStack = GameData.findItemStack(modId, name);
	    if (foundStack != null)
	    {
            final ItemStack is = foundStack.copy();
    	    is.stackSize = Math.min(stackSize, is.getMaxStackSize());
    	    return is;
	    }
	    return null;
	}

	public static class UniqueIdentifier
	{
	    public final String modId;
	    public final String name;
        UniqueIdentifier(final String modId, final String name)
        {
            this.modId = modId;
            this.name = name;
        }
	}

	/**
	 * Look up the mod identifier data for a block.
	 * Returns null if there is no mod specified mod identifier data, or it is part of a
	 * custom itemstack definition {@link #registerCustomItemStack}
	 *
	 * Note: uniqueness and persistence is only guaranteed by mods using the game registry
	 * correctly.
	 *
	 * @param block to lookup
     * @return a {@link UniqueIdentifier} for the block or null
	 */
	public static UniqueIdentifier findUniqueIdentifierFor(final net.minecraft.block.Block block)
	{
	    return GameData.getUniqueName(block);
	}
    /**
     * Look up the mod identifier data for an item.
     * Returns null if there is no mod specified mod identifier data, or it is part of a
     * custom itemstack definition {@link #registerCustomItemStack}
     *
     * Note: uniqueness and persistence is only guaranteed by mods using the game registry
     * correctly.
     *
     * @param item to lookup
     * @return a {@link UniqueIdentifier} for the item or null
     */
    public static UniqueIdentifier findUniqueIdentifierFor(final net.minecraft.item.Item item)
    {
        return GameData.getUniqueName(item);
    }
}
