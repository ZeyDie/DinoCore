package net.minecraft.profiler;

import java.util.List;

// CraftBukkit start - Strip down to empty methods, performance cost
public class Profiler
{
    /** Flag profiling enabled */
    public boolean profilingEnabled = false;

    /**
     * Clear profiling.
     */
    public final void clearProfiling() { }

    /**
     * Start section
     */
    public final void startSection(final String par1Str) { }

    /**
     * End section
     */
    public final void endSection() { }

    /**
     * Get profiling data
     */
    public final List getProfilingData(final String par1Str)
    {
        return null;
    }

    /**
     * End current section and start a new section
     */
    public final void endStartSection(final String par1Str) { }
    public final String getNameOfLastSection()
    {
        return null;
    }
}
// CraftBukkit end
