package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;

import static net.minecraftforge.event.Event.Result.DEFAULT;
import static net.minecraftforge.event.Event.Result.DENY;

@Cancelable
public class PlayerInteractEvent extends PlayerEvent
{
    public static enum Action
    {
        RIGHT_CLICK_AIR,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK
    }
    
    public final Action action;
    public final int x;
    public final int y;
    public final int z;
    public final int face;
    
    public Result useBlock = DEFAULT;
    public Result useItem = DEFAULT;
    
    public PlayerInteractEvent(final EntityPlayer player, final Action action, final int x, final int y, final int z, final int face)
    {
        super(player);
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        if (face == -1) useBlock = DENY;
    }
    
    @Override
    public void setCanceled(final boolean cancel)
    {
        super.setCanceled(cancel);
        useBlock = (cancel ? DENY : useBlock == DENY ? DEFAULT : useBlock);
        useItem = (cancel ? DENY : useItem == DENY ? DEFAULT : useItem);
    }
}
