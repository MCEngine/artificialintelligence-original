package io.github.mcengine.common.artificialintelligence.command;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MCEngineArtificialIntelligenceCommonCommand implements CommandExecutor {

    private final MCEngineArtificialIntelligenceApi aiApi;

    public MCEngineArtificialIntelligenceCommonCommand(MCEngineArtificialIntelligenceApi aiApi) {
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
        String response = aiApi.getResponse(message);

        if (response == null || response.isEmpty()) {
            player.sendMessage(ChatColor.RED + "AI did not respond.");
        } else {
            player.sendMessage(ChatColor.GREEN + "AI: " + ChatColor.WHITE + response);
        }

        return true;
    }
}
