package net.minecraftforge.client.event;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

import java.util.ArrayList;

@Cancelable
public class RenderGameOverlayEvent extends Event
{
    public static enum ElementType
    {
        ALL,
        HELMET,
        PORTAL,
        CROSSHAIRS,
        BOSSHEALTH,
        ARMOR,
        HEALTH,
        FOOD,
        AIR,
        HOTBAR,
        EXPERIENCE,
        TEXT,
        HEALTHMOUNT,
        JUMPBAR,
        CHAT,
        PLAYER_LIST
    }

    public final float partialTicks;
    public final ScaledResolution resolution;
    public final int mouseX;
    public final int mouseY;
    public final ElementType type;

    public RenderGameOverlayEvent(final float partialTicks, final ScaledResolution resolution, final int mouseX, final int mouseY)
    {
        this.partialTicks = partialTicks;
        this.resolution = resolution;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.type = null;
    }

    private RenderGameOverlayEvent(final RenderGameOverlayEvent parent, final ElementType type)
    {
        this.partialTicks = parent.partialTicks;
        this.resolution = parent.resolution;
        this.mouseX = parent.mouseX;
        this.mouseY = parent.mouseY;
        this.type = type;
    }

    public static class Pre extends RenderGameOverlayEvent
    {
        public Pre(final RenderGameOverlayEvent parent, final ElementType type)
        {
            super(parent, type);
        }
    }

    public static class Post extends RenderGameOverlayEvent
    {
        public Post(final RenderGameOverlayEvent parent, final ElementType type)
        {
            super(parent, type);
        }
        @Override public boolean isCancelable(){ return false; }
    }

    public static class Text extends Pre
    {
        public final ArrayList<String> left;
        public final ArrayList<String> right;
        public Text(final RenderGameOverlayEvent parent, final ArrayList<String> left, final ArrayList<String> right)
        {
            super(parent, ElementType.TEXT);
            this.left = left;
            this.right = right;
        }
    }

    public static class Chat extends Pre
    {
        public int posX;
        public int posY;

        public Chat(final RenderGameOverlayEvent parent, final int posX, final int posY)
        {
            super(parent, ElementType.CHAT);
            this.posX = posX;
            this.posY = posY;
        }
    }
}
