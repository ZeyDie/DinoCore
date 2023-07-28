package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.Type;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer implements JsonSerializer
{
    public AnimationMetadataSection func_110493_a(final JsonElement par1JsonElement, final Type par2Type, final JsonDeserializationContext par3JsonDeserializationContext)
    {
        final ArrayList arraylist = Lists.newArrayList();
        final JsonObject jsonobject = (JsonObject)par1JsonElement;
        final int i = this.func_110485_a(jsonobject.get("frametime"), "frametime", Integer.valueOf(1), 1, Integer.MAX_VALUE);
        int j;

        if (jsonobject.has("frames"))
        {
            try
            {
                final JsonArray jsonarray = jsonobject.getAsJsonArray("frames");

                for (j = 0; j < jsonarray.size(); ++j)
                {
                    final JsonElement jsonelement1 = jsonarray.get(j);
                    final AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement1);

                    if (animationframe != null)
                    {
                        arraylist.add(animationframe);
                    }
                }
            }
            catch (final ClassCastException classcastexception)
            {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonobject.get("frames"), classcastexception);
            }
        }

        final int k = this.func_110485_a(jsonobject.get("width"), "width", Integer.valueOf(-1), 1, Integer.MAX_VALUE);
        j = this.func_110485_a(jsonobject.get("height"), "height", Integer.valueOf(-1), 1, Integer.MAX_VALUE);
        return new AnimationMetadataSection(arraylist, k, j, i);
    }

    private AnimationFrame parseAnimationFrame(final int par1, final JsonElement par2JsonElement)
    {
        if (par2JsonElement.isJsonPrimitive())
        {
            try
            {
                return new AnimationFrame(par2JsonElement.getAsInt());
            }
            catch (final NumberFormatException numberformatexception)
            {
                throw new JsonParseException("Invalid animation->frames->" + par1 + ": expected number, was " + par2JsonElement, numberformatexception);
            }
        }
        else if (par2JsonElement.isJsonObject())
        {
            final JsonObject jsonobject = par2JsonElement.getAsJsonObject();
            final int j = this.func_110485_a(jsonobject.get("time"), "frames->" + par1 + "->time", Integer.valueOf(-1), 1, Integer.MAX_VALUE);
            final int k = this.func_110485_a(jsonobject.get("index"), "frames->" + par1 + "->index", (Integer)null, 0, Integer.MAX_VALUE);
            return new AnimationFrame(k, j);
        }
        else
        {
            return null;
        }
    }

    public JsonElement func_110491_a(final AnimationMetadataSection par1AnimationMetadataSection, final Type par2Type, final JsonSerializationContext par3JsonSerializationContext)
    {
        final JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("frametime", Integer.valueOf(par1AnimationMetadataSection.getFrameTime()));

        if (par1AnimationMetadataSection.getFrameWidth() != -1)
        {
            jsonobject.addProperty("width", Integer.valueOf(par1AnimationMetadataSection.getFrameWidth()));
        }

        if (par1AnimationMetadataSection.getFrameHeight() != -1)
        {
            jsonobject.addProperty("height", Integer.valueOf(par1AnimationMetadataSection.getFrameHeight()));
        }

        if (par1AnimationMetadataSection.getFrameCount() > 0)
        {
            final JsonArray jsonarray = new JsonArray();

            for (int i = 0; i < par1AnimationMetadataSection.getFrameCount(); ++i)
            {
                if (par1AnimationMetadataSection.frameHasTime(i))
                {
                    final JsonObject jsonobject1 = new JsonObject();
                    jsonobject1.addProperty("index", Integer.valueOf(par1AnimationMetadataSection.getFrameIndex(i)));
                    jsonobject1.addProperty("time", Integer.valueOf(par1AnimationMetadataSection.getFrameTimeSingle(i)));
                    jsonarray.add(jsonobject1);
                }
                else
                {
                    jsonarray.add(new JsonPrimitive(Integer.valueOf(par1AnimationMetadataSection.getFrameIndex(i))));
                }
            }

            jsonobject.add("frames", jsonarray);
        }

        return jsonobject;
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "animation";
    }

    public Object deserialize(final JsonElement par1JsonElement, final Type par2Type, final JsonDeserializationContext par3JsonDeserializationContext)
    {
        return this.func_110493_a(par1JsonElement, par2Type, par3JsonDeserializationContext);
    }

    public JsonElement serialize(final Object par1Obj, final Type par2Type, final JsonSerializationContext par3JsonSerializationContext)
    {
        return this.func_110491_a((AnimationMetadataSection)par1Obj, par2Type, par3JsonSerializationContext);
    }
}
