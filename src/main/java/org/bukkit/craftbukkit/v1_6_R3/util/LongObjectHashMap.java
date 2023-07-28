package org.bukkit.craftbukkit.v1_6_R3.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import static org.bukkit.craftbukkit.v1_6_R3.util.Java15Compat.Arrays_copyOf;

@SuppressWarnings("unchecked")
public class LongObjectHashMap<V> implements Cloneable, Serializable {
    static final long serialVersionUID = 2841537710170573815L;

    private static final long EMPTY_KEY = Long.MIN_VALUE;
    private static final int  BUCKET_SIZE = 4096;

    private transient long[][] keys;
    private transient V[][]    values;
    private transient int      modCount;
    private transient int      size;
    private transient org.spigotmc.FlatMap<V> flat = new org.spigotmc.FlatMap<V>(); // Spigot

    public LongObjectHashMap() {
        initialize();
    }

    public LongObjectHashMap(final Map<? extends Long, ? extends V> map) {
        this();
        putAll(map);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(final long key) {
        return get(key) != null;
    }

    public boolean containsValue(final V value) {
        for (final V val : values()) {
            if (val == value || val.equals(value)) {
                return true;
            }
        }

        return false;
    }

    public V get(final long key) {
        // Spigot start
        if ( size == 0 )
        {
            return null;
        }
        final V val = flat.get( key );
        if ( val != null )
        {
            return val;
        }
        // Spigot end
        final int index = (int) (keyIndex(key) & (BUCKET_SIZE - 1));
        final long[] inner = keys[index];
        if (inner == null) return null;

        for (int i = 0; i < inner.length; i++) {
            final long innerKey = inner[i];
            if (innerKey == EMPTY_KEY) {
                return null;
            } else if (innerKey == key) {
                return values[index][i];
            }
        }

        return null;
    }

    public V put(final long key, final V value) {
        flat.put(key, value); // Spigot
        final int index = (int) (keyIndex(key) & (BUCKET_SIZE - 1));
        long[] innerKeys = keys[index];
        V[] innerValues = values[index];
        modCount++;

        if (innerKeys == null) {
            // need to make a new chain
            keys[index] = innerKeys = new long[8];
            Arrays.fill(innerKeys, EMPTY_KEY);
            values[index] = innerValues = (V[]) new Object[8];
            innerKeys[0] = key;
            innerValues[0] = value;
            size++;
        } else {
            int i;
            for (i = 0; i < innerKeys.length; i++) {
                // found an empty spot in the chain to put this
                if (innerKeys[i] == EMPTY_KEY) {
                    size++;
                    innerKeys[i] = key;
                    innerValues[i] = value;
                    return null;
                }

                // found an existing entry in the chain with this key, replace it
                if (innerKeys[i] == key) {
                    final V oldValue = innerValues[i];
                    innerKeys[i] = key;
                    innerValues[i] = value;
                    return oldValue;
                }
            }

            // chain is full, resize it and add our new entry
            keys[index] = innerKeys = Arrays_copyOf(innerKeys, i << 1);
            Arrays.fill(innerKeys, i, innerKeys.length, EMPTY_KEY);
            values[index] = innerValues = Arrays_copyOf(innerValues, i << 1);
            innerKeys[i] = key;
            innerValues[i] = value;
            size++;
        }

        return null;
    }

    public V remove(final long key) {
        flat.remove(key); // Spigot
        final int index = (int) (keyIndex(key) & (BUCKET_SIZE - 1));
        final long[] inner = keys[index];
        if (inner == null) {
            return null;
        }

        for (int i = 0; i < inner.length; i++) {
            // hit the end of the chain, didn't find this entry
            if (inner[i] == EMPTY_KEY) {
                break;
            }

            if (inner[i] == key) {
                final V value = values[index][i];

                for (i++; i < inner.length; i++) {
                    if (inner[i] == EMPTY_KEY) {
                        break;
                    }

                    inner[i - 1] = inner[i];
                    values[index][i - 1] = values[index][i];
                }

                inner[i - 1] = EMPTY_KEY;
                values[index][i - 1] = null;
                size--;
                modCount++;
                return value;
            }
        }

        return null;
    }

    public void putAll(final Map<? extends Long, ? extends V> map) {
        for (final Map.Entry entry : map.entrySet()) {
            put((Long) entry.getKey(), (V) entry.getValue());
        }
    }

    public void clear() {
        if (size == 0) {
            return;
        }

        modCount++;
        size = 0;
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        flat = new org.spigotmc.FlatMap<V>();
    }

    public Set<Long> keySet() {
        return new KeySet();
    }

    public Collection<V> values() {
        return new ValueCollection();
    }

    /**
     * Returns a Set of Entry objects for the HashMap. This is not how the internal
     * implementation is laid out so this constructs the entire Set when called. For
     * this reason it should be avoided if at all possible.
     *
     * @return Set of Entry objects
     * @deprecated
     */
    @Deprecated
    public Set<Map.Entry<Long, V>> entrySet() {
        final HashSet<Map.Entry<Long, V>> set = new HashSet<Map.Entry<Long, V>>();
        for (final long key : keySet()) {
            set.add(new Entry(key, get(key)));
        }

        return set;
    }

    public Object clone() throws CloneNotSupportedException {
        final LongObjectHashMap clone = (LongObjectHashMap) super.clone();
        // Make sure we clear any existing information from the clone
        clone.clear();
        // Make sure the clone is properly setup for new entries
        clone.initialize();

        // Iterate through the data normally to do a safe clone
        for (final long key : keySet()) {
            final V value = get(key);
            clone.put(key, value);
        }

        return clone;
    }

    private void initialize() {
        keys = new long[BUCKET_SIZE][];
        values = (V[][]) new Object[BUCKET_SIZE][];
    }

    private long keyIndex(long key) {
        long key1 = key;
        key1 ^= key1 >>> 33;
        key1 *= 0xff51afd7ed558ccdL;
        key1 ^= key1 >>> 33;
        key1 *= 0xc4ceb9fe1a85ec53L;
        key1 ^= key1 >>> 33;
        return key1;
    }

    private void writeObject(final ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();

        for (final long key : keySet()) {
            final V value = get(key);
            outputStream.writeLong(key);
            outputStream.writeObject(value);
        }

        outputStream.writeLong(EMPTY_KEY);
        outputStream.writeObject(null);
    }

    private void readObject(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        initialize();

        while (true) {
            final long key = inputStream.readLong();
            final V value = (V) inputStream.readObject();
            if (key == EMPTY_KEY && value == null) {
                break;
            }

            put(key, value);
        }
    }


    private class ValueIterator implements Iterator<V> {
        private int count;
        private int index;
        private int innerIndex;
        private int expectedModCount;
        private long lastReturned = EMPTY_KEY;

        long prevKey = EMPTY_KEY;
        V prevValue;

        ValueIterator() {
            expectedModCount = LongObjectHashMap.this.modCount;
        }

        public boolean hasNext() {
            return count < LongObjectHashMap.this.size;
        }

        public void remove() {
            if (LongObjectHashMap.this.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            if (lastReturned == EMPTY_KEY) {
                throw new IllegalStateException();
            }

            count--;
            LongObjectHashMap.this.remove(lastReturned);
            lastReturned = EMPTY_KEY;
            expectedModCount = LongObjectHashMap.this.modCount;
        }

        public V next() {
            if (LongObjectHashMap.this.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            final long[][] keys = LongObjectHashMap.this.keys;
            count++;

            if (prevKey != EMPTY_KEY) {
                innerIndex++;
            }

            for (; index < keys.length; index++) {
                if (keys[index] != null) {
                    for (; innerIndex < keys[index].length; innerIndex++) {
                        final long key = keys[index][innerIndex];
                        final V value = values[index][innerIndex];
                        if (key == EMPTY_KEY) {
                            break;
                        }

                        lastReturned = key;
                        prevKey = key;
                        prevValue = value;
                        return prevValue;
                    }
                    innerIndex = 0;
                }
            }

            throw new NoSuchElementException();
        }
    }

    private class KeyIterator implements Iterator<Long> {
        final ValueIterator iterator;

        public KeyIterator() {
            iterator = new ValueIterator();
        }

        public void remove() {
            iterator.remove();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Long next() {
            iterator.next();
            return iterator.prevKey;
        }
    }


    private class KeySet extends AbstractSet<Long> {
        public void clear() {
            LongObjectHashMap.this.clear();
        }

        public int size() {
            return LongObjectHashMap.this.size();
        }

        public boolean contains(final Object key) {
            return key instanceof Long && LongObjectHashMap.this.containsKey((Long) key);

        }

        public boolean remove(final Object key) {
            return LongObjectHashMap.this.remove((Long) key) != null;
        }

        public Iterator<Long> iterator() {
            return new KeyIterator();
        }
    }


    private class ValueCollection extends AbstractCollection<V> {
        public void clear() {
            LongObjectHashMap.this.clear();
        }

        public int size() {
            return LongObjectHashMap.this.size();
        }

        public boolean contains(final Object value) {
            return LongObjectHashMap.this.containsValue((V) value);
        }

        public Iterator<V> iterator() {
            return new ValueIterator();
        }
    }


    private class Entry implements Map.Entry<Long, V> {
        private final Long key;
        private V value;

        Entry(final long k, final V v) {
            key = k;
            value = v;
        }

        public Long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(final V v) {
            final V old = value;
            value = v;
            put(key, v);
            return old;
        }
    }
}
