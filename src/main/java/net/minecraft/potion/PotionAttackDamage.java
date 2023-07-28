package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class PotionAttackDamage extends Potion
{
    protected PotionAttackDamage(final int par1, final boolean par2, final int par3)
    {
        super(par1, par2, par3);
    }

    public double func_111183_a(final int par1, final AttributeModifier par2AttributeModifier)
    {
        return this.id == Potion.weakness.id ? (double)(-0.5F * (float)(par1 + 1)) : 1.3D * (double)(par1 + 1);
    }
}
