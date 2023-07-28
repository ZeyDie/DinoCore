package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

public class ZombieEvent extends EntityEvent {

    public ZombieEvent(final EntityZombie entity)
    {
        super(entity);
    }

    public EntityZombie getSummoner()
    {
        return (EntityZombie) entity;
    }

    @HasResult
    public static class SummonAidEvent extends ZombieEvent {
        /**
         * Populate this field to have a custom zombie instead of a normal zombie summoned
         */
        public EntityZombie customSummonedAid;
        
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public final EntityLivingBase attacker;
        public final double summonChance;
        
        public SummonAidEvent(final EntityZombie entity, final World world, final int x, final int y, final int z, final EntityLivingBase attacker, final double summonChance)
        {
            super(entity);
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.attacker = attacker;
            this.summonChance = summonChance;
        }
        
    }
}
