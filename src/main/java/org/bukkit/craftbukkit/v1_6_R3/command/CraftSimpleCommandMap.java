package org.bukkit.craftbukkit.v1_6_R3.command;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;

import java.util.regex.Pattern;

import static org.bukkit.util.Java15Compat.Arrays_copyOfRange;

public class CraftSimpleCommandMap extends SimpleCommandMap {

    private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);
    private ICommandSender vanillaConsoleSender; // Cauldron

    public CraftSimpleCommandMap(final Server server) {
        super(server);
    }

    /**
     * {@inheritDoc}
     */
    public boolean dispatch(final CommandSender sender, final String commandLine) throws CommandException {
        final String[] args = PATTERN_ON_SPACE.split(commandLine);

        if (args.length == 0) {
            return false;
        }

        final String sentCommandLabel = args[0].toLowerCase();
        final Command target = getCommand(sentCommandLabel);

        if (target == null) {
            return false;
        }
        try {
            // Cauldron start - if command is a mod command, check permissions and route through vanilla
            if (target instanceof ModCustomCommand)
            {
                if (!target.testPermission(sender)) return true;
                if (sender instanceof ConsoleCommandSender)
                {
                    FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(this.vanillaConsoleSender, commandLine);
                }
                else FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(((CraftPlayer)sender).getHandle(), commandLine);
            }
            else {
            // Cauldron end
                // Note: we don't return the result of target.execute as thats success / failure, we return handled (true) or not handled (false)
                target.execute(sender, sentCommandLabel, Arrays_copyOfRange(args, 1, args.length));
            }
        } catch (final CommandException ex) {
            throw ex;
        } catch (final Throwable ex) {
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
        }

        // return true as command was handled
        return true;
    }

    // Cauldron start - sets the vanilla console sender
    public void setVanillaConsoleSender(final ICommandSender console)
    {
        this.vanillaConsoleSender = console;
    }
    // Cauldron end
}
