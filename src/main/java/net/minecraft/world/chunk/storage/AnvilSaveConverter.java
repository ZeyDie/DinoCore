package net.minecraft.world.chunk.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AnvilSaveConverter extends SaveFormatOld
{
    public AnvilSaveConverter(final File par1File)
    {
        super(par1File);
    }

    @SideOnly(Side.CLIENT)
    public List getSaveList() throws AnvilConverterException
    {
        if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory())
        {
            final ArrayList arraylist = new ArrayList();
            final File[] afile = this.savesDirectory.listFiles();
            final File[] afile1 = afile;
            final int i = afile.length;

            for (int j = 0; j < i; ++j)
            {
                final File file1 = afile1[j];

                if (file1.isDirectory())
                {
                    final String s = file1.getName();
                    final WorldInfo worldinfo = this.getWorldInfo(s);

                    if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133))
                    {
                        final boolean flag = worldinfo.getSaveVersion() != this.getSaveVersion();
                        String s1 = worldinfo.getWorldName();

                        if (s1 == null || MathHelper.stringNullOrLengthZero(s1))
                        {
                            s1 = s;
                        }

                        final long k = 0L;
                        arraylist.add(new SaveFormatComparator(s, s1, worldinfo.getLastTimePlayed(), k, worldinfo.getGameType(), flag, worldinfo.isHardcoreModeEnabled(), worldinfo.areCommandsAllowed()));
                    }
                }
            }

            return arraylist;
        }
        else
        {
            throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
        }
    }

    protected int getSaveVersion()
    {
        return 19133;
    }

    public void flushCache()
    {
        RegionFileCache.clearRegionFileReferences();
    }

    /**
     * Returns back a loader for the specified save directory
     */
    public ISaveHandler getSaveLoader(final String par1Str, final boolean par2)
    {
        return new AnvilSaveHandler(this.savesDirectory, par1Str, par2);
    }

    /**
     * Checks if the save directory uses the old map format
     */
    public boolean isOldMapFormat(final String par1Str)
    {
        final WorldInfo worldinfo = this.getWorldInfo(par1Str);
        return worldinfo != null && worldinfo.getSaveVersion() != this.getSaveVersion();
    }

    /**
     * Converts the specified map to the new map format. Args: worldName, loadingScreen
     */
    public boolean convertMapFormat(final String par1Str, final IProgressUpdate par2IProgressUpdate)
    {
        par2IProgressUpdate.setLoadingProgress(0);
        final ArrayList arraylist = new ArrayList();
        final ArrayList arraylist1 = new ArrayList();
        final ArrayList arraylist2 = new ArrayList();
        final File file1 = new File(this.savesDirectory, par1Str);
        final File file2 = new File(file1, "DIM-1");
        final File file3 = new File(file1, "DIM1");
        MinecraftServer.getServer().getLogAgent().logInfo("Scanning folders...");
        this.addRegionFilesToCollection(file1, arraylist);

        if (file2.exists())
        {
            this.addRegionFilesToCollection(file2, arraylist1);
        }

        if (file3.exists())
        {
            this.addRegionFilesToCollection(file3, arraylist2);
        }

        final int i = arraylist.size() + arraylist1.size() + arraylist2.size();
        MinecraftServer.getServer().getLogAgent().logInfo("Total conversion count is " + i);
        final WorldInfo worldinfo = this.getWorldInfo(par1Str);
        Object object = null;

        if (worldinfo.getTerrainType() == WorldType.FLAT)
        {
            object = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F, 0.5F);
        }
        else
        {
            object = new WorldChunkManager(worldinfo.getSeed(), worldinfo.getTerrainType());
        }

        this.convertFile(new File(file1, "region"), arraylist, (WorldChunkManager)object, 0, i, par2IProgressUpdate);
        this.convertFile(new File(file2, "region"), arraylist1, new WorldChunkManagerHell(BiomeGenBase.hell, 1.0F, 0.0F), arraylist.size(), i, par2IProgressUpdate);
        this.convertFile(new File(file3, "region"), arraylist2, new WorldChunkManagerHell(BiomeGenBase.sky, 0.5F, 0.0F), arraylist.size() + arraylist1.size(), i, par2IProgressUpdate);
        worldinfo.setSaveVersion(19133);

        if (worldinfo.getTerrainType() == WorldType.DEFAULT_1_1)
        {
            worldinfo.setTerrainType(WorldType.DEFAULT);
        }

        this.createFile(par1Str);
        final ISaveHandler isavehandler = this.getSaveLoader(par1Str, false);
        isavehandler.saveWorldInfo(worldinfo);
        return true;
    }

    /**
     * par: filename for the level.dat_mcr backup
     */
    private void createFile(final String par1Str)
    {
        final File file1 = new File(this.savesDirectory, par1Str);

        if (!file1.exists())
        {
            System.out.println("Warning: Unable to create level.dat_mcr backup");
        }
        else
        {
            final File file2 = new File(file1, "level.dat");

            if (!file2.exists())
            {
                System.out.println("Warning: Unable to create level.dat_mcr backup");
            }
            else
            {
                final File file3 = new File(file1, "level.dat_mcr");

                if (!file2.renameTo(file3))
                {
                    System.out.println("Warning: Unable to create level.dat_mcr backup");
                }
            }
        }
    }

    private void convertFile(final File par1File, final Iterable par2Iterable, final WorldChunkManager par3WorldChunkManager, int par4, final int par5, final IProgressUpdate par6IProgressUpdate)
    {
        int par41 = par4;
        final Iterator iterator = par2Iterable.iterator();

        while (iterator.hasNext())
        {
            final File file2 = (File)iterator.next();
            this.convertChunks(par1File, file2, par3WorldChunkManager, par41, par5, par6IProgressUpdate);
            ++par41;
            final int k = (int)Math.round(100.0D * (double) par41 / (double)par5);
            par6IProgressUpdate.setLoadingProgress(k);
        }
    }

    /**
     * copies a 32x32 chunk set from par2File to par1File, via AnvilConverterData
     */
    private void convertChunks(final File par1File, final File par2File, final WorldChunkManager par3WorldChunkManager, final int par4, final int par5, final IProgressUpdate par6IProgressUpdate)
    {
        try
        {
            final String s = par2File.getName();
            final RegionFile regionfile = new RegionFile(par2File);
            final RegionFile regionfile1 = new RegionFile(new File(par1File, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

            for (int k = 0; k < 32; ++k)
            {
                int l;

                for (l = 0; l < 32; ++l)
                {
                    if (regionfile.isChunkSaved(k, l) && !regionfile1.isChunkSaved(k, l))
                    {
                        final DataInputStream datainputstream = regionfile.getChunkDataInputStream(k, l);

                        if (datainputstream == null)
                        {
                            MinecraftServer.getServer().getLogAgent().logWarning("Failed to fetch input stream");
                        }
                        else
                        {
                            final NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                            datainputstream.close();
                            final NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Level");
                            final AnvilConverterData anvilconverterdata = ChunkLoader.load(nbttagcompound1);
                            final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                            final NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                            nbttagcompound2.setTag("Level", nbttagcompound3);
                            ChunkLoader.convertToAnvilFormat(anvilconverterdata, nbttagcompound3, par3WorldChunkManager);
                            final DataOutputStream dataoutputstream = regionfile1.getChunkDataOutputStream(k, l);
                            CompressedStreamTools.write(nbttagcompound2, dataoutputstream);
                            dataoutputstream.close();
                        }
                    }
                }

                l = (int)Math.round(100.0D * (double)(par4 * 1024) / (double)(par5 * 1024));
                final int i1 = (int)Math.round(100.0D * (double)((k + 1) * 32 + par4 * 1024) / (double)(par5 * 1024));

                if (i1 > l)
                {
                    par6IProgressUpdate.setLoadingProgress(i1);
                }
            }

            regionfile.close();
            regionfile1.close();
        }
        catch (final IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    /**
     * filters the files in the par1 directory, and adds them to the par2 collections
     */
    private void addRegionFilesToCollection(final File par1File, final Collection par2Collection)
    {
        final File file2 = new File(par1File, "region");
        final File[] afile = file2.listFiles(new AnvilSaveConverterFileFilter(this));

        if (afile != null)
        {
            Collections.addAll(par2Collection, afile);
        }
    }
}
