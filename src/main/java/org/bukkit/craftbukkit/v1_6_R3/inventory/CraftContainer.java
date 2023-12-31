package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;


public class CraftContainer extends net.minecraft.inventory.Container {
    private final InventoryView view;
    private InventoryType cachedType;
    private String cachedTitle;
    private final int cachedSize;

    public CraftContainer(final InventoryView view, final int id) {
        this.view = view;
        this.windowId = id;
        // TODO: Do we need to check that it really is a CraftInventory?
        final net.minecraft.inventory.IInventory top = ((CraftInventory)view.getTopInventory()).getInventory();
        final net.minecraft.inventory.IInventory bottom = ((CraftInventory)view.getBottomInventory()).getInventory();
        cachedType = view.getType();
        cachedTitle = view.getTitle();
        cachedSize = getSize();
        setupSlots(top, bottom);
    }

    public CraftContainer(final Inventory inventory, final HumanEntity player, final int id) {
        this(new InventoryView() {
            @Override
            public Inventory getTopInventory() {
                return inventory;
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
                return inventory.getType();
            }
        }, id);
    }

    @Override
    public InventoryView getBukkitView() {
        return view;
    }

    private int getSize() {
        return view.getTopInventory().getSize();
    }

    @Override

    /**
     * NotUsing because adding a player twice is an error
     */
    public boolean isPlayerNotUsingContainer(final net.minecraft.entity.player.EntityPlayer entityhuman) {
        if (cachedType == view.getType() && cachedSize == getSize() && cachedTitle.equals(view.getTitle())) {
            return true;
        }
        // If the window type has changed for some reason, update the player
        // This method will be called every tick or something, so it's
        // as good a place as any to put something like this.
        final boolean typeChanged = (cachedType != view.getType());
        cachedType = view.getType();
        cachedTitle = view.getTitle();
        if (view.getPlayer() instanceof CraftPlayer) {
            final CraftPlayer player = (CraftPlayer) view.getPlayer();
            final int type = getNotchInventoryType(cachedType);
            final net.minecraft.inventory.IInventory top = ((CraftInventory)view.getTopInventory()).getInventory();
            final net.minecraft.inventory.IInventory bottom = ((CraftInventory)view.getBottomInventory()).getInventory();
            this.inventoryItemStacks.clear();
            this.inventorySlots.clear();
            if (typeChanged) {
                setupSlots(top, bottom);
            }
            final int size = getSize();
            player.getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet100OpenWindow(this.windowId, type, cachedTitle, size, true));
            player.updateInventory();
        }
        return true;
    }

    public static int getNotchInventoryType(final InventoryType type) {
        final int typeID;
        switch(type) {
        case WORKBENCH:
            typeID = 1;
            break;
        case FURNACE:
            typeID = 2;
            break;
        case DISPENSER:
            typeID = 3;
            break;
        case ENCHANTING:
            typeID = 4;
            break;
        case BREWING:
            typeID = 5;
            break;
        case BEACON:
            typeID = 7;
            break;
        case ANVIL:
            typeID = 8;
            break;
        case HOPPER:
            typeID = 9;
            break;
        default:
            typeID = 0;
            break;
        }
        return typeID;
    }

    private void setupSlots(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        switch(cachedType) {
        case CREATIVE:
            break; // TODO: This should be an error?
        case PLAYER:
        case CHEST:
            setupChest(top, bottom);
            break;
        case DISPENSER:
            setupDispenser(top, bottom);
            break;
        case FURNACE:
            setupFurnace(top, bottom);
            break;
        case CRAFTING: // TODO: This should be an error?
        case WORKBENCH:
            setupWorkbench(top, bottom);
            break;
        case ENCHANTING:
            setupEnchanting(top, bottom);
            break;
        case BREWING:
            setupBrewing(top, bottom);
            break;
        case HOPPER:
            setupHopper(top, bottom);
            break;
        }
    }

    private void setupChest(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        final int rows = top.getSizeInventory() / 9;
        int row;
        int col;
        // This code copied from ContainerChest
        final int i = (rows - 4) * 18;
        for (row = 0; row < rows; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(top, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + i));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col, 8 + col * 18, 161 + i));
        }
        // End copy from ContainerChest
    }

    private void setupWorkbench(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        // This code copied from ContainerWorkbench
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 0, 124, 35));

        int row;
        int col;

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 3; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 1 + col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col, 8 + col * 18, 142));
        }
        // End copy from ContainerWorkbench
    }

    private void setupFurnace(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        // This code copied from ContainerFurnace
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 0, 56, 17));
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 1, 56, 53));
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 2, 116, 35));

        int row;
        int col;

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col, 8 + col * 18, 142));
        }
        // End copy from ContainerFurnace
    }

    private void setupDispenser(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        // This code copied from ContainerDispenser
        int row;
        int col;

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 3; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(top, col + row * 3, 61 + col * 18, 17 + row * 18));
            }
        }

        for (row = 0; row < 3; ++row) {
            for (col = 0; col < 9; ++col) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, col, 8 + col * 18, 142));
        }
        // End copy from ContainerDispenser
    }

    private void setupEnchanting(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        // This code copied from ContainerEnchantTable
        this.addSlotToContainer((new net.minecraft.inventory.Slot(top, 0, 25, 47)));

        int row;

        for (row = 0; row < 3; ++row) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, i1 + row * 9 + 9, 8 + i1 * 18, 84 + row * 18));
            }
        }

        for (row = 0; row < 9; ++row) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, row, 8 + row * 18, 142));
        }
        // End copy from ContainerEnchantTable
    }

    private void setupBrewing(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        // This code copied from ContainerBrewingStand
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 0, 56, 46));
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 1, 79, 53));
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 2, 102, 46));
        this.addSlotToContainer(new net.minecraft.inventory.Slot(top, 3, 79, 17));

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, i, 8 + i * 18, 142));
        }
        // End copy from ContainerBrewingStand
    }

    private void setupHopper(final net.minecraft.inventory.IInventory top, final net.minecraft.inventory.IInventory bottom) {
        // This code copied from ContainerHopper
        final byte b0 = 51;

        int i;

        for (i = 0; i < top.getSizeInventory(); ++i) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(top, i, 44 + i * 18, 20));
        }

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new net.minecraft.inventory.Slot(bottom, i, 8 + i * 18, 58 + b0));
        }
        // End copy from ContainerHopper
    }

    public boolean canInteractWith(final net.minecraft.entity.player.EntityPlayer entity) {
        return true;
    }
}
