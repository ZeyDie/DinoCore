package net.minecraft.enchantment;

import net.minecraft.item.*;

public enum EnumEnchantmentType
{
    all,
    armor,
    armor_feet,
    armor_legs,
    armor_torso,
    armor_head,
    weapon,
    digger,
    bow;

    /**
     * Return true if the item passed can be enchanted by a enchantment of this type.
     */
    public boolean canEnchantItem(final Item par1Item)
    {
        if (this == all)
        {
            return true;
        }
        else if (par1Item instanceof ItemArmor)
        {
            if (this == armor)
            {
                return true;
            }
            else
            {
                final ItemArmor itemarmor = (ItemArmor)par1Item;
                return itemarmor.armorType == 0 ? this == armor_head : (itemarmor.armorType == 2 ? this == armor_legs : (itemarmor.armorType == 1 ? this == armor_torso : (itemarmor.armorType == 3 ? this == armor_feet : false)));
            }
        }
        else
        {
            return par1Item instanceof ItemSword ? this == weapon : (par1Item instanceof ItemTool ? this == digger : (par1Item instanceof ItemBow ? this == bow : false));
        }
    }
}
