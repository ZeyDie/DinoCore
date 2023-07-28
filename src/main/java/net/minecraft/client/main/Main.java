package net.minecraft.client.main;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

@SideOnly(Side.CLIENT)
public class Main
{
    public static void main(final String[] par0ArrayOfStr)
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        final OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec = optionparser.accepts("server").withRequiredArg();
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(25565), new Integer[0]);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, new String[0]);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec10 = optionparser.accepts("session").withRequiredArg();
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec11 = optionparser.accepts("version").withRequiredArg().required();
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec12 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(854), new Integer[0]);
        final ArgumentAcceptingOptionSpec argumentacceptingoptionspec13 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(480), new Integer[0]);
        final NonOptionArgumentSpec nonoptionargumentspec = optionparser.nonOptions();
        final OptionSet optionset = optionparser.parse(par0ArrayOfStr);
        final List list = optionset.valuesOf(nonoptionargumentspec);
        final String s = (String)optionset.valueOf(argumentacceptingoptionspec5);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null)
        {
            try
            {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, ((Integer)optionset.valueOf(argumentacceptingoptionspec6)).intValue()));
            }
            catch (final Exception exception)
            {
                ;
            }
        }

        final String s1 = (String)optionset.valueOf(argumentacceptingoptionspec7);
        final String s2 = (String)optionset.valueOf(argumentacceptingoptionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && func_110121_a(s1) && func_110121_a(s2))
        {
            Authenticator.setDefault(new MainProxyAuthenticator(s1, s2));
        }

        final int i = ((Integer)optionset.valueOf(argumentacceptingoptionspec12)).intValue();
        final int j = ((Integer)optionset.valueOf(argumentacceptingoptionspec13)).intValue();
        final boolean flag = optionset.has("fullscreen");
        final boolean flag1 = optionset.has("demo");
        final String s3 = (String)optionset.valueOf(argumentacceptingoptionspec11);
        final File file1 = (File)optionset.valueOf(argumentacceptingoptionspec2);
        final File file2 = optionset.has(argumentacceptingoptionspec3) ? (File)optionset.valueOf(argumentacceptingoptionspec3) : new File(file1, "assets/");
        final File file3 = optionset.has(argumentacceptingoptionspec4) ? (File)optionset.valueOf(argumentacceptingoptionspec4) : new File(file1, "resourcepacks/");
        final Session session = new Session((String)argumentacceptingoptionspec9.value(optionset), (String)argumentacceptingoptionspec10.value(optionset));
        final Minecraft minecraft = new Minecraft(session, i, j, flag, flag1, file1, file2, file3, proxy, s3);
        final String s4 = (String)optionset.valueOf(argumentacceptingoptionspec);

        if (s4 != null)
        {
            minecraft.setServer(s4, ((Integer)optionset.valueOf(argumentacceptingoptionspec1)).intValue());
        }

        Runtime.getRuntime().addShutdownHook(new MainShutdownHook());

        if (!list.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + list);
        }

        Thread.currentThread().setName("Minecraft main thread");
        minecraft.run();
    }

    private static boolean func_110121_a(final String par0Str)
    {
        return par0Str != null && !par0Str.isEmpty();
    }
}
