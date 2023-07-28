package org.bukkit.craftbukkit.v1_6_R3.chunkio;

import org.bukkit.craftbukkit.v1_6_R3.util.AsynchronousExecutor;

public class ChunkIOExecutor {
    static final int BASE_THREADS = 1;
    static final int PLAYERS_PER_THREAD = 50;

    private static final AsynchronousExecutor<QueuedChunk, net.minecraft.world.chunk.Chunk, Runnable, RuntimeException> instance = new AsynchronousExecutor<QueuedChunk, net.minecraft.world.chunk.Chunk, Runnable, RuntimeException>(new ChunkIOProvider(), BASE_THREADS);

    public static net.minecraft.world.chunk.Chunk syncChunkLoad(final net.minecraft.world.World world, final net.minecraft.world.chunk.storage.AnvilChunkLoader loader, final net.minecraft.world.gen.ChunkProviderServer provider, final int x, final int z) {
        return instance.getSkipQueue(new QueuedChunk(x, z, loader, world, provider));
    }

    public static void queueChunkLoad(final net.minecraft.world.World world, final net.minecraft.world.chunk.storage.AnvilChunkLoader loader, final net.minecraft.world.gen.ChunkProviderServer provider, final int x, final int z, final Runnable runnable) {
        instance.add(new QueuedChunk(x, z, loader, world, provider), runnable);
    }

    // Abuses the fact that hashCode and equals for QueuedChunk only use world and coords
    public static void dropQueuedChunkLoad(final net.minecraft.world.World world, final int x, final int z, final Runnable runnable) {
        instance.drop(new QueuedChunk(x, z, null, world, null), runnable);
    }

    public static void adjustPoolSize(final int players) {
        final int size = Math.max(BASE_THREADS, (int) Math.ceil(players / PLAYERS_PER_THREAD));
        instance.setActiveThreads(size);
    }

    public static void tick() {
        instance.finishActive();
    }
}
