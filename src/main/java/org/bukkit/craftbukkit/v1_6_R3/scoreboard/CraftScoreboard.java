package org.bukkit.craftbukkit.v1_6_R3.scoreboard;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CraftScoreboard implements org.bukkit.scoreboard.Scoreboard {
    final net.minecraft.scoreboard.Scoreboard board;
    final Map<String, CraftObjective> objectives = new HashMap<String, CraftObjective>();
    final Map<String, CraftTeam> teams = new HashMap<String, CraftTeam>();

    CraftScoreboard(final net.minecraft.scoreboard.Scoreboard board) {
        this.board = board;

        for (final net.minecraft.scoreboard.ScoreObjective objective : (Iterable<net.minecraft.scoreboard.ScoreObjective>) board.getScoreObjectives()) {
            new CraftObjective(this, objective); // It adds itself to map
        }
        for (final net.minecraft.scoreboard.ScorePlayerTeam team : (Iterable<net.minecraft.scoreboard.ScorePlayerTeam>) board.func_96525_g()) {
            new CraftTeam(this, team); // It adds itself to map
        }
    }

    public CraftObjective registerNewObjective(final String name, final String criteria) throws IllegalArgumentException {
        Validate.notNull(name, "Objective name cannot be null");
        Validate.notNull(criteria, "Criteria cannot be null");
        Validate.isTrue(name.length() <= 16, "The name '" + name + "' is longer than the limit of 16 characters");
        Validate.isTrue(board.getObjective(name) == null, "An objective of name '" + name + "' already exists");

        final CraftCriteria craftCriteria = CraftCriteria.getFromBukkit(criteria);
        final net.minecraft.scoreboard.ScoreObjective objective = board.func_96535_a(name, craftCriteria.criteria);
        return new CraftObjective(this, objective);
    }

    public Objective getObjective(final String name) throws IllegalArgumentException {
        Validate.notNull(name, "Name cannot be null");
        return objectives.get(name);
    }

    public ImmutableSet<Objective> getObjectivesByCriteria(final String criteria) throws IllegalArgumentException {
        Validate.notNull(criteria, "Criteria cannot be null");

        final ImmutableSet.Builder<Objective> objectives = ImmutableSet.builder();
        for (final CraftObjective objective : this.objectives.values()) {
            if (objective.getCriteria().equals(criteria)) {
                objectives.add(objective);
            }
        }
        return objectives.build();
    }

    public ImmutableSet<Objective> getObjectives() {
        return ImmutableSet.copyOf((Collection<? extends Objective>) objectives.values());
    }

    public Objective getObjective(final DisplaySlot slot) throws IllegalArgumentException {
        Validate.notNull(slot, "Display slot cannot be null");
        final net.minecraft.scoreboard.ScoreObjective objective = board.func_96539_a(CraftScoreboardTranslations.fromBukkitSlot(slot));
        if (objective == null) {
            return null;
        }
        return this.objectives.get(objective.getName());
    }

    public ImmutableSet<Score> getScores(final OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "OfflinePlayer cannot be null");

        final ImmutableSet.Builder<Score> scores = ImmutableSet.builder();
        for (final CraftObjective objective : objectives.values()) {
            scores.add(objective.getScore(player));
        }
        return scores.build();
    }

    public void resetScores(final OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "OfflinePlayer cannot be null");

        board.func_96515_c(player.getName());
    }

    public Team getPlayerTeam(final OfflinePlayer player) throws IllegalArgumentException {
        Validate.notNull(player, "OfflinePlayer cannot be null");

        final net.minecraft.scoreboard.ScorePlayerTeam team = board.getPlayersTeam(player.getName());
        return team == null ? null : teams.get(team.func_96661_b());
    }

    public Team getTeam(final String teamName) throws IllegalArgumentException {
        Validate.notNull(teamName, "Team name cannot be null");

        return teams.get(teamName);
    }

    public ImmutableSet<Team> getTeams() {
        return ImmutableSet.copyOf((Collection<? extends Team>) teams.values());
    }

    public Team registerNewTeam(final String name) throws IllegalArgumentException {
        Validate.notNull(name, "Team name cannot be null");
        Validate.isTrue(name.length() <= 16, "Team name '" + name + "' is longer than the limit of 16 characters");
        Validate.isTrue(board.func_96508_e(name) == null, "Team name '" + name + "' is already in use");

        return new CraftTeam(this, board.createTeam(name));
    }

    public ImmutableSet<OfflinePlayer> getPlayers() {
        final ImmutableSet.Builder<OfflinePlayer> players = ImmutableSet.builder();
        for (final Object playerName : board.getObjectiveNames()) {
            players.add(Bukkit.getOfflinePlayer(playerName.toString()));
        }
        return players.build();
    }

    public void clearSlot(final DisplaySlot slot) throws IllegalArgumentException {
        Validate.notNull(slot, "Slot cannot be null");
        board.func_96530_a(CraftScoreboardTranslations.fromBukkitSlot(slot), null);
    }

    // CraftBukkit method
    public net.minecraft.scoreboard.Scoreboard getHandle() {
        return board;
    }
}
