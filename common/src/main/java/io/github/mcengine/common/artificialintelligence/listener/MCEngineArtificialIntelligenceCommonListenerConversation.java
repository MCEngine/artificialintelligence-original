package io.github.mcengine.common.artificialintelligence.listener;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtilBotManager;
import io.github.mcengine.common.artificialintelligence.FunctionCallingLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Listener class that intercepts player chat messages and routes them to the AI if an active session exists.
 */
public class MCEngineArtificialIntelligenceCommonListenerConversation implements Listener {

    private final Plugin plugin;
    private final FunctionCallingLoader functionCallingLoader;
    private final boolean keepConversation;

    public MCEngineArtificialIntelligenceCommonListenerConversation(Plugin plugin) {
        this.plugin = plugin;
        this.functionCallingLoader = new FunctionCallingLoader(plugin);
        this.keepConversation = plugin.getConfig().getBoolean("conversation.keep", false);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!MCEngineArtificialIntelligenceApiUtilBotManager.isActive(player)) return;

        event.setCancelled(true);
        event.getRecipients().clear();

        MCEngineArtificialIntelligenceApi api = MCEngineArtificialIntelligenceApi.getApi();

        String message = event.getMessage().trim();

        // Prevent message during wait
        if (api.checkWaitingPlayer(player)) {
            player.sendMessage(ChatColor.RED + "⏳ Please wait for the AI to respond before sending another message.");
            return;
        }

        // Handle quit command
        if (message.equalsIgnoreCase("quit")) {
            MCEngineArtificialIntelligenceApiUtilBotManager.terminate(player);
            Bukkit.getScheduler().runTask(plugin, () ->
                player.sendMessage(ChatColor.RED + "❌ AI conversation ended.")
            );
            return;
        }

        player.sendMessage(ChatColor.GRAY + "[You → AI]: " + ChatColor.WHITE + message);

        List<String> matched;
        try {
            matched = functionCallingLoader.match(player, message);
        } catch (Exception e) {
            matched = List.of();
            plugin.getLogger().warning("Function matching failed: " + e.getMessage());
        }

        String fullMessage = message;

        if (!matched.isEmpty()) {
            StringBuilder sb = new StringBuilder(message).append("\n\n[Function Info]\n");
            for (String entry : matched) {
                sb.append("- ").append(entry).append("\n");
            }
            fullMessage = sb.toString();
        }

        if (keepConversation) {
            MCEngineArtificialIntelligenceApiUtilBotManager.append(player, "You: " + message);
            fullMessage = MCEngineArtificialIntelligenceApiUtilBotManager.get(player);
        }

        String platform = MCEngineArtificialIntelligenceApiUtilBotManager.getPlatform(player);
        String model = MCEngineArtificialIntelligenceApiUtilBotManager.getModel(player);

        api.runBotTask(player, "server", platform, model, fullMessage);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (MCEngineArtificialIntelligenceApiUtilBotManager.isActive(player)) {
            MCEngineArtificialIntelligenceApiUtilBotManager.terminate(player);
        }
    }
}
