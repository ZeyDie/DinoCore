package org.bukkit.craftbukkit.v1_6_R3.scoreboard;


import com.google.common.collect.ImmutableBiMap;
import org.bukkit.scoreboard.DisplaySlot;

class CraftScoreboardTranslations {
    static final int MAX_DISPLAY_SLOT = 3;
    static ImmutableBiMap<DisplaySlot, String> SLOTS = ImmutableBiMap.of(
            DisplaySlot.BELOW_NAME, "belowName",
            DisplaySlot.PLAYER_LIST, "list",
            DisplaySlot.SIDEBAR, "sidebar");

    private CraftScoreboardTranslations() {}

    static DisplaySlot toBukkitSlot(final int i) {
        return SLOTS.inverse().get(net.minecraft.scoreboard.Scoreboard.getObjectiveDisplaySlot(i));
    }

    static int fromBukkitSlot(final DisplaySlot slot) {
        return net.minecraft.scoreboard.Scoreboard.getObjectiveDisplaySlotNumber(SLOTS.get(slot));
    }

}
