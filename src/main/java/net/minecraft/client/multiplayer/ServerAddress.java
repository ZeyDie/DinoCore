package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@SideOnly(Side.CLIENT)
public class ServerAddress
{
    private final String ipAddress;
    private final int serverPort;

    private ServerAddress(final String par1Str, final int par2)
    {
        this.ipAddress = par1Str;
        this.serverPort = par2;
    }

    public String getIP()
    {
        return this.ipAddress;
    }

    public int getPort()
    {
        return this.serverPort;
    }

    public static ServerAddress func_78860_a(final String par0Str)
    {
        if (par0Str == null)
        {
            return null;
        }
        else
        {
            String[] astring = par0Str.split(":");

            if (par0Str.startsWith("["))
            {
                final int i = par0Str.indexOf("]");

                if (i > 0)
                {
                    final String s1 = par0Str.substring(1, i);
                    String s2 = par0Str.substring(i + 1).trim();

                    if (s2.startsWith(":") && !s2.isEmpty())
                    {
                        s2 = s2.substring(1);
                        astring = new String[] {s1, s2};
                    }
                    else
                    {
                        astring = new String[] {s1};
                    }
                }
            }

            if (astring.length > 2)
            {
                astring = new String[] {par0Str};
            }

            String s3 = astring[0];
            int j = astring.length > 1 ? parseIntWithDefault(astring[1], 25565) : 25565;

            if (j == 25565)
            {
                final String[] astring1 = getServerAddress(s3);
                s3 = astring1[0];
                j = parseIntWithDefault(astring1[1], 25565);
            }

            return new ServerAddress(s3, j);
        }
    }

    /**
     * Returns a server's address and port for the specified hostname, looking up the SRV record if possible
     */
    private static String[] getServerAddress(final String par0Str)
    {
        try
        {
            final String s1 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            final Hashtable hashtable = new Hashtable();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            final InitialDirContext initialdircontext = new InitialDirContext(hashtable);
            final Attributes attributes = initialdircontext.getAttributes("_minecraft._tcp." + par0Str, new String[] {"SRV"});
            final String[] astring = attributes.get("srv").get().toString().split(" ", 4);
            return new String[] {astring[3], astring[2]};
        }
        catch (final Throwable throwable)
        {
            return new String[] {par0Str, Integer.toString(25565)};
        }
    }

    private static int parseIntWithDefault(final String par0Str, final int par1)
    {
        try
        {
            return Integer.parseInt(par0Str.trim());
        }
        catch (final Exception exception)
        {
            return par1;
        }
    }
}
