package org.bukkit.craftbukkit.v1_6_R3.scoreboard;


import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

final class CraftObjective extends CraftScoreboardComponent implements Objective {
    private final net.minecraft.scoreboard.ScoreObjective objective;
    private final CraftCriteria criteria;

    CraftObjective(final CraftScoreboard scoreboard, final net.minecraft.scoreboard.ScoreObjective objective) {
        super(scoreboard);
        this.objective = objective;
        this.criteria = CraftCriteria.getFromNMS(objective);

        scoreboard.objectives.put(objective.getName(), this);
    }

    net.minecraft.scoreboard.ScoreObjective getHandle() {
        return objective;
    }

    public String getName() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return objective.getName();
    }

    public String getDisplayName() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return objective.getDisplayName();
    }

    public void setDisplayName(final String displayName) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
        final CraftScoreboard scoreboard = checkState();

        objective.setDisplayName(displayName);
    }

    public String getCriteria() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return criteria.bukkitName;
    }

    public boolean isModifiable() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return !criteria.criteria.isReadOnly();
    }

    public void setDisplaySlot(final DisplaySlot slot) throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();
        final net.minecraft.scoreboard.Scoreboard board = scoreboard.board;
        final net.minecraft.scoreboard.ScoreObjective objective = this.objective;

        for (int i = 0; i < CraftScoreboardTranslations.MAX_DISPLAY_SLOT; i++) {
            if (board.func_96539_a(i) == objective) {
                board.func_96530_a(i, null);
            }
        }
        if (slot != null) {
            final int slotNumber = CraftScoreboardTranslations.fromBukkitSlot(slot);
            board.func_96530_a(slotNumber, getHandle());
        }
    }

    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();
        final net.minecraft.scoreboard.Scoreboard board = scoreboard.board;
        final net.minecraft.scoreboard.ScoreObjective objective = this.objective;

        for (int i = 0; i < CraftScoreboardTranslations.MAX_DISPLAY_SLOT; i++) {
            if (board.func_96539_a(i) == objective) {
                return CraftScoreboardTranslations.toBukkitSlot(i);
            }
        }
        return null;
    }

    public Score getScore(final OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(player, "Player cannot be null");
        final CraftScoreboard scoreboard = checkState();

        return new CraftScore(this, player.getName());
    }

    @Override
    public void unregister() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        scoreboard.objectives.remove(this.getName());
        scoreboard.board.func_96519_k(objective);
        setUnregistered();
    }
}
