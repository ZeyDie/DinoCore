package net.minecraft.server.management;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgePacket;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftTravelAgent;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.chunkio.ChunkIOExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

// CraftBukkit start
// CraftBukkit end
// Cauldron start
// Cauldron end

public abstract class ServerConfigurationManager {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    /**
     * Reference to the MinecraftServer object.
     */
    private final MinecraftServer mcServer;

    /**
     * A list of player entities that exist on this server.
     */
    public final List playerEntityList = new ArrayList();
    private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
    private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));

    /**
     * A set containing the OPs.
     */
    private Set ops = new HashSet();

    /**
     * The Set of all whitelisted players.
     */
    private Set whiteListedPlayers = new java.util.LinkedHashSet(); // CraftBukkit - HashSet -> LinkedHashSet

    /**
     * Reference to the PlayerNBTManager object.
     */
    public IPlayerFileData playerNBTManagerObj; // CraftBukkit - private -> public

    /**
     * Server setting to only allow OPs and whitelisted players to join the server.
     */
    public boolean whiteListEnforced; // CraftBukkit - private -> public

    /**
     * The maximum number of players that can be connected at a time.
     */
    protected int maxPlayers;
    protected int viewDistance;
    private EnumGameType gameType;

    /**
     * True if all players are allowed to use commands (cheats).
     */
    private boolean commandsAllowedForAll;

    /**
     * index into playerEntities of player to ping, updated every tick; currently hardcoded to max at 200 players
     */
    private int playerPingIndex;
    public boolean allowLoginEvent = false;

    // CraftBukkit start
    private CraftServer cserver;

    public ServerConfigurationManager(MinecraftServer par1MinecraftServer) {
        par1MinecraftServer.server = new CraftServer(par1MinecraftServer, this);
        par1MinecraftServer.console = org.bukkit.craftbukkit.v1_6_R3.command.ColouredConsoleSender.getInstance();
        par1MinecraftServer.reader.addCompleter(new org.bukkit.craftbukkit.v1_6_R3.command.ConsoleCommandCompleter(par1MinecraftServer.server));
        this.cserver = par1MinecraftServer.server;
        // CraftBukkit end
        this.mcServer = par1MinecraftServer;
        this.bannedPlayers.setListActive(false);
        this.bannedIPs.setListActive(false);
        this.maxPlayers = 8;
    }

    public void initializeConnectionToPlayer(INetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP) {
        NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(par2EntityPlayerMP);
        par2EntityPlayerMP.setWorld(this.mcServer.worldServerForDimension(par2EntityPlayerMP.dimension));
        par2EntityPlayerMP.theItemInWorldManager.setWorld((WorldServer) par2EntityPlayerMP.worldObj);
        String s = "local";

        if (par1INetworkManager.getSocketAddress() != null) {
            s = par1INetworkManager.getSocketAddress().toString();
        }

        // CraftBukkit - add world and location to 'logged in' message.
        this.mcServer.getLogAgent().logInfo(par2EntityPlayerMP.getCommandSenderName() + "[" + s + "] logged in with entity id " + par2EntityPlayerMP.entityId + " at ([" + par2EntityPlayerMP.worldObj.worldInfo.getWorldName() + "] " + par2EntityPlayerMP.posX + ", " + par2EntityPlayerMP.posY + ", " + par2EntityPlayerMP.posZ + ")");
        WorldServer worldserver = this.mcServer.worldServerForDimension(par2EntityPlayerMP.dimension);
        ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
        this.func_72381_a(par2EntityPlayerMP, (EntityPlayerMP) null, worldserver);
        NetServerHandler netserverhandler = new NetServerHandler(this.mcServer, par1INetworkManager, par2EntityPlayerMP);
        // CraftBukkit start -- Don't send a higher than 60 MaxPlayer size, otherwise the PlayerInfo window won't render correctly.
        int maxPlayers = this.getMaxPlayers();

        if (maxPlayers > 60) {
            maxPlayers = 60;
        }

        // Cauldron start - send DimensionRegisterPacket to client before attempting to login to a Bukkit dimension
        if (DimensionManager.isBukkitDimension(par2EntityPlayerMP.dimension)) {
            Packet250CustomPayload[] pkt = ForgePacket.makePacketSet(new DimensionRegisterPacket(par2EntityPlayerMP.dimension, worldserver.getWorld().getEnvironment().getId()));
            par2EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(pkt[0]);
        }
        // Cauldron end
        netserverhandler.sendPacketToPlayer(new Packet1Login(par2EntityPlayerMP.entityId, worldserver.getWorldInfo().getTerrainType(), par2EntityPlayerMP.theItemInWorldManager.getGameType(), worldserver.getWorldInfo().isHardcoreModeEnabled(), worldserver.provider.dimensionId, worldserver.difficultySetting, worldserver.getHeight(), maxPlayers));
        par2EntityPlayerMP.getBukkitEntity().sendSupportedChannels();
        // CraftBukkit end
        netserverhandler.sendPacketToPlayer(new Packet250CustomPayload("MC|Brand", this.getServerInstance().getServerModName().getBytes(Charsets.UTF_8)));
        netserverhandler.sendPacketToPlayer(new Packet6SpawnPosition(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ));
        netserverhandler.sendPacketToPlayer(new Packet202PlayerAbilities(par2EntityPlayerMP.capabilities));
        netserverhandler.sendPacketToPlayer(new Packet16BlockItemSwitch(par2EntityPlayerMP.inventory.currentItem));

        this.func_96456_a((ServerScoreboard) worldserver.getScoreboard(), par2EntityPlayerMP);
        this.updateTimeAndWeatherForPlayer(par2EntityPlayerMP, worldserver);
        // this.sendAll(new Packet3Chat(EnumChatFormat.YELLOW + entityplayermp.getScoreboardDisplayName() + EnumChatFormat.YELLOW + " joined the game.")); // CraftBukkit - handled in event
        this.playerLoggedIn(par2EntityPlayerMP);
        netserverhandler.setPlayerLocation(par2EntityPlayerMP.posX, par2EntityPlayerMP.posY, par2EntityPlayerMP.posZ, par2EntityPlayerMP.rotationYaw, par2EntityPlayerMP.rotationPitch);
        this.mcServer.getNetworkThread().addPlayer(netserverhandler);
        netserverhandler.sendPacketToPlayer(new Packet4UpdateTime(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getGameRuleBooleanValue("doDaylightCycle")));

        if (this.mcServer.getTexturePack().length() > 0) {
            par2EntityPlayerMP.requestTexturePackLoad(this.mcServer.getTexturePack(), this.mcServer.textureSize());
        }

        Iterator iterator = par2EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext()) {
            PotionEffect potioneffect = (PotionEffect) iterator.next();
            netserverhandler.sendPacketToPlayer(new Packet41EntityEffect(par2EntityPlayerMP.entityId, potioneffect));
        }

        par2EntityPlayerMP.addSelfToInternalCraftingInventory();

        FMLNetworkHandler.handlePlayerLogin(par2EntityPlayerMP, netserverhandler, par1INetworkManager);

        if (nbttagcompound != null && nbttagcompound.hasKey("Riding")) {
            Entity entity = EntityList.createEntityFromNBT(nbttagcompound.getCompoundTag("Riding"), worldserver);

            if (entity != null) {
                entity.forceSpawn = true;
                worldserver.spawnEntityInWorld(entity);
                par2EntityPlayerMP.mountEntity(entity);
                entity.forceSpawn = false;
            }
        }
    }

    public void func_96456_a(ServerScoreboard par1ServerScoreboard, EntityPlayerMP par2EntityPlayerMP)   // CraftBukkit - protected -> public
    {
        HashSet hashset = new HashSet();
        Iterator iterator = par1ServerScoreboard.func_96525_g().iterator();

        while (iterator.hasNext()) {
            ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) iterator.next();
            par2EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet209SetPlayerTeam(scoreplayerteam, 0));
        }

        for (int i = 0; i < 3; ++i) {
            ScoreObjective scoreobjective = par1ServerScoreboard.func_96539_a(i);

            if (scoreobjective != null && !hashset.contains(scoreobjective)) {
                List list = par1ServerScoreboard.func_96550_d(scoreobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    Packet packet = (Packet) iterator1.next();
                    par2EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(packet);
                }

                hashset.add(scoreobjective);
            }
        }
    }

    /**
     * Sets the NBT manager to the one for the WorldServer given.
     */
    public void setPlayerManager(WorldServer[] par1ArrayOfWorldServer) {
        if (this.playerNBTManagerObj != null) {
            return;    // CraftBukkit
        }

        this.playerNBTManagerObj = par1ArrayOfWorldServer[0].getSaveHandler().getSaveHandler();
    }

    public void func_72375_a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer) {
        WorldServer worldserver1 = par1EntityPlayerMP.getServerForPlayer();

        if (par2WorldServer != null) {
            par2WorldServer.getPlayerManager().removePlayer(par1EntityPlayerMP);
        }

        worldserver1.getPlayerManager().addPlayer(par1EntityPlayerMP);
        worldserver1.theChunkProviderServer.loadChunk((int) par1EntityPlayerMP.posX >> 4, (int) par1EntityPlayerMP.posZ >> 4);
    }

    public int getEntityViewDistance() {
        return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP par1EntityPlayerMP) {
        NBTTagCompound nbttagcompound = this.mcServer.worlds.get(0).getWorldInfo().getPlayerNBTTagCompound(); // CraftBukkit
        NBTTagCompound nbttagcompound1;

        if (par1EntityPlayerMP.getCommandSenderName().equals(this.mcServer.getServerOwner()) && nbttagcompound != null) {
            par1EntityPlayerMP.readFromNBT(nbttagcompound);
            nbttagcompound1 = nbttagcompound;
            System.out.println("loading single player");
        } else {
            nbttagcompound1 = this.playerNBTManagerObj.readPlayerData(par1EntityPlayerMP);
        }

        return nbttagcompound1;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP) {
        this.playerNBTManagerObj.writePlayerData(par1EntityPlayerMP);
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP) {
        cserver.detectListNameConflict(par1EntityPlayerMP); // CraftBukkit
        // this.sendAll(new Packet201PlayerInfo(entityplayermp.name, true, 1000)); // CraftBukkit - replaced with loop below
        this.playerEntityList.add(par1EntityPlayerMP);
        WorldServer worldserver = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        // CraftBukkit start
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this.cserver.getPlayer(par1EntityPlayerMP), "\u00A7e" + par1EntityPlayerMP.getTranslatedEntityName() + " joined the game.");
        this.cserver.getPluginManager().callEvent(playerJoinEvent);
        String joinMessage = playerJoinEvent.getJoinMessage();

        if ((joinMessage != null) && (joinMessage.length() > 0)) {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat(ChatMessageComponent.createFromText(joinMessage)));
        }

        this.cserver.onPlayerJoin(playerJoinEvent.getPlayer());
        ChunkIOExecutor.adjustPoolSize(this.getCurrentPlayerCount());
        // CraftBukkit end

        // CraftBukkit start - Only add if the player wasn't moved in the event
        if (par1EntityPlayerMP.worldObj == worldserver && !worldserver.playerEntities.contains(par1EntityPlayerMP)) {
            worldserver.spawnEntityInWorld(par1EntityPlayerMP);
            this.func_72375_a(par1EntityPlayerMP, (WorldServer) null);
        }

        // CraftBukkit end
        // CraftBukkit start - sendAll above replaced with this loop
        Packet201PlayerInfo packet = new Packet201PlayerInfo(par1EntityPlayerMP.listName, true, 1000);

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            EntityPlayerMP entityplayermp1 = (EntityPlayerMP) this.playerEntityList.get(i);

            if (entityplayermp1.getBukkitEntity().canSee(par1EntityPlayerMP.getBukkitEntity())) {
                entityplayermp1.playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }

        // CraftBukkit end

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            EntityPlayerMP entityplayermp1 = (EntityPlayerMP) this.playerEntityList.get(i);

            // CraftBukkit start - .name -> .listName
            if (par1EntityPlayerMP.getBukkitEntity().canSee(entityplayermp1.getBukkitEntity())) {
                par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet201PlayerInfo(entityplayermp1.listName, true, entityplayermp1.ping));
            }

            // CraftBukkit end
        }
    }

    /**
     * using player's dimension, update their movement when in a vehicle (e.g. cart, boat)
     */
    public void serverUpdateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP) {
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(par1EntityPlayerMP);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public String disconnect(EntityPlayerMP entityplayermp)   // CraftBukkit - return string
    {
        if (entityplayermp.playerNetServerHandler.connectionClosed) {
            return null;    // CraftBukkit - exploitsies fix
        }

        // CraftBukkit start - Quitting must be before we do final save of data, in case plugins need to modify it
        org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.handleInventoryCloseEvent(entityplayermp);
        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(this.cserver.getPlayer(entityplayermp), "\u00A7e" + entityplayermp.username + " left the game.");
        this.cserver.getPluginManager().callEvent(playerQuitEvent);
        entityplayermp.getBukkitEntity().disconnect(playerQuitEvent.getQuitMessage());
        // CraftBukkit end

        //TODO ZeyCodeStart
        final BaseAttributeMap baseAttributeMap = entityplayermp.getAttributeMap();
        final AttributeInstance attributeInstance = baseAttributeMap.getAttributeInstance(SharedMonsterAttributes.attackDamage);

        for (final AttributeModifier attributeModifier : attributeInstance.func_111122_c())
            attributeInstance.removeModifier(attributeModifier);
        //TODO ZeyCodeEnd

        GameRegistry.onPlayerLogout(entityplayermp); // Forge
        this.writePlayerData(entityplayermp);
        WorldServer worldserver = entityplayermp.getServerForPlayer();

        if (entityplayermp.ridingEntity != null && !(entityplayermp.ridingEntity instanceof EntityPlayer)) // CraftBukkit - Don't remove players
        {
            worldserver.removePlayerEntityDangerously(entityplayermp.ridingEntity);
            // System.out.println("removing player mount"); // CraftBukkit - Removed debug message
        }

        worldserver.removeEntity(entityplayermp);
        worldserver.getPlayerManager().removePlayer(entityplayermp);
        this.playerEntityList.remove(entityplayermp);
        ChunkIOExecutor.adjustPoolSize(this.getCurrentPlayerCount()); // CraftBukkit
        // CraftBukkit start - .name -> .listName, replace sendAll with loop
        Packet201PlayerInfo packet = new Packet201PlayerInfo(entityplayermp.listName, false, 9999);

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            EntityPlayerMP entityplayermp1 = (EntityPlayerMP) this.playerEntityList.get(i);

            if (entityplayermp1.getBukkitEntity().canSee(entityplayermp.getBukkitEntity())) {
                entityplayermp1.playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }

        // This removes the scoreboard (and player reference) for the specific player in the manager
        this.cserver.getScoreboardManager().removePlayer(entityplayermp.getBukkitEntity());
        return playerQuitEvent.getQuitMessage();
        // CraftBukkit end
    }

    // Cauldron start - vanilla compatibility
    public void playerLoggedOut(EntityPlayerMP entityPlayerMP) {
        disconnect(entityPlayerMP);
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress par1SocketAddress, String par2Str) {
        if (this.bannedPlayers.isBanned(par2Str)) {
            BanEntry banentry = (BanEntry) this.bannedPlayers.getBannedList().get(par2Str);
            String s1 = "You are banned from this server!\nReason: " + banentry.getBanReason();

            if (banentry.getBanEndDate() != null) {
                s1 = s1 + "\nYour ban will be removed on " + dateFormat.format(banentry.getBanEndDate());
            }

            return s1;
        } else if (!this.isAllowedToLogin(par2Str)) {
            return "You are not white-listed on this server!";
        } else {
            String s2 = par1SocketAddress.toString();
            s2 = s2.substring(s2.indexOf("/") + 1);
            s2 = s2.substring(0, s2.indexOf(":"));

            if (this.bannedIPs.isBanned(s2)) {
                BanEntry banentry1 = (BanEntry) this.bannedIPs.getBannedList().get(s2);
                String s3 = "Your IP address is banned from this server!\nReason: " + banentry1.getBanReason();

                if (banentry1.getBanEndDate() != null) {
                    s3 = s3 + "\nYour ban will be removed on " + dateFormat.format(banentry1.getBanEndDate());
                }

                return s3;
            } else {
                return this.playerEntityList.size() >= this.maxPlayers ? "The server is full!" : null;
            }
        }
    }

    // CraftBukkit start - Whole method and signature
    public EntityPlayerMP attemptLogin(NetLoginHandler pendingconnection, String s, String hostname) {
        // Instead of kicking then returning, we need to store the kick reason
        // in the event, check with plugins to see if it's ok, and THEN kick
        // depending on the outcome.
        EntityPlayerMP entity = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), s, this.mcServer.isDemo() ? new DemoWorldManager(this.mcServer.worldServerForDimension(0)) : new ItemInWorldManager(this.mcServer.worldServerForDimension(0)));
        Player player = entity.getBukkitEntity();
        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, pendingconnection.getSocket().getInetAddress());
        // Cauldron start - heavy player load can cause connections to be null so we must validate before attempting to use it
        SocketAddress socketaddress = null;
        if (pendingconnection != null && pendingconnection.myTCPConnection != null)
            socketaddress = pendingconnection.myTCPConnection.getSocketAddress();
        // Cauldron end

        if (this.bannedPlayers.isBanned(s)) {
            BanEntry banentry = (BanEntry) this.bannedPlayers.getBannedList().get(s);
            String s1 = "You are banned from this server!\nReason: " + banentry.getBanReason();

            if (banentry.getBanEndDate() != null) {
                s1 = s1 + "\nYour ban will be removed on " + dateFormat.format(banentry.getBanEndDate());
            }

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, s1);
        } else if (!this.isAllowedToLogin(s)) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, org.spigotmc.SpigotConfig.whitelistMessage); // Spigot
        } else {
            // Cauldron start - validate socket
            String s2 = socketaddress != null ? socketaddress.toString() : "";
            if (!s2.equals("")) {
                s2 = s2.substring(s2.indexOf("/") + 1);
                s2 = s2.substring(0, s2.indexOf(":"));
            }

            if (!s2.equals("") && this.bannedIPs.isBanned(s2)) {
                BanEntry banentry1 = (BanEntry) this.bannedIPs.getBannedList().get(s2);
                String s3 = "Your IP address is banned from this server!\nReason: " + banentry1.getBanReason();

                if (banentry1.getBanEndDate() != null) {
                    s3 = s3 + "\nYour ban will be removed on " + dateFormat.format(banentry1.getBanEndDate());
                }

                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, s3);
            } else if (this.playerEntityList.size() >= this.maxPlayers) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, org.spigotmc.SpigotConfig.serverFullMessage); // Spigot
            } else {
                event.disallow(PlayerLoginEvent.Result.ALLOWED, s2);
            }
            // Cauldron end
        }
        // Cauldron start - if login event is allowed, execute remaining login code
        if (allowLoginEvent || event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            this.cserver.getPluginManager().callEvent(event);
        }
        // Cauldron end

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            pendingconnection.raiseErrorAndDisconnect(event.getKickMessage());
            return null;
        }

        return entity;
        // CraftBukkit end
    }

    // Cauldron start - vanilla compatibility
    public EntityPlayerMP createPlayerForUser(String par1Str) {
        ArrayList arraylist = new ArrayList();
        EntityPlayerMP entityplayermp;

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);

            if (entityplayermp.getCommandSenderName().equalsIgnoreCase(par1Str)) {
                arraylist.add(entityplayermp);
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            entityplayermp = (EntityPlayerMP) iterator.next();
            entityplayermp.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        Object object;

        if (this.mcServer.isDemo()) {
            object = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
        } else {
            object = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), par1Str, (ItemInWorldManager) object);
    }
    // Cauldron end

    public EntityPlayerMP processLogin(EntityPlayerMP player) {
        String s = player.username; // CraftBukkit
        ArrayList arraylist = new ArrayList();
        EntityPlayerMP entityplayermp;

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);

            if (entityplayermp.getCommandSenderName().equalsIgnoreCase(s)) {
                arraylist.add(entityplayermp);
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            entityplayermp = (EntityPlayerMP) iterator.next();
            entityplayermp.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        /* CraftBukkit start
        Object object;

        if (this.server.M()) {
            object = new DemoPlayerInteractManager(this.server.getWorldServer(0));
        } else {
            object = new PlayerInteractManager(this.server.getWorldServer(0));
        }

        return new EntityPlayer(this.server, this.server.getWorldServer(0), s, (PlayerInteractManager) object);
        */
        return player;
        // CraftBukkit end
    }

    // CraftBukkit start

    /**
     * creates and returns a respawned player based on the provided PlayerEntity. Args are the PlayerEntityMP to
     * respawn, an INT for the dimension to respawn into (usually 0), and a boolean value that is true if the player
     * beat the game rather than dying
     */
    public EntityPlayerMP respawnPlayer(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3) {
        return this.respawnPlayer(par1EntityPlayerMP, par2, par3, null);
    }

    public EntityPlayerMP respawnPlayer(EntityPlayerMP par1EntityPlayerMP, int targetDimension, boolean returnFromEnd, Location location) {
        // Cauldron start - refactor entire method for sanity.
        // Phase 1 - check if the player is allowed to respawn in same dimension
        World world = mcServer.worldServerForDimension(targetDimension);
        if (world == null) {
            targetDimension = 0;
        } else if (location == null && !world.provider.canRespawnHere()) // ignore plugins
        {
            targetDimension = world.provider.getRespawnDimension(par1EntityPlayerMP);
        }

        // Phase 2 - handle return from End
        if (returnFromEnd) {
            WorldServer exitWorld = this.mcServer.worldServerForDimension(targetDimension);
            Location enter = par1EntityPlayerMP.getBukkitEntity().getLocation();
            Location exit = null;
            // THE_END -> NORMAL; use bed if available, otherwise default spawn
            exit = ((org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer) par1EntityPlayerMP.getBukkitEntity()).getBedSpawnLocation();

            if (exit == null || ((CraftWorld) exit.getWorld()).getHandle().dimension != 0) {
                exit = exitWorld.getWorld().getSpawnLocation();
            }
            PlayerPortalEvent event = new PlayerPortalEvent(par1EntityPlayerMP.getBukkitEntity(), enter, exit, CraftTravelAgent.DEFAULT, TeleportCause.END_PORTAL);
            event.useTravelAgent(false);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled() || event.getTo() == null) {
                return null;
            }
        }

        // Phase 3 - remove current player from current dimension
        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(par1EntityPlayerMP);
        //par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().removePlayer(par1EntityPlayerMP);
        this.playerEntityList.remove(par1EntityPlayerMP);
        this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension).removePlayerEntityDangerously(par1EntityPlayerMP);

        // Phase 4 - handle bed spawn
        ChunkCoordinates bedSpawnChunkCoords = par1EntityPlayerMP.getBedLocation(targetDimension);
        boolean spawnForced = par1EntityPlayerMP.isSpawnForced(targetDimension);
        par1EntityPlayerMP.dimension = targetDimension;
        // CraftBukkit start
        EntityPlayerMP entityplayermp1 = par1EntityPlayerMP;
        entityplayermp1.setWorld(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension)); // make sure to update reference for bed spawn logic
        org.bukkit.World fromWorld = entityplayermp1.getBukkitEntity().getWorld();
        entityplayermp1.playerConqueredTheEnd = false;
        ChunkCoordinates chunkcoordinates1;
        boolean isBedSpawn = false;
        org.bukkit.World toWorld = entityplayermp1.getBukkitEntity().getWorld();

        if (location == null) // use bed logic only if player respawns (player death)
        {
            if (bedSpawnChunkCoords != null) // if player has a bed
            {
                chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), bedSpawnChunkCoords, spawnForced);

                if (chunkcoordinates1 != null) {
                    isBedSpawn = true;
                    entityplayermp1.setLocationAndAngles((double) ((float) chunkcoordinates1.posX + 0.5F), (double) ((float) chunkcoordinates1.posY + 0.1F), (double) ((float) chunkcoordinates1.posZ + 0.5F), 0.0F, 0.0F);
                    entityplayermp1.setSpawnChunk(bedSpawnChunkCoords, spawnForced);
                    location = new Location(toWorld, bedSpawnChunkCoords.posX + 0.5, bedSpawnChunkCoords.posY, bedSpawnChunkCoords.posZ + 0.5);
                } else // bed was not found (broken)
                {
                    //entityplayermp1.setSpawnChunk(null, true); // CraftBukkit
                    entityplayermp1.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(0, 0));
                    location = new Location(toWorld, toWorld.getSpawnLocation().getX(), toWorld.getSpawnLocation().getY(), toWorld.getSpawnLocation().getZ()); // use the spawnpoint as location
                }
            }

            if (location == null) {
                location = new Location(toWorld, toWorld.getSpawnLocation().getX(), toWorld.getSpawnLocation().getY(), toWorld.getSpawnLocation().getZ()); // use the world spawnpoint as default location
            }

            Player respawnPlayer = this.cserver.getPlayer(entityplayermp1);
            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn);
            this.cserver.getPluginManager().callEvent(respawnEvent);

            if (!spawnForced) // mods override plugins
            {
                location = respawnEvent.getRespawnLocation();
            }

            par1EntityPlayerMP.reset();
        } else // plugin
        {
            location.setWorld(this.mcServer.worldServerForDimension(targetDimension).getWorld());
        }

        WorldServer targetWorld = ((CraftWorld) location.getWorld()).getHandle();
        entityplayermp1.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // CraftBukkit end
        targetWorld.theChunkProviderServer.loadChunk((int) entityplayermp1.posX >> 4, (int) entityplayermp1.posZ >> 4);

        while (!targetWorld.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).isEmpty()) {
            entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ);
        }

        // Phase 5 - Respawn player in new world
        int actualDimension = targetWorld.provider.dimensionId;
        // Cauldron - change dim for bukkit added dimensions
        if (DimensionManager.isBukkitDimension(actualDimension)) {
            Packet250CustomPayload[] pkt = ForgePacket.makePacketSet(new DimensionRegisterPacket(actualDimension, targetWorld.getWorld().getEnvironment().getId()));
            entityplayermp1.playerNetServerHandler.sendPacketToPlayer(pkt[0]);
        }
        // Cauldron end
        // CraftBukkit start
        entityplayermp1.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(actualDimension, (byte) targetWorld.difficultySetting, targetWorld.getWorldInfo().getTerrainType(), targetWorld.getHeight(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        entityplayermp1.setWorld(targetWorld); // in case plugin changed it
        entityplayermp1.isDead = false;
        entityplayermp1.playerNetServerHandler.teleport(new Location(targetWorld.getWorld(), entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch));
        entityplayermp1.setSneaking(false);
        chunkcoordinates1 = targetWorld.getSpawnPoint();
        // CraftBukkit end
        entityplayermp1.playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(chunkcoordinates1.posX, chunkcoordinates1.posY, chunkcoordinates1.posZ));
        entityplayermp1.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(entityplayermp1.experience, entityplayermp1.experienceTotal, entityplayermp1.experienceLevel));
        this.updateTimeAndWeatherForPlayer(entityplayermp1, targetWorld);
        targetWorld.getPlayerManager().addPlayer(entityplayermp1);
        targetWorld.spawnEntityInWorld(entityplayermp1);
        this.playerEntityList.add(entityplayermp1);
        entityplayermp1.addSelfToInternalCraftingInventory();
        entityplayermp1.setHealth(entityplayermp1.getHealth());

        // If world changed then fire the appropriate change world event else respawn
        if (fromWorld != location.getWorld()) {
            GameRegistry.onPlayerChangedDimension(par1EntityPlayerMP, (CraftWorld) fromWorld); // Cauldron - fire changed dimension for mods
        } else GameRegistry.onPlayerRespawn(entityplayermp1);

        return entityplayermp1;
    }

    // Cauldron start - refactor transferPlayerToDimension to be compatible with Bukkit. These methods are to be used when a player comes in contact with a portal
    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2) {
        this.transferPlayerToDimension(par1EntityPlayerMP, par2, mcServer.worldServerForDimension(par2).getDefaultTeleporter(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter) // mods such as Twilight Forest call this method directly
    {
        this.transferPlayerToDimension(par1EntityPlayerMP, par2, teleporter, TeleportCause.MOD); // use our mod cause
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, TeleportCause cause) {
        this.transferPlayerToDimension(par1EntityPlayerMP, par2, mcServer.worldServerForDimension(par2).getDefaultTeleporter(), cause);
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int targetDimension, Teleporter teleporter, TeleportCause cause) // Cauldron - add TeleportCause
    {
        // Allow Forge hotloading on teleport
        WorldServer fromWorld = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        WorldServer exitWorld = this.mcServer.worldServerForDimension(targetDimension);

        // CraftBukkit start - Replaced the standard handling of portals with a more customised method.
        Location enter = par1EntityPlayerMP.getBukkitEntity().getLocation();
        Location exit = null;
        boolean useTravelAgent = false;

        if (exitWorld != null) {
            exit = this.calculateTarget(enter, exitWorld);
            if (cause != cause.MOD) // don't use travel agent for custom dimensions
            {
                useTravelAgent = true;
            }
        }

        // allow forge mods to be the teleporter
        TravelAgent agent = null;
        if (exit != null && teleporter == null) {
            teleporter = ((CraftWorld) exit.getWorld()).getHandle().getDefaultTeleporter();
            if (teleporter instanceof TravelAgent) {
                agent = (TravelAgent) teleporter;
            }
        } else {
            if (teleporter instanceof TravelAgent) {
                agent = (TravelAgent) teleporter;
            }
        }
        if (agent == null) // mod teleporter such as Twilight Forest
        {
            agent = CraftTravelAgent.DEFAULT; // return arbitrary TA to compensate for implementation dependent plugins
        }

        PlayerPortalEvent event = new PlayerPortalEvent(par1EntityPlayerMP.getBukkitEntity(), enter, exit, agent, cause);
        event.useTravelAgent(useTravelAgent);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled() || event.getTo() == null) {
            return;
        }

        exit = event.useTravelAgent() && cause != cause.MOD ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo(); // make sure plugins don't override travelagent for mods

        if (exit == null) {
            return;
        }

        exitWorld = ((CraftWorld) exit.getWorld()).getHandle();
        Vector velocity = par1EntityPlayerMP.getBukkitEntity().getVelocity();
        boolean before = exitWorld.theChunkProviderServer.loadChunkOnProvideRequest;
        exitWorld.theChunkProviderServer.loadChunkOnProvideRequest = true;
        exitWorld.getDefaultTeleporter().adjustExit(par1EntityPlayerMP, exit, velocity);
        exitWorld.theChunkProviderServer.loadChunkOnProvideRequest = before;
        // CraftBukkit end

        par1EntityPlayerMP.dimension = targetDimension;
        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(targetDimension, (byte) par1EntityPlayerMP.worldObj.difficultySetting, exitWorld.getWorldInfo().getTerrainType(), exitWorld.getHeight(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        fromWorld.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        this.transferEntityToWorld(par1EntityPlayerMP, fromWorld.provider.dimensionId, fromWorld, exitWorld, teleporter);
        this.func_72375_a(par1EntityPlayerMP, fromWorld);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(exitWorld);
        this.updateTimeAndWeatherForPlayer(par1EntityPlayerMP, exitWorld);
        this.syncPlayerInventory(par1EntityPlayerMP);
        Iterator iterator = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext()) {
            PotionEffect potioneffect = (PotionEffect) iterator.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(par1EntityPlayerMP.entityId, potioneffect));
        }

        GameRegistry.onPlayerChangedDimension(par1EntityPlayerMP, fromWorld.getWorld());
    }
    // Cauldron end

    /**
     * Transfers an entity from a world to another world.
     */
    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer) {
        // CraftBukkit start - Split into modular functions
        Location exit = this.calculateTarget(par1Entity.getBukkitEntity().getLocation(), par4WorldServer);
        this.repositionEntity(par1Entity, exit, true);
    }

    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter) {
        WorldProvider pOld = par3WorldServer.provider;
        WorldProvider pNew = par4WorldServer.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = par1Entity.posX * moveFactor;
        double d1 = par1Entity.posZ * moveFactor;
        double d3 = par1Entity.posX;
        double d4 = par1Entity.posY;
        double d5 = par1Entity.posZ;
        float f = par1Entity.rotationYaw;
        par3WorldServer.theProfiler.startSection("moving");

        if (par1Entity.dimension == 1) {
            ChunkCoordinates chunkcoordinates;

            if (par2 == 1) {
                chunkcoordinates = par4WorldServer.getSpawnPoint();
            } else {
                chunkcoordinates = par4WorldServer.getEntrancePortalLocation();
            }

            d0 = (double) chunkcoordinates.posX;
            par1Entity.posY = (double) chunkcoordinates.posY;
            d1 = (double) chunkcoordinates.posZ;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, 90.0F, 0.0F);

            if (par1Entity.isEntityAlive()) {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }

        par3WorldServer.theProfiler.endSection();

        if (par2 != 1) {
            par3WorldServer.theProfiler.startSection("placing");
            d0 = (double) MathHelper.clamp_int((int) d0, -29999872, 29999872);
            d1 = (double) MathHelper.clamp_int((int) d1, -29999872, 29999872);

            if (par1Entity.isEntityAlive()) {
                par4WorldServer.spawnEntityInWorld(par1Entity);
                par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);
                par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
                teleporter.placeInPortal(par1Entity, d3, d4, d5, f);
            }

            par3WorldServer.theProfiler.endSection();
        }

        par1Entity.setWorld(par4WorldServer);
    }

    // Copy of original a(Entity, int, WorldServer, WorldServer) method with only location calculation logic
    public Location calculateTarget(Location enter, World target) {
        WorldServer worldserver = ((CraftWorld) enter.getWorld()).getHandle();
        WorldServer worldserver1 = ((CraftWorld) target.getWorld()).getHandle();
        int i = worldserver.provider.dimensionId;
        double y = enter.getY();
        float yaw = enter.getYaw();
        float pitch = enter.getPitch();
        double d0 = enter.getX();
        double d1 = enter.getZ();
        double d2 = 8.0D;

        if (worldserver1.provider.dimensionId == -1) {
            d0 /= d2;
            d1 /= d2;
            /*
            entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
            */
        } else if (worldserver1.provider.dimensionId == 0) {
            d0 *= d2;
            d1 *= d2;
        } else {
            ChunkCoordinates chunkcoordinates;

            if (i == 1) {
                // use default NORMAL world spawn instead of target
                worldserver1 = this.mcServer.worlds.get(0);
                chunkcoordinates = worldserver1.getSpawnPoint();
            } else {
                chunkcoordinates = worldserver1.getEntrancePortalLocation();
            }

            if (chunkcoordinates != null) // Cauldron
            {
                d0 = (double) chunkcoordinates.posX;
                y = (double) chunkcoordinates.posY;
                d1 = (double) chunkcoordinates.posZ;
                yaw = 90.0F;
                pitch = 0.0F;
            }
        }

        if (i != 1) {
            d0 = (double) MathHelper.clamp_int((int) d0, -29999872, 29999872);
            d1 = (double) MathHelper.clamp_int((int) d1, -29999872, 29999872);
        }

        // entity.spawnIn(worldserver1);
        return new Location(worldserver1.getWorld(), d0, y, d1, yaw, pitch);
    }

    // copy of original a(Entity, int, WorldServer, WorldServer) method with only entity repositioning logic
    public void repositionEntity(Entity entity, Location exit, boolean portal) {
        int i = entity.dimension;
        WorldServer worldserver = (WorldServer) entity.worldObj;
        WorldServer worldserver1 = ((CraftWorld) exit.getWorld()).getHandle();
        worldserver.theProfiler.startSection("moving");
        entity.setLocationAndAngles(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());

        if (entity.isEntityAlive()) {
            worldserver.updateEntityWithOptionalForce(entity, false);
        }

        worldserver.theProfiler.endSection();

        if (i != 1) {
            worldserver.theProfiler.startSection("placing");

            /*
            d0 = (double) MathHelper.a((int) d0, -29999872, 29999872);
            d1 = (double) MathHelper.a((int) d1, -29999872, 29999872);
            */
            if (entity.isEntityAlive()) {
                worldserver1.spawnEntityInWorld(entity);
                // entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch)
                worldserver1.updateEntityWithOptionalForce(entity, false);

                // worldserver1.s().a(entity, d3, d4, d5, f);
                if (portal) {
                    Vector velocity = entity.getBukkitEntity().getVelocity();
                    worldserver1.getDefaultTeleporter().adjustExit(entity, exit, velocity); // Should be getTravelAgent
                    entity.setLocationAndAngles(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());

                    if (entity.motionX != velocity.getX() || entity.motionY != velocity.getY() || entity.motionZ != velocity.getZ()) {
                        entity.getBukkitEntity().setVelocity(velocity);
                    }
                }
            }

            worldserver.theProfiler.endSection();
        }

        entity.setWorld(worldserver1);
        // CraftBukkit end
    }

    /**
     * sends 1 player per tick, but only sends a player once every 600 ticks
     */
    public void sendPlayerInfoToAllPlayers() {
        if (++this.playerPingIndex > 600) {
            this.playerPingIndex = 0;
        }

        /* CraftBukkit start - Remove updating of lag to players -- it spams way to much on big servers.
        if (this.n < this.players.size()) {
            EntityPlayer entityplayermp = (EntityPlayer) this.players.get(this.n);

            this.sendAll(new Packet201PlayerInfo(entityplayermp.getName(), true, entityplayermp.ping));
        }
        // CraftBukkit end */
    }

    /**
     * sends a packet to all players
     */
    public void sendPacketToAllPlayers(Packet par1Packet) {
        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            ((EntityPlayerMP) this.playerEntityList.get(i)).playerNetServerHandler.sendPacketToPlayer(par1Packet);
        }
    }

    /**
     * Sends a packet to all players in the specified Dimension
     */
    public void sendPacketToAllPlayersInDimension(Packet par1Packet, int par2) {
        for (int j = 0; j < this.playerEntityList.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(j);

            if (entityplayermp.dimension == par2) {
                entityplayermp.playerNetServerHandler.sendPacketToPlayer(par1Packet);
            }
        }
    }

    /**
     * returns a string containing a comma-seperated list of player names
     */
    public String getPlayerListAsString() {
        String s = "";

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            if (i > 0) {
                s = s + ", ";
            }

            s = s + ((EntityPlayerMP) this.playerEntityList.get(i)).getCommandSenderName();
        }

        return s;
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames() {
        String[] astring = new String[this.playerEntityList.size()];

        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            astring[i] = ((EntityPlayerMP) this.playerEntityList.get(i)).getCommandSenderName();
        }

        return astring;
    }

    public BanList getBannedPlayers() {
        return this.bannedPlayers;
    }

    public BanList getBannedIPs() {
        return this.bannedIPs;
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str) {
        this.ops.add(par1Str.toLowerCase());
        // CraftBukkit start
        Player player = mcServer.server.getPlayer(par1Str);

        if (player != null) {
            player.recalculatePermissions();
        }

        // CraftBukkit end
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str) {
        this.ops.remove(par1Str.toLowerCase());
        // CraftBukkit start
        Player player = mcServer.server.getPlayer(par1Str);

        if (player != null) {
            player.recalculatePermissions();
        }

        // CraftBukkit end
    }

    /**
     * Determine if the player is allowed to connect based on current server settings.
     */
    public boolean isAllowedToLogin(String par1Str) {
        par1Str = par1Str.trim().toLowerCase();
        return !this.whiteListEnforced || this.ops.contains(par1Str) || this.whiteListedPlayers.contains(par1Str);
    }

    /**
     * Returns true if the specified player is opped, even if they're currently offline.
     */
    public boolean isPlayerOpped(String par1Str) {
        if (par1Str == null)
            return false; // Cauldron - fixes Aether ServerPlayerAPI initialization which passes a null username
        return this.ops.contains(par1Str.trim().toLowerCase()) || (this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && this.mcServer.getServerOwner().equalsIgnoreCase(par1Str)) || this.commandsAllowedForAll;
    }

    public EntityPlayerMP getPlayerForUsername(String par1Str) {
        Iterator iterator = this.playerEntityList.iterator();
        EntityPlayerMP entityplayermp;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityplayermp = (EntityPlayerMP) iterator.next();
        }
        while (!entityplayermp.getCommandSenderName().equalsIgnoreCase(par1Str));

        return entityplayermp;
    }

    /**
     * Find all players in a specified range and narrowing down by other parameters
     */
    public List findPlayers(ChunkCoordinates par1ChunkCoordinates, int par2, int par3, int par4, int par5, int par6, int par7, Map par8Map, String par9Str, String par10Str, World par11World) {
        if (this.playerEntityList.isEmpty()) {
            return null;
        } else {
            Object object = new ArrayList();
            boolean flag = par4 < 0;
            boolean flag1 = par9Str != null && par9Str.startsWith("!");
            boolean flag2 = par10Str != null && par10Str.startsWith("!");
            int k1 = par2 * par2;
            int l1 = par3 * par3;
            par4 = MathHelper.abs_int(par4);

            if (flag1) {
                par9Str = par9Str.substring(1);
            }

            if (flag2) {
                par10Str = par10Str.substring(1);
            }

            for (int i2 = 0; i2 < this.playerEntityList.size(); ++i2) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i2);

                if ((par11World == null || entityplayermp.worldObj == par11World) && (par9Str == null || flag1 != par9Str.equalsIgnoreCase(entityplayermp.getEntityName()))) {
                    if (par10Str != null) {
                        Team team = entityplayermp.getTeam();
                        String s2 = team == null ? "" : team.func_96661_b();

                        if (flag2 == par10Str.equalsIgnoreCase(s2)) {
                            continue;
                        }
                    }

                    if (par1ChunkCoordinates != null && (par2 > 0 || par3 > 0)) {
                        float f = par1ChunkCoordinates.getDistanceSquaredToChunkCoordinates(entityplayermp.getPlayerCoordinates());

                        if (par2 > 0 && f < (float) k1 || par3 > 0 && f > (float) l1) {
                            continue;
                        }
                    }

                    if (this.func_96457_a(entityplayermp, par8Map) && (par5 == EnumGameType.NOT_SET.getID() || par5 == entityplayermp.theItemInWorldManager.getGameType().getID()) && (par6 <= 0 || entityplayermp.experienceLevel >= par6) && entityplayermp.experienceLevel <= par7) {
                        ((List) object).add(entityplayermp);
                    }
                }
            }

            if (par1ChunkCoordinates != null) {
                Collections.sort((List) object, new PlayerPositionComparator(par1ChunkCoordinates));
            }

            if (flag) {
                Collections.reverse((List) object);
            }

            if (par4 > 0) {
                object = ((List) object).subList(0, Math.min(par4, ((List) object).size()));
            }

            return (List) object;
        }
    }

    private boolean func_96457_a(EntityPlayer par1EntityPlayer, Map par2Map) {
        if (par2Map != null && par2Map.size() != 0) {
            Iterator iterator = par2Map.entrySet().iterator();
            Entry entry;
            boolean flag;
            int i;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entry = (Entry) iterator.next();
                String s = (String) entry.getKey();
                flag = false;

                if (s.endsWith("_min") && s.length() > 4) {
                    flag = true;
                    s = s.substring(0, s.length() - 4);
                }

                Scoreboard scoreboard = par1EntityPlayer.getWorldScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjective(s);

                if (scoreobjective == null) {
                    return false;
                }

                Score score = par1EntityPlayer.getWorldScoreboard().func_96529_a(par1EntityPlayer.getEntityName(), scoreobjective);
                i = score.getScorePoints();

                if (i < ((Integer) entry.getValue()).intValue() && flag) {
                    return false;
                }
            }
            while (i <= ((Integer) entry.getValue()).intValue() || flag);

            return false;
        } else {
            return true;
        }
    }

    /**
     * params: x,y,z,d,dimension. The packet is sent to all players within d distance of x,y,z (d^2<x^2+y^2+z^2)
     */
    public void sendToAllNear(double par1, double par3, double par5, double par7, int par9, Packet par10Packet) {
        this.sendToAllNearExcept((EntityPlayer) null, par1, par3, par5, par7, par9, par10Packet);
    }

    /**
     * params: srcPlayer,x,y,z,d,dimension. The packet is not sent to the srcPlayer, but all other players where
     * dx*dx+dy*dy+dz*dz<d*d
     */
    public void sendToAllNearExcept(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, double par8, int par10, Packet par11Packet) {
        for (int j = 0; j < this.playerEntityList.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(j);

            // CraftBukkit start - Test if player receiving packet can see the source of the packet
            if (par1EntityPlayer != null && par1EntityPlayer instanceof EntityPlayerMP && !entityplayermp.getBukkitEntity().canSee(((EntityPlayerMP) par1EntityPlayer).getBukkitEntity())) {
                continue;
            }

            // CraftBukkit end
            if (entityplayermp != par1EntityPlayer && entityplayermp.dimension == par10) {
                double d4 = par2 - entityplayermp.posX;
                double d5 = par4 - entityplayermp.posY;
                double d6 = par6 - entityplayermp.posZ;

                // Cauldron start - send packets only to players within configured player tracking range)
                if (par8 > org.spigotmc.TrackingRange.getEntityTrackingRange(entityplayermp, 512)) {
                    par8 = org.spigotmc.TrackingRange.getEntityTrackingRange(entityplayermp, 512);
                }
                // Cauldron end

                if (d4 * d4 + d5 * d5 + d6 * d6 < par8 * par8) {
                    entityplayermp.playerNetServerHandler.sendPacketToPlayer(par11Packet);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void saveAllPlayerData() {
        for (int i = 0; i < this.playerEntityList.size(); ++i) {
            this.writePlayerData((EntityPlayerMP) this.playerEntityList.get(i));
        }
    }

    /**
     * Add the specified player to the white list.
     */
    public void addToWhiteList(String par1Str) {
        this.whiteListedPlayers.add(par1Str);
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeFromWhitelist(String par1Str) {
        this.whiteListedPlayers.remove(par1Str);
    }

    /**
     * Returns the whitelisted players.
     */
    public Set getWhiteListedPlayers() {
        return this.whiteListedPlayers;
    }

    public Set getOps() {
        return this.ops;
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList() {
    }

    /**
     * Updates the time and weather for the given player to those of the given world
     */
    public void updateTimeAndWeatherForPlayer(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer) {
        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet4UpdateTime(par2WorldServer.getTotalWorldTime(), par2WorldServer.getWorldTime(), par2WorldServer.getGameRules().getGameRuleBooleanValue("doDaylightCycle")));

        if (par2WorldServer.isRaining()) {
            par1EntityPlayerMP.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL, false); // CraftBukkit - handle player specific weather
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void syncPlayerInventory(EntityPlayerMP par1EntityPlayerMP) {
        par1EntityPlayerMP.sendContainerToPlayer(par1EntityPlayerMP.inventoryContainer);
        par1EntityPlayerMP.getBukkitEntity().updateScaledHealth(); // CraftBukkit - Update scaled health on respawn and worldchange
        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet16BlockItemSwitch(par1EntityPlayerMP.inventory.currentItem));
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount() {
        return this.playerEntityList.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat() {
        // Cauldron start - don't crash if the overworld isn't loaded
        List<WorldServer> worldServers = this.mcServer.worlds;
        return worldServers.isEmpty() ? new String[0] : worldServers.get(0).getSaveHandler().getSaveHandler().getAvailablePlayerDat(); // CraftBukkit
        // Cauldron end
    }

    public boolean isWhiteListEnabled() {
        return this.whiteListEnforced;
    }

    public void setWhiteListEnabled(boolean par1) {
        this.whiteListEnforced = par1;
    }

    public List getPlayerList(String par1Str) {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.playerEntityList.iterator();

        while (iterator.hasNext()) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) iterator.next();

            if (entityplayermp.getPlayerIP().equals(par1Str)) {
                arraylist.add(entityplayermp);
            }
        }

        return arraylist;
    }

    /**
     * Gets the View Distance.
     */
    public int getViewDistance() {
        return this.viewDistance;
    }

    public MinecraftServer getServerInstance() {
        return this.mcServer;
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void setGameType(EnumGameType par1EnumGameType) {
        this.gameType = par1EnumGameType;
    }

    private void func_72381_a(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP, World par3World) {
        if (par2EntityPlayerMP != null) {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(par2EntityPlayerMP.theItemInWorldManager.getGameType());
        } else if (this.gameType != null) {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(this.gameType);
        }

        par1EntityPlayerMP.theItemInWorldManager.initializeGameType(par3World.getWorldInfo().getGameType());
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets whether all players are allowed to use commands (cheats) on the server.
     */
    public void setCommandsAllowedForAll(boolean par1) {
        this.commandsAllowedForAll = par1;
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void removeAllPlayers() {
        while (!this.playerEntityList.isEmpty()) {
            // Spigot start
            EntityPlayerMP p = (EntityPlayerMP) this.playerEntityList.get(0);
            p.playerNetServerHandler.kickPlayerFromServer(this.mcServer.server.getShutdownMessage());

            if ((!this.playerEntityList.isEmpty()) && (this.playerEntityList.get(0) == p)) {
                this.playerEntityList.remove(0); // Prevent shutdown hang if already disconnected
            }

            // Spigot end
        }
    }

    public void func_110459_a(ChatMessageComponent par1ChatMessageComponent, boolean par2) {
        this.mcServer.sendChatToPlayer(par1ChatMessageComponent);
        this.sendPacketToAllPlayers(new Packet3Chat(par1ChatMessageComponent, par2));
    }

    /**
     * Sends the given string to every player as chat message.
     */
    public void sendChatMsg(ChatMessageComponent par1ChatMessageComponent) {
        this.func_110459_a(par1ChatMessageComponent, true);
    }
}
