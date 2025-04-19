package io.github.mcengine.common.artificialintelligence.command;

import io.github.mcengine.api.artificialintelligence.ConversationManager;
import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.ThreadPoolManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Command executor for the /ai command.
 * This allows players to interact with an AI model by sending a message and receiving a response.
 * All AI communication is performed asynchronously to avoid blocking the main thread.
 */
public class MCEngineArtificialIntelligenceCommonCommand implements CommandExecutor {

    /** The Bukkit plugin instance used for context and scheduling. */
    private final Plugin plugin;

    /** The AI API used to handle chat requests. */
    private final MCEngineArtificialIntelligenceApi aiApi;

    /** Thread pool manager for offloading heavy/long tasks. */
    private final ThreadPoolManager threadPoolManager;

    /** Whether conversation should be kept. */
    private final boolean keepConversation;

    /**
     * Constructs the AI command executor.
     *
     * @param plugin            The plugin instance.
     * @param aiApi             The AI API to communicate with.
     * @param threadPoolManager The shared thread pool for executing async tasks.
     */
    public MCEngineArtificialIntelligenceCommonCommand(Plugin plugin, MCEngineArtificialIntelligenceApi aiApi, ThreadPoolManager threadPoolManager) {
        this.plugin = plugin;
        this.aiApi = aiApi;
        this.threadPoolManager = threadPoolManager;
        this.keepConversation = plugin.getConfig().getBoolean("conversation.keep", false);
    }

    /**
     * Handles the /ai command.
     *
     * @param sender  The sender of the command.
     * @param command The command object.
     * @param label   The alias used for the command.
     * @param args    Arguments passed to the command.
     * @return true if the command was executed successfully.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
    
        Player player = (Player) sender;
    
        if (!player.hasPermission("mcengine.artificialintelligence.deepseek")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
    
        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /ai <message>");
            return true;
        }
    
        String message = String.join(" ", args);
        
        if (keepConversation) {
            ConversationManager.append(player, "You: " + message);
        }
    
        threadPoolManager.submit(() -> {
            String inputToAI = keepConversation ? ConversationManager.get(player) : "You: " + message;
            String response;
            try {
                response = aiApi.getResponse(inputToAI);
            } catch (Exception e) {
                e.printStackTrace();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.RED + "Error while contacting AI.");
                    }
                }.runTask(plugin);
                return;
            }

            if (response == null || response.isEmpty()) {
                response = "No response from AI.";
            }

            final String finalResponse = response;

            if (keepConversation) {
                ConversationManager.append(player, "AI: " + finalResponse);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.GREEN + "[AI]: " + ChatColor.WHITE + finalResponse);
                }
            }.runTask(plugin);
        });

        return true;
    }
}
