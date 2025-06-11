package io.github.mcengine.common.artificialintelligence.functions.calling.json;

import io.github.mcengine.common.artificialintelligence.functions.calling.FunctionRule;
import io.github.mcengine.common.artificialintelligence.functions.calling.IFunctionCallingLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallingJson implements IFunctionCallingLoader {
    private final File rootFolder;

    public FunctionCallingJson(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public List<FunctionRule> loadFunctionRules() {
        List<FunctionRule> rules = new ArrayList<>();
        loadJsonFilesRecursively(rootFolder, rules);
        return rules;
    }

    private void loadJsonFilesRecursively(File folder, List<FunctionRule> rules) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            // System.out.println("File path: " + file.getAbsolutePath());
            if (file.isDirectory()) {
                loadJsonFilesRecursively(file, rules);
            } else if (file.isFile() && file.getName().endsWith(".json")) {
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
                        rules.add(new FunctionRule(matchList, response));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
