package org.bukkit.craftbukkit.v1_6_R3.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public abstract class LazyHashSet<E> implements Set<E> {
    Set<E> reference = null;

    public int size() {
        return getReference().size();
    }

    public boolean isEmpty() {
        return getReference().isEmpty();
    }

    public boolean contains(final Object o) {
        return getReference().contains(o);
    }

    public Iterator<E> iterator() {
        return getReference().iterator();
    }

    public Object[] toArray() {
        return getReference().toArray();
    }

    public <T> T[] toArray(final T[] a) {
        return getReference().toArray(a);
    }

    public boolean add(final E o) {
        return getReference().add(o);
    }

    public boolean remove(final Object o) {
        return getReference().remove(o);
    }

    public boolean containsAll(final Collection<?> c) {
        return getReference().containsAll(c);
    }

    public boolean addAll(final Collection<? extends E> c) {
        return getReference().addAll(c);
    }

    public boolean retainAll(final Collection<?> c) {
        return getReference().retainAll(c);
    }

    public boolean removeAll(final Collection<?> c) {
        return getReference().removeAll(c);
    }

    public void clear() {
        getReference().clear();
    }

    public Set<E> getReference() {
        final Set<E> reference = this.reference ;
        if (reference != null) {
            return reference;
        }
        return this.reference = makeReference();
    }

    abstract Set<E> makeReference();

    public boolean isLazy() {
        return reference == null;
    }

    @Override
    public int hashCode() {
        return 157 * getReference().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final LazyHashSet<?> that = (LazyHashSet<?>) obj;
        return (this.isLazy() && that.isLazy()) || this.getReference().equals(that.getReference());
    }

    @Override
    public String toString() {
        return getReference().toString();
    }
}
