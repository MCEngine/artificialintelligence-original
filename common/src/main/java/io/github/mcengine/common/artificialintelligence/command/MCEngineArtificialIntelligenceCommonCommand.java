package io.github.mcengine.common.artificialintelligence.command;

import io.github.mcengine.api.artificialintelligence.ConversationManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command executor for the /ai command.
 * This allows players to interact with an AI model by sending a message and receiving a response.
 * All AI communication is performed asynchronously to avoid blocking the main thread.
 */
public class MCEngineArtificialIntelligenceCommonCommand implements CommandExecutor {

    private final Plugin plugin;
    private final Runnable reloadTask;

    public MCEngineArtificialIntelligenceCommonCommand(Plugin plugin, Runnable reloadTask) {
        this.plugin = plugin;
        this.reloadTask = reloadTask;
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

        // Handle /ai reload
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mcengine.artificialintelligence.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to reload the plugin.");
                return true;
            }

            reloadTask.run();
            ConversationManager.terminateAll();
            sender.sendMessage(ChatColor.GREEN + "AI configuration reloaded.");
            return true;
        }

        // Handle /ai to start conversation
        if (!player.hasPermission("mcengine.artificialintelligence.use")) {
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
