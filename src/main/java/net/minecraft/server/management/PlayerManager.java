package net.minecraft.server.management;

import com.zeydie.legacy.core.ChunkCoordComparator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

import java.util.*;

// CraftBukkit start
// CraftBukkit end

public class PlayerManager {
    private final WorldServer theWorldServer;

    /**
     * players in the current instance
     */
    private final List players = new ArrayList();

    /**
     * A map of chunk position (two ints concatenated into a long) to PlayerInstance
     */
    private final LongHashMap playerInstances = new LongHashMap();

    /**
     * contains a PlayerInstance for every chunk they can see. the "player instance" cotains a list of all players who
     * can also that chunk
     */
    private final Queue chunkWatcherWithPlayers = new java.util.concurrent.ConcurrentLinkedQueue(); // CraftBukkit ArrayList -> ConcurrentLinkedQueue

    /**
     * This field is using when chunk should be processed (every 8000 ticks)
     */
    private final Queue playerInstanceList = new java.util.concurrent.ConcurrentLinkedQueue(); // CraftBukkit ArrayList -> ConcurrentLinkedQueue

    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private final int playerViewRadius;

    /**
     * time what is using to check if InhabitedTime should be calculated
     */
    private long previousTotalWorldTime;

    /**
     * x, z direction vectors: east, south, west, north
     */
    private final int[][] xzDirectionsConst = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private boolean wasNotEmpty; // CraftBukkit

    public PlayerManager(final WorldServer par1WorldServer, final int par2) {
        if (par2 > 15) {
            throw new IllegalArgumentException("Too big view radius!");
        } else if (par2 < 1) // Spigot
        {
            throw new IllegalArgumentException("Too small view radius!");
        } else {
            this.playerViewRadius = par2;
            this.theWorldServer = par1WorldServer;
        }
    }

    public WorldServer getWorldServer() {
        return this.theWorldServer;
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances() {
        final long i = this.theWorldServer.getTotalWorldTime();
        int j;
        PlayerInstance playerinstance;

        if (i - this.previousTotalWorldTime > 8000L) {
            this.previousTotalWorldTime = i;
            // CraftBukkit start - Use iterator
            final java.util.Iterator iterator = this.playerInstanceList.iterator();

            while (iterator.hasNext()) {
                playerinstance = (PlayerInstance) iterator.next();
                playerinstance.sendChunkUpdate();
                playerinstance.processChunk();
            }
        } else {
            final java.util.Iterator iterator = this.chunkWatcherWithPlayers.iterator();

            while (iterator.hasNext()) {
                playerinstance = (PlayerInstance) iterator.next();
                playerinstance.sendChunkUpdate();
                iterator.remove();
                // CraftBukkit end
            }
        }

        // this.d.clear(); // CraftBukkit - Removals are already covered
        if (this.players.isEmpty()) {
            if (!wasNotEmpty) {
                return;    // CraftBukkit - Only do unload when we go from non-empty to empty
            }

            final WorldProvider worldprovider = this.theWorldServer.provider;

            if (!worldprovider.canRespawnHere()) {
                this.theWorldServer.theChunkProviderServer.unloadAllChunks();
            }

            // CraftBukkit start
            wasNotEmpty = false;
        } else {
            wasNotEmpty = true;
        }

        // CraftBukkit end
    }

    public PlayerInstance getOrCreateChunkWatcher(final int par1, final int par2, final boolean par3) {
        final long k = (long) par1 + 2147483647L | (long) par2 + 2147483647L << 32;
        PlayerInstance playerinstance = (PlayerInstance) this.playerInstances.getValueByKey(k);

        if (playerinstance == null && par3) {
            playerinstance = new PlayerInstance(this, par1, par2);
            this.playerInstances.add(k, playerinstance);
            this.playerInstanceList.add(playerinstance);
        }

        return playerinstance;
    }

    // CraftBukkit start
    public final boolean isChunkInUse(final int x, final int z) {
        final PlayerInstance pi = getOrCreateChunkWatcher(x, z, false);

        if (pi != null) {
            return (!PlayerInstance.getPlayersInChunk(pi).isEmpty());
        }

        return false;
    }
    // CraftBukkit end

    /**
     * Called by WorldManager.markBlockForUpdate; marks a block to be resent to clients.
     */
    public void markBlockForUpdate(final int par1, final int par2, final int par3) {
        final int l = par1 >> 4;
        final int i1 = par3 >> 4;
        final PlayerInstance playerinstance = this.getOrCreateChunkWatcher(l, i1, false);

        if (playerinstance != null) {
            playerinstance.flagChunkForUpdate(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager.
     */
    public void addPlayer(final EntityPlayerMP par1EntityPlayerMP) {
        final int i = (int) par1EntityPlayerMP.posX >> 4;
        final int j = (int) par1EntityPlayerMP.posZ >> 4;
        par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
        par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
        // CraftBukkit start - Load nearby chunks first
        final List<ChunkCoordIntPair> chunkList = new LinkedList<ChunkCoordIntPair>();

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k) {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
                chunkList.add(new ChunkCoordIntPair(k, l));
            }
        }

        //TODO ZoomCodeClear
        //Collections.sort(chunkList, new ChunkCoordComparator(par1EntityPlayerMP));
        //TODO ZoomCodeStart
        Collections.sort(chunkList, new ChunkCoordComparator(par1EntityPlayerMP));
        //TODO ZoomCodeEnd

        for (final ChunkCoordIntPair pair : chunkList) {
            this.getOrCreateChunkWatcher(pair.chunkXPos, pair.chunkZPos, true).addPlayer(par1EntityPlayerMP);
        }

        // CraftBukkit end
        this.players.add(par1EntityPlayerMP);
        this.filterChunkLoadQueue(par1EntityPlayerMP);
    }

    /**
     * Removes all chunks from the given player's chunk load queue that are not in viewing range of the player.
     */
    public void filterChunkLoadQueue(final EntityPlayerMP par1EntityPlayerMP) {
        final ArrayList arraylist = new ArrayList(par1EntityPlayerMP.loadedChunks);
        int i = 0;
        final int j = this.playerViewRadius;
        final int k = (int) par1EntityPlayerMP.posX >> 4;
        final int l = (int) par1EntityPlayerMP.posZ >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = PlayerInstance.getChunkLocation(this.getOrCreateChunkWatcher(k, l, true));
        par1EntityPlayerMP.loadedChunks.clear();

        if (arraylist.contains(chunkcoordintpair)) {
            par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
        }

        int k1;

        for (k1 = 1; k1 <= j * 2; ++k1) {
            for (int l1 = 0; l1 < 2; ++l1) {
                final int[] aint = this.xzDirectionsConst[i++ % 4];

                for (int i2 = 0; i2 < k1; ++i2) {
                    i1 += aint[0];
                    j1 += aint[1];
                    chunkcoordintpair = PlayerInstance.getChunkLocation(this.getOrCreateChunkWatcher(k + i1, l + j1, true));

                    if (arraylist.contains(chunkcoordintpair)) {
                        par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
                    }
                }
            }
        }

        i %= 4;

        for (k1 = 0; k1 < j * 2; ++k1) {
            i1 += this.xzDirectionsConst[i][0];
            j1 += this.xzDirectionsConst[i][1];
            chunkcoordintpair = PlayerInstance.getChunkLocation(this.getOrCreateChunkWatcher(k + i1, l + j1, true));

            if (arraylist.contains(chunkcoordintpair)) {
                par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
            }
        }
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(final EntityPlayerMP par1EntityPlayerMP) {
        final int i = (int) par1EntityPlayerMP.managedPosX >> 4;
        final int j = (int) par1EntityPlayerMP.managedPosZ >> 4;

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k) {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l) {
                final PlayerInstance playerinstance = this.getOrCreateChunkWatcher(k, l, false);

                if (playerinstance != null) {
                    playerinstance.removePlayer(par1EntityPlayerMP);
                }
            }
        }

        this.players.remove(par1EntityPlayerMP);
    }

    /**
     * Determine if two rectangles centered at the given points overlap for the provided radius. Arguments: x1, z1, x2,
     * z2, radius.
     */
    private boolean overlaps(final int par1, final int par2, final int par3, final int par4, final int par5) {
        final int j1 = par1 - par3;
        final int k1 = par2 - par4;
        return j1 >= -par5 && j1 <= par5 ? k1 >= -par5 && k1 <= par5 : false;
    }

    /**
     * update chunks around a player being moved by server logic (e.g. cart, boat)
     */
    public void updateMountedMovingPlayer(final EntityPlayerMP par1EntityPlayerMP) {
        final int i = (int) par1EntityPlayerMP.posX >> 4;
        final int j = (int) par1EntityPlayerMP.posZ >> 4;
        final double d0 = par1EntityPlayerMP.managedPosX - par1EntityPlayerMP.posX;
        final double d1 = par1EntityPlayerMP.managedPosZ - par1EntityPlayerMP.posZ;
        final double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            final int k = (int) par1EntityPlayerMP.managedPosX >> 4;
            final int l = (int) par1EntityPlayerMP.managedPosZ >> 4;
            final int i1 = this.playerViewRadius;
            final int j1 = i - k;
            final int k1 = j - l;
            final List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>(); // CraftBukkit

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.overlaps(l1, i2, k, l, i1)) {
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2)); // CraftBukkit
                        }

                        if (!this.overlaps(l1 - j1, i2 - k1, i, j, i1)) {
                            final PlayerInstance playerinstance = this.getOrCreateChunkWatcher(l1 - j1, i2 - k1, false);

                            if (playerinstance != null) {
                                playerinstance.removePlayer(par1EntityPlayerMP);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(par1EntityPlayerMP);
                par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
                par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
                // CraftBukkit start - send nearest chunks first
                //TODO ZoomCodeClear
                //Collections.sort(chunksToLoad, new ChunkCoordComparator(par1EntityPlayerMP));
                //TODO ZoomCodeStart
                Collections.sort(chunksToLoad, new ChunkCoordComparator(par1EntityPlayerMP));
                //TODO ZoomCodeEnd

                for (final ChunkCoordIntPair pair : chunksToLoad) {
                    this.getOrCreateChunkWatcher(pair.chunkXPos, pair.chunkZPos, true).addPlayer(par1EntityPlayerMP);
                }

                if (i1 > 1 || i1 < -1 || j1 > 1 || j1 < -1) {
                    //TODO ZoomCodeClear
                    //Collections.sort(par1EntityPlayerMP.loadedChunks, new ChunkCoordComparator(par1EntityPlayerMP));
                    //TODO ZoomCodeStart
                    Collections.sort(par1EntityPlayerMP.loadedChunks, new ChunkCoordComparator(par1EntityPlayerMP));
                    //TODO ZoomCodeEnd
                }

                // CraftBukkit end
            }
        }
    }

    public boolean isPlayerWatchingChunk(final EntityPlayerMP par1EntityPlayerMP, final int par2, final int par3) {
        final PlayerInstance playerinstance = this.getOrCreateChunkWatcher(par2, par3, false);
        return playerinstance == null ? false : PlayerInstance.getPlayersInChunk(playerinstance).contains(par1EntityPlayerMP) && !par1EntityPlayerMP.loadedChunks.contains(PlayerInstance.getChunkLocation(playerinstance));
    }

    /**
     * Get the furthest viewable block given player's view distance
     */
    public static int getFurthestViewableBlock(final int par0) {
        return par0 * 16 - 16;
    }

    static WorldServer getWorldServer(final PlayerManager par0PlayerManager) {
        return par0PlayerManager.theWorldServer;
    }

    static LongHashMap getChunkWatchers(final PlayerManager par0PlayerManager) {
        return par0PlayerManager.playerInstances;
    }

    /**
     * Returns player instances as list
     */
    static Queue getChunkWatcherList(final PlayerManager par0PlayerManager)   // CraftBukkit List -> Queue
    {
        return par0PlayerManager.playerInstanceList;
    }

    static Queue getChunkWatchersWithPlayers(final PlayerManager par0PlayerManager)   // CraftBukkit List -> Queue
    {
        return par0PlayerManager.chunkWatcherWithPlayers;
    }

    //TODO ZoomCodeClear
    // CraftBukkit start - Sorter to load nearby chunks first
    /*private static class ChunkCoordComparator implements java.util.Comparator<ChunkCoordIntPair>
    {
        private int x;
        private int z;

        public ChunkCoordComparator(EntityPlayerMP entityplayer)
        {
            x = (int) entityplayer.posX >> 4;
            z = (int) entityplayer.posZ >> 4;
        }

        public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b)
        {
            if (a.equals(b))
            {
                return 0;
            }

            // Subtract current position to set center point
            int ax = a.chunkXPos - this.x;
            int az = a.chunkZPos - this.z;
            int bx = b.chunkXPos - this.x;
            int bz = b.chunkZPos - this.z;
            int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));

            if (result != 0)
            {
                return result;
            }

            if (ax < 0)
            {
                if (bx < 0)
                {
                    return bz - az;
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                if (bx < 0)
                {
                    return 1;
                }
                else
                {
                    return az - bz;
                }
            }
        }
    }*/
    // CraftBukkit end
}
