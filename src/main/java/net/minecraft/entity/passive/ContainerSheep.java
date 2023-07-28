package net.minecraft.entity.passive;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

class ContainerSheep extends Container
{
    final EntitySheep field_90034_a;

    ContainerSheep(final EntitySheep par1EntitySheep)
    {
        this.field_90034_a = par1EntitySheep;
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer)
    {
        return false;
    }
}
