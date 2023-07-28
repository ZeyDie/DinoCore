package net.minecraftforge.common.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.FluidIdMapPacket;

public class ForgeConnectionHandler implements IConnectionHandler {

    @Override
    public void playerLoggedIn(final Player player, final NetHandler netHandler, final INetworkManager manager)
    {
        final Packet250CustomPayload[] fluidPackets = ForgePacket.makePacketSet(new FluidIdMapPacket());
        for (int i = 0; i < fluidPackets.length; i++) {
            PacketDispatcher.sendPacketToPlayer(fluidPackets[i], player);
        }
    }

    @Override
    public String connectionReceived(final NetLoginHandler netHandler, final INetworkManager manager)
    {
        return null;
    }

    @Override
    public void connectionOpened(final NetHandler netClientHandler, final String server, final int port, final INetworkManager manager)
    {

    }

    @Override
    public void connectionOpened(final NetHandler netClientHandler, final MinecraftServer server, final INetworkManager manager)
    {

    }

    @Override
    public void connectionClosed(final INetworkManager manager)
    {

    }

    @Override
    public void clientLoggedIn(final NetHandler clientHandler, final INetworkManager manager, final Packet1Login login)
    {

    }

}
