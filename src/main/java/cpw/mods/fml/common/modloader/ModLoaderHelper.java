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

package cpw.mods.fml.common.modloader;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.src.TradeEntry;

import java.util.EnumSet;
import java.util.Map;

/**
 * @author cpw
 *
 */
@SuppressWarnings("deprecation")
public class ModLoaderHelper
{
    public static IModLoaderSidedHelper sidedHelper;

    private static Map<BaseModProxy, ModLoaderGuiHelper> guiHelpers = Maps.newHashMap();
    private static Map<Integer, ModLoaderGuiHelper> guiIDs = Maps.newHashMap();

    public static void updateStandardTicks(final BaseModProxy mod, final boolean enable, final boolean useClock)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().getReversedModObjectList().get(mod);
        if (mlmc==null)
        {
            mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        }
        if (mlmc == null)
        {
            FMLLog.severe("Attempted to register ModLoader ticking for invalid BaseMod %s",mod);
            return;
        }
        final BaseModTicker ticker = mlmc.getGameTickHandler();
        final EnumSet<TickType> ticks = ticker.ticks();
        // If we're enabled we get render ticks
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        } else {
            ticks.remove(TickType.RENDER);
        }
        // If we're enabled but we want clock ticks, or we're server side we get game ticks
        if (enable && (useClock || FMLCommonHandler.instance().getSide().isServer())) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }
    }

    public static void updateGUITicks(final BaseModProxy mod, final boolean enable, final boolean useClock)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().getReversedModObjectList().get(mod);
        if (mlmc==null)
        {
            mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        }
        if (mlmc == null)
        {
            FMLLog.severe("Attempted to register ModLoader ticking for invalid BaseMod %s",mod);
            return;
        }
        final EnumSet<TickType> ticks = mlmc.getGUITickHandler().ticks();
        // If we're enabled and we don't want clock ticks we get render ticks
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        } else {
            ticks.remove(TickType.RENDER);
        }
        // If we're enabled but we want clock ticks, or we're server side we get world ticks
        if (enable && useClock) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }
    }

    public static IPacketHandler buildPacketHandlerFor(final BaseModProxy mod)
    {
        return new ModLoaderPacketHandler(mod);
    }

    public static IWorldGenerator buildWorldGenHelper(final BaseModProxy mod)
    {
        return new ModLoaderWorldGenerator(mod);
    }

    public static IFuelHandler buildFuelHelper(final BaseModProxy mod)
    {
        return new ModLoaderFuelHelper(mod);
    }

    public static ICraftingHandler buildCraftingHelper(final BaseModProxy mod)
    {
        return new ModLoaderCraftingHelper(mod);
    }

    public static void finishModLoading(final ModLoaderModContainer mc)
    {
        if (sidedHelper != null)
        {
            sidedHelper.finishModLoading(mc);
        }
    }

    public static IConnectionHandler buildConnectionHelper(final BaseModProxy mod)
    {
        return new ModLoaderConnectionHandler(mod);
    }

    public static IPickupNotifier buildPickupHelper(final BaseModProxy mod)
    {
        return new ModLoaderPickupNotifier(mod);
    }

    public static void buildGuiHelper(final BaseModProxy mod, final int id)
    {
        ModLoaderGuiHelper handler = guiHelpers.get(mod);
        if (handler == null)
        {
            handler = new ModLoaderGuiHelper(mod);
            guiHelpers.put(mod,handler);
            NetworkRegistry.instance().registerGuiHandler(mod, handler);
        }
        handler.associateId(id);
        guiIDs.put(id, handler);
    }

    public static void openGui(final int id, final EntityPlayer player, final Container container, final int x, final int y, final int z)
    {
        final ModLoaderGuiHelper helper = guiIDs.get(id);
        helper.injectContainerAndID(container, id);
        player.openGui(helper.getMod(), id, player.worldObj, x, y, z);
    }

    public static Object getClientSideGui(final BaseModProxy mod, final EntityPlayer player, final int ID, final int x, final int y, final int z)
    {
        if (sidedHelper != null)
        {
            return sidedHelper.getClientGui(mod, player, ID, x, y, z);
        }
        return null;
    }

    public static void buildEntityTracker(final BaseModProxy mod, final Class<? extends Entity> entityClass, final int entityTypeId, final int updateRange, final int updateInterval,
                                          final boolean sendVelocityInfo)
    {
        final EntityRegistration er = EntityRegistry.registerModLoaderEntity(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
        er.setCustomSpawning(new ModLoaderEntitySpawnCallback(mod, er), EntityDragon.class.isAssignableFrom(entityClass) || IAnimals.class.isAssignableFrom(entityClass));
    }

    private static ModLoaderVillageTradeHandler[] tradeHelpers = new ModLoaderVillageTradeHandler[6];

    public static void registerTrade(final int profession, final TradeEntry entry)
    {
        assert profession < tradeHelpers.length : "The profession is out of bounds";
        if (tradeHelpers[profession] == null)
        {
            tradeHelpers[profession] = new ModLoaderVillageTradeHandler();
            VillagerRegistry.instance().registerVillageTradeHandler(profession, tradeHelpers[profession]);
        }

        tradeHelpers[profession].addTrade(entry);
    }

    public static void addCommand(final ICommand command)
    {
        final ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        if (mlmc!=null)
        {
            mlmc.addServerCommand(command);
        }
    }

    public static IChatListener buildChatListener(final BaseModProxy mod)
    {
        return new ModLoaderChatListener(mod);
    }
}
