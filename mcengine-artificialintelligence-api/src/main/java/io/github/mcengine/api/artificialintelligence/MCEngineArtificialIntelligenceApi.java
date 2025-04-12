package io.github.mcengine.api.artificialintelligence;

import io.github.mcengine.api.artificialintelligence.model.MCEngineArtificialIntelligenceApiDeepSeek;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtil;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class MCEngineArtificialIntelligenceApi {

    private final Plugin plugin;
    private final MCEngineArtificialIntelligenceApiUtil logger;
    private final IMCEngineArtificialIntelligenceApi ai;

    public MCEngineArtificialIntelligenceApi(Plugin plugin) {
        this.plugin = plugin;
        this.logger = new MCEngineArtificialIntelligenceApiUtil(plugin.getLogger());
        String aiType = plugin.getConfig().getString("ai", "deepseek");

        if (aiType.equalsIgnoreCase("deepseek")) {
            this.ai = new MCEngineArtificialIntelligenceApiDeepSeek(plugin);
        } else {
            throw new IllegalArgumentException("Unsupported AI type: " + aiType);
        }
    }
    
    public String getResponse(String message) {
        return ai.getResponse(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }
    
    public void error(String message) {
        logger.error(message);
    }
}
