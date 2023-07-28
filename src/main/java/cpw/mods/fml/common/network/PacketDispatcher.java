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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;

/**
 * A simple utility class to send packet 250 packets around the place
 *
 * @author cpw
 *
 */
public class PacketDispatcher
{
    public static Packet250CustomPayload getPacket(final String type, final byte[] data)
    {
        return new Packet250CustomPayload(type, data);
    }

    public static void sendPacketToServer(final Packet packet)
    {
        FMLCommonHandler.instance().getSidedDelegate().sendPacket(packet);
    }

    public static void sendPacketToPlayer(final Packet packet, final Player player)
    {
        if (player instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    public static void sendPacketToAllAround(final double X, final double Y, final double Z, final double range, final int dimensionId, final Packet packet)
    {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null)
        {
            server.getConfigurationManager().sendToAllNear(X, Y, Z, range, dimensionId, packet);
        }
        else
        {
            FMLLog.fine("Attempt to send packet to all around without a server instance available");
        }
    }

    public static void sendPacketToAllInDimension(final Packet packet, final int dimId)
    {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null)
        {
            server.getConfigurationManager().sendPacketToAllPlayersInDimension(packet, dimId);
        }
        else
        {
            FMLLog.fine("Attempt to send packet to all in dimension without a server instance available");
        }
    }

    public static void sendPacketToAllPlayers(final Packet packet)
    {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null)
        {
            server.getConfigurationManager().sendPacketToAllPlayers(packet);
        }
        else
        {
            FMLLog.fine("Attempt to send packet to all in dimension without a server instance available");
        }
    }

    public static Packet131MapData getTinyPacket(final Object mod, final short tag, final byte[] data)
    {
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mod);
        return new Packet131MapData((short) nmh.getNetworkId(), tag, data);
    }
}
