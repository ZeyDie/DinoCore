package ru.zoom4ikdan4ik.legacy.core.waitables;

import net.minecraft.network.ThreadLoginVerifier;
import org.bukkit.craftbukkit.v1_6_R3.util.Waitable;
import org.bukkit.event.player.PlayerPreLoginEvent;

public final class WaitablePlayerPreLogin extends Waitable<PlayerPreLoginEvent.Result> {
    private final ThreadLoginVerifier threadLoginVerifier;
    private final PlayerPreLoginEvent playerPreLoginEvent;

    public WaitablePlayerPreLogin(final ThreadLoginVerifier threadLoginVerifier, final PlayerPreLoginEvent playerPreLoginEvent) {
        this.threadLoginVerifier = threadLoginVerifier;
        this.playerPreLoginEvent = playerPreLoginEvent;
    }

    @Override
    public PlayerPreLoginEvent.Result evaluate() {
        this.threadLoginVerifier.server.getPluginManager().callEvent(this.playerPreLoginEvent);

        return this.playerPreLoginEvent.getResult();
    }
}
