package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet55BlockDestroy;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet62LevelSound;
import net.minecraft.server.MinecraftServer;

import java.util.Iterator;

public class WorldManager implements IWorldAccess
{
    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /** The WorldServer object. */
    public WorldServer theWorldServer; // CraftBukkit - private -> public

    public WorldManager(final MinecraftServer par1MinecraftServer, final WorldServer par2WorldServer)
    {
        this.mcServer = par1MinecraftServer;
        this.theWorldServer = par2WorldServer;
    }

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(final String par1Str, final double par2, final double par4, final double par6, final double par8, final double par10, final double par12) {}

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityCreate(final Entity par1Entity)
    {
        this.theWorldServer.getEntityTracker().addEntityToTracker(par1Entity);
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityDestroy(final Entity par1Entity)
    {
        this.theWorldServer.getEntityTracker().removeEntityFromAllTrackingPlayers(par1Entity);
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    public void playSound(final String par1Str, final double par2, final double par4, final double par6, final float par8, final float par9)
    {
        this.mcServer.getConfigurationManager().sendToAllNear(par2, par4, par6, par8 > 1.0F ? (double)(16.0F * par8) : 16.0D, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(par1Str, par2, par4, par6, par8, par9));
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(final EntityPlayer par1EntityPlayer, final String par2Str, final double par3, final double par5, final double par7, final float par9, final float par10)
    {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(par1EntityPlayer, par3, par5, par7, par9 > 1.0F ? (double)(16.0F * par9) : 16.0D, this.theWorldServer.provider.dimensionId, new Packet62LevelSound(par2Str, par3, par5, par7, par9, par10));
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {}

    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it),
     * including the tile entity description packet if applicable. Args: x, y, z
     */
    public void markBlockForUpdate(final int par1, final int par2, final int par3)
    {
        this.theWorldServer.getPlayerManager().markBlockForUpdate(par1, par2, par3);
    }

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    public void markBlockForRenderUpdate(final int par1, final int par2, final int par3) {}

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    public void playRecord(final String par1Str, final int par2, final int par3, final int par4) {}

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    public void playAuxSFX(final EntityPlayer par1EntityPlayer, final int par2, final int par3, final int par4, final int par5, final int par6)
    {
        this.mcServer.getConfigurationManager().sendToAllNearExcept(par1EntityPlayer, (double)par3, (double)par4, (double)par5, 64.0D, this.theWorldServer.provider.dimensionId, new Packet61DoorChange(par2, par3, par4, par5, par6, false));
    }

    public void broadcastSound(final int par1, final int par2, final int par3, final int par4, final int par5)
    {
        this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet61DoorChange(par1, par2, par3, par4, par5, true));
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void destroyBlockPartially(final int par1, final int par2, final int par3, final int par4, final int par5)
    {
        final Iterator iterator = this.mcServer.getConfigurationManager().playerEntityList.iterator();

        while (iterator.hasNext())
        {
            final EntityPlayerMP entityplayermp = (EntityPlayerMP)iterator.next();

            if (entityplayermp != null && entityplayermp.worldObj == this.theWorldServer && entityplayermp.entityId != par1)
            {
                final double d0 = (double)par2 - entityplayermp.posX;
                final double d1 = (double)par3 - entityplayermp.posY;
                final double d2 = (double)par4 - entityplayermp.posZ;

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D)
                {
                    entityplayermp.playerNetServerHandler.sendPacketToPlayer(new Packet55BlockDestroy(par1, par2, par3, par4, par5));
                }
            }
        }
    }
}
