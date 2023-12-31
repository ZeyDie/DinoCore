package org.bukkit.craftbukkit.v1_6_R3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Player")
public class CraftOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {
    private final String name;
    private final CraftServer server;
    private final net.minecraft.world.storage.SaveHandler storage;

    protected CraftOfflinePlayer(final CraftServer server, final String name) {
        this.server = server;
        this.name = name;
        this.storage = (net.minecraft.world.storage.SaveHandler) (server.console.worlds.get(0).getSaveHandler());
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public String getName() {
        return name;
    }

    public Server getServer() {
        return server;
    }

    public boolean isOp() {
        return server.getHandle().isPlayerOpped(getName().toLowerCase());
    }

    public void setOp(final boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().addOp(getName().toLowerCase());
        } else {
            server.getHandle().removeOp(getName().toLowerCase());
        }
    }

    public boolean isBanned() {
        return server.getHandle().getBannedPlayers().isBanned(name.toLowerCase());
    }

    public void setBanned(final boolean value) {
        if (value) {
            final net.minecraft.server.management.BanEntry entry = new net.minecraft.server.management.BanEntry(name.toLowerCase());
            server.getHandle().getBannedPlayers().put(entry);
        } else {
            server.getHandle().getBannedPlayers().remove(name.toLowerCase());
        }

        server.getHandle().getBannedPlayers().saveToFileWithHeader();
    }

    public boolean isWhitelisted() {
        return server.getHandle().getWhiteListedPlayers().contains(name.toLowerCase());
    }

    public void setWhitelisted(final boolean value) {
        if (value) {
            server.getHandle().addToWhiteList(name.toLowerCase());
        } else {
            server.getHandle().removeFromWhitelist(name.toLowerCase());
        }
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", name);

        return result;
    }

    public static OfflinePlayer deserialize(final Map<String, Object> args) {
        return Bukkit.getServer().getOfflinePlayer((String) args.get("name"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + "]";
    }

    public Player getPlayer() {
        for (final Object obj : server.getHandle().playerEntityList) {
            final net.minecraft.entity.player.EntityPlayerMP player = (net.minecraft.entity.player.EntityPlayerMP) obj;
            if (player.getCommandSenderName().equalsIgnoreCase(getName())) {
                return (player.playerNetServerHandler != null) ? player.playerNetServerHandler.getPlayerB() : null;
            }
        }

        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        final OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }
        return this.getName().equalsIgnoreCase(other.getName());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.getName() != null ? this.getName().toLowerCase().hashCode() : 0);
        return hash;
    }

    private net.minecraft.nbt.NBTTagCompound getData() {
        return storage.getPlayerData(getName());
    }

    private net.minecraft.nbt.NBTTagCompound getBukkitData() {
        net.minecraft.nbt.NBTTagCompound result = getData();

        if (result != null) {
            if (!result.hasKey("bukkit")) {
                result.setCompoundTag("bukkit", new net.minecraft.nbt.NBTTagCompound());
            }
            result = result.getCompoundTag("bukkit");
        }

        return result;
    }

    private File getDataFile() {
        return new File(storage.getPlayerDir(), name + ".dat");
    }

    public long getFirstPlayed() {
        final Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();

        final net.minecraft.nbt.NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.hasKey("firstPlayed")) {
                return data.getLong("firstPlayed");
            } else {
                final File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    public long getLastPlayed() {
        final Player player = getPlayer();
        if (player != null) return player.getLastPlayed();

        final net.minecraft.nbt.NBTTagCompound data = getBukkitData();

        if (data != null) {
            if (data.hasKey("lastPlayed")) {
                return data.getLong("lastPlayed");
            } else {
                final File file = getDataFile();
                return file.lastModified();
            }
        } else {
            return 0;
        }
    }

    public boolean hasPlayedBefore() {
        return getData() != null;
    }

    public Location getBedSpawnLocation() {
        final net.minecraft.nbt.NBTTagCompound data = getData();
        if (data == null) return null;

        if (data.hasKey("SpawnX") && data.hasKey("SpawnY") && data.hasKey("SpawnZ")) {
            String spawnWorld = data.getString("SpawnWorld");
            if (spawnWorld.isEmpty()) {
                spawnWorld = server.getWorlds().get(0).getName();
            }
            return new Location(server.getWorld(spawnWorld), data.getInteger("SpawnX"), data.getInteger("SpawnY"), data.getInteger("SpawnZ"));
        }
        return null;
    }

    public void setMetadata(final String metadataKey, final MetadataValue metadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, metadataValue);
    }

    public List<MetadataValue> getMetadata(final String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(final String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    public void removeMetadata(final String metadataKey, final Plugin plugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, plugin);
    }
}
