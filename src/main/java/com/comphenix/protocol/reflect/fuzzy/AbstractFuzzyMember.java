package com.comphenix.protocol.reflect.fuzzy;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Member;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractFuzzyMember<T extends Member> extends AbstractFuzzyMatcher<T> {
    protected int modifiersRequired;
    protected int modifiersBanned;
    protected Pattern nameRegex;
    protected AbstractFuzzyMatcher<Class<?>> declaringMatcher;
    protected transient boolean sealed;

    protected AbstractFuzzyMember() {
        this.declaringMatcher = ClassExactMatcher.MATCH_ALL;
    }

    protected void prepareBuild() {
    }

    protected AbstractFuzzyMember(AbstractFuzzyMember<T> other) {
        this.declaringMatcher = ClassExactMatcher.MATCH_ALL;
        this.modifiersRequired = other.modifiersRequired;
        this.modifiersBanned = other.modifiersBanned;
        this.nameRegex = other.nameRegex;
        this.declaringMatcher = other.declaringMatcher;
        this.sealed = true;
    }

    public int getModifiersRequired() {
        return this.modifiersRequired;
    }

    public int getModifiersBanned() {
        return this.modifiersBanned;
    }

    public Pattern getNameRegex() {
        return this.nameRegex;
    }

    public AbstractFuzzyMatcher<Class<?>> getDeclaringMatcher() {
        return this.declaringMatcher;
    }

    public boolean isMatch(T value, Object parent) {
        int mods = value.getModifiers();
        return (mods & this.modifiersRequired) == this.modifiersRequired && (mods & this.modifiersBanned) == 0 && this.declaringMatcher.isMatch(value.getDeclaringClass(), value) && this.isNameMatch(value.getName());
    }

    private boolean isNameMatch(String name) {
        return this.nameRegex == null ? true : this.nameRegex.matcher(name).matches();
    }

    protected int calculateRoundNumber() {
        if (!this.sealed) {
            throw new IllegalStateException("Cannot calculate round number during construction.");
        } else {
            return this.declaringMatcher.getRoundNumber();
        }
    }

    public String toString() {
        return this.getKeyValueView().toString();
    }

    protected Map<String, Object> getKeyValueView() {
        Map<String, Object> map = Maps.newLinkedHashMap();
        if (this.modifiersRequired != Integer.MAX_VALUE || this.modifiersBanned != 0) {
            map.put("modifiers", String.format("[required: %s, banned: %s]", getBitView(this.modifiersRequired, 16), getBitView(this.modifiersBanned, 16)));
        }

        if (this.nameRegex != null) {
            map.put("name", this.nameRegex.pattern());
        }

        if (this.declaringMatcher != ClassExactMatcher.MATCH_ALL) {
            map.put("declaring", this.declaringMatcher);
        }

        return map;
    }

    private static String getBitView(int value, int bits) {
        if (bits >= 0 && bits <= 31) {
            int snipped = value & (1 << bits) - 1;
            return Integer.toBinaryString(snipped);
        } else {
            throw new IllegalArgumentException("Bits must be a value between 0 and 32");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AbstractFuzzyMember)) {
            return false;
        } else {
            AbstractFuzzyMember<T> other = (AbstractFuzzyMember) obj;
            return this.modifiersBanned == other.modifiersBanned && this.modifiersRequired == other.modifiersRequired && FuzzyMatchers.checkPattern(this.nameRegex, other.nameRegex) && Objects.equal(this.declaringMatcher, other.declaringMatcher);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.modifiersBanned, this.modifiersRequired, this.nameRegex != null ? this.nameRegex.pattern() : null, this.declaringMatcher);
    }

    public abstract static class Builder<T extends AbstractFuzzyMember<?>> {
        protected T member = this.initialMember();

        public Builder() {
        }

        public Builder<T> requireModifier(int modifier) {
            AbstractFuzzyMember var10000 = this.member;
            var10000.modifiersRequired |= modifier;
            return this;
        }

        public Builder<T> requirePublic() {
            return this.requireModifier(1);
        }

        public Builder<T> banModifier(int modifier) {
            AbstractFuzzyMember var10000 = this.member;
            var10000.modifiersBanned |= modifier;
            return this;
        }

        public Builder<T> nameRegex(String regex) {
            this.member.nameRegex = Pattern.compile(regex);
            return this;
        }

        public Builder<T> nameRegex(Pattern pattern) {
            this.member.nameRegex = pattern;
            return this;
        }

        public Builder<T> nameExact(String name) {
            return this.nameRegex(Pattern.quote(name));
        }

        public Builder<T> declaringClassExactType(Class<?> declaringClass) {
            this.member.declaringMatcher = FuzzyMatchers.matchExact(declaringClass);
            return this;
        }

        public Builder<T> declaringClassSuperOf(Class<?> declaringClass) {
            this.member.declaringMatcher = FuzzyMatchers.matchSuper(declaringClass);
            return this;
        }

        public Builder<T> declaringClassDerivedOf(Class<?> declaringClass) {
            this.member.declaringMatcher = FuzzyMatchers.matchDerived(declaringClass);
            return this;
        }

        public Builder<T> declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher) {
            this.member.declaringMatcher = classMatcher;
            return this;
        }

        @NotNull
        protected abstract T initialMember();

        public abstract T build();
    }
}
