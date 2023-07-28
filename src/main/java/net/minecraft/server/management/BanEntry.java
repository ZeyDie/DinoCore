package net.minecraft.server.management;

import net.minecraft.server.MinecraftServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class BanEntry
{
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final String username;
    private Date banStartDate = new Date();
    private String bannedBy = "(Unknown)";
    private Date banEndDate;
    private String reason = "Banned by an operator.";

    public BanEntry(final String par1Str)
    {
        this.username = par1Str;
    }

    public String getBannedUsername()
    {
        return this.username;
    }

    public Date getBanStartDate()
    {
        return this.banStartDate;
    }

    /**
     * null == start ban now
     */
    public void setBanStartDate(final Date par1Date)
    {
        this.banStartDate = par1Date != null ? par1Date : new Date();
    }

    public String getBannedBy()
    {
        return this.bannedBy;
    }

    public void setBannedBy(final String par1Str)
    {
        this.bannedBy = par1Str;
    }

    public Date getBanEndDate()
    {
        return this.banEndDate;
    }

    public void setBanEndDate(final Date par1Date)
    {
        this.banEndDate = par1Date;
    }

    public boolean hasBanExpired()
    {
        return this.banEndDate == null ? false : this.banEndDate.before(new Date());
    }

    public String getBanReason()
    {
        return this.reason;
    }

    public void setBanReason(final String par1Str)
    {
        this.reason = par1Str;
    }

    public String buildBanString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(this.getBannedUsername());
        stringbuilder.append("|");
        stringbuilder.append(dateFormat.format(this.getBanStartDate()));
        stringbuilder.append("|");
        stringbuilder.append(this.getBannedBy());
        stringbuilder.append("|");
        stringbuilder.append(this.getBanEndDate() == null ? "Forever" : dateFormat.format(this.getBanEndDate()));
        stringbuilder.append("|");
        stringbuilder.append(this.getBanReason());
        return stringbuilder.toString();
    }

    public static BanEntry parse(final String par0Str)
    {
        if (par0Str.trim().length() < 2)
        {
            return null;
        }
        else
        {
            final String[] astring = par0Str.trim().split(Pattern.quote("|"), 5);
            final BanEntry banentry = new BanEntry(astring[0].trim());
            final byte b0 = 0;
            int i = astring.length;
            int j = b0 + 1;

            if (i <= j)
            {
                return banentry;
            }
            else
            {
                try
                {
                    banentry.setBanStartDate(dateFormat.parse(astring[j].trim()));
                }
                catch (final ParseException parseexception)
                {
                    MinecraftServer.getServer().getLogAgent().logWarningException("Could not read creation date format for ban entry \'" + banentry.getBannedUsername() + "\' (was: \'" + astring[j] + "\')", parseexception);
                }

                i = astring.length;
                ++j;

                if (i <= j)
                {
                    return banentry;
                }
                else
                {
                    banentry.setBannedBy(astring[j].trim());
                    i = astring.length;
                    ++j;

                    if (i <= j)
                    {
                        return banentry;
                    }
                    else
                    {
                        try
                        {
                            final String s1 = astring[j].trim();

                            if (!s1.equalsIgnoreCase("Forever") && !s1.isEmpty())
                            {
                                banentry.setBanEndDate(dateFormat.parse(s1));
                            }
                        }
                        catch (final ParseException parseexception1)
                        {
                            MinecraftServer.getServer().getLogAgent().logWarningException("Could not read expiry date format for ban entry \'" + banentry.getBannedUsername() + "\' (was: \'" + astring[j] + "\')", parseexception1);
                        }

                        i = astring.length;
                        ++j;

                        if (i <= j)
                        {
                            return banentry;
                        }
                        else
                        {
                            banentry.setBanReason(astring[j].trim());
                            return banentry;
                        }
                    }
                }
            }
        }
    }
}
