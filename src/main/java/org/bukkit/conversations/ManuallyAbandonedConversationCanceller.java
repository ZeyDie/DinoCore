package org.bukkit.conversations;

/**
 * The ManuallyAbandonedConversationCanceller is only used as part of a {@link ConversationAbandonedEvent} to indicate
 * that the conversation was manually abandoned by programatically calling the abandon() method on it.
 */
public class ManuallyAbandonedConversationCanceller implements ConversationCanceller{
    public void setConversation(final Conversation conversation) {
        throw new UnsupportedOperationException();
    }

    public boolean cancelBasedOnInput(final ConversationContext context, final String input) {
        throw new UnsupportedOperationException();
    }

    public ConversationCanceller clone() {
        throw new UnsupportedOperationException();
    }
}
