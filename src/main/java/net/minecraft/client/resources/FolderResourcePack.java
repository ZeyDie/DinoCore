package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class FolderResourcePack extends AbstractResourcePack
{
    public FolderResourcePack(final File par1File)
    {
        super(par1File);
    }

    protected InputStream getInputStreamByName(final String par1Str) throws IOException
    {
        return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, par1Str)));
    }

    protected boolean hasResourceName(final String par1Str)
    {
        return (new File(this.resourcePackFile, par1Str)).isFile();
    }

    public Set getResourceDomains()
    {
        final HashSet hashset = Sets.newHashSet();
        final File file1 = new File(this.resourcePackFile, "assets/");

        if (file1.isDirectory())
        {
            final File[] afile = file1.listFiles((java.io.FileFilter)DirectoryFileFilter.DIRECTORY);
            final int i = afile.length;

            for (int j = 0; j < i; ++j)
            {
                final File file2 = afile[j];
                final String s = getRelativeName(file1, file2);

                if (!s.equals(s.toLowerCase()))
                {
                    this.logNameNotLowercase(s);
                }
                else
                {
                    hashset.add(s.substring(0, s.length() - 1));
                }
            }
        }

        return hashset;
    }
}
