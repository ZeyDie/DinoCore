package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionHealthBoost extends Potion
{
    public PotionHealthBoost(final int par1, final boolean par2, final int par3)
    {
        super(par1, par2, par3);
    }

    public void removeAttributesModifiersFromEntity(final EntityLivingBase par1EntityLivingBase, final BaseAttributeMap par2BaseAttributeMap, final int par3)
    {
        super.removeAttributesModifiersFromEntity(par1EntityLivingBase, par2BaseAttributeMap, par3);

        if (par1EntityLivingBase.getHealth() > par1EntityLivingBase.getMaxHealth())
        {
            par1EntityLivingBase.setHealth(par1EntityLivingBase.getMaxHealth());
        }
    }
}
