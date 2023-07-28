package org.bukkit.craftbukkit.v1_6_R3.chunkio;


class QueuedChunk {
    final int x;
    final int z;
    final net.minecraft.world.chunk.storage.AnvilChunkLoader loader;
    final net.minecraft.world.World world;
    final net.minecraft.world.gen.ChunkProviderServer provider;
    net.minecraft.nbt.NBTTagCompound compound;

    public QueuedChunk(final int x, final int z, final net.minecraft.world.chunk.storage.AnvilChunkLoader loader, final net.minecraft.world.World world, final net.minecraft.world.gen.ChunkProviderServer provider) {
        this.x = x;
        this.z = z;
        this.loader = loader;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        return (x * 31 + z * 29) ^ world.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof QueuedChunk) {
            final QueuedChunk other = (QueuedChunk) object;
            return x == other.x && z == other.z && world == other.world;
        }

        return false;
    }

    // Cauldron start - add more debug info
    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();
        final String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName()).append(" {").append(NEW_LINE);
        result.append(" x: ").append(x).append(NEW_LINE);
        result.append(" z: ").append(z).append(NEW_LINE);
        result.append(" loader: ").append(loader).append(NEW_LINE);
        result.append(" world: ").append(world.getWorldInfo().getWorldName()).append(NEW_LINE);
        result.append(" dimension: ").append(world.provider.dimensionId).append(NEW_LINE);
        result.append(" provider: ").append(world.provider.getClass().getName()).append(NEW_LINE);
        result.append("}");

        return result.toString();
    }
    // Cauldron end
}
