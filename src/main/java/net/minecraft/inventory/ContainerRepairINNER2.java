package net.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class ContainerRepairINNER2 extends Slot
{
    final World field_135071_a;

    final int field_135069_b;

    final int field_135070_c;

    final int field_135067_d;

    final ContainerRepair repairContainer;

    ContainerRepairINNER2(final ContainerRepair par1ContainerRepair, final IInventory par2IInventory, final int par3, final int par4, final int par5, final World par6World, final int par7, final int par8, final int par9)
    {
        super(par2IInventory, par3, par4, par5);
        this.repairContainer = par1ContainerRepair;
        this.field_135071_a = par6World;
        this.field_135069_b = par7;
        this.field_135070_c = par8;
        this.field_135067_d = par9;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(final ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer)
    {
        return (par1EntityPlayer.capabilities.isCreativeMode || par1EntityPlayer.experienceLevel >= this.repairContainer.maximumCost) && this.repairContainer.maximumCost > 0 && this.getHasStack();
    }

    public void onPickupFromSlot(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack)
    {
        if (!par1EntityPlayer.capabilities.isCreativeMode)
        {
            par1EntityPlayer.addExperienceLevel(-this.repairContainer.maximumCost);
        }

        ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(0, (ItemStack)null);

        if (ContainerRepair.getStackSizeUsedInRepair(this.repairContainer) > 0)
        {
            final ItemStack itemstack1 = ContainerRepair.getRepairInputInventory(this.repairContainer).getStackInSlot(1);

            if (itemstack1 != null && itemstack1.stackSize > ContainerRepair.getStackSizeUsedInRepair(this.repairContainer))
            {
                itemstack1.stackSize -= ContainerRepair.getStackSizeUsedInRepair(this.repairContainer);
                ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(1, itemstack1);
            }
            else
            {
                ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(1, (ItemStack)null);
            }
        }
        else
        {
            ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(1, (ItemStack)null);
        }

        this.repairContainer.maximumCost = 0;

        if (!par1EntityPlayer.capabilities.isCreativeMode && !this.field_135071_a.isRemote && this.field_135071_a.getBlockId(this.field_135069_b, this.field_135070_c, this.field_135067_d) == Block.anvil.blockID && par1EntityPlayer.getRNG().nextFloat() < 0.12F)
        {
            final int i = this.field_135071_a.getBlockMetadata(this.field_135069_b, this.field_135070_c, this.field_135067_d);
            final int j = i & 3;
            int k = i >> 2;
            ++k;

            if (k > 2)
            {
                this.field_135071_a.setBlockToAir(this.field_135069_b, this.field_135070_c, this.field_135067_d);
                this.field_135071_a.playAuxSFX(1020, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
            }
            else
            {
                this.field_135071_a.setBlockMetadataWithNotify(this.field_135069_b, this.field_135070_c, this.field_135067_d, j | k << 2, 2);
                this.field_135071_a.playAuxSFX(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
            }
        }
        else if (!this.field_135071_a.isRemote)
        {
            this.field_135071_a.playAuxSFX(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
        }
    }
}
