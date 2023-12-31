package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

import java.util.List;

public class CommandServerEmote extends CommandBase
{
    public String getCommandName()
    {
        return "me";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.me.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            final String s = func_82361_a(par1ICommandSender, par2ArrayOfStr, 0, par1ICommandSender.canCommandSenderUseCommand(1, "me"));
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("chat.type.emote", new Object[] {par1ICommandSender.getCommandSenderName(), s}));
        }
        else
        {
            throw new WrongUsageException("commands.me.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }
}
