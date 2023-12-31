package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.dispenser.IRegistry;
import net.minecraft.dispenser.RegistrySimple;

@SideOnly(Side.CLIENT)
public class MetadataSerializer
{
    private final IRegistry metadataSectionSerializerRegistry = new RegistrySimple();
    private final GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Cached Gson instance. Set to null when more sections are registered, and then re-created from the builder.
     */
    private Gson gson;

    public void registerMetadataSectionType(final MetadataSectionSerializer par1MetadataSectionSerializer, final Class par2Class)
    {
        this.metadataSectionSerializerRegistry.putObject(par1MetadataSectionSerializer.getSectionName(), new MetadataSerializerRegistration(this, par1MetadataSectionSerializer, par2Class, (MetadataSerializerEmptyAnon)null));
        this.gsonBuilder.registerTypeAdapter(par2Class, par1MetadataSectionSerializer);
        this.gson = null;
    }

    public MetadataSection parseMetadataSection(final String par1Str, final JsonObject par2JsonObject)
    {
        if (par1Str == null)
        {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        }
        else if (!par2JsonObject.has(par1Str))
        {
            return null;
        }
        else if (!par2JsonObject.get(par1Str).isJsonObject())
        {
            throw new IllegalArgumentException("Invalid metadata for \'" + par1Str + "\' - expected object, found " + par2JsonObject.get(par1Str));
        }
        else
        {
            final MetadataSerializerRegistration metadataserializerregistration = (MetadataSerializerRegistration)this.metadataSectionSerializerRegistry.getObject(par1Str);

            if (metadataserializerregistration == null)
            {
                throw new IllegalArgumentException("Don\'t know how to handle metadata section \'" + par1Str + "\'");
            }
            else
            {
                return (MetadataSection)this.getGson().fromJson(par2JsonObject.getAsJsonObject(par1Str), metadataserializerregistration.field_110500_b);
            }
        }
    }

    /**
     * Returns a Gson instance with type adapters registered for metadata sections.
     */
    private Gson getGson()
    {
        if (this.gson == null)
        {
            this.gson = this.gsonBuilder.create();
        }

        return this.gson;
    }
}
