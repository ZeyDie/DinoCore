package com.zeydie.legacy.core;

import net.minecraft.server.management.PlayerInstance;

public final class PlayerInstanceRunnable implements Runnable {
    private final PlayerInstance playerInstance;

    public PlayerInstanceRunnable(final PlayerInstance playerInstance) {
        this.playerInstance = playerInstance;
    }

    @Override
    public void run() {
        this.playerInstance.loaded = true;
    }
}
