package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;

public class CommandKill extends CommandBase
{
    public String getCommandName()
    {
        return "kill";
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
        return "commands.kill.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        final EntityPlayerMP entityplayermp = getCommandSenderAsPlayer(par1ICommandSender);
        entityplayermp.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
    }
}
