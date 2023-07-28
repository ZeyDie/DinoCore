package net.minecraft.client.multiplayer;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.packet.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.*;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.*;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.*;

@SideOnly(Side.CLIENT)
public class NetClientHandler extends NetHandler
{
    /** True if kicked or disconnected from the server. */
    private boolean disconnected;

    /** Reference to the NetworkManager object. */
    private INetworkManager netManager;
    public String field_72560_a;

    /** Reference to the Minecraft object. */
    private Minecraft mc;
    private WorldClient worldClient;

    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of a player position packet,
     * reset upon respawning.
     */
    private boolean doneLoadingTerrain;
    public MapStorage mapStorage = new MapStorage((ISaveHandler)null);

    /** A HashMap of all player names and their player information objects */
    private Map playerInfoMap = new HashMap();

    /**
     * An ArrayList of GuiPlayerInfo (includes all the players' GuiPlayerInfo on the current server)
     */
    public List playerInfoList = new ArrayList();
    public int currentServerMaxPlayers = 20;
    private GuiScreen field_98183_l;

    /** RNG. */
    Random rand = new Random();

    private static byte connectionCompatibilityLevel;

    public NetClientHandler(final Minecraft par1Minecraft, final String par2Str, final int par3) throws IOException
    {
        this.mc = par1Minecraft;
        final Socket socket = new Socket(InetAddress.getByName(par2Str), par3);
        //TODO ZeyCodeClear
        //this.netManager = new TcpConnection(par1Minecraft.getLogAgent(), socket, "Client", this);
        FMLNetworkHandler.onClientConnectionToRemoteServer(this, par2Str, par3, this.netManager);
    }

    public NetClientHandler(final Minecraft par1Minecraft, final String par2Str, final int par3, final GuiScreen par4GuiScreen) throws IOException
    {
        this.mc = par1Minecraft;
        this.field_98183_l = par4GuiScreen;
        final Socket socket = new Socket(InetAddress.getByName(par2Str), par3);
        //TODO ZeyCodeClear
        //this.netManager = new TcpConnection(par1Minecraft.getLogAgent(), socket, "Client", this);
        FMLNetworkHandler.onClientConnectionToRemoteServer(this, par2Str, par3, this.netManager);
    }

    public NetClientHandler(final Minecraft par1Minecraft, final IntegratedServer par2IntegratedServer) throws IOException
    {
        this.mc = par1Minecraft;
        this.netManager = new MemoryConnection(par1Minecraft.getLogAgent(), this);
        par2IntegratedServer.getServerListeningThread().func_71754_a((MemoryConnection)this.netManager, par1Minecraft.getSession().getUsername());
        FMLNetworkHandler.onClientConnectionToIntegratedServer(this, par2IntegratedServer, this.netManager);
    }

    /**
     * sets netManager and worldClient to null
     */
    public void cleanup()
    {
        if (this.netManager != null)
        {
            this.netManager.wakeThreads();
        }

        this.netManager = null;
        this.worldClient = null;
    }

    /**
     * Processes the packets that have been read since the last call to this function.
     */
    public void processReadPackets()
    {
        if (!this.disconnected && this.netManager != null)
        {
            this.netManager.processReadPackets();
        }

        if (this.netManager != null)
        {
            this.netManager.wakeThreads();
        }
    }

    public void handleServerAuthData(final Packet253ServerAuthData par1Packet253ServerAuthData)
    {
        final String s = par1Packet253ServerAuthData.getServerId().trim();
        final PublicKey publickey = par1Packet253ServerAuthData.getPublicKey();
        final SecretKey secretkey = CryptManager.createNewSharedKey();

        if (!"-".equals(s))
        {
            final String s1 = (new BigInteger(CryptManager.getServerIdHash(s, publickey, secretkey))).toString(16);
            final String s2 = this.sendSessionRequest(this.mc.getSession().getUsername(), this.mc.getSession().getSessionID(), s1);

            if (!"ok".equalsIgnoreCase(s2))
            {
                this.netManager.networkShutdown("disconnect.loginFailedInfo", new Object[] {s2});
                return;
            }
        }

        this.addToSendQueue(new Packet252SharedKey(secretkey, publickey, par1Packet253ServerAuthData.getVerifyToken()));
    }

    /**
     * Send request to http://session.minecraft.net with user's sessionId and serverId hash
     */
    private String sendSessionRequest(final String par1Str, final String par2Str, final String par3Str)
    {
        try
        {
            final URL url = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + urlEncode(par1Str) + "&sessionId=" + urlEncode(par2Str) + "&serverId=" + urlEncode(par3Str));
            final InputStream inputstream = url.openConnection(this.mc.getProxy()).getInputStream();
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
            final String s3 = bufferedreader.readLine();
            bufferedreader.close();
            return s3;
        }
        catch (final IOException ioexception)
        {
            return ioexception.toString();
        }
    }

    /**
     * Encode the given string for insertion into a URL
     */
    private static String urlEncode(final String par0Str) throws IOException
    {
        return URLEncoder.encode(par0Str, "UTF-8");
    }

    public void handleSharedKey(final Packet252SharedKey par1Packet252SharedKey)
    {
        this.addToSendQueue(FMLNetworkHandler.getFMLFakeLoginPacket());
        this.addToSendQueue(new Packet205ClientCommand(0));
    }

    public void handleLogin(final Packet1Login par1Packet1Login)
    {
        this.mc.playerController = new PlayerControllerMP(this.mc, this);
        this.mc.statFileWriter.readStat(StatList.joinMultiplayerStat, 1);
        this.worldClient = new WorldClient(this, new WorldSettings(0L, par1Packet1Login.gameType, false, par1Packet1Login.hardcoreMode, par1Packet1Login.terrainType), par1Packet1Login.dimension, par1Packet1Login.difficultySetting, this.mc.mcProfiler, this.mc.getLogAgent());
        this.worldClient.isRemote = true;
        this.mc.loadWorld(this.worldClient);
        this.mc.thePlayer.dimension = par1Packet1Login.dimension;
        this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
        this.mc.thePlayer.entityId = par1Packet1Login.clientEntityId;
        this.currentServerMaxPlayers = par1Packet1Login.maxPlayers;
        this.mc.playerController.setGameType(par1Packet1Login.gameType);
        FMLNetworkHandler.onConnectionEstablishedToServer(this, netManager, par1Packet1Login);
        this.mc.gameSettings.sendSettingsToServer();
        this.netManager.addToSendQueue(new Packet250CustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
    }

    public void handleVehicleSpawn(final Packet23VehicleSpawn par1Packet23VehicleSpawn)
    {
        final double d0 = (double)par1Packet23VehicleSpawn.xPosition / 32.0D;
        final double d1 = (double)par1Packet23VehicleSpawn.yPosition / 32.0D;
        final double d2 = (double)par1Packet23VehicleSpawn.zPosition / 32.0D;
        Object object = null;

        if (par1Packet23VehicleSpawn.type == 10)
        {
            object = EntityMinecart.createMinecart(this.worldClient, d0, d1, d2, par1Packet23VehicleSpawn.throwerEntityId);
        }
        else if (par1Packet23VehicleSpawn.type == 90)
        {
            final Entity entity = this.getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);

            if (entity instanceof EntityPlayer)
            {
                object = new EntityFishHook(this.worldClient, d0, d1, d2, (EntityPlayer)entity);
            }

            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 60)
        {
            object = new EntityArrow(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 61)
        {
            object = new EntitySnowball(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 71)
        {
            object = new EntityItemFrame(this.worldClient, (int)d0, (int)d1, (int)d2, par1Packet23VehicleSpawn.throwerEntityId);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 77)
        {
            object = new EntityLeashKnot(this.worldClient, (int)d0, (int)d1, (int)d2);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 65)
        {
            object = new EntityEnderPearl(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 72)
        {
            object = new EntityEnderEye(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 76)
        {
            object = new EntityFireworkRocket(this.worldClient, d0, d1, d2, (ItemStack)null);
        }
        else if (par1Packet23VehicleSpawn.type == 63)
        {
            object = new EntityLargeFireball(this.worldClient, d0, d1, d2, (double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 64)
        {
            object = new EntitySmallFireball(this.worldClient, d0, d1, d2, (double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 66)
        {
            object = new EntityWitherSkull(this.worldClient, d0, d1, d2, (double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 62)
        {
            object = new EntityEgg(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 73)
        {
            object = new EntityPotion(this.worldClient, d0, d1, d2, par1Packet23VehicleSpawn.throwerEntityId);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 75)
        {
            object = new EntityExpBottle(this.worldClient, d0, d1, d2);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }
        else if (par1Packet23VehicleSpawn.type == 1)
        {
            object = new EntityBoat(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 50)
        {
            object = new EntityTNTPrimed(this.worldClient, d0, d1, d2, (EntityLivingBase)null);
        }
        else if (par1Packet23VehicleSpawn.type == 51)
        {
            object = new EntityEnderCrystal(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 2)
        {
            object = new EntityItem(this.worldClient, d0, d1, d2);
        }
        else if (par1Packet23VehicleSpawn.type == 70)
        {
            object = new EntityFallingSand(this.worldClient, d0, d1, d2, par1Packet23VehicleSpawn.throwerEntityId & 65535, par1Packet23VehicleSpawn.throwerEntityId >> 16);
            par1Packet23VehicleSpawn.throwerEntityId = 0;
        }

        if (object != null)
        {
            ((Entity)object).serverPosX = par1Packet23VehicleSpawn.xPosition;
            ((Entity)object).serverPosY = par1Packet23VehicleSpawn.yPosition;
            ((Entity)object).serverPosZ = par1Packet23VehicleSpawn.zPosition;
            ((Entity)object).rotationPitch = (float)(par1Packet23VehicleSpawn.pitch * 360) / 256.0F;
            ((Entity)object).rotationYaw = (float)(par1Packet23VehicleSpawn.yaw * 360) / 256.0F;
            final Entity[] aentity = ((Entity)object).getParts();

            if (aentity != null)
            {
                final int i = par1Packet23VehicleSpawn.entityId - ((Entity)object).entityId;

                for (int j = 0; j < aentity.length; ++j)
                {
                    aentity[j].entityId += i;
                }
            }

            ((Entity)object).entityId = par1Packet23VehicleSpawn.entityId;
            this.worldClient.addEntityToWorld(par1Packet23VehicleSpawn.entityId, (Entity)object);

            if (par1Packet23VehicleSpawn.throwerEntityId > 0)
            {
                if (par1Packet23VehicleSpawn.type == 60)
                {
                    final Entity entity1 = this.getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);

                    if (entity1 instanceof EntityLivingBase)
                    {
                        final EntityArrow entityarrow = (EntityArrow)object;
                        entityarrow.shootingEntity = entity1;
                    }
                }

                ((Entity)object).setVelocity((double)par1Packet23VehicleSpawn.speedX / 8000.0D, (double)par1Packet23VehicleSpawn.speedY / 8000.0D, (double)par1Packet23VehicleSpawn.speedZ / 8000.0D);
            }
        }
    }

    /**
     * Handle a entity experience orb packet.
     */
    public void handleEntityExpOrb(final Packet26EntityExpOrb par1Packet26EntityExpOrb)
    {
        final EntityXPOrb entityxporb = new EntityXPOrb(this.worldClient, (double)par1Packet26EntityExpOrb.posX, (double)par1Packet26EntityExpOrb.posY, (double)par1Packet26EntityExpOrb.posZ, par1Packet26EntityExpOrb.xpValue);
        entityxporb.serverPosX = par1Packet26EntityExpOrb.posX;
        entityxporb.serverPosY = par1Packet26EntityExpOrb.posY;
        entityxporb.serverPosZ = par1Packet26EntityExpOrb.posZ;
        entityxporb.rotationYaw = 0.0F;
        entityxporb.rotationPitch = 0.0F;
        entityxporb.entityId = par1Packet26EntityExpOrb.entityId;
        this.worldClient.addEntityToWorld(par1Packet26EntityExpOrb.entityId, entityxporb);
    }

    /**
     * Handles weather packet
     */
    public void handleWeather(final Packet71Weather par1Packet71Weather)
    {
        final double d0 = (double)par1Packet71Weather.posX / 32.0D;
        final double d1 = (double)par1Packet71Weather.posY / 32.0D;
        final double d2 = (double)par1Packet71Weather.posZ / 32.0D;
        EntityLightningBolt entitylightningbolt = null;

        if (par1Packet71Weather.isLightningBolt == 1)
        {
            entitylightningbolt = new EntityLightningBolt(this.worldClient, d0, d1, d2);
        }

        if (entitylightningbolt != null)
        {
            entitylightningbolt.serverPosX = par1Packet71Weather.posX;
            entitylightningbolt.serverPosY = par1Packet71Weather.posY;
            entitylightningbolt.serverPosZ = par1Packet71Weather.posZ;
            entitylightningbolt.rotationYaw = 0.0F;
            entitylightningbolt.rotationPitch = 0.0F;
            entitylightningbolt.entityId = par1Packet71Weather.entityID;
            this.worldClient.addWeatherEffect(entitylightningbolt);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityPainting(final Packet25EntityPainting par1Packet25EntityPainting)
    {
        final EntityPainting entitypainting = new EntityPainting(this.worldClient, par1Packet25EntityPainting.xPosition, par1Packet25EntityPainting.yPosition, par1Packet25EntityPainting.zPosition, par1Packet25EntityPainting.direction, par1Packet25EntityPainting.title);
        this.worldClient.addEntityToWorld(par1Packet25EntityPainting.entityId, entitypainting);
    }

    /**
     * Packet handler
     */
    public void handleEntityVelocity(final Packet28EntityVelocity par1Packet28EntityVelocity)
    {
        final Entity entity = this.getEntityByID(par1Packet28EntityVelocity.entityId);

        if (entity != null)
        {
            entity.setVelocity((double)par1Packet28EntityVelocity.motionX / 8000.0D, (double)par1Packet28EntityVelocity.motionY / 8000.0D, (double)par1Packet28EntityVelocity.motionZ / 8000.0D);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityMetadata(final Packet40EntityMetadata par1Packet40EntityMetadata)
    {
        final Entity entity = this.getEntityByID(par1Packet40EntityMetadata.entityId);

        if (entity != null && par1Packet40EntityMetadata.getMetadata() != null)
        {
            entity.getDataWatcher().updateWatchedObjectsFromList(par1Packet40EntityMetadata.getMetadata());
        }
    }

    public void handleNamedEntitySpawn(final Packet20NamedEntitySpawn par1Packet20NamedEntitySpawn)
    {
        final double d0 = (double)par1Packet20NamedEntitySpawn.xPosition / 32.0D;
        final double d1 = (double)par1Packet20NamedEntitySpawn.yPosition / 32.0D;
        final double d2 = (double)par1Packet20NamedEntitySpawn.zPosition / 32.0D;
        final float f = (float)(par1Packet20NamedEntitySpawn.rotation * 360) / 256.0F;
        final float f1 = (float)(par1Packet20NamedEntitySpawn.pitch * 360) / 256.0F;
        final EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.mc.theWorld, par1Packet20NamedEntitySpawn.name);
        entityotherplayermp.prevPosX = entityotherplayermp.lastTickPosX = (double)(entityotherplayermp.serverPosX = par1Packet20NamedEntitySpawn.xPosition);
        entityotherplayermp.prevPosY = entityotherplayermp.lastTickPosY = (double)(entityotherplayermp.serverPosY = par1Packet20NamedEntitySpawn.yPosition);
        entityotherplayermp.prevPosZ = entityotherplayermp.lastTickPosZ = (double)(entityotherplayermp.serverPosZ = par1Packet20NamedEntitySpawn.zPosition);
        final int i = par1Packet20NamedEntitySpawn.currentItem;

        if (i == 0)
        {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = null;
        }
        else
        {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = new ItemStack(i, 1, 0);
        }

        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.worldClient.addEntityToWorld(par1Packet20NamedEntitySpawn.entityId, entityotherplayermp);
        final List list = par1Packet20NamedEntitySpawn.getWatchedMetadata();

        if (list != null)
        {
            entityotherplayermp.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleEntityTeleport(final Packet34EntityTeleport par1Packet34EntityTeleport)
    {
        final Entity entity = this.getEntityByID(par1Packet34EntityTeleport.entityId);

        if (entity != null)
        {
            entity.serverPosX = par1Packet34EntityTeleport.xPosition;
            entity.serverPosY = par1Packet34EntityTeleport.yPosition;
            entity.serverPosZ = par1Packet34EntityTeleport.zPosition;
            final double d0 = (double)entity.serverPosX / 32.0D;
            final double d1 = (double)entity.serverPosY / 32.0D + 0.015625D;
            final double d2 = (double)entity.serverPosZ / 32.0D;
            final float f = (float)(par1Packet34EntityTeleport.yaw * 360) / 256.0F;
            final float f1 = (float)(par1Packet34EntityTeleport.pitch * 360) / 256.0F;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3);
        }
    }

    public void handleBlockItemSwitch(final Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize())
        {
            this.mc.thePlayer.inventory.currentItem = par1Packet16BlockItemSwitch.id;
        }
    }

    public void handleEntity(final Packet30Entity par1Packet30Entity)
    {
        final Entity entity = this.getEntityByID(par1Packet30Entity.entityId);

        if (entity != null)
        {
            entity.serverPosX += par1Packet30Entity.xPosition;
            entity.serverPosY += par1Packet30Entity.yPosition;
            entity.serverPosZ += par1Packet30Entity.zPosition;
            final double d0 = (double)entity.serverPosX / 32.0D;
            final double d1 = (double)entity.serverPosY / 32.0D;
            final double d2 = (double)entity.serverPosZ / 32.0D;
            final float f = par1Packet30Entity.rotating ? (float)(par1Packet30Entity.yaw * 360) / 256.0F : entity.rotationYaw;
            final float f1 = par1Packet30Entity.rotating ? (float)(par1Packet30Entity.pitch * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3);
        }
    }

    public void handleEntityHeadRotation(final Packet35EntityHeadRotation par1Packet35EntityHeadRotation)
    {
        final Entity entity = this.getEntityByID(par1Packet35EntityHeadRotation.entityId);

        if (entity != null)
        {
            final float f = (float)(par1Packet35EntityHeadRotation.headRotationYaw * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    public void handleDestroyEntity(final Packet29DestroyEntity par1Packet29DestroyEntity)
    {
        for (int i = 0; i < par1Packet29DestroyEntity.entityId.length; ++i)
        {
            this.worldClient.removeEntityFromWorld(par1Packet29DestroyEntity.entityId[i]);
        }
    }

    public void handleFlying(final Packet10Flying par1Packet10Flying)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        double d0 = entityclientplayermp.posX;
        double d1 = entityclientplayermp.posY;
        double d2 = entityclientplayermp.posZ;
        float f = entityclientplayermp.rotationYaw;
        float f1 = entityclientplayermp.rotationPitch;

        if (par1Packet10Flying.moving)
        {
            d0 = par1Packet10Flying.xPosition;
            d1 = par1Packet10Flying.yPosition;
            d2 = par1Packet10Flying.zPosition;
        }

        if (par1Packet10Flying.rotating)
        {
            f = par1Packet10Flying.yaw;
            f1 = par1Packet10Flying.pitch;
        }

        entityclientplayermp.ySize = 0.0F;
        entityclientplayermp.motionX = entityclientplayermp.motionY = entityclientplayermp.motionZ = 0.0D;
        entityclientplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        par1Packet10Flying.xPosition = entityclientplayermp.posX;
        par1Packet10Flying.yPosition = entityclientplayermp.boundingBox.minY;
        par1Packet10Flying.zPosition = entityclientplayermp.posZ;
        par1Packet10Flying.stance = entityclientplayermp.posY;
        this.netManager.addToSendQueue(par1Packet10Flying);

        if (!this.doneLoadingTerrain)
        {
            this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
            this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
            this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    public void handleMultiBlockChange(final Packet52MultiBlockChange par1Packet52MultiBlockChange)
    {
        final int i = par1Packet52MultiBlockChange.xPosition * 16;
        final int j = par1Packet52MultiBlockChange.zPosition * 16;

        if (par1Packet52MultiBlockChange.metadataArray != null)
        {
            final DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(par1Packet52MultiBlockChange.metadataArray));

            try
            {
                for (int k = 0; k < par1Packet52MultiBlockChange.size; ++k)
                {
                    final short short1 = datainputstream.readShort();
                    final short short2 = datainputstream.readShort();
                    final int l = short2 >> 4 & 4095;
                    final int i1 = short2 & 15;
                    final int j1 = short1 >> 12 & 15;
                    final int k1 = short1 >> 8 & 15;
                    final int l1 = short1 & 255;
                    this.worldClient.setBlockAndMetadataAndInvalidate(j1 + i, l1, k1 + j, l, i1);
                }
            }
            catch (final IOException ioexception)
            {
                ;
            }
        }
    }

    /**
     * Handle Packet51MapChunk (full chunk update of blocks, metadata, light levels, and optionally biome data)
     */
    public void handleMapChunk(final Packet51MapChunk par1Packet51MapChunk)
    {
        if (par1Packet51MapChunk.includeInitialize)
        {
            if (par1Packet51MapChunk.yChMin == 0)
            {
                this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, false);
                return;
            }

            this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, true);
        }

        this.worldClient.invalidateBlockReceiveRegion(par1Packet51MapChunk.xCh << 4, 0, par1Packet51MapChunk.zCh << 4, (par1Packet51MapChunk.xCh << 4) + 15, 256, (par1Packet51MapChunk.zCh << 4) + 15);
        Chunk chunk = this.worldClient.getChunkFromChunkCoords(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh);

        if (par1Packet51MapChunk.includeInitialize && chunk == null)
        {
            this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, true);
            chunk = this.worldClient.getChunkFromChunkCoords(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh);
        }

        if (chunk != null)
        {
            chunk.fillChunk(par1Packet51MapChunk.getCompressedChunkData(), par1Packet51MapChunk.yChMin, par1Packet51MapChunk.yChMax, par1Packet51MapChunk.includeInitialize);
            this.worldClient.markBlockRangeForRenderUpdate(par1Packet51MapChunk.xCh << 4, 0, par1Packet51MapChunk.zCh << 4, (par1Packet51MapChunk.xCh << 4) + 15, 256, (par1Packet51MapChunk.zCh << 4) + 15);

            if (!par1Packet51MapChunk.includeInitialize || !(this.worldClient.provider instanceof WorldProviderSurface))
            {
                chunk.resetRelightChecks();
            }
        }
    }

    public void handleBlockChange(final Packet53BlockChange par1Packet53BlockChange)
    {
        this.worldClient.setBlockAndMetadataAndInvalidate(par1Packet53BlockChange.xPosition, par1Packet53BlockChange.yPosition, par1Packet53BlockChange.zPosition, par1Packet53BlockChange.type, par1Packet53BlockChange.metadata);
    }

    public void handleKickDisconnect(final Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.netManager.networkShutdown("disconnect.kicked", par1Packet255KickDisconnect.reason);
        this.disconnected = true;
        this.mc.loadWorld((WorldClient)null);

        if (this.field_98183_l != null)
        {
            this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.disconnected", "disconnect.genericReason", new Object[] {par1Packet255KickDisconnect.reason}));
        }
        else
        {
            this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.disconnected", "disconnect.genericReason", new Object[] {par1Packet255KickDisconnect.reason}));
        }
    }

    public void handleErrorMessage(final String par1Str, final Object[] par2ArrayOfObj)
    {
        if (!this.disconnected)
        {
            this.disconnected = true;
            this.mc.loadWorld((WorldClient)null);

            if (this.field_98183_l != null)
            {
                this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.lost", par1Str, par2ArrayOfObj));
            }
            else
            {
                this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", par1Str, par2ArrayOfObj));
            }
        }
    }

    public void quitWithPacket(final Packet par1Packet)
    {
        if (!this.disconnected)
        {
            this.netManager.addToSendQueue(par1Packet);
            this.netManager.serverShutdown();
            FMLNetworkHandler.onConnectionClosed(this.netManager, this.getPlayer());
        }
    }

    /**
     * Adds the packet to the send queue
     */
    public void addToSendQueue(final Packet par1Packet)
    {
        if (!this.disconnected)
        {
            this.netManager.addToSendQueue(par1Packet);
        }
    }

    public void handleCollect(final Packet22Collect par1Packet22Collect)
    {
        final Entity entity = this.getEntityByID(par1Packet22Collect.collectedEntityId);
        Object object = (EntityLivingBase)this.getEntityByID(par1Packet22Collect.collectorEntityId);

        if (object == null)
        {
            object = this.mc.thePlayer;
        }

        if (entity != null)
        {
            if (entity instanceof EntityXPOrb)
            {
                this.worldClient.playSoundAtEntity(entity, "random.orb", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            else
            {
                this.worldClient.playSoundAtEntity(entity, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, entity, (Entity)object, -0.5F));
            this.worldClient.removeEntityFromWorld(par1Packet22Collect.collectedEntityId);
        }
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
        Packet3Chat par1Packet3Chat1 = FMLNetworkHandler.handleChatMessage(this, par1Packet3Chat);
        if (par1Packet3Chat1 == null)
        {
            return;
        }
        final ClientChatReceivedEvent event = new ClientChatReceivedEvent(par1Packet3Chat1.message);
        if (!MinecraftForge.EVENT_BUS.post(event) && event.message != null)
        {
            this.mc.ingameGUI.getChatGUI().printChatMessage(ChatMessageComponent.createFromJson(event.message).toStringWithFormatting(true));
        }
    }

    public void handleAnimation(final Packet18Animation par1Packet18Animation)
    {
        final Entity entity = this.getEntityByID(par1Packet18Animation.entityId);

        if (entity != null)
        {
            if (par1Packet18Animation.animate == 1)
            {
                final EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                entitylivingbase.swingItem();
            }
            else if (par1Packet18Animation.animate == 2)
            {
                entity.performHurtAnimation();
            }
            else if (par1Packet18Animation.animate == 3)
            {
                final EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.wakeUpPlayer(false, false, false);
            }
            else if (par1Packet18Animation.animate != 4)
            {
                if (par1Packet18Animation.animate == 6)
                {
                    this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, entity));
                }
                else if (par1Packet18Animation.animate == 7)
                {
                    final EntityCrit2FX entitycrit2fx = new EntityCrit2FX(this.mc.theWorld, entity, "magicCrit");
                    this.mc.effectRenderer.addEffect(entitycrit2fx);
                }
                else if (par1Packet18Animation.animate == 5 && entity instanceof EntityOtherPlayerMP)
                {
                    ;
                }
            }
        }
    }

    public void handleSleep(final Packet17Sleep par1Packet17Sleep)
    {
        final Entity entity = this.getEntityByID(par1Packet17Sleep.entityID);

        if (entity != null)
        {
            if (par1Packet17Sleep.field_73622_e == 0)
            {
                final EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.sleepInBedAt(par1Packet17Sleep.bedX, par1Packet17Sleep.bedY, par1Packet17Sleep.bedZ);
            }
        }
    }

    /**
     * Disconnects the network connection.
     */
    public void disconnect()
    {
        this.disconnected = true;
        this.netManager.wakeThreads();
        this.netManager.networkShutdown("disconnect.closed", new Object[0]);
    }

    public void handleMobSpawn(final Packet24MobSpawn par1Packet24MobSpawn)
    {
        final double d0 = (double)par1Packet24MobSpawn.xPosition / 32.0D;
        final double d1 = (double)par1Packet24MobSpawn.yPosition / 32.0D;
        final double d2 = (double)par1Packet24MobSpawn.zPosition / 32.0D;
        final float f = (float)(par1Packet24MobSpawn.yaw * 360) / 256.0F;
        final float f1 = (float)(par1Packet24MobSpawn.pitch * 360) / 256.0F;
        final EntityLivingBase entitylivingbase = (EntityLivingBase)EntityList.createEntityByID(par1Packet24MobSpawn.type, this.mc.theWorld);
        entitylivingbase.serverPosX = par1Packet24MobSpawn.xPosition;
        entitylivingbase.serverPosY = par1Packet24MobSpawn.yPosition;
        entitylivingbase.serverPosZ = par1Packet24MobSpawn.zPosition;
        entitylivingbase.rotationYawHead = (float)(par1Packet24MobSpawn.headYaw * 360) / 256.0F;
        final Entity[] aentity = entitylivingbase.getParts();

        if (aentity != null)
        {
            final int i = par1Packet24MobSpawn.entityId - entitylivingbase.entityId;

            for (int j = 0; j < aentity.length; ++j)
            {
                aentity[j].entityId += i;
            }
        }

        entitylivingbase.entityId = par1Packet24MobSpawn.entityId;
        entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
        entitylivingbase.motionX = (double)((float)par1Packet24MobSpawn.velocityX / 8000.0F);
        entitylivingbase.motionY = (double)((float)par1Packet24MobSpawn.velocityY / 8000.0F);
        entitylivingbase.motionZ = (double)((float)par1Packet24MobSpawn.velocityZ / 8000.0F);
        this.worldClient.addEntityToWorld(par1Packet24MobSpawn.entityId, entitylivingbase);
        final List list = par1Packet24MobSpawn.getMetadata();

        if (list != null)
        {
            entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleUpdateTime(final Packet4UpdateTime par1Packet4UpdateTime)
    {
        this.mc.theWorld.func_82738_a(par1Packet4UpdateTime.worldAge);
        this.mc.theWorld.setWorldTime(par1Packet4UpdateTime.time);
    }

    public void handleSpawnPosition(final Packet6SpawnPosition par1Packet6SpawnPosition)
    {
        this.mc.thePlayer.setSpawnChunk(new ChunkCoordinates(par1Packet6SpawnPosition.xPosition, par1Packet6SpawnPosition.yPosition, par1Packet6SpawnPosition.zPosition), true);
        this.mc.theWorld.getWorldInfo().setSpawnPosition(par1Packet6SpawnPosition.xPosition, par1Packet6SpawnPosition.yPosition, par1Packet6SpawnPosition.zPosition);
    }

    /**
     * Packet handler
     */
    public void handleAttachEntity(final Packet39AttachEntity par1Packet39AttachEntity)
    {
        Object object = this.getEntityByID(par1Packet39AttachEntity.ridingEntityId);
        final Entity entity = this.getEntityByID(par1Packet39AttachEntity.vehicleEntityId);

        if (par1Packet39AttachEntity.attachState == 0)
        {
            boolean flag = false;

            if (par1Packet39AttachEntity.ridingEntityId == this.mc.thePlayer.entityId)
            {
                object = this.mc.thePlayer;

                if (entity instanceof EntityBoat)
                {
                    ((EntityBoat)entity).func_70270_d(false);
                }

                flag = ((Entity)object).ridingEntity == null && entity != null;
            }
            else if (entity instanceof EntityBoat)
            {
                ((EntityBoat)entity).func_70270_d(true);
            }

            if (object == null)
            {
                return;
            }

            ((Entity)object).mountEntity(entity);

            if (flag)
            {
                final GameSettings gamesettings = this.mc.gameSettings;
                this.mc.ingameGUI.func_110326_a(I18n.getStringParams("mount.onboard", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindSneak.keyCode)}), false);
            }
        }
        else if (par1Packet39AttachEntity.attachState == 1 && object != null && object instanceof EntityLiving)
        {
            if (entity != null)
            {
                ((EntityLiving)object).setLeashedToEntity(entity, false);
            }
            else
            {
                ((EntityLiving)object).clearLeashed(false, false);
            }
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityStatus(final Packet38EntityStatus par1Packet38EntityStatus)
    {
        final Entity entity = this.getEntityByID(par1Packet38EntityStatus.entityId);

        if (entity != null)
        {
            entity.handleHealthUpdate(par1Packet38EntityStatus.entityStatus);
        }
    }

    private Entity getEntityByID(final int par1)
    {
        return (Entity)(par1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : this.worldClient.getEntityByID(par1));
    }

    /**
     * Recieves player health from the server and then proceeds to set it locally on the client.
     */
    public void handleUpdateHealth(final Packet8UpdateHealth par1Packet8UpdateHealth)
    {
        this.mc.thePlayer.setPlayerSPHealth(par1Packet8UpdateHealth.healthMP);
        this.mc.thePlayer.getFoodStats().setFoodLevel(par1Packet8UpdateHealth.food);
        this.mc.thePlayer.getFoodStats().setFoodSaturationLevel(par1Packet8UpdateHealth.foodSaturation);
    }

    /**
     * Handle an experience packet.
     */
    public void handleExperience(final Packet43Experience par1Packet43Experience)
    {
        this.mc.thePlayer.setXPStats(par1Packet43Experience.experience, par1Packet43Experience.experienceTotal, par1Packet43Experience.experienceLevel);
    }

    /**
     * respawns the player
     */
    public void handleRespawn(final Packet9Respawn par1Packet9Respawn)
    {
        if (par1Packet9Respawn.respawnDimension != this.mc.thePlayer.dimension)
        {
            this.doneLoadingTerrain = false;
            final Scoreboard scoreboard = this.worldClient.getScoreboard();
            this.worldClient = new WorldClient(this, new WorldSettings(0L, par1Packet9Respawn.gameType, false, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), par1Packet9Respawn.terrainType), par1Packet9Respawn.respawnDimension, par1Packet9Respawn.difficulty, this.mc.mcProfiler, this.mc.getLogAgent());
            this.worldClient.func_96443_a(scoreboard);
            this.worldClient.isRemote = true;
            this.mc.loadWorld(this.worldClient);
            this.mc.thePlayer.dimension = par1Packet9Respawn.respawnDimension;
            this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.mc.setDimensionAndSpawnPlayer(par1Packet9Respawn.respawnDimension);
        this.mc.playerController.setGameType(par1Packet9Respawn.gameType);
    }

    public void handleExplosion(final Packet60Explosion par1Packet60Explosion)
    {
        final Explosion explosion = new Explosion(this.mc.theWorld, (Entity)null, par1Packet60Explosion.explosionX, par1Packet60Explosion.explosionY, par1Packet60Explosion.explosionZ, par1Packet60Explosion.explosionSize);
        explosion.affectedBlockPositions = par1Packet60Explosion.chunkPositionRecords;
        explosion.doExplosionB(true);
        this.mc.thePlayer.motionX += (double)par1Packet60Explosion.getPlayerVelocityX();
        this.mc.thePlayer.motionY += (double)par1Packet60Explosion.getPlayerVelocityY();
        this.mc.thePlayer.motionZ += (double)par1Packet60Explosion.getPlayerVelocityZ();
    }

    public void handleOpenWindow(final Packet100OpenWindow par1Packet100OpenWindow)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        switch (par1Packet100OpenWindow.inventoryType)
        {
            case 0:
                entityclientplayermp.displayGUIChest(new InventoryBasic(par1Packet100OpenWindow.windowTitle, par1Packet100OpenWindow.useProvidedWindowTitle, par1Packet100OpenWindow.slotsCount));
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 1:
                entityclientplayermp.displayGUIWorkbench(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 2:
                final TileEntityFurnace tileentityfurnace = new TileEntityFurnace();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentityfurnace.setGuiDisplayName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIFurnace(tileentityfurnace);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 3:
                final TileEntityDispenser tileentitydispenser = new TileEntityDispenser();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitydispenser.setCustomName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIDispenser(tileentitydispenser);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 4:
                entityclientplayermp.displayGUIEnchantment(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), par1Packet100OpenWindow.useProvidedWindowTitle ? par1Packet100OpenWindow.windowTitle : null);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 5:
                final TileEntityBrewingStand tileentitybrewingstand = new TileEntityBrewingStand();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitybrewingstand.func_94131_a(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIBrewingStand(tileentitybrewingstand);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 6:
                entityclientplayermp.displayGUIMerchant(new NpcMerchant(entityclientplayermp), par1Packet100OpenWindow.useProvidedWindowTitle ? par1Packet100OpenWindow.windowTitle : null);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 7:
                final TileEntityBeacon tileentitybeacon = new TileEntityBeacon();
                entityclientplayermp.displayGUIBeacon(tileentitybeacon);

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitybeacon.func_94047_a(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 8:
                entityclientplayermp.displayGUIAnvil(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 9:
                final TileEntityHopper tileentityhopper = new TileEntityHopper();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentityhopper.setInventoryName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIHopper(tileentityhopper);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 10:
                final TileEntityDropper tileentitydropper = new TileEntityDropper();

                if (par1Packet100OpenWindow.useProvidedWindowTitle)
                {
                    tileentitydropper.setCustomName(par1Packet100OpenWindow.windowTitle);
                }

                entityclientplayermp.displayGUIDispenser(tileentitydropper);
                entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                break;
            case 11:
                final Entity entity = this.getEntityByID(par1Packet100OpenWindow.field_111008_f);

                if (entity != null && entity instanceof EntityHorse)
                {
                    entityclientplayermp.displayGUIHorse((EntityHorse)entity, new AnimalChest(par1Packet100OpenWindow.windowTitle, par1Packet100OpenWindow.useProvidedWindowTitle, par1Packet100OpenWindow.slotsCount));
                    entityclientplayermp.openContainer.windowId = par1Packet100OpenWindow.windowId;
                }
        }
    }

    public void handleSetSlot(final Packet103SetSlot par1Packet103SetSlot)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        if (par1Packet103SetSlot.windowId == -1)
        {
            entityclientplayermp.inventory.setItemStack(par1Packet103SetSlot.myItemStack);
        }
        else
        {
            boolean flag = false;

            if (this.mc.currentScreen instanceof GuiContainerCreative)
            {
                final GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.mc.currentScreen;
                flag = guicontainercreative.getCurrentTabIndex() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (par1Packet103SetSlot.windowId == 0 && par1Packet103SetSlot.itemSlot >= 36 && par1Packet103SetSlot.itemSlot < 45)
            {
                final ItemStack itemstack = entityclientplayermp.inventoryContainer.getSlot(par1Packet103SetSlot.itemSlot).getStack();

                if (par1Packet103SetSlot.myItemStack != null && (itemstack == null || itemstack.stackSize < par1Packet103SetSlot.myItemStack.stackSize))
                {
                    par1Packet103SetSlot.myItemStack.animationsToGo = 5;
                }

                entityclientplayermp.inventoryContainer.putStackInSlot(par1Packet103SetSlot.itemSlot, par1Packet103SetSlot.myItemStack);
            }
            else if (par1Packet103SetSlot.windowId == entityclientplayermp.openContainer.windowId && (par1Packet103SetSlot.windowId != 0 || !flag))
            {
                entityclientplayermp.openContainer.putStackInSlot(par1Packet103SetSlot.itemSlot, par1Packet103SetSlot.myItemStack);
            }
        }
    }

    public void handleTransaction(final Packet106Transaction par1Packet106Transaction)
    {
        Container container = null;
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        if (par1Packet106Transaction.windowId == 0)
        {
            container = entityclientplayermp.inventoryContainer;
        }
        else if (par1Packet106Transaction.windowId == entityclientplayermp.openContainer.windowId)
        {
            container = entityclientplayermp.openContainer;
        }

        if (container != null && !par1Packet106Transaction.accepted)
        {
            this.addToSendQueue(new Packet106Transaction(par1Packet106Transaction.windowId, par1Packet106Transaction.shortWindowId, true));
        }
    }

    public void handleWindowItems(final Packet104WindowItems par1Packet104WindowItems)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;

        if (par1Packet104WindowItems.windowId == 0)
        {
            entityclientplayermp.inventoryContainer.putStacksInSlots(par1Packet104WindowItems.itemStack);
        }
        else if (par1Packet104WindowItems.windowId == entityclientplayermp.openContainer.windowId)
        {
            entityclientplayermp.openContainer.putStacksInSlots(par1Packet104WindowItems.itemStack);
        }
    }

    public void func_142031_a(final Packet133TileEditorOpen par1Packet133TileEditorOpen)
    {
        final TileEntity tileentity = this.worldClient.getBlockTileEntity(par1Packet133TileEditorOpen.field_142035_b, par1Packet133TileEditorOpen.field_142036_c, par1Packet133TileEditorOpen.field_142034_d);

        if (tileentity != null)
        {
            this.mc.thePlayer.displayGUIEditSign(tileentity);
        }
        else if (par1Packet133TileEditorOpen.field_142037_a == 0)
        {
            final TileEntitySign tileentitysign = new TileEntitySign();
            tileentitysign.setWorldObj(this.worldClient);
            tileentitysign.xCoord = par1Packet133TileEditorOpen.field_142035_b;
            tileentitysign.yCoord = par1Packet133TileEditorOpen.field_142036_c;
            tileentitysign.zCoord = par1Packet133TileEditorOpen.field_142034_d;
            this.mc.thePlayer.displayGUIEditSign(tileentitysign);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(final Packet130UpdateSign par1Packet130UpdateSign)
    {
        boolean flag = false;

        if (this.mc.theWorld.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            final TileEntity tileentity = this.mc.theWorld.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if (tileentity instanceof TileEntitySign)
            {
                final TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (tileentitysign.isEditable())
                {
                    System.arraycopy(par1Packet130UpdateSign.signLines, 0, tileentitysign.signText, 0, 4);

                    tileentitysign.onInventoryChanged();
                }

                flag = true;
            }
        }

        if (!flag && this.mc.thePlayer != null)
        {
            this.mc.thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Unable to locate sign at " + par1Packet130UpdateSign.xPosition + ", " + par1Packet130UpdateSign.yPosition + ", " + par1Packet130UpdateSign.zPosition));
        }
    }

    public void handleTileEntityData(final Packet132TileEntityData par1Packet132TileEntityData)
    {
        if (this.mc.theWorld.blockExists(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition))
        {
            final TileEntity tileentity = this.mc.theWorld.getBlockTileEntity(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition);

            if (tileentity != null)
            {
                if (par1Packet132TileEntityData.actionType == 1 && tileentity instanceof TileEntityMobSpawner)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.data);
                }
                else if (par1Packet132TileEntityData.actionType == 2 && tileentity instanceof TileEntityCommandBlock)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.data);
                }
                else if (par1Packet132TileEntityData.actionType == 3 && tileentity instanceof TileEntityBeacon)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.data);
                }
                else if (par1Packet132TileEntityData.actionType == 4 && tileentity instanceof TileEntitySkull)
                {
                    tileentity.readFromNBT(par1Packet132TileEntityData.data);
                }
                else
                {
                    tileentity.onDataPacket(netManager,  par1Packet132TileEntityData);
                }
            }
        }
    }

    public void handleUpdateProgressbar(final Packet105UpdateProgressbar par1Packet105UpdateProgressbar)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        this.unexpectedPacket(par1Packet105UpdateProgressbar);

        if (entityclientplayermp.openContainer != null && entityclientplayermp.openContainer.windowId == par1Packet105UpdateProgressbar.windowId)
        {
            entityclientplayermp.openContainer.updateProgressBar(par1Packet105UpdateProgressbar.progressBar, par1Packet105UpdateProgressbar.progressBarValue);
        }
    }

    public void handlePlayerInventory(final Packet5PlayerInventory par1Packet5PlayerInventory)
    {
        final Entity entity = this.getEntityByID(par1Packet5PlayerInventory.entityID);

        if (entity != null)
        {
            entity.setCurrentItemOrArmor(par1Packet5PlayerInventory.slot, par1Packet5PlayerInventory.getItemSlot());
        }
    }

    public void handleCloseWindow(final Packet101CloseWindow par1Packet101CloseWindow)
    {
        this.mc.thePlayer.func_92015_f();
    }

    public void handleBlockEvent(final Packet54PlayNoteBlock par1Packet54PlayNoteBlock)
    {
        this.mc.theWorld.addBlockEvent(par1Packet54PlayNoteBlock.xLocation, par1Packet54PlayNoteBlock.yLocation, par1Packet54PlayNoteBlock.zLocation, par1Packet54PlayNoteBlock.blockId, par1Packet54PlayNoteBlock.instrumentType, par1Packet54PlayNoteBlock.pitch);
    }

    public void handleBlockDestroy(final Packet55BlockDestroy par1Packet55BlockDestroy)
    {
        this.mc.theWorld.destroyBlockInWorldPartially(par1Packet55BlockDestroy.getEntityId(), par1Packet55BlockDestroy.getPosX(), par1Packet55BlockDestroy.getPosY(), par1Packet55BlockDestroy.getPosZ(), par1Packet55BlockDestroy.getDestroyedStage());
    }

    public void handleMapChunks(final Packet56MapChunks par1Packet56MapChunks)
    {
        for (int i = 0; i < par1Packet56MapChunks.getNumberOfChunkInPacket(); ++i)
        {
            final int j = par1Packet56MapChunks.getChunkPosX(i);
            final int k = par1Packet56MapChunks.getChunkPosZ(i);
            this.worldClient.doPreChunk(j, k, true);
            this.worldClient.invalidateBlockReceiveRegion(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
            Chunk chunk = this.worldClient.getChunkFromChunkCoords(j, k);

            if (chunk == null)
            {
                this.worldClient.doPreChunk(j, k, true);
                chunk = this.worldClient.getChunkFromChunkCoords(j, k);
            }

            if (chunk != null)
            {
                chunk.fillChunk(par1Packet56MapChunks.getChunkCompressedData(i), par1Packet56MapChunks.field_73590_a[i], par1Packet56MapChunks.field_73588_b[i], true);
                this.worldClient.markBlockRangeForRenderUpdate(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);

                if (!(this.worldClient.provider instanceof WorldProviderSurface))
                {
                    chunk.resetRelightChecks();
                }
            }
        }
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return this.mc != null && this.mc.theWorld != null && this.mc.thePlayer != null && this.worldClient != null;
    }

    public void handleGameEvent(final Packet70GameEvent par1Packet70GameEvent)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        final int i = par1Packet70GameEvent.eventType;
        final int j = par1Packet70GameEvent.gameMode;

        if (i >= 0 && i < Packet70GameEvent.clientMessage.length && Packet70GameEvent.clientMessage[i] != null)
        {
            entityclientplayermp.addChatMessage(Packet70GameEvent.clientMessage[i]);
        }

        if (i == 1)
        {
            this.worldClient.getWorldInfo().setRaining(true);
            this.worldClient.setRainStrength(0.0F);
        }
        else if (i == 2)
        {
            this.worldClient.getWorldInfo().setRaining(false);
            this.worldClient.setRainStrength(1.0F);
        }
        else if (i == 3)
        {
            this.mc.playerController.setGameType(EnumGameType.getByID(j));
        }
        else if (i == 4)
        {
            this.mc.displayGuiScreen(new GuiWinGame());
        }
        else if (i == 5)
        {
            final GameSettings gamesettings = this.mc.gameSettings;

            if (j == 0)
            {
                this.mc.displayGuiScreen(new GuiScreenDemo());
            }
            else if (j == 101)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.movement", new Object[] {Keyboard.getKeyName(gamesettings.keyBindForward.keyCode), Keyboard.getKeyName(gamesettings.keyBindLeft.keyCode), Keyboard.getKeyName(gamesettings.keyBindBack.keyCode), Keyboard.getKeyName(gamesettings.keyBindRight.keyCode)});
            }
            else if (j == 102)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.jump", new Object[] {Keyboard.getKeyName(gamesettings.keyBindJump.keyCode)});
            }
            else if (j == 103)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.inventory", new Object[] {Keyboard.getKeyName(gamesettings.keyBindInventory.keyCode)});
            }
        }
        else if (i == 6)
        {
            this.worldClient.playSound(entityclientplayermp.posX, entityclientplayermp.posY + (double)entityclientplayermp.getEyeHeight(), entityclientplayermp.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        }
    }

    /**
     * Contains logic for handling packets containing arbitrary unique item data. Currently this is only for maps.
     */
    public void handleMapData(final Packet131MapData par1Packet131MapData)
    {
        FMLNetworkHandler.handlePacket131Packet(this, par1Packet131MapData);
    }

    public void fmlPacket131Callback(final Packet131MapData par1Packet131MapData)
    {
        if (par1Packet131MapData.itemID == Item.map.itemID)
        {
            ItemMap.getMPMapData(par1Packet131MapData.uniqueID, this.mc.theWorld).updateMPMapData(par1Packet131MapData.itemData);
        }
        else
        {
            this.mc.getLogAgent().logWarning("Unknown itemid: " + par1Packet131MapData.uniqueID);
        }
    }

    public void handleDoorChange(final Packet61DoorChange par1Packet61DoorChange)
    {
        if (par1Packet61DoorChange.getRelativeVolumeDisabled())
        {
            this.mc.theWorld.func_82739_e(par1Packet61DoorChange.sfxID, par1Packet61DoorChange.posX, par1Packet61DoorChange.posY, par1Packet61DoorChange.posZ, par1Packet61DoorChange.auxData);
        }
        else
        {
            this.mc.theWorld.playAuxSFX(par1Packet61DoorChange.sfxID, par1Packet61DoorChange.posX, par1Packet61DoorChange.posY, par1Packet61DoorChange.posZ, par1Packet61DoorChange.auxData);
        }
    }

    /**
     * Increment player statistics
     */
    public void handleStatistic(final Packet200Statistic par1Packet200Statistic)
    {
        this.mc.thePlayer.incrementStat(StatList.getOneShotStat(par1Packet200Statistic.statisticId), par1Packet200Statistic.amount);
    }

    /**
     * Handle an entity effect packet.
     */
    public void handleEntityEffect(final Packet41EntityEffect par1Packet41EntityEffect)
    {
        final Entity entity = this.getEntityByID(par1Packet41EntityEffect.entityId);

        if (entity instanceof EntityLivingBase)
        {
            final PotionEffect potioneffect = new PotionEffect(par1Packet41EntityEffect.effectId, par1Packet41EntityEffect.duration, par1Packet41EntityEffect.effectAmplifier);
            potioneffect.setPotionDurationMax(par1Packet41EntityEffect.isDurationMax());
            ((EntityLivingBase)entity).addPotionEffect(potioneffect);
        }
    }

    /**
     * Handle a remove entity effect packet.
     */
    public void handleRemoveEntityEffect(final Packet42RemoveEntityEffect par1Packet42RemoveEntityEffect)
    {
        final Entity entity = this.getEntityByID(par1Packet42RemoveEntityEffect.entityId);

        if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase)entity).removePotionEffectClient(par1Packet42RemoveEntityEffect.effectId);
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return false;
    }

    /**
     * Handle a player information packet.
     */
    public void handlePlayerInfo(final Packet201PlayerInfo par1Packet201PlayerInfo)
    {
        GuiPlayerInfo guiplayerinfo = (GuiPlayerInfo)this.playerInfoMap.get(par1Packet201PlayerInfo.playerName);

        if (guiplayerinfo == null && par1Packet201PlayerInfo.isConnected)
        {
            guiplayerinfo = new GuiPlayerInfo(par1Packet201PlayerInfo.playerName);
            this.playerInfoMap.put(par1Packet201PlayerInfo.playerName, guiplayerinfo);
            this.playerInfoList.add(guiplayerinfo);
        }

        if (guiplayerinfo != null && !par1Packet201PlayerInfo.isConnected)
        {
            this.playerInfoMap.remove(par1Packet201PlayerInfo.playerName);
            this.playerInfoList.remove(guiplayerinfo);
        }

        if (par1Packet201PlayerInfo.isConnected && guiplayerinfo != null)
        {
            guiplayerinfo.responseTime = par1Packet201PlayerInfo.ping;
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(final Packet0KeepAlive par1Packet0KeepAlive)
    {
        this.addToSendQueue(new Packet0KeepAlive(par1Packet0KeepAlive.randomId));
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(final Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        final EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        entityclientplayermp.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying();
        entityclientplayermp.capabilities.isCreativeMode = par1Packet202PlayerAbilities.isCreativeMode();
        entityclientplayermp.capabilities.disableDamage = par1Packet202PlayerAbilities.getDisableDamage();
        entityclientplayermp.capabilities.allowFlying = par1Packet202PlayerAbilities.getAllowFlying();
        entityclientplayermp.capabilities.setFlySpeed(par1Packet202PlayerAbilities.getFlySpeed());
        entityclientplayermp.capabilities.setPlayerWalkSpeed(par1Packet202PlayerAbilities.getWalkSpeed());
    }

    public void handleAutoComplete(final Packet203AutoComplete par1Packet203AutoComplete)
    {
        final String[] astring = par1Packet203AutoComplete.getText().split("\u0000");

        if (this.mc.currentScreen instanceof GuiChat)
        {
            final GuiChat guichat = (GuiChat)this.mc.currentScreen;
            guichat.func_73894_a(astring);
        }
    }

    public void handleLevelSound(final Packet62LevelSound par1Packet62LevelSound)
    {
        this.mc.theWorld.playSound(par1Packet62LevelSound.getEffectX(), par1Packet62LevelSound.getEffectY(), par1Packet62LevelSound.getEffectZ(), par1Packet62LevelSound.getSoundName(), par1Packet62LevelSound.getVolume(), par1Packet62LevelSound.getPitch(), false);
    }

    public void handleCustomPayload(final Packet250CustomPayload par1Packet250CustomPayload)
    {
    FMLNetworkHandler.handlePacket250Packet(par1Packet250CustomPayload, netManager, this);
    }

    public void handleVanilla250Packet(final Packet250CustomPayload par1Packet250CustomPayload)
    {
        if ("MC|TrList".equals(par1Packet250CustomPayload.channel))
        {
            final DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));

            try
            {
                final int i = datainputstream.readInt();
                final GuiScreen guiscreen = this.mc.currentScreen;

                if (guiscreen != null && guiscreen instanceof GuiMerchant && i == this.mc.thePlayer.openContainer.windowId)
                {
                    final IMerchant imerchant = ((GuiMerchant)guiscreen).getIMerchant();
                    final MerchantRecipeList merchantrecipelist = MerchantRecipeList.readRecipiesFromStream(datainputstream);
                    imerchant.setRecipes(merchantrecipelist);
                }
            }
            catch (final IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }
        else if ("MC|Brand".equals(par1Packet250CustomPayload.channel))
        {
            this.mc.thePlayer.func_142020_c(new String(par1Packet250CustomPayload.data, Charsets.UTF_8));
        }
    }

    /**
     * Handle a set objective packet.
     */
    public void handleSetObjective(final Packet206SetObjective par1Packet206SetObjective)
    {
        final Scoreboard scoreboard = this.worldClient.getScoreboard();
        final ScoreObjective scoreobjective;

        if (par1Packet206SetObjective.change == 0)
        {
            scoreobjective = scoreboard.func_96535_a(par1Packet206SetObjective.objectiveName, ScoreObjectiveCriteria.field_96641_b);
            scoreobjective.setDisplayName(par1Packet206SetObjective.objectiveDisplayName);
        }
        else
        {
            scoreobjective = scoreboard.getObjective(par1Packet206SetObjective.objectiveName);

            if (par1Packet206SetObjective.change == 1)
            {
                scoreboard.func_96519_k(scoreobjective);
            }
            else if (par1Packet206SetObjective.change == 2)
            {
                scoreobjective.setDisplayName(par1Packet206SetObjective.objectiveDisplayName);
            }
        }
    }

    /**
     * Handle a set score packet.
     */
    public void handleSetScore(final Packet207SetScore par1Packet207SetScore)
    {
        final Scoreboard scoreboard = this.worldClient.getScoreboard();
        final ScoreObjective scoreobjective = scoreboard.getObjective(par1Packet207SetScore.scoreName);

        if (par1Packet207SetScore.updateOrRemove == 0)
        {
            final Score score = scoreboard.func_96529_a(par1Packet207SetScore.itemName, scoreobjective);
            score.func_96647_c(par1Packet207SetScore.value);
        }
        else if (par1Packet207SetScore.updateOrRemove == 1)
        {
            scoreboard.func_96515_c(par1Packet207SetScore.itemName);
        }
    }

    /**
     * Handle a set display objective packet.
     */
    public void handleSetDisplayObjective(final Packet208SetDisplayObjective par1Packet208SetDisplayObjective)
    {
        final Scoreboard scoreboard = this.worldClient.getScoreboard();

        if (par1Packet208SetDisplayObjective.scoreName.isEmpty())
        {
            scoreboard.func_96530_a(par1Packet208SetDisplayObjective.scoreboardPosition, (ScoreObjective)null);
        }
        else
        {
            final ScoreObjective scoreobjective = scoreboard.getObjective(par1Packet208SetDisplayObjective.scoreName);
            scoreboard.func_96530_a(par1Packet208SetDisplayObjective.scoreboardPosition, scoreobjective);
        }
    }

    /**
     * Handle a set player team packet.
     */
    public void handleSetPlayerTeam(final Packet209SetPlayerTeam par1Packet209SetPlayerTeam)
    {
        final Scoreboard scoreboard = this.worldClient.getScoreboard();
        final ScorePlayerTeam scoreplayerteam;

        if (par1Packet209SetPlayerTeam.mode == 0)
        {
            scoreplayerteam = scoreboard.createTeam(par1Packet209SetPlayerTeam.teamName);
        }
        else
        {
            scoreplayerteam = scoreboard.func_96508_e(par1Packet209SetPlayerTeam.teamName);
        }

        if (par1Packet209SetPlayerTeam.mode == 0 || par1Packet209SetPlayerTeam.mode == 2)
        {
            scoreplayerteam.setTeamName(par1Packet209SetPlayerTeam.teamDisplayName);
            scoreplayerteam.setNamePrefix(par1Packet209SetPlayerTeam.teamPrefix);
            scoreplayerteam.setNameSuffix(par1Packet209SetPlayerTeam.teamSuffix);
            scoreplayerteam.func_98298_a(par1Packet209SetPlayerTeam.friendlyFire);
        }

        Iterator iterator;
        String s;

        if (par1Packet209SetPlayerTeam.mode == 0 || par1Packet209SetPlayerTeam.mode == 3)
        {
            iterator = par1Packet209SetPlayerTeam.playerNames.iterator();

            while (iterator.hasNext())
            {
                s = (String)iterator.next();
                scoreboard.addPlayerToTeam(s, scoreplayerteam);
            }
        }

        if (par1Packet209SetPlayerTeam.mode == 4)
        {
            iterator = par1Packet209SetPlayerTeam.playerNames.iterator();

            while (iterator.hasNext())
            {
                s = (String)iterator.next();
                scoreboard.removePlayerFromTeam(s, scoreplayerteam);
            }
        }

        if (par1Packet209SetPlayerTeam.mode == 1)
        {
            scoreboard.func_96511_d(scoreplayerteam);
        }
    }

    /**
     * Handle a world particles packet.
     */
    public void handleWorldParticles(final Packet63WorldParticles par1Packet63WorldParticles)
    {
        for (int i = 0; i < par1Packet63WorldParticles.getQuantity(); ++i)
        {
            final double d0 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getOffsetX();
            final double d1 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getOffsetY();
            final double d2 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getOffsetZ();
            final double d3 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getSpeed();
            final double d4 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getSpeed();
            final double d5 = this.rand.nextGaussian() * (double)par1Packet63WorldParticles.getSpeed();
            this.worldClient.spawnParticle(par1Packet63WorldParticles.getParticleName(), par1Packet63WorldParticles.getPositionX() + d0, par1Packet63WorldParticles.getPositionY() + d1, par1Packet63WorldParticles.getPositionZ() + d2, d3, d4, d5);
        }
    }

    public void func_110773_a(final Packet44UpdateAttributes par1Packet44UpdateAttributes)
    {
        final Entity entity = this.getEntityByID(par1Packet44UpdateAttributes.func_111002_d());

        if (entity != null)
        {
            if (!(entity instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            }
            else
            {
                final BaseAttributeMap baseattributemap = ((EntityLivingBase)entity).getAttributeMap();
                final Iterator iterator = par1Packet44UpdateAttributes.func_111003_f().iterator();

                while (iterator.hasNext())
                {
                    final Packet44UpdateAttributesSnapshot packet44updateattributessnapshot = (Packet44UpdateAttributesSnapshot)iterator.next();
                    AttributeInstance attributeinstance = baseattributemap.getAttributeInstanceByName(packet44updateattributessnapshot.func_142040_a());

                    if (attributeinstance == null)
                    {
                        attributeinstance = baseattributemap.func_111150_b(new RangedAttribute(packet44updateattributessnapshot.func_142040_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    attributeinstance.setAttribute(packet44updateattributessnapshot.func_142041_b());
                    attributeinstance.func_142049_d();
                    final Iterator iterator1 = packet44updateattributessnapshot.func_142039_c().iterator();

                    while (iterator1.hasNext())
                    {
                        final AttributeModifier attributemodifier = (AttributeModifier)iterator1.next();
                        attributeinstance.applyModifier(attributemodifier);
                    }
                }
            }
        }
    }

    /**
     * Return the NetworkManager instance used by this NetClientHandler
     */
    public INetworkManager getNetManager()
    {
        return this.netManager;
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return mc.thePlayer;
    }

    public static void setConnectionCompatibilityLevel(final byte connectionCompatibilityLevel)
    {
        NetClientHandler.connectionCompatibilityLevel = connectionCompatibilityLevel;
    }

    public static byte getConnectionCompatibilityLevel()
    {
        return connectionCompatibilityLevel;
    }
}
