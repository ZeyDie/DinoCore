package com.zeydie.legacy.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerInstance;

public final class PlayerRunnableLoadChunk implements Runnable {
    private final EntityPlayerMP par1EntityPlayerMP;
    private final PlayerInstance playerInstance;

    public PlayerRunnableLoadChunk(final EntityPlayerMP par1EntityPlayerMP, final PlayerInstance playerInstance) {
        this.par1EntityPlayerMP = par1EntityPlayerMP;
        this.playerInstance = playerInstance;
    }

    @Override
    public void run() {
        this.par1EntityPlayerMP.loadedChunks.add(this.playerInstance.chunkLocation);
    }
}
