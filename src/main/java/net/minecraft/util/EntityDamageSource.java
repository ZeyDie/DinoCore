package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource
{
    protected Entity damageSourceEntity;

    public EntityDamageSource(final String par1Str, final Entity par2Entity)
    {
        super(par1Str);
        this.damageSourceEntity = par2Entity;
    }

    public Entity getEntity()
    {
        return this.damageSourceEntity;
    }

    /**
     * Returns the message to be displayed on player death.
     */
    public ChatMessageComponent getDeathMessage(final EntityLivingBase par1EntityLivingBase)
    {
        final ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
        final String s = "death.attack." + this.damageType;
        final String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.func_94522_b(s1) ? ChatMessageComponent.createFromTranslationWithSubstitutions(s1, new Object[] {par1EntityLivingBase.getTranslatedEntityName(), this.damageSourceEntity.getTranslatedEntityName(), itemstack.getDisplayName()}): ChatMessageComponent.createFromTranslationWithSubstitutions(s, new Object[] {par1EntityLivingBase.getTranslatedEntityName(), this.damageSourceEntity.getTranslatedEntityName()});
    }

    /**
     * Return whether this damage source will have its damage amount scaled based on the current difficulty.
     */
    public boolean isDifficultyScaled()
    {
        return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
    }
}
