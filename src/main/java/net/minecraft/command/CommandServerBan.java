package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;

import java.util.List;

public class CommandServerBan extends CommandBase
{
    public String getCommandName()
    {
        return "ban";
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
        return "commands.ban.usage";
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(final ICommandSender par1ICommandSender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isListActive() && super.canCommandSenderUseCommand(par1ICommandSender);
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 1 && !par2ArrayOfStr[0].isEmpty())
        {
            final EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par2ArrayOfStr[0]);
            final BanEntry banentry = new BanEntry(par2ArrayOfStr[0]);
            banentry.setBannedBy(par1ICommandSender.getCommandSenderName());

            if (par2ArrayOfStr.length >= 2)
            {
                banentry.setBanReason(func_82360_a(par1ICommandSender, par2ArrayOfStr, 1));
            }

            MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().put(banentry);

            if (entityplayermp != null)
            {
                entityplayermp.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
            }

            notifyAdmins(par1ICommandSender, "commands.ban.success", new Object[] {par2ArrayOfStr[0]});
        }
        else
        {
            throw new WrongUsageException("commands.ban.usage", new Object[0]);
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
