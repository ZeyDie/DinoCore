package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import java.util.List;

// CraftBukkit start
// CraftBukkit end

public class AnimalChest extends InventoryBasic
{
    public AnimalChest(String par1Str, int par2)
    {
        super(par1Str, false, par2);
    }

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private EntityHorse horse;
    private int maxStack = MAX_STACK;

    public AnimalChest(String s, int i, EntityHorse horse)
    {
        this(s, i);
        this.horse = horse;
    }

    @Override
    public ItemStack[] getContents()
    {
        return this.inventoryContents;
    }

    @Override
    public void onOpen(CraftHumanEntity who)
    {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who)
    {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers()
    {
        return transaction;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner()
    {
        return (org.bukkit.entity.Horse) this.horse.getBukkitEntity();
    }

    @Override
    public void setMaxStackSize(int size)
    {
        maxStack = size;
    }

    @Override

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return maxStack;
    }
    // CraftBukkit end

    @SideOnly(Side.CLIENT)
    public AnimalChest(String par1Str, boolean par2, int par3)
    {
        super(par1Str, par2, par3);
    }
}
