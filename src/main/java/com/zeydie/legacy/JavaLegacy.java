package com.zeydie.legacy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class JavaLegacy {
    public static <T> void sort(final List<T> list, final Comparator<? super T> c) {
        final T[] toSort = list.toArray((T[]) new Object[list.size()]);
        Arrays.sort(toSort, c);

        for (int j = 0; j < toSort.length; j++)
            list.set(j, toSort[j]);
    }
}
