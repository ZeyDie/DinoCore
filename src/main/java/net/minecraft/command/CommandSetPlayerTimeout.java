package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase
{
    public String getCommandName()
    {
        return "setidletimeout";
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
        return "commands.setidletimeout.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            final int i = parseIntWithMin(par1ICommandSender, par2ArrayOfStr[0], 0);
            MinecraftServer.getServer().func_143006_e(i);
            notifyAdmins(par1ICommandSender, "commands.setidletimeout.success", new Object[] {Integer.valueOf(i)});
        }
        else
        {
            throw new WrongUsageException("commands.setidletimeout.usage", new Object[0]);
        }
    }
}
