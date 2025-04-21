package io.github.mcengine.api.artificialintelligence;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConversationManager {

    private static final Map<UUID, StringBuilder> playerConversations = new ConcurrentHashMap<>();
    private static final Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();

    public static void startConversation(Player player) {
        playerConversations.put(player.getUniqueId(), new StringBuilder());
    }

    public static void append(Player player, String message) {
        playerConversations.computeIfAbsent(player.getUniqueId(), k -> new StringBuilder()).append(message).append("\n");
    }

    public static String get(Player player) {
        return playerConversations.getOrDefault(player.getUniqueId(), new StringBuilder()).toString();
    }

    public static void end(Player player) {
        playerConversations.remove(player.getUniqueId());
    }

    public static void activate(Player player) {
        activePlayers.add(player.getUniqueId());
    }

    public static void deactivate(Player player) {
        activePlayers.remove(player.getUniqueId());
    }

    public static boolean isActive(Player player) {
        return activePlayers.contains(player.getUniqueId());
    }

    public static void terminate(Player player) {
        end(player);
        deactivate(player);
    }

    public static void terminateAll() {
        for (UUID uuid : Set.copyOf(activePlayers)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                terminate(player);
                player.sendMessage("§cYour AI session has ended due to the plugin being reloaded or disabled.");
            }
        }
    
        // Fallback in case any UUIDs weren't removed (e.g., offline players)
        playerConversations.clear();
        activePlayers.clear();
    }
}
