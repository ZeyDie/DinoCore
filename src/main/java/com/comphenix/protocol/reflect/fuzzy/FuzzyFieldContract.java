package com.comphenix.protocol.reflect.fuzzy;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

public class FuzzyFieldContract extends AbstractFuzzyMember<Field> {
    private AbstractFuzzyMatcher<Class<?>> typeMatcher;

    public static FuzzyFieldContract matchType(AbstractFuzzyMatcher<Class<?>> matcher) {
        return newBuilder().typeMatches(matcher).build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private FuzzyFieldContract() {
        this.typeMatcher = ClassExactMatcher.MATCH_ALL;
    }

    public AbstractFuzzyMatcher<Class<?>> getTypeMatcher() {
        return this.typeMatcher;
    }

    private FuzzyFieldContract(FuzzyFieldContract other) {
        super(other);
        this.typeMatcher = ClassExactMatcher.MATCH_ALL;
        this.typeMatcher = other.typeMatcher;
    }

    public boolean isMatch(Field value, Object parent) {
        return super.isMatch(value, parent) ? this.typeMatcher.isMatch(value.getType(), value) : false;
    }

    protected int calculateRoundNumber() {
        return this.combineRounds(super.calculateRoundNumber(), this.typeMatcher.calculateRoundNumber());
    }

    protected Map<String, Object> getKeyValueView() {
        Map<String, Object> member = super.getKeyValueView();
        if (this.typeMatcher != ClassExactMatcher.MATCH_ALL) {
            member.put("type", this.typeMatcher);
        }

        return member;
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.typeMatcher, super.hashCode()});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof FuzzyFieldContract && super.equals(obj) ? Objects.equal(this.typeMatcher, ((FuzzyFieldContract) obj).typeMatcher) : true;
        }
    }

    public static class Builder extends AbstractFuzzyMember.Builder<FuzzyFieldContract> {
        public Builder() {
        }

        public Builder requireModifier(int modifier) {
            super.requireModifier(modifier);
            return this;
        }

        public Builder banModifier(int modifier) {
            super.banModifier(modifier);
            return this;
        }

        public Builder requirePublic() {
            super.requirePublic();
            return this;
        }

        public Builder nameRegex(String regex) {
            super.nameRegex(regex);
            return this;
        }

        public Builder nameRegex(Pattern pattern) {
            super.nameRegex(pattern);
            return this;
        }

        public Builder nameExact(String name) {
            super.nameExact(name);
            return this;
        }

        public Builder declaringClassExactType(Class<?> declaringClass) {
            super.declaringClassExactType(declaringClass);
            return this;
        }

        public Builder declaringClassSuperOf(Class<?> declaringClass) {
            super.declaringClassSuperOf(declaringClass);
            return this;
        }

        public Builder declaringClassDerivedOf(Class<?> declaringClass) {
            super.declaringClassDerivedOf(declaringClass);
            return this;
        }

        public Builder declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher) {
            super.declaringClassMatching(classMatcher);
            return this;
        }

        @NotNull
        protected FuzzyFieldContract initialMember() {
            return new FuzzyFieldContract();
        }

        public Builder typeExact(Class<?> type) {
            ((FuzzyFieldContract) this.member).typeMatcher = FuzzyMatchers.matchExact(type);
            return this;
        }

        public Builder typeSuperOf(Class<?> type) {
            ((FuzzyFieldContract) this.member).typeMatcher = FuzzyMatchers.matchSuper(type);
            return this;
        }

        public Builder typeDerivedOf(Class<?> type) {
            ((FuzzyFieldContract) this.member).typeMatcher = FuzzyMatchers.matchDerived(type);
            return this;
        }

        public Builder typeMatches(AbstractFuzzyMatcher<Class<?>> matcher) {
            ((FuzzyFieldContract) this.member).typeMatcher = matcher;
            return this;
        }

        public FuzzyFieldContract build() {
            ((FuzzyFieldContract) this.member).prepareBuild();
            return new FuzzyFieldContract((FuzzyFieldContract) this.member);
        }
    }
}
