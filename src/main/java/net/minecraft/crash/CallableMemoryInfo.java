package net.minecraft.crash;

import java.util.concurrent.Callable;

class CallableMemoryInfo implements Callable
{
    /** Reference to the CrashReport object. */
    final CrashReport theCrashReport;

    CallableMemoryInfo(final CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    /**
     * Returns the memory information as a String.  Includes the Free Memory in bytes and MB, Total Memory in bytes and
     * MB, and Max Memory in Bytes and MB.
     */
    public String getMemoryInfoAsString()
    {
        final Runtime runtime = Runtime.getRuntime();
        final long i = runtime.maxMemory();
        final long j = runtime.totalMemory();
        final long k = runtime.freeMemory();
        final long l = i / 1024L / 1024L;
        final long i1 = j / 1024L / 1024L;
        final long j1 = k / 1024L / 1024L;
        return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
    }

    public Object call()
    {
        return this.getMemoryInfoAsString();
    }
}
