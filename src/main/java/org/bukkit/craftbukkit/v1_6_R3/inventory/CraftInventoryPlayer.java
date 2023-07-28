package org.bukkit.craftbukkit.v1_6_R3.inventory;


import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryPlayer extends CraftInventory implements org.bukkit.inventory.PlayerInventory, EntityEquipment {
    public CraftInventoryPlayer(final net.minecraft.entity.player.InventoryPlayer inventory) {
        super(inventory);
    }

    @Override
    public net.minecraft.entity.player.InventoryPlayer getInventory() {
        return (net.minecraft.entity.player.InventoryPlayer) inventory;
    }

    @Override
    public int getSize() {
        return getInventory().mainInventory.length; // Cauldron - Galacticraft and Aether extend equipped item slots so we need to check the main inventory array directly
    }

    public ItemStack getItemInHand() {
        return CraftItemStack.asCraftMirror(getInventory().getCurrentItem());
    }

    public void setItemInHand(final ItemStack stack) {
        setItem(getHeldItemSlot(), stack);
    }

    public int getHeldItemSlot() {
        return getInventory().currentItem;
    }

    public void setHeldItemSlot(final int slot) {
        Validate.isTrue(slot >= 0 && slot < net.minecraft.entity.player.InventoryPlayer.getHotbarSize(), "Slot is not between 0 and 8 inclusive");
        this.getInventory().currentItem = slot;
        ((CraftPlayer) this.getHolder()).getHandle().playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet16BlockItemSwitch(slot));
    }

    public ItemStack getHelmet() {
        return getItem(getSize() + 3);
    }

    public ItemStack getChestplate() {
        return getItem(getSize() + 2);
    }

    public ItemStack getLeggings() {
        return getItem(getSize() + 1);
    }

    public ItemStack getBoots() {
        return getItem(getSize() + 0);
    }

    public void setHelmet(final ItemStack helmet) {
        setItem(getSize() + 3, helmet);
    }

    public void setChestplate(final ItemStack chestplate) {
        setItem(getSize() + 2, chestplate);
    }

    public void setLeggings(final ItemStack leggings) {
        setItem(getSize() + 1, leggings);
    }

    public void setBoots(final ItemStack boots) {
        setItem(getSize() + 0, boots);
    }

    public ItemStack[] getArmorContents() {
        final net.minecraft.item.ItemStack[] mcItems = getInventory().getArmorContents();
        final ItemStack[] ret = new ItemStack[mcItems.length];

        for (int i = 0; i < mcItems.length; i++) {
            ret[i] = CraftItemStack.asCraftMirror(mcItems[i]);
        }
        return ret;
    }

    public void setArmorContents(ItemStack[] items) {
        ItemStack[] items1 = items;
        int cnt = getSize();

        if (items1 == null) {
            items1 = new ItemStack[4];
        }
        for (final ItemStack item : items1) {
            if (item == null || item.getTypeId() == 0) {
                clear(cnt++);
            } else {
                setItem(cnt++, item);
            }
        }
    }

    public int clear(final int id, final int data) {
        int count = 0;
        final ItemStack[] items = getContents();
        final ItemStack[] armor = getArmorContents();
        int armorSlot = getSize();

        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (item == null) continue;
            if (id > -1 && item.getTypeId() != id) continue;
            if (data > -1 && item.getData().getData() != data) continue;

            count += item.getAmount();
            setItem(i, null);
        }

        for (final ItemStack item : armor) {
            if (item == null) continue;
            if (id > -1 && item.getTypeId() != id) continue;
            if (data > -1 && item.getData().getData() != data) continue;

            count += item.getAmount();
            setItem(armorSlot++, null);
        }
        return count;
    }

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) inventory.getOwner();
    }

    public float getItemInHandDropChance() {
        return 1;
    }

    public void setItemInHandDropChance(final float chance) {
        throw new UnsupportedOperationException();
    }

    public float getHelmetDropChance() {
        return 1;
    }

    public void setHelmetDropChance(final float chance) {
        throw new UnsupportedOperationException();
    }

    public float getChestplateDropChance() {
        return 1;
    }

    public void setChestplateDropChance(final float chance) {
        throw new UnsupportedOperationException();
    }

    public float getLeggingsDropChance() {
        return 1;
    }

    public void setLeggingsDropChance(final float chance) {
        throw new UnsupportedOperationException();
    }

    public float getBootsDropChance() {
        return 1;
    }

    public void setBootsDropChance(final float chance) {
        throw new UnsupportedOperationException();
    }
}
