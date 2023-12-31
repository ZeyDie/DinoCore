package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.Event;

public class EntityEvent extends Event
{
    public final Entity entity;

    public EntityEvent(final Entity entity)
    {
        this.entity = entity;
    }

    public static class EntityConstructing extends EntityEvent
    {
        public EntityConstructing(final Entity entity)
        {
            super(entity);
        }
    }

    public static class CanUpdate extends EntityEvent
    {
        public boolean canUpdate = false;
        public CanUpdate(final Entity entity)
        {
            super(entity);
        }
    }

    public static class EnteringChunk extends EntityEvent
    {
        public int newChunkX;
        public int newChunkZ;
        public int oldChunkX;
        public int oldChunkZ;

        public EnteringChunk(final Entity entity, final int newChunkX, final int newChunkZ, final int oldChunkX, final int oldChunkZ)
        {
            super(entity);
            this.newChunkX = newChunkX;
            this.newChunkZ = newChunkZ;
            this.oldChunkX = oldChunkX;
            this.oldChunkZ = oldChunkZ;
        }
    }
}
