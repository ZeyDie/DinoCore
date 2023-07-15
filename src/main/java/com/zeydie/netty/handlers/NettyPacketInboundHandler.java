package com.zeydie.netty.handlers;

import com.zeydie.netty.packets.PacketHandlers;
import com.zeydie.netty.packets.PacketWithBytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class NettyPacketInboundHandler extends MessageToMessageDecoder<Packet> {
    @NotNull
    private final String username;
    @NotNull
    private final UUID uuid;

    public NettyPacketInboundHandler(
            @NotNull final String var1,
            @NotNull final UUID var2
    ) {
        this.username = var1;
        this.uuid = var2;
    }

    @Override
    protected void decode(
            @NotNull final ChannelHandlerContext channelHandlerContext,
            @NotNull final Packet packet,
            @NotNull final List<Object> list
    ) {
        final PacketWithBytes var4 = new PacketWithBytes(packet, null);
        final List<PacketHandlers.PacketHandler> packetHandlers = PacketHandlers.specificHandlers.get(packet.getPacketId());

        Iterator<PacketHandlers.PacketHandler> var6;
        PacketHandlers.PacketHandler var7;
        if (packetHandlers != null) {
            var6 = packetHandlers.iterator();

            while (var6.hasNext()) {
                var7 = var6.next();

                if (var7.handleInboundPacket(var4, this.username, this.uuid, list)) {
                    return;
                }
            }
        }

        var6 = PacketHandlers.generalHandlers.iterator();

        do {
            if (!var6.hasNext()) {
                list.add(packet);
                return;
            }

            var7 = var6.next();
        } while (!var7.handleInboundPacket(var4, this.username, this.uuid, list));
    }
}