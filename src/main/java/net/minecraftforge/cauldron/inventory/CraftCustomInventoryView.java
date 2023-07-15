package net.minecraftforge.cauldron.inventory;

import net.minecraft.inventory.Container;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class CraftCustomInventoryView extends CraftInventoryView {

    public CraftCustomInventoryView(HumanEntity player, Inventory viewing,
            Container container) {
        super(player, viewing, container);
        // TODO Auto-generated constructor stub
    }

}
