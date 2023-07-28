package net.minecraft.world.storage;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;

import java.io.*;
import java.util.UUID;

// CraftBukkit start
// CraftBukkit end

public class SaveHandler implements ISaveHandler, IPlayerFileData
{
    /** The directory in which to save world data. */
    private final File worldDirectory;

    /** The directory in which to save player data. */
    private final File playersDirectory;
    private final File mapDataDir;

    /**
     * The time in milliseconds when this field was initialized. Stored in the session lock file.
     */
    private final long initializationTime = MinecraftServer.getSystemTimeMillis();

    /** The directory name of the world */
    private final String saveDirectoryName;
    private UUID uuid = null; // CraftBukkit

    public SaveHandler(final File par1File, final String par2Str, final boolean par3)
    {
        this.worldDirectory = new File(par1File, par2Str);
        this.worldDirectory.mkdirs();
        this.playersDirectory = new File(this.worldDirectory, "players");
        this.mapDataDir = new File(this.worldDirectory, "data");
        this.mapDataDir.mkdirs();
        this.saveDirectoryName = par2Str;

        if (par3)
        {
            this.playersDirectory.mkdirs();
        }

        this.setSessionLock();
    }

    /**
     * Creates a session lock file for this process
     */
    private void setSessionLock()
    {
        try
        {
            final File file1 = new File(this.worldDirectory, "session.lock");
            final DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));

            try
            {
                dataoutputstream.writeLong(this.initializationTime);
            }
            finally
            {
                dataoutputstream.close();
            }
        }
        catch (final IOException ioexception)
        {
            ioexception.printStackTrace();
            throw new RuntimeException("Failed to check session lock for world " + this.worldDirectory + ", aborting"); // Cauldron
        }
    }

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    public File getWorldDirectory()
    {
        return this.worldDirectory;
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSessionLock() throws MinecraftException
    {
        try
        {
            final File file1 = new File(this.worldDirectory, "session.lock");
            final DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));

            try
            {
                if (datainputstream.readLong() != this.initializationTime)
                {
                    throw new MinecraftException("The save folder for world " + this.worldDirectory + " is being accessed from another location, aborting"); // Cauldron
                }
            }
            finally
            {
                datainputstream.close();
            }
        }
        catch (final IOException ioexception)
        {
            // Cauldron start
            ioexception.printStackTrace();
            throw new MinecraftException("Failed to check session lock for world " + this.worldDirectory + ", aborting");
            // Cauldron end
        }
    }

    /**
     * Returns the chunk loader with the provided world provider
     */
    public IChunkLoader getChunkLoader(final WorldProvider par1WorldProvider)
    {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    /**
     * Loads and returns the world info
     */
    public WorldInfo loadWorldInfo()
    {
        File file1 = new File(this.worldDirectory, "level.dat");
        NBTTagCompound nbttagcompound;
        NBTTagCompound nbttagcompound1;

        WorldInfo worldInfo = null;

        if (file1.exists())
        {
            try
            {
                nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                worldInfo = new WorldInfo(nbttagcompound1);
                FMLCommonHandler.instance().handleWorldDataLoad(this, worldInfo, nbttagcompound);
                return worldInfo;
            }
            catch (final Exception exception)
            {
                if (FMLCommonHandler.instance().shouldServerBeKilledQuietly())
                {
                    throw (RuntimeException)exception;
                }
                exception.printStackTrace();
            }
        }

        file1 = new File(this.worldDirectory, "level.dat_old");

        if (file1.exists())
        {
            try
            {
                nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                worldInfo = new WorldInfo(nbttagcompound1);
                FMLCommonHandler.instance().handleWorldDataLoad(this, worldInfo, nbttagcompound);
                return worldInfo;
            }
            catch (final Exception exception1)
            {
                exception1.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(final WorldInfo par1WorldInfo, final NBTTagCompound par2NBTTagCompound)
    {
        final NBTTagCompound nbttagcompound1 = par1WorldInfo.cloneNBTCompound(par2NBTTagCompound);
        final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        nbttagcompound2.setTag("Data", nbttagcompound1);

        FMLCommonHandler.instance().handleWorldDataSave(this, par1WorldInfo, nbttagcompound2);

        try
        {
            final File file1 = new File(this.worldDirectory, "level.dat_new");
            final File file2 = new File(this.worldDirectory, "level.dat_old");
            final File file3 = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(nbttagcompound2, new FileOutputStream(file1));

            if (file2.exists())
            {
                file2.delete();
            }

            file3.renameTo(file2);

            if (file3.exists())
            {
                file3.delete();
            }

            file1.renameTo(file3);

            if (file1.exists())
            {
                file1.delete();
            }
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Saves the passed in world info.
     */
    public void saveWorldInfo(final WorldInfo par1WorldInfo)
    {
        final NBTTagCompound nbttagcompound = par1WorldInfo.getNBTTagCompound();
        final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setTag("Data", nbttagcompound);

        FMLCommonHandler.instance().handleWorldDataSave(this, par1WorldInfo, nbttagcompound1);

        try
        {
            final File file1 = new File(this.worldDirectory, "level.dat_new");
            final File file2 = new File(this.worldDirectory, "level.dat_old");
            final File file3 = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(nbttagcompound1, new FileOutputStream(file1));

            if (file2.exists())
            {
                file2.delete();
            }

            file3.renameTo(file2);

            if (file3.exists())
            {
                file3.delete();
            }

            file1.renameTo(file3);

            if (file1.exists())
            {
                file1.delete();
            }
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Writes the player data to disk from the specified PlayerEntityMP.
     */
    public void writePlayerData(final EntityPlayer par1EntityPlayer)
    {
        try
        {
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            par1EntityPlayer.writeToNBT(nbttagcompound);
            final File file1 = new File(this.playersDirectory, par1EntityPlayer.getCommandSenderName() + ".dat.tmp");
            final File file2 = new File(this.playersDirectory, par1EntityPlayer.getCommandSenderName() + ".dat");
            CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file1));

            if (file2.exists())
            {
                file2.delete();
            }

            file1.renameTo(file2);
        }
        catch (final Exception exception)
        {
            MinecraftServer.getServer().getLogAgent().logWarning("Failed to save player data for " + par1EntityPlayer.getCommandSenderName());
        }
    }

    /**
     * Reads the player data from disk into the specified PlayerEntityMP.
     */
    public NBTTagCompound readPlayerData(final EntityPlayer par1EntityPlayer)
    {
        final NBTTagCompound nbttagcompound = this.getPlayerData(par1EntityPlayer.getCommandSenderName());

        if (nbttagcompound != null)
        {
            // CraftBukkit start
            if (par1EntityPlayer instanceof EntityPlayerMP)
            {
                final CraftPlayer player = (CraftPlayer) par1EntityPlayer.getBukkitEntity(); // Cauldron - make sure we set our bukkitEntity to avoid null
                player.setFirstPlayed(new File(playersDirectory, par1EntityPlayer.username + ".dat").lastModified());
            }

            // CraftBukkit end
            par1EntityPlayer.readFromNBT(nbttagcompound);
        }

        return nbttagcompound;
    }

    /**
     * Gets the player data for the given playername as a NBTTagCompound.
     */
    public NBTTagCompound getPlayerData(final String par1Str)
    {
        try
        {
            final File file1 = new File(this.playersDirectory, par1Str + ".dat");

            if (file1.exists())
            {
                return CompressedStreamTools.readCompressed(new FileInputStream(file1));
            }
        }
        catch (final Exception exception)
        {
            MinecraftServer.getServer().getLogAgent().logWarning("Failed to load player data for " + par1Str);
        }

        return null;
    }

    /**
     * returns null if no saveHandler is relevent (eg. SMP)
     */
    public IPlayerFileData getSaveHandler()
    {
        return this;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat()
    {
        final String[] astring = this.playersDirectory.list();

        for (int i = 0; i < astring.length; ++i)
        {
            if (astring[i].endsWith(".dat"))
            {
                astring[i] = astring[i].substring(0, astring[i].length() - 4);
            }
        }

        return astring;
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush() {}

    /**
     * Gets the file location of the given map
     */
    public File getMapFileFromName(final String par1Str)
    {
        return new File(this.mapDataDir, par1Str + ".dat");
    }

    /**
     * Returns the name of the directory where world information is saved.
     */
    public String getWorldDirectoryName()
    {
        return this.saveDirectoryName;
    }

    // CraftBukkit start
    public UUID getUUID()
    {
        if (uuid != null)
        {
            return uuid;
        }

        final File file1 = new File(this.worldDirectory, "uid.dat");

        if (file1.exists())
        {
            DataInputStream dis = null;

            try
            {
                dis = new DataInputStream(new FileInputStream(file1));
                return uuid = new UUID(dis.readLong(), dis.readLong());
            }
            catch (final IOException ex)
            {
                MinecraftServer.getServer().getLogAgent().logSevereException("Failed to read " + file1 + ", generating new random UUID", ex);
            }
            finally
            {
                if (dis != null)
                {
                    try
                    {
                        dis.close();
                    }
                    catch (final IOException ex)
                    {
                        // NOOP
                    }
                }
            }
        }

        uuid = UUID.randomUUID();
        DataOutputStream dos = null;

        try
        {
            dos = new DataOutputStream(new FileOutputStream(file1));
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
        }
        catch (final IOException ex)
        {
            MinecraftServer.getServer().getLogAgent().logSevereException("Failed to write " + file1, ex);
        }
        finally
        {
            if (dos != null)
            {
                try
                {
                    dos.close();
                }
                catch (final IOException ex)
                {
                    // NOOP
                }
            }
        }

        return uuid;
    }

    public File getPlayerDir()
    {
        return playersDirectory;
    }
    // CraftBukkit end
}
