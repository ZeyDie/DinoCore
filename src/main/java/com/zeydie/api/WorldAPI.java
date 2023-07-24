package com.zeydie.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public final class WorldAPI {
    private net.minecraft.world.World world;
    private WorldInfo worldInfo;

    public WorldAPI(@NotNull final Object object) {
        if (object instanceof net.minecraft.world.World)
            this.world = (net.minecraft.world.World) object;
        else if (object instanceof org.bukkit.World)
            this.world = DimensionManager.getWorld(((org.bukkit.World) object).getName());

        if (this.world != null)
            this.worldInfo = this.world.getWorldInfo();
    }

    public boolean hasOwnerBlock(
            final int x,
            final int y,
            final int z
    ) {
        if (this.worldInfo == null) return false;

        return this.worldInfo.containsBlock(x, y, z);
    }

    public @Nullable String getOwnerBlock(
            final int x,
            final int y,
            final int z
    ) {
        if (this.worldInfo == null) return null;

        return this.worldInfo.getPlayerOfBlock(x, y, z);
    }

    public void replaceOwnerBlock(
            @NotNull final String player,
            final int x,
            final int y,
            final int z
    ) {
        if (this.worldInfo == null) return;

        this.worldInfo.updateBlocksPlayers(
                player,
                false,
                x,
                y,
                z
        );
    }
}
