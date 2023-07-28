package net.minecraftforge.cauldron;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.logging.ILogAgent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import org.bukkit.inventory.InventoryHolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CauldronUtils {

    private static boolean deobfuscated = false;

    public static boolean isOverridingUpdateEntity(final Class<? extends TileEntity> c)
    {
        Class clazz = null;
        try
        {
            clazz = c.getMethod("func_70316_g").getDeclaringClass();  // updateEntity
        }
        catch (final Throwable e)
        {
            //e.printStackTrace(); no need for spam
        }

        return clazz != TileEntity.class;
    }

    public static boolean canTileEntityUpdate(final Class<? extends TileEntity> c)
    {
        boolean canUpdate = false;
        try 
        {
            final Constructor<? extends TileEntity> ctor = c.getConstructor();
            final TileEntity te = ctor.newInstance();
            canUpdate = te.canUpdate();
        } 
        catch (final Throwable e)
        {
            // ignore
        }
        return canUpdate;
    }

    public static <T> void dumpAndSortClassList(final List<Class<? extends T>> classList)
    {
        final List<String> sortedClassList = new ArrayList();
        for (final Class clazz : classList)
        {
            sortedClassList.add(clazz.getName());
        }
        Collections.sort(sortedClassList);
        if (MinecraftServer.getServer().tileEntityConfig.enableTECanUpdateWarning.getValue())
        {
            for (int i = 0; i < sortedClassList.size(); i++)
            {
                MinecraftServer.getServer().logInfo("Detected TE " + sortedClassList.get(i) + " with canUpdate set to true and no updateEntity override!. This is NOT good, please report to mod author as this can hurt performance.");
            }
        }
    }

    public static boolean migrateWorlds(final String worldType, final String oldWorldContainer, final String newWorldContainer, final String worldName)
    {
        boolean result = true;
        final File newWorld = new File(new File(newWorldContainer), worldName);
        final File oldWorld = new File(new File(oldWorldContainer), worldName);

        if ((!newWorld.isDirectory()) && (oldWorld.isDirectory()))
        {
            final ILogAgent log = MinecraftServer.getServer().getLogAgent();
            log.logInfo("---- Migration of old " + worldType + " folder required ----");
            log.logInfo("Cauldron has moved back to using the Forge World structure, your " + worldType + " folder will be moved to a new location in order to operate correctly.");
            log.logInfo("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Cauldron in the future.");
            log.logInfo("Attempting to move " + oldWorld + " to " + newWorld + "...");

            if (newWorld.exists())
            {
                log.logSevere("A file or folder already exists at " + newWorld + "!");
                log.logInfo("---- Migration of old " + worldType + " folder failed ----");
                result = false;
            }
            else if (newWorld.getParentFile().mkdirs() || newWorld.getParentFile().exists())
            {
                log.logInfo("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);

                // Migrate world data
                try
                {
                    com.google.common.io.Files.move(oldWorld, newWorld);
                }
                catch (final IOException exception)
                {
                    log.logSevere("Unable to move world data.");
                    exception.printStackTrace();
                    result = false;
                }
                try
                {
                    com.google.common.io.Files.copy(new File(oldWorld.getParent(), "level.dat"), new File(newWorld, "level.dat"));
                }
                catch (final IOException exception)
                {
                    log.logSevere("Unable to migrate world level.dat.");
                }

                log.logInfo("---- Migration of old " + worldType + " folder complete ----");
            }
            else result = false;
        }
        return result;
    }

    public static InventoryHolder getOwner(final TileEntity tileentity)
    {
        final org.bukkit.block.BlockState state = tileentity.worldObj.getWorld().getBlockAt(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord).getState();

        if (state instanceof InventoryHolder)
        {
            return (InventoryHolder) state;
        }

        return null;
    }

    public static boolean deobfuscatedEnvironment()
    {
        try
        {
            // Are we in a 'decompiled' environment?
            final byte[] bs = ((net.minecraft.launchwrapper.LaunchClassLoader)CauldronUtils.class.getClassLoader()).getClassBytes("net.minecraft.world.World");
            if (bs != null)
            {
                FMLRelaunchLog.info("Managed to load a deobfuscated Minecraft name- we are in a deobfuscated environment. Skipping runtime deobfuscation");
                deobfuscated = true;
            }
        }
        catch (final IOException e1)
        {
        }
        return deobfuscated;
    }
}
