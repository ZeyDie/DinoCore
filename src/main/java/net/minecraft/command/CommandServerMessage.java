package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class CommandServerMessage extends CommandBase
{
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"w", "msg"});
    }

    public String getCommandName()
    {
        return "tell";
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
        return "commands.message.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException("commands.message.usage", new Object[0]);
        }
        else
        {
            final EntityPlayerMP entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException();
            }
            else if (entityplayermp == par1ICommandSender)
            {
                throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
            }
            else
            {
                final String s = func_82361_a(par1ICommandSender, par2ArrayOfStr, 1, !(par1ICommandSender instanceof EntityPlayer));
                entityplayermp.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.message.display.incoming", new Object[] {par1ICommandSender.getCommandSenderName(), s}).setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true)));
                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.message.display.outgoing", new Object[] {entityplayermp.getCommandSenderName(), s}).setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true)));
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2)
    {
        return par2 == 0;
    }
}
