package net.minecraft.crash;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;

class CallableSuspiciousClasses implements Callable
{
    final CrashReport theCrashReport;

    CallableSuspiciousClasses(final CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    public String callSuspiciousClasses() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
    {
        final StringBuilder stringbuilder = new StringBuilder();
        final ArrayList arraylist;

        try
        {
            final Field field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);
            arraylist = new ArrayList((Vector)field.get(CrashReport.class.getClassLoader()));
        }
        catch (final Exception ex)
        {
            return "";
        }

        boolean flag = true;
        final boolean flag1 = !CrashReport.class.getCanonicalName().equals("net.minecraft.CrashReport");
        final HashMap hashmap = new HashMap();
        String s = "";
        Collections.sort(arraylist, new ComparatorClassSorter(this));
        final Iterator iterator = arraylist.iterator();

        while (iterator.hasNext())
        {
            final Class oclass = (Class)iterator.next();

            if (oclass != null)
            {
                final String s1 = oclass.getCanonicalName();

                if (s1 != null && !s1.startsWith("org.lwjgl.") && !s1.startsWith("paulscode.") && !s1.startsWith("org.bouncycastle.") && !s1.startsWith("argo.") && !s1.startsWith("com.jcraft.") && !s1.startsWith("com.fasterxml.") && !s1.startsWith("com.google.") && !s1.startsWith("joptsimple.") && !s1.startsWith("org.apache.") && !s1.equals("util.GLX"))
                {
                    if (flag1)
                    {
                        if (s1.length() <= 3 || s1.equals("net.minecraft.client.main.Main") || s1.equals("net.minecraft.client.Minecraft") || s1.equals("net.minecraft.client.ClientBrandRetriever") || s1.equals("net.minecraft.server.MinecraftServer"))
                        {
                            continue;
                        }
                    }
                    else if (s1.startsWith("net.minecraft"))
                    {
                        continue;
                    }

                    final Package opackage = oclass.getPackage();
                    final String s2 = opackage == null ? "" : opackage.getName();

                    if (hashmap.containsKey(s2))
                    {
                        final int i = ((Integer)hashmap.get(s2)).intValue();
                        hashmap.put(s2, Integer.valueOf(i + 1));

                        if (i == 3)
                        {
                            if (!flag)
                            {
                                stringbuilder.append(", ");
                            }

                            stringbuilder.append("...");
                            flag = false;
                            continue;
                        }

                        if (i > 3)
                        {
                            continue;
                        }
                    }
                    else
                    {
                        hashmap.put(s2, Integer.valueOf(1));
                    }

                    if (!s.equals(s2) && !s.isEmpty())
                    {
                        stringbuilder.append("], ");
                    }

                    if (!flag && s.equals(s2))
                    {
                        stringbuilder.append(", ");
                    }

                    if (!s.equals(s2))
                    {
                        stringbuilder.append("[");
                        stringbuilder.append(s2);
                        stringbuilder.append(".");
                    }

                    stringbuilder.append(oclass.getSimpleName());
                    s = s2;
                    flag = false;
                }
            }
        }

        if (flag)
        {
            stringbuilder.append("No suspicious classes found.");
        }
        else
        {
            stringbuilder.append("]");
        }

        return stringbuilder.toString();
    }

    public Object call()
    {
        return "FML and Forge are installed";
    }
}
