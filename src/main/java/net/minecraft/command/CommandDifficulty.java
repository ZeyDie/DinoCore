package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

import java.util.List;

public class CommandDifficulty extends CommandBase
{
    private static final String[] difficulties = {"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};

    public String getCommandName()
    {
        return "difficulty";
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
        return "commands.difficulty.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            final int i = this.getDifficultyForName(par1ICommandSender, par2ArrayOfStr[0]);
            MinecraftServer.getServer().setDifficultyForAllWorlds(i);
            notifyAdmins(par1ICommandSender, "commands.difficulty.success", new Object[] {ChatMessageComponent.createFromTranslationKey(difficulties[i])});
        }
        else
        {
            throw new WrongUsageException("commands.difficulty.usage", new Object[0]);
        }
    }

    /**
     * Return the difficulty value for the specified string.
     */
    protected int getDifficultyForName(final ICommandSender par1ICommandSender, final String par2Str)
    {
        return !par2Str.equalsIgnoreCase("peaceful") && !par2Str.equalsIgnoreCase("p") ? (!par2Str.equalsIgnoreCase("easy") && !par2Str.equalsIgnoreCase("e") ? (!par2Str.equalsIgnoreCase("normal") && !par2Str.equalsIgnoreCase("n") ? (!par2Str.equalsIgnoreCase("hard") && !par2Str.equalsIgnoreCase("h") ? parseIntBounded(par1ICommandSender, par2Str, 0, 3) : 3) : 2) : 1) : 0;
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"peaceful", "easy", "normal", "hard"}): null;
    }
}
