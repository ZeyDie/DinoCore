package org.bukkit.conversations;

import org.apache.commons.lang.math.NumberUtils;

/**
 * NumericPrompt is the base class for any prompt that requires a {@link Number} response from the user.
 */
public abstract class NumericPrompt extends ValidatingPrompt{
    public NumericPrompt() {
        super();
    }

    @Override
    protected boolean isInputValid(final ConversationContext context, final String input) {
        return NumberUtils.isNumber(input) && isNumberValid(context, NumberUtils.createNumber(input));
    }

    /**
     * Override this method to do further validation on the numeric player input after the input has been determined
     * to actually be a number.
     *
     * @param context Context information about the conversation.
     * @param input The number the player provided.
     * @return The validity of the player's input.
     */
    protected boolean isNumberValid(final ConversationContext context, final Number input) {
        return true;
    }

    @Override
    protected Prompt acceptValidatedInput(final ConversationContext context, final String input) {
        try
        {
            return acceptValidatedInput(context, NumberUtils.createNumber(input));
        } catch (final NumberFormatException e) {
            return acceptValidatedInput(context, NumberUtils.INTEGER_ZERO);
        }
    }

    /**
     * Override this method to perform some action with the user's integer response.
     *
     * @param context Context information about the conversation.
     * @param input The user's response as a {@link Number}.
     * @return The next {@link Prompt} in the prompt graph.
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext context, Number input);

    @Override
    protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
        if (NumberUtils.isNumber(invalidInput)) {
            return getFailedValidationText(context, NumberUtils.createNumber(invalidInput));
        } else {
            return getInputNotNumericText(context, invalidInput);
        }
    }

    /**
     * Optionally override this method to display an additional message if the user enters an invalid number.
     *
     * @param context Context information about the conversation.
     * @param invalidInput The invalid input provided by the user.
     * @return A message explaining how to correct the input.
     */
    protected String getInputNotNumericText(final ConversationContext context, final String invalidInput) {
        return null;
    }

    /**
     * Optionally override this method to display an additional message if the user enters an invalid numeric input.
     *
     * @param context Context information about the conversation.
     * @param invalidInput The invalid input provided by the user.
     * @return A message explaining how to correct the input.
     */
    protected String getFailedValidationText(final ConversationContext context, final Number invalidInput) {
        return null;
    }
}
