package net.minecraftforge.server.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.ForgeTimeTracker;

import java.text.DecimalFormat;

public class ForgeCommand extends CommandBase {

    private MinecraftServer server;

    public ForgeCommand(final MinecraftServer server)
    {
        this.server = server;
    }
    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

    @Override
    public String getCommandName()
    {
        return "forge";
    }

    @Override
    public String getCommandUsage(final ICommandSender icommandsender)
    {
        return "commands.forge.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    @Override
    public void processCommand(final ICommandSender sender, final String[] args)
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("commands.forge.usage");
        }
        else if ("help".equals(args[0]))
        {
            throw new WrongUsageException("commands.forge.usage");
        }
        else if ("tps".equals(args[0]))
        {
            displayTPS(sender,args);
        }
        else if ("tpslog".equals(args[0]))
        {
            doTPSLog(sender,args);
        }
        else if ("track".equals(args[0]))
        {
            handleTracking(sender, args);
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage");
        }
    }

    private void handleTracking(final ICommandSender sender, final String[] args)
    {
        if (args.length != 3)
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
        final String type = args[1];
        final int duration = parseIntBounded(sender, args[2], 1, 60);

        if ("te".equals(type))
        {
            doTurnOnTileEntityTracking(sender, duration);
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
    }

    private void doTurnOnTileEntityTracking(final ICommandSender sender, final int duration)
    {
        ForgeTimeTracker.tileEntityTrackingDuration = duration;
        ForgeTimeTracker.tileEntityTracking = true;
        sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.forge.tracking.te.enabled", duration));
    }

    private void doTPSLog(final ICommandSender sender, final String[] args)
    {

    }

    private void displayTPS(final ICommandSender sender, final String[] args)
    {
        int dim = 0;
        boolean summary = true;
        if (args.length > 1)
        {
            dim = parseInt(sender, args[1]);
            summary = false;
        }
        if (summary)
        {
            for (final Integer dimId : DimensionManager.getIDs())
            {
                final double worldTickTime = ForgeCommand.mean(this.server.worldTickTimes.get(dimId)) * 1.0E-6D;
                final double worldTPS = Math.min(1000.0/worldTickTime, 20);
                sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.forge.tps.summary",String.format("Dim %d", dimId), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)));
            }
            final double meanTickTime = ForgeCommand.mean(this.server.tickTimeArray) * 1.0E-6D;
            final double meanTPS = Math.min(1000.0/meanTickTime, 20);
            sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.forge.tps.summary","Overall", timeFormatter.format(meanTickTime), timeFormatter.format(meanTPS)));
        }
        else
        {
            final double worldTickTime = ForgeCommand.mean(this.server.worldTickTimes.get(dim)) * 1.0E-6D;
            final double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.forge.tps.summary",String.format("Dim %d", dim), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)));
        }
    }

    private static long mean(final long[] values)
    {
        long sum = 0l;
        for (final long v : values)
        {
            sum+=v;
        }

        return sum / values.length;
    }
}
