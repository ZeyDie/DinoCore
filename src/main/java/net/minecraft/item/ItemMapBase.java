package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item
{
    protected ItemMapBase(final int par1)
    {
        super(par1);
    }

    /**
     * false for all Items except sub-classes of ItemMapBase
     */
    public boolean isMap()
    {
        return true;
    }

    /**
     * returns null if no update is to be sent
     */
    public Packet createMapDataPacket(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer)
    {
        return null;
    }
}
