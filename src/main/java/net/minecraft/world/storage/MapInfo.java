package net.minecraft.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public class MapInfo
{
    /** Reference for EntityPlayer object in MapInfo */
    public final EntityPlayer entityplayerObj;
    public int[] field_76209_b;
    public int[] field_76210_c;

    /**
     * updated by x = mod(x*11,128) +1  x-1 is used to index field_76209_b and field_76210_c
     */
    private int currentRandomNumber;
    private int ticksUntilPlayerLocationMapUpdate;

    /**
     * a cache of the result from getPlayersOnMap so that it is not resent when nothing changes
     */
    private byte[] lastPlayerLocationOnMap;
    public int field_82569_d;
    private boolean field_82570_i;

    /** reference in MapInfo to MapData object */
    final MapData mapDataObj;

    public MapInfo(final MapData par1MapData, final EntityPlayer par2EntityPlayer)
    {
        this.mapDataObj = par1MapData;
        this.field_76209_b = new int[128];
        this.field_76210_c = new int[128];
        this.entityplayerObj = par2EntityPlayer;

        for (int i = 0; i < this.field_76209_b.length; ++i)
        {
            this.field_76209_b[i] = 0;
            this.field_76210_c[i] = 127;
        }
    }

    /**
     * returns a 1+players*3 array, of x,y, and color . the name of this function may be partially wrong, as there is a
     * second branch to the code here
     */
    public byte[] getPlayersOnMap(final ItemStack par1ItemStack)
    {
        final byte[] abyte;

        if (!this.field_82570_i)
        {
            abyte = new byte[] {(byte)2, this.mapDataObj.scale};
            this.field_82570_i = true;
            return abyte;
        }
        else
        {
            int i;
            int j;
            // Spigot start
            final boolean custom = this.mapDataObj.mapView.renderers.size() > 1 || !(this.mapDataObj.mapView.renderers.get(0) instanceof org.bukkit.craftbukkit.v1_6_R3.map.CraftMapRenderer);
            final org.bukkit.craftbukkit.v1_6_R3.map.RenderData render = (custom) ? this.mapDataObj.mapView.render((org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer) entityplayerObj.getBukkitEntity()) : null; // CraftBukkit

            if (--this.ticksUntilPlayerLocationMapUpdate < 0)
            {
                this.ticksUntilPlayerLocationMapUpdate = 4;
                abyte = new byte[((custom) ? render.cursors.size() : this.mapDataObj.playersVisibleOnMap.size()) * 3 + 1]; // CraftBukkit
                abyte[0] = 1;
                i = 0;

                // CraftBukkit start

                // Spigot start
                for (final Iterator iterator = ((custom) ? render.cursors.iterator() : this.mapDataObj.playersVisibleOnMap.values().iterator()); iterator.hasNext(); ++i)
                {
                    final org.bukkit.map.MapCursor cursor = (custom) ? (org.bukkit.map.MapCursor) iterator.next() : null;

                    if (cursor != null && !cursor.isVisible())
                    {
                        continue;
                    }

                    final MapCoord deco = (custom) ? null : (MapCoord) iterator.next();
                    abyte[i * 3 + 1] = (byte)(((custom) ? cursor.getRawType() : deco.iconSize) << 4 | ((custom) ? cursor.getDirection() : deco.iconRotation) & 15);
                    abyte[i * 3 + 2] = (byte)((custom) ? cursor.getX() : deco.centerX);
                    abyte[i * 3 + 3] = (byte)((custom) ? cursor.getY() : deco.centerZ);
                }

                // Spigot end
                // CraftBukkit end
                boolean flag = !par1ItemStack.isOnItemFrame();

                if (this.lastPlayerLocationOnMap != null && this.lastPlayerLocationOnMap.length == abyte.length)
                {
                    for (j = 0; j < abyte.length; ++j)
                    {
                        if (abyte[j] != this.lastPlayerLocationOnMap[j])
                        {
                            flag = false;
                            break;
                        }
                    }
                }
                else
                {
                    flag = false;
                }

                if (!flag)
                {
                    this.lastPlayerLocationOnMap = abyte;
                    return abyte;
                }
            }

            for (int k = 0; k < 1; ++k)
            {
                i = this.currentRandomNumber++ * 11 % 128;

                if (this.field_76209_b[i] >= 0)
                {
                    final int l = this.field_76210_c[i] - this.field_76209_b[i] + 1;
                    j = this.field_76209_b[i];
                    final byte[] abyte1 = new byte[l + 3];
                    abyte1[0] = 0;
                    abyte1[1] = (byte)i;
                    abyte1[2] = (byte)j;

                    for (int i1 = 0; i1 < abyte1.length - 3; ++i1)
                    {
                        abyte1[i1 + 3] = ((custom) ? render.buffer : this.mapDataObj.colors)[(i1 + j) * 128 + i]; // Spigot
                    }

                    this.field_76210_c[i] = -1;
                    this.field_76209_b[i] = -1;
                    return abyte1;
                }
            }

            return null;
        }
    }
}
