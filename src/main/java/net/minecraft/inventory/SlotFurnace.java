package net.minecraft.inventory;

// CraftBukkit start

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceExtractEvent;
// CraftBukkit end

public class SlotFurnace extends Slot
{
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;
    private int field_75228_b;

    public SlotFurnace(final EntityPlayer par1EntityPlayer, final IInventory par2IInventory, final int par3, final int par4, final int par5)
    {
        super(par2IInventory, par3, par4, par5);
        this.thePlayer = par1EntityPlayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(final int par1)
    {
        if (this.getHasStack())
        {
            this.field_75228_b += Math.min(par1, this.getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack)
    {
        this.onCrafting(par2ItemStack);
        super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(final ItemStack par1ItemStack, final int par2)
    {
        this.field_75228_b += par2;
        this.onCrafting(par1ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(final ItemStack par1ItemStack)
    {
        if (par1ItemStack == null) return; // Cauldron - fix "random crash" when picking up items from furnace slots, #1222
        par1ItemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75228_b);

        if (!this.thePlayer.worldObj.isRemote)
        {
            int i = this.field_75228_b;
            final float f = FurnaceRecipes.smelting().getExperience(par1ItemStack);
            int j;

            if (f == 0.0F)
            {
                i = 0;
            }
            else if (f < 1.0F)
            {
                j = MathHelper.floor_float((float)i * f);

                if (j < MathHelper.ceiling_float_int((float)i * f) && (float)Math.random() < (float)i * f - (float)j)
                {
                    ++j;
                }

                i = j;
            }

            if (this.inventory instanceof TileEntityFurnace) { // Cauldron - fix IC2 crash
            // CraftBukkit start
            final Player player = (Player) thePlayer.getBukkitEntity();
            final TileEntityFurnace furnace = ((TileEntityFurnace) this.inventory);
            final org.bukkit.block.Block block = thePlayer.worldObj.getWorld().getBlockAt(furnace.xCoord, furnace.yCoord, furnace.zCoord);
            final FurnaceExtractEvent event = new FurnaceExtractEvent(player, block, org.bukkit.Material.getMaterial(par1ItemStack.itemID), par1ItemStack.stackSize, i);
            thePlayer.worldObj.getServer().getPluginManager().callEvent(event);
            i = event.getExpToDrop();
            // CraftBukkit end
            } // Cauldron

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, j));
            }
        }

        this.field_75228_b = 0;

        GameRegistry.onItemSmelted(thePlayer, par1ItemStack);

        if (par1ItemStack.itemID == Item.ingotIron.itemID)
        {
            this.thePlayer.addStat(AchievementList.acquireIron, 1);
        }

        if (par1ItemStack.itemID == Item.fishCooked.itemID)
        {
            this.thePlayer.addStat(AchievementList.cookFish, 1);
        }
    }
}
