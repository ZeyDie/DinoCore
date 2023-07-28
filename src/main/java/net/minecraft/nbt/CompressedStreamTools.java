package net.minecraft.nbt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools
{
    /**
     * Load the gzipped compound from the inputstream.
     */
    public static NBTTagCompound readCompressed(final InputStream par0InputStream) throws IOException
    {
        final DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(par0InputStream)));
        NBTTagCompound nbttagcompound;

        try
        {
            nbttagcompound = read(datainputstream);
        }
        finally
        {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    /**
     * Write the compound, gzipped, to the outputstream.
     */
    public static void writeCompressed(final NBTTagCompound par0NBTTagCompound, final OutputStream par1OutputStream) throws IOException
    {
        final DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(par1OutputStream));

        try
        {
            write(par0NBTTagCompound, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }
    }

    public static NBTTagCompound decompress(final byte[] par0ArrayOfByte) throws IOException
    {
        final DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(par0ArrayOfByte))));
        NBTTagCompound nbttagcompound;

        try
        {
            nbttagcompound = read(datainputstream);
        }
        finally
        {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static byte[] compress(final NBTTagCompound par0NBTTagCompound) throws IOException
    {
        final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        final DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

        try
        {
            write(par0NBTTagCompound, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }

        return bytearrayoutputstream.toByteArray();
    }

    @SideOnly(Side.CLIENT)
    public static void safeWrite(final NBTTagCompound par0NBTTagCompound, final File par1File) throws IOException
    {
        final File file2 = new File(par1File.getAbsolutePath() + "_tmp");

        if (file2.exists())
        {
            file2.delete();
        }

        write(par0NBTTagCompound, file2);

        if (par1File.exists())
        {
            par1File.delete();
        }

        if (par1File.exists())
        {
            throw new IOException("Failed to delete " + par1File);
        }
        else
        {
            file2.renameTo(par1File);
        }
    }

    /**
     * Reads from a CompressedStream.
     */
    public static NBTTagCompound read(final DataInput par0DataInput) throws IOException
    {
        final NBTBase nbtbase = NBTBase.readNamedTag(par0DataInput);

        if (nbtbase instanceof NBTTagCompound)
        {
            return (NBTTagCompound)nbtbase;
        }
        else
        {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(final NBTTagCompound par0NBTTagCompound, final DataOutput par1DataOutput) throws IOException
    {
        NBTBase.writeNamedTag(par0NBTTagCompound, par1DataOutput);
    }

    public static void write(final NBTTagCompound par0NBTTagCompound, final File par1File) throws IOException
    {
        final DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(par1File));

        try
        {
            write(par0NBTTagCompound, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }
    }

    public static NBTTagCompound read(final File par0File) throws IOException
    {
        if (!par0File.exists())
        {
            return null;
        }
        else
        {
            final DataInputStream datainputstream = new DataInputStream(new FileInputStream(par0File));
            NBTTagCompound nbttagcompound;

            try
            {
                nbttagcompound = read(datainputstream);
            }
            finally
            {
                datainputstream.close();
            }

            return nbttagcompound;
        }
    }
}
