package net.minecraft.entity;

import com.google.common.collect.Sets;
import com.zeydie.settings.optimization.CoreSettings;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class EntityTracker {
    private final WorldServer theWorld;

    /**
     * List of tracked entities, used for iteration operations on tracked entities.
     */
    //TODO ZoomCodeClear
    //private Set trackedEntities = new HashSet();
    //TOOD ZoomCodeStart
    private final Set<EntityTrackerEntry> trackedEntities = Sets.newCopyOnWriteArraySet();
    //TODO ZoomCodeEnd

    public IntHashMap trackedEntityIDs = new IntHashMap(); // CraftBukkit - private -> public
    private final int entityViewDistance;

    public EntityTracker(final WorldServer par1WorldServer) {
        this.theWorld = par1WorldServer;
        this.entityViewDistance = par1WorldServer.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
    }

    /**
     * if entity is a player sends all tracked events to the player, otherwise, adds with a visibility and update arate
     * based on the class type
     */
    public void addEntityToTracker(final Entity par1Entity) {
        if (EntityRegistry.instance().tryTrackingEntity(this, par1Entity)) {
            return;
        }

        if (par1Entity instanceof EntityPlayerMP) {
            this.addEntityToTracker(par1Entity, 512, 2);
            final EntityPlayerMP entityplayermp = (EntityPlayerMP) par1Entity;

            for (final EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
                if (entitytrackerentry.myEntity != entityplayermp) {
                    entitytrackerentry.tryStartWachingThis(entityplayermp);
                }
            }
        } else if (par1Entity instanceof EntityFishHook) {
            this.addEntityToTracker(par1Entity, 64, 5, true);
        } else if (par1Entity instanceof EntityArrow) {
            this.addEntityToTracker(par1Entity, 64, 20, false);
        } else if (par1Entity instanceof EntitySmallFireball) {
            this.addEntityToTracker(par1Entity, 64, 10, false);
        } else if (par1Entity instanceof EntityFireball) {
            this.addEntityToTracker(par1Entity, 64, 10, false);
        } else if (par1Entity instanceof EntitySnowball) {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        } else if (par1Entity instanceof EntityEnderPearl) {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        } else if (par1Entity instanceof EntityEnderEye) {
            this.addEntityToTracker(par1Entity, 64, 4, true);
        } else if (par1Entity instanceof EntityEgg) {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        } else if (par1Entity instanceof EntityPotion) {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        } else if (par1Entity instanceof EntityExpBottle) {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        } else if (par1Entity instanceof EntityFireworkRocket) {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        } else if (par1Entity instanceof EntityItem) {
            this.addEntityToTracker(par1Entity, 64, 20, true);
        } else if (par1Entity instanceof EntityMinecart) {
            this.addEntityToTracker(par1Entity, 80, 3, true);
        } else if (par1Entity instanceof EntityBoat) {
            this.addEntityToTracker(par1Entity, 80, 3, true);
        } else if (par1Entity instanceof EntitySquid) {
            this.addEntityToTracker(par1Entity, 64, 3, true);
        } else if (par1Entity instanceof EntityWither) {
            this.addEntityToTracker(par1Entity, 80, 3, false);
        } else if (par1Entity instanceof EntityBat) {
            this.addEntityToTracker(par1Entity, 80, 3, false);
        } else if (par1Entity instanceof IAnimals) {
            this.addEntityToTracker(par1Entity, 80, 3, true);
        } else if (par1Entity instanceof EntityTNTPrimed) {
            this.addEntityToTracker(par1Entity, 160, 10, true);
        } else if (par1Entity instanceof EntityFallingSand) {
            this.addEntityToTracker(par1Entity, 160, 20, true);
        } else if (par1Entity instanceof EntityHanging) {
            this.addEntityToTracker(par1Entity, 160, Integer.MAX_VALUE, false);
        } else if (par1Entity instanceof EntityXPOrb) {
            this.addEntityToTracker(par1Entity, 160, 20, true);
        } else if (par1Entity instanceof EntityEnderCrystal) {
            this.addEntityToTracker(par1Entity, 256, Integer.MAX_VALUE, false);
        }
    }

    public void addEntityToTracker(final Entity par1Entity, final int par2, final int par3) {
        this.addEntityToTracker(par1Entity, par2, par3, false);
    }

    public void addEntityToTracker(final Entity par1Entity, int par2, final int par3, final boolean par4) {

        //TODO ZoomCodeStart
        int par21 = par2;
        if (CoreSettings.getInstance().getSettings().isAsynchronousWarnings())
            //TODO ZoomCodeEnd

            if (Thread.currentThread() != net.minecraft.server.MinecraftServer.getServer().primaryThread) {
                throw new IllegalStateException("Asynchronous entity track!");    // Spigot
            }

        par21 = org.spigotmc.TrackingRange.getEntityTrackingRange(par1Entity, par21); // Spigot

        if (par21 > this.entityViewDistance) {
            par21 = this.entityViewDistance;
        }

        try {

            //TODO ZoomCodeStart
            if (!CoreSettings.getInstance().getSettings().isAsynchronousWarnings())
                if (this.trackedEntityIDs.containsItem(par1Entity.entityId))
                    return;
            //TODO ZoomCodeEnd

            if (this.trackedEntityIDs.containsItem(par1Entity.entityId)) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            final EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(par1Entity, par21, par3, par4);
            this.trackedEntities.add(entitytrackerentry);
            this.trackedEntityIDs.addKey(par1Entity.entityId, entitytrackerentry);
            entitytrackerentry.sendEventsToPlayers(this.theWorld.playerEntities);
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding entity to track");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity To Track");
            crashreportcategory.addCrashSection("Tracking range", par21 + " blocks");
            crashreportcategory.addCrashSectionCallable("Update interval", new CallableEntityTracker(this, par3));
            par1Entity.addEntityCrashInfo(crashreportcategory);
            final CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Entity That Is Already Tracked");
            ((EntityTrackerEntry) this.trackedEntityIDs.lookup(par1Entity.entityId)).myEntity.addEntityCrashInfo(crashreportcategory1);

            try {
                throw new ReportedException(crashreport);
            } catch (final ReportedException reportedexception) {
                System.err.println("\"Silently\" catching entity tracking error.");
                reportedexception.printStackTrace();
            }
        }
    }

    public void removeEntityFromAllTrackingPlayers(final Entity par1Entity) {

        //TODO ZoomCodeStart
        if (CoreSettings.getInstance().getSettings().isAsynchronousWarnings())
            //TODO ZoomCodeEnd

            if (Thread.currentThread() != net.minecraft.server.MinecraftServer.getServer().primaryThread) {
                throw new IllegalStateException("Asynchronous entity untrack!");    // Spigot
            }

        if (par1Entity instanceof EntityPlayerMP) {
            final EntityPlayerMP entityplayermp = (EntityPlayerMP) par1Entity;
            final Iterator iterator = this.trackedEntities.iterator();

            while (iterator.hasNext()) {
                final EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();
                entitytrackerentry.removeFromWatchingList(entityplayermp);
            }
        }

        final EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) this.trackedEntityIDs.removeObject(par1Entity.entityId);

        if (entitytrackerentry1 != null) {
            this.trackedEntities.remove(entitytrackerentry1);
            entitytrackerentry1.informAllAssociatedPlayersOfItemDestruction();
        }
    }

    public void updateTrackedEntities() {
        try {
            final ArrayList arraylist = new ArrayList();
            final Iterator iterator = this.trackedEntities.iterator();

            while (iterator.hasNext()) {
                final EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();
                entitytrackerentry.sendLocationToAllClients(this.theWorld.playerEntities);

                if (entitytrackerentry.playerEntitiesUpdated && entitytrackerentry.myEntity instanceof EntityPlayerMP) {
                    arraylist.add(entitytrackerentry.myEntity);
                }
            }

            for (int i = 0; i < arraylist.size(); ++i) {
                final EntityPlayerMP entityplayermp = (EntityPlayerMP) arraylist.get(i);
                final Iterator iterator1 = this.trackedEntities.iterator();

                while (iterator1.hasNext()) {
                    final EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) iterator1.next();

                    if (entitytrackerentry1.myEntity != entityplayermp) {
                        entitytrackerentry1.tryStartWachingThis(entityplayermp);
                    }
                }
            }
        } catch (final Throwable t) {}
    }

    /**
     * does not send the packet to the entity if the entity is a player
     */
    public void sendPacketToAllPlayersTrackingEntity(final Entity par1Entity, final Packet par2Packet) {
        final EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntityIDs.lookup(par1Entity.entityId);

        if (entitytrackerentry != null) {
            entitytrackerentry.sendPacketToAllTrackingPlayers(par2Packet);
        }
    }

    /**
     * sends to the entity if the entity is a player
     */
    public void sendPacketToAllAssociatedPlayers(final Entity par1Entity, final Packet par2Packet) {
        final EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntityIDs.lookup(par1Entity.entityId);

        if (entitytrackerentry != null) {
            entitytrackerentry.sendPacketToAllAssociatedPlayers(par2Packet);
        }
    }

    public void removePlayerFromTrackers(final EntityPlayerMP par1EntityPlayerMP) {
        final Iterator iterator = this.trackedEntities.iterator();

        while (iterator.hasNext()) {
            final EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();
            entitytrackerentry.removePlayerFromTracker(par1EntityPlayerMP);
        }
    }

    public void func_85172_a(final EntityPlayerMP par1EntityPlayerMP, final Chunk par2Chunk) {
        final Iterator iterator = this.trackedEntities.iterator();

        while (iterator.hasNext()) {
            final EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            if (entitytrackerentry.myEntity != par1EntityPlayerMP && entitytrackerentry.myEntity.chunkCoordX == par2Chunk.xPosition && entitytrackerentry.myEntity.chunkCoordZ == par2Chunk.zPosition) {
                entitytrackerentry.tryStartWachingThis(par1EntityPlayerMP);
            }
        }
    }
}
