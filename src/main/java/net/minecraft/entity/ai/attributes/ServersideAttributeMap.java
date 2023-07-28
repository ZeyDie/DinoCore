package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import net.minecraft.server.management.LowerStringMap;

import java.util.*;

public class ServersideAttributeMap extends BaseAttributeMap
{
    private final Set field_111162_d = Sets.newHashSet();
    protected final Map field_111163_c = new LowerStringMap();

    public ModifiableAttributeInstance func_111159_c(final Attribute par1Attribute)
    {
        return (ModifiableAttributeInstance)super.getAttributeInstance(par1Attribute);
    }

    public ModifiableAttributeInstance func_111158_b(final String par1Str)
    {
        AttributeInstance attributeinstance = super.getAttributeInstanceByName(par1Str);

        if (attributeinstance == null)
        {
            attributeinstance = (AttributeInstance)this.field_111163_c.get(par1Str);
        }

        return (ModifiableAttributeInstance)attributeinstance;
    }

    public AttributeInstance func_111150_b(final Attribute par1Attribute)
    {
        if (this.attributesByName.containsKey(par1Attribute.getAttributeUnlocalizedName()))
        {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        else
        {
            final ModifiableAttributeInstance modifiableattributeinstance = new ModifiableAttributeInstance(this, par1Attribute);
            this.attributesByName.put(par1Attribute.getAttributeUnlocalizedName(), modifiableattributeinstance);

            if (par1Attribute instanceof RangedAttribute && ((RangedAttribute)par1Attribute).func_111116_f() != null)
            {
                this.field_111163_c.put(((RangedAttribute)par1Attribute).func_111116_f(), modifiableattributeinstance);
            }

            this.attributes.put(par1Attribute, modifiableattributeinstance);
            return modifiableattributeinstance;
        }
    }

    public void func_111149_a(final ModifiableAttributeInstance par1ModifiableAttributeInstance)
    {
        if (par1ModifiableAttributeInstance.func_111123_a().getShouldWatch())
        {
            this.field_111162_d.add(par1ModifiableAttributeInstance);
        }
    }

    public Set func_111161_b()
    {
        return this.field_111162_d;
    }

    public Collection func_111160_c()
    {
        final HashSet hashset = Sets.newHashSet();
        final Iterator iterator = this.getAllAttributes().iterator();

        while (iterator.hasNext())
        {
            final AttributeInstance attributeinstance = (AttributeInstance)iterator.next();

            if (attributeinstance.func_111123_a().getShouldWatch())
            {
                hashset.add(attributeinstance);
            }
        }

        return hashset;
    }

    public AttributeInstance getAttributeInstanceByName(final String par1Str)
    {
        return this.func_111158_b(par1Str);
    }

    public AttributeInstance getAttributeInstance(final Attribute par1Attribute)
    {
        return this.func_111159_c(par1Attribute);
    }
}
