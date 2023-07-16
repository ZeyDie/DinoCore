package com.zeydie.netty.packets;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PacketHandlers {
    public static List<PacketHandler> generalHandlers = new ArrayList<PacketHandler>();

    public static Map<Integer, List<PacketHandler>> specificHandlers = new HashMap<Integer, List<PacketHandler>>();

    public static void regPacketHandler(@NotNull final PacketHandler paramPacketHandler) {
        generalHandlers.add(paramPacketHandler);
    }

    public static void regPacketHandler(
            final int paramInt,
            @NotNull final PacketHandler paramPacketHandler
    ) {
        List<PacketHandler> list = specificHandlers.get(paramInt);

        if (list == null) {
            list = new ArrayList<>();
            specificHandlers.put(paramInt, list);
        }

        list.add(paramPacketHandler);
    }

    public interface PacketHandler {
        boolean handleInboundPacket(
                @NotNull final PacketWithBytes packetWithBytes,
                @NotNull final String username,
                @NotNull final UUID uuid,
                @NotNull final List<Object> list
        );

        boolean handleOutboundPacket(
                @NotNull final PacketWithBytes packetWithBytes,
                @NotNull final String username,
                @NotNull final UUID uuid,
                @NotNull final List<Object> list
        );
    }
}
