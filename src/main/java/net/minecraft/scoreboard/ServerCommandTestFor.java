package net.minecraft.scoreboard;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.tileentity.TileEntityCommandBlock;

public class ServerCommandTestFor extends CommandBase
{
    public String getCommandName()
    {
        return "testfor";
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
        return "commands.testfor.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length != 1)
        {
            throw new WrongUsageException("commands.testfor.usage", new Object[0]);
        }
        else if (!(par1ICommandSender instanceof TileEntityCommandBlock))
        {
            throw new CommandException("commands.testfor.failed", new Object[0]);
        }
        else
        {
            getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2)
    {
        return par2 == 0;
    }
}
