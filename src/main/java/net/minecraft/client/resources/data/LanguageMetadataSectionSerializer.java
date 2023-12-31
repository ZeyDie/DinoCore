package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.Language;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer
{
    public LanguageMetadataSection func_135020_a(final JsonElement par1JsonElement, final Type par2Type, final JsonDeserializationContext par3JsonDeserializationContext)
    {
        final JsonObject jsonobject = par1JsonElement.getAsJsonObject();
        final HashSet hashset = Sets.newHashSet();
        final Iterator iterator = jsonobject.entrySet().iterator();
        String s;
        String s1;
        String s2;
        boolean flag;

        do
        {
            if (!iterator.hasNext())
            {
                return new LanguageMetadataSection(hashset);
            }

            final Entry entry = (Entry)iterator.next();
            s = (String)entry.getKey();
            final JsonElement jsonelement1 = (JsonElement)entry.getValue();

            if (!jsonelement1.isJsonObject())
            {
                throw new JsonParseException("Invalid language->\'" + s + "\': expected object, was " + jsonelement1);
            }

            final JsonObject jsonobject1 = jsonelement1.getAsJsonObject();
            s1 = this.func_110486_a(jsonobject1.get("region"), "region", "", 0, Integer.MAX_VALUE);
            s2 = this.func_110486_a(jsonobject1.get("name"), "name", "", 0, Integer.MAX_VALUE);
            flag = this.func_110484_a(jsonobject1.get("bidirectional"), "bidirectional", Boolean.valueOf(false));

            if (s1.isEmpty())
            {
                throw new JsonParseException("Invalid language->\'" + s + "\'->region: empty value");
            }

            if (s2.isEmpty())
            {
                throw new JsonParseException("Invalid language->\'" + s + "\'->name: empty value");
            }
        }
        while (hashset.add(new Language(s, s1, s2, flag)));

        throw new JsonParseException("Duplicate language->\'" + s + "\' defined");
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "language";
    }

    public Object deserialize(final JsonElement par1JsonElement, final Type par2Type, final JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_135020_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }
}
