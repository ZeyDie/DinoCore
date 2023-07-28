package org.bukkit.conversations;

/**
 * An ExactMatchConversationCanceller cancels a conversation if the user enters an exact input string
 */
public class ExactMatchConversationCanceller implements ConversationCanceller {
    private String escapeSequence;

    /**
     * Builds an ExactMatchConversationCanceller.
     *
     * @param escapeSequence The string that, if entered by the user, will cancel the conversation.
     */
    public ExactMatchConversationCanceller(final String escapeSequence) {
        this.escapeSequence = escapeSequence;
    }
    
    public void setConversation(final Conversation conversation) {}

    public boolean cancelBasedOnInput(final ConversationContext context, final String input) {
        return input.equals(escapeSequence);
    }

    public ConversationCanceller clone() {
        return new ExactMatchConversationCanceller(escapeSequence);
    }
}
