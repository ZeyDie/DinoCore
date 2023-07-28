package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.EnumGameType;

import java.util.Iterator;

public class CommandDefaultGameMode extends CommandGameMode
{
    public String getCommandName()
    {
        return "defaultgamemode";
    }

    public String getCommandUsage(final ICommandSender par1ICommandSender)
    {
        return "commands.defaultgamemode.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            final EnumGameType enumgametype = this.getGameModeFromCommand(par1ICommandSender, par2ArrayOfStr[0]);
            this.setGameType(enumgametype);
            notifyAdmins(par1ICommandSender, "commands.defaultgamemode.success", new Object[] {ChatMessageComponent.createFromTranslationKey("gameMode." + enumgametype.getName())});
        }
        else
        {
            throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
        }
    }

    protected void setGameType(final EnumGameType par1EnumGameType)
    {
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        minecraftserver.setGameType(par1EnumGameType);
        EntityPlayerMP entityplayermp;

        if (minecraftserver.getForceGamemode())
        {
            for (final Iterator iterator = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator(); iterator.hasNext(); entityplayermp.fallDistance = 0.0F)
            {
                entityplayermp = (EntityPlayerMP)iterator.next();
                entityplayermp.setGameType(par1EnumGameType);
            }
        }
    }
}
