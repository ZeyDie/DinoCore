package com.comphenix.protocol.reflect.fuzzy;

import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Set;

class ClassSetMatcher extends AbstractFuzzyMatcher<Class<?>> {
    private final Set<Class<?>> classes;

    public ClassSetMatcher(Set<Class<?>> classes) {
        if (classes == null) {
            throw new IllegalArgumentException("Set of classes cannot be NULL.");
        } else {
            this.classes = classes;
        }
    }

    public boolean isMatch(Class<?> value, Object parent) {
        return this.classes.contains(value);
    }

    protected int calculateRoundNumber() {
        int roundNumber = 0;

        Class clazz;
        for(Iterator i$ = this.classes.iterator(); i$.hasNext(); roundNumber = this.combineRounds(roundNumber, -ClassExactMatcher.getClassNumber(clazz))) {
            clazz = (Class)i$.next();
        }

        return roundNumber;
    }

    public String toString() {
        return "match any: " + this.classes;
    }

    public int hashCode() {
        return this.classes.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return !(obj instanceof ClassSetMatcher) || Objects.equal(this.classes, ((ClassSetMatcher) obj).classes);
        }
    }
}
