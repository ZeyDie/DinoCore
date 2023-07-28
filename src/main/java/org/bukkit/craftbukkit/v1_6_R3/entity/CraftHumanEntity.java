package org.bukkit.craftbukkit.v1_6_R3.entity;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class CraftHumanEntity extends CraftLivingEntity implements HumanEntity {
    private CraftInventoryPlayer inventory;
    private final CraftInventory enderChest;
    protected final PermissibleBase perm = new PermissibleBase(this);
    private boolean op;
    private GameMode mode;

    public CraftHumanEntity(final CraftServer server, final net.minecraft.entity.player.EntityPlayer entity) {
        super(server, entity);
        mode = server.getDefaultGameMode();
        this.inventory = new CraftInventoryPlayer(entity.inventory);
        enderChest = new CraftInventory(entity.getInventoryEnderChest());
    }

    public String getName() {
        return getHandle().getCommandSenderName();
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public EntityEquipment getEquipment() {
        return inventory;
    }

    public Inventory getEnderChest() {
        return enderChest;
    }

    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    public void setItemInHand(final ItemStack item) {
        getInventory().setItemInHand(item);
    }

    public ItemStack getItemOnCursor() {
        return CraftItemStack.asCraftMirror(getHandle().inventory.getItemStack());
    }

    public void setItemOnCursor(final ItemStack item) {
        final net.minecraft.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        getHandle().inventory.setItemStack(stack);
        if (this instanceof CraftPlayer) {
            ((net.minecraft.entity.player.EntityPlayerMP) getHandle()).updateHeldItem(); // Send set slot for cursor
        }
    }

    public boolean isSleeping() {
        return getHandle().sleeping;
    }

    public int getSleepTicks() {
        return getHandle().sleepTimer;
    }

    public boolean isOp() {
        return op;
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

    public void setOp(final boolean value) {
        this.op = value;
        perm.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    public GameMode getGameMode() {
        return mode;
    }

    public void setGameMode(final GameMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        this.mode = mode;
    }

    @Override
    public net.minecraft.entity.player.EntityPlayer getHandle() {
        return (net.minecraft.entity.player.EntityPlayer) entity;
    }

    public void setHandle(final net.minecraft.entity.player.EntityPlayer entity) {
        super.setHandle(entity);
        this.inventory = new CraftInventoryPlayer(entity.inventory);
    }

    @Override
    public String toString() {
        return "CraftHumanEntity{" + "id=" + getEntityId() + "name=" + getName() + '}';
    }

    public InventoryView getOpenInventory() {
        return getHandle().openContainer.getBukkitView();
    }

    public InventoryView openInventory(final Inventory inventory) {
        if(!(getHandle() instanceof net.minecraft.entity.player.EntityPlayerMP)) return null;
        final net.minecraft.entity.player.EntityPlayerMP player = (net.minecraft.entity.player.EntityPlayerMP) getHandle();
        final InventoryType type = inventory.getType();
        final net.minecraft.inventory.Container formerContainer = getHandle().openContainer;
        // TODO: Should we check that it really IS a CraftInventory first?
        final CraftInventory craftinv = (CraftInventory) inventory;
        switch(type) {
        case PLAYER:
        case CHEST:
        case ENDER_CHEST:
            getHandle().displayGUIChest(craftinv.getInventory());
            break;
        case DISPENSER:
            if (craftinv.getInventory() instanceof net.minecraft.tileentity.TileEntityDispenser) {
                getHandle().displayGUIDispenser((net.minecraft.tileentity.TileEntityDispenser) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, 3);
            }
            break;
        case FURNACE:
            if (craftinv.getInventory() instanceof net.minecraft.tileentity.TileEntityFurnace) {
                getHandle().displayGUIFurnace((net.minecraft.tileentity.TileEntityFurnace) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, 2);
            }
            break;
        case WORKBENCH:
            openCustomInventory(inventory, player, 1);
            break;
        case BREWING:
            if (craftinv.getInventory() instanceof net.minecraft.tileentity.TileEntityBrewingStand) {
                getHandle().displayGUIBrewingStand((net.minecraft.tileentity.TileEntityBrewingStand) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, 5);
            }
            break;
        case ENCHANTING:
            openCustomInventory(inventory, player, 4);
            break;
        case HOPPER:
            if (craftinv.getInventory() instanceof net.minecraft.tileentity.TileEntityHopper) {
                getHandle().displayGUIHopper((net.minecraft.tileentity.TileEntityHopper) craftinv.getInventory());
            } else if (craftinv.getInventory() instanceof net.minecraft.entity.item.EntityMinecartHopper) {
                getHandle().displayGUIHopperMinecart((net.minecraft.entity.item.EntityMinecartHopper) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, 9);
            }
            break;
        case CREATIVE:
        case CRAFTING:
            throw new IllegalArgumentException("Can't open a " + type + " inventory!");
        }
        if (getHandle().openContainer == formerContainer) {
            return null;
        }
        getHandle().openContainer.checkReachable = false;
        return getHandle().openContainer.getBukkitView();
    }

    private void openCustomInventory(final Inventory inventory, final net.minecraft.entity.player.EntityPlayerMP player, final int windowType) {
        if (player.playerNetServerHandler == null) return;
        net.minecraft.inventory.Container container = new CraftContainer(inventory, this, player.nextContainerCounter());

        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if(container == null) return;

        final String title = container.getBukkitView().getTitle();
        final int size = container.getBukkitView().getTopInventory().getSize();

        player.playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet100OpenWindow(container.windowId, windowType, title, size, true));
        getHandle().openContainer = container;
        getHandle().openContainer.addCraftingToCrafters(player);
    }

    public InventoryView openWorkbench(Location location, final boolean force) {
        Location location1 = location;
        if (!force) {
            final Block block = location1.getBlock();
            if (block.getType() != Material.WORKBENCH) {
                return null;
            }
        }
        if (location1 == null) {
            location1 = getLocation();
        }
        getHandle().displayGUIWorkbench(location1.getBlockX(), location1.getBlockY(), location1.getBlockZ());
        if (force) {
            getHandle().openContainer.checkReachable = false;
        }
        return getHandle().openContainer.getBukkitView();
    }

    public InventoryView openEnchanting(Location location, final boolean force) {
        Location location1 = location;
        if (!force) {
            final Block block = location1.getBlock();
            if (block.getType() != Material.ENCHANTMENT_TABLE) {
                return null;
            }
        }
        if (location1 == null) {
            location1 = getLocation();
        }
        getHandle().displayGUIEnchantment(location1.getBlockX(), location1.getBlockY(), location1.getBlockZ(), null);
        if (force) {
            getHandle().openContainer.checkReachable = false;
        }
        return getHandle().openContainer.getBukkitView();
    }

    public void openInventory(final InventoryView inventory) {
        if (!(getHandle() instanceof net.minecraft.entity.player.EntityPlayerMP)) return; // TODO: NPC support?
        if (((net.minecraft.entity.player.EntityPlayerMP) getHandle()).playerNetServerHandler == null) return;
        if (getHandle().openContainer != getHandle().inventoryContainer) {
            // fire INVENTORY_CLOSE if one already open
            ((net.minecraft.entity.player.EntityPlayerMP)getHandle()).playerNetServerHandler.handleCloseWindow(new net.minecraft.network.packet.Packet101CloseWindow(getHandle().openContainer.windowId));
        }
        final net.minecraft.entity.player.EntityPlayerMP player = (net.minecraft.entity.player.EntityPlayerMP) getHandle();
        net.minecraft.inventory.Container container;
        if (inventory instanceof CraftInventoryView) {
            container = ((CraftInventoryView) inventory).getHandle();
        } else {
            container = new CraftContainer(inventory, player.nextContainerCounter());
        }

        // Trigger an INVENTORY_OPEN event
        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if (container == null) {
            return;
        }

        // Now open the window
        final InventoryType type = inventory.getType();
        final int windowType = CraftContainer.getNotchInventoryType(type);
        final String title = inventory.getTitle();
        final int size = inventory.getTopInventory().getSize();
        player.playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet100OpenWindow(container.windowId, windowType, title, size, false));
        player.openContainer = container;
        player.openContainer.addCraftingToCrafters(player);
    }

    public void closeInventory() {
        getHandle().closeScreen();
    }

    public boolean isBlocking() {
        return getHandle().isBlocking();
    }

    public boolean setWindowProperty(final InventoryView.Property prop, final int value) {
        return false;
    }

    public int getExpToLevel() {
        return getHandle().xpBarCap();
    }
}
