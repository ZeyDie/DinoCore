package net.minecraft.village;

import net.minecraft.entity.EntityLivingBase;

class VillageAgressor
{
    public EntityLivingBase agressor;
    public int agressionTime;

    final Village villageObj;

    VillageAgressor(final Village par1Village, final EntityLivingBase par2EntityLivingBase, final int par3)
    {
        this.villageObj = par1Village;
        this.agressor = par2EntityLivingBase;
        this.agressionTime = par3;
    }
}
