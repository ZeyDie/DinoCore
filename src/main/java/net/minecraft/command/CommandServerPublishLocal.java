package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumGameType;

public class CommandServerPublishLocal extends CommandBase
{
    public String getCommandName()
    {
        return "publish";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.publish.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        final String s = MinecraftServer.getServer().shareToLAN(EnumGameType.SURVIVAL, false);

        if (s != null)
        {
            notifyAdmins(par1ICommandSender, "commands.publish.started", new Object[] {s});
        }
        else
        {
            notifyAdmins(par1ICommandSender, "commands.publish.failed", new Object[0]);
        }
    }
}
