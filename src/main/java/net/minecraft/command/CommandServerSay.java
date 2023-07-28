package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

import java.util.List;

public class CommandServerSay extends CommandBase
{
    public String getCommandName()
    {
        return "say";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 1;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.say.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0 && !par2ArrayOfStr[0].isEmpty())
        {
            final String s = func_82361_a(par1ICommandSender, par2ArrayOfStr, 0, true);
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.announcement", new Object[] {par1ICommandSender.getCommandSenderName(), s}));
        }
        else
        {
            throw new WrongUsageException("commands.say.usage", new Object[0]);
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
