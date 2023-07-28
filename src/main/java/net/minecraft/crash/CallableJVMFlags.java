package net.minecraft.crash;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

class CallableJVMFlags implements Callable
{
    /** Reference to the CrashReport object. */
    final CrashReport theCrashReport;

    CallableJVMFlags(final CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    /**
     * Returns the number of JVM Flags along with the passed JVM Flags.
     */
    public String getJVMFlagsAsString()
    {
        final RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        final List list = runtimemxbean.getInputArguments();
        int i = 0;
        final StringBuilder stringbuilder = new StringBuilder();
        final Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            final String s = (String)iterator.next();

            if (s.startsWith("-X"))
            {
                if (i++ > 0)
                {
                    stringbuilder.append(" ");
                }

                stringbuilder.append(s);
            }
        }

        return String.format("%d total; %s", new Object[] {Integer.valueOf(i), stringbuilder.toString()});
    }

    public Object call()
    {
        return this.getJVMFlagsAsString();
    }
}
