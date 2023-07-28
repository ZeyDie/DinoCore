package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class FallbackResourceManager implements ResourceManager
{
    protected final List resourcePacks = new ArrayList();
    private final MetadataSerializer frmMetadataSerializer;

    public FallbackResourceManager(final MetadataSerializer par1MetadataSerializer)
    {
        this.frmMetadataSerializer = par1MetadataSerializer;
    }

    public void addResourcePack(final ResourcePack par1ResourcePack)
    {
        this.resourcePacks.add(par1ResourcePack);
    }

    public Set getResourceDomains()
    {
        return null;
    }

    public Resource getResource(final ResourceLocation par1ResourceLocation) throws IOException
    {
        ResourcePack resourcepack = null;
        final ResourceLocation resourcelocation1 = getLocationMcmeta(par1ResourceLocation);

        for (int i = this.resourcePacks.size() - 1; i >= 0; --i)
        {
            final ResourcePack resourcepack1 = (ResourcePack)this.resourcePacks.get(i);

            if (resourcepack == null && resourcepack1.resourceExists(resourcelocation1))
            {
                resourcepack = resourcepack1;
            }

            if (resourcepack1.resourceExists(par1ResourceLocation))
            {
                InputStream inputstream = null;

                if (resourcepack != null)
                {
                    inputstream = resourcepack.getInputStream(resourcelocation1);
                }

                return new SimpleResource(par1ResourceLocation, resourcepack1.getInputStream(par1ResourceLocation), inputstream, this.frmMetadataSerializer);
            }
        }

        throw new FileNotFoundException(par1ResourceLocation.toString());
    }

    public List getAllResources(final ResourceLocation par1ResourceLocation) throws IOException
    {
        final ArrayList arraylist = Lists.newArrayList();
        final ResourceLocation resourcelocation1 = getLocationMcmeta(par1ResourceLocation);
        final Iterator iterator = this.resourcePacks.iterator();

        while (iterator.hasNext())
        {
            final ResourcePack resourcepack = (ResourcePack)iterator.next();

            if (resourcepack.resourceExists(par1ResourceLocation))
            {
                final InputStream inputstream = resourcepack.resourceExists(resourcelocation1) ? resourcepack.getInputStream(resourcelocation1) : null;
                arraylist.add(new SimpleResource(par1ResourceLocation, resourcepack.getInputStream(par1ResourceLocation), inputstream, this.frmMetadataSerializer));
            }
        }

        if (arraylist.isEmpty())
        {
            throw new FileNotFoundException(par1ResourceLocation.toString());
        }
        else
        {
            return arraylist;
        }
    }

    static ResourceLocation getLocationMcmeta(final ResourceLocation par0ResourceLocation)
    {
        return new ResourceLocation(par0ResourceLocation.getResourceDomain(), par0ResourceLocation.getResourcePath() + ".mcmeta");
    }
}
