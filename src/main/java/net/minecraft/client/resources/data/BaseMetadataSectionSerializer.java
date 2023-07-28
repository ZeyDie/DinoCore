package net.minecraft.client.resources.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BaseMetadataSectionSerializer implements MetadataSectionSerializer
{
    protected float func_110487_a(final JsonElement par1JsonElement, String par2Str, final Float par3, final float par4, final float par5)
    {
        String par2Str1 = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3 == null)
            {
                throw new JsonParseException("Missing " + par2Str1 + ": expected float");
            }
            else
            {
                return par3.floatValue();
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str1 + ": expected float, was " + par1JsonElement);
        }
        else
        {
            try
            {
                final float f2 = par1JsonElement.getAsFloat();

                if (f2 < par4)
                {
                    throw new JsonParseException("Invalid " + par2Str1 + ": expected float >= " + par4 + ", was " + f2);
                }
                else if (f2 > par5)
                {
                    throw new JsonParseException("Invalid " + par2Str1 + ": expected float <= " + par5 + ", was " + f2);
                }
                else
                {
                    return f2;
                }
            }
            catch (final NumberFormatException numberformatexception)
            {
                throw new JsonParseException("Invalid " + par2Str1 + ": expected float, was " + par1JsonElement, numberformatexception);
            }
        }
    }

    protected int func_110485_a(final JsonElement par1JsonElement, String par2Str, final Integer par3, final int par4, final int par5)
    {
        String par2Str1 = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3 == null)
            {
                throw new JsonParseException("Missing " + par2Str1 + ": expected int");
            }
            else
            {
                return par3.intValue();
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str1 + ": expected int, was " + par1JsonElement);
        }
        else
        {
            try
            {
                final int k = par1JsonElement.getAsInt();

                if (k < par4)
                {
                    throw new JsonParseException("Invalid " + par2Str1 + ": expected int >= " + par4 + ", was " + k);
                }
                else if (k > par5)
                {
                    throw new JsonParseException("Invalid " + par2Str1 + ": expected int <= " + par5 + ", was " + k);
                }
                else
                {
                    return k;
                }
            }
            catch (final NumberFormatException numberformatexception)
            {
                throw new JsonParseException("Invalid " + par2Str1 + ": expected int, was " + par1JsonElement, numberformatexception);
            }
        }
    }

    protected String func_110486_a(final JsonElement par1JsonElement, String par2Str, final String par3Str, final int par4, final int par5)
    {
        String par2Str1 = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3Str == null)
            {
                throw new JsonParseException("Missing " + par2Str1 + ": expected string");
            }
            else
            {
                return par3Str;
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str1 + ": expected string, was " + par1JsonElement);
        }
        else
        {
            final String s2 = par1JsonElement.getAsString();

            if (s2.length() < par4)
            {
                throw new JsonParseException("Invalid " + par2Str1 + ": expected string length >= " + par4 + ", was " + s2);
            }
            else if (s2.length() > par5)
            {
                throw new JsonParseException("Invalid " + par2Str1 + ": expected string length <= " + par5 + ", was " + s2);
            }
            else
            {
                return s2;
            }
        }
    }

    protected boolean func_110484_a(final JsonElement par1JsonElement, String par2Str, final Boolean par3)
    {
        String par2Str1 = this.getSectionName() + "->" + par2Str;

        if (par1JsonElement == null)
        {
            if (par3 == null)
            {
                throw new JsonParseException("Missing " + par2Str1 + ": expected boolean");
            }
            else
            {
                return par3.booleanValue();
            }
        }
        else if (!par1JsonElement.isJsonPrimitive())
        {
            throw new JsonParseException("Invalid " + par2Str1 + ": expected boolean, was " + par1JsonElement);
        }
        else
        {
            final boolean flag = par1JsonElement.getAsBoolean();
            return flag;
        }
    }
}
