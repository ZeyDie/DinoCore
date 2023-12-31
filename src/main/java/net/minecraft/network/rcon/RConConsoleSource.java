package net.minecraft.network.rcon;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender
{
    /** only ever used by MinecraftServer.executeCommand */
    public static final RConConsoleSource consoleBuffer = new RConConsoleSource();

    /** RCon string buffer for log. */
    private StringBuffer buffer = new StringBuffer();

    /**
     * Clears the RCon log
     */
    public void resetLog()
    {
        this.buffer.setLength(0);
    }

    public String getChatBuffer()
    {
        return this.buffer.toString();
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return "Rcon";
    }

    public void sendChatToPlayer(final ChatMessageComponent par1ChatMessageComponent)
    {
        this.buffer.append(par1ChatMessageComponent.toString());
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(final int par1, final String par2Str)
    {
        return true;
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0, 0, 0);
    }

    public World getEntityWorld()
    {
        return MinecraftServer.getServer().getEntityWorld();
    }
}
