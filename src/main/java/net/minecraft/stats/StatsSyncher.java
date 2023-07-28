package net.minecraft.stats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Session;

import java.io.*;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class StatsSyncher
{
    private volatile boolean isBusy;
    private volatile Map field_77430_b;
    private volatile Map field_77431_c;

    /**
     * The StatFileWriter object, presumably used to write to the statistics files
     */
    private StatFileWriter statFileWriter;

    /** A file named 'stats_' [lower case username] '_unsent.dat' */
    private File unsentDataFile;

    /** A file named 'stats_' [lower case username] '.dat' */
    private File dataFile;

    /** A file named 'stats_' [lower case username] '_unsent.tmp' */
    private File unsentTempFile;

    /** A file named 'stats_' [lower case username] '.tmp' */
    private File tempFile;

    /** A file named 'stats_' [lower case username] '_unsent.old' */
    private File unsentOldFile;

    /** A file named 'stats_' [lower case username] '.old' */
    private File oldFile;

    /** The Session object */
    private Session theSession;
    private int field_77433_l;
    private int field_77434_m;

    public StatsSyncher(final Session par1Session, final StatFileWriter par2StatFileWriter, final File par3File)
    {
        final String s = par1Session.getUsername();
        final String s1 = s.toLowerCase();
        this.unsentDataFile = new File(par3File, "stats_" + s1 + "_unsent.dat");
        this.dataFile = new File(par3File, "stats_" + s1 + ".dat");
        this.unsentOldFile = new File(par3File, "stats_" + s1 + "_unsent.old");
        this.oldFile = new File(par3File, "stats_" + s1 + ".old");
        this.unsentTempFile = new File(par3File, "stats_" + s1 + "_unsent.tmp");
        this.tempFile = new File(par3File, "stats_" + s1 + ".tmp");

        if (!s1.equals(s))
        {
            this.func_77412_a(par3File, "stats_" + s + "_unsent.dat", this.unsentDataFile);
            this.func_77412_a(par3File, "stats_" + s + ".dat", this.dataFile);
            this.func_77412_a(par3File, "stats_" + s + "_unsent.old", this.unsentOldFile);
            this.func_77412_a(par3File, "stats_" + s + ".old", this.oldFile);
            this.func_77412_a(par3File, "stats_" + s + "_unsent.tmp", this.unsentTempFile);
            this.func_77412_a(par3File, "stats_" + s + ".tmp", this.tempFile);
        }

        this.statFileWriter = par2StatFileWriter;
        this.theSession = par1Session;

        if (this.unsentDataFile.exists())
        {
            par2StatFileWriter.writeStats(this.func_77417_a(this.unsentDataFile, this.unsentTempFile, this.unsentOldFile));
        }

        this.beginReceiveStats();
    }

    private void func_77412_a(final File par1File, final String par2Str, final File par3File)
    {
        final File file3 = new File(par1File, par2Str);

        if (file3.exists() && !file3.isDirectory() && !par3File.exists())
        {
            file3.renameTo(par3File);
        }
    }

    private Map func_77417_a(final File par1File, final File par2File, final File par3File)
    {
        return par1File.exists() ? this.func_77413_a(par1File) : (par3File.exists() ? this.func_77413_a(par3File) : (par2File.exists() ? this.func_77413_a(par2File) : null));
    }

    private Map func_77413_a(final File par1File)
    {
        BufferedReader bufferedreader = null;

        try
        {
            bufferedreader = new BufferedReader(new FileReader(par1File));
            String s = "";
            final StringBuilder stringbuilder = new StringBuilder();

            while ((s = bufferedreader.readLine()) != null)
            {
                stringbuilder.append(s);
            }

            final Map map = StatFileWriter.func_77453_b(stringbuilder.toString());
            return map;
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader.close();
                }
                catch (final Exception exception1)
                {
                    exception1.printStackTrace();
                }
            }
        }

        return null;
    }

    private void func_77421_a(final Map par1Map, final File par2File, final File par3File, final File par4File) throws IOException
    {
        final PrintWriter printwriter = new PrintWriter(new FileWriter(par3File, false));

        try
        {
            printwriter.print(StatFileWriter.func_77441_a(this.theSession.getUsername(), "local", par1Map));
        }
        finally
        {
            printwriter.close();
        }

        if (par4File.exists())
        {
            par4File.delete();
        }

        if (par2File.exists())
        {
            par2File.renameTo(par4File);
        }

        par3File.renameTo(par2File);
    }

    /**
     * Attempts to begin receiving stats from the server. Will throw an IllegalStateException if the syncher is already
     * busy.
     */
    public void beginReceiveStats()
    {
        if (this.isBusy)
        {
            throw new IllegalStateException("Can\'t get stats from server while StatsSyncher is busy!");
        }
        else
        {
            this.field_77433_l = 100;
            this.isBusy = true;
            (new ThreadStatSyncherReceive(this)).start();
        }
    }

    /**
     * Attempts to begin sending stats to the server. Will throw an IllegalStateException if the syncher is already
     * busy.
     */
    public void beginSendStats(final Map par1Map)
    {
        if (this.isBusy)
        {
            throw new IllegalStateException("Can\'t save stats while StatsSyncher is busy!");
        }
        else
        {
            this.field_77433_l = 100;
            this.isBusy = true;
            (new ThreadStatSyncherSend(this, par1Map)).start();
        }
    }

    public void syncStatsFileWithMap(final Map par1Map)
    {
        int i = 30;

        while (this.isBusy)
        {
            --i;

            if (i <= 0)
            {
                break;
            }

            try
            {
                Thread.sleep(100L);
            }
            catch (final InterruptedException interruptedexception)
            {
                interruptedexception.printStackTrace();
            }
        }

        this.isBusy = true;

        try
        {
            this.func_77421_a(par1Map, this.unsentDataFile, this.unsentTempFile, this.unsentOldFile);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            this.isBusy = false;
        }
    }

    public boolean func_77425_c()
    {
        return this.field_77433_l <= 0 && !this.isBusy && this.field_77431_c == null;
    }

    public void func_77422_e()
    {
        if (this.field_77433_l > 0)
        {
            --this.field_77433_l;
        }

        if (this.field_77434_m > 0)
        {
            --this.field_77434_m;
        }

        if (this.field_77431_c != null)
        {
            this.statFileWriter.func_77448_c(this.field_77431_c);
            this.field_77431_c = null;
        }

        if (this.field_77430_b != null)
        {
            this.statFileWriter.func_77452_b(this.field_77430_b);
            this.field_77430_b = null;
        }
    }

    static Map func_77419_a(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.field_77430_b;
    }

    static File func_77408_b(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.dataFile;
    }

    static File func_77407_c(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.tempFile;
    }

    static File func_77411_d(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.oldFile;
    }

    static void func_77414_a(final StatsSyncher par0StatsSyncher, final Map par1Map, final File par2File, final File par3File, final File par4File) throws IOException
    {
        par0StatsSyncher.func_77421_a(par1Map, par2File, par3File, par4File);
    }

    static Map func_77416_a(final StatsSyncher par0StatsSyncher, final Map par1Map)
    {
        return par0StatsSyncher.field_77430_b = par1Map;
    }

    static Map func_77410_a(final StatsSyncher par0StatsSyncher, final File par1File, final File par2File, final File par3File)
    {
        return par0StatsSyncher.func_77417_a(par1File, par2File, par3File);
    }

    static boolean setBusy(final StatsSyncher par0StatsSyncher, final boolean par1)
    {
        return par0StatsSyncher.isBusy = par1;
    }

    static File getUnsentDataFile(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.unsentDataFile;
    }

    static File getUnsentTempFile(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.unsentTempFile;
    }

    static File getUnsentOldFile(final StatsSyncher par0StatsSyncher)
    {
        return par0StatsSyncher.unsentOldFile;
    }
}
