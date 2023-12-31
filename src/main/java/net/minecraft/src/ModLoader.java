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
package net.minecraft.src;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static cpw.mods.fml.relauncher.Side.CLIENT;

/**
 * ModLoader support is officially deprecated, to be removed with 1.7.
 * @author cpw
 *
 */
@Deprecated
public class ModLoader
{
    public static final String fmlMarker = "This is an FML marker";
    @Deprecated
    public static final Map<String,Map<String,String>> localizedStrings=Collections.emptyMap();

    /**
     * Adds localization info for an achievement, Not used on the server.
     *
     * @param achievement The achievement to name
     * @param name The name
     * @param description The description
     */
    @Deprecated
    public static void addAchievementDesc(final Achievement achievement, final String name, final String description)
    {
        final String achName=achievement.getName();
        addLocalization(achName, name);
        addLocalization(achName+".desc", description);
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * {@link GameRegistry#getFuelValue(ItemStack)}
     *
     * @param id The Item ID
     * @param metadata The Item Metadata
     * @return The fuel strength, in ticks, 0 if unhandled
     */
    @Deprecated
    public static int addAllFuel(final int id, final int metadata)
    {
        return 0;
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void addAllRenderers(final Map<Class<? extends Entity>, Render> renderers)
    {
    }

    /**
     * Adds a new prefix to the armor texture list
     *
     * {@link RenderingRegistry#addNewArmourRendererPrefix(String)}
     *
     * @param armor The new armor prefix
     * @return The new armor index
     */
    @Deprecated
    @SideOnly(CLIENT)
    public static int addArmor(final String armor)
    {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }

    /**
     * This method adds the supplied biome to the set of candidate biomes for the default world generator type.
     *
     * @param biome The biome to add
     */
    @Deprecated
    public static void addBiome(final BiomeGenBase biome)
    {
        GameRegistry.addBiome(biome);
    }

    @Deprecated
    public static void addEntityTracker(final BaseMod mod, final Class<? extends Entity> entityClass, final int entityTypeId, final int updateRange, final int updateInterval, final boolean sendVelocityInfo)
    {
        ModLoaderHelper.buildEntityTracker(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
    }

    @Deprecated
    public static void addCommand(final ICommand command)
    {
        ModLoaderHelper.addCommand(command);
    }

    /**
     * Add a behaviour to the dispenser
     *
     * @param item
     * @param behavior
     */
    @Deprecated
    public static void addDispenserBehavior(final Item item, final IBehaviorDispenseItem behavior)
    {
    	BlockDispenser.dispenseBehaviorRegistry.putObject(item, behavior);
    }
    /**
     * Add localization for the specified string
     *
     * @param key Key
     * @param value Value
     */
    @Deprecated
    public static void addLocalization(final String key, final String value)
    {
        addLocalization(key, "en_US", value);
    }

    /**
     * Add localization for the specified string
     *
     * @param key Key
     * @param lang Language identifier
     * @param value Value
     */
    @Deprecated
    public static void addLocalization(final String key, final String lang, final String value)
    {
        LanguageRegistry.instance().addStringLocalization(key, lang, value);
    }

    /**
     * Name the specified minecraft object with the supplied name
     *
     * @param instance Item to name
     * @param name The name to give it
     */
    @Deprecated
    public static void addName(final Object instance, final String name)
    {
        addName(instance,"en_US",name);
    }

    /**
     * Unimplemented on the server as it does not generate names
     *
     * @param instance Item to name
     * @param lang Languge identifier
     * @param name Name to give it
     */
    @Deprecated
    public static void addName(final Object instance, final String lang, final String name)
    {
        LanguageRegistry.instance().addNameForObject(instance, lang, name);
    }

    /**
     * Attempts to register a small image to be applied to a larger texture image,
     * typically how old ModLoader mods add custom Item/Block textures.
     *
     * Forge mods should use setTextureFile in Item/Block
     *
     * Will return the icon index it was applied to.
     *
     * Unimplemented on the server as it does not render textures
     *
     * @param fileToOverride The texture to apply the new image to
     * @param fileToAdd The new image
     * @return The 'icon index' in the main image that the new image will be applied to
     */
    @Deprecated
    @SideOnly(CLIENT)
    public static int addOverride(final String fileToOverride, final String fileToAdd)
    {
        return RenderingRegistry.addTextureOverride(fileToOverride, fileToAdd);
    }

    /**
     * Attempts to register a small image to be applied to a larger texture image,
     * typically how old ModLoader mods add custom Item/Block textures.
     *
     * Forge mods should use setTextureFile in Item/Block
     *
     * Unimplemented on the server as it does not render textures
     *
     * @param path The texture to apply the new image to
     * @param overlayPath The new image
     * @param index Where on the texture to apply it
     */
    @Deprecated
    @SideOnly(CLIENT)
    public static void addOverride(final String path, final String overlayPath, final int index)
    {
        RenderingRegistry.addTextureOverride(path, overlayPath, index);
    }

    /**
     * Add a Shaped Recipe
     *
     * @param output The result
     * @param params The input
     */
    @Deprecated
    public static void addRecipe(final ItemStack output, final Object... params)
    {
        GameRegistry.addRecipe(output, params);
    }

    /**
     * Add a shapeless recipe
     *
     * @param output The result
     * @param params The input
     */
    @Deprecated
    public static void addShapelessRecipe(final ItemStack output, final Object... params)
    {
        GameRegistry.addShapelessRecipe(output, params);
    }

    /**
     * Add a new product to be smelted
     *
     * @param input
     * @param output
     */
    @Deprecated
    public static void addSmelting(final int input, final ItemStack output)
    {
        GameRegistry.addSmelting(input, output, 1.0f);
    }

    /**
     * Add a new product to be smelted
     *
     * @param input
     * @param output
     */
    @Deprecated
    public static void addSmelting(final int input, final ItemStack output, final float experience)
    {
        GameRegistry.addSmelting(input, output, experience);
    }
    /**
     * Add a mob to the spawn list
     *
     * @param entityClass
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     */
    @Deprecated
    public static void addSpawn(final Class<? extends EntityLiving> entityClass, final int weightedProb, final int min, final int max, final EnumCreatureType spawnList)
    {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, WorldType.base12Biomes);
    }

    /**
     * Add a mob to the spawn list
     *
     * @param entityClass
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     * @param biomes
     */
    @Deprecated
    public static void addSpawn(final Class<? extends EntityLiving> entityClass, final int weightedProb, final int min, final int max, final EnumCreatureType spawnList, final BiomeGenBase... biomes)
    {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, biomes);
    }

    /**
     * Add a mob to the spawn list
     *
     * @param entityName
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     */
    @Deprecated
    public static void addSpawn(final String entityName, final int weightedProb, final int min, final int max, final EnumCreatureType spawnList)
    {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, WorldType.base12Biomes);
    }

    /**
     * Add a mob to the spawn list
     *
     * @param entityName
     * @param weightedProb
     * @param min
     * @param max
     * @param spawnList
     * @param biomes
     */
    @Deprecated
    public static void addSpawn(final String entityName, final int weightedProb, final int min, final int max, final EnumCreatureType spawnList, final BiomeGenBase... biomes)
    {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, biomes);
    }

    @Deprecated
    public static void addTrade(final int profession, final TradeEntry entry)
    {
        ModLoaderHelper.registerTrade(profession, entry);
    }
    /**
     * Send a packet from the client
     * @param packet
     */
    @Deprecated
    public static void clientSendPacket(final Packet packet)
    {
        PacketDispatcher.sendPacketToServer(packet);
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param xVel
     * @param zVel
     * @param item
     * @return Always false, not implemented here
     */
    @Deprecated
    public static boolean dispenseEntity(final World world, final double x, final double y, final double z, final int xVel, final int zVel, final ItemStack item)
    {
        return false;
    }

    /**
     * Remove a container and drop all the items in it on the ground around
     *
     * @param world
     * @param x
     * @param y
     * @param z
     */
    @Deprecated
    public static void genericContainerRemoval(final World world, final int x, final int y, final int z)
    {
/*        TileEntity te = world.func_603_b(x, y, z);

        if (!(te instanceof IInventory))
        {
            return;
        }

        IInventory inv = (IInventory)te;

        for (int l = 0; l < inv.func_469_c(); l++)
        {
            ItemStack itemstack = inv.func_468_c(l);

            if (itemstack == null)
            {
                continue;
            }

            float f = world.field_1037_n.nextFloat() * 0.8F + 0.1F;
            float f1 = world.field_1037_n.nextFloat() * 0.8F + 0.1F;
            float f2 = world.field_1037_n.nextFloat() * 0.8F + 0.1F;

            while (itemstack.field_1615_a > 0)
            {
                int i1 = world.field_1037_n.nextInt(21) + 10;

                if (i1 > itemstack.field_1615_a)
                {
                    i1 = itemstack.field_1615_a;
                }

                itemstack.field_1615_a -= i1;
                EntityItem entityitem = new EntityItem(world, (float)te.field_823_f + f, (float)te.field_822_g + f1, (float)te.field_821_h + f2, new ItemStack(itemstack.field_1617_c, i1, itemstack.func_21181_i()));
                float f3 = 0.05F;
                entityitem.field_608_an = (float) world.field_1037_n.nextGaussian() * f3;
                entityitem.field_607_ao = (float) world.field_1037_n.nextGaussian() * f3 + 0.2F;
                entityitem.field_606_ap = (float) world.field_1037_n.nextGaussian() * f3;

                if (itemstack.func_40710_n())
                {
                    entityitem.field_801_a.func_40706_d((NBTTagCompound) itemstack.func_40709_o().func_40195_b());
                }

                world.func_674_a(entityitem);
            }
        }
*/    }

    /**
     * Get a list of all BaseMod loaded into the system
     * {@link ModLoaderModContainer#findAll}
     *
     * @return A list containing all loaded ModLoader mods
     */
    @Deprecated
    public static List<BaseMod> getLoadedMods()
    {
        return ModLoaderModContainer.findAll(BaseMod.class);
    }

    /**
     * Get a logger instance {@link FMLCommonHandler#getFMLLogger()}
     *
     * @return The current logger
     */
    @Deprecated
    public static Logger getLogger()
    {
        return FMLLog.getLogger();
    }

    @SideOnly(CLIENT)
    @Deprecated
    public static Minecraft getMinecraftInstance()
    {
        return FMLClientHandler.instance().getClient();
    }

    @Deprecated
    public static MinecraftServer getMinecraftServerInstance()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    /**
     * Get a value from a field using reflection
     * {@link ObfuscationReflectionHelper#getPrivateValue(Class, Object, int)}
     *
     * @param instanceclass
     * @param instance
     * @param fieldindex
     * @return The value in the specified field.
     */
    @Deprecated
    public static <T, E> T getPrivateValue(final Class<? super E> instanceclass, final E instance, final int fieldindex)
    {
        return ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, fieldindex);
    }

    /**
     * Get a value from a field using reflection
     * {@link ObfuscationReflectionHelper#getPrivateValue(Class, Object, String[])}
     *
     * @param instanceclass
     * @param instance
     * @param field
     * @return The value in the specified field.
     */
    @Deprecated
    public static <T, E> T getPrivateValue(final Class<? super E> instanceclass, final E instance, final String field)
    {
        return ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, field);
    }

    /**
     * Stubbed method on the server to return a unique model id
     *
     */
    @SideOnly(CLIENT)
    @Deprecated
    public static int getUniqueBlockModelID(final BaseMod mod, final boolean inventoryRenderer)
    {
        return ModLoaderClientHelper.obtainBlockModelIdFor(mod, inventoryRenderer);
    }

    /**
     * Get a new unique entity id
     * {@link EntityRegistry#findGlobalUniqueEntityId()}
     *
     * @return A unique entity ID
     */
    @Deprecated
    public static int getUniqueEntityId()
    {
        return EntityRegistry.findGlobalUniqueEntityId();
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static int getUniqueSpriteIndex(final String path)
    {
        return -1;
    }

    /**
     * To properly implement packet 250 protocol you should always check your
     * channel is active prior to sending the packet
     *
     * @param player
     * @param channel
     * @return If the channel is registered to the current connection.
     */
    @Deprecated
    public static boolean isChannelActive(final EntityPlayer player, final String channel)
    {
        return NetworkRegistry.instance().isChannelActive(channel, (Player)player);
    }

    @SideOnly(CLIENT)
    @Deprecated
    public static boolean isGUIOpen(final Class<? extends GuiScreen> gui)
    {
        return FMLClientHandler.instance().isGUIOpen(gui);
    }

    /**
     * Is the named mod loaded?
     * {@link Loader#isModLoaded(String)}
     *
     * @param modname
     * @return If the specified mod is loaded
     */
    @Deprecated
    public static boolean isModLoaded(final String modname)
    {
        return Loader.isModLoaded(modname);
    }

    /**
     * Implemented elsewhere
     */
    @Deprecated
    public static void loadConfig()
    {
    }

//    @SideOnly(CLIENT)
//    public static BufferedImage loadImage(RenderEngine renderEngine, String path) throws Exception
//    {
//        return TextureFXManager.instance().loadImageFromTexturePack(renderEngine, path);
//    }

    /**
     * Call in from elsewhere. Unimplemented here.
     * @param player
     * @param item
     */
    @Deprecated
    public static void onItemPickup(final EntityPlayer player, final ItemStack item)
    {
    }
    /**
     * Call in from elsewhere. Unimplemented here.
     */
    @Deprecated
    @SideOnly(CLIENT)
    public static void onTick(final float tick, final Minecraft game)
    {
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void openGUI(final EntityPlayer player, final GuiScreen gui)
    {
        FMLClientHandler.instance().displayGuiScreen(player, gui);
    }

    @Deprecated
    public static void populateChunk(final IChunkProvider generator, final int chunkX, final int chunkZ, final World world)
    {
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param packet
     */
    @Deprecated
    public static void receivePacket(final Packet250CustomPayload packet)
    {
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static KeyBinding[] registerAllKeys(final KeyBinding[] keys)
    {
        return keys;
    }

//    @Deprecated
//    @SideOnly(CLIENT)
//    public static void registerAllTextureOverrides(RenderEngine cache)
//    {
//    }

    /**
     * Register a new block
     *
     * @param block
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public static void registerBlock(final Block block)
    {
        GameRegistry.registerBlock(block);
    }

    /**
     * Register a new block
     *
     * @param block
     * @param itemclass
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public static void registerBlock(final Block block, final Class<? extends ItemBlock> itemclass)
    {
        GameRegistry.registerBlock(block, itemclass);
    }

    @Deprecated
    public static void registerContainerID(final BaseMod mod, final int id)
    {
        ModLoaderHelper.buildGuiHelper(mod, id);
    }
    /**
     * Register a new entity ID
     *
     * @param entityClass
     * @param entityName
     * @param id
     */
    @Deprecated
    public static void registerEntityID(final Class<? extends Entity> entityClass, final String entityName, final int id)
    {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
    }

    /**
     * Register a new entity ID
     *
     * @param entityClass
     * @param entityName
     * @param id
     * @param background
     * @param foreground
     */
    @Deprecated
    public static void registerEntityID(final Class<? extends Entity> entityClass, final String entityName, final int id, final int background, final int foreground)
    {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id, background, foreground);
    }

    @SideOnly(CLIENT)
    @Deprecated
    public static void registerKey(final BaseMod mod, final KeyBinding keyHandler, final boolean allowRepeat)
    {
        ModLoaderClientHelper.registerKeyBinding(mod, keyHandler, allowRepeat);
    }

    /**
     * Register the mod for packets on this channel.
     * {@link NetworkRegistry#registerChannel(IPacketHandler, String)}
     *
     * @param mod
     * @param channel
     */
    @Deprecated
    public static void registerPacketChannel(final BaseMod mod, final String channel)
    {
        NetworkRegistry.instance().registerChannel(ModLoaderHelper.buildPacketHandlerFor(mod), channel);
    }

    /**
     * Register a new tile entity class
     *
     * @param tileEntityClass
     * @param id
     */
    @Deprecated
    public static void registerTileEntity(final Class<? extends TileEntity> tileEntityClass, final String id)
    {
        GameRegistry.registerTileEntity(tileEntityClass, id);
    }

    @SideOnly(CLIENT)
    @Deprecated
    public static void registerTileEntity(final Class<? extends TileEntity> tileEntityClass, final String id, final TileEntitySpecialRenderer renderer)
    {
        ClientRegistry.registerTileEntity(tileEntityClass, id, renderer);
    }

    /**
     * Remove a biome from the list of generated biomes
     *
     * @param biome
     */
    @Deprecated
    public static void removeBiome(final BiomeGenBase biome)
    {
        GameRegistry.removeBiome(biome);
    }

    /**
     * Remove a spawn
     *
     * @param entityClass
     * @param spawnList
     */
    @Deprecated
    public static void removeSpawn(final Class<? extends EntityLiving> entityClass, final EnumCreatureType spawnList)
    {
        EntityRegistry.removeSpawn(entityClass, spawnList, WorldType.base12Biomes);
    }

    /**
     * Remove a spawn
     *
     * @param entityClass
     * @param spawnList
     * @param biomes
     */
    @Deprecated
    public static void removeSpawn(final Class<? extends EntityLiving> entityClass, final EnumCreatureType spawnList, final BiomeGenBase... biomes)
    {
        EntityRegistry.removeSpawn(entityClass, spawnList, biomes);
    }

    /**
     * Remove a spawn
     *
     * @param entityName
     * @param spawnList
     */
    @Deprecated
    public static void removeSpawn(final String entityName, final EnumCreatureType spawnList)
    {
        EntityRegistry.removeSpawn(entityName, spawnList, WorldType.base12Biomes);
    }

    /**
     * Remove a spawn
     *
     * @param entityName
     * @param spawnList
     * @param biomes
     */
    @Deprecated
    public static void removeSpawn(final String entityName, final EnumCreatureType spawnList, final BiomeGenBase... biomes)
    {
        EntityRegistry.removeSpawn(entityName, spawnList, biomes);
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static boolean renderBlockIsItemFull3D(final int modelID)
    {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelID);
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void renderInvBlock(final RenderBlocks renderer, final Block block, final int metadata, final int modelID)
    {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static boolean renderWorldBlock(final RenderBlocks renderer, final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelID)
    {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelID);
    }

    /**
     * Configuration is handled elsewhere
     * {@link ModLoaderModContainer}
     */
    @Deprecated
    public static void saveConfig()
    {
    }

    /**
     * Send a packet from client to server
     *
     * @param packet
     */
    @Deprecated
    public static void sendPacket(final Packet packet) {
        PacketDispatcher.sendPacketToServer(packet);
    }
    /**
     * Send a chat message to the server
     *
     * @param text
     */
    @Deprecated
    public static void serverChat(final String text)
    {
    }

    @Deprecated
    @SideOnly(CLIENT)
    public static void serverLogin(final NetClientHandler handler, final Packet1Login loginPacket)
    {
    }

    @Deprecated
    public static void serverSendPacket(final NetServerHandler handler, final Packet packet)
    {
        if (handler != null)
        {
            PacketDispatcher.sendPacketToPlayer(packet, (Player)handler.getPlayer());
        }
    }
    @Deprecated
    public static void serverOpenWindow(final EntityPlayerMP player, final Container container, final int ID, final int x, final int y, final int z)
    {
        ModLoaderHelper.openGui(ID, player, container, x, y, z);
    }

    /**
     * Indicate that you want to receive ticks
     *
     * @param mod receiving the events
     * @param enable indicates whether you want to recieve them or not
     * @param useClock don't receive render subticks, just world ticks
     */
    @Deprecated
    public static void setInGameHook(final BaseMod mod, final boolean enable, final boolean useClock)
    {
        ModLoaderHelper.updateStandardTicks(mod, enable, useClock);
    }


    @Deprecated
    public static void setInGUIHook(final BaseMod mod, final boolean enable, final boolean useClock)
    {
        ModLoaderHelper.updateGUITicks(mod, enable, useClock);
    }

    /**
     * Set a private field to a value using reflection
     * {@link ObfuscationReflectionHelper#setPrivateValue(Class, Object, int, Object)}
     *
     * @param instanceclass
     * @param instance
     * @param fieldindex
     * @param value
     */
    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> instanceclass, final T instance, final int fieldindex, final E value)
    {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, fieldindex);
    }

    /**
     * Set a private field to a value using reflection
     * {@link ObfuscationReflectionHelper#setPrivateValue(Class, Object, String, Object)}
     *
     * @param instanceclass
     * @param instance
     * @param field
     * @param value
     */
    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> instanceclass, final T instance, final String field, final E value)
    {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, field);
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param player
     * @param item
     * @param matrix
     */
    @Deprecated
    public static void takenFromCrafting(final EntityPlayer player, final ItemStack item, final IInventory matrix)
    {
    }

    /**
     * This method is a call in hook from modified external code. Implemented elsewhere.
     *
     * @param player
     * @param item
     */
    @Deprecated
    public static void takenFromFurnace(final EntityPlayer player, final ItemStack item)
    {
    }

    /**
     * Throw the offered exception. Likely will stop the game.
     *
     * @param message
     * @param e
     */
    @Deprecated
    public static void throwException(final String message, final Throwable e)
    {
        FMLCommonHandler.instance().raiseException(e, message, true);
    }

    @Deprecated
    public static void throwException(final Throwable e)
    {
        throwException("Exception in ModLoader", e);
    }
}