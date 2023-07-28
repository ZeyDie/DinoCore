package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;

public class MinecartCollisionEvent extends MinecartEvent
{
    public final Entity collider;

    public MinecartCollisionEvent(final EntityMinecart minecart, final Entity collider)
    {
        super(minecart);
        this.collider = collider;
    }
}
