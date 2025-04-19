package io.github.mcengine.common.artificialintelligence.command;

import java.util.List;
import io.github.mcengine.api.artificialintelligence.ConversationManager;
import io.github.mcengine.api.artificialintelligence.FunctionCallingLoader;
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

    /** Loader for dynamic function-calling rules. */
    private final FunctionCallingLoader functionLoader;

    /** Whether conversation should be kept. */
    private final boolean keepConversation;

    /**
     * Constructs the AI command executor.
     *
     * @param plugin            The plugin instance.
     * @param aiApi             The AI API to communicate with.
     * @param threadPoolManager The shared thread pool for executing async tasks.
     */
    public MCEngineArtificialIntelligenceCommonCommand(Plugin plugin, MCEngineArtificialIntelligenceApi aiApi, ThreadPoolManager threadPoolManager, FunctionCallingLoader functionLoader) {
        this.plugin = plugin;
        this.aiApi = aiApi;
        this.threadPoolManager = threadPoolManager;
        this.functionLoader = functionLoader;
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

        if (!ConversationManager.isActive(player)) {
            ConversationManager.startConversation(player);
            ConversationManager.activate(player);
            player.sendMessage(ChatColor.GREEN + "AI conversation started. Type messages in chat. Type 'quit' to end.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "You are already in a conversation. Type 'quit' to end.");
        }

        return true;
    }
}
