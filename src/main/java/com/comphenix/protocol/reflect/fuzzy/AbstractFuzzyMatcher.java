package com.comphenix.protocol.reflect.fuzzy;

import com.google.common.primitives.Ints;

public abstract class AbstractFuzzyMatcher<T> implements Comparable<AbstractFuzzyMatcher<T>> {
    private Integer roundNumber;

    public AbstractFuzzyMatcher() {
    }

    public abstract boolean isMatch(T var1, Object var2);

    protected abstract int calculateRoundNumber();

    public final int getRoundNumber() {
        return this.roundNumber == null ? this.roundNumber = this.calculateRoundNumber() : this.roundNumber;
    }

    protected final int combineRounds(int roundA, int roundB) {
        if (roundA == 0) {
            return roundB;
        } else {
            return roundB == 0 ? roundA : Math.max(roundA, roundB);
        }
    }

    protected final int combineRounds(Integer... rounds) {
        if (rounds.length < 2) {
            throw new IllegalArgumentException("Must supply at least two arguments.");
        } else {
            int reduced = this.combineRounds(rounds[0], rounds[1]);

            for(int i = 2; i < rounds.length; ++i) {
                reduced = this.combineRounds(reduced, rounds[i]);
            }

            return reduced;
        }
    }

    public int compareTo(AbstractFuzzyMatcher<T> obj) {
        return obj != null ? Ints.compare(this.getRoundNumber(), obj.getRoundNumber()) : -1;
    }

    public AbstractFuzzyMatcher<T> inverted() {
        return new AbstractFuzzyMatcher<T>() {
            @Override
            public boolean isMatch(T value, Object parent) {
                return !AbstractFuzzyMatcher.this.isMatch(value, parent);
            }

            @Override
            protected int calculateRoundNumber() {
                return -2;
            }
        };
    }

    public AbstractFuzzyMatcher<T> and(final AbstractFuzzyMatcher<T> other) {
        return new AbstractFuzzyMatcher<T>() {
            @Override
            public boolean isMatch(T value, Object parent) {
                return AbstractFuzzyMatcher.this.isMatch(value, parent) && other.isMatch(value, parent);
            }

            @Override
            protected int calculateRoundNumber() {
                return this.combineRounds(AbstractFuzzyMatcher.this.getRoundNumber(), other.getRoundNumber());
            }
        };
    }

    public AbstractFuzzyMatcher<T> or(final AbstractFuzzyMatcher<T> other) {
        return new AbstractFuzzyMatcher<T>() {
            @Override
            public boolean isMatch(T value, Object parent) {
                return AbstractFuzzyMatcher.this.isMatch(value, parent) || other.isMatch(value, parent);
            }

            @Override
            protected int calculateRoundNumber() {
                return this.combineRounds(AbstractFuzzyMatcher.this.getRoundNumber(), other.getRoundNumber());
            }
        };
    }
}
