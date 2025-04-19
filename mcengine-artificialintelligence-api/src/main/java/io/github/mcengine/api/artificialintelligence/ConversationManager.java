package io.github.mcengine.api.artificialintelligence;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConversationManager {

    private static final Map<UUID, StringBuilder> playerConversations = new ConcurrentHashMap<>();

    public static void startConversation(Player player) {
        playerConversations.put(player.getUniqueId(), new StringBuilder());
    }

    public static void appendToConversation(Player player, String message) {
        playerConversations.computeIfAbsent(player.getUniqueId(), k -> new StringBuilder()).append(message).append("\n");
    }

    public static String getConversation(Player player) {
        return playerConversations.getOrDefault(player.getUniqueId(), new StringBuilder()).toString();
    }

    public static void endConversation(Player player) {
        playerConversations.remove(player.getUniqueId());
    }
}
