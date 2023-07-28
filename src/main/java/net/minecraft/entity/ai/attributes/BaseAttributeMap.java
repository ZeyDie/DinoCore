package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Multimap;
import net.minecraft.server.management.LowerStringMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BaseAttributeMap
{
    //TODO ZeyCodeReplace Map on Map<Attribute, AttributeInstance>
    protected final Map<Attribute, AttributeInstance> attributes = new HashMap<>();
    //TODO ZeyCodeReplace Map on Map<String, AttributeInstance>
    protected final Map<String, AttributeInstance> attributesByName = new LowerStringMap();

    public AttributeInstance getAttributeInstance(final Attribute par1Attribute)
    {
        return this.attributes.get(par1Attribute);
    }

    public AttributeInstance getAttributeInstanceByName(final String par1Str)
    {
        return this.attributesByName.get(par1Str);
    }

    public abstract AttributeInstance func_111150_b(Attribute attribute);

    //TODO ZeyCodeReplace Collection on Collection<AttributeInstance>
    public Collection<AttributeInstance> getAllAttributes()
    {
        return this.attributesByName.values();
    }

    public void func_111149_a(final ModifiableAttributeInstance par1ModifiableAttributeInstance) {}

    public void removeAttributeModifiers(final Multimap par1Multimap)
    {
        final Iterator iterator = par1Multimap.entries().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            final AttributeInstance attributeinstance = this.getAttributeInstanceByName((String)entry.getKey());

            if (attributeinstance != null)
            {
                attributeinstance.removeModifier((AttributeModifier)entry.getValue());
            }
        }
    }

    public void applyAttributeModifiers(final Multimap par1Multimap)
    {
        final Iterator iterator = par1Multimap.entries().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            final AttributeInstance attributeinstance = this.getAttributeInstanceByName((String)entry.getKey());

            if (attributeinstance != null)
            {
                attributeinstance.removeModifier((AttributeModifier)entry.getValue());
                attributeinstance.applyModifier((AttributeModifier)entry.getValue());
            }
        }
    }
}
