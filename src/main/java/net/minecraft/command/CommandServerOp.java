package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class CommandServerOp extends CommandBase
{
    public String getCommandName()
    {
        return "op";
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
        return "commands.op.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1 && !par2ArrayOfStr[0].isEmpty())
        {
            MinecraftServer.getServer().getConfigurationManager().addOp(par2ArrayOfStr[0]);
            notifyAdmins(par1ICommandSender, "commands.op.success", new Object[] {par2ArrayOfStr[0]});
        }
        else
        {
            throw new WrongUsageException("commands.op.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            final String s = par2ArrayOfStr[par2ArrayOfStr.length - 1];
            final ArrayList arraylist = new ArrayList();
            final String[] astring1 = MinecraftServer.getServer().getAllUsernames();
            final int i = astring1.length;

            for (int j = 0; j < i; ++j)
            {
                final String s1 = astring1[j];

                if (!MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(s1) && doesStringStartWith(s, s1))
                {
                    arraylist.add(s1);
                }
            }

            return arraylist;
        }
        else
        {
            return null;
        }
    }
}
