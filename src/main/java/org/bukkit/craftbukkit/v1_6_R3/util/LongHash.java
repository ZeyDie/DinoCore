package org.bukkit.craftbukkit.v1_6_R3.util;

public class LongHash {
    public static long toLong(final int msw, final int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }

    public static int msw(final long l) {
        return (int) (l >> 32);
    }

    public static int lsw(final long l) {
        return (int) (l) + Integer.MIN_VALUE; // Spigot - remove redundant &
    }
}
