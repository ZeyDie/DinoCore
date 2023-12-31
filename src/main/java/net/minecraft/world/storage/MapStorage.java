package net.minecraft.world.storage;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

import java.io.*;
import java.util.*;

public class MapStorage
{
    private ISaveHandler saveHandler;

    /** Map of item data String id to loaded MapDataBases */
    private Map loadedDataMap = new HashMap();

    /** List of loaded MapDataBases. */
    private List loadedDataList = new ArrayList();

    /**
     * Map of MapDataBase id String prefixes ('map' etc) to max known unique Short id (the 0 part etc) for that prefix
     */
    private Map idCounts = new HashMap();

    public MapStorage(final ISaveHandler par1ISaveHandler)
    {
        this.saveHandler = par1ISaveHandler;
        this.loadIdCounts();
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk, instantiating the given Class, or
     * returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadData(final Class par1Class, final String par2Str)
    {
        WorldSavedData worldsaveddata = (WorldSavedData)this.loadedDataMap.get(par2Str);

        if (worldsaveddata != null)
        {
            return worldsaveddata;
        }
        else
        {
            if (this.saveHandler != null)
            {
                try
                {
                    final File file1 = this.saveHandler.getMapFileFromName(par2Str);

                    if (file1 != null && file1.exists())
                    {
                        try
                        {
                            worldsaveddata = (WorldSavedData)par1Class.getConstructor(new Class[] {String.class}).newInstance(new Object[] {par2Str});
                        }
                        catch (final Exception exception)
                        {
                            throw new RuntimeException("Failed to instantiate " + par1Class.toString(), exception);
                        }

                        final FileInputStream fileinputstream = new FileInputStream(file1);
                        final NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
                        fileinputstream.close();
                        worldsaveddata.readFromNBT(nbttagcompound.getCompoundTag("data"));
                    }
                }
                catch (final Exception exception1)
                {
                    exception1.printStackTrace();
                }
            }

            if (worldsaveddata != null)
            {
                this.loadedDataMap.put(par2Str, worldsaveddata);
                this.loadedDataList.add(worldsaveddata);
            }

            return worldsaveddata;
        }
    }

    /**
     * Assigns the given String id to the given MapDataBase, removing any existing ones of the same id.
     */
    public void setData(final String par1Str, final WorldSavedData par2WorldSavedData)
    {
        if (par2WorldSavedData == null)
        {
            throw new RuntimeException("Can\'t set null data");
        }
        else
        {
            if (this.loadedDataMap.containsKey(par1Str))
            {
                this.loadedDataList.remove(this.loadedDataMap.remove(par1Str));
            }

            this.loadedDataMap.put(par1Str, par2WorldSavedData);
            this.loadedDataList.add(par2WorldSavedData);
        }
    }

    /**
     * Saves all dirty loaded MapDataBases to disk.
     */
    public void saveAllData()
    {
        for (int i = 0; i < this.loadedDataList.size(); ++i)
        {
            final WorldSavedData worldsaveddata = (WorldSavedData)this.loadedDataList.get(i);

            if (worldsaveddata.isDirty())
            {
                this.saveData(worldsaveddata);
                worldsaveddata.setDirty(false);
            }
        }
    }

    /**
     * Saves the given MapDataBase to disk.
     */
    private void saveData(final WorldSavedData par1WorldSavedData)
    {
        if (this.saveHandler != null)
        {
            try
            {
                final File file1 = this.saveHandler.getMapFileFromName(par1WorldSavedData.mapName);

                if (file1 != null)
                {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    par1WorldSavedData.writeToNBT(nbttagcompound);
                    final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setCompoundTag("data", nbttagcompound);
                    final FileOutputStream fileoutputstream = new FileOutputStream(file1);
                    CompressedStreamTools.writeCompressed(nbttagcompound1, fileoutputstream);
                    fileoutputstream.close();
                }
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Loads the idCounts Map from the 'idcounts' file.
     */
    private void loadIdCounts()
    {
        try
        {
            this.idCounts.clear();

            if (this.saveHandler == null)
            {
                return;
            }

            final File file1 = this.saveHandler.getMapFileFromName("idcounts");

            if (file1 != null && file1.exists())
            {
                final DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
                final NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                datainputstream.close();
                final Iterator iterator = nbttagcompound.getTags().iterator();

                while (iterator.hasNext())
                {
                    final NBTBase nbtbase = (NBTBase)iterator.next();

                    if (nbtbase instanceof NBTTagShort)
                    {
                        final NBTTagShort nbttagshort = (NBTTagShort)nbtbase;
                        final String s = nbttagshort.getName();
                        final short short1 = nbttagshort.data;
                        this.idCounts.put(s, Short.valueOf(short1));
                    }
                }
            }
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Returns an unique new data id for the given prefix and saves the idCounts map to the 'idcounts' file.
     */
    public int getUniqueDataId(final String par1Str)
    {
        Short oshort = (Short)this.idCounts.get(par1Str);

        if (oshort == null)
        {
            oshort = Short.valueOf((short)0);
        }
        else
        {
            oshort = Short.valueOf((short)(oshort.shortValue() + 1));
        }

        this.idCounts.put(par1Str, oshort);

        if (this.saveHandler == null)
        {
            return oshort.shortValue();
        }
        else
        {
            try
            {
                final File file1 = this.saveHandler.getMapFileFromName("idcounts");

                if (file1 != null)
                {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    final Iterator iterator = this.idCounts.keySet().iterator();

                    while (iterator.hasNext())
                    {
                        final String s1 = (String)iterator.next();
                        final short short1 = ((Short)this.idCounts.get(s1)).shortValue();
                        nbttagcompound.setShort(s1, short1);
                    }

                    final DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
                    CompressedStreamTools.write(nbttagcompound, dataoutputstream);
                    dataoutputstream.close();
                }
            }
            catch (final Exception exception)
            {
                exception.printStackTrace();
            }

            return oshort.shortValue();
        }
    }
}
