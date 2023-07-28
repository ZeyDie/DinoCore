package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;

public class PlayerSleepInBedEvent extends PlayerEvent
{
    public EnumStatus result = null;
    public final int x;
    public final int y;
    public final int z;

    public PlayerSleepInBedEvent(final EntityPlayer player, final int x, final int y, final int z)
    {
        super(player);
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
