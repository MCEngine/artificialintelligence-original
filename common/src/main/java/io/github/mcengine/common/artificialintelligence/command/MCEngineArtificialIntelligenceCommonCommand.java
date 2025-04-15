package io.github.mcengine.common.artificialintelligence.command;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Command executor for the /ai command, allowing players to interact with an AI via chat.
 * Sends user messages to the AI and displays the response asynchronously.
 */
public class MCEngineArtificialIntelligenceCommonCommand implements CommandExecutor {

    /** The Bukkit plugin instance. */
    private final Plugin plugin;

    /** The AI API used to handle chat requests. */
    private final MCEngineArtificialIntelligenceApi aiApi;

    /**
     * Constructs the command executor with the plugin context and AI API.
     *
     * @param plugin The plugin registering this command.
     * @param aiApi  The AI API used to get responses.
     */
    public MCEngineArtificialIntelligenceCommonCommand(Plugin plugin, MCEngineArtificialIntelligenceApi aiApi) {
        this.plugin = plugin;
        this.aiApi = aiApi;
    }

    /**
     * Executes the /ai command.
     * Verifies permissions, constructs the message, and retrieves an AI response asynchronously.
     *
     * @param sender  The command sender.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The message arguments provided by the user.
     * @return true if the command was handled; false otherwise.
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

        // Run the AI call asynchronously to avoid blocking the main thread
        new BukkitRunnable() {
            @Override
            public void run() {
                String response;
                try {
                    response = aiApi.getResponse(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "Error while contacting AI.");
                    return;
                }

                if (response == null || response.isEmpty()) {
                    response = "No response from AI.";
                }

                String finalResponse = response;
                // Send the response back to the player on the main thread
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.GREEN + "[AI]: " + ChatColor.WHITE + finalResponse);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
