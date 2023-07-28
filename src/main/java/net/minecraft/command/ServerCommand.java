package net.minecraft.command;

public class ServerCommand
{
    /** The command string. */
    public final String command;
    public final ICommandSender sender;

    public ServerCommand(final String par1Str, final ICommandSender par2ICommandSender)
    {
        this.command = par1Str;
        this.sender = par2ICommandSender;
    }
}
