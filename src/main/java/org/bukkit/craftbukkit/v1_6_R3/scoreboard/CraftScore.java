package org.bukkit.craftbukkit.v1_6_R3.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Map;

/**
 * TL;DR: This class is special and lazily grabs a handle...
 * ...because a handle is a full fledged (I think permanent) hashMap for the associated name.
 * <p>
 * Also, as an added perk, a CraftScore will (intentionally) stay a valid reference so long as objective is valid.
 */
final class CraftScore implements Score {
    private final String playerName;
    private final CraftObjective objective;

    CraftScore(final CraftObjective objective, final String playerName) {
        this.objective = objective;
        this.playerName = playerName;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(playerName);
    }

    public Objective getObjective() {
        return objective;
    }

    public int getScore() throws IllegalStateException {
        final net.minecraft.scoreboard.Scoreboard board = objective.checkState().board;

        if (board.getObjectiveNames().contains(playerName)) { // Lazy
            final Map<String, net.minecraft.scoreboard.Score> scores = board.func_96510_d(playerName);
            final net.minecraft.scoreboard.Score score = scores.get(objective.getHandle());
            if (score != null) { // Lazy
                return score.getScorePoints();
            }
        }
        return 0; // Lazy
    }

    public void setScore(final int score) throws IllegalStateException {
        objective.checkState().board.func_96529_a(playerName, objective.getHandle()).func_96647_c(score);
    }

    public CraftScoreboard getScoreboard() {
        return objective.getScoreboard();
    }
}
