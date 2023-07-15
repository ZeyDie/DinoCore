package org.bukkit.craftbukkit.v1_6_R3.inventory;

import org.bukkit.inventory.MerchantInventory;

public class CraftInventoryMerchant extends CraftInventory implements MerchantInventory {
    public CraftInventoryMerchant(net.minecraft.inventory.InventoryMerchant merchant) {
        super(merchant);
    }
}
