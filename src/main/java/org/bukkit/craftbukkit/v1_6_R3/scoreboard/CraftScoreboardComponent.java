package org.bukkit.craftbukkit.v1_6_R3.scoreboard;

abstract class CraftScoreboardComponent {
    private CraftScoreboard scoreboard;

    CraftScoreboardComponent(final CraftScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    CraftScoreboard checkState() throws IllegalStateException {
        final CraftScoreboard scoreboard = this.scoreboard;
        if (scoreboard == null) {
            throw new IllegalStateException("Unregistered scoreboard component");
        }
        return scoreboard;
    }

    public CraftScoreboard getScoreboard() {
        return scoreboard;
    }

    abstract void unregister() throws IllegalStateException;

    final void setUnregistered() {
        scoreboard = null;
    }
}
