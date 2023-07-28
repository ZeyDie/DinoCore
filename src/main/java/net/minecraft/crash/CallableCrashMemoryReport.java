package net.minecraft.crash;

import net.minecraft.util.AxisAlignedBB;

import java.util.concurrent.Callable;

class CallableCrashMemoryReport implements Callable
{
    final CrashReport theCrashReport;

    CallableCrashMemoryReport(final CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    /**
     * Returns a string with allocated and used memory.
     */
    public String getMemoryReport()
    {
        final int i = AxisAlignedBB.getAABBPool().getlistAABBsize();
        final int j = 56 * i;
        final int k = j / 1024 / 1024;
        final int l = AxisAlignedBB.getAABBPool().getnextPoolIndex();
        final int i1 = 56 * l;
        final int j1 = i1 / 1024 / 1024;
        return i + " (" + j + " bytes; " + k + " MB) allocated, " + l + " (" + i1 + " bytes; " + j1 + " MB) used";
    }

    public Object call()
    {
        return this.getMemoryReport();
    }
}
