package net.minecraft.entity;

import net.minecraft.entity.ai.attributes.*;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class SharedMonsterAttributes
{
    public static final Attribute maxHealth = (new RangedAttribute("generic.maxHealth", 20.0D, 0.0D, Double.MAX_VALUE)).func_111117_a("Max Health").setShouldWatch(true);
    public static final Attribute followRange = (new RangedAttribute("generic.followRange", 32.0D, 0.0D, 2048.0D)).func_111117_a("Follow Range");
    public static final Attribute knockbackResistance = (new RangedAttribute("generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).func_111117_a("Knockback Resistance");
    public static final Attribute movementSpeed = (new RangedAttribute("generic.movementSpeed", 0.699999988079071D, 0.0D, Double.MAX_VALUE)).func_111117_a("Movement Speed").setShouldWatch(true);
    public static final Attribute attackDamage = new RangedAttribute("generic.attackDamage", 2.0D, 0.0D, Double.MAX_VALUE);

    public static NBTTagList func_111257_a(final BaseAttributeMap par0BaseAttributeMap)
    {
        final NBTTagList nbttaglist = new NBTTagList();
        final Iterator iterator = par0BaseAttributeMap.getAllAttributes().iterator();

        while (iterator.hasNext())
        {
            final AttributeInstance attributeinstance = (AttributeInstance)iterator.next();
            nbttaglist.appendTag(func_111261_a(attributeinstance));
        }

        return nbttaglist;
    }

    private static NBTTagCompound func_111261_a(final AttributeInstance par0AttributeInstance)
    {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        final Attribute attribute = par0AttributeInstance.func_111123_a();
        nbttagcompound.setString("Name", attribute.getAttributeUnlocalizedName());
        nbttagcompound.setDouble("Base", par0AttributeInstance.getBaseValue());
        final Collection collection = par0AttributeInstance.func_111122_c();

        if (collection != null && !collection.isEmpty())
        {
            final NBTTagList nbttaglist = new NBTTagList();
            final Iterator iterator = collection.iterator();

            while (iterator.hasNext())
            {
                final AttributeModifier attributemodifier = (AttributeModifier)iterator.next();

                if (attributemodifier.isSaved())
                {
                    nbttaglist.appendTag(func_111262_a(attributemodifier));
                }
            }

            nbttagcompound.setTag("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    private static NBTTagCompound func_111262_a(final AttributeModifier par0AttributeModifier)
    {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("Name", par0AttributeModifier.getName());
        nbttagcompound.setDouble("Amount", par0AttributeModifier.getAmount());
        nbttagcompound.setInteger("Operation", par0AttributeModifier.getOperation());
        nbttagcompound.setLong("UUIDMost", par0AttributeModifier.getID().getMostSignificantBits());
        nbttagcompound.setLong("UUIDLeast", par0AttributeModifier.getID().getLeastSignificantBits());
        return nbttagcompound;
    }

    public static void func_111260_a(final BaseAttributeMap par0BaseAttributeMap, final NBTTagList par1NBTTagList, final ILogAgent par2ILogAgent)
    {
        for (int i = 0; i < par1NBTTagList.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound = (NBTTagCompound)par1NBTTagList.tagAt(i);
            final AttributeInstance attributeinstance = par0BaseAttributeMap.getAttributeInstanceByName(nbttagcompound.getString("Name"));

            if (attributeinstance != null)
            {
                func_111258_a(attributeinstance, nbttagcompound);
            }
            else if (par2ILogAgent != null)
            {
                par2ILogAgent.logWarning("Ignoring unknown attribute \'" + nbttagcompound.getString("Name") + "\'");
            }
        }
    }

    private static void func_111258_a(final AttributeInstance par0AttributeInstance, final NBTTagCompound par1NBTTagCompound)
    {
        par0AttributeInstance.setAttribute(par1NBTTagCompound.getDouble("Base"));

        if (par1NBTTagCompound.hasKey("Modifiers"))
        {
            final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Modifiers");

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                final AttributeModifier attributemodifier = func_111259_a((NBTTagCompound)nbttaglist.tagAt(i));
                final AttributeModifier attributemodifier1 = par0AttributeInstance.getModifier(attributemodifier.getID());

                if (attributemodifier1 != null)
                {
                    par0AttributeInstance.removeModifier(attributemodifier1);
                }

                par0AttributeInstance.applyModifier(attributemodifier);
            }
        }
    }

    public static AttributeModifier func_111259_a(final NBTTagCompound par0NBTTagCompound)
    {
        final UUID uuid = new UUID(par0NBTTagCompound.getLong("UUIDMost"), par0NBTTagCompound.getLong("UUIDLeast"));
        return new AttributeModifier(uuid, par0NBTTagCompound.getString("Name"), par0NBTTagCompound.getDouble("Amount"), par0NBTTagCompound.getInteger("Operation"));
    }
}
