package net.minecraft.potion;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StringUtils;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

// CraftBukkit start
// CraftBukkit end

public class Potion
{
    /** The array of potion types. */
    public static final Potion[] potionTypes = new Potion[32];
    public static final Potion field_76423_b = null;
    public static final Potion moveSpeed = (new Potion(1, false, 8171462)).setPotionName("potion.moveSpeed").setIconIndex(0, 0).func_111184_a(SharedMonsterAttributes.movementSpeed, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2);
    public static final Potion moveSlowdown = (new Potion(2, true, 5926017)).setPotionName("potion.moveSlowdown").setIconIndex(1, 0).func_111184_a(SharedMonsterAttributes.movementSpeed, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2);
    public static final Potion digSpeed = (new Potion(3, false, 14270531)).setPotionName("potion.digSpeed").setIconIndex(2, 0).setEffectiveness(1.5D);
    public static final Potion digSlowdown = (new Potion(4, true, 4866583)).setPotionName("potion.digSlowDown").setIconIndex(3, 0);
    public static final Potion damageBoost = (new PotionAttackDamage(5, false, 9643043)).setPotionName("potion.damageBoost").setIconIndex(4, 0).func_111184_a(SharedMonsterAttributes.attackDamage, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0D, 2);
    public static final Potion heal = (new PotionHealth(6, false, 16262179)).setPotionName("potion.heal");
    public static final Potion harm = (new PotionHealth(7, true, 4393481)).setPotionName("potion.harm");
    public static final Potion jump = (new Potion(8, false, 7889559)).setPotionName("potion.jump").setIconIndex(2, 1);
    public static final Potion confusion = (new Potion(9, true, 5578058)).setPotionName("potion.confusion").setIconIndex(3, 1).setEffectiveness(0.25D);

    /** The regeneration Potion object. */
    public static final Potion regeneration = (new Potion(10, false, 13458603)).setPotionName("potion.regeneration").setIconIndex(7, 0).setEffectiveness(0.25D);
    public static final Potion resistance = (new Potion(11, false, 10044730)).setPotionName("potion.resistance").setIconIndex(6, 1);

    /** The fire resistance Potion object. */
    public static final Potion fireResistance = (new Potion(12, false, 14981690)).setPotionName("potion.fireResistance").setIconIndex(7, 1);

    /** The water breathing Potion object. */
    public static final Potion waterBreathing = (new Potion(13, false, 3035801)).setPotionName("potion.waterBreathing").setIconIndex(0, 2);

    /** The invisibility Potion object. */
    public static final Potion invisibility = (new Potion(14, false, 8356754)).setPotionName("potion.invisibility").setIconIndex(0, 1);

    /** The blindness Potion object. */
    public static final Potion blindness = (new Potion(15, true, 2039587)).setPotionName("potion.blindness").setIconIndex(5, 1).setEffectiveness(0.25D);

    /** The night vision Potion object. */
    public static final Potion nightVision = (new Potion(16, false, 2039713)).setPotionName("potion.nightVision").setIconIndex(4, 1);

    /** The hunger Potion object. */
    public static final Potion hunger = (new Potion(17, true, 5797459)).setPotionName("potion.hunger").setIconIndex(1, 1);

    /** The weakness Potion object. */
    public static final Potion weakness = (new PotionAttackDamage(18, true, 4738376)).setPotionName("potion.weakness").setIconIndex(5, 0).func_111184_a(SharedMonsterAttributes.attackDamage, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);

    /** The poison Potion object. */
    public static final Potion poison = (new Potion(19, true, 5149489)).setPotionName("potion.poison").setIconIndex(6, 0).setEffectiveness(0.25D);

    /** The wither Potion object. */
    public static final Potion wither = (new Potion(20, true, 3484199)).setPotionName("potion.wither").setIconIndex(1, 2).setEffectiveness(0.25D);
    public static final Potion field_76434_w = (new PotionHealthBoost(21, false, 16284963)).setPotionName("potion.healthBoost").setIconIndex(2, 2).func_111184_a(SharedMonsterAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0);
    public static final Potion field_76444_x = (new PotionAbsoption(22, false, 2445989)).setPotionName("potion.absorption").setIconIndex(2, 2);
    public static final Potion field_76443_y = (new PotionHealth(23, false, 16262179)).setPotionName("potion.saturation");
    public static final Potion field_76442_z = null;
    public static final Potion field_76409_A = null;
    public static final Potion field_76410_B = null;
    public static final Potion field_76411_C = null;
    public static final Potion field_76405_D = null;
    public static final Potion field_76406_E = null;
    public static final Potion field_76407_F = null;
    public static final Potion field_76408_G = null;

    /** The Id of a Potion object. */
    public final int id;
    private final Map field_111188_I = Maps.newHashMap();

    /**
     * This field indicated if the effect is 'bad' - negative - for the entity.
     */
    private final boolean isBadEffect;

    /** Is the color of the liquid for this potion. */
    private final int liquidColor;

    /** The name of the Potion. */
    private String name = "";

    /** The index for the icon displayed when the potion effect is active. */
    private int statusIconIndex = -1;
    private double effectiveness;
    private boolean usable;

    protected Potion(final int par1, final boolean par2, final int par3)
    {
        this.id = par1;
        potionTypes[par1] = this;
        this.isBadEffect = par2;

        if (par2)
        {
            this.effectiveness = 0.5D;
        }
        else
        {
            this.effectiveness = 1.0D;
        }

        this.liquidColor = par3;
        org.bukkit.potion.PotionEffectType.registerPotionEffectType(new org.bukkit.craftbukkit.v1_6_R3.potion.CraftPotionEffectType(this)); // CraftBukkit
    }

    /**
     * Sets the index for the icon displayed in the player's inventory when the status is active.
     */
    public Potion setIconIndex(final int par1, final int par2)
    {
        this.statusIconIndex = par1 + par2 * 8;
        return this;
    }

    /**
     * returns the ID of the potion
     */
    public int getId()
    {
        return this.id;
    }

    public void performEffect(final EntityLivingBase par1EntityLivingBase, final int par2)
    {
        if (this.id == regeneration.id)
        {
            if (par1EntityLivingBase.getHealth() < par1EntityLivingBase.getMaxHealth())
            {
                par1EntityLivingBase.heal(1.0F, RegainReason.MAGIC_REGEN); // CraftBukkit
            }
        }
        else if (this.id == poison.id)
        {
            if (par1EntityLivingBase.getHealth() > 1.0F)
            {
                par1EntityLivingBase.attackEntityFrom(CraftEventFactory.POISON, 1.0F); // CraftBukkit - DamageSource.MAGIC -> CraftEventFactory.POISON
            }
        }
        else if (this.id == wither.id)
        {
            par1EntityLivingBase.attackEntityFrom(DamageSource.wither, 1.0F);
        }
        else if (this.id == hunger.id && par1EntityLivingBase instanceof EntityPlayer)
        {
            ((EntityPlayer)par1EntityLivingBase).addExhaustion(0.025F * (float)(par2 + 1));
        }
        else if (this.id == field_76443_y.id && par1EntityLivingBase instanceof EntityPlayer)
        {
            if (!par1EntityLivingBase.worldObj.isRemote)
            {
                ((EntityPlayer)par1EntityLivingBase).getFoodStats().addStats(par2 + 1, 1.0F);
            }
        }
        else if ((this.id != heal.id || par1EntityLivingBase.isEntityUndead()) && (this.id != harm.id || !par1EntityLivingBase.isEntityUndead()))
        {
            if (this.id == harm.id && !par1EntityLivingBase.isEntityUndead() || this.id == heal.id && par1EntityLivingBase.isEntityUndead())
            {
                par1EntityLivingBase.attackEntityFrom(DamageSource.magic, (float)(6 << par2));
            }
        }
        else
        {
            par1EntityLivingBase.heal((float) Math.max(4 << par2, 0), RegainReason.MAGIC); // CraftBukkit
        }
    }

    /**
     * Hits the provided entity with this potion's instant effect.
     */
    public void affectEntity(final EntityLivingBase par1EntityLivingBase, final EntityLivingBase par2EntityLivingBase, final int par3, final double par4)
    {
        // CraftBukkit start - Delegate; we need EntityPotion
        applyInstantEffect(par1EntityLivingBase, par2EntityLivingBase, par3, par4, null);
    }

    public void applyInstantEffect(final EntityLivingBase entityliving, final EntityLivingBase entitylivingbase1, final int i, final double d0, final EntityPotion potion)
    {
        // CraftBukkit end
        final int j;

        if ((this.id != heal.id || entitylivingbase1.isEntityUndead()) && (this.id != harm.id || !entitylivingbase1.isEntityUndead()))
        {
            if (this.id == harm.id && !entitylivingbase1.isEntityUndead() || this.id == heal.id && entitylivingbase1.isEntityUndead())
            {
                j = (int)(d0 * (double)(6 << i) + 0.5D);

                if (entityliving == null)
                {
                    entitylivingbase1.attackEntityFrom(DamageSource.magic, (float) j);
                }
                else
                {
                    // CraftBukkit - The "damager" needs to be the potion
                    entitylivingbase1.attackEntityFrom(DamageSource.causeIndirectMagicDamage(potion != null ? potion : entitylivingbase1, entityliving), (float) j);
                }
            }
        }
        else
        {
            j = (int)(d0 * (double)(4 << i) + 0.5D);
            entitylivingbase1.heal((float) j, RegainReason.MAGIC);
        }
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant()
    {
        return false;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(final int par1, final int par2)
    {
        final int k;

        if (this.id == regeneration.id)
        {
            k = 50 >> par2;
            return k > 0 ? par1 % k == 0 : true;
        }
        else if (this.id == poison.id)
        {
            k = 25 >> par2;
            return k > 0 ? par1 % k == 0 : true;
        }
        else if (this.id == wither.id)
        {
            k = 40 >> par2;
            return k > 0 ? par1 % k == 0 : true;
        }
        else
        {
            return this.id == hunger.id;
        }
    }

    /**
     * Set the potion name.
     */
    public Potion setPotionName(final String par1Str)
    {
        this.name = par1Str;
        return this;
    }

    /**
     * returns the name of the potion
     */
    public String getName()
    {
        return this.name;
    }

    protected Potion setEffectiveness(final double par1)
    {
        this.effectiveness = par1;
        return this;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the potion has a associated status icon to display in then inventory when active.
     */
    public boolean hasStatusIcon()
    {
        return this.statusIconIndex >= 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the index for the icon to display when the potion is active.
     */
    public int getStatusIconIndex()
    {
        return this.statusIconIndex;
    }

    @SideOnly(Side.CLIENT)

    /**
     * This method returns true if the potion effect is bad - negative - for the entity.
     */
    public boolean isBadEffect()
    {
        return this.isBadEffect;
    }

    @SideOnly(Side.CLIENT)
    public static String getDurationString(final PotionEffect par0PotionEffect)
    {
        if (par0PotionEffect.getIsPotionDurationMax())
        {
            return "**:**";
        }
        else
        {
            final int i = par0PotionEffect.getDuration();
            return StringUtils.ticksToElapsedTime(i);
        }
    }

    public double getEffectiveness()
    {
        return this.effectiveness;
    }

    public boolean isUsable()
    {
        return this.usable;
    }

    /**
     * Returns the color of the potion liquid.
     */
    public int getLiquidColor()
    {
        return this.liquidColor;
    }

    public Potion func_111184_a(final Attribute par1Attribute, final String par2Str, final double par3, final int par5)
    {
        final AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(par2Str), this.getName(), par3, par5);
        this.field_111188_I.put(par1Attribute, attributemodifier);
        return this;
    }

    public void removeAttributesModifiersFromEntity(final EntityLivingBase par1EntityLivingBase, final BaseAttributeMap par2BaseAttributeMap, final int par3)
    {
        final Iterator iterator = this.field_111188_I.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            final AttributeInstance attributeinstance = par2BaseAttributeMap.getAttributeInstance((Attribute)entry.getKey());

            if (attributeinstance != null)
            {
                attributeinstance.removeModifier((AttributeModifier)entry.getValue());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public Map func_111186_k()
    {
        return this.field_111188_I;
    }

    public void applyAttributesModifiersToEntity(final EntityLivingBase par1EntityLivingBase, final BaseAttributeMap par2BaseAttributeMap, final int par3)
    {
        final Iterator iterator = this.field_111188_I.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            final AttributeInstance attributeinstance = par2BaseAttributeMap.getAttributeInstance((Attribute)entry.getKey());

            if (attributeinstance != null)
            {
                final AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
                attributeinstance.removeModifier(attributemodifier);
                attributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), this.getName() + " " + par3, this.func_111183_a(par3, attributemodifier), attributemodifier.getOperation()));
            }
        }
    }

    public double func_111183_a(final int par1, final AttributeModifier par2AttributeModifier)
    {
        return par2AttributeModifier.getAmount() * (double)(par1 + 1);
    }
}
