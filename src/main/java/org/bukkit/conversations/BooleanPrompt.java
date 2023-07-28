package org.bukkit.conversations;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;

/**
 * BooleanPrompt is the base class for any prompt that requires a boolean response from the user.
 */
public abstract class BooleanPrompt extends ValidatingPrompt{

    public BooleanPrompt() {
        super();
    }

    @Override
    protected boolean isInputValid(final ConversationContext context, final String input) {
        final String[] accepted = {"true", "false", "on", "off", "yes", "no" /* Spigot: */, "y", "n", "1", "0", "right", "wrong", "correct", "incorrect", "valid", "invalid"}; // Spigot
        return ArrayUtils.contains(accepted, input.toLowerCase());
    }

    @Override
    protected Prompt acceptValidatedInput(final ConversationContext context, String input) {
        String input1 = input;
        if (input1.equalsIgnoreCase("y") || input1.equals("1") || input1.equalsIgnoreCase("right") || input1.equalsIgnoreCase("correct") || input1.equalsIgnoreCase("valid")) input1 = "true"; // Spigot
        return acceptValidatedInput(context, BooleanUtils.toBoolean(input1));
    }

    /**
     * Override this method to perform some action with the user's boolean response.
     *
     * @param context Context information about the conversation.
     * @param input The user's boolean response.
     * @return The next {@link Prompt} in the prompt graph.
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext context, boolean input);
}
