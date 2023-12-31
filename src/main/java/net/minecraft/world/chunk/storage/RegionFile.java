package net.minecraft.world.chunk.storage;

import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile
{
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private RandomAccessFile dataFile;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private ArrayList sectorFree;

    /** McRegion sizeDelta */
    private int sizeDelta;
    private long lastModified;

    public RegionFile(final File par1File)
    {
        this.fileName = par1File;
        this.sizeDelta = 0;

        try
        {
            if (par1File.exists())
            {
                this.lastModified = par1File.lastModified();
            }

            this.dataFile = new RandomAccessFile(par1File, "rw");
            int i;

            if (this.dataFile.length() < 4096L)
            {
                for (i = 0; i < 1024; ++i)
                {
                    this.dataFile.writeInt(0);
                }

                for (i = 0; i < 1024; ++i)
                {
                    this.dataFile.writeInt(0);
                }

                this.sizeDelta += 8192;
            }

            if ((this.dataFile.length() & 4095L) != 0L)
            {
                for (i = 0; (long)i < (this.dataFile.length() & 4095L); ++i)
                {
                    this.dataFile.write(0);
                }
            }

            i = (int)this.dataFile.length() / 4096;
            this.sectorFree = new ArrayList(i);
            int j;

            for (j = 0; j < i; ++j)
            {
                this.sectorFree.add(Boolean.valueOf(true));
            }

            this.sectorFree.set(0, Boolean.valueOf(false));
            this.sectorFree.set(1, Boolean.valueOf(false));
            this.dataFile.seek(0L);
            int k;

            for (j = 0; j < 1024; ++j)
            {
                k = this.dataFile.readInt();
                this.offsets[j] = k;

                if (k != 0 && (k >> 8) + (k & 255) <= this.sectorFree.size())
                {
                    for (int l = 0; l < (k & 255); ++l)
                    {
                        this.sectorFree.set((k >> 8) + l, Boolean.valueOf(false));
                    }
                }
            }

            for (j = 0; j < 1024; ++j)
            {
                k = this.dataFile.readInt();
                this.chunkTimestamps[j] = k;
            }
        }
        catch (final IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    // CraftBukkit start - This is a copy (sort of) of the method below it, make sure they stay in sync

    public synchronized boolean chunkExists(final int i, final int j)
    {
        if (this.outOfBounds(i, j))
        {
            return false;
        }
        else
        {
            try
            {
                final int k = this.getOffset(i, j);

                if (k == 0)
                {
                    return false;
                }
                else
                {
                    final int l = k >> 8;
                    final int i1 = k & 255;

                    if (l + i1 > this.sectorFree.size())
                    {
                        return false;
                    }

                    this.dataFile.seek((long)(l * 4096));
                    final int j1 = this.dataFile.readInt();

                    if (j1 > 4096 * i1 || j1 <= 0)
                    {
                        return false;
                    }

                    final byte b0 = this.dataFile.readByte();

                    if (b0 == 1 || b0 == 2)
                    {
                        return true;
                    }
                }
            }
            catch (final IOException ioexception)
            {
                return false;
            }
        }

        return false;
    }

    // CraftBukkit end

    /**
     * args: x, y - get uncompressed chunk stream from the region file
     */
    public synchronized DataInputStream getChunkDataInputStream(final int par1, final int par2)
    {
        if (this.outOfBounds(par1, par2))
        {
            return null;
        }
        else
        {
            try
            {
                final int k = this.getOffset(par1, par2);

                if (k == 0)
                {
                    return null;
                }
                else
                {
                    final int l = k >> 8;
                    final int i1 = k & 255;

                    if (l + i1 > this.sectorFree.size())
                    {
                        return null;
                    }
                    else
                    {
                        this.dataFile.seek((long)(l * 4096));
                        final int j1 = this.dataFile.readInt();

                        if (j1 > 4096 * i1)
                        {
                            return null;
                        }
                        else if (j1 <= 0)
                        {
                            return null;
                        }
                        else
                        {
                            final byte b0 = this.dataFile.readByte();
                            final byte[] abyte;

                            if (b0 == 1)
                            {
                                abyte = new byte[j1 - 1];
                                this.dataFile.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
                            }
                            else if (b0 == 2)
                            {
                                abyte = new byte[j1 - 1];
                                this.dataFile.read(abyte);
                                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                            }
                            else
                            {
                                return null;
                            }
                        }
                    }
                }
            }
            catch (final IOException ioexception)
            {
                return null;
            }
        }
    }

    /**
     * args: x, z - get an output stream used to write chunk data, data is on disk when the returned stream is closed
     */
    public DataOutputStream getChunkDataOutputStream(final int par1, final int par2)
    {
        return this.outOfBounds(par1, par2) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFileChunkBuffer(this, par1, par2)));
    }

    /**
     * args: x, z, data, length - write chunk data at (x, z) to disk
     */
    protected synchronized void write(final int par1, final int par2, final byte[] par3ArrayOfByte, final int par4)
    {
        try
        {
            final int l = this.getOffset(par1, par2);
            int i1 = l >> 8;
            final int j1 = l & 255;
            final int k1 = (par4 + 5) / 4096 + 1;

            if (k1 >= 256)
            {
                return;
            }

            if (i1 != 0 && j1 == k1)
            {
                this.write(i1, par3ArrayOfByte, par4);
            }
            else
            {
                int l1;

                for (l1 = 0; l1 < j1; ++l1)
                {
                    this.sectorFree.set(i1 + l1, Boolean.valueOf(true));
                }

                l1 = this.sectorFree.indexOf(Boolean.valueOf(true));
                int i2 = 0;
                int j2;

                if (l1 != -1)
                {
                    for (j2 = l1; j2 < this.sectorFree.size(); ++j2)
                    {
                        if (i2 != 0)
                        {
                            if (((Boolean)this.sectorFree.get(j2)).booleanValue())
                            {
                                ++i2;
                            }
                            else
                            {
                                i2 = 0;
                            }
                        }
                        else if (((Boolean)this.sectorFree.get(j2)).booleanValue())
                        {
                            l1 = j2;
                            i2 = 1;
                        }

                        if (i2 >= k1)
                        {
                            break;
                        }
                    }
                }

                if (i2 >= k1)
                {
                    i1 = l1;
                    this.setOffset(par1, par2, l1 << 8 | k1);

                    for (j2 = 0; j2 < k1; ++j2)
                    {
                        this.sectorFree.set(i1 + j2, Boolean.valueOf(false));
                    }

                    this.write(i1, par3ArrayOfByte, par4);
                }
                else
                {
                    this.dataFile.seek(this.dataFile.length());
                    i1 = this.sectorFree.size();

                    for (j2 = 0; j2 < k1; ++j2)
                    {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(Boolean.valueOf(false));
                    }

                    this.sizeDelta += 4096 * k1;
                    this.write(i1, par3ArrayOfByte, par4);
                    this.setOffset(par1, par2, i1 << 8 | k1);
                }
            }

            this.setChunkTimestamp(par1, par2, (int)(MinecraftServer.getSystemTimeMillis() / 1000L));
        }
        catch (final IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    /**
     * args: sectorNumber, data, length - write the chunk data to this RegionFile
     */
    private void write(final int par1, final byte[] par2ArrayOfByte, final int par3) throws IOException
    {
        this.dataFile.seek((long)(par1 * 4096));
        this.dataFile.writeInt(par3 + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(par2ArrayOfByte, 0, par3);
    }

    /**
     * args: x, z - check region bounds
     */
    private boolean outOfBounds(final int par1, final int par2)
    {
        return par1 < 0 || par1 >= 32 || par2 < 0 || par2 >= 32;
    }

    /**
     * args: x, y - get chunk's offset in region file
     */
    private int getOffset(final int par1, final int par2)
    {
        return this.offsets[par1 + par2 * 32];
    }

    /**
     * args: x, z, - true if chunk has been saved / converted
     */
    public boolean isChunkSaved(final int par1, final int par2)
    {
        return this.getOffset(par1, par2) != 0;
    }

    /**
     * args: x, z, offset - sets the chunk's offset in the region file
     */
    private void setOffset(final int par1, final int par2, final int par3) throws IOException
    {
        this.offsets[par1 + par2 * 32] = par3;
        this.dataFile.seek((long)((par1 + par2 * 32) * 4));
        this.dataFile.writeInt(par3);
    }

    /**
     * args: x, z, timestamp - sets the chunk's write timestamp
     */
    private void setChunkTimestamp(final int par1, final int par2, final int par3) throws IOException
    {
        this.chunkTimestamps[par1 + par2 * 32] = par3;
        this.dataFile.seek((long)(4096 + (par1 + par2 * 32) * 4));
        this.dataFile.writeInt(par3);
    }

    /**
     * close this RegionFile and prevent further writes
     */
    public void close() throws IOException
    {
        if (this.dataFile != null)
        {
            this.dataFile.close();
        }
    }
}
