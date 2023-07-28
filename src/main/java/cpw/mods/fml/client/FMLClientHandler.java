/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.client;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.ReloadableResourceManager;
import net.minecraft.client.resources.ResourcePack;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #beginMinecraftLoading(Minecraft, List)} called from
 * {@link Minecraft}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLClientHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();

    /**
     * A reference to the server itself
     */
    private Minecraft client;

    private DummyModContainer optifineContainer;

    private boolean guiLoaded;

    private boolean serverIsRunning;

    private MissingModsException modsMissing;

    private ModSortingException modSorting;

    private boolean loading = true;

    private WrongMinecraftVersionException wrongMC;

    private CustomModLoadingErrorDisplayException customError;

	private DuplicateModsFoundException dupesFound;

    private boolean serverShouldBeKilledQuietly;

    private List<ResourcePack> resourcePackList;

    private ReloadableResourceManager resourceManager;

    private Map<String, ResourcePack> resourcePackMap;

    /**
     * Called to start the whole game off
     *
     * @param minecraft The minecraft instance being launched
     * @param resourcePackList The resource pack list we will populate with mods
     * @param resourceManager The resource manager
     */
    public void beginMinecraftLoading(final Minecraft minecraft, final List resourcePackList, final ReloadableResourceManager resourceManager)
    {
        client = minecraft;
        this.resourcePackList = resourcePackList;
        this.resourceManager = resourceManager;
        this.resourcePackMap = Maps.newHashMap();
        if (minecraft.isDemo())
        {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.");
            haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }

//        TextureFXManager.instance().setClient(client);
        FMLCommonHandler.instance().beginLoading(this);
        new ModLoaderClientHelper(client);
        try
        {
            final Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            final String optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
            final Map<String,Object> dummyOptifineMeta = ImmutableMap.<String,Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
            final ModMetadata optifineMetadata = MetadataCollection.from(getClass().getResourceAsStream("optifinemod.info"),"optifine").getMetadataForId("optifine", dummyOptifineMeta);
            optifineContainer = new DummyModContainer(optifineMetadata);
            FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features",optifineContainer.getVersion());
        }
        catch (final Exception e)
        {
            optifineContainer = null;
        }
        try
        {
            Loader.instance().loadMods();
        }
        catch (final WrongMinecraftVersionException wrong)
        {
            wrongMC = wrong;
        }
        catch (final DuplicateModsFoundException dupes)
        {
        	dupesFound = dupes;
        }
        catch (final MissingModsException missing)
        {
            modsMissing = missing;
        }
        catch (final ModSortingException sorting)
        {
            modSorting = sorting;
        }
        catch (final CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
        }
        catch (final LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }

        Map<String,Map<String,String>> sharedModList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");
        if (sharedModList == null)
        {
            sharedModList = Maps.newHashMap();
            Launch.blackboard.put("modList", sharedModList);
        }
        for (final ModContainer mc : Loader.instance().getActiveModList())
        {
            final Map<String,String> sharedModDescriptor = mc.getSharedModDescriptor();
            if (sharedModDescriptor != null)
            {
                final String sharedModId = "fml:"+mc.getModId();
                sharedModList.put(sharedModId, sharedModDescriptor);
            }
        }
    }

    @Override
    public void haltGame(final String message, final Throwable t)
    {
        client.displayCrashReport(new CrashReport(message, t));
        throw Throwables.propagate(t);
    }
    /**
     * Called a bit later on during initialization to finish loading mods
     * Also initializes key bindings
     *
     */
    @SuppressWarnings("deprecation")
    public void finishMinecraftLoading()
    {
        if (modsMissing != null || wrongMC != null || customError!=null || dupesFound!=null || modSorting!=null)
        {
            return;
        }
        try
        {
            Loader.instance().initializeMods();
        }
        catch (final CustomModLoadingErrorDisplayException custom)
        {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt");
            customError = custom;
            return;
        }
        catch (final LoaderException le)
        {
            haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        
        client.sndManager.LOAD_SOUND_SYSTEM = true;
        // Reload resources
        client.refreshResources();
        RenderingRegistry.instance().loadEntityRenderers((Map<Class<? extends Entity>, Render>)RenderManager.instance.entityRenderMap);

        loading = false;
        KeyBindingRegistry.instance().uploadKeyBindingsToGame(client.gameSettings);
    }

    public void extendModList()
    {
        final Map<String,Map<String,String>> modList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");
        if (modList != null)
        {
            for (final Entry<String, Map<String, String>> modEntry : modList.entrySet())
            {
                final String sharedModId = modEntry.getKey();
                final String system = sharedModId.split(":")[0];
                if ("fml".equals(system))
                {
                    continue;
                }
                final Map<String, String> mod = modEntry.getValue();
                final String modSystem = mod.get("modsystem"); // the modsystem (FML uses FML or ModLoader)
                final String modId = mod.get("id"); // unique ID
                final String modVersion = mod.get("version"); // version
                final String modName = mod.get("name"); // a human readable name
                final String modURL = mod.get("url"); // a URL for the mod (can be empty string)
                final String modAuthors = mod.get("authors"); // a csv of authors (can be empty string)
                final String modDescription = mod.get("description"); // a (potentially) multiline description (can be empty string)
            }
        }

    }
    public void onInitializationComplete()
    {
        if (wrongMC != null)
        {
            client.displayGuiScreen(new GuiWrongMinecraft(wrongMC));
        }
        else if (modsMissing != null)
        {
            client.displayGuiScreen(new GuiModsMissing(modsMissing));
        }
        else if (dupesFound != null)
        {
        	client.displayGuiScreen(new GuiDupesFound(dupesFound));
        }
        else if (modSorting != null)
        {
            client.displayGuiScreen(new GuiSortingProblem(modSorting));
        }
		else if (customError != null)
        {
            client.displayGuiScreen(new GuiCustomModLoadingErrorScreen(customError));
        }
        else
        {
            // Force renderengine to reload and re-initialize all textures
//            client.renderEngine.func_78352_b();
//            TextureFXManager.instance().loadTextures(client.field_71418_C.func_77292_e());
        }
    }
    /**
     * Get the server instance
     */
    public Minecraft getClient()
    {
        return client;
    }

    /**
     * Get a handle to the client's logger instance
     * The client actually doesn't have one- so we return null
     */
    public Logger getMinecraftLogger()
    {
        return null;
    }

    /**
     * @return the instance
     */
    public static FMLClientHandler instance()
    {
        return INSTANCE;
    }

    /**
     * @param player
     * @param gui
     */
    public void displayGuiScreen(final EntityPlayer player, final GuiScreen gui)
    {
        if (client.thePlayer==player && gui != null) {
            client.displayGuiScreen(gui);
        }
    }

    /**
     * @param mods
     */
    public void addSpecialModEntries(final ArrayList<ModContainer> mods)
    {
        if (optifineContainer!=null) {
            mods.add(optifineContainer);
        }
    }

    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        if (optifineContainer!=null)
        {
            return Collections.singletonList(String.format("Optifine %s", optifineContainer.getVersion()));
        } else {
            return ImmutableList.<String>of();
        }
    }

    @Override
    public Side getSide()
    {
        return Side.CLIENT;
    }

    public boolean hasOptifine()
    {
        return optifineContainer!=null;
    }

    @Override
    public void showGuiScreen(final Object clientGuiElement)
    {
        final GuiScreen gui = (GuiScreen) clientGuiElement;
        client.displayGuiScreen(gui);
    }

    @Override
    public Entity spawnEntityIntoClientWorld(final EntityRegistration er, final EntitySpawnPacket packet)
    {
        final WorldClient wc = client.theWorld;

        final Class<? extends Entity> cls = er.getEntityClass();

        try
        {
            final Entity entity;
            if (er.hasCustomSpawning())
            {
                entity = er.doCustomSpawning(packet);
            }
            else
            {
                entity = (Entity)(cls.getConstructor(World.class).newInstance(wc));
                final int offset = packet.entityId - entity.entityId;
                entity.entityId = packet.entityId;
                entity.setLocationAndAngles(packet.scaledX, packet.scaledY, packet.scaledZ, packet.scaledYaw, packet.scaledPitch);
                if (entity instanceof EntityLiving)
                {
                    ((EntityLiving)entity).rotationYawHead = packet.scaledHeadYaw;
                }

                final Entity[] parts = entity.getParts();
                if (parts != null)
                {
                    for (int j = 0; j < parts.length; j++)
                    {
                        parts[j].entityId += offset;
                    }
                }
            }

            entity.serverPosX = packet.rawX;
            entity.serverPosY = packet.rawY;
            entity.serverPosZ = packet.rawZ;

            if (entity instanceof IThrowableEntity)
            {
                final Entity thrower = client.thePlayer.entityId == packet.throwerId ? client.thePlayer : wc.getEntityByID(packet.throwerId);
                ((IThrowableEntity)entity).setThrower(thrower);
            }

            if (packet.metadata != null)
            {
                entity.getDataWatcher().updateWatchedObjectsFromList((List)packet.metadata);
            }

            if (packet.throwerId > 0)
            {
                entity.setVelocity(packet.speedScaledX, packet.speedScaledY, packet.speedScaledZ);
            }

            if (entity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData)entity).readSpawnData(packet.dataStream);
            }

            wc.addEntityToWorld(packet.entityId, entity);
            return entity;
        }
        catch (final Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A severe problem occurred during the spawning of an entity");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void adjustEntityLocationOnClient(final EntitySpawnAdjustmentPacket packet)
    {
        final Entity ent = client.theWorld.getEntityByID(packet.entityId);
        if (ent != null)
        {
            ent.serverPosX = packet.serverX;
            ent.serverPosY = packet.serverY;
            ent.serverPosZ = packet.serverZ;
        }
        else
        {
            FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", packet.entityId);
        }
    }

    @Override
    public void beginServerLoading(final MinecraftServer server)
    {
        serverShouldBeKilledQuietly = false;
        // NOOP
    }

    @Override
    public void finishServerLoading()
    {
        // NOOP
    }

    @Override
    public MinecraftServer getServer()
    {
        return client.getIntegratedServer();
    }

    @Override
    public void sendPacket(final Packet packet)
    {
        if(client.thePlayer != null)
        {
            client.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }

    @Override
    public void displayMissingMods(final ModMissingPacket modMissingPacket)
    {
        client.displayGuiScreen(new GuiModsMissingForServer(modMissingPacket));
    }

    /**
     * If the client is in the midst of loading, we disable saving so that custom settings aren't wiped out
     */
    public boolean isLoading()
    {
        return loading;
    }

    @Override
    public void handleTinyPacket(final NetHandler handler, final Packet131MapData mapData)
    {
        ((NetClientHandler)handler).fmlPacket131Callback(mapData);
    }

    @Override
    public void setClientCompatibilityLevel(final byte compatibilityLevel)
    {
        NetClientHandler.setConnectionCompatibilityLevel(compatibilityLevel);
    }

    @Override
    public byte getClientCompatibilityLevel()
    {
        return NetClientHandler.getConnectionCompatibilityLevel();
    }

    public void warnIDMismatch(final MapDifference<Integer, ItemData> idDifferences, final boolean mayContinue)
    {
        final GuiIdMismatchScreen mismatch = new GuiIdMismatchScreen(idDifferences, mayContinue);
        client.displayGuiScreen(mismatch);
    }

    public void callbackIdDifferenceResponse(final boolean response)
    {
        if (response)
        {
            serverShouldBeKilledQuietly = false;
            GameData.releaseGate(true);
            client.continueWorldLoading();
        }
        else
        {
            serverShouldBeKilledQuietly = true;
            GameData.releaseGate(false);
            // Reset and clear the client state
            client.loadWorld((WorldClient)null);
            client.displayGuiScreen(null);
        }
    }

    @Override
    public boolean shouldServerShouldBeKilledQuietly()
    {
        return serverShouldBeKilledQuietly;
    }

    @Override
    public void disconnectIDMismatch(final MapDifference<Integer, ItemData> s, final NetHandler toKill, final INetworkManager mgr)
    {
        boolean criticalMismatch = !s.entriesOnlyOnLeft().isEmpty();
        for (final Entry<Integer, ValueDifference<ItemData>> mismatch : s.entriesDiffering().entrySet())
        {
            final ValueDifference<ItemData> vd = mismatch.getValue();
            if (!vd.leftValue().mayDifferByOrdinal(vd.rightValue()))
            {
                criticalMismatch = true;
            }
        }

        if (!criticalMismatch)
        {
            // We'll carry on with this connection, and just log a message instead
            return;
        }
        // Nuke the connection
        ((NetClientHandler)toKill).disconnect();
        // Stop GuiConnecting
        GuiConnecting.forceTermination((GuiConnecting)client.currentScreen);
        // pulse the network manager queue to clear cruft
        mgr.processReadPackets();
        // Nuke the world client
        client.loadWorld((WorldClient)null);
        // Show error screen
        warnIDMismatch(s, false);
    }

    /**
     * Is this GUI type open?
     *
     * @param gui The type of GUI to test for
     * @return if a GUI of this type is open
     */
    public boolean isGUIOpen(final Class<? extends GuiScreen> gui)
    {
        return client.currentScreen != null && client.currentScreen.getClass().equals(gui);
    }


    @Override
    public void addModAsResource(final ModContainer container)
    {
        final Class<?> resourcePackType = container.getCustomResourcePackClass();
        if (resourcePackType != null)
        {
            try
            {
                final ResourcePack pack = (ResourcePack) resourcePackType.getConstructor(ModContainer.class).newInstance(container);
                resourcePackList.add(pack);
                resourcePackMap.put(container.getModId(), pack);
            }
            catch (final NoSuchMethodException e)
            {
                FMLLog.log(Level.SEVERE, "The container %s (type %s) returned an invalid class for it's resource pack.", container.getName(), container.getClass().getName());
                return;
            }
            catch (final Exception e)
            {
                FMLLog.log(Level.SEVERE, e, "An unexpected exception occurred constructing the custom resource pack for %s", container.getName());
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void updateResourcePackList()
    {
        client.refreshResources();
    }

    public ResourcePack getResourcePackFor(final String modId)
    {
        return resourcePackMap.get(modId);
    }

    @Override
    public String getCurrentLanguage()
    {
        return client.getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    @Override
    public void serverStopped()
    {
        // If the server crashes during startup, it might hang the client- reset the client so it can abend properly.
        final MinecraftServer server = getServer();

        if (server != null && !server.serverIsInRunLoop())
        {
            ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, true, "field_71296"+"_Q","serverIs"+"Running");
        }
    }
}
