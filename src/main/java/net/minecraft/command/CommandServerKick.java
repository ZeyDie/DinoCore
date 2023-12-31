package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandServerKick extends CommandBase
{
    public String getCommandName()
    {
        return "kick";
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
        return "commands.kick.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0 && par2ArrayOfStr[0].length() > 1)
        {
            final EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par2ArrayOfStr[0]);
            String s = "Kicked by an operator.";
            boolean flag = false;

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                if (par2ArrayOfStr.length >= 2)
                {
                    s = func_82360_a(par1ICommandSender, par2ArrayOfStr, 1);
                    flag = true;
                }

                entityplayermp.playerNetServerHandler.kickPlayerFromServer(s);

                if (flag)
                {
                    notifyAdmins(par1ICommandSender, "commands.kick.success.reason", new Object[] {entityplayermp.getEntityName(), s});
                }
                else
                {
                    notifyAdmins(par1ICommandSender, "commands.kick.success", new Object[] {entityplayermp.getEntityName()});
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.kick.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length >= 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
