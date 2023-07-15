package ru.zoom4ikdan4ik.legacy.core.waitables;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetServerHandler;
import net.minecraft.util.ChatMessageComponent;
import org.bukkit.craftbukkit.v1_6_R3.util.LazyPlayerSet;
import org.bukkit.craftbukkit.v1_6_R3.util.Waitable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public final class WaitableBukkit extends Waitable {
    private final NetServerHandler netServerHandler;
    private final PlayerChatEvent queueEvent;

    public WaitableBukkit(final NetServerHandler netServerHandler, final PlayerChatEvent queueEvent) {
        this.netServerHandler = netServerHandler;
        this.queueEvent = queueEvent;
    }

    @Override
    protected Object evaluate() {
        org.bukkit.Bukkit.getPluginManager().callEvent(queueEvent);

        if (queueEvent.isCancelled()) {
            return null;
        }

        String message = String.format(queueEvent.getFormat(), queueEvent.getPlayer().getDisplayName(), queueEvent.getMessage());
        this.netServerHandler.mcServer.console.sendMessage(message);

        if (((LazyPlayerSet) queueEvent.getRecipients()).isLazy()) {
            for (Object player : this.netServerHandler.mcServer.getConfigurationManager().playerEntityList) {
                ((EntityPlayerMP) player).sendChatToPlayer(ChatMessageComponent.createFromText(message));
            }
        } else {
            for (Player player : queueEvent.getRecipients()) {
                player.sendMessage(message);
            }
        }

        return null;
    }
}
