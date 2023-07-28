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

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Throwables;
import com.google.common.collect.*;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Table.Cell;
import com.google.common.io.Files;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public class GameData {
    private static Map<Integer, ItemData> idMap = Maps.newHashMap();
    private static CountDownLatch serverValidationLatch;
    private static CountDownLatch clientValidationLatch;
    private static MapDifference<Integer, ItemData> difference;
    private static boolean shouldContinue = true;
    private static boolean isSaveValid = true;
    private static ImmutableTable<String, String, Integer> modObjectTable;
    private static Table<String, String, ItemStack> customItemStacks = HashBasedTable.create();
    private static Map<String,String> ignoredMods;
    private static boolean validated;

    private static boolean isModIgnoredForIdValidation(final String modId)
    {
        if (ignoredMods == null)
        {
            final File f = new File(Loader.instance().getConfigDir(),"fmlIDChecking.properties");
            if (f.exists())
            {
                final Properties p = new Properties();
                try
                {
                    p.load(new FileInputStream(f));
                    ignoredMods = Maps.fromProperties(p);
                    if (!ignoredMods.isEmpty())
                    {
                        FMLLog.log("fml.ItemTracker", Level.WARNING, "Using non-empty ignored mods configuration file %s", ignoredMods.keySet());
                    }
                }
                catch (final Exception e)
                {
                    Throwables.propagateIfPossible(e);
                    FMLLog.log("fml.ItemTracker", Level.SEVERE, e, "Failed to read ignored ID checker mods properties file");
                    ignoredMods = ImmutableMap.<String, String>of();
                }
            }
            else
            {
                ignoredMods = ImmutableMap.<String, String>of();
            }
        }
        return ignoredMods.containsKey(modId);
    }

    public static void newItemAdded(final Item item)
    {
        ModContainer mc = Loader.instance().activeModContainer();
        if (mc == null)
        {
            mc = Loader.instance().getMinecraftModContainer();
            if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION) || validated)
            {
                FMLLog.severe("It appears something has tried to allocate an Item or Block outside of the preinitialization phase for mods. This will NOT work in 1.7 and beyond!");
            }
        }
        final String itemType = item.getClass().getName();
        final ItemData itemData = new ItemData(item, mc);
        if (idMap.containsKey(item.itemID))
        {
            final ItemData id = idMap.get(item.itemID);
            FMLLog.log("fml.ItemTracker", Level.INFO, "The mod %s is overwriting existing item at %d (%s from %s) with %s", mc.getModId(), id.getItemId(), id.getItemType(), id.getModId(), itemType);
        }
        idMap.put(item.itemID, itemData);
        if (!"Minecraft".equals(mc.getModId()))
        {
            FMLLog.log("fml.ItemTracker",Level.FINE, "Adding item %s(%d) owned by %s", item.getClass().getName(), item.itemID, mc.getModId());
        }
    }

    public static void validateWorldSave(final Set<ItemData> worldSaveItems)
    {
        isSaveValid = true;
        shouldContinue = true;
        // allow ourselves to continue if there's no saved data
        if (worldSaveItems == null)
        {
            serverValidationLatch.countDown();
            try
            {
                clientValidationLatch.await();
            }
            catch (final InterruptedException e)
            {
            }
            return;
        }

        final Function<? super ItemData, Integer> idMapFunction = new Function<ItemData, Integer>() {
            public Integer apply(final ItemData input) {
                return input.getItemId();
            };
        };

        final Map<Integer,ItemData> worldMap = Maps.uniqueIndex(worldSaveItems,idMapFunction);
        difference = Maps.difference(worldMap, idMap);
        FMLLog.log("fml.ItemTracker", Level.FINE, "The difference set is %s", difference);
        if (!difference.entriesDiffering().isEmpty() || !difference.entriesOnlyOnLeft().isEmpty())
        {
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "FML has detected item discrepancies");
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "Missing items : %s", difference.entriesOnlyOnLeft());
            FMLLog.log("fml.ItemTracker", Level.SEVERE, "Mismatched items : %s", difference.entriesDiffering());
            boolean foundNonIgnored = false;
            for (final ItemData diff : difference.entriesOnlyOnLeft().values())
            {
                if (!isModIgnoredForIdValidation(diff.getModId()))
                {
                    foundNonIgnored = true;
                }
            }
            for (final ValueDifference<ItemData> diff : difference.entriesDiffering().values())
            {
                if (! ( isModIgnoredForIdValidation(diff.leftValue().getModId()) || isModIgnoredForIdValidation(diff.rightValue().getModId()) ) )
                {
                    foundNonIgnored = true;
                }
            }
            if (!foundNonIgnored)
            {
                FMLLog.log("fml.ItemTracker", Level.SEVERE, "FML is ignoring these ID discrepancies because of configuration. YOUR GAME WILL NOW PROBABLY CRASH. HOPEFULLY YOU WON'T HAVE CORRUPTED YOUR WORLD. BLAME %s", ignoredMods.keySet());
            }
            isSaveValid = !foundNonIgnored;
            serverValidationLatch.countDown();
        }
        else
        {
            isSaveValid = true;
            serverValidationLatch.countDown();
        }
        try
        {
            clientValidationLatch.await();
            if (!shouldContinue)
            {
                throw new RuntimeException("This server instance is going to stop abnormally because of a fatal ID mismatch");
            }
        }
        catch (final InterruptedException e)
        {
        }
    }

    public static void writeItemData(final NBTTagList itemList)
    {
        for (final ItemData dat : idMap.values())
        {
            itemList.appendTag(dat.toNBT());
        }
    }

    /**
     * Initialize the server gate
     * @param gateCount the countdown amount. If it's 2 we're on the client and the client and server
     * will wait at the latch. 1 is a server and the server will proceed
     */
    public static void initializeServerGate(final int gateCount)
    {
        serverValidationLatch = new CountDownLatch(gateCount - 1);
        clientValidationLatch = new CountDownLatch(gateCount - 1);
    }

    public static MapDifference<Integer, ItemData> gateWorldLoadingForValidation()
    {
        try
        {
            serverValidationLatch.await();
            if (!isSaveValid)
            {
                return difference;
            }
        }
        catch (final InterruptedException e)
        {
        }
        difference = null;
        return null;
    }


    public static void releaseGate(final boolean carryOn)
    {
        shouldContinue = carryOn;
        clientValidationLatch.countDown();
    }

    public static Set<ItemData> buildWorldItemData(final NBTTagList modList)
    {
        final Set<ItemData> worldSaveItems = Sets.newHashSet();
        for (int i = 0; i < modList.tagCount(); i++)
        {
            final NBTTagCompound mod = (NBTTagCompound) modList.tagAt(i);
            final ItemData dat = new ItemData(mod);
            worldSaveItems.add(dat);
        }
        return worldSaveItems;
    }

    static void setName(final Item item, final String name, final String modId)
    {
        final int id = item.itemID;
        final ItemData itemData = idMap.get(id);
        itemData.setName(name,modId);
    }

    public static void buildModObjectTable()
    {
        if (modObjectTable != null)
        {
            throw new IllegalStateException("Illegal call to buildModObjectTable!");
        }

        final Map<Integer, Cell<String, String, Integer>> map = Maps.transformValues(idMap, new Function<ItemData,Cell<String,String,Integer>>() {
            public Cell<String,String,Integer> apply(final ItemData data)
            {
                if ("Minecraft".equals(data.getModId()) || !data.isOveridden())
                {
                    return null;
                }
                return Tables.immutableCell(data.getModId(), data.getItemType(), data.getItemId());
            }
        });

        final Builder<String, String, Integer> tBuilder = ImmutableTable.builder();
        for (final Cell<String, String, Integer> c : map.values())
        {
            if (c!=null)
            {
                tBuilder.put(c);
            }
        }
        modObjectTable = tBuilder.build();
    }
    static Item findItem(final String modId, final String name)
    {
        if (modObjectTable == null || !modObjectTable.contains(modId, name))
        {
            return null;
        }

        return Item.itemsList[modObjectTable.get(modId, name)];
    }

    static Block findBlock(final String modId, final String name)
    {
        if (modObjectTable == null)
        {
            return null;
        }

        final Integer blockId = modObjectTable.get(modId, name);
        if (blockId == null || blockId >= Block.blocksList.length)
        {
            return null;
        }
        return Block.blocksList[blockId];
    }

    static ItemStack findItemStack(final String modId, final String name)
    {
        ItemStack is = customItemStacks.get(modId, name);
        if (is == null)
        {
            final Item i = findItem(modId, name);
            if (i != null)
            {
                is = new ItemStack(i, 0 ,0);
            }
        }
        if (is == null)
        {
            final Block b = findBlock(modId, name);
            if (b != null)
            {
                is = new ItemStack(b, 0, Short.MAX_VALUE);
            }
        }
        return is;
    }

    static void registerCustomItemStack(final String name, final ItemStack itemStack)
    {
        customItemStacks.put(Loader.instance().activeModContainer().getModId(), name, itemStack);
    }

    public static void dumpRegistry(final File minecraftDir)
    {
        if (customItemStacks == null)
        {
            return;
        }
        if (Boolean.valueOf(System.getProperty("fml.dumpRegistry", "false")).booleanValue())
        {
            final ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
            for (final String modId : customItemStacks.rowKeySet())
            {
                builder.putAll(modId, customItemStacks.row(modId).keySet());
            }

            final File f = new File(minecraftDir, "itemStackRegistry.csv");
            final MapJoiner mapJoiner = Joiner.on("\n").withKeyValueSeparator(",");
            try
            {
                Files.write(mapJoiner.join(builder.build().entries()), f, Charsets.UTF_8);
                FMLLog.log(Level.INFO, "Dumped item registry data to %s", f.getAbsolutePath());
            }
            catch (final IOException e)
            {
                FMLLog.log(Level.SEVERE, e, "Failed to write registry data to %s", f.getAbsolutePath());
            }
        }
    }

    static UniqueIdentifier getUniqueName(final Block block)
    {
        if (block == null) return null;
        final ItemData itemData = idMap.get(block.blockID);
        if (itemData == null || !itemData.isOveridden() || customItemStacks.contains(itemData.getModId(), itemData.getItemType()))
        {
            return null;
        }

        return new UniqueIdentifier(itemData.getModId(), itemData.getItemType());
    }

    static UniqueIdentifier getUniqueName(final Item item)
    {
        if (item == null) return null;
        final ItemData itemData = idMap.get(item.itemID);
        if (itemData == null || !itemData.isOveridden() || customItemStacks.contains(itemData.getModId(), itemData.getItemType()))
        {
            return null;
        }

        return new UniqueIdentifier(itemData.getModId(), itemData.getItemType());
    }

    public static void validateRegistry()
    {
        for (int i = 0; i < Item.itemsList.length; i++)
        {
            if (Item.itemsList[i] != null)
            {
                final ItemData itemData = idMap.get(i);
                if (itemData == null)
                {
                    FMLLog.severe("Found completely unknown item of class %s with ID %d, this will NOT work for a 1.7 upgrade", Item.itemsList[i].getClass().getName(), i);
                }
                else if (!itemData.isOveridden() && !"Minecraft".equals(itemData.getModId()))
                {
                    FMLLog.severe("Found anonymous item of class %s with ID %d owned by mod %s, this item will NOT survive a 1.7 upgrade!", Item.itemsList[i].getClass().getName(), i, itemData.getModId());
                }
            }
        }
        validated = true;
    }
}
