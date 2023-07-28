package org.bukkit.craftbukkit.v1_6_R3.scoreboard;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;

import java.util.Set;


final class CraftTeam extends CraftScoreboardComponent implements Team {
    private final net.minecraft.scoreboard.ScorePlayerTeam team;

    CraftTeam(final CraftScoreboard scoreboard, final net.minecraft.scoreboard.ScorePlayerTeam team) {
        super(scoreboard);
        this.team = team;
        scoreboard.teams.put(team.func_96661_b(), this);
    }

    public String getName() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.func_96661_b();
    }

    public String getDisplayName() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.func_96669_c();
    }

    public void setDisplayName(final String displayName) throws IllegalStateException {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
        final CraftScoreboard scoreboard = checkState();

        team.setTeamName(displayName);
    }

    public String getPrefix() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.getColorPrefix();
    }

    public void setPrefix(final String prefix) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(prefix, "Prefix cannot be null");
        Validate.isTrue(prefix.length() <= 32, "Prefix '" + prefix + "' is longer than the limit of 32 characters");
        final CraftScoreboard scoreboard = checkState();

        team.setNamePrefix(prefix);
    }

    public String getSuffix() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.getColorSuffix();
    }

    public void setSuffix(final String suffix) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(suffix, "Suffix cannot be null");
        Validate.isTrue(suffix.length() <= 32, "Suffix '" + suffix + "' is longer than the limit of 32 characters");
        final CraftScoreboard scoreboard = checkState();

        team.setNameSuffix(suffix);
    }

    public boolean allowFriendlyFire() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.getAllowFriendlyFire();
    }

    public void setAllowFriendlyFire(final boolean enabled) throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        team.setAllowFriendlyFire(enabled);
    }

    public boolean canSeeFriendlyInvisibles() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.func_98297_h();
    }

    public void setCanSeeFriendlyInvisibles(final boolean enabled) throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        team.setSeeFriendlyInvisiblesEnabled(enabled);
    }

    public Set<OfflinePlayer> getPlayers() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        final ImmutableSet.Builder<OfflinePlayer> players = ImmutableSet.builder();
        for (final Object o : team.getMembershipCollection()) {
            players.add(Bukkit.getOfflinePlayer(o.toString()));
        }
        return players.build();
    }

    public int getSize() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        return team.getMembershipCollection().size();
    }

    public void addPlayer(final OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        final CraftScoreboard scoreboard = checkState();

        scoreboard.board.addPlayerToTeam(player.getName(), team);
    }

    public boolean removePlayer(final OfflinePlayer player) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        final CraftScoreboard scoreboard = checkState();

        if (!team.getMembershipCollection().contains(player.getName())) {
            return false;
        }

        scoreboard.board.removePlayerFromTeam(player.getName(), team);
        return true;
    }

    public boolean hasPlayer(final OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(player, "OfflinePlayer cannot be null");
        final CraftScoreboard scoreboard = checkState();

        return team.getMembershipCollection().contains(player.getName());
    }

    @Override
    public void unregister() throws IllegalStateException {
        final CraftScoreboard scoreboard = checkState();

        scoreboard.board.func_96511_d(team);
        scoreboard.teams.remove(team.func_96661_b());
        setUnregistered();
    }
}
