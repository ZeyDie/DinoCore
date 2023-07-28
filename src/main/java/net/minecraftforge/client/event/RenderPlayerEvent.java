package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    public final RenderPlayer renderer;
    public final float partialRenderTick;

    public RenderPlayerEvent(final EntityPlayer player, final RenderPlayer renderer, final float partialRenderTick)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
    }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(final EntityPlayer player, final RenderPlayer renderer, final float tick){ super(player, renderer, tick); }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(final EntityPlayer player, final RenderPlayer renderer, final float tick){ super(player, renderer, tick); }
    }
    
    public abstract static class Specials extends RenderPlayerEvent
    {
        @Deprecated
        public final float partialTicks;
        public Specials(final EntityPlayer player, final RenderPlayer renderer, final float partialTicks)
        {
            super(player, renderer, partialTicks);
            this.partialTicks = partialTicks;
        }

        @Cancelable
        public static class Pre extends Specials
        {
            public boolean renderHelmet = true;
            public boolean renderCape = true;
            public boolean renderItem = true;
            public Pre(final EntityPlayer player, final RenderPlayer renderer, final float partialTicks){ super(player, renderer, partialTicks); }
        }

        public static class Post extends Specials
        {
            public Post(final EntityPlayer player, final RenderPlayer renderer, final float partialTicks){ super(player, renderer, partialTicks); }
        }
    }

    public static class SetArmorModel extends RenderPlayerEvent
    {
        /**
         * Setting this to any value besides -1 will result in the function being 
         * Immediately exited with the return value specified.
         */
        public int result = -1;
        public final int slot;
        @Deprecated
        public final float partialTick;
        public final ItemStack stack;
        public SetArmorModel(final EntityPlayer player, final RenderPlayer renderer, final int slot, final float partialTick, final ItemStack stack)
        {
            super(player, renderer, partialTick);
            this.slot = slot;
            this.partialTick = partialTick;
            this.stack = stack;
        }
    }
}