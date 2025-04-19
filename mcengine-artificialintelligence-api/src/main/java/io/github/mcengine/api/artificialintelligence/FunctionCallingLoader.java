package io.github.mcengine.api.artificialintelligence;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallingLoader {

    public static class FunctionRule {
        public List<String> match;
        public String response;

        public FunctionRule(List<String> match, String response) {
            this.match = match;
            this.response = response;
        }
    }

    private final List<FunctionRule> mergedRules = new ArrayList<>();

    public FunctionCallingLoader(Plugin plugin) {
        File folder = new File(plugin.getDataFolder(), "functions");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONArray jsonArray = new JSONArray(content);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONArray matchArray = obj.getJSONArray("match");

                    List<String> matchList = new ArrayList<>();
                    for (int j = 0; j < matchArray.length(); j++) {
                        matchList.add(matchArray.getString(j));
                    }

                    String response = obj.getString("response");
                    mergedRules.add(new FunctionRule(matchList, response));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load function file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public List<String> match(Player player, String input) {
        List<String> results = new ArrayList<>();
        String lowerInput = input.toLowerCase();

        for (FunctionRule rule : mergedRules) {
            for (String pattern : rule.match) {
                if (lowerInput.contains(pattern)) {
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
