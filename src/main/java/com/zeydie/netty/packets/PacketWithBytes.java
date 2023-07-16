package com.zeydie.netty.packets;

import lombok.Data;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Data
public final class PacketWithBytes {
    @NotNull
    private final Packet packet;
    private final byte[] bytes;

    public PacketWithBytes(
            @NotNull final Packet packet,
            final byte[] bytes
    ) {
        this.packet = packet;
        this.bytes = bytes;
    }
}
