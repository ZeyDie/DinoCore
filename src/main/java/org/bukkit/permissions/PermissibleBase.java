package org.bukkit.permissions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

/**
 * Base Permissible for use in any Permissible object via proxy or extension
 */
public class PermissibleBase implements Permissible {
    private ServerOperator opable = null;
    private Permissible parent = this;
    private final List<PermissionAttachment> attachments = new LinkedList<PermissionAttachment>();
    private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<String, PermissionAttachmentInfo>();

    public PermissibleBase(final ServerOperator opable) {
        this.opable = opable;

        if (opable instanceof Permissible) {
            this.parent = (Permissible) opable;
        }

        recalculatePermissions();
    }

    public boolean isOp() {
        if (opable == null) {
            return false;
        } else {
            return opable.isOp();
        }
    }

    public void setOp(final boolean value) {
        if (opable == null) {
            throw new UnsupportedOperationException("Cannot change op value as no ServerOperator is set");
        } else {
            opable.setOp(value);
        }
    }

    public boolean isPermissionSet(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        }

        return permissions.containsKey(name.toLowerCase());
    }

    public boolean isPermissionSet(final Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }

        return isPermissionSet(perm.getName());
    }

    public boolean hasPermission(final String inName) {
        if (inName == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        }

        final String name = inName.toLowerCase();

        if (isPermissionSet(name)) {
            return permissions.get(name).getValue();
        } else {
            final Permission perm = Bukkit.getServer().getPluginManager().getPermission(name);

            if (perm != null) {
                return perm.getDefault().getValue(isOp());
            } else {
                return Permission.DEFAULT_PERMISSION.getValue(isOp());
            }
        }
    }

    public boolean hasPermission(final Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }

        final String name = perm.getName().toLowerCase();

        if (isPermissionSet(name)) {
            return permissions.get(name).getValue();
        }
        return perm.getDefault().getValue(isOp());
    }

    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        final PermissionAttachment result = addAttachment(plugin);
        result.setPermission(name, value);

        recalculatePermissions();

        return result;
    }

    public PermissionAttachment addAttachment(final Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        final PermissionAttachment result = new PermissionAttachment(plugin, parent);

        attachments.add(result);
        recalculatePermissions();

        return result;
    }

    public void removeAttachment(final PermissionAttachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }

        if (attachments.contains(attachment)) {
            attachments.remove(attachment);
            final PermissionRemovedExecutor ex = attachment.getRemovalCallback();

            if (ex != null) {
                ex.attachmentRemoved(attachment);
            }

            recalculatePermissions();
        } else {
            throw new IllegalArgumentException("Given attachment is not part of Permissible object " + parent);
        }
    }

    public void recalculatePermissions() {
        clearPermissions();
        final Set<Permission> defaults = Bukkit.getServer().getPluginManager().getDefaultPermissions(isOp());
        Bukkit.getServer().getPluginManager().subscribeToDefaultPerms(isOp(), parent);

        for (final Permission perm : defaults) {
            final String name = perm.getName().toLowerCase();
            permissions.put(name, new PermissionAttachmentInfo(parent, name, null, true));
            Bukkit.getServer().getPluginManager().subscribeToPermission(name, parent);
            calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (final PermissionAttachment attachment : attachments) {
            calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
    }

    public synchronized void clearPermissions() {
        final Set<String> perms = permissions.keySet();

        for (final String name : perms) {
            Bukkit.getServer().getPluginManager().unsubscribeFromPermission(name, parent);
        }

        Bukkit.getServer().getPluginManager().unsubscribeFromDefaultPerms(false, parent);
        Bukkit.getServer().getPluginManager().unsubscribeFromDefaultPerms(true, parent);

        permissions.clear();
    }

    private void calculateChildPermissions(final Map<String, Boolean> children, final boolean invert, final PermissionAttachment attachment) {
        final Set<String> keys = children.keySet();

        for (final String name : keys) {
            final Permission perm = Bukkit.getServer().getPluginManager().getPermission(name);
            final boolean value = children.get(name) ^ invert;
            final String lname = name.toLowerCase();

            permissions.put(lname, new PermissionAttachmentInfo(parent, lname, attachment, value));
            Bukkit.getServer().getPluginManager().subscribeToPermission(name, parent);

            if (perm != null) {
                calculateChildPermissions(perm.getChildren(), !value, attachment);
            }
        }
    }

    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value, final int ticks) {
        if (name == null) {
            throw new IllegalArgumentException("Permission name cannot be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        final PermissionAttachment result = addAttachment(plugin, ticks);

        if (result != null) {
            result.setPermission(name, value);
        }

        return result;
    }

    public PermissionAttachment addAttachment(final Plugin plugin, final int ticks) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        } else if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is disabled");
        }

        final PermissionAttachment result = addAttachment(plugin);

        if (Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RemoveAttachmentRunnable(result), ticks) == -1) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Could not add PermissionAttachment to " + parent + " for plugin " + plugin.getDescription().getFullName() + ": Scheduler returned -1");
            result.remove();
            return null;
        } else {
            return result;
        }
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<PermissionAttachmentInfo>(permissions.values());
    }

    private class RemoveAttachmentRunnable implements Runnable {
        private PermissionAttachment attachment;

        public RemoveAttachmentRunnable(final PermissionAttachment attachment) {
            this.attachment = attachment;
        }

        public void run() {
            attachment.remove();
        }
    }
}
