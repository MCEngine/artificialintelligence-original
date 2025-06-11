package io.github.mcengine.common.artificialintelligence.tabcompleter;

import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtilAi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Tab completer for the /ai command.
 * Suggests platform names and model names based on registered models,
 * and includes "reload" subcommand.
 */
public class MCEngineArtificialIntelligenceCommonTabCompleter implements TabCompleter {

    /**
     * Provides tab completion for /ai command.
     *
     * @param sender  The sender of the command.
     * @param command The command object.
     * @param label   The alias used for the command.
     * @param args    The arguments passed to the command.
     * @return A list of completion suggestions.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Map<String, Map<String, ?>> models = MCEngineArtificialIntelligenceApiUtilAi.getAllModels();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();

            // Include "reload" as a top-level suggestion
            if ("reload".startsWith(partial)) {
                suggestions.add("reload");
            }

            for (String platform : models.keySet()) {
                if (platform.toLowerCase().startsWith(partial)) {
                    suggestions.add(platform);
                }
            }

            Collections.sort(suggestions);
            return suggestions;
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
            String platform = args[0];
            if (!models.containsKey(platform)) return Collections.emptyList();

            String partial = args[1].toLowerCase();
            List<String> modelNames = new ArrayList<>();
            for (String model : models.get(platform).keySet()) {
                if (model.toLowerCase().startsWith(partial)) {
                    modelNames.add(model);
                }
            }
            Collections.sort(modelNames);
            return modelNames;
        }

        return Collections.emptyList();
    }
}
