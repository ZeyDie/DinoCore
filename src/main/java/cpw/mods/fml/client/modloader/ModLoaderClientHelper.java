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

package cpw.mods.fml.client.modloader;

import com.google.common.base.Equivalence;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.google.common.collect.MapDifference.ValueDifference;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.modloader.BaseModProxy;
import cpw.mods.fml.common.modloader.IModLoaderSidedHelper;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.BaseMod;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class ModLoaderClientHelper implements IModLoaderSidedHelper
{
    public static int obtainBlockModelIdFor(final BaseMod mod, final boolean inventoryRenderer)
    {
        final int renderId=RenderingRegistry.getNextAvailableRenderId();
        final ModLoaderBlockRendererHandler bri=new ModLoaderBlockRendererHandler(renderId, inventoryRenderer, mod);
        RenderingRegistry.registerBlockHandler(bri);
        return renderId;
    }


    public static void handleFinishLoadingFor(final ModLoaderModContainer mc, final Minecraft game)
    {
        FMLLog.log(mc.getModId(), Level.FINE, "Handling post startup activities for ModLoader mod %s", mc.getModId());
        final BaseMod mod = (BaseMod) mc.getMod();

        final Map<Class<? extends Entity>, Render> renderers = Maps.newHashMap(RenderManager.instance.entityRenderMap);

        try
        {
            FMLLog.log(mc.getModId(), Level.FINEST, "Requesting renderers from basemod %s", mc.getModId());
            mod.addRenderer(renderers);
            FMLLog.log(mc.getModId(), Level.FINEST, "Received %d renderers from basemod %s", renderers.size(), mc.getModId());
        }
        catch (final Exception e)
        {
            FMLLog.log(mc.getModId(), Level.SEVERE, e, "A severe problem was detected with the mod %s during the addRenderer call. Continuing, but expect odd results", mc.getModId());
        }

        final MapDifference<Class<? extends Entity>, Render> difference = Maps.difference(RenderManager.instance.entityRenderMap, renderers, Equivalence.identity());

        for ( final Entry<Class<? extends Entity>, Render> e : difference.entriesOnlyOnLeft().entrySet())
        {
            FMLLog.log(mc.getModId(), Level.WARNING, "The mod %s attempted to remove an entity renderer %s from the entity map. This will be ignored.", mc.getModId(), e.getKey().getName());
        }

        for (final Entry<Class<? extends Entity>, Render> e : difference.entriesOnlyOnRight().entrySet())
        {
            FMLLog.log(mc.getModId(), Level.FINEST, "Registering ModLoader entity renderer %s as instance of %s", e.getKey().getName(), e.getValue().getClass().getName());
            RenderingRegistry.registerEntityRenderingHandler(e.getKey(), e.getValue());
        }

        for (final Entry<Class<? extends Entity>, ValueDifference<Render>> e : difference.entriesDiffering().entrySet())
        {
            FMLLog.log(mc.getModId(), Level.FINEST, "Registering ModLoader entity rendering override for %s as instance of %s", e.getKey().getName(), e.getValue().rightValue().getClass().getName());
            RenderingRegistry.registerEntityRenderingHandler(e.getKey(), e.getValue().rightValue());
        }

        try
        {
            mod.registerAnimation(game);
        }
        catch (final Exception e)
        {
            FMLLog.log(mc.getModId(), Level.SEVERE, e, "A severe problem was detected with the mod %s during the registerAnimation call. Continuing, but expect odd results", mc.getModId());
        }
    }

    public ModLoaderClientHelper(final Minecraft client)
    {
        this.client = client;
        ModLoaderHelper.sidedHelper = this;
        keyBindingContainers = Multimaps.newMultimap(Maps.<ModLoaderModContainer, Collection<ModLoaderKeyBindingHandler>>newHashMap(), new Supplier<Collection<ModLoaderKeyBindingHandler>>()
        {
            @Override
            public Collection<ModLoaderKeyBindingHandler> get()
            {
                return Collections.singleton(new ModLoaderKeyBindingHandler());
            }
        });
    }

    private Minecraft client;
    private static Multimap<ModLoaderModContainer, ModLoaderKeyBindingHandler> keyBindingContainers;

    @Override
    public void finishModLoading(final ModLoaderModContainer mc)
    {
        handleFinishLoadingFor(mc, client);
    }


    public static void registerKeyBinding(final BaseModProxy mod, final KeyBinding keyHandler, final boolean allowRepeat)
    {
        final ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        final ModLoaderKeyBindingHandler handler = Iterables.getOnlyElement(keyBindingContainers.get(mlmc));
        handler.setModContainer(mlmc);
        handler.addKeyBinding(keyHandler, allowRepeat);
        KeyBindingRegistry.registerKeyBinding(handler);
    }


    @Override
    public Object getClientGui(final BaseModProxy mod, final EntityPlayer player, final int ID, final int x, final int y, final int z)
    {
        return ((net.minecraft.src.BaseMod)mod).getContainerGUI((EntityClientPlayerMP) player, ID, x, y, z);
    }


    @Override
    public Entity spawnEntity(final BaseModProxy mod, final EntitySpawnPacket input, final EntityRegistration er)
    {
        return ((net.minecraft.src.BaseMod)mod).spawnEntity(er.getModEntityId(), client.theWorld, input.scaledX, input.scaledY, input.scaledZ);
    }


    @Override
    public void sendClientPacket(final BaseModProxy mod, final Packet250CustomPayload packet)
    {
        ((net.minecraft.src.BaseMod)mod).clientCustomPayload(client.thePlayer.sendQueue, packet);
    }

    private Map<INetworkManager,NetHandler> managerLookups = new MapMaker().weakKeys().weakValues().makeMap();
    @Override
    public void clientConnectionOpened(final NetHandler netClientHandler, final INetworkManager manager, final BaseModProxy mod)
    {
        managerLookups.put(manager, netClientHandler);
        ((BaseMod)mod).clientConnect((NetClientHandler)netClientHandler);
    }


    @Override
    public boolean clientConnectionClosed(final INetworkManager manager, final BaseModProxy mod)
    {
        if (managerLookups.containsKey(manager))
        {
            ((BaseMod)mod).clientDisconnect((NetClientHandler) managerLookups.get(manager));
            return true;
        }
        return false;
    }
}
