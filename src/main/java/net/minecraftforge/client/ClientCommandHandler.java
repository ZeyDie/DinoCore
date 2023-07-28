package net.minecraftforge.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.*;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

/**
 * The class that handles client-side chat commands. You should register any
 * commands that you want handled on the client with this command handler.
 * 
 * If there is a command with the same name registered both on the server and
 * client, the client takes precedence!
 * 
 */
public class ClientCommandHandler extends CommandHandler
{
    public static final ClientCommandHandler instance = new ClientCommandHandler();

    public String[] latestAutoComplete = null;

    /**
     * @return 1 if successfully executed, 0 if wrong usage, it doesn't exist or
     *         it was canceled.
     */
    @Override
    public int executeCommand(final ICommandSender sender, String message)
    {
        String message1 = message;
        message1 = message1.trim();

        if (message1.startsWith("/"))
        {
            message1 = message1.substring(1);
        }

        final String[] temp = message1.split(" ");
        final String[] args = new String[temp.length - 1];
        final String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        final ICommand icommand = (ICommand) getCommands().get(commandName);

        try
        {
            if (icommand == null)
            {
                return 0;
            }

            if (icommand.canCommandSenderUseCommand(sender))
            {
                final CommandEvent event = new CommandEvent(icommand, sender, args);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }
                    return 0;
                }

                icommand.processCommand(sender, args);
                return 1;
            }
            else
            {
                sender.sendChatToPlayer(format("commands.generic.permission").setColor(RED));
            }
        }
        catch (final WrongUsageException wue)
        {
            sender.sendChatToPlayer(format("commands.generic.usage", format(wue.getMessage(), wue.getErrorOjbects())).setColor(RED));
        }
        catch (final CommandException ce)
        {
            sender.sendChatToPlayer(format(ce.getMessage(), ce.getErrorOjbects()).setColor(RED));
        }
        catch (final Throwable t)
        {
            sender.sendChatToPlayer(format("commands.generic.exception").setColor(RED));
            t.printStackTrace();
        }

        return 0;
    }

    //Couple of helpers because the mcp names are stupid and long...
    private ChatMessageComponent format(final String str, final Object... args)
    {
        return ChatMessageComponent.createFromTranslationWithSubstitutions(str, args);
    }

    private ChatMessageComponent format(final String str)
    {
        return ChatMessageComponent.createFromTranslationKey(str);
    }

    public void autoComplete(String leftOfCursor, final String full)
    {
        String leftOfCursor1 = leftOfCursor;
        latestAutoComplete = null;

        if (leftOfCursor1.charAt(0) == '/')
        {
            leftOfCursor1 = leftOfCursor1.substring(1);

            final Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen instanceof GuiChat)
            {
                final List<String> commands = getPossibleCommands(mc.thePlayer, leftOfCursor1);
                if (commands != null && !commands.isEmpty())
                {
                    if (leftOfCursor1.indexOf(' ') == -1)
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, GRAY + "/" + commands.get(i) + RESET);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, GRAY + commands.get(i) + RESET);
                        }
                    }

                    latestAutoComplete = commands.toArray(new String[0]);
                }
            }
        }
    }
}