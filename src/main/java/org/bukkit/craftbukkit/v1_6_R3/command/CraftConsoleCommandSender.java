package org.bukkit.craftbukkit.v1_6_R3.command;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.v1_6_R3.conversations.ConversationTracker;

/**
 * Represents CLI input from a console
 */
public class CraftConsoleCommandSender extends ServerCommandSender implements ConsoleCommandSender {

    protected final ConversationTracker conversationTracker = new ConversationTracker();

    protected CraftConsoleCommandSender() {
        super();
    }

    public void sendMessage(final String message) {
        sendRawMessage(message);
    }

    public void sendRawMessage(final String message) {
        System.out.println(ChatColor.stripColor(message));
    }

    public void sendMessage(final String[] messages) {
        for (final String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "CONSOLE";
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(final boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of server console");
    }

    public boolean beginConversation(final Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    public void abandonConversation(final Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    public void abandonConversation(final Conversation conversation, final ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    public void acceptConversationInput(final String input) {
        conversationTracker.acceptConversationInput(input);
    }

    public boolean isConversing() {
        return conversationTracker.isConversing();
    }
}
