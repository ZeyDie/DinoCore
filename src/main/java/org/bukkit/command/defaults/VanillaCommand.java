package org.bukkit.command.defaults;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class VanillaCommand extends Command {
    static final int MAX_COORD = 30000000;
    static final int MIN_COORD_MINUS_ONE = -30000001;
    static final int MIN_COORD = -30000000;

    protected VanillaCommand(final String name) {
        super(name);
    }

    protected VanillaCommand(final String name, final String description, final String usageMessage, final List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public boolean matches(final String input) {
        return input.equalsIgnoreCase(this.getName());
    }

    protected int getInteger(final CommandSender sender, final String value, final int min) {
        return getInteger(sender, value, min, Integer.MAX_VALUE);
    }

    int getInteger(final CommandSender sender, final String value, final int min, final int max) {
        return getInteger(sender, value, min, max, false);
    }

    int getInteger(final CommandSender sender, final String value, final int min, final int max, final boolean Throws) {
        int i = min;

        try {
            i = Integer.valueOf(value);
        } catch (final NumberFormatException ex) {
            if (Throws) {
                throw new NumberFormatException(String.format("%s is not a valid number", value));
            }
        }

        if (i < min) {
            i = min;
        } else if (i > max) {
            i = max;
        }

        return i;
    }

    Integer getInteger(final String value) {
        try {
            return Integer.valueOf(value);
        } catch (final NumberFormatException ex) {
            return null;
        }
    }

    public static double getRelativeDouble(final double original, final CommandSender sender, final String input) {
        if (input.startsWith("~")) {
            final double value = getDouble(sender, input.substring(1));
            if (value == MIN_COORD_MINUS_ONE) {
                return MIN_COORD_MINUS_ONE;
            }
            return original + value;
        } else {
            return getDouble(sender, input);
        }
    }

    public static double getDouble(final CommandSender sender, final String input) {
        try {
            return Double.parseDouble(input);
        } catch (final NumberFormatException ex) {
            return MIN_COORD_MINUS_ONE;
        }
    }

    public static double getDouble(final CommandSender sender, final String input, final double min, final double max) {
        double result = getDouble(sender, input);

        // TODO: This should throw an exception instead.
        if (result < min) {
            result = min;
        } else if (result > max) {
            result = max;
        }

        return result;
    }

    String createString(final String[] args, final int start) {
        return createString(args, start, " ");
    }

    String createString(final String[] args, final int start, final String glue) {
        final StringBuilder string = new StringBuilder();

        for (int x = start; x < args.length; x++) {
            string.append(args[x]);
            if (x != args.length - 1) {
                string.append(glue);
            }
        }

        return string.toString();
    }
}
