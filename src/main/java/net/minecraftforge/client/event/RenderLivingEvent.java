package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public abstract class RenderLivingEvent extends Event
{
    public final EntityLivingBase entity;
    public final RendererLivingEntity renderer;

    public RenderLivingEvent(final EntityLivingBase entity, final RendererLivingEntity renderer)
    {
        this.entity = entity;
        this.renderer = renderer;
    }
    
    @Cancelable
    public static class Pre extends RenderLivingEvent
    {
        public Pre(final EntityLivingBase entity, final RendererLivingEntity renderer){ super(entity, renderer); }
    }
    public static class Post extends RenderLivingEvent
    {
        public Post(final EntityLivingBase entity, final RendererLivingEntity renderer){ super(entity, renderer); }
    }

    public abstract static class Specials extends RenderLivingEvent
    {
        public Specials(final EntityLivingBase entity, final RendererLivingEntity renderer){ super(entity, renderer); }

        @Cancelable
        public static class Pre extends Specials
        {
            public Pre(final EntityLivingBase entity, final RendererLivingEntity renderer){ super(entity, renderer); }
        }
        public static class Post extends Specials
        {
            public Post(final EntityLivingBase entity, final RendererLivingEntity renderer){ super(entity, renderer); }
        }
    }
}
