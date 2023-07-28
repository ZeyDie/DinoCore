package net.minecraft.crash;

import java.util.Comparator;

class ComparatorClassSorter implements Comparator
{
    final CallableSuspiciousClasses theSuspiciousClasses;

    ComparatorClassSorter(final CallableSuspiciousClasses par1CallableSuspiciousClasses)
    {
        this.theSuspiciousClasses = par1CallableSuspiciousClasses;
    }

    public int func_85081_a(final Class par1Class, final Class par2Class)
    {
        final String s = par1Class.getPackage() == null ? "" : par1Class.getPackage().getName();
        final String s1 = par2Class.getPackage() == null ? "" : par2Class.getPackage().getName();
        return s.compareTo(s1);
    }

    public int compare(final Object par1Obj, final Object par2Obj)
    {
        return this.func_85081_a((Class)par1Obj, (Class)par2Obj);
    }
}
