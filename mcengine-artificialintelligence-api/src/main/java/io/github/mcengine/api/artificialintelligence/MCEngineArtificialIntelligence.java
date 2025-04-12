package io.github.mcengine.api.artificialintelligence;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class MCEngineArtificialIntelligence {

    private final Plugin plugin;
    private final Logger logger;

    public MCEngineArtificialIntelligence(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warning(message);
    }

    public void error(String message) {
        logger.severe(message);
    }
}
