package net.minecraft.server.dedicated;

import java.io.IOException;

import static net.minecraft.server.MinecraftServer.useConsole;
import static net.minecraft.server.MinecraftServer.useJline;

class DedicatedServerCommandThread extends Thread
{
    final DedicatedServer server;

    DedicatedServerCommandThread(final DedicatedServer par1DedicatedServer)
    {
        super("Command Reader");
        this.server = par1DedicatedServer;
    }

    public void run()
    {
        // CraftBukkit start
        if (!useConsole)
        {
            return;
        }

        // CraftBukkit end
        final jline.console.ConsoleReader bufferedreader = this.server.reader; // CraftBukkit
        String s;

        try
        {
            // CraftBukkit start - JLine disabling compatibility
            while (!this.server.isServerStopped() && this.server.isServerRunning())
            {
                if (useJline)
                {
                    s = bufferedreader.readLine(">", null);
                }
                else
                {
                    s = bufferedreader.readLine();
                }

                if (s != null)
                {
                    this.server.addPendingCommand(s, this.server);
                }

                // CraftBukkit end
            }
        }
        catch (final IOException ioexception)
        {
            // CraftBukkit
            java.util.logging.Logger.getLogger("").log(java.util.logging.Level.SEVERE, null, ioexception);
        }
    }
}
