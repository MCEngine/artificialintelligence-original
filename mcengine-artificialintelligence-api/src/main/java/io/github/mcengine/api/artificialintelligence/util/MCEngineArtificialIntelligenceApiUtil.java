package io.github.mcengine.api.artificialintelligence.util;

import java.util.logging.Logger;

/**
 * Utility class for logging messages for the MCEngine Artificial Intelligence API.
 * Provides standardized methods to log informational, warning, and error messages.
 */
public class MCEngineArtificialIntelligenceApiUtil {

    /** Java logger instance for outputting log messages. */
    private final Logger logger;

    /**
     * Constructs the utility with the provided logger.
     *
     * @param logger The {@link Logger} instance to be used for logging.
     */
    public MCEngineArtificialIntelligenceApiUtil(Logger logger) {
        this.logger = logger;
    }

    /**
     * Logs an informational message.
     *
     * @param message The message to log as info.
     */
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a warning message.
     *
     * @param message The message to log as a warning.
     */
    public void warn(String message) {
        logger.warning(message);
    }

    /**
     * Logs an error message.
     *
     * @param message The message to log as a severe error.
     */
    public void error(String message) {
        logger.severe(message);
    }
}
