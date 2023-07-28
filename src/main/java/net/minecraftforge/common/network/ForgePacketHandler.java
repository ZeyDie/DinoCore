package net.minecraftforge.common.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class ForgePacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(final INetworkManager network, final Packet250CustomPayload packet, final Player player)
    {
        final ForgePacket pkt = ForgePacket.readPacket(network, packet.data);
        // Part of an incomplete multipart packet
        if (pkt == null)
        {
            return;
        }

        pkt.execute(network, (EntityPlayer)player);
    }

}
