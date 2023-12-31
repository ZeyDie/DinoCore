package net.minecraftforge.cauldron.command;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.cauldron.CauldronHooks;
import net.minecraftforge.cauldron.configuration.BoolSetting;
import net.minecraftforge.cauldron.configuration.IntSetting;
import net.minecraftforge.cauldron.configuration.Setting;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CauldronCommand extends Command
{
    private static final List<String> COMMANDS = ImmutableList.of("get", "set", "tick-interval", "save", "reload", "chunks", "heap");
    private static final List<String> CHUNK_COMMANDS = ImmutableList.of("print", "dump");

    public CauldronCommand()
    {
        super("cauldron");
        this.description = "Toggle certain Cauldron options";

        this.usageMessage = "/cauldron [" + StringUtils.join(COMMANDS, '|') + "] <option> [value]";
        this.setPermission("cauldron.command.cauldron");
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args)
    {
        if (!testPermission(sender))
        {
            return true;
        }
        if ((args.length > 0) && "heap".equalsIgnoreCase(args[0]))
        {
            processHeap(sender, args);
            return true;
        }
        if ((args.length > 0) && "chunks".equalsIgnoreCase(args[0]))
        {
            processChunks(sender, args);
            return true;
        }
        if ((args.length == 1) && "save".equalsIgnoreCase(args[0]))
        {
            MinecraftServer.getServer().cauldronConfig.save();
            sender.sendMessage(ChatColor.GREEN + "Config file saved");
            return true;
        }
        if ((args.length == 1) && "reload".equalsIgnoreCase(args[0]))
        {
            MinecraftServer.getServer().cauldronConfig.load();
            for (int i = 0; i < MinecraftServer.getServer().worlds.size(); i++)
            {
                MinecraftServer.getServer().worlds.get(i).cauldronConfig.init(); // reload world configs
            }
            sender.sendMessage(ChatColor.GREEN + "Config file reloaded");
            return true;
        }
        if (args.length < 2)
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        if ("tick-interval".equalsIgnoreCase(args[0]))
        {
            return intervalSet(sender, args);
        }
        if ("get".equalsIgnoreCase(args[0]))
        {
            return getToggle(sender, args);
        }
        else if ("set".equalsIgnoreCase(args[0]))
        {
            return setToggle(sender, args);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        }

        return false;
    }

    private void processHeap(final CommandSender sender, final String[] args)
    {
        final File file = new File(new File(new File("."), "dumps"), "heap-dump-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.bin");
        sender.sendMessage("Writing chunk info to: " + file);
        CauldronHooks.dumpHeap(file, true);
        sender.sendMessage("Chunk info complete");
    }

    private void processChunks(final CommandSender sender, final String[] args)
    {
        sender.sendMessage(ChatColor.GOLD + "Dimension stats: ");
        for (final net.minecraft.world.WorldServer world : MinecraftServer.getServer().worlds)
        {
            sender.sendMessage(ChatColor.GOLD + "Dimension: " + ChatColor.GRAY + world.provider.dimensionId +
                    ChatColor.GOLD + " Loaded Chunks: " + ChatColor.GRAY + world.theChunkProviderServer.loadedChunkHashMap.size() +
                    ChatColor.GOLD + " Active Chunks: " + ChatColor.GRAY + world.activeChunkSet.size() +
                    ChatColor.GOLD + " Entities: " + ChatColor.GRAY + world.loadedEntityList.size() +
                    ChatColor.GOLD + " Tile Entities: " + ChatColor.GRAY + world.loadedTileEntityList.size()
                    );
            sender.sendMessage(ChatColor.GOLD + " Entities Last Tick: " + ChatColor.GRAY + world.entitiesTicked +
                    ChatColor.GOLD + " Tiles Last Tick: " + ChatColor.GRAY + world.tilesTicked +
                    ChatColor.GOLD + " Removed Entities: " + ChatColor.GRAY + world.unloadedEntityList.size() +
                    ChatColor.GOLD + " Removed Tile Entities: " + ChatColor.GRAY + world.entityRemoval.size()
                    );
        }

        if ((args.length < 2) || !"dump".equalsIgnoreCase(args[1]))
        {
            return;
        }
        final boolean dumpAll = ((args.length > 2) && "all".equalsIgnoreCase(args[2]));

        final File file = new File(new File(new File("."), "chunk-dumps"), "chunk-info-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
        sender.sendMessage("Writing chunk info to: " + file);
        CauldronHooks.writeChunks(file, dumpAll);
        sender.sendMessage("Chunk info complete");
    }

    private boolean getToggle(final CommandSender sender, final String[] args)
    {
        try
        {
            Setting toggle = MinecraftServer.getServer().cauldronConfig.getSettings().get(args[1]);
            // check config directly
            if (toggle == null && MinecraftServer.getServer().cauldronConfig.isSet(args[1]))
            {
                if (MinecraftServer.getServer().cauldronConfig.isBoolean(args[1]))
                {
                    toggle = new BoolSetting(MinecraftServer.getServer().cauldronConfig, args[1], MinecraftServer.getServer().cauldronConfig.getBoolean(args[1], false), "");
                }
                else if (MinecraftServer.getServer().cauldronConfig.isInt(args[1]))
                {
                    toggle = new IntSetting(MinecraftServer.getServer().cauldronConfig, args[1], MinecraftServer.getServer().cauldronConfig.getInt(args[1], 1), "");
                }
                if (toggle != null)
                {
                    MinecraftServer.getServer().cauldronConfig.getSettings().put(toggle.path, toggle);
                    MinecraftServer.getServer().cauldronConfig.load();
                }
            }
            if (toggle == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not find option: " + args[1]);
                return false;
            }
            final Object value = toggle.getValue();
            final String option = (Boolean.TRUE.equals(value) ? ChatColor.GREEN : ChatColor.RED) + " " + value;
            sender.sendMessage(ChatColor.GOLD + args[1] + " " + option);
        }
        catch (final Exception ex)
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            ex.printStackTrace();
        }
        return true;
    }

    private boolean intervalSet(final CommandSender sender, final String[] args)
    {
        try
        {
            final int setting = NumberUtils.toInt(args[2], 1);
            MinecraftServer.getServer().cauldronConfig.set(args[1], setting);
        }
        catch (final Exception ex)
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        return true;
    }

    private boolean setToggle(final CommandSender sender, final String[] args)
    {
        try
        {
            Setting toggle = MinecraftServer.getServer().cauldronConfig.getSettings().get(args[1]);
            // check config directly
            if (toggle == null && MinecraftServer.getServer().cauldronConfig.isSet(args[1]))
            {
                toggle = new BoolSetting(MinecraftServer.getServer().cauldronConfig, args[1], MinecraftServer.getServer().cauldronConfig.getBoolean(args[1], false), "");
                MinecraftServer.getServer().cauldronConfig.getSettings().put(toggle.path, toggle);
                MinecraftServer.getServer().cauldronConfig.load();
            }
            if (toggle == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not find option: " + args[1]);
                return false;
            }
            if (args.length == 2)
            {
                sender.sendMessage(ChatColor.RED + "Usage: " + args[0] + " " + args[1] + " [value]");
                return false;
            }
            toggle.setValue(args[2]);
            final Object value = toggle.getValue();
            final String option = (Boolean.TRUE.equals(value) ? ChatColor.GREEN : ChatColor.RED) + " " + value;
            sender.sendMessage(ChatColor.GOLD + args[1] + " " + option);
            // Special case for load-on-request
            if (toggle == MinecraftServer.getServer().cauldronConfig.loadChunkOnRequest)
            {
                for (final net.minecraft.world.WorldServer world : MinecraftServer.getServer().worlds)
                {
                    world.theChunkProviderServer.loadChunkOnProvideRequest = MinecraftServer.getServer().cauldronConfig.loadChunkOnRequest.getValue();
                }
            }
            MinecraftServer.getServer().cauldronConfig.save();
        }
        catch (final Exception ex)
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args)
    {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1)
        {
            return StringUtil.copyPartialMatches(args[0], COMMANDS, new ArrayList<String>(COMMANDS.size()));
        }
        if (((args.length == 2) && "get".equalsIgnoreCase(args[0])) || "set".equalsIgnoreCase(args[0]))
        {
            return StringUtil.copyPartialMatches(args[1], MinecraftServer.getServer().cauldronConfig.getSettings().keySet(), new ArrayList<String>(MinecraftServer.getServer().cauldronConfig.getSettings().size()));
        }
        else if ((args.length == 2) && "chunks".equalsIgnoreCase(args[0]))
        {
            return StringUtil.copyPartialMatches(args[1], CHUNK_COMMANDS, new ArrayList<String>(CHUNK_COMMANDS.size()));
        }

        return ImmutableList.of();
    }

}
