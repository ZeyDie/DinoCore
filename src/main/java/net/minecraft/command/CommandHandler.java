package net.minecraft.command;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import org.bukkit.craftbukkit.v1_6_R3.command.CraftSimpleCommandMap;
import org.bukkit.craftbukkit.v1_6_R3.command.ModCustomCommand;

import java.util.*;
import java.util.Map.Entry;

// Cauldron start
// Cauldron end

public class CommandHandler implements ICommandManager
{
    /** Map of Strings to the ICommand objects they represent */
    private final Map commandMap = new HashMap();

    /** The set of ICommand objects currently loaded. */
    private final Set commandSet = new HashSet();

    public int executeCommand(final ICommandSender par1ICommandSender, String par2Str)
    {
        String par2Str1 = par2Str;
        par2Str1 = par2Str1.trim();

        if (par2Str1.startsWith("/"))
        {
            par2Str1 = par2Str1.substring(1);
        }

        String[] astring = par2Str1.split(" ");
        final String s1 = astring[0];
        astring = dropFirstString(astring);
        final ICommand icommand = (ICommand)this.commandMap.get(s1);
        final int i = this.getUsernameIndex(icommand, astring);
        int j = 0;

        try
        {
            if (icommand == null)
            {
                throw new CommandNotFoundException();
            }

            if (true || icommand.canCommandSenderUseCommand(par1ICommandSender)) // Cauldron start - disable check for permissions since we handle it on Bukkit side
            {
                final CommandEvent event = new CommandEvent(icommand, par1ICommandSender, astring);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }
                    return 1;
                }

                if (i > -1)
                {
                    final EntityPlayerMP[] aentityplayermp = PlayerSelector.matchPlayers(par1ICommandSender, astring[i]);
                    final String s2 = astring[i];
                    final EntityPlayerMP[] aentityplayermp1 = aentityplayermp;
                    final int k = aentityplayermp.length;

                    for (int l = 0; l < k; ++l)
                    {
                        final EntityPlayerMP entityplayermp = aentityplayermp1[l];
                        astring[i] = entityplayermp.getEntityName();

                        try
                        {
                            icommand.processCommand(par1ICommandSender, astring);
                            ++j;
                        }
                        catch (final CommandException commandexception)
                        {
                            par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(commandexception.getMessage(), commandexception.getErrorOjbects()).setColor(EnumChatFormatting.RED));
                        }
                    }

                    astring[i] = s2;
                }
                else
                {
                    icommand.processCommand(par1ICommandSender, astring);
                    ++j;
                }
            }
            else
            {
                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.generic.permission").setColor(EnumChatFormatting.RED));
            }
        }
        catch (final WrongUsageException wrongusageexception)
        {
            par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.generic.usage", new Object[] {ChatMessageComponent.createFromTranslationWithSubstitutions(wrongusageexception.getMessage(), wrongusageexception.getErrorOjbects())}).setColor(EnumChatFormatting.RED));
        }
        catch (final CommandException commandexception1)
        {
            par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(commandexception1.getMessage(), commandexception1.getErrorOjbects()).setColor(EnumChatFormatting.RED));
        }
        catch (final Throwable throwable)
        {
            par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.generic.exception").setColor(EnumChatFormatting.RED));
            throwable.printStackTrace();
        }

        return j;
    }

    /**
     * adds the command and any aliases it has to the internal map of available commands
     */
    public ICommand registerCommand(final ICommand par1ICommand)
    {
        // Cauldron start - register commands with permission nodes, defaulting to class name
        return registerCommand(par1ICommand, par1ICommand.getClass().getName());
    }

    public ICommand registerCommand(final String permissionGroup, final ICommand par1ICommand)
    {
        return registerCommand(par1ICommand, permissionGroup + "." + par1ICommand.getCommandName());
    }

    public ICommand registerCommand(final ICommand par1ICommand, final String permissionNode)
    {
        // Cauldron end    
        final List list = par1ICommand.getCommandAliases();
        this.commandMap.put(par1ICommand.getCommandName(), par1ICommand);
        this.commandSet.add(par1ICommand);
        // Cauldron start - register vanilla commands with Bukkit to support permissions.
        final CraftSimpleCommandMap commandMap = FMLCommonHandler.instance().getMinecraftServerInstance().server.getCraftCommandMap();
        final ModCustomCommand customCommand = new ModCustomCommand(par1ICommand.getCommandName());
        customCommand.setPermission(permissionNode);
        if (list != null)
            customCommand.setAliases(list);
        commandMap.register(par1ICommand.getCommandName(), customCommand);
        FMLCommonHandler.instance().getMinecraftServerInstance().server.getLogger().info("Registered command " + par1ICommand.getCommandName() + " with permission node " + permissionNode);
        // Cauldron end

        if (list != null)
        {
            final Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                final String s = (String)iterator.next();
                final ICommand icommand1 = (ICommand)this.commandMap.get(s);

                if (icommand1 == null || !icommand1.getCommandName().equals(s))
                {
                    this.commandMap.put(s, par1ICommand);
                }
            }
        }

        return par1ICommand;
    }

    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(final String[] par0ArrayOfStr)
    {
        final String[] astring1 = new String[par0ArrayOfStr.length - 1];

        System.arraycopy(par0ArrayOfStr, 1, astring1, 0, par0ArrayOfStr.length - 1);

        return astring1;
    }

    /**
     * Performs a "begins with" string match on each token in par2. Only returns commands that par1 can use.
     */
    public List getPossibleCommands(final ICommandSender par1ICommandSender, final String par2Str)
    {
        final String[] astring = par2Str.split(" ", -1);
        final String s1 = astring[0];

        if (astring.length == 1)
        {
            final ArrayList arraylist = new ArrayList();
            final Iterator iterator = this.commandMap.entrySet().iterator();

            while (iterator.hasNext())
            {
                final Entry entry = (Entry)iterator.next();

                if (CommandBase.doesStringStartWith(s1, (String)entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(par1ICommandSender))
                {
                    arraylist.add(entry.getKey());
                }
            }

            return arraylist;
        }
        else
        {
            if (astring.length > 1)
            {
                final ICommand icommand = (ICommand)this.commandMap.get(s1);

                if (icommand != null)
                {
                    return icommand.addTabCompletionOptions(par1ICommandSender, dropFirstString(astring));
                }
            }

            return null;
        }
    }

    /**
     * returns all commands that the commandSender can use
     */
    public List getPossibleCommands(final ICommandSender par1ICommandSender)
    {
        final ArrayList arraylist = new ArrayList();
        final Iterator iterator = this.commandSet.iterator();

        while (iterator.hasNext())
        {
            final ICommand icommand = (ICommand)iterator.next();

            if (icommand.canCommandSenderUseCommand(par1ICommandSender))
            {
                arraylist.add(icommand);
            }
        }

        return arraylist;
    }

    /**
     * returns a map of string to commads. All commands are returned, not just ones which someone has permission to use.
     */
    public Map getCommands()
    {
        return this.commandMap;
    }

    /**
     * Return a command's first parameter index containing a valid username.
     */
    private int getUsernameIndex(final ICommand par1ICommand, final String[] par2ArrayOfStr)
    {
        if (par1ICommand == null)
        {
            return -1;
        }
        else
        {
            for (int i = 0; i < par2ArrayOfStr.length; ++i)
            {
                if (par1ICommand.isUsernameIndex(par2ArrayOfStr, i) && PlayerSelector.matchesMultiplePlayers(par2ArrayOfStr[i]))
                {
                    return i;
                }
            }

            return -1;
        }
    }
}
