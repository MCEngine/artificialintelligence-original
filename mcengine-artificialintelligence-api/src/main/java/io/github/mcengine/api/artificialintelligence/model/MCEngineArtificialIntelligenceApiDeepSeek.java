package io.github.mcengine.api.artificialintelligence.model;

import io.github.mcengine.api.artificialintelligence.IMCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtil;
import org.bukkit.plugin.Plugin;

public class MCEngineArtificialIntelligenceApiDeepSeek implements IMCEngineArtificialIntelligenceApi {

    private final Plugin plugin;
    private final String token;
    private final MCEngineArtificialIntelligenceApiUtil logger;

    public MCEngineArtificialIntelligenceApiDeepSeek(Plugin plugin) {
        this.plugin = plugin;
        this.token = plugin.getConfig().getString("deepseek.token", null);
        this.logger = new MCEngineArtificialIntelligenceApiUtil(plugin.getLogger());
    }

    public String getResponse(String message) {
        logger.info("Getting response from DeepSeek API...");
        return null;
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
