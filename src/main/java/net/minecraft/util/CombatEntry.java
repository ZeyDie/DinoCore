package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry
{
    private final DamageSource damageSrc;
    private final int field_94567_b;
    private final float field_94568_c;
    private final float field_94565_d;
    private final String field_94566_e;
    private final float field_94564_f;

    public CombatEntry(final DamageSource par1DamageSource, final int par2, final float par3, final float par4, final String par5Str, final float par6)
    {
        this.damageSrc = par1DamageSource;
        this.field_94567_b = par2;
        this.field_94568_c = par4;
        this.field_94565_d = par3;
        this.field_94566_e = par5Str;
        this.field_94564_f = par6;
    }

    /**
     * Get the DamageSource of the CombatEntry instance.
     */
    public DamageSource getDamageSrc()
    {
        return this.damageSrc;
    }

    public float func_94563_c()
    {
        return this.field_94568_c;
    }

    public boolean func_94559_f()
    {
        return this.damageSrc.getEntity() instanceof EntityLivingBase;
    }

    public String func_94562_g()
    {
        return this.field_94566_e;
    }

    public String func_94558_h()
    {
        return this.getDamageSrc().getEntity() == null ? null : this.getDamageSrc().getEntity().getTranslatedEntityName();
    }

    public float func_94561_i()
    {
        return this.damageSrc == DamageSource.outOfWorld ? Float.MAX_VALUE : this.field_94564_f;
    }
}
