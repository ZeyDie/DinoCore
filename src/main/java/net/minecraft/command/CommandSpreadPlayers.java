package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.*;

public class CommandSpreadPlayers extends CommandBase
{
    public String getCommandName()
    {
        return "spreadplayers";
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
        return "commands.spreadplayers.usage";
    }

    public void processCommand(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 6)
        {
            throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
        }
        else
        {
            final byte b0 = 0;
            int i = b0 + 1;
            final double d0 = func_110666_a(par1ICommandSender, Double.NaN, par2ArrayOfStr[b0]);
            final double d1 = func_110666_a(par1ICommandSender, Double.NaN, par2ArrayOfStr[i++]);
            final double d2 = func_110664_a(par1ICommandSender, par2ArrayOfStr[i++], 0.0D);
            final double d3 = func_110664_a(par1ICommandSender, par2ArrayOfStr[i++], d2 + 1.0D);
            final boolean flag = func_110662_c(par1ICommandSender, par2ArrayOfStr[i++]);
            final ArrayList arraylist = Lists.newArrayList();

            while (true)
            {
                while (i < par2ArrayOfStr.length)
                {
                    final String s = par2ArrayOfStr[i++];

                    if (PlayerSelector.hasArguments(s))
                    {
                        final EntityPlayerMP[] aentityplayermp = PlayerSelector.matchPlayers(par1ICommandSender, s);

                        if (aentityplayermp == null || aentityplayermp.length == 0)
                        {
                            throw new PlayerNotFoundException();
                        }

                        Collections.addAll(arraylist, aentityplayermp);
                    }
                    else
                    {
                        final EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(s);

                        if (entityplayermp == null)
                        {
                            throw new PlayerNotFoundException();
                        }

                        arraylist.add(entityplayermp);
                    }
                }

                if (arraylist.isEmpty())
                {
                    throw new PlayerNotFoundException();
                }

                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.spreadplayers.spreading." + (flag ? "teams" : "players"), new Object[] {func_110663_b(arraylist), Double.valueOf(d0), Double.valueOf(d1), Double.valueOf(d2), Double.valueOf(d3)}));
                this.func_110669_a(par1ICommandSender, arraylist, new CommandSpreadPlayersPosition(d0, d1), d2, d3, ((EntityLivingBase)arraylist.get(0)).worldObj, flag);
                return;
            }
        }
    }

    private void func_110669_a(final ICommandSender par1ICommandSender, final List par2List, final CommandSpreadPlayersPosition par3CommandSpreadPlayersPosition, final double par4, final double par6, final World par8World, final boolean par9)
    {
        final Random random = new Random();
        final double d2 = par3CommandSpreadPlayersPosition.field_111101_a - par6;
        final double d3 = par3CommandSpreadPlayersPosition.field_111100_b - par6;
        final double d4 = par3CommandSpreadPlayersPosition.field_111101_a + par6;
        final double d5 = par3CommandSpreadPlayersPosition.field_111100_b + par6;
        final CommandSpreadPlayersPosition[] acommandspreadplayersposition = this.func_110670_a(random, par9 ? this.func_110667_a(par2List) : par2List.size(), d2, d3, d4, d5);
        final int i = this.func_110668_a(par3CommandSpreadPlayersPosition, par4, par8World, random, d2, d3, d4, d5, acommandspreadplayersposition, par9);
        final double d6 = this.func_110671_a(par2List, par8World, acommandspreadplayersposition, par9);
        notifyAdmins(par1ICommandSender, "commands.spreadplayers.success." + (par9 ? "teams" : "players"), new Object[] {Integer.valueOf(acommandspreadplayersposition.length), Double.valueOf(par3CommandSpreadPlayersPosition.field_111101_a), Double.valueOf(par3CommandSpreadPlayersPosition.field_111100_b)});

        if (acommandspreadplayersposition.length > 1)
        {
            par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.spreadplayers.info." + (par9 ? "teams" : "players"), new Object[] {String.format("%.2f", new Object[]{Double.valueOf(d6)}), Integer.valueOf(i)}));
        }
    }

    private int func_110667_a(final List par1List)
    {
        final HashSet hashset = Sets.newHashSet();
        final Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            final EntityLivingBase entitylivingbase = (EntityLivingBase)iterator.next();

            if (entitylivingbase instanceof EntityPlayer)
            {
                hashset.add(((EntityPlayer)entitylivingbase).getTeam());
            }
            else
            {
                hashset.add((Object)null);
            }
        }

        return hashset.size();
    }

    private int func_110668_a(final CommandSpreadPlayersPosition par1CommandSpreadPlayersPosition, final double par2, final World par4World, final Random par5Random, final double par6, final double par8, final double par10, final double par12, final CommandSpreadPlayersPosition[] par14ArrayOfCommandSpreadPlayersPosition, final boolean par15)
    {
        boolean flag1 = true;
        double d5 = 3.4028234663852886E38D;
        int i;

        for (i = 0; i < 10000 && flag1; ++i)
        {
            flag1 = false;
            d5 = 3.4028234663852886E38D;
            CommandSpreadPlayersPosition commandspreadplayersposition1;
            int j;

            for (int k = 0; k < par14ArrayOfCommandSpreadPlayersPosition.length; ++k)
            {
                final CommandSpreadPlayersPosition commandspreadplayersposition2 = par14ArrayOfCommandSpreadPlayersPosition[k];
                j = 0;
                commandspreadplayersposition1 = new CommandSpreadPlayersPosition();

                for (int l = 0; l < par14ArrayOfCommandSpreadPlayersPosition.length; ++l)
                {
                    if (k != l)
                    {
                        final CommandSpreadPlayersPosition commandspreadplayersposition3 = par14ArrayOfCommandSpreadPlayersPosition[l];
                        final double d6 = commandspreadplayersposition2.func_111099_a(commandspreadplayersposition3);
                        d5 = Math.min(d6, d5);

                        if (d6 < par2)
                        {
                            ++j;
                            commandspreadplayersposition1.field_111101_a += commandspreadplayersposition3.field_111101_a - commandspreadplayersposition2.field_111101_a;
                            commandspreadplayersposition1.field_111100_b += commandspreadplayersposition3.field_111100_b - commandspreadplayersposition2.field_111100_b;
                        }
                    }
                }

                if (j > 0)
                {
                    commandspreadplayersposition1.field_111101_a /= (double)j;
                    commandspreadplayersposition1.field_111100_b /= (double)j;
                    final double d7 = (double)commandspreadplayersposition1.func_111096_b();

                    if (d7 > 0.0D)
                    {
                        commandspreadplayersposition1.func_111095_a();
                        commandspreadplayersposition2.func_111094_b(commandspreadplayersposition1);
                    }
                    else
                    {
                        commandspreadplayersposition2.func_111097_a(par5Random, par6, par8, par10, par12);
                    }

                    flag1 = true;
                }

                if (commandspreadplayersposition2.func_111093_a(par6, par8, par10, par12))
                {
                    flag1 = true;
                }
            }

            if (!flag1)
            {
                final CommandSpreadPlayersPosition[] acommandspreadplayersposition1 = par14ArrayOfCommandSpreadPlayersPosition;
                final int i1 = par14ArrayOfCommandSpreadPlayersPosition.length;

                for (j = 0; j < i1; ++j)
                {
                    commandspreadplayersposition1 = acommandspreadplayersposition1[j];

                    if (!commandspreadplayersposition1.func_111098_b(par4World))
                    {
                        commandspreadplayersposition1.func_111097_a(par5Random, par6, par8, par10, par12);
                        flag1 = true;
                    }
                }
            }
        }

        if (i >= 10000)
        {
            throw new CommandException("commands.spreadplayers.failure." + (par15 ? "teams" : "players"), new Object[] {Integer.valueOf(par14ArrayOfCommandSpreadPlayersPosition.length), Double.valueOf(par1CommandSpreadPlayersPosition.field_111101_a), Double.valueOf(par1CommandSpreadPlayersPosition.field_111100_b), String.format("%.2f", new Object[]{Double.valueOf(d5)})});
        }
        else
        {
            return i;
        }
    }

    private double func_110671_a(final List par1List, final World par2World, final CommandSpreadPlayersPosition[] par3ArrayOfCommandSpreadPlayersPosition, final boolean par4)
    {
        double d0 = 0.0D;
        int i = 0;
        final HashMap hashmap = Maps.newHashMap();

        for (int j = 0; j < par1List.size(); ++j)
        {
            final EntityLivingBase entitylivingbase = (EntityLivingBase)par1List.get(j);
            final CommandSpreadPlayersPosition commandspreadplayersposition;

            if (par4)
            {
                final Team team = entitylivingbase instanceof EntityPlayer ? ((EntityPlayer)entitylivingbase).getTeam() : null;

                if (!hashmap.containsKey(team))
                {
                    hashmap.put(team, par3ArrayOfCommandSpreadPlayersPosition[i++]);
                }

                commandspreadplayersposition = (CommandSpreadPlayersPosition)hashmap.get(team);
            }
            else
            {
                commandspreadplayersposition = par3ArrayOfCommandSpreadPlayersPosition[i++];
            }

            entitylivingbase.setPositionAndUpdate((double)((float)MathHelper.floor_double(commandspreadplayersposition.field_111101_a) + 0.5F), (double)commandspreadplayersposition.func_111092_a(par2World), (double)MathHelper.floor_double(commandspreadplayersposition.field_111100_b) + 0.5D);
            double d1 = Double.MAX_VALUE;

            for (int k = 0; k < par3ArrayOfCommandSpreadPlayersPosition.length; ++k)
            {
                if (commandspreadplayersposition != par3ArrayOfCommandSpreadPlayersPosition[k])
                {
                    final double d2 = commandspreadplayersposition.func_111099_a(par3ArrayOfCommandSpreadPlayersPosition[k]);
                    d1 = Math.min(d2, d1);
                }
            }

            d0 += d1;
        }

        d0 /= (double)par1List.size();
        return d0;
    }

    private CommandSpreadPlayersPosition[] func_110670_a(final Random par1Random, final int par2, final double par3, final double par5, final double par7, final double par9)
    {
        final CommandSpreadPlayersPosition[] acommandspreadplayersposition = new CommandSpreadPlayersPosition[par2];

        for (int j = 0; j < acommandspreadplayersposition.length; ++j)
        {
            final CommandSpreadPlayersPosition commandspreadplayersposition = new CommandSpreadPlayersPosition();
            commandspreadplayersposition.func_111097_a(par1Random, par3, par5, par7, par9);
            acommandspreadplayersposition[j] = commandspreadplayersposition;
        }

        return acommandspreadplayersposition;
    }
}
