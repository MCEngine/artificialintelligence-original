package io.github.mcengine.api.artificialintelligence;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import io.github.mcengine.api.artificialintelligence.IMCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.functions.calling.IFunctionCallingLoader;
import io.github.mcengine.api.artificialintelligence.functions.calling.FunctionRule;
import io.github.mcengine.api.artificialintelligence.functions.calling.json.FunctionCallingJson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallingLoader {

    private final IMCEngineArtificialIntelligenceApi aiApi;
    private final List<FunctionRule> mergedRules = new ArrayList<>();

    public FunctionCallingLoader(Plugin plugin, IMCEngineArtificialIntelligenceApi aiApi) {
        this.aiApi = aiApi;

        String dbType = plugin.getConfig().getString("dbType", "json").toLowerCase();

        File jsonFolder = null;
        if (dbType.equals("json")) {
            jsonFolder = new File(plugin.getDataFolder(), plugin.getConfig().getString("db.json.path", "json"));
        }

        IFunctionCallingLoader orm = switch (dbType) {
            case "json" -> new FunctionCallingJson(jsonFolder);
            default -> new FunctionCallingJson(jsonFolder);
        };

        List<FunctionRule> rules = orm.loadFunctionRules();
        if (rules != null) mergedRules.addAll(rules);
    }

    public List<String> match(Player player, String input) {
        List<String> results = new ArrayList<>();
        String lowerInput = input.toLowerCase().trim();
    
        for (FunctionRule rule : mergedRules) {
            for (String pattern : rule.match) {
                String lowerPattern = pattern.toLowerCase();
                if (lowerInput.contains(lowerPattern) || lowerPattern.contains(lowerInput)) {
                    String resolved = rule.response
                            .replace("{player_name}", player.getName())
                            .replace("{player_uuid}", player.getUniqueId().toString());
                    results.add(resolved);
                    break;
                }
            }
        }
        return results;
    }
}
