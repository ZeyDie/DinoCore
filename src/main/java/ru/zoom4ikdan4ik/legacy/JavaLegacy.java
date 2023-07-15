package ru.zoom4ikdan4ik.legacy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class JavaLegacy {
    public final static <T> void sort(final List<T> list, final Comparator<? super T> c) {
        T[] toSort = list.toArray((T[]) new Object[list.size()]);
        Arrays.sort(toSort, c);

        for (int j = 0; j < toSort.length; j++)
            list.set(j, toSort[j]);
    }
}
