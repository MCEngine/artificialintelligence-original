package io.github.mcengine.common.artificialintelligence.command;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtilBotManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command executor for the /ai command.
 * This allows players to start or restart an AI conversation session with a specified platform and model.
 * If a platform and model are not specified, it continues using the previous session data.
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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Get API instance
        MCEngineArtificialIntelligenceApi api = MCEngineArtificialIntelligenceApi.getApi();

        if (args.length == 0) {
            if (!MCEngineArtificialIntelligenceApiUtilBotManager.isActive(player)) {
                player.sendMessage(ChatColor.RED + "No active session found. Use /ai <platform> <model> to start.");
                return true;
            }

            MCEngineArtificialIntelligenceApiUtilBotManager.activate(player);
            player.sendMessage(ChatColor.GREEN + "You resumed chatting with the AI.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /ai <platform> <model>");
            return true;
        }

        String platform = args[0];
        String model = args[1];

        MCEngineArtificialIntelligenceApiUtilBotManager.setModel(player, platform, model);
        MCEngineArtificialIntelligenceApiUtilBotManager.startConversation(player);
        MCEngineArtificialIntelligenceApiUtilBotManager.activate(player);

        player.sendMessage(ChatColor.GREEN + "You are now chatting with the AI.");
        player.sendMessage(ChatColor.GRAY + "Type your message in chat. Type 'quit' to end the conversation.");

        return true;
    }
}
