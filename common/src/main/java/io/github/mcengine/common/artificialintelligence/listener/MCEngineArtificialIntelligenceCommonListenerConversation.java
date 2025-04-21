package io.github.mcengine.common.artificialintelligence.listener;

import io.github.mcengine.api.artificialintelligence.ConversationManager;
import io.github.mcengine.api.artificialintelligence.FunctionCallingLoader;
import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.ThreadPoolManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class MCEngineArtificialIntelligenceCommonListenerConversation implements Listener {

    private final Plugin plugin;
    private final MCEngineArtificialIntelligenceApi aiApi;
    private final ThreadPoolManager threadPoolManager;
    private final FunctionCallingLoader functionLoader;
    private final boolean keepConversation;

    public MCEngineArtificialIntelligenceCommonListenerConversation(Plugin plugin, MCEngineArtificialIntelligenceApi aiApi, ThreadPoolManager threadPoolManager, FunctionCallingLoader functionLoader) {
        this.plugin = plugin;
        this.aiApi = aiApi;
        this.threadPoolManager = threadPoolManager;
        this.functionLoader = functionLoader;
        this.keepConversation = plugin.getConfig().getBoolean("conversation.keep", false);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!ConversationManager.isActive(player)) return;

        event.setCancelled(true);
        event.getRecipients().clear();
        String message = event.getMessage().trim();

        player.sendMessage(ChatColor.GRAY + "[You → AI]: " + ChatColor.WHITE + message);

        if (message.equalsIgnoreCase("quit")) {
            ConversationManager.terminate(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.RED + "AI conversation ended.");
                }
            }.runTask(plugin);
            return;
        }

        StringBuilder contextInfo = new StringBuilder();
        List<String> matchedResponses = functionLoader.match(player, message);
        for (String res : matchedResponses) {
            contextInfo.append(res).append(" ");
        }

        String userInput = "You: " + message;
        if (contextInfo.length() > 0) {
            userInput += " (" + contextInfo.toString().trim() + ")";
        }

        String inputToAI;

        if (keepConversation) {
            ConversationManager.append(player, userInput);
            inputToAI = ConversationManager.get(player);
        } else {
            inputToAI = userInput;
        }

        threadPoolManager.submit(() -> {
            String response = aiApi.getResponse(inputToAI);
            if (keepConversation) {
                ConversationManager.append(player, "AI: " + response);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.GREEN + "[AI]: " + ChatColor.WHITE + response);
                }
            }.runTask(plugin);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ConversationManager.isActive(player)) {
            ConversationManager.terminate(player);
        }
    }
}
