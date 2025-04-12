package io.github.mcengine.api.artificialintelligence.util;

import java.util.logging.Logger;

public class MCEngineArtificialIntelligenceApiUtil {

    private final Logger logger;

    public MCEngineArtificialIntelligenceApiUtil(Logger logger) {
        this.logger = logger;
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
