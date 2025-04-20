package io.github.mcengine.api.artificialintelligence;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallingLoader {

    private final MCEngineArtificialIntelligenceApiUtil logger;

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
        this.logger = new MCEngineArtificialIntelligenceApiUtil(plugin.getLogger());
        File rootFolder = new File(plugin.getDataFolder(), "functions");
        if (!rootFolder.exists()) rootFolder.mkdirs();

        loadJsonFilesRecursively(plugin, rootFolder);
    }

    private void loadJsonFilesRecursively(Plugin plugin, File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadJsonFilesRecursively(plugin, file);
            } else if (file.isFile() && file.getName().endsWith(".json")) {
                logger.info("[FunctionCallingLoader] Loaded file from: " + file.getAbsolutePath());
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
                    logger.warn("Failed to load function file: " + file.getPath());
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> match(Player player, String input) {
        List<String> results = new ArrayList<>();
        String lowerInput = input.toLowerCase().trim();
    
        for (FunctionRule rule : mergedRules) {
            for (String pattern : rule.match) {
                if (lowerInput.equals(pattern.toLowerCase())) {
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
