package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandServerDeop extends CommandBase
{
    public String getCommandName()
    {
        return "deop";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.deop.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1 && !par2ArrayOfStr[0].isEmpty())
        {
            MinecraftServer.getServer().getConfigurationManager().removeOp(par2ArrayOfStr[0]);
            notifyAdmins(par1ICommandSender, "commands.deop.success", new Object[] {par2ArrayOfStr[0]});
        }
        else
        {
            throw new WrongUsageException("commands.deop.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getConfigurationManager().getOps()) : null;
    }
}
