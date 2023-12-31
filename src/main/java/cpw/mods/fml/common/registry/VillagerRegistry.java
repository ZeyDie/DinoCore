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
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureVillagePieceWeight;

import java.util.*;

/**
 * Registry for villager trading control
 *
 * @author cpw
 *
 */
public class VillagerRegistry
{
    private static final VillagerRegistry INSTANCE = new VillagerRegistry();

    private Multimap<Integer, IVillageTradeHandler> tradeHandlers = ArrayListMultimap.create();
    private Map<Class<?>, IVillageCreationHandler> villageCreationHandlers = Maps.newHashMap();
    private List<Integer> newVillagerIds = Lists.newArrayList();
    @SideOnly(Side.CLIENT)
    private Map<Integer, ResourceLocation> newVillagers;

    /**
     * Allow access to the {@link net.minecraft.world.gen.structure.StructureVillagePieces} array controlling new village
     * creation so you can insert your own new village pieces
     *
     * @author cpw
     *
     */
    public interface IVillageCreationHandler
    {
        /**
         * Called when {@link net.minecraft.world.gen.structure.MapGenVillage} is creating a new village
         *
         * @param random
         * @param i
         */
        StructureVillagePieceWeight getVillagePieceWeight(Random random, int i);

        /**
         * The class of the root structure component to add to the village
         */
        Class<?> getComponentClass();


        /**
         * Build an instance of the village component {@link net.minecraft.world.gen.structure.StructureVillagePieces}
         * @param villagePiece
         * @param startPiece
         * @param pieces
         * @param random
         * @param p1
         * @param p2
         * @param p3
         * @param p4
         * @param p5
         */
        Object buildComponent(StructureVillagePieceWeight villagePiece, ComponentVillageStartPiece startPiece, List pieces, Random random, int p1,
                int p2, int p3, int p4, int p5);
    }

    /**
     * Allow access to the {@link MerchantRecipeList} for a villager type for manipulation
     *
     * @author cpw
     *
     */
    public interface IVillageTradeHandler
    {
        /**
         * Called to allow changing the content of the {@link MerchantRecipeList} for the villager
         * supplied during creation
         *
         * @param villager
         * @param recipeList
         */
        void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random);
    }

    public static VillagerRegistry instance()
    {
        return INSTANCE;
    }

    /**
     * Register your villager id
     * @param id
     */
    public void registerVillagerId(final int id)
    {
        if (newVillagerIds.contains(id))
        {
            FMLLog.severe("Attempt to register duplicate villager id %d", id);
            throw new RuntimeException();
        }
        newVillagerIds.add(id);
    }
    /**
     * Register a new skin for a villager type
     *
     * @param villagerId
     * @param villagerSkin
     */
    @SideOnly(Side.CLIENT)
    public void registerVillagerSkin(final int villagerId, final ResourceLocation villagerSkin)
    {
        if (newVillagers == null)
        {
            newVillagers = Maps.newHashMap();
        }
        newVillagers.put(villagerId, villagerSkin);
    }

    /**
     * Register a new village creation handler
     *
     * @param handler
     */
    public void registerVillageCreationHandler(final IVillageCreationHandler handler)
    {
        villageCreationHandlers.put(handler.getComponentClass(), handler);
    }

    /**
     * Register a new villager trading handler for the specified villager type
     *
     * @param villagerId
     * @param handler
     */
    public void registerVillageTradeHandler(final int villagerId, final IVillageTradeHandler handler)
    {
        tradeHandlers.put(villagerId, handler);
    }

    /**
     * Callback to setup new villager types
     *
     * @param villagerType
     * @param defaultSkin
     */
    @SideOnly(Side.CLIENT)
    public static ResourceLocation getVillagerSkin(final int villagerType, final ResourceLocation defaultSkin)
    {
        if (instance().newVillagers != null && instance().newVillagers.containsKey(villagerType))
        {
            return instance().newVillagers.get(villagerType);
        }
        return defaultSkin;
    }

    /**
     * Returns a list of all added villager types
     *
     * @return newVillagerIds
     */
    public static Collection<Integer> getRegisteredVillagers()
    {
        return Collections.unmodifiableCollection(instance().newVillagerIds);
    }
    /**
     * Callback to handle trade setup for villagers
     *
     * @param recipeList
     * @param villager
     * @param villagerType
     * @param random
     */
    public static void manageVillagerTrades(final MerchantRecipeList recipeList, final EntityVillager villager, final int villagerType, final Random random)
    {
        for (final IVillageTradeHandler handler : instance().tradeHandlers.get(villagerType))
        {
            handler.manipulateTradesForVillager(villager, recipeList, random);
        }
    }

    public static void addExtraVillageComponents(final ArrayList components, final Random random, final int i)
    {
        final List<StructureVillagePieceWeight> parts = components;
        for (final IVillageCreationHandler handler : instance().villageCreationHandlers.values())
        {
            parts.add(handler.getVillagePieceWeight(random, i));
        }
    }

    public static Object getVillageComponent(final StructureVillagePieceWeight villagePiece, final ComponentVillageStartPiece startPiece, final List pieces, final Random random,
                                             final int p1, final int p2, final int p3, final int p4, final int p5)
    {
        return instance().villageCreationHandlers.get(villagePiece.villagePieceClass).buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, p4, p5);
    }


    public static void addEmeraldBuyRecipe(final EntityVillager villager, final MerchantRecipeList list, final Random random, final Item item, final float chance, final int min, final int max)
    {
        if (min > 0 && max > 0)
        {
            EntityVillager.villagerStockList.put(item.itemID, new Tuple(min, max));
        }
        villager.addMerchantItem(list, item.getMaxDamage(), random, chance);
    }

    public static void addEmeraldSellRecipe(final EntityVillager villager, final MerchantRecipeList list, final Random random, final Item item, final float chance, final int min, final int max)
    {
        if (min > 0 && max > 0)
        {
            EntityVillager.blacksmithSellingList.put(item.itemID, new Tuple(min, max));
        }
        villager.addBlacksmithItem(list, item.getMaxDamage(), random, chance);
    }

    public static void applyRandomTrade(final EntityVillager villager, final Random rand)
    {
        final int extra = instance().newVillagerIds.size();
        final int trade = rand.nextInt(5 + extra);
        villager.setProfession(trade < 5 ? trade : instance().newVillagerIds.get(trade - 5));
    }
}
