package ru.zoom4ikdan4ik.legacy.core.waitables;

import net.minecraft.network.NetServerHandler;
import org.bukkit.craftbukkit.v1_6_R3.util.Waitable;

public final class WaitableChatMessage extends Waitable {
    private final NetServerHandler netServerHandler;
    private final String message;

    public WaitableChatMessage(final NetServerHandler netServerHandler, final String message) {
        this.netServerHandler = netServerHandler;
        this.message = message;
    }

    @Override
    protected Object evaluate() {
        this.netServerHandler.kickPlayerFromServer(this.message);
        return null;
    }
}
