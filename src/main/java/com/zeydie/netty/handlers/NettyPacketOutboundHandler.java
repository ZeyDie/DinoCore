package com.zeydie.netty.handlers;

import com.zeydie.netty.packets.PacketHandlers;
import com.zeydie.netty.packets.PacketWithBytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class NettyPacketOutboundHandler extends MessageToMessageEncoder<Packet> {
    @NotNull
    private final String username;
    @NotNull
    private final UUID uuid;

    public NettyPacketOutboundHandler(
            @NotNull final String username,
            @NotNull final UUID uuid
    ) {
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    protected void encode(
            @NotNull final ChannelHandlerContext channelHandlerContext,
            @NotNull final Packet packet,
            @NotNull final List<Object> list
    ) {
        final PacketWithBytes packetWithBytes = new PacketWithBytes(packet, null);
        final List<PacketHandlers.PacketHandler> packetHandlers = PacketHandlers.specificHandlers.get(packet.getPacketId());

        for (PacketHandlers.PacketHandler packetHandler : packetHandlers)
            if (packetHandler.handleOutboundPacket(packetWithBytes, this.username, this.uuid, list))
                return;

        for (PacketHandlers.PacketHandler packetHandler : PacketHandlers.generalHandlers)
            if (packetHandler.handleOutboundPacket(packetWithBytes, this.username, this.uuid, list))
                return;

        list.add(packet);
    }
}
