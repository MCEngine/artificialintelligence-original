package io.github.mcengine.spigotmc.artificialintelligence.engine;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.common.artificialintelligence.command.MCEngineArtificialIntelligenceCommonCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineArtificialIntelligenceSpigotMC extends JavaPlugin {

    private static MCEngineArtificialIntelligenceSpigotMC instance;
    private MCEngineArtificialIntelligenceApi artificialintelligenceApi;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config if not already present
        saveDefaultConfig();

        try {
            artificialintelligenceApi = new MCEngineArtificialIntelligenceApi(this);
            getLogger().info("AI Engine initialized successfully.");

            getCommand("ai").setExecutor(
                new MCEngineArtificialIntelligenceCommonCommand(artificialintelligenceApi)
            );
        } catch (Exception e) {
            getLogger().severe("Failed to initialize AI Engine: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("AI Engine disabled.");
    }

    public static MCEngineArtificialIntelligenceSpigotMC getInstance() {
        return instance;
    }

    public MCEngineArtificialIntelligenceApi getArtificialIntelligenceApi() {
        return artificialintelligenceApi;
    }
}
