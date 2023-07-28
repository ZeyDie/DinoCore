package net.minecraftforge.event.entity.player;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerEvent extends LivingEvent
{
    public final EntityPlayer entityPlayer;
    public PlayerEvent(final EntityPlayer player)
    {
        super(player);
        entityPlayer = player;
    }
    
    public static class HarvestCheck extends PlayerEvent
    {
        public final Block block;
        public boolean success;

        public HarvestCheck(final EntityPlayer player, final Block block, final boolean success)
        {
            super(player);
            this.block = block;
            this.success = success;
        }
    }

    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        public final Block block;
        public final int metadata;
        public final float originalSpeed;
        public float newSpeed = 0.0f;

        public BreakSpeed(final EntityPlayer player, final Block block, final int metadata, final float original)
        {
            super(player);
            this.block = block;
            this.metadata = metadata;
            this.originalSpeed = original;
            this.newSpeed = original;
        }
    }

    public static class NameFormat extends PlayerEvent
    {
        public final String username;
        public String displayname;

        public NameFormat(final EntityPlayer player, final String username) {
            super(player);
            this.username = username;
            this.displayname = username;
        }
    }
}
