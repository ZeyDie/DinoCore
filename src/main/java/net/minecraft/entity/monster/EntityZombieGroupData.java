package net.minecraft.entity.monster;

import net.minecraft.entity.EntityLivingData;

class EntityZombieGroupData implements EntityLivingData
{
    public boolean field_142048_a;
    public boolean field_142046_b;

    final EntityZombie field_142047_c;

    private EntityZombieGroupData(final EntityZombie par1EntityZombie, final boolean par2, final boolean par3)
    {
        this.field_142047_c = par1EntityZombie;
        this.field_142048_a = false;
        this.field_142046_b = false;
        this.field_142048_a = par2;
        this.field_142046_b = par3;
    }

    EntityZombieGroupData(final EntityZombie par1EntityZombie, final boolean par2, final boolean par3, final EntityZombieINNER1 par4EntityZombieINNER1)
    {
        this(par1EntityZombie, par2, par3);
    }
}
