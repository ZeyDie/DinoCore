package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;


public class CraftInventoryView extends InventoryView {
    private final net.minecraft.inventory.Container container;
    private final CraftHumanEntity player;
    private final CraftInventory viewing;

    public CraftInventoryView(final HumanEntity player, final Inventory viewing, final net.minecraft.inventory.Container container) {
        // TODO: Should we make sure it really IS a CraftHumanEntity first? And a CraftInventory?
        this.player = (CraftHumanEntity) player;
        this.viewing = (CraftInventory) viewing;
        this.container = container;
    }

    @Override
    public Inventory getTopInventory() {
        return viewing;
    }

    @Override
    public Inventory getBottomInventory() {
        return player.getInventory();
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        final InventoryType type = viewing.getType();
        if (type == InventoryType.CRAFTING && player.getGameMode() == GameMode.CREATIVE) {
            return InventoryType.CREATIVE;
        }
        return type;
    }

    @Override
    public void setItem(final int slot, final ItemStack item) {
        final net.minecraft.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        if (slot != -999) {
            container.getSlot(slot).putStack(stack);
        } else {
            player.getHandle().dropPlayerItem(stack);
        }
    }

    @Override
    public ItemStack getItem(final int slot) {
        if (slot == -999) {
            return null;
        }
        return CraftItemStack.asCraftMirror(container.getSlot(slot).getStack());
    }

    public boolean isInTop(final int rawSlot) {
        return rawSlot < viewing.getSize();
    }

    public net.minecraft.inventory.Container getHandle() {
        return container;
    }

    public static SlotType getSlotType(final InventoryView inventory, final int slot) {
        SlotType type = SlotType.CONTAINER;
        if (inventory == null) return type; // Cauldron - modded inventories with no Bukkit wrapper
        if (slot >= 0 && slot < inventory.getTopInventory().getSize()) {
            switch(inventory.getType()) {
            case FURNACE:
                if (slot == 2) {
                    type = SlotType.RESULT;
                } else if(slot == 1) {
                    type = SlotType.FUEL;
                }
                break;
            case BREWING:
                if (slot == 3) {
                    type = SlotType.FUEL;
                } else {
                    type = SlotType.CRAFTING;
                }
                break;
            case ENCHANTING:
                type = SlotType.CRAFTING;
                break;
            case WORKBENCH:
            case CRAFTING:
                if (slot == 0) {
                    type = SlotType.RESULT;
                } else {
                    type = SlotType.CRAFTING;
                }
                break;
            case MERCHANT:
                if (slot == 2) {
                    type = SlotType.RESULT;
                } else {
                    type = SlotType.CRAFTING;
                }
                break;
            case BEACON:
                type = SlotType.CRAFTING;
                break;
            case ANVIL:
                if (slot == 2) {
                    type = SlotType.RESULT;
                } else {
                    type = SlotType.CRAFTING;
                }
                break;
            default:
                // Nothing to do, it's a CONTAINER slot
            }
        } else {
            if (slot == -999) {
                type = SlotType.OUTSIDE;
            } else if (inventory.getType() == InventoryType.CRAFTING && slot < 9) {
                type = SlotType.ARMOR;
            } else if (slot >= (inventory.countSlots() - 9)) {
                type = SlotType.QUICKBAR;
            }
        }
        return type;
    }
}
