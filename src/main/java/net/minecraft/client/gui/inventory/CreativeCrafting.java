package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

import java.util.List;

@SideOnly(Side.CLIENT)
public class CreativeCrafting implements ICrafting
{
    private final Minecraft mc;

    public CreativeCrafting(final Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public void sendContainerAndContentsToPlayer(final Container par1Container, final List par2List) {}

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot. Args: Container, slot number, slot contents
     */
    public void sendSlotContents(final Container par1Container, final int par2, final ItemStack par3ItemStack)
    {
        this.mc.playerController.sendSlotPacket(par3ItemStack, par2);
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendProgressBarUpdate(final Container par1Container, final int par2, final int par3) {}
}
