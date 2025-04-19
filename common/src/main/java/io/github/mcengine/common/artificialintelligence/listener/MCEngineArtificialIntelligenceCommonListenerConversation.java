package io.github.mcengine.common.artificialintelligence.listener;

import io.github.mcengine.api.artificialintelligence.ConversationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for managing per-player AI conversations.
 * <p>
 * This class listens for player join and quit events to start and end
 * AI conversation tracking using the {@link ConversationManager}.
 */
public class MCEngineArtificialIntelligenceCommonListenerConversation implements Listener {

    /**
     * Handles the {@link PlayerJoinEvent}.
     * <p>
     * Starts a new AI conversation for the player upon joining the server.
     *
     * @param event the event triggered when a player joins
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ConversationManager.startConversation(event.getPlayer());
    }

    /**
     * Handles the {@link PlayerQuitEvent}.
     * <p>
     * Ends and removes the player's AI conversation when they leave the server.
     *
     * @param event the event triggered when a player quits
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ConversationManager.end(event.getPlayer());
    }
}
