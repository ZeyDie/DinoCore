package net.minecraft.command;

import com.google.common.primitives.Doubles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CommandBase implements ICommand
{
    private static IAdminCommand theAdmin;

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public List getCommandAliases()
    {
        return null;
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(final ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(final ICommandSender par1ICommandSender, final String[] par2ArrayOfStr)
    {
        return null;
    }

    /**
     * Parses an int from the given string.
     */
    public static int parseInt(final ICommandSender par0ICommandSender, final String par1Str)
    {
        try
        {
            return Integer.parseInt(par1Str);
        }
        catch (final NumberFormatException numberformatexception)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {par1Str});
        }
    }

    /**
     * Parses an int from the given sring with a specified minimum.
     */
    public static int parseIntWithMin(final ICommandSender par0ICommandSender, final String par1Str, final int par2)
    {
        return parseIntBounded(par0ICommandSender, par1Str, par2, Integer.MAX_VALUE);
    }

    /**
     * Parses an int from the given string within a specified bound.
     */
    public static int parseIntBounded(final ICommandSender par0ICommandSender, final String par1Str, final int par2, final int par3)
    {
        final int k = parseInt(par0ICommandSender, par1Str);

        if (k < par2)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Integer.valueOf(k), Integer.valueOf(par2)});
        }
        else if (k > par3)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Integer.valueOf(k), Integer.valueOf(par3)});
        }
        else
        {
            return k;
        }
    }

    /**
     * Parses a double from the given string or throws an exception if it's not a double.
     */
    public static double parseDouble(final ICommandSender par0ICommandSender, final String par1Str)
    {
        try
        {
            final double d0 = Double.parseDouble(par1Str);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberInvalidException("commands.generic.double.invalid", new Object[] {par1Str});
            }
            else
            {
                return d0;
            }
        }
        catch (final NumberFormatException numberformatexception)
        {
            throw new NumberInvalidException("commands.generic.double.invalid", new Object[] {par1Str});
        }
    }

    public static double func_110664_a(final ICommandSender par0ICommandSender, final String par1Str, final double par2)
    {
        return func_110661_a(par0ICommandSender, par1Str, par2, Double.MAX_VALUE);
    }

    public static double func_110661_a(final ICommandSender par0ICommandSender, final String par1Str, final double par2, final double par4)
    {
        final double d2 = parseDouble(par0ICommandSender, par1Str);

        if (d2 < par2)
        {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d2), Double.valueOf(par2)});
        }
        else if (d2 > par4)
        {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d2), Double.valueOf(par4)});
        }
        else
        {
            return d2;
        }
    }

    public static boolean func_110662_c(final ICommandSender par0ICommandSender, final String par1Str)
    {
        if (!par1Str.equals("true") && !par1Str.equals("1"))
        {
            if (!par1Str.equals("false") && !par1Str.equals("0"))
            {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] {par1Str});
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns the given ICommandSender as a EntityPlayer or throw an exception.
     */
    public static EntityPlayerMP getCommandSenderAsPlayer(final ICommandSender par0ICommandSender)
    {
        if (par0ICommandSender instanceof EntityPlayerMP)
        {
            return (EntityPlayerMP)par0ICommandSender;
        }
        else
        {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP getPlayer(final ICommandSender par0ICommandSender, final String par1Str)
    {
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(par0ICommandSender, par1Str);

        if (entityplayermp != null)
        {
            return entityplayermp;
        }
        else
        {
            entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par1Str);

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                return entityplayermp;
            }
        }
    }

    public static String func_96332_d(final ICommandSender par0ICommandSender, final String par1Str)
    {
        final EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(par0ICommandSender, par1Str);

        if (entityplayermp != null)
        {
            return entityplayermp.getEntityName();
        }
        else if (PlayerSelector.hasArguments(par1Str))
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return par1Str;
        }
    }

    public static String func_82360_a(final ICommandSender par0ICommandSender, final String[] par1ArrayOfStr, final int par2)
    {
        return func_82361_a(par0ICommandSender, par1ArrayOfStr, par2, false);
    }

    public static String func_82361_a(final ICommandSender par0ICommandSender, final String[] par1ArrayOfStr, final int par2, final boolean par3)
    {
        final StringBuilder stringbuilder = new StringBuilder();

        for (int j = par2; j < par1ArrayOfStr.length; ++j)
        {
            if (j > par2)
            {
                stringbuilder.append(" ");
            }

            String s = par1ArrayOfStr[j];

            if (par3)
            {
                final String s1 = PlayerSelector.matchPlayersAsString(par0ICommandSender, s);

                if (s1 != null)
                {
                    s = s1;
                }
                else if (PlayerSelector.hasArguments(s))
                {
                    throw new PlayerNotFoundException();
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static double func_110666_a(final ICommandSender par0ICommandSender, final double par1, final String par3Str)
    {
        return func_110665_a(par0ICommandSender, par1, par3Str, -30000000, 30000000);
    }

    public static double func_110665_a(final ICommandSender par0ICommandSender, final double par1, String par3Str, final int par4, final int par5)
    {
        String par3Str1 = par3Str;
        final boolean flag = par3Str1.startsWith("~");

        if (flag && Double.isNaN(par1))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(par1)});
        }
        else
        {
            double d1 = flag ? par1 : 0.0D;

            if (!flag || par3Str1.length() > 1)
            {
                final boolean flag1 = par3Str1.contains(".");

                if (flag)
                {
                    par3Str1 = par3Str1.substring(1);
                }

                d1 += parseDouble(par0ICommandSender, par3Str1);

                if (!flag1 && !flag)
                {
                    d1 += 0.5D;
                }
            }

            if (par4 != 0 || par5 != 0)
            {
                if (d1 < (double)par4)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d1), Integer.valueOf(par4)});
                }

                if (d1 > (double)par5)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d1), Integer.valueOf(par5)});
                }
            }

            return d1;
        }
    }

    /**
     * Joins the given string array into a "x, y, and z" seperated string.
     */
    public static String joinNiceString(final Object[] par0ArrayOfObj)
    {
        final StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < par0ArrayOfObj.length; ++i)
        {
            final String s = par0ArrayOfObj[i].toString();

            if (i > 0)
            {
                if (i == par0ArrayOfObj.length - 1)
                {
                    stringbuilder.append(" and ");
                }
                else
                {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static String func_96333_a(final Collection par0Collection)
    {
        return joinNiceString(par0Collection.toArray(new String[0]));
    }

    public static String func_110663_b(final Collection par0Collection)
    {
        final String[] astring = new String[par0Collection.size()];
        int i = 0;
        EntityLivingBase entitylivingbase;

        for (final Iterator iterator = par0Collection.iterator(); iterator.hasNext(); astring[i++] = entitylivingbase.getTranslatedEntityName())
        {
            entitylivingbase = (EntityLivingBase)iterator.next();
        }

        return joinNiceString(astring);
    }

    /**
     * Returns true if the given substring is exactly equal to the start of the given string (case insensitive).
     */
    public static boolean doesStringStartWith(final String par0Str, final String par1Str)
    {
        return par1Str.regionMatches(true, 0, par0Str, 0, par0Str.length());
    }

    /**
     * Returns a List of strings (chosen from the given strings) which the last word in the given string array is a
     * beginning-match for. (Tab completion).
     */
    public static List getListOfStringsMatchingLastWord(final String[] par0ArrayOfStr, final String ... par1ArrayOfStr)
    {
        final String s1 = par0ArrayOfStr[par0ArrayOfStr.length - 1];
        final ArrayList arraylist = new ArrayList();
        final String[] astring1 = par1ArrayOfStr;
        final int i = par1ArrayOfStr.length;

        for (int j = 0; j < i; ++j)
        {
            final String s2 = astring1[j];

            if (doesStringStartWith(s1, s2))
            {
                arraylist.add(s2);
            }
        }

        return arraylist;
    }

    /**
     * Returns a List of strings (chosen from the given string iterable) which the last word in the given string array
     * is a beginning-match for. (Tab completion).
     */
    public static List getListOfStringsFromIterableMatchingLastWord(final String[] par0ArrayOfStr, final Iterable par1Iterable)
    {
        final String s = par0ArrayOfStr[par0ArrayOfStr.length - 1];
        final ArrayList arraylist = new ArrayList();
        final Iterator iterator = par1Iterable.iterator();

        while (iterator.hasNext())
        {
            final String s1 = (String)iterator.next();

            if (doesStringStartWith(s, s1))
            {
                arraylist.add(s1);
            }
        }

        return arraylist;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(final String[] par1ArrayOfStr, final int par2)
    {
        return false;
    }

    public static void notifyAdmins(final ICommandSender par0ICommandSender, final String par1Str, final Object ... par2ArrayOfObj)
    {
        notifyAdmins(par0ICommandSender, 0, par1Str, par2ArrayOfObj);
    }

    public static void notifyAdmins(final ICommandSender par0ICommandSender, final int par1, final String par2Str, final Object ... par3ArrayOfObj)
    {
        if (theAdmin != null)
        {
            theAdmin.notifyAdmins(par0ICommandSender, par1, par2Str, par3ArrayOfObj);
        }
    }

    /**
     * Sets the static IAdminCommander.
     */
    public static void setAdminCommander(final IAdminCommand par0IAdminCommand)
    {
        theAdmin = par0IAdminCommand;
    }

    /**
     * Compares the name of this command to the name of the given command.
     */
    public int compareTo(final ICommand par1ICommand)
    {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    public int compareTo(final Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}
