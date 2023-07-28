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

package cpw.mods.fml.common.network;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLPacket.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryView;
import org.bukkit.event.inventory.InventoryType;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

// Cauldron start
// Cauldron end
/**
 * @author cpw
 *
 */
public class NetworkRegistry
{

    private static final NetworkRegistry INSTANCE = new NetworkRegistry();
    /**
     * A map of active channels per player
     */
    private Multimap<Player, String> activeChannels = ArrayListMultimap.create();
    /**
     * A map of the packet handlers for packets
     */
    private Multimap<String, IPacketHandler> universalPacketHandlers = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> clientPacketHandlers = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> serverPacketHandlers = ArrayListMultimap.create();
    /**
     * A linked set of registered connection handlers
     */
    private Set<IConnectionHandler> connectionHandlers = Sets.newLinkedHashSet();
    private Map<ModContainer, IGuiHandler> serverGuiHandlers = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> clientGuiHandlers = Maps.newHashMap();
    private List<IChatListener> chatListeners = Lists.newArrayList();

    public static NetworkRegistry instance()
    {
        return INSTANCE;
    }
    /**
     * Get the packet 250 channel registration string
     * @return the {@link Packet250CustomPayload} channel registration string
     */
    byte[] getPacketRegistry(final Side side)
    {
        return Joiner.on('\0').join(Iterables.concat(Collections.singletonList("FML"),universalPacketHandlers.keySet(), side.isClient() ? clientPacketHandlers.keySet() : serverPacketHandlers.keySet())).getBytes(Charsets.UTF_8);
    }
    /**
     * Is the specified channel active for the player?
     * @param channel
     * @param player
     */
    public boolean isChannelActive(final String channel, final Player player)
    {
        return activeChannels.containsEntry(player,channel);
    }
    /**
     * register a channel to a mod
     * @param handler the packet handler
     * @param channelName the channel name to register it with
     */
    public void registerChannel(final IPacketHandler handler, final String channelName)
    {
        if (Strings.isNullOrEmpty(channelName) || (channelName!=null && channelName.length()>16))
        {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");

        }
        universalPacketHandlers.put(channelName, handler);
    }

    public void registerChannel(final IPacketHandler handler, final String channelName, final Side side)
    {
        if (side == null)
        {
            registerChannel(handler, channelName);
            return;
        }
        if (Strings.isNullOrEmpty(channelName) || (channelName!=null && channelName.length()>16))
        {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");

        }
        if (side.isClient())
        {
            clientPacketHandlers.put(channelName, handler);
        }
        else
        {
            serverPacketHandlers.put(channelName, handler);
        }
    }
    /**
     * Activate the channel for the player
     * @param player
     */
    void activateChannel(final Player player, final String channel)
    {
        activeChannels.put(player, channel);
    }
    /**
     * Deactivate the channel for the player
     * @param player
     * @param channel
     */
    void deactivateChannel(final Player player, final String channel)
    {
        activeChannels.remove(player, channel);
    }
    /**
     * Register a connection handler
     *
     * @param handler
     */
    public void registerConnectionHandler(final IConnectionHandler handler)
    {
        connectionHandlers.add(handler);
    }

    /**
     * Register a chat listener
     * @param listener
     */
    public void registerChatListener(final IChatListener listener)
    {
        chatListeners.add(listener);
    }

    void playerLoggedIn(final EntityPlayerMP player, final NetServerHandler netHandler, final INetworkManager manager)
    {
        generateChannelRegistration(player, netHandler, manager);
        for (final IConnectionHandler handler : connectionHandlers)
        {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }
    }

    String connectionReceived(final NetLoginHandler netHandler, final INetworkManager manager)
    {
        for (final IConnectionHandler handler : connectionHandlers)
        {
            final String kick = handler.connectionReceived(netHandler, manager);
            if (!Strings.isNullOrEmpty(kick))
            {
                return kick;
            }
        }
        return null;
    }

    void connectionOpened(final NetHandler netClientHandler, final String server, final int port, final INetworkManager networkManager)
    {
        for (final IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionOpened(netClientHandler, server, port, networkManager);
        }
    }

    void connectionOpened(final NetHandler netClientHandler, final MinecraftServer server, final INetworkManager networkManager)
    {
        for (final IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionOpened(netClientHandler, server, networkManager);
        }
    }

    void clientLoggedIn(final NetHandler clientHandler, final INetworkManager manager, final Packet1Login login)
    {
        generateChannelRegistration(clientHandler.getPlayer(), clientHandler, manager);
        for (final IConnectionHandler handler : connectionHandlers)
        {
            handler.clientLoggedIn(clientHandler, manager, login);
        }
    }

    void connectionClosed(final INetworkManager manager, final EntityPlayer player)
    {
        for (final IConnectionHandler handler : connectionHandlers)
        {
            handler.connectionClosed(manager);
        }
        activeChannels.removeAll(player);
    }

    void generateChannelRegistration(final EntityPlayer player, final NetHandler netHandler, final INetworkManager manager)
    {
        final Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "REGISTER";
        pkt.data = getPacketRegistry(player instanceof EntityPlayerMP ? Side.SERVER : Side.CLIENT);
        pkt.length = pkt.data.length;
        manager.addToSendQueue(pkt);
    }

    void handleCustomPacket(final Packet250CustomPayload packet, final INetworkManager network, final NetHandler handler)
    {
        if ("REGISTER".equals(packet.channel))
        {
            handleRegistrationPacket(packet, (Player)handler.getPlayer());
            handleBukkitRegistrationPacket(packet, (CraftPlayer)((NetServerHandler)(handler)).getPlayerB()); // Cauldron
        }
        else if ("UNREGISTER".equals(packet.channel))
        {
            handleUnregistrationPacket(packet, (Player)handler.getPlayer());
            handleBukkitUnregistrationPacket(packet, (CraftPlayer)((NetServerHandler)(handler)).getPlayerB()); // Cauldron
        }
        else
        {
            handlePacket(packet, network, (Player)handler.getPlayer());
            handler.handleVanilla250Packet(packet);  // Cauldron send it back for CB dispatch
        }
    }


    private void handlePacket(final Packet250CustomPayload packet, final INetworkManager network, final Player player)
    {
        final String channel = packet.channel;
        for (final IPacketHandler handler : Iterables.concat(universalPacketHandlers.get(channel), player instanceof EntityPlayerMP ? serverPacketHandlers.get(channel) : clientPacketHandlers.get(channel)))
        {
            handler.onPacketData(network, packet, player);
        }
    }

    private void handleRegistrationPacket(final Packet250CustomPayload packet, final Player player)
    {
        final List<String> channels = extractChannelList(packet);
        for (final String channel : channels)
        {
            activateChannel(player, channel);
        }
    }
    private void handleUnregistrationPacket(final Packet250CustomPayload packet, final Player player)
    {
        final List<String> channels = extractChannelList(packet);
        for (final String channel : channels)
        {
            deactivateChannel(player, channel);
        }
    }

    // Cauldron start - handle CB plugin registration
    private void handleBukkitRegistrationPacket(final Packet250CustomPayload packet, final CraftPlayer player)
    {
        final String channels = new String(packet.data, StandardCharsets.UTF_8);

        for (final String channel : channels.split("\0"))
        {
            if (MinecraftServer.getServer().cauldronConfig.connectionLogging.getValue()) {
                System.out.println("adding plugin channel " + channel);
            }
            player.addChannel(channel);
        }
    }

    private void handleBukkitUnregistrationPacket(final Packet250CustomPayload packet, final CraftPlayer player)
    {
        final String channels = new String(packet.data, StandardCharsets.UTF_8);

        for (final String channel : channels.split("\0"))
        {
            player.removeChannel(channel);
        }
    }
    // Cauldron end

    private List<String> extractChannelList(final Packet250CustomPayload packet)
    {
        final String request = new String(packet.data, Charsets.UTF_8);
        final List<String> channels = Lists.newArrayList(Splitter.on('\0').split(request));
        return channels;
    }

    public void registerGuiHandler(final Object mod, final IGuiHandler handler)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null)
        {
            mc = Loader.instance().activeModContainer();
            FMLLog.log(Level.WARNING, "Mod %s attempted to register a gui network handler during a construction phase", mc.getModId());
        }
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
        if (nmh == null)
        {
            FMLLog.log(Level.FINE, "The mod %s needs to be a @NetworkMod to register a Networked Gui Handler", mc.getModId());
        }
        else
        {
            serverGuiHandlers.put(mc, handler);
        }
        clientGuiHandlers.put(mc, handler);
    }
    void openRemoteGui(final ModContainer mc, final EntityPlayerMP player, final int modGuiId, final World world, final int x, final int y, final int z)
    {
        final IGuiHandler handler = serverGuiHandlers.get(mc);
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
        if (handler != null && nmh != null)
        {
            Container container = (Container)handler.getServerGuiElement(modGuiId, player, world, x, y, z);
            if (container != null)
            {
                // Cauldron start - create bukkitView for passed container then fire open event.
                if (player != null)
                {
                    if (container.getBukkitView() == null)
                    {
                        final TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
                        if (te != null && te instanceof IInventory)
                        {
                            final IInventory teInv = (IInventory)te;
                            final CraftInventory inventory = new CraftInventory(teInv);
                            container.bukkitView = new CraftInventoryView(player.getBukkitEntity(), inventory, container);
                        }
                        else
                        {
                            container.bukkitView = new CraftInventoryView(player.getBukkitEntity(), MinecraftServer.getServer().server.createInventory(player.getBukkitEntity(), InventoryType.CHEST), container);
                        }

                        container = CraftEventFactory.callInventoryOpenEvent(player, container, false);
                        if (container == null)
                        {
                            return;
                        }
                    }
                }
                // Cauldron end
                player.incrementWindowID();
                player.closeContainer();
                final int windowId = player.currentWindowId;
                final Packet250CustomPayload pkt = new Packet250CustomPayload();
                pkt.channel = "FML";
                pkt.data = FMLPacket.makePacket(Type.GUIOPEN, windowId, nmh.getNetworkId(), modGuiId, x, y, z);
                pkt.length = pkt.data.length;
                player.playerNetServerHandler.sendPacketToPlayer(pkt);
                player.openContainer = container;
                player.openContainer.windowId = windowId;
                player.openContainer.addCraftingToCrafters(player);
            }
        }
    }
    void openLocalGui(final ModContainer mc, final EntityPlayer player, final int modGuiId, final World world, final int x, final int y, final int z)
    {
        final IGuiHandler handler = clientGuiHandlers.get(mc);
        FMLCommonHandler.instance().showGuiScreen(handler.getClientGuiElement(modGuiId, player, world, x, y, z));
    }
    public Packet3Chat handleChat(final NetHandler handler, Packet3Chat chat)
    {
        Packet3Chat chat1 = chat;
        Side s = Side.CLIENT;
        if (handler instanceof NetServerHandler)
        {
            s = Side.SERVER;
        }
        for (final IChatListener listener : chatListeners)
        {
            chat1 = s.isClient() ? listener.clientChat(handler, chat1) : listener.serverChat(handler, chat1);
        }

        return chat1;
    }
    public void handleTinyPacket(final NetHandler handler, final Packet131MapData mapData)
    {
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler((int)mapData.itemID);
        if (nmh == null)
        {
            FMLLog.info("Received a tiny packet for network id %d that is not recognised here", mapData.itemID);
            return;
        }
        if (nmh.hasTinyPacketHandler())
        {
            nmh.getTinyPacketHandler().handle(handler, mapData);
        }
        else
        {
            FMLLog.info("Received a tiny packet for a network mod that does not accept tiny packets %s", nmh.getContainer().getModId());
        }
    }
}
