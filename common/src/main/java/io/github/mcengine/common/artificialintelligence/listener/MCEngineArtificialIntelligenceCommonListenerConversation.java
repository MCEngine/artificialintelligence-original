package io.github.mcengine.common.artificialintelligence.listener;

import io.github.mcengine.api.artificialintelligence.ConversationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MCEngineArtificialIntelligenceCommonListenerConversation implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ConversationManager.startConversation(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ConversationManager.endConversation(event.getPlayer());
    }
}


