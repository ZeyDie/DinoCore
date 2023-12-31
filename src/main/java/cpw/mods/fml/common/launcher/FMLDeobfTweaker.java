package cpw.mods.fml.common.launcher;

import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class FMLDeobfTweaker implements ITweaker {
    @Override
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile)
    {
    }

    @Override
    public void injectIntoClassLoader(final LaunchClassLoader classLoader)
    {
        // Deobfuscation transformer, always last
        if (!(Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))
        {
            classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.DeobfuscationTransformer");
        }
        try
        {
            FMLRelaunchLog.fine("Validating minecraft");
            final Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, classLoader);
            Method m = loaderClazz.getMethod("injectData", Object[].class);
            m.invoke(null, (Object)FMLInjectionData.data());
            m = loaderClazz.getMethod("instance");
            m.invoke(null);
            FMLRelaunchLog.fine("Minecraft validated, launching...");
        }
        catch (final Exception e)
        {
            // Load in the Loader, make sure he's ready to roll - this will initialize most of the rest of minecraft here
            System.out.println("A CRITICAL PROBLEM OCCURED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLaunchTarget()
    {
        throw new RuntimeException("Invalid for use as a primary tweaker");
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[0];
    }

}
