/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FMLLog
{
    private static cpw.mods.fml.relauncher.FMLRelaunchLog coreLog = cpw.mods.fml.relauncher.FMLRelaunchLog.log;

    public static void log(final String logChannel, final Level level, final String format, final Object... data)
    {
        coreLog.log(logChannel, level, format, data);
    }

    public static void log(final Level level, final String format, final Object... data)
    {
        coreLog.log(level, format, data);
    }

    public static void log(final String logChannel, final Level level, final Throwable ex, final String format, final Object... data)
    {
        coreLog.log(logChannel, level, ex, format, data);
    }

    public static void log(final Level level, final Throwable ex, final String format, final Object... data)
    {
        coreLog.log(level, ex, format, data);
    }

    public static void severe(final String format, final Object... data)
    {
        log(Level.SEVERE, format, data);
    }

    public static void warning(final String format, final Object... data)
    {
        log(Level.WARNING, format, data);
    }

    public static void info(final String format, final Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void fine(final String format, final Object... data)
    {
        log(Level.FINE, format, data);
    }

    public static void finer(final String format, final Object... data)
    {
        log(Level.FINER, format, data);
    }

    public static void finest(final String format, final Object... data)
    {
        log(Level.FINEST, format, data);
    }
    public static Logger getLogger()
    {
        return coreLog.getLogger();
    }

    public static void makeLog(final String logChannel)
    {
        coreLog.makeLog(logChannel);
    }
}
