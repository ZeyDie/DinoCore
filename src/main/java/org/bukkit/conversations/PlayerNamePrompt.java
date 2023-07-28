package org.bukkit.conversations;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * PlayerNamePrompt is the base class for any prompt that requires the player to enter another player's name.
 */
public abstract class PlayerNamePrompt extends ValidatingPrompt{
    private Plugin plugin;

    public PlayerNamePrompt(final Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    protected boolean isInputValid(final ConversationContext context, final String input) {
        return plugin.getServer().getPlayer(input) != null;
        
    }

    @Override
    protected Prompt acceptValidatedInput(final ConversationContext context, final String input) {
        return acceptValidatedInput(context, plugin.getServer().getPlayer(input));
    }

    /**
     * Override this method to perform some action with the user's player name response.
     *
     * @param context Context information about the conversation.
     * @param input The user's player name response.
     * @return The next {@link Prompt} in the prompt graph.
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext context, Player input);
}
