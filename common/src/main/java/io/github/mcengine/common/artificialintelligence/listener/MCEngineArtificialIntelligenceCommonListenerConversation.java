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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Listener class that intercepts player chat messages and routes them to the AI if an active session exists.
 */
public class MCEngineArtificialIntelligenceCommonListenerConversation implements Listener {

    private final Plugin plugin;
    private final MCEngineArtificialIntelligenceApi aiApi;
    private final FunctionCallingLoader functionLoader;
    private final boolean keepConversation;

    public MCEngineArtificialIntelligenceCommonListenerConversation(Plugin plugin, MCEngineArtificialIntelligenceApi aiApi, FunctionCallingLoader functionLoader) {
        this.plugin = plugin;
        this.aiApi = aiApi;
        this.functionLoader = functionLoader;
        this.keepConversation = plugin.getConfig().getBoolean("conversation.keep", false);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!MCEngineArtificialIntelligenceApiUtilBotManager.isActive(player)) return;

        event.setCancelled(true);
        event.getRecipients().clear();

        if (aiApi.checkWaitingPlayer(player)) {
            player.sendMessage(ChatColor.RED + "⏳ Please wait for the AI to respond before sending another message.");
            return;
        }

        String message = event.getMessage().trim();

        player.sendMessage(ChatColor.GRAY + "[You → AI]: " + ChatColor.WHITE + message);

        if (message.equalsIgnoreCase("quit")) {
            MCEngineArtificialIntelligenceApiUtilBotManager.terminate(player);
            Bukkit.getScheduler().runTask(plugin, () ->
                player.sendMessage(ChatColor.RED + "❌ AI conversation ended.")
            );
            return;
        }

        aiApi.setWaiting(player, true);

        StringBuilder contextInfo = new StringBuilder();
        List<String> matchedResponses = functionLoader.match(player, message);
        for (String res : matchedResponses) {
            contextInfo.append(res).append("\n");
        }

        String userInput = message;
        if (contextInfo.length() > 0) {
            userInput += "\n\n[Function Info]\n" + contextInfo;
        }

        if (keepConversation) {
            MCEngineArtificialIntelligenceApiUtilBotManager.append(player, "You: " + message);
            userInput = MCEngineArtificialIntelligenceApiUtilBotManager.get(player);
        }

        String platform = MCEngineArtificialIntelligenceApiUtilBotManager.getPlatform(player);
        String model = MCEngineArtificialIntelligenceApiUtilBotManager.getModel(player);

        aiApi.runBotTask(player, "server", platform, model, userInput);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (MCEngineArtificialIntelligenceApiUtilBotManager.isActive(player)) {
            MCEngineArtificialIntelligenceApiUtilBotManager.terminate(player);
        }
    }
}
