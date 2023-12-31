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

package cpw.mods.fml.relauncher;

import com.google.common.base.Throwables;
import com.zeydie.DefaultPaths;
import com.zeydie.modified.ArchiveLogs;
import net.minecraft.launchwrapper.LogWrapper;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

public class FMLRelaunchLog
{

    public static boolean useOnlyThisLogger = false; // Cauldron - FML logger vs. CB logger
    public static class ConsoleLogWrapper extends Handler // Cauldron private -> public
    {
        @Override
        public void publish(final LogRecord record)
        {
            final boolean currInt = Thread.interrupted();
            try
            {
                ConsoleLogThread.recordQueue.put(record);
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace(errCache);
            }
            if (currInt)
            {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void flush()
        {

        }

        @Override
        public void close() throws SecurityException
        {
        }

    }
    public static class ConsoleLogThread implements Runnable // Cauldron private -> public
    {
        public static ConsoleHandler wrappedHandler = new ConsoleHandler(); // Cauldron private -> public
        static LinkedBlockingQueue<LogRecord> recordQueue = new LinkedBlockingQueue<LogRecord>();
        @Override
        public void run()
        {
            do
            {
                final LogRecord lr;
                try
                {
                    lr = recordQueue.take();
                    wrappedHandler.publish(lr);
                }
                catch (final InterruptedException e)
                {
                    e.printStackTrace(errCache);
                    Thread.interrupted();
                    // Stupid
                }
            }
            while (true);
        }
    }
    private static class LoggingOutStream extends ByteArrayOutputStream
    {
        private Logger log;
        private StringBuilder currentMessage;

        public LoggingOutStream(final Logger log)
        {
            this.log = log;
            this.currentMessage = new StringBuilder();
        }

        @Override
        public void flush() throws IOException
        {
            final String record;
            synchronized(FMLRelaunchLog.class)
            {
                super.flush();
                record = this.toString();
                super.reset();

                currentMessage.append(record.replace(FMLLogFormatter.LINE_SEPARATOR, "\n"));
                // Are we longer than just the line separator?
                int lastIdx = -1;
                int idx = currentMessage.indexOf("\n",lastIdx+1);
                while (idx >= 0)
                {
                    log.log(Level.INFO, currentMessage.substring(lastIdx+1,idx));
                    lastIdx = idx;
                    idx = currentMessage.indexOf("\n",lastIdx+1);
                }
                if (lastIdx >= 0)
                {
                    final String rem = currentMessage.substring(lastIdx+1);
                    currentMessage.setLength(0);
                    currentMessage.append(rem);
                }
            }
        }
    }
    /**
     * Our special logger for logging issues to. We copy various assets from the
     * Minecraft logger to achieve a similar appearance.
     */
    public static FMLRelaunchLog log = new FMLRelaunchLog();

    static File minecraftHome;
    private static boolean configured;

    private static Thread consoleLogThread;

    private static PrintStream errCache;
    private Logger myLog;

    private static FileHandler fileHandler;

    private static FMLLogFormatter formatter;

    static String logFileNamePattern;

    private FMLRelaunchLog()
    {
    }
    /**
     * Configure the FML logger
     */
    private static void configureLogging()
    {

        //TODO ZoomCodeStart
        ArchiveLogs.zipAndClear(DefaultPaths.LOGS_FOLDER);
        //TODO ZoomCodeEnd

        LogManager.getLogManager().reset();
        final Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(Level.OFF);

        log.myLog = Logger.getLogger("ForgeModLoader");
        LogWrapper.retarget(log.myLog);

        // Cauldron start - FML and CraftBukkit logging compatibility - add conditional
        final Logger stdOut = Logger.getLogger("STDOUT");
        final Logger stdErr = Logger.getLogger("STDERR");
        if (useOnlyThisLogger)
        {
            stdOut.setParent(log.myLog);
            stdErr.setParent(log.myLog);
        }
        // Cauldron end
        log.myLog.setLevel(Level.ALL);
        log.myLog.setUseParentHandlers(false);
        consoleLogThread = new Thread(new ConsoleLogThread());
        consoleLogThread.setDaemon(true);
        consoleLogThread.start();
        formatter = new FMLLogFormatter();
        try
        {
            //TODO ZoomCodeStart
            final File logPath = new File(DefaultPaths.getLogFile(logFileNamePattern));
            //TODO ZoomCodeEnd
            //TODO ZoomCodeClear
            //File logPath = new File(minecraftHome, logFileNamePattern);
            fileHandler = new FileHandler(logPath.getPath(), 0, 3)
            {
                public synchronized void close() throws SecurityException {
                    // We don't want this handler to reset
                }
            };
        }
        catch (final Throwable t)
        {
            throw Throwables.propagate(t);
        }

        resetLoggingHandlers();

        // Set system out to a log stream
        errCache = System.err;

        // Cauldron start - conditional
        if (useOnlyThisLogger)
        {
            System.setOut(new PrintStream(new LoggingOutStream(stdOut), true));
            System.setErr(new PrintStream(new LoggingOutStream(stdErr), true));
        }
        // Cauldron end

        configured = true;
    }
    private static void resetLoggingHandlers()
    {
        ConsoleLogThread.wrappedHandler.setLevel(Level.parse(System.getProperty("fml.log.level","INFO")));
        // Console handler captures the normal stderr before it gets replaced
        log.myLog.addHandler(new ConsoleLogWrapper());
        ConsoleLogThread.wrappedHandler.setFormatter(formatter);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(formatter);
        log.myLog.addHandler(fileHandler);
    }

    public static void loadLogConfiguration(final File logConfigFile)
    {
        if (logConfigFile!=null && logConfigFile.exists() && logConfigFile.canRead())
        {
            try
            {
                LogManager.getLogManager().readConfiguration(new FileInputStream(logConfigFile));
                resetLoggingHandlers();
            }
            catch (final Exception e)
            {
                log(Level.SEVERE, e, "Error reading logging configuration file %s", logConfigFile.getName());
            }
        }
    }
    public static void log(final String logChannel, final Level level, final String format, final Object... data)
    {
        makeLog(logChannel);
        Logger.getLogger(logChannel).log(level, String.format(format, data));
    }

    public static void log(final Level level, final String format, final Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log.myLog.log(level, String.format(format, data));
    }

    public static void log(final String logChannel, final Level level, final Throwable ex, final String format, final Object... data)
    {
        makeLog(logChannel);
        Logger.getLogger(logChannel).log(level, String.format(format, data), ex);
    }

    public static void log(final Level level, final Throwable ex, final String format, final Object... data)
    {
        if (!configured)
        {
            configureLogging();
        }
        log.myLog.log(level, String.format(format, data), ex);
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
    public Logger getLogger()
    {
        return myLog;
    }
    public static void makeLog(final String logChannel)
    {
        final Logger l = Logger.getLogger(logChannel);
        l.setParent(log.myLog);
    }
}
