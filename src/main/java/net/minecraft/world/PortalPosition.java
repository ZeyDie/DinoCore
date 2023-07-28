package net.minecraft.world;

import net.minecraft.util.ChunkCoordinates;

public class PortalPosition extends ChunkCoordinates
{
    /** The worldtime at which this PortalPosition was last verified */
    public long lastUpdateTime;

    /** The teleporter to which this PortalPosition applies */
    final Teleporter teleporterInstance;

    public PortalPosition(final Teleporter par1Teleporter, final int par2, final int par3, final int par4, final long par5)
    {
        super(par2, par3, par4);
        this.teleporterInstance = par1Teleporter;
        this.lastUpdateTime = par5;
    }
}
