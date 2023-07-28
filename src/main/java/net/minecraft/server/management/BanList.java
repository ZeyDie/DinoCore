package net.minecraft.server.management;

import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class BanList
{
    private final LowerStringMap theBanList = new LowerStringMap();
    private final File fileName;

    /** set to true if not singlePlayer */
    private boolean listActive = true;

    public BanList(final File par1File)
    {
        this.fileName = par1File;
    }

    public boolean isListActive()
    {
        return this.listActive;
    }

    public void setListActive(final boolean par1)
    {
        this.listActive = par1;
    }

    /**
     * removes expired Bans before returning
     */
    public Map getBannedList()
    {
        this.removeExpiredBans();
        return this.theBanList;
    }

    public boolean isBanned(final String par1Str)
    {
        if (!this.isListActive())
        {
            return false;
        }
        else
        {
            this.removeExpiredBans();
            return this.theBanList.containsKey(par1Str);
        }
    }

    public void put(final BanEntry par1BanEntry)
    {
        this.theBanList.putLower(par1BanEntry.getBannedUsername(), par1BanEntry);
        this.saveToFileWithHeader();
    }

    public void remove(final String par1Str)
    {
        this.theBanList.remove(par1Str);
        this.saveToFileWithHeader();
    }

    public void removeExpiredBans()
    {
        final Iterator iterator = this.theBanList.values().iterator();

        while (iterator.hasNext())
        {
            final BanEntry banentry = (BanEntry)iterator.next();

            if (banentry.hasBanExpired())
            {
                iterator.remove();
            }
        }
    }

    /**
     * Loads the ban list from the file (adds every entry, does not clear the current list).
     */
    public void loadBanList()
    {
        if (this.fileName.isFile())
        {
            final BufferedReader bufferedreader;

            try
            {
                bufferedreader = new BufferedReader(new FileReader(this.fileName));
            }
            catch (final FileNotFoundException filenotfoundexception)
            {
                throw new Error();
            }

            String s;

            try
            {
                while ((s = bufferedreader.readLine()) != null)
                {
                    if (!s.startsWith("#"))
                    {
                        final BanEntry banentry = BanEntry.parse(s);

                        if (banentry != null)
                        {
                            this.theBanList.putLower(banentry.getBannedUsername(), banentry);
                        }
                    }
                }
            }
            catch (final IOException ioexception)
            {
                MinecraftServer.getServer().getLogAgent().logSevereException("Could not load ban list", ioexception);
            }
        }
    }

    public void saveToFileWithHeader()
    {
        this.saveToFile(true);
    }

    /**
     * par1: include header
     */
    public void saveToFile(final boolean par1)
    {
        this.removeExpiredBans();

        try
        {
            final PrintWriter printwriter = new PrintWriter(new FileWriter(this.fileName, false));

            if (par1)
            {
                printwriter.println("# Updated " + (new SimpleDateFormat()).format(new Date()) + " by Minecraft " + "1.6.4");
                printwriter.println("# victim name | ban date | banned by | banned until | reason");
                printwriter.println();
            }

            final Iterator iterator = this.theBanList.values().iterator();

            while (iterator.hasNext())
            {
                final BanEntry banentry = (BanEntry)iterator.next();
                printwriter.println(banentry.buildBanString());
            }

            printwriter.close();
        }
        catch (final IOException ioexception)
        {
            MinecraftServer.getServer().getLogAgent().logSevereException("Could not save ban list", ioexception);
        }
    }
}
