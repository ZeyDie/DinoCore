package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;

public class CommandSetSpawnpoint extends CommandBase
{
    public String getCommandName()
    {
        return "spawnpoint";
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
        return "commands.spawnpoint.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        final EntityPlayerMP entityplayermp = par2ArrayOfStr.length == 0 ? getCommandSenderAsPlayer(par1ICommandSender) : getPlayer(par1ICommandSender, par2ArrayOfStr[0]);

        if (par2ArrayOfStr.length == 4)
        {
            if (entityplayermp.worldObj != null)
            {
                final byte b0 = 1;
                final int i = 30000000;
                int j = b0 + 1;
                final int k = parseIntBounded(par1ICommandSender, par2ArrayOfStr[b0], -i, i);
                final int l = parseIntBounded(par1ICommandSender, par2ArrayOfStr[j++], 0, 256);
                final int i1 = parseIntBounded(par1ICommandSender, par2ArrayOfStr[j++], -i, i);
                entityplayermp.setSpawnChunk(new ChunkCoordinates(k, l, i1), true);
                notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {entityplayermp.getEntityName(), Integer.valueOf(k), Integer.valueOf(l), Integer.valueOf(i1)});
            }
        }
        else
        {
            if (par2ArrayOfStr.length > 1)
            {
                throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]);
            }

            final ChunkCoordinates chunkcoordinates = entityplayermp.getPlayerCoordinates();
            entityplayermp.setSpawnChunk(chunkcoordinates, true);
            notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {entityplayermp.getEntityName(), Integer.valueOf(chunkcoordinates.posX), Integer.valueOf(chunkcoordinates.posY), Integer.valueOf(chunkcoordinates.posZ)});
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length != 1 && par2ArrayOfStr.length != 2 ? null : getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2)
    {
        return par2 == 0;
    }
}
