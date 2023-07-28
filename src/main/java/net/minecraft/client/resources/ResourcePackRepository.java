package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.GameSettings;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

@SideOnly(Side.CLIENT)
public class ResourcePackRepository
{
    protected static final FileFilter resourcePackFilter = new ResourcePackRepositoryFilter();
    private final File dirResourcepacks;
    public final ResourcePack rprDefaultResourcePack;
    public final MetadataSerializer rprMetadataSerializer;
    private List repositoryEntriesAll = Lists.newArrayList();
    private List repositoryEntries = Lists.newArrayList();

    public ResourcePackRepository(final File par1File, final ResourcePack par2ResourcePack, final MetadataSerializer par3MetadataSerializer, final GameSettings par4GameSettings)
    {
        this.dirResourcepacks = par1File;
        this.rprDefaultResourcePack = par2ResourcePack;
        this.rprMetadataSerializer = par3MetadataSerializer;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        final Iterator iterator = this.repositoryEntriesAll.iterator();

        while (iterator.hasNext())
        {
            final ResourcePackRepositoryEntry resourcepackrepositoryentry = (ResourcePackRepositoryEntry)iterator.next();

            if (resourcepackrepositoryentry.getResourcePackName().equals(par4GameSettings.skin))
            {
                this.repositoryEntries.add(resourcepackrepositoryentry);
            }
        }
    }

    private void fixDirResourcepacks()
    {
        if (!this.dirResourcepacks.isDirectory())
        {
            this.dirResourcepacks.delete();
            this.dirResourcepacks.mkdirs();
        }
    }

    private List getResourcePackFiles()
    {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll()
    {
        final ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.getResourcePackFiles().iterator();

        while (iterator.hasNext())
        {
            final File file1 = (File)iterator.next();
            final ResourcePackRepositoryEntry resourcepackrepositoryentry = new ResourcePackRepositoryEntry(this, file1, (ResourcePackRepositoryFilter)null);

            if (!this.repositoryEntriesAll.contains(resourcepackrepositoryentry))
            {
                try
                {
                    resourcepackrepositoryentry.updateResourcePack();
                    arraylist.add(resourcepackrepositoryentry);
                }
                catch (final Exception exception)
                {
                    arraylist.remove(resourcepackrepositoryentry);
                }
            }
            else
            {
                arraylist.add(this.repositoryEntriesAll.get(this.repositoryEntriesAll.indexOf(resourcepackrepositoryentry)));
            }
        }

        this.repositoryEntriesAll.removeAll(arraylist);
        iterator = this.repositoryEntriesAll.iterator();

        while (iterator.hasNext())
        {
            final ResourcePackRepositoryEntry resourcepackrepositoryentry1 = (ResourcePackRepositoryEntry)iterator.next();
            resourcepackrepositoryentry1.closeResourcePack();
        }

        this.repositoryEntriesAll = arraylist;
    }

    public List getRepositoryEntriesAll()
    {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List getRepositoryEntries()
    {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public String getResourcePackName()
    {
        return this.repositoryEntries.isEmpty() ? "Default" : ((ResourcePackRepositoryEntry)this.repositoryEntries.get(0)).getResourcePackName();
    }

    public void setRepositoryEntries(final ResourcePackRepositoryEntry ... par1ArrayOfResourcePackRepositoryEntry)
    {
        this.repositoryEntries.clear();
        Collections.addAll(this.repositoryEntries, par1ArrayOfResourcePackRepositoryEntry);
    }

    public File getDirResourcepacks()
    {
        return this.dirResourcepacks;
    }
}
