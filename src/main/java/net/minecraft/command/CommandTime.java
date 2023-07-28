package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.List;

public class CommandTime extends CommandBase
{
    public String getCommandName()
    {
        return "time";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.time.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 1)
        {
            final int i;

            if (par2ArrayOfStr[0].equals("set"))
            {
                if (par2ArrayOfStr[1].equals("day"))
                {
                    i = 0;
                }
                else if (par2ArrayOfStr[1].equals("night"))
                {
                    i = 12500;
                }
                else
                {
                    i = parseIntWithMin(par1ICommandSender, par2ArrayOfStr[1], 0);
                }

                this.setTime(par1ICommandSender, i);
                notifyAdmins(par1ICommandSender, "commands.time.set", new Object[] {Integer.valueOf(i)});
                return;
            }

            if (par2ArrayOfStr[0].equals("add"))
            {
                i = parseIntWithMin(par1ICommandSender, par2ArrayOfStr[1], 0);
                this.addTime(par1ICommandSender, i);
                notifyAdmins(par1ICommandSender, "commands.time.added", new Object[] {Integer.valueOf(i)});
                return;
            }
        }

        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"set", "add"}): (par2ArrayOfStr.length == 2 && par2ArrayOfStr[0].equals("set") ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"day", "night"}): null);
    }

    /**
     * Set the time in the server object.
     */
    protected void setTime(final ICommandSender par1ICommandSender, final int par2)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            MinecraftServer.getServer().worldServers[j].setWorldTime((long)par2);
        }
    }

    /**
     * Adds (or removes) time in the server object.
     */
    protected void addTime(final ICommandSender par1ICommandSender, final int par2)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            final WorldServer worldserver = MinecraftServer.getServer().worldServers[j];
            worldserver.setWorldTime(worldserver.getWorldTime() + (long)par2);
        }
    }
}
