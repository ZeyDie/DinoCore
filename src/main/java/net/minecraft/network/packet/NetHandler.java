package net.minecraft.network.packet;

import net.minecraft.entity.player.EntityPlayer;

public abstract class NetHandler
{
    /**
     * determine if it is a server handler
     */
    public abstract boolean isServerHandler();

    /**
     * Handle Packet51MapChunk (full chunk update of blocks, metadata, light levels, and optionally biome data)
     */
    public void handleMapChunk(final Packet51MapChunk par1Packet51MapChunk) {}

    /**
     * Default handler called for packets that don't have their own handlers in NetClientHandler; currentlly does
     * nothing.
     */
    public void unexpectedPacket(final Packet par1Packet) {}

    public void handleErrorMessage(final String par1Str, final Object[] par2ArrayOfObj) {}

    public void handleKickDisconnect(final Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.unexpectedPacket(par1Packet255KickDisconnect);
    }

    public void handleLogin(final Packet1Login par1Packet1Login)
    {
        this.unexpectedPacket(par1Packet1Login);
    }

    public void handleFlying(final Packet10Flying par1Packet10Flying)
    {
        this.unexpectedPacket(par1Packet10Flying);
    }

    public void handleMultiBlockChange(final Packet52MultiBlockChange par1Packet52MultiBlockChange)
    {
        this.unexpectedPacket(par1Packet52MultiBlockChange);
    }

    public void handleBlockDig(final Packet14BlockDig par1Packet14BlockDig)
    {
        this.unexpectedPacket(par1Packet14BlockDig);
    }

    public void handleBlockChange(final Packet53BlockChange par1Packet53BlockChange)
    {
        this.unexpectedPacket(par1Packet53BlockChange);
    }

    public void handleNamedEntitySpawn(final Packet20NamedEntitySpawn par1Packet20NamedEntitySpawn)
    {
        this.unexpectedPacket(par1Packet20NamedEntitySpawn);
    }

    public void handleEntity(final Packet30Entity par1Packet30Entity)
    {
        this.unexpectedPacket(par1Packet30Entity);
    }

    public void handleEntityTeleport(final Packet34EntityTeleport par1Packet34EntityTeleport)
    {
        this.unexpectedPacket(par1Packet34EntityTeleport);
    }

    public void handlePlace(final Packet15Place par1Packet15Place)
    {
        this.unexpectedPacket(par1Packet15Place);
    }

    public void handleBlockItemSwitch(final Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        this.unexpectedPacket(par1Packet16BlockItemSwitch);
    }

    public void handleDestroyEntity(final Packet29DestroyEntity par1Packet29DestroyEntity)
    {
        this.unexpectedPacket(par1Packet29DestroyEntity);
    }

    public void handleCollect(final Packet22Collect par1Packet22Collect)
    {
        this.unexpectedPacket(par1Packet22Collect);
    }

    public void handleChat(final Packet3Chat par1Packet3Chat)
    {
        this.unexpectedPacket(par1Packet3Chat);
    }

    public void handleVehicleSpawn(final Packet23VehicleSpawn par1Packet23VehicleSpawn)
    {
        this.unexpectedPacket(par1Packet23VehicleSpawn);
    }

    public void handleAnimation(final Packet18Animation par1Packet18Animation)
    {
        this.unexpectedPacket(par1Packet18Animation);
    }

    /**
     * runs registerPacket on the given Packet19EntityAction
     */
    public void handleEntityAction(final Packet19EntityAction par1Packet19EntityAction)
    {
        this.unexpectedPacket(par1Packet19EntityAction);
    }

    public void handleClientProtocol(final Packet2ClientProtocol par1Packet2ClientProtocol)
    {
        this.unexpectedPacket(par1Packet2ClientProtocol);
    }

    public void handleServerAuthData(final Packet253ServerAuthData par1Packet253ServerAuthData)
    {
        this.unexpectedPacket(par1Packet253ServerAuthData);
    }

    public void handleSharedKey(final Packet252SharedKey par1Packet252SharedKey)
    {
        this.unexpectedPacket(par1Packet252SharedKey);
    }

    public void handleMobSpawn(final Packet24MobSpawn par1Packet24MobSpawn)
    {
        this.unexpectedPacket(par1Packet24MobSpawn);
    }

    public void handleUpdateTime(final Packet4UpdateTime par1Packet4UpdateTime)
    {
        this.unexpectedPacket(par1Packet4UpdateTime);
    }

    public void handleSpawnPosition(final Packet6SpawnPosition par1Packet6SpawnPosition)
    {
        this.unexpectedPacket(par1Packet6SpawnPosition);
    }

    /**
     * Packet handler
     */
    public void handleEntityVelocity(final Packet28EntityVelocity par1Packet28EntityVelocity)
    {
        this.unexpectedPacket(par1Packet28EntityVelocity);
    }

    /**
     * Packet handler
     */
    public void handleEntityMetadata(final Packet40EntityMetadata par1Packet40EntityMetadata)
    {
        this.unexpectedPacket(par1Packet40EntityMetadata);
    }

    /**
     * Packet handler
     */
    public void handleAttachEntity(final Packet39AttachEntity par1Packet39AttachEntity)
    {
        this.unexpectedPacket(par1Packet39AttachEntity);
    }

    public void handleUseEntity(final Packet7UseEntity par1Packet7UseEntity)
    {
        this.unexpectedPacket(par1Packet7UseEntity);
    }

    /**
     * Packet handler
     */
    public void handleEntityStatus(final Packet38EntityStatus par1Packet38EntityStatus)
    {
        this.unexpectedPacket(par1Packet38EntityStatus);
    }

    /**
     * Recieves player health from the server and then proceeds to set it locally on the client.
     */
    public void handleUpdateHealth(final Packet8UpdateHealth par1Packet8UpdateHealth)
    {
        this.unexpectedPacket(par1Packet8UpdateHealth);
    }

    /**
     * respawns the player
     */
    public void handleRespawn(final Packet9Respawn par1Packet9Respawn)
    {
        this.unexpectedPacket(par1Packet9Respawn);
    }

    public void handleExplosion(final Packet60Explosion par1Packet60Explosion)
    {
        this.unexpectedPacket(par1Packet60Explosion);
    }

    public void handleOpenWindow(final Packet100OpenWindow par1Packet100OpenWindow)
    {
        this.unexpectedPacket(par1Packet100OpenWindow);
    }

    public void handleCloseWindow(final Packet101CloseWindow par1Packet101CloseWindow)
    {
        this.unexpectedPacket(par1Packet101CloseWindow);
    }

    public void handleWindowClick(final Packet102WindowClick par1Packet102WindowClick)
    {
        this.unexpectedPacket(par1Packet102WindowClick);
    }

    public void handleSetSlot(final Packet103SetSlot par1Packet103SetSlot)
    {
        this.unexpectedPacket(par1Packet103SetSlot);
    }

    public void handleWindowItems(final Packet104WindowItems par1Packet104WindowItems)
    {
        this.unexpectedPacket(par1Packet104WindowItems);
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(final Packet130UpdateSign par1Packet130UpdateSign)
    {
        this.unexpectedPacket(par1Packet130UpdateSign);
    }

    public void handleUpdateProgressbar(final Packet105UpdateProgressbar par1Packet105UpdateProgressbar)
    {
        this.unexpectedPacket(par1Packet105UpdateProgressbar);
    }

    public void handlePlayerInventory(final Packet5PlayerInventory par1Packet5PlayerInventory)
    {
        this.unexpectedPacket(par1Packet5PlayerInventory);
    }

    public void handleTransaction(final Packet106Transaction par1Packet106Transaction)
    {
        this.unexpectedPacket(par1Packet106Transaction);
    }

    /**
     * Packet handler
     */
    public void handleEntityPainting(final Packet25EntityPainting par1Packet25EntityPainting)
    {
        this.unexpectedPacket(par1Packet25EntityPainting);
    }

    public void handleBlockEvent(final Packet54PlayNoteBlock par1Packet54PlayNoteBlock)
    {
        this.unexpectedPacket(par1Packet54PlayNoteBlock);
    }

    /**
     * Increment player statistics
     */
    public void handleStatistic(final Packet200Statistic par1Packet200Statistic)
    {
        this.unexpectedPacket(par1Packet200Statistic);
    }

    public void handleSleep(final Packet17Sleep par1Packet17Sleep)
    {
        this.unexpectedPacket(par1Packet17Sleep);
    }

    public void func_110774_a(final Packet27PlayerInput par1Packet27PlayerInput)
    {
        this.unexpectedPacket(par1Packet27PlayerInput);
    }

    public void handleGameEvent(final Packet70GameEvent par1Packet70GameEvent)
    {
        this.unexpectedPacket(par1Packet70GameEvent);
    }

    /**
     * Handles weather packet
     */
    public void handleWeather(final Packet71Weather par1Packet71Weather)
    {
        this.unexpectedPacket(par1Packet71Weather);
    }

    /**
     * Contains logic for handling packets containing arbitrary unique item data. Currently this is only for maps.
     */
    public void handleMapData(final Packet131MapData par1Packet131MapData)
    {
        this.unexpectedPacket(par1Packet131MapData);
    }

    public void handleDoorChange(final Packet61DoorChange par1Packet61DoorChange)
    {
        this.unexpectedPacket(par1Packet61DoorChange);
    }

    /**
     * Handle a server ping packet.
     */
    public void handleServerPing(final Packet254ServerPing par1Packet254ServerPing)
    {
        this.unexpectedPacket(par1Packet254ServerPing);
    }

    /**
     * Handle an entity effect packet.
     */
    public void handleEntityEffect(final Packet41EntityEffect par1Packet41EntityEffect)
    {
        this.unexpectedPacket(par1Packet41EntityEffect);
    }

    /**
     * Handle a remove entity effect packet.
     */
    public void handleRemoveEntityEffect(final Packet42RemoveEntityEffect par1Packet42RemoveEntityEffect)
    {
        this.unexpectedPacket(par1Packet42RemoveEntityEffect);
    }

    /**
     * Handle a player information packet.
     */
    public void handlePlayerInfo(final Packet201PlayerInfo par1Packet201PlayerInfo)
    {
        this.unexpectedPacket(par1Packet201PlayerInfo);
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(final Packet0KeepAlive par1Packet0KeepAlive)
    {
        this.unexpectedPacket(par1Packet0KeepAlive);
    }

    /**
     * Handle an experience packet.
     */
    public void handleExperience(final Packet43Experience par1Packet43Experience)
    {
        this.unexpectedPacket(par1Packet43Experience);
    }

    /**
     * Handle a creative slot packet.
     */
    public void handleCreativeSetSlot(final Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
    {
        this.unexpectedPacket(par1Packet107CreativeSetSlot);
    }

    /**
     * Handle a entity experience orb packet.
     */
    public void handleEntityExpOrb(final Packet26EntityExpOrb par1Packet26EntityExpOrb)
    {
        this.unexpectedPacket(par1Packet26EntityExpOrb);
    }

    public void handleEnchantItem(final Packet108EnchantItem par1Packet108EnchantItem) {}

    public void handleCustomPayload(final Packet250CustomPayload par1Packet250CustomPayload) {}

    public void handleEntityHeadRotation(final Packet35EntityHeadRotation par1Packet35EntityHeadRotation)
    {
        this.unexpectedPacket(par1Packet35EntityHeadRotation);
    }

    public void handleTileEntityData(final Packet132TileEntityData par1Packet132TileEntityData)
    {
        this.unexpectedPacket(par1Packet132TileEntityData);
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(final Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        this.unexpectedPacket(par1Packet202PlayerAbilities);
    }

    public void handleAutoComplete(final Packet203AutoComplete par1Packet203AutoComplete)
    {
        this.unexpectedPacket(par1Packet203AutoComplete);
    }

    public void handleClientInfo(final Packet204ClientInfo par1Packet204ClientInfo)
    {
        this.unexpectedPacket(par1Packet204ClientInfo);
    }

    public void handleLevelSound(final Packet62LevelSound par1Packet62LevelSound)
    {
        this.unexpectedPacket(par1Packet62LevelSound);
    }

    public void handleBlockDestroy(final Packet55BlockDestroy par1Packet55BlockDestroy)
    {
        this.unexpectedPacket(par1Packet55BlockDestroy);
    }

    public void handleClientCommand(final Packet205ClientCommand par1Packet205ClientCommand) {}

    public void handleMapChunks(final Packet56MapChunks par1Packet56MapChunks)
    {
        this.unexpectedPacket(par1Packet56MapChunks);
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return false;
    }

    /**
     * Handle a set objective packet.
     */
    public void handleSetObjective(final Packet206SetObjective par1Packet206SetObjective)
    {
        this.unexpectedPacket(par1Packet206SetObjective);
    }

    /**
     * Handle a set score packet.
     */
    public void handleSetScore(final Packet207SetScore par1Packet207SetScore)
    {
        this.unexpectedPacket(par1Packet207SetScore);
    }

    /**
     * Handle a set display objective packet.
     */
    public void handleSetDisplayObjective(final Packet208SetDisplayObjective par1Packet208SetDisplayObjective)
    {
        this.unexpectedPacket(par1Packet208SetDisplayObjective);
    }

    /**
     * Handle a set player team packet.
     */
    public void handleSetPlayerTeam(final Packet209SetPlayerTeam par1Packet209SetPlayerTeam)
    {
        this.unexpectedPacket(par1Packet209SetPlayerTeam);
    }

    /**
     * Handle a world particles packet.
     */
    public void handleWorldParticles(final Packet63WorldParticles par1Packet63WorldParticles)
    {
        this.unexpectedPacket(par1Packet63WorldParticles);
    }

    public void func_110773_a(final Packet44UpdateAttributes par1Packet44UpdateAttributes)
    {
        this.unexpectedPacket(par1Packet44UpdateAttributes);
    }

    public void func_142031_a(final Packet133TileEditorOpen par1Packet133TileEditorOpen) {}

    public boolean isDisconnected()
    {
        return false;
    }

    public abstract void handleVanilla250Packet(Packet250CustomPayload payload);

    public abstract EntityPlayer getPlayer();
}
