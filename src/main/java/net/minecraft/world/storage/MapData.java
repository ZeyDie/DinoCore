package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.map.CraftMapView;

import java.util.*;

// CraftBukkit start
// CraftBukkit end

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public int dimension;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];

    /**
     * Holds a reference to the MapInfo of the players who own a copy of the map
     */
    public List playersArrayList = new ArrayList();

    /**
     * Holds a reference to the players who own a copy of the map and a reference to their MapInfo
     */
    private Map playersHashMap = new HashMap();
    public Map playersVisibleOnMap = new LinkedHashMap();

    // CraftBukkit start
    public final CraftMapView mapView;
    private CraftServer server;
    private UUID uniqueId = null;
    // CraftBukkit end

    public MapData(final String par1Str)
    {
        super(par1Str);
        // CraftBukkit start
        mapView = new CraftMapView(this);
        server = (CraftServer) org.bukkit.Bukkit.getServer();
        // CraftBukkit end
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        // CraftBukkit start
        final NBTBase dimTag = par1NBTTagCompound.getTag("dimension");
        final int dimension = (dimTag instanceof NBTTagByte) ? ((NBTTagByte) dimTag).data : ((NBTTagInt) dimTag).data;

        this.xCenter = par1NBTTagCompound.getInteger("xCenter");
        this.zCenter = par1NBTTagCompound.getInteger("zCenter");
        this.scale = par1NBTTagCompound.getByte("scale");

        if (this.scale < 0)
        {
            this.scale = 0;
        }

        if (this.scale > 4)
        {
            this.scale = 4;
        }

        final short short1 = par1NBTTagCompound.getShort("width");
        final short short2 = par1NBTTagCompound.getShort("height");

        if (short1 == 128 && short2 == 128)
        {
            this.colors = par1NBTTagCompound.getByteArray("colors");
        }
        else
        {
            final byte[] abyte = par1NBTTagCompound.getByteArray("colors");
            this.colors = new byte[16384];
            final int i = (128 - short1) / 2;
            final int j = (128 - short2) / 2;

            for (int k = 0; k < short2; ++k)
            {
                final int l = k + j;

                if (l >= 0 || l < 128)
                {
                    for (int i1 = 0; i1 < short1; ++i1)
                    {
                        final int j1 = i1 + i;

                        if (j1 >= 0 || j1 < 128)
                        {
                            this.colors[j1 + l * 128] = abyte[i1 + k * short1];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("dimension", this.dimension);
        par1NBTTagCompound.setInteger("xCenter", this.xCenter);
        par1NBTTagCompound.setInteger("zCenter", this.zCenter);
        par1NBTTagCompound.setByte("scale", this.scale);
        par1NBTTagCompound.setShort("width", (short)128);
        par1NBTTagCompound.setShort("height", (short)128);
        par1NBTTagCompound.setByteArray("colors", this.colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void updateVisiblePlayers(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack)
    {
        if (!this.playersHashMap.containsKey(par1EntityPlayer))
        {
            final MapInfo mapinfo = new MapInfo(this, par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, mapinfo);
            this.playersArrayList.add(mapinfo);
        }

        if (!par1EntityPlayer.inventory.hasItemStack(par2ItemStack))
        {
            this.playersVisibleOnMap.remove(par1EntityPlayer.getCommandSenderName());
        }

        for (int i = 0; i < this.playersArrayList.size(); ++i)
        {
            final MapInfo mapinfo1 = (MapInfo)this.playersArrayList.get(i);

            if (!mapinfo1.entityplayerObj.isDead && (mapinfo1.entityplayerObj.inventory.hasItemStack(par2ItemStack) || par2ItemStack.isOnItemFrame()))
            {
                if (!par2ItemStack.isOnItemFrame() && mapinfo1.entityplayerObj.dimension == this.dimension)
                {
                    this.func_82567_a(0, mapinfo1.entityplayerObj.worldObj, mapinfo1.entityplayerObj.getCommandSenderName(), mapinfo1.entityplayerObj.posX, mapinfo1.entityplayerObj.posZ, (double)mapinfo1.entityplayerObj.rotationYaw);
                }
            }
            else
            {
                this.playersHashMap.remove(mapinfo1.entityplayerObj);
                this.playersArrayList.remove(mapinfo1);
            }
        }

        if (par2ItemStack.isOnItemFrame())
        {
            this.func_82567_a(1, par1EntityPlayer.worldObj, "frame-" + par2ItemStack.getItemFrame().entityId, (double)par2ItemStack.getItemFrame().xPosition, (double)par2ItemStack.getItemFrame().zPosition, (double)(par2ItemStack.getItemFrame().hangingDirection * 90));
        }
    }

    private void func_82567_a(int par1, final World par2World, final String par3Str, final double par4, final double par6, double par8)
    {
        double par81 = par8;
        int par11 = par1;
        final int j = 1 << this.scale;
        final float f = (float)(par4 - (double)this.xCenter) / (float)j;
        final float f1 = (float)(par6 - (double)this.zCenter) / (float)j;
        byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
        byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
        final byte b2 = 63;
        byte b3;

        if (f >= (float)(-b2) && f1 >= (float)(-b2) && f <= (float)b2 && f1 <= (float)b2)
        {
            par81 += par81 < 0.0D ? -8.0D : 8.0D;
            b3 = (byte)((int)(par81 * 16.0D / 360.0D));

            if (par2World.provider.shouldMapSpin(par3Str, par4, par6, par81))
            {
                final int k = (int)(par2World.getWorldInfo().getWorldTime() / 10L);
                b3 = (byte)(k * k * 34187121 + k * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(f) >= 320.0F || Math.abs(f1) >= 320.0F)
            {
                this.playersVisibleOnMap.remove(par3Str);
                return;
            }

            par11 = 6;
            b3 = 0;

            if (f <= (float)(-b2))
            {
                b0 = (byte)((int)((double)(b2 * 2) + 2.5D));
            }

            if (f1 <= (float)(-b2))
            {
                b1 = (byte)((int)((double)(b2 * 2) + 2.5D));
            }

            if (f >= (float)b2)
            {
                b0 = (byte)(b2 * 2 + 1);
            }

            if (f1 >= (float)b2)
            {
                b1 = (byte)(b2 * 2 + 1);
            }
        }

        this.playersVisibleOnMap.put(par3Str, new MapCoord(this, (byte) par11, b0, b1, b3));
    }

    /**
     * Get byte array of packet data to send to players on map for updating map data
     */
    public byte[] getUpdatePacketData(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer)
    {
        final MapInfo mapinfo = (MapInfo)this.playersHashMap.get(par3EntityPlayer);
        return mapinfo == null ? null : mapinfo.getPlayersOnMap(par1ItemStack);
    }

    /**
     * Marks a vertical range of pixels as being modified so they will be resent to clients. Parameters: X, lowest Y,
     * highest Y
     */
    public void setColumnDirty(final int par1, final int par2, final int par3)
    {
        super.markDirty();

        for (int l = 0; l < this.playersArrayList.size(); ++l)
        {
            final MapInfo mapinfo = (MapInfo)this.playersArrayList.get(l);

            if (mapinfo.field_76209_b[par1] < 0 || mapinfo.field_76209_b[par1] > par2)
            {
                mapinfo.field_76209_b[par1] = par2;
            }

            if (mapinfo.field_76210_c[par1] < 0 || mapinfo.field_76210_c[par1] < par3)
            {
                mapinfo.field_76210_c[par1] = par3;
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Updates the client's map with information from other players in MP
     */
    public void updateMPMapData(final byte[] par1ArrayOfByte)
    {
        int i;

        if (par1ArrayOfByte[0] == 0)
        {
            i = par1ArrayOfByte[1] & 255;
            final int j = par1ArrayOfByte[2] & 255;

            for (int k = 0; k < par1ArrayOfByte.length - 3; ++k)
            {
                this.colors[(k + j) * 128 + i] = par1ArrayOfByte[k + 3];
            }

            this.markDirty();
        }
        else if (par1ArrayOfByte[0] == 1)
        {
            this.playersVisibleOnMap.clear();

            for (i = 0; i < (par1ArrayOfByte.length - 1) / 3; ++i)
            {
                final byte b0 = (byte)(par1ArrayOfByte[i * 3 + 1] >> 4);
                final byte b1 = par1ArrayOfByte[i * 3 + 2];
                final byte b2 = par1ArrayOfByte[i * 3 + 3];
                final byte b3 = (byte)(par1ArrayOfByte[i * 3 + 1] & 15);
                this.playersVisibleOnMap.put("icon-" + i, new MapCoord(this, b0, b1, b2, b3));
            }
        }
        else if (par1ArrayOfByte[0] == 2)
        {
            this.scale = par1ArrayOfByte[1];
        }
    }

    public MapInfo func_82568_a(final EntityPlayer par1EntityPlayer)
    {
        MapInfo mapinfo = (MapInfo)this.playersHashMap.get(par1EntityPlayer);

        if (mapinfo == null)
        {
            mapinfo = new MapInfo(this, par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, mapinfo);
            this.playersArrayList.add(mapinfo);
        }

        return mapinfo;
    }
}
