package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandServerSaveOn extends CommandBase
{
    public String getCommandName()
    {
        return "save-on";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.save-on.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        boolean flag = false;

        for (int i = 0; i < minecraftserver.worldServers.length; ++i)
        {
            if (minecraftserver.worldServers[i] != null)
            {
                final WorldServer worldserver = minecraftserver.worldServers[i];

                if (worldserver.canNotSave)
                {
                    worldserver.canNotSave = false;
                    flag = true;
                }
            }
        }

        if (flag)
        {
            notifyAdmins(par1ICommandSender, "commands.save.enabled", new Object[0]);
        }
        else
        {
            throw new CommandException("commands.save-on.alreadyOn", new Object[0]);
        }
    }
}
