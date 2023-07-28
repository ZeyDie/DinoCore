package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;

public class AnvilSaveHandler extends SaveHandler
{
    public AnvilSaveHandler(final File par1File, final String par2Str, final boolean par3)
    {
        super(par1File, par2Str, par3);
    }

    /**
     * Returns the chunk loader with the provided world provider
     */
    public IChunkLoader getChunkLoader(final WorldProvider par1WorldProvider)
    {
        final File file1 = this.getWorldDirectory();
        File file2;
        // Cauldron start
        // To workaround the issue of Bukkit relying on every world having a seperate container
        // we won't be generating a DIMXX folder for chunk loaders since this name is already generated
        // for the world container with provider.getSaveFolder().
        return new AnvilChunkLoader(file1);
        // Cauldron end
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(final WorldInfo par1WorldInfo, final NBTTagCompound par2NBTTagCompound)
    {
        par1WorldInfo.setSaveVersion(19133);
        super.saveWorldInfoWithPlayer(par1WorldInfo, par2NBTTagCompound);
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush()
    {
        try
        {
            ThreadedFileIOBase.threadedIOInstance.waitForFinish();
        }
        catch (final InterruptedException interruptedexception)
        {
            interruptedexception.printStackTrace();
        }

        RegionFileCache.clearRegionFileReferences();
    }
}
