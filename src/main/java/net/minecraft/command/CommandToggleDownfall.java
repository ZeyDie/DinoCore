package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandToggleDownfall extends CommandBase
{
    public String getCommandName()
    {
        return "toggledownfall";
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
        return "commands.downfall.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        this.toggleDownfall();
        notifyAdmins(par1ICommandSender, "commands.downfall.success", new Object[0]);
    }

    /**
     * Toggle rain and enable thundering.
     */
    protected void toggleDownfall()
    {
        MinecraftServer.getServer().worldServers[0].toggleRain();
        MinecraftServer.getServer().worldServers[0].getWorldInfo().setThundering(true);
    }
}
