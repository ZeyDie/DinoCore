package org.bukkit.craftbukkit.v1_6_R3;


import org.bukkit.Location;
import org.bukkit.TravelAgent;

public class CraftTravelAgent extends net.minecraft.world.Teleporter implements TravelAgent {

    public static TravelAgent DEFAULT = null;

    private int searchRadius = 128;
    private int creationRadius = 16;
    private boolean canCreatePortal = true;

    public CraftTravelAgent(final net.minecraft.world.WorldServer worldserver) {
        super(worldserver);
        if (DEFAULT == null && worldserver.provider.dimensionId == 0) {
            DEFAULT = this;
        }
    }

    public Location findOrCreate(final Location target) {
        final net.minecraft.world.WorldServer worldServer = ((CraftWorld) target.getWorld()).getHandle();
        // Cauldron start
        final boolean before = worldServer.theChunkProviderServer.loadChunkOnProvideRequest;
        worldServer.theChunkProviderServer.loadChunkOnProvideRequest = true;
        // Cauldron end
        Location found = this.findPortal(target);
        if (found == null) {
            if (this.getCanCreatePortal() && this.createPortal(target)) {
                found = this.findPortal(target);
            } else {
                found = target; // fallback to original if unable to find or create
            }
        }

        worldServer.theChunkProviderServer.loadChunkOnProvideRequest = before;
        return found;
    }

    public Location findPortal(final Location location) {
        final net.minecraft.world.Teleporter pta = ((CraftWorld) location.getWorld()).getHandle().getDefaultTeleporter(); // Should be getTravelAgent
        final net.minecraft.util.ChunkCoordinates found = pta.findPortal(location.getX(), location.getY(), location.getZ(), this.getSearchRadius());
        return found != null ? new Location(location.getWorld(), found.posX, found.posY, found.posZ, location.getYaw(), location.getPitch()) : null;
    }

    public boolean createPortal(final Location location) {
        final net.minecraft.world.Teleporter pta = ((CraftWorld) location.getWorld()).getHandle().getDefaultTeleporter();
        return pta.createPortal(location.getX(), location.getY(), location.getZ(), this.getCreationRadius());
    }

    public TravelAgent setSearchRadius(final int radius) {
        this.searchRadius = radius;
        return this;
    }

    public int getSearchRadius() {
        return this.searchRadius;
    }

    public TravelAgent setCreationRadius(final int radius) {
        this.creationRadius = radius < 2 ? 0 : radius;
        return this;
    }

    public int getCreationRadius() {
        return this.creationRadius;
    }

    public boolean getCanCreatePortal() {
        return this.canCreatePortal;
    }

    public void setCanCreatePortal(final boolean create) {
        this.canCreatePortal = create;
    }
}
