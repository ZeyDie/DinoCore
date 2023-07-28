package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache
{
    /** A map containing Files as keys and RegionFiles as values */
    public static final Map regionsByFilename = new HashMap(); // CraftBukkit - private -> public

    public static synchronized RegionFile createOrLoadRegionFile(final File par0File, final int par1, final int par2)
    {
        final File file2 = new File(par0File, "region");
        final File file3 = new File(file2, "r." + (par1 >> 5) + "." + (par2 >> 5) + ".mca");
        final RegionFile regionfile = (RegionFile)regionsByFilename.get(file3);

        if (regionfile != null)
        {
            return regionfile;
        }
        else
        {
            if (!file2.exists())
            {
                file2.mkdirs();
            }

            if (regionsByFilename.size() >= 256)
            {
                clearRegionFileReferences();
            }

            final RegionFile regionfile1 = new RegionFile(file3);
            regionsByFilename.put(file3, regionfile1);
            return regionfile1;
        }
    }

    /**
     * Saves the current Chunk Map Cache
     */
    public static synchronized void clearRegionFileReferences()
    {
        final Iterator iterator = regionsByFilename.values().iterator();

        while (iterator.hasNext())
        {
            final RegionFile regionfile = (RegionFile)iterator.next();

            try
            {
                if (regionfile != null)
                {
                    regionfile.close();
                }
            }
            catch (final IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }

        regionsByFilename.clear();
    }

    /**
     * Returns an input stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataInputStream getChunkInputStream(final File par0File, final int par1, final int par2)
    {
        final RegionFile regionfile = createOrLoadRegionFile(par0File, par1, par2);
        return regionfile.getChunkDataInputStream(par1 & 31, par2 & 31);
    }

    /**
     * Returns an output stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataOutputStream getChunkOutputStream(final File par0File, final int par1, final int par2)
    {
        final RegionFile regionfile = createOrLoadRegionFile(par0File, par1, par2);
        return regionfile.getChunkDataOutputStream(par1 & 31, par2 & 31);
    }
}
