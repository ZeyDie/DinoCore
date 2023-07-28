package org.bukkit.util;

import java.lang.reflect.Array;

public class Java15Compat {
    @SuppressWarnings("unchecked")
    public static <T> T[] Arrays_copyOfRange(final T[] original, final int start, final int end) {
        if (original.length >= start && 0 <= start) {
            if (start <= end) {
                final int length = end - start;
                final int copyLength = Math.min(length, original.length - start);
                final T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), length);

                System.arraycopy(original, start, copy, 0, copyLength);
                return copy;
            }
            throw new IllegalArgumentException();
        }
        throw new ArrayIndexOutOfBoundsException();
    }
}
