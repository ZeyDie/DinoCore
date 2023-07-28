package org.bukkit.craftbukkit.v1_6_R3.util;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class LazyPlayerSet extends LazyHashSet<Player> {

    @Override
    HashSet<Player> makeReference() {
        if (reference != null) {
            throw new IllegalStateException("Reference already created!");
        }
        final List<net.minecraft.entity.player.EntityPlayerMP> players = net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        final HashSet<Player> reference = new HashSet<Player>(players.size());
        for (final net.minecraft.entity.player.EntityPlayerMP player : players) {
            reference.add(player.getBukkitEntity());
        }
        return reference;
    }

}
