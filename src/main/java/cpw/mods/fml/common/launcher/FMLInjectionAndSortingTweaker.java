package cpw.mods.fml.common.launcher;

import cpw.mods.fml.relauncher.CoreModManager;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

/**
 * This class is to manage the injection of coremods as tweakers into the tweak framework.
 * It has to inject the coremod tweaks during construction, because that is the only time
 * the tweak list is writeable.
 * @author cpw
 *
 */
public class FMLInjectionAndSortingTweaker implements ITweaker {
    private boolean run;
    public FMLInjectionAndSortingTweaker()
    {
        CoreModManager.injectCoreModTweaks(this);
        run = false;
    }

    @Override
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile)
    {
        if (!run)
        {
            // We sort the tweak list here so that it obeys the tweakordering
            CoreModManager.sortTweakList();
        }
        run = true;
    }

    @Override
    public void injectIntoClassLoader(final LaunchClassLoader classLoader)
    {
    }

    @Override
    public String getLaunchTarget()
    {
        return "";
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[0];
    }

}
