package io.github.mcengine.common.artificialintelligence.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tab completer for the /ai command.
 * Provides suggestions for subcommands such as "reload".
 */
public class MCEngineArtificialIntelligenceCommonTabCompleter implements TabCompleter {

    /**
     * Provides tab completion suggestions for the /ai command.
     *
     * @param sender  The sender of the command.
     * @param command The command object.
     * @param alias   The alias used for the command.
     * @param args    The arguments provided with the command.
     * @return A list of possible completions for the last argument.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Only one argument supported currently
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("mcengine.artificialintelligence.reload")) {
                if ("reload".startsWith(args[0].toLowerCase())) {
                    completions.add("reload");
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
