package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandServerTp extends CommandBase
{
    public String getCommandName()
    {
        return "tp";
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
        return "commands.tp.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            final EntityPlayerMP entityplayermp;

            if (par2ArrayOfStr.length != 2 && par2ArrayOfStr.length != 4)
            {
                entityplayermp = getCommandSenderAsPlayer(par1ICommandSender);
            }
            else
            {
                entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);

                if (entityplayermp == null)
                {
                    throw new PlayerNotFoundException();
                }
            }

            if (par2ArrayOfStr.length != 3 && par2ArrayOfStr.length != 4)
            {
                if (par2ArrayOfStr.length == 1 || par2ArrayOfStr.length == 2)
                {
                    final EntityPlayerMP entityplayermp1 = getPlayer(par1ICommandSender, par2ArrayOfStr[par2ArrayOfStr.length - 1]);

                    if (entityplayermp1 == null)
                    {
                        throw new PlayerNotFoundException();
                    }

                    if (entityplayermp1.worldObj != entityplayermp.worldObj)
                    {
                        notifyAdmins(par1ICommandSender, "commands.tp.notSameDimension", new Object[0]);
                        return;
                    }

                    entityplayermp.mountEntity((Entity)null);
                    entityplayermp.playerNetServerHandler.setPlayerLocation(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
                    notifyAdmins(par1ICommandSender, "commands.tp.success", new Object[] {entityplayermp.getEntityName(), entityplayermp1.getEntityName()});
                }
            }
            else if (entityplayermp.worldObj != null)
            {
                int i = par2ArrayOfStr.length - 3;
                final double d0 = func_110666_a(par1ICommandSender, entityplayermp.posX, par2ArrayOfStr[i++]);
                final double d1 = func_110665_a(par1ICommandSender, entityplayermp.posY, par2ArrayOfStr[i++], 0, 0);
                final double d2 = func_110666_a(par1ICommandSender, entityplayermp.posZ, par2ArrayOfStr[i++]);
                entityplayermp.mountEntity((Entity)null);
                entityplayermp.setPositionAndUpdate(d0, d1, d2);
                notifyAdmins(par1ICommandSender, "commands.tp.success.coordinates", new Object[] {entityplayermp.getEntityName(), Double.valueOf(d0), Double.valueOf(d1), Double.valueOf(d2)});
            }
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
