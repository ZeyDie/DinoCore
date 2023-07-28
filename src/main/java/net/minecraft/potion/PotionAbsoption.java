package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionAbsoption extends Potion
{
    protected PotionAbsoption(final int par1, final boolean par2, final int par3)
    {
        super(par1, par2, par3);
    }

    public void removeAttributesModifiersFromEntity(final EntityLivingBase par1EntityLivingBase, final BaseAttributeMap par2BaseAttributeMap, final int par3)
    {
        par1EntityLivingBase.setAbsorptionAmount(par1EntityLivingBase.getAbsorptionAmount() - (float)(4 * (par3 + 1)));
        super.removeAttributesModifiersFromEntity(par1EntityLivingBase, par2BaseAttributeMap, par3);
    }

    public void applyAttributesModifiersToEntity(final EntityLivingBase par1EntityLivingBase, final BaseAttributeMap par2BaseAttributeMap, final int par3)
    {
        par1EntityLivingBase.setAbsorptionAmount(par1EntityLivingBase.getAbsorptionAmount() + (float)(4 * (par3 + 1)));
        super.applyAttributesModifiersToEntity(par1EntityLivingBase, par2BaseAttributeMap, par3);
    }
}
