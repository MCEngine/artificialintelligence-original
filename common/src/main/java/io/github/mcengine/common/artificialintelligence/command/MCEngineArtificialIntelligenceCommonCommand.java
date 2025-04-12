package io.github.mcengine.common.artificialintelligence.command;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MCEngineArtificialIntelligenceCommonCommand implements CommandExecutor {

    private final Plugin plugin;
    private final MCEngineArtificialIntelligenceApi aiApi;

    public MCEngineArtificialIntelligenceCommonCommand(Plugin plugin, MCEngineArtificialIntelligenceApi aiApi) {
        this.plugin = plugin;
        this.aiApi = aiApi;
    }

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

        // Run the API call asynchronously
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
                // Send response back on the main thread
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.GREEN + "[AI]: " + ChatColor.WHITE + finalResponse);
                    }
                }.runTask(plugin); // runs on main thread
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
