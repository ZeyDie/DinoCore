package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SideOnly(Side.CLIENT)
public class FileResourcePack extends AbstractResourcePack implements Closeable
{
    public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
    private ZipFile resourcePackZipFile;

    public FileResourcePack(final File par1File)
    {
        super(par1File);
    }

    private ZipFile getResourcePackZipFile() throws IOException
    {
        if (this.resourcePackZipFile == null)
        {
            this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
        }

        return this.resourcePackZipFile;
    }

    protected InputStream getInputStreamByName(final String par1Str) throws IOException
    {
        final ZipFile zipfile = this.getResourcePackZipFile();
        final ZipEntry zipentry = zipfile.getEntry(par1Str);

        if (zipentry == null)
        {
            throw new ResourcePackFileNotFoundException(this.resourcePackFile, par1Str);
        }
        else
        {
            return zipfile.getInputStream(zipentry);
        }
    }

    public boolean hasResourceName(final String par1Str)
    {
        try
        {
            return this.getResourcePackZipFile().getEntry(par1Str) != null;
        }
        catch (final IOException ioexception)
        {
            return false;
        }
    }

    public Set getResourceDomains()
    {
        final ZipFile zipfile;

        try
        {
            zipfile = this.getResourcePackZipFile();
        }
        catch (final IOException ioexception)
        {
            return Collections.emptySet();
        }

        final Enumeration enumeration = zipfile.entries();
        final HashSet hashset = Sets.newHashSet();

        while (enumeration.hasMoreElements())
        {
            final ZipEntry zipentry = (ZipEntry)enumeration.nextElement();
            final String s = zipentry.getName();

            if (s.startsWith("assets/"))
            {
                final ArrayList arraylist = Lists.newArrayList(entryNameSplitter.split(s));

                if (arraylist.size() > 1)
                {
                    final String s1 = (String)arraylist.get(1);

                    if (!s1.equals(s1.toLowerCase()))
                    {
                        this.logNameNotLowercase(s1);
                    }
                    else
                    {
                        hashset.add(s1);
                    }
                }
            }
        }

        return hashset;
    }

    protected void finalize()
    {
        this.close();

        try
        {
            super.finalize();
        }
        catch (final Throwable t)
        {
        }
    }

    public void close()
    {
        if (this.resourcePackZipFile != null)
        {
            try
            {
                this.resourcePackZipFile.close();
            }
            catch (final Exception ex)
            {
            }

            this.resourcePackZipFile = null;
        }
    }
}
