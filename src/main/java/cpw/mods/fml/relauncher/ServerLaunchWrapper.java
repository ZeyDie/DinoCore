package cpw.mods.fml.relauncher;

import java.lang.reflect.Method;

public class ServerLaunchWrapper {

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        new ServerLaunchWrapper().run(args);
    }

    private ServerLaunchWrapper()
    {

    }

    private void run(final String[] args)
    {
        Class<?> launchwrapper = null;
        try
        {
            launchwrapper = Class.forName("net.minecraft.launchwrapper.Launch",true,getClass().getClassLoader());
            Class.forName("org.objectweb.asm.Type",true,getClass().getClassLoader());
        }
        catch (final Exception e)
        {
            System.err.printf("We appear to be missing one or more essential library files.\n" +
            		"You will need to add them to your server before FML and Forge will run successfully.");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        try
        {
            final Method main = launchwrapper.getMethod("main", String[].class);
            final String[] allArgs = new String[args.length + 2];
            allArgs[0] = "--tweakClass";
            allArgs[1] = "cpw.mods.fml.common.launcher.FMLServerTweaker";
            System.arraycopy(args, 0, allArgs, 2, args.length);
            main.invoke(null,(Object)allArgs);
        }
        catch (final Exception e)
        {
            System.err.printf("A problem occurred running the Server launcher.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
