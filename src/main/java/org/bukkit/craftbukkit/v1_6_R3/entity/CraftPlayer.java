package org.bukkit.craftbukkit.v1_6_R3.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.zeydie.settings.optimization.CoreSettings;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.v1_6_R3.*;
import org.bukkit.craftbukkit.v1_6_R3.conversations.ConversationTracker;
import org.bukkit.craftbukkit.v1_6_R3.map.CraftMapView;
import org.bukkit.craftbukkit.v1_6_R3.map.RenderData;
import org.bukkit.craftbukkit.v1_6_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player {
    private long firstPlayed = 0;
    private long lastPlayed = 0;
    private boolean hasPlayedBefore = false;
    private final ConversationTracker conversationTracker = new ConversationTracker();
    private final Set<String> channels = new HashSet<String>();
    private final Map<String, Player> hiddenPlayers = new MapMaker().softValues().makeMap();
    private int hash = 0;
    private double health = 20;
    private boolean scaledHealth = false;
    private double healthScale = 20;

    public CraftPlayer(final CraftServer server, final net.minecraft.entity.player.EntityPlayerMP entity) {
        super(server, entity);

        firstPlayed = System.currentTimeMillis();
    }

    @Override
    public boolean isOp() {
        return server.getHandle().isPlayerOpped(getName());
    }

    @Override
    public void setOp(final boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().addOp(getName());
        } else {
            server.getHandle().removeOp(getName());
        }

        perm.recalculatePermissions();
    }

    public boolean isOnline() {
        for (final Object obj : server.getHandle().playerEntityList) {
            final net.minecraft.entity.player.EntityPlayerMP player = (net.minecraft.entity.player.EntityPlayerMP) obj;
            if (player.getCommandSenderName().equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    public InetSocketAddress getAddress() {
        if (getHandle().playerNetServerHandler == null) return null;

        final SocketAddress addr = getHandle().playerNetServerHandler.netManager.getSocketAddress();
        if (addr instanceof InetSocketAddress) {
            return (InetSocketAddress) addr;
        } else {
            return null;
        }
    }

    @Override
    public double getEyeHeight() {
        return getEyeHeight(false);
    }

    @Override
    public double getEyeHeight(final boolean ignoreSneaking) {
        if (ignoreSneaking) {
            return 1.62D;
        } else {
            if (isSneaking()) {
                return 1.54D;
            } else {
                return 1.62D;
            }
        }
    }

    public void sendRawMessage(final String message) {
        if (getHandle().playerNetServerHandler == null) return;

        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet3Chat(net.minecraft.util.ChatMessageComponent.createFromText(message)));
    }

    public void sendMessage(final String message) {
        if (!conversationTracker.isConversingModaly()) {
            this.sendRawMessage(message);
        }
    }

    public void sendMessage(final String[] messages) {
        for (final String message : messages) {
            sendMessage(message);
        }
    }

    public String getDisplayName() {
        return getHandle().cb_displayName;
    }

    public void setDisplayName(final String name) {
        getHandle().cb_displayName = name;
    }

    public String getPlayerListName() {
        return getHandle().listName;
    }

    public void setPlayerListName(String name) {
        String name1 = name;
        final String oldName = getHandle().listName;

        if (name1 == null) {
            name1 = getName();
        }

        if (oldName.equals(name1)) {
            return;
        }

        if (name1.length() > 16) {
            throw new IllegalArgumentException("Player list names can only be a maximum of 16 characters long");
        }

        // Collisions will make for invisible people
        for (int i = 0; i < server.getHandle().playerEntityList.size(); ++i) {
            if (((net.minecraft.entity.player.EntityPlayerMP) server.getHandle().playerEntityList.get(i)).listName.equals(name1)) {
                throw new IllegalArgumentException(name1 + " is already assigned as a player list name for someone");
            }
        }

        getHandle().listName = name1;

        // Change the name on the client side
        final net.minecraft.network.packet.Packet201PlayerInfo oldpacket = new net.minecraft.network.packet.Packet201PlayerInfo(oldName, false, 9999);
        final net.minecraft.network.packet.Packet201PlayerInfo packet = new net.minecraft.network.packet.Packet201PlayerInfo(name1, true, getHandle().ping);
        for (int i = 0; i < server.getHandle().playerEntityList.size(); ++i) {
            final net.minecraft.entity.player.EntityPlayerMP entityplayer = (net.minecraft.entity.player.EntityPlayerMP) server.getHandle().playerEntityList.get(i);
            if (entityplayer.playerNetServerHandler == null) continue;

            if (entityplayer.getBukkitEntity().canSee(this)) {
                entityplayer.playerNetServerHandler.sendPacketToPlayer(oldpacket);
                entityplayer.playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof OfflinePlayer)) {
            return false;
        }
        final OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }

        final boolean nameEquals = this.getName().equalsIgnoreCase(other.getName());
        boolean idEquals = true;

        if (other instanceof CraftPlayer) {
            idEquals = this.getEntityId() == ((CraftPlayer) other).getEntityId();
        }

        return nameEquals && idEquals;
    }

    public void kickPlayer(final String message) {
        //TODO ZoomCodeStart
        if (CoreSettings.getInstance().getSettings().isAsynchronousWarnings())
            //TODO ZoomCodeEnd

            if (Thread.currentThread() != net.minecraft.server.MinecraftServer.getServer().primaryThread)
                throw new IllegalStateException("Asynchronous player kick!"); // Spigot
        if (getHandle().playerNetServerHandler == null) return;

        getHandle().playerNetServerHandler.kickPlayerFromServer(message == null ? "" : message);
    }

    public void setCompassTarget(final Location loc) {
        if (getHandle().playerNetServerHandler == null) return;

        // Do not directly assign here, from the packethandler we'll assign it.
        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet6SpawnPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public Location getCompassTarget() {
        return getHandle().compassTarget;
    }

    public void chat(final String msg) {
        if (getHandle().playerNetServerHandler == null) return;

        getHandle().playerNetServerHandler.chat(msg, false);
    }

    public boolean performCommand(final String command) {
        return server.dispatchCommand(this, command);
    }

    public void playNote(final Location loc, final byte instrument, final byte note) {
        if (getHandle().playerNetServerHandler == null) return;

        final int id = getHandle().worldObj.getBlockId(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet54PlayNoteBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, instrument, note));
    }

    public void playNote(final Location loc, final Instrument instrument, final Note note) {
        if (getHandle().playerNetServerHandler == null) return;

        final int id = getHandle().worldObj.getBlockId(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet54PlayNoteBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), id, instrument.getType(), note.getId()));
    }

    public void playSound(final Location loc, final Sound sound, final float volume, final float pitch) {
        if (sound == null) {
            return;
        }
        playSound(loc, CraftSound.getSound(sound), volume, pitch);
    }

    public void playSound(final Location loc, final String sound, final float volume, final float pitch) {
        if (loc == null || sound == null || getHandle().playerNetServerHandler == null) return;

        final double x = loc.getBlockX() + 0.5;
        final double y = loc.getBlockY() + 0.5;
        final double z = loc.getBlockZ() + 0.5;

        final net.minecraft.network.packet.Packet62LevelSound packet = new net.minecraft.network.packet.Packet62LevelSound(sound, x, y, z, volume, pitch);
        getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
    }

    // Spigot start
    public void playEffect(final Location location, final Effect effect, final int data) {
        spigot().playEffect(location, effect, data, 0, 0.0f, 0.0f, 0.0f, 1.0f, 1, 64);
    }
    // Spigot end

    public <T> void playEffect(final Location loc, final Effect effect, final T data) {
        if (data != null) {
            Validate.isTrue(data.getClass().equals(effect.getData()), "Wrong kind of data for this effect!");
        } else {
            Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
        }
        if (data != null && data.getClass().equals(org.bukkit.material.MaterialData.class)) {
            final org.bukkit.material.MaterialData materialData = (org.bukkit.material.MaterialData) data;
            Validate.isTrue(!materialData.getItemType().isBlock(), "Material must be block");
            spigot().playEffect(loc, effect, materialData.getItemType().getId(), materialData.getData(), 0, 0, 0, 1, 1, 64);
        } else {
            final int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
            playEffect(loc, effect, datavalue);
        }
    }

    public void sendBlockChange(final Location loc, final Material material, final byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    public void sendBlockChange(final Location loc, final int material, final byte data) {
        if (getHandle().playerNetServerHandler == null) return;

        final net.minecraft.network.packet.Packet53BlockChange packet = new net.minecraft.network.packet.Packet53BlockChange(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), ((CraftWorld) loc.getWorld()).getHandle());

        packet.type = material;
        packet.metadata = data;
        getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
    }

    public boolean sendChunkChange(final Location loc, final int sx, final int sy, final int sz, final byte[] data) {
        if (getHandle().playerNetServerHandler == null) return false;

        /*
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int cx = x >> 4;
        int cz = z >> 4;

        if (sx <= 0 || sy <= 0 || sz <= 0) {
            return false;
        }

        if ((x + sx - 1) >> 4 != cx || (z + sz - 1) >> 4 != cz || y < 0 || y + sy > 128) {
            return false;
        }

        if (data.length != (sx * sy * sz * 5) / 2) {
            return false;
        }

        Packet51MapChunk packet = new Packet51MapChunk(x, y, z, sx, sy, sz, data);

        getHandle().playerConnection.sendPacket(packet);

        return true;
        */

        throw new NotImplementedException("Chunk changes do not yet work"); // TODO: Chunk changes.
    }

    public void sendMap(final MapView map) {
        if (getHandle().playerNetServerHandler == null) return;

        final RenderData data = ((CraftMapView) map).render(this);
        for (int x = 0; x < 128; ++x) {
            final byte[] bytes = new byte[131];
            bytes[1] = (byte) x;
            for (int y = 0; y < 128; ++y) {
                bytes[y + 3] = data.buffer[y * 128 + x];
            }
            final net.minecraft.network.packet.Packet131MapData packet = new net.minecraft.network.packet.Packet131MapData((short) Material.MAP.getId(), map.getId(), bytes);
            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    @Override
    public boolean teleport(final Location location, final PlayerTeleportEvent.TeleportCause cause) {
        final net.minecraft.entity.player.EntityPlayerMP entity = getHandle();

        if (getHealth() == 0 || entity.isDead) {
            return false;
        }

        if (entity.playerNetServerHandler == null || entity.playerNetServerHandler.disconnected) {
            return false;
        }

        if (entity.ridingEntity != null || entity.riddenByEntity != null) {
            return false;
        }

        // From = Players current Location
        Location from = this.getLocation();
        // To = Players new Location if Teleport is Successful
        Location to = location;
        // Create & Call the Teleport Event.
        final PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
        server.getPluginManager().callEvent(event);

        // Return False to inform the Plugin that the Teleport was unsuccessful/cancelled.
        if (event.isCancelled()) {
            return false;
        }

        // Update the From Location
        from = event.getFrom();
        // Grab the new To Location dependent on whether the event was cancelled.
        to = event.getTo();
        // Grab the To and From World Handles.
        final net.minecraft.world.WorldServer fromWorld = ((CraftWorld) from.getWorld()).getHandle();
        final net.minecraft.world.WorldServer toWorld = ((CraftWorld) to.getWorld()).getHandle();

        // Close any foreign inventory
        if (getHandle().openContainer != getHandle().inventoryContainer) {
            getHandle().closeScreen();
        }

        // Check if the fromWorld and toWorld are the same.
        if (fromWorld == toWorld) {
            entity.playerNetServerHandler.teleport(to);
        } else {
            server.getHandle().respawnPlayer(entity, toWorld.provider.dimensionId, false, to); // Cauldron
        }
        return true;
    }

    public void setSneaking(final boolean sneak) {
        getHandle().setSneaking(sneak);
    }

    public boolean isSneaking() {
        return getHandle().isSneaking();
    }

    public boolean isSprinting() {
        return getHandle().isSprinting();
    }

    public void setSprinting(final boolean sprinting) {
        getHandle().setSprinting(sprinting);
    }

    public void loadData() {
        server.getHandle().playerNBTManagerObj.readPlayerData(getHandle());
    }

    public void saveData() {
        server.getHandle().playerNBTManagerObj.writePlayerData(getHandle());
    }

    @Deprecated
    public void updateInventory() {
        getHandle().sendContainerToPlayer(getHandle().openContainer);
    }

    public void setSleepingIgnored(final boolean isSleeping) {
        getHandle().fauxSleeping = isSleeping;
        ((CraftWorld) getWorld()).getHandle().checkSleepStatus();
    }

    public boolean isSleepingIgnored() {
        return getHandle().fauxSleeping;
    }

    public void awardAchievement(final Achievement achievement) {
        sendStatistic(achievement.getId(), 1);
    }

    public void incrementStatistic(final Statistic statistic) {
        incrementStatistic(statistic, 1);
    }

    public void incrementStatistic(final Statistic statistic, final int amount) {
        sendStatistic(statistic.getId(), amount);
    }

    public void incrementStatistic(final Statistic statistic, final Material material) {
        incrementStatistic(statistic, material, 1);
    }

    public void incrementStatistic(final Statistic statistic, final Material material, final int amount) {
        if (!statistic.isSubstatistic()) {
            throw new IllegalArgumentException("Given statistic is not a substatistic");
        }
        if (statistic.isBlock() != material.isBlock()) {
            throw new IllegalArgumentException("Given material is not valid for this substatistic");
        }

        int mat = material.getId();

        if (!material.isBlock()) {
            mat -= 255;
        }

        sendStatistic(statistic.getId() + mat, amount);
    }

    private void sendStatistic(final int id, int amount) {
        int amount1 = amount;
        if (getHandle().playerNetServerHandler == null) return;

        while (amount1 > Byte.MAX_VALUE) {
            sendStatistic(id, Byte.MAX_VALUE);
            amount1 -= Byte.MAX_VALUE;
        }

        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet200Statistic(id, amount1));
    }

    public void setPlayerTime(final long time, final boolean relative) {
        getHandle().timeOffset = time;
        getHandle().relativeTime = relative;
    }

    public long getPlayerTimeOffset() {
        return getHandle().timeOffset;
    }

    public long getPlayerTime() {
        return getHandle().getPlayerTime();
    }

    public boolean isPlayerTimeRelative() {
        return getHandle().relativeTime;
    }

    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    public void setPlayerWeather(final WeatherType type) {
        getHandle().setPlayerWeather(type, true);
    }

    public WeatherType getPlayerWeather() {
        return getHandle().getPlayerWeather();
    }

    public void resetPlayerWeather() {
        getHandle().resetPlayerWeather();
    }

    public boolean isBanned() {
        return server.getHandle().getBannedPlayers().isBanned(getName().toLowerCase());
    }

    public void setBanned(final boolean value) {
        if (value) {
            final net.minecraft.server.management.BanEntry entry = new net.minecraft.server.management.BanEntry(getName().toLowerCase());
            server.getHandle().getBannedPlayers().put(entry);
        } else {
            server.getHandle().getBannedPlayers().remove(getName().toLowerCase());
        }

        server.getHandle().getBannedPlayers().saveToFileWithHeader();
    }

    public boolean isWhitelisted() {
        return server.getHandle().getWhiteListedPlayers().contains(getName().toLowerCase());
    }

    public void setWhitelisted(final boolean value) {
        if (value) {
            server.getHandle().addToWhiteList(getName().toLowerCase());
        } else {
            server.getHandle().removeFromWhitelist(getName().toLowerCase());
        }
    }

    @Override
    public void setGameMode(final GameMode mode) {
        if (getHandle().playerNetServerHandler == null) return;

        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        if (mode != getGameMode()) {
            final PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, mode);
            server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            getHandle().theItemInWorldManager.setGameType(net.minecraft.world.EnumGameType.getByID(mode.getValue()));
            getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet70GameEvent(3, mode.getValue()));
        }
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.getByValue(getHandle().theItemInWorldManager.getGameType().getID());
    }

    public void giveExp(final int exp) {
        getHandle().addExperience(exp);
    }

    public void giveExpLevels(final int levels) {
        getHandle().addExperienceLevel(levels);
    }

    public float getExp() {
        return getHandle().experience;
    }

    public void setExp(final float exp) {
        getHandle().experience = exp;
        getHandle().lastExperience = -1;
    }

    public int getLevel() {
        return getHandle().experienceLevel;
    }

    public void setLevel(final int level) {
        getHandle().experienceLevel = level;
        getHandle().lastExperience = -1;
    }

    public int getTotalExperience() {
        return getHandle().experienceTotal;
    }

    public void setTotalExperience(final int exp) {
        getHandle().experienceTotal = exp;
    }

    public float getExhaustion() {
        return getHandle().getFoodStats().foodExhaustionLevel;
    }

    public void setExhaustion(final float value) {
        getHandle().getFoodStats().foodExhaustionLevel = value;
    }

    public float getSaturation() {
        return getHandle().getFoodStats().foodSaturationLevel;
    }

    public void setSaturation(final float value) {
        getHandle().getFoodStats().foodSaturationLevel = value;
    }

    public int getFoodLevel() {
        return getHandle().getFoodStats().foodLevel;
    }

    public void setFoodLevel(final int value) {
        getHandle().getFoodStats().foodLevel = value;
    }

    public Location getBedSpawnLocation() {
        final World world = getServer().getWorld(getHandle().spawnWorld);
        net.minecraft.util.ChunkCoordinates bed = getHandle().getBedLocation();

        if (world != null && bed != null) {
            bed = net.minecraft.entity.player.EntityPlayer.verifyRespawnCoordinates(((CraftWorld) world).getHandle(), bed, getHandle().isSpawnForced());
            if (bed != null) {
                return new Location(world, bed.posX, bed.posY, bed.posZ);
            }
        }
        return null;
    }

    public void setBedSpawnLocation(final Location location) {
        setBedSpawnLocation(location, false);
    }

    public void setBedSpawnLocation(final Location location, final boolean override) {
        if (location == null) {
            getHandle().setSpawnChunk(null, override);
        } else {
            getHandle().setSpawnChunk(new net.minecraft.util.ChunkCoordinates(location.getBlockX(), location.getBlockY(), location.getBlockZ()), override);
            getHandle().spawnWorld = location.getWorld().getName();
        }
    }

    public void hidePlayer(final Player player) {
        Validate.notNull(player, "hidden player cannot be null");
        if (getHandle().playerNetServerHandler == null) return;
        if (equals(player)) return;
        if (hiddenPlayers.containsKey(player.getName())) return;
        hiddenPlayers.put(player.getName(), player);

        //remove this player from the hidden player's EntityTrackerEntry
        final net.minecraft.entity.EntityTracker tracker = ((net.minecraft.world.WorldServer) entity.worldObj).theEntityTracker;
        final net.minecraft.entity.player.EntityPlayerMP other = ((CraftPlayer) player).getHandle();
        final net.minecraft.entity.EntityTrackerEntry entry = (net.minecraft.entity.EntityTrackerEntry) tracker.trackedEntityIDs.lookup(other.entityId);
        if (entry != null) {
            entry.removePlayerFromTracker(getHandle());
        }

        //remove the hidden player from this player user list
        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet201PlayerInfo(player.getPlayerListName(), false, 9999));
    }

    public void showPlayer(final Player player) {
        Validate.notNull(player, "shown player cannot be null");
        if (getHandle().playerNetServerHandler == null) return;
        if (equals(player)) return;
        if (!hiddenPlayers.containsKey(player.getName())) return;
        hiddenPlayers.remove(player.getName());

        final net.minecraft.entity.EntityTracker tracker = ((net.minecraft.world.WorldServer) entity.worldObj).theEntityTracker;
        final net.minecraft.entity.player.EntityPlayerMP other = ((CraftPlayer) player).getHandle();
        final net.minecraft.entity.EntityTrackerEntry entry = (net.minecraft.entity.EntityTrackerEntry) tracker.trackedEntityIDs.lookup(other.entityId);
        if (entry != null && !entry.trackingPlayers.contains(getHandle())) {
            entry.tryStartWachingThis(getHandle());
        }

        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet201PlayerInfo(player.getPlayerListName(), true, getHandle().ping));
    }

    public boolean canSee(final Player player) {
        return !hiddenPlayers.containsKey(player.getName());
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", getName());

        return result;
    }

    public Player getPlayer() {
        return this;
    }

    @Override
    public net.minecraft.entity.player.EntityPlayerMP getHandle() {
        return (net.minecraft.entity.player.EntityPlayerMP) entity;
    }

    public void setHandle(final net.minecraft.entity.player.EntityPlayerMP entity) {
        super.setHandle(entity);
    }

    @Override
    public String toString() {
        return "CraftPlayer{" + "name=" + getName() + '}';
    }

    @Override
    public int hashCode() {
        if (hash == 0 || hash == 485) {
            hash = 97 * 5 + (this.getName() != null ? this.getName().toLowerCase().hashCode() : 0);
        }
        return hash;
    }

    public long getFirstPlayed() {
        return firstPlayed;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    public void setFirstPlayed(final long firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public void readExtraData(final net.minecraft.nbt.NBTTagCompound nbttagcompound) {
        hasPlayedBefore = true;
        if (nbttagcompound.hasKey("bukkit")) {
            final net.minecraft.nbt.NBTTagCompound data = nbttagcompound.getCompoundTag("bukkit");

            if (data.hasKey("firstPlayed")) {
                firstPlayed = data.getLong("firstPlayed");
                lastPlayed = data.getLong("lastPlayed");
            }

            if (data.hasKey("newExp")) {
                final net.minecraft.entity.player.EntityPlayerMP handle = getHandle();
                handle.newExp = data.getInteger("newExp");
                handle.newTotalExp = data.getInteger("newTotalExp");
                handle.newLevel = data.getInteger("newLevel");
                handle.expToDrop = data.getInteger("expToDrop");
                handle.keepLevel = data.getBoolean("keepLevel");
            }
        }
    }

    public void setExtraData(final net.minecraft.nbt.NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.hasKey("bukkit")) {
            nbttagcompound.setCompoundTag("bukkit", new net.minecraft.nbt.NBTTagCompound());
        }

        final net.minecraft.nbt.NBTTagCompound data = nbttagcompound.getCompoundTag("bukkit");
        final net.minecraft.entity.player.EntityPlayerMP handle = getHandle();
        data.setInteger("newExp", handle.newExp);
        data.setInteger("newTotalExp", handle.newTotalExp);
        data.setInteger("newLevel", handle.newLevel);
        data.setInteger("expToDrop", handle.expToDrop);
        data.setBoolean("keepLevel", handle.keepLevel);
        data.setLong("firstPlayed", getFirstPlayed());
        data.setLong("lastPlayed", System.currentTimeMillis());
    }

    public boolean beginConversation(final Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    public void abandonConversation(final Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    public void abandonConversation(final Conversation conversation, final ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    public void acceptConversationInput(final String input) {
        conversationTracker.acceptConversationInput(input);
    }

    public boolean isConversing() {
        return conversationTracker.isConversing();
    }

    public void sendPluginMessage(final Plugin source, final String channel, final byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        if (getHandle().playerNetServerHandler == null) return;

        if (channels.contains(channel)) {
            final net.minecraft.network.packet.Packet250CustomPayload packet = new net.minecraft.network.packet.Packet250CustomPayload();
            packet.channel = channel;
            packet.length = message.length;
            packet.data = message;
            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    public void setTexturePack(final String url) {
        Validate.notNull(url, "Texture pack URL cannot be null");

        final byte[] message = (url + "\0" + "16").getBytes();
        Validate.isTrue(message.length <= Messenger.MAX_MESSAGE_SIZE, "Texture pack URL is too long");

        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet250CustomPayload("MC|TPack", message));
    }

    public void addChannel(final String channel) {
        if (channels.add(channel)) {
            server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
        }
    }

    public void removeChannel(final String channel) {
        if (channels.remove(channel)) {
            server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
        }
    }

    public Set<String> getListeningPluginChannels() {
        return ImmutableSet.copyOf(channels);
    }

    public void sendSupportedChannels() {
        if (getHandle().playerNetServerHandler == null) return;
        final Set<String> listening = server.getMessenger().getIncomingChannels();

        if (!listening.isEmpty()) {
            final net.minecraft.network.packet.Packet250CustomPayload packet = new net.minecraft.network.packet.Packet250CustomPayload();

            packet.channel = "REGISTER";
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();

            for (final String channel : listening) {
                try {
                    stream.write(channel.getBytes(StandardCharsets.UTF_8));
                    stream.write((byte) 0);
                } catch (final IOException ex) {
                    Logger.getLogger(CraftPlayer.class.getName()).log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + getName(), ex);
                }
            }

            packet.data = stream.toByteArray();
            packet.length = packet.data.length;

            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void setMetadata(final String metadataKey, final MetadataValue newMetadataValue) {
        server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(final String metadataKey) {
        return server.getPlayerMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(final String metadataKey) {
        return server.getPlayerMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(final String metadataKey, final Plugin owningPlugin) {
        server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public boolean setWindowProperty(final Property prop, final int value) {
        final net.minecraft.inventory.Container container = getHandle().openContainer;
        if (container.getBukkitView().getType() != prop.getType()) {
            return false;
        }
        getHandle().sendProgressBarUpdate(container, prop.getId(), value);
        return true;
    }

    public void disconnect(final String reason) {
        conversationTracker.abandonAllConversations();
        perm.clearPermissions();
    }

    public boolean isFlying() {
        return getHandle().capabilities.isFlying;
    }

    public void setFlying(final boolean value) {
        if (!getAllowFlight() && value) {
            throw new IllegalArgumentException("Cannot make player fly if getAllowFlight() is false");
        }

        getHandle().capabilities.isFlying = value;
        getHandle().sendPlayerAbilities();
    }

    public boolean getAllowFlight() {
        return getHandle().capabilities.allowFlying;
    }

    public void setAllowFlight(final boolean value) {
        if (isFlying() && !value) {
            getHandle().capabilities.isFlying = false;
        }

        getHandle().capabilities.allowFlying = value;
        getHandle().sendPlayerAbilities();
    }

    @Override
    public int getNoDamageTicks() {
        if (getHandle().initialInvulnerability > 0) {
            return Math.max(getHandle().initialInvulnerability, getHandle().hurtResistantTime);
        } else {
            return getHandle().hurtResistantTime;
        }
    }

    public void setFlySpeed(final float value) {
        validateSpeed(value);
        final net.minecraft.entity.player.EntityPlayerMP player = getHandle();
        player.capabilities.setFlySpeed(Math.max(value, 0.0001f) / 2.0f); // Spigot
        player.sendPlayerAbilities();

    }

    public void setWalkSpeed(final float value) {
        validateSpeed(value);
        final net.minecraft.entity.player.EntityPlayerMP player = getHandle();
        player.capabilities.setPlayerWalkSpeed(Math.max(value, 0.0001f) / 2.0f); // Spigot
        player.sendPlayerAbilities();
    }

    public float getFlySpeed() {
        return getHandle().capabilities.getFlySpeed() * 2.0f;
    }

    public float getWalkSpeed() {
        return getHandle().capabilities.getWalkSpeed() * 2.0f;
    }

    private void validateSpeed(final float value) {
        if (value < 0) {
            if (value < -1.0f) {
                throw new IllegalArgumentException(value + " is too low");
            }
        } else {
            if (value > 1.0f) {
                throw new IllegalArgumentException(value + " is too high");
            }
        }
    }

    @Override
    public void setMaxHealth(final double amount) {
        super.setMaxHealth(amount);
        this.health = Math.min(this.health, health);
        getHandle().setPlayerHealthUpdated();
    }

    @Override
    public void resetMaxHealth() {
        super.resetMaxHealth();
        getHandle().setPlayerHealthUpdated();
    }

    public CraftScoreboard getScoreboard() {
        return this.server.getScoreboardManager().getPlayerBoard(this);
    }

    public void setScoreboard(final Scoreboard scoreboard) {
        Validate.notNull(scoreboard, "Scoreboard cannot be null");
        final net.minecraft.network.NetServerHandler playerConnection = getHandle().playerNetServerHandler;
        if (playerConnection == null) {
            throw new IllegalStateException("Cannot set scoreboard yet");
        }
        if (playerConnection.disconnected) {
            throw new IllegalStateException("Cannot set scoreboard for invalid CraftPlayer");
        }

        this.server.getScoreboardManager().setPlayerBoard(this, scoreboard);
    }

    public void setHealthScale(final double value) {
        Validate.isTrue((float) value > 0.0F, "Must be greater than 0");
        healthScale = value;
        scaledHealth = true;
        updateScaledHealth();
    }

    public double getHealthScale() {
        return healthScale;
    }

    public void setHealthScaled(final boolean scale) {
        if (scaledHealth != (scaledHealth = scale)) {
            updateScaledHealth();
        }
    }

    public boolean isHealthScaled() {
        return scaledHealth;
    }

    public float getScaledHealth() {
        return (float) (isHealthScaled() ? getHealth() * getHealthScale() / getMaxHealth() : getHealth());
    }

    @Override
    public double getHealth() {
        return health;
    }

    public void setRealHealth(final double health) {
        this.health = health;
    }

    public void updateScaledHealth() {
        final net.minecraft.entity.ai.attributes.ServersideAttributeMap attributemapserver = (net.minecraft.entity.ai.attributes.ServersideAttributeMap) getHandle().getAttributeMap();
        final Set set = attributemapserver.func_111161_b();

        injectScaledMaxHealth(set, true);

        getHandle().getDataWatcher().updateObject(6, (float) getScaledHealth());
        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet8UpdateHealth(getScaledHealth(), getHandle().getFoodStats().getFoodLevel(), getHandle().getFoodStats().getSaturationLevel()));
        getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet44UpdateAttributes(getHandle().entityId, set));

        set.clear();
        getHandle().maxHealthCache = getMaxHealth();
    }

    public void injectScaledMaxHealth(final Collection collection, final boolean force) {
        if (!scaledHealth && !force) {
            return;
        }
        for (final Object genericInstance : collection) {
            final net.minecraft.entity.ai.attributes.Attribute attribute = ((net.minecraft.entity.ai.attributes.AttributeInstance) genericInstance).func_111123_a();
            if (attribute.getAttributeUnlocalizedName().equals("generic.maxHealth")) {
                collection.remove(genericInstance);
                break;
            }
            continue;
        }
        collection.add(new net.minecraft.entity.ai.attributes.ModifiableAttributeInstance(getHandle().getAttributeMap(), (new net.minecraft.entity.ai.attributes.RangedAttribute("generic.maxHealth", scaledHealth ? healthScale : getMaxHealth(), 0.0D, Float.MAX_VALUE)).func_111117_a("Max Health").setShouldWatch(true)));
    }

    // Spigot start
    private final Player.Spigot spigot = new Player.Spigot() {
        /* Cauldron - remove
        @Override
        public InetSocketAddress getRawAddress()
        {
            return ( getHandle().playerConnection == null ) ? null : (InetSocketAddress) getHandle().playerConnection.networkManager.getSocket().getRemoteSocketAddress();
        }
        */

        @Override
        public void playEffect(final Location location, final Effect effect, final int id, final int data, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int particleCount, final int radius) {
            Validate.notNull(location, "Location cannot be null");
            Validate.notNull(effect, "Effect cannot be null");
            Validate.notNull(location.getWorld(), "World cannot be null");

            final net.minecraft.network.packet.Packet packet;
            if (effect.getType() != Effect.Type.PARTICLE) {
                final int packetData = effect.getId();
                packet = new net.minecraft.network.packet.Packet61DoorChange(packetData, location.getBlockX(), location.getBlockY(), location.getBlockZ(), id, false);
            } else {
                final StringBuilder particleFullName = new StringBuilder();
                particleFullName.append(effect.getName());

                if (effect.getData() != null && (effect.getData().equals(Material.class) || effect.getData().equals(org.bukkit.material.MaterialData.class))) {
                    particleFullName.append('_').append(id);
                }

                if (effect.getData() != null && effect.getData().equals(org.bukkit.material.MaterialData.class)) {
                    particleFullName.append('_').append(data);
                }
                packet = new net.minecraft.network.packet.Packet63WorldParticles(effect.getName(), (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, particleCount, radius);
            }

            if (!location.getWorld().equals(getWorld())) {
                return;
            }

            getHandle().playerNetServerHandler.sendPacketToPlayer(packet);
        }

        @Override
        public boolean getCollidesWithEntities() {
            return getHandle().collidesWithEntities;
        }

        @Override
        public void setCollidesWithEntities(final boolean collides) {
            getHandle().collidesWithEntities = collides;
            getHandle().preventEntitySpawning = collides; // First boolean of Entity
        }
    };

    public Player.Spigot spigot() {
        return spigot;
    }
    // Spigot end
}