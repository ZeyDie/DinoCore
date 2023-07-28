package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation
{
    private final String resourceDomain;
    private final String resourcePath;

    public ResourceLocation(final String par1Str, final String par2Str)
    {
        Validate.notNull(par2Str);

        if (par1Str != null && !par1Str.isEmpty())
        {
            this.resourceDomain = par1Str;
        }
        else
        {
            this.resourceDomain = "minecraft";
        }

        this.resourcePath = par2Str;
    }

    public ResourceLocation(final String par1Str)
    {
        String s1 = "minecraft";
        String s2 = par1Str;
        final int i = par1Str.indexOf(58);

        if (i >= 0)
        {
            s2 = par1Str.substring(i + 1, par1Str.length());

            if (i > 1)
            {
                s1 = par1Str.substring(0, i);
            }
        }

        this.resourceDomain = s1.toLowerCase();
        this.resourcePath = s2;
    }

    public String getResourcePath()
    {
        return this.resourcePath;
    }

    public String getResourceDomain()
    {
        return this.resourceDomain;
    }

    public String toString()
    {
        return this.resourceDomain + ":" + this.resourcePath;
    }

    public boolean equals(final Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ResourceLocation))
        {
            return false;
        }
        else
        {
            final ResourceLocation resourcelocation = (ResourceLocation)par1Obj;
            return this.resourceDomain.equals(resourcelocation.resourceDomain) && this.resourcePath.equals(resourcelocation.resourcePath);
        }
    }

    public int hashCode()
    {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
