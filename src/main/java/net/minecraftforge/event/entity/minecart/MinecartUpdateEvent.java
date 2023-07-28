package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.item.EntityMinecart;

public class MinecartUpdateEvent extends MinecartEvent
{
    public final float x;
    public final float y;
    public final float z;

    public MinecartUpdateEvent(final EntityMinecart minecart, final float x, final float y, final float z)
    {
        super(minecart);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
