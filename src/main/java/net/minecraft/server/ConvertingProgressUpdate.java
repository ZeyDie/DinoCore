package net.minecraft.server;

import net.minecraft.util.IProgressUpdate;

public class ConvertingProgressUpdate implements IProgressUpdate
{
    private long field_96245_b;

    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    public ConvertingProgressUpdate(final MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
        this.field_96245_b = MinecraftServer.getSystemTimeMillis();
    }

    /**
     * "Saving level", or the loading,or downloading equivelent
     */
    public void displayProgressMessage(final String par1Str) {}

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    public void setLoadingProgress(final int par1)
    {
        if (MinecraftServer.getSystemTimeMillis() - this.field_96245_b >= 1000L)
        {
            this.field_96245_b = MinecraftServer.getSystemTimeMillis();
            this.mcServer.getLogAgent().logInfo("Converting... " + par1 + "%");
        }
    }

    /**
     * This is called with "Working..." by resetProgressAndMessage
     */
    public void resetProgresAndWorkingMessage(final String par1Str) {}
}
