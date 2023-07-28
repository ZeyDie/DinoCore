package org.bukkit.craftbukkit.v1_6_R3.scoreboard;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

final class CraftCriteria {
    static final Map<String, CraftCriteria> DEFAULTS;
    static final CraftCriteria DUMMY;

    static {
        final ImmutableMap.Builder<String, CraftCriteria> defaults = ImmutableMap.builder();

        for (final Map.Entry<?, ?> entry : ((Map<?,?> ) net.minecraft.scoreboard.ScoreObjectiveCriteria.field_96643_a).entrySet()) {
            final String name = entry.getKey().toString();
            final net.minecraft.scoreboard.ScoreObjectiveCriteria criteria = (net.minecraft.scoreboard.ScoreObjectiveCriteria) entry.getValue();
            if (!criteria.func_96636_a().equals(name)) {
                throw new AssertionError("Unexpected entry " + name + " to criteria " + criteria + "(" + criteria.func_96636_a() + ")");
            }

            defaults.put(name, new CraftCriteria(criteria));
        }

        DEFAULTS = defaults.build();
        DUMMY = DEFAULTS.get("dummy");
    }

    final net.minecraft.scoreboard.ScoreObjectiveCriteria criteria;
    final String bukkitName;

    private CraftCriteria(final String bukkitName) {
        this.bukkitName = bukkitName;
        this.criteria = DUMMY.criteria;
    }

    private CraftCriteria(final net.minecraft.scoreboard.ScoreObjectiveCriteria criteria) {
        this.criteria = criteria;
        this.bukkitName = criteria.func_96636_a();
    }

    static CraftCriteria getFromNMS(final net.minecraft.scoreboard.ScoreObjective objective) {
        return DEFAULTS.get(objective.getCriteria().func_96636_a());
    }

    static CraftCriteria getFromBukkit(final String name) {
        final CraftCriteria criteria = DEFAULTS.get(name);
        if (criteria != null) {
            return criteria;
        }
        return new CraftCriteria(name);
    }

    @Override
    public boolean equals(final Object that) {
        if (!(that instanceof CraftCriteria)) {
            return false;
        }
        return ((CraftCriteria) that).bukkitName.equals(this.bukkitName);
    }

    @Override
    public int hashCode() {
        return this.bukkitName.hashCode() ^ CraftCriteria.class.hashCode();
    }
}
