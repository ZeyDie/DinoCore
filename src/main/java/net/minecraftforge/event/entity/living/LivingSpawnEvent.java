package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

public class LivingSpawnEvent extends LivingEvent
{
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    
    public LivingSpawnEvent(final EntityLiving entity, final World world, final float x, final float y, final float z)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Fires before mob spawn events.
     * 
     * Result is significant:
     *    DEFAULT: use vanilla spawn rules
     *    ALLOW:   allow the spawn
     *    DENY:    deny the spawn
     *
     */
    @HasResult
    public static class CheckSpawn extends LivingSpawnEvent
    {
        public CheckSpawn(final EntityLiving entity, final World world, final float x, final float y, final float z)
        {
            super(entity, world, x, y, z);
            entity.spawnReason = "natural"; // Cauldron - used to handle CraftBukkit's SpawnReason with CustomSpawners
        }
    }

    @Cancelable
    public static class SpecialSpawn extends LivingSpawnEvent
    {
        public SpecialSpawn(final EntityLiving entity, final World world, final float x, final float y, final float z)
        {
            super(entity, world, x, y, z);
            entity.spawnReason = "spawner"; // Cauldron - used to handle CraftBukkit's SpawnReason with CustomSpawners
        }
    }
    
    /**
     * Fired each tick for despawnable mobs to allow control over despawning.
     * {@link Result#DEFAULT} will pass the mob on to vanilla despawn mechanics.
     * {@link Result#ALLOW} will force the mob to despawn.
     * {@link Result#DENY} will force the mob to remain.
     * This is fired every tick for every despawnable entity. Be efficient in your handlers.
     * 
     * Note: this is not fired <em>if</em> the mob is definitely going to otherwise despawn. It is fired to check if
     * the mob can be allowed to despawn. See {@link EntityLiving#despawnEntity}
     * 
     * @author cpw
     *
     */
    @HasResult
    public static class AllowDespawn extends LivingSpawnEvent
    {
        public AllowDespawn(final EntityLiving entity)
        {
            super(entity, entity.worldObj, (float)entity.posX, (float)entity.posY, (float)entity.posZ);
        }
        
    }
}