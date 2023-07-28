package org.bukkit.craftbukkit.v1_6_R3.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public abstract class ServerCommandSender implements CommandSender {
    private final PermissibleBase perm = new PermissibleBase(this);

    public ServerCommandSender() {
    }

    public boolean isPermissionSet(final String name) {
        return perm.isPermissionSet(name);
    }

    public boolean isPermissionSet(final Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    public boolean hasPermission(final String name) {
        return perm.hasPermission(name);
    }

    public boolean hasPermission(final Permission perm) {
        return this.perm.hasPermission(perm);
    }

    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(final Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value, final int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    public PermissionAttachment addAttachment(final Plugin plugin, final int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    public void removeAttachment(final PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    public boolean isPlayer() {
        return false;
    }

    public Server getServer() {
        return Bukkit.getServer();
    }
}
