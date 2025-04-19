package io.github.mcengine.spigotmc.artificialintelligence.engine;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.ThreadPoolManager;
import io.github.mcengine.common.artificialintelligence.command.MCEngineArtificialIntelligenceCommonCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the MCEngineArtificialIntelligence plugin on SpigotMC.
 * This class initializes and manages the AI engine, including configuration and command registration.
 */
public class MCEngineArtificialIntelligenceSpigotMC extends JavaPlugin {

    /** Singleton instance of the plugin. */
    private static MCEngineArtificialIntelligenceSpigotMC instance;

    /** API for accessing and managing AI functionality. */
    private MCEngineArtificialIntelligenceApi artificialintelligenceApi;

    /**
     * Called when the plugin is enabled. Initializes configuration,
     * sets up the AI engine, and registers the /ai command.
     */
    @Override
    public void onEnable() {
        instance = this;

        // Save default config if not already present
        saveDefaultConfig();

        try {
            artificialintelligenceApi = new MCEngineArtificialIntelligenceApi(this);
            ThreadPoolManager threadPoolManager = new ThreadPoolManager(this);
            getLogger().info("AI Engine initialized successfully.");

            getCommand("ai").setExecutor(
                new MCEngineArtificialIntelligenceCommonCommand(this, artificialintelligenceApi, threadPoolManager)
            );
        } catch (Exception e) {
            getLogger().severe("Failed to initialize AI Engine: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the plugin is disabled. Used for cleanup or shutdown routines.
     */
    @Override
    public void onDisable() {
        getLogger().info("AI Engine disabled.");
    }

    /**
     * Returns the singleton instance of the plugin.
     *
     * @return The current instance of {@link MCEngineArtificialIntelligenceSpigotMC}.
     */
    public static MCEngineArtificialIntelligenceSpigotMC getInstance() {
        return instance;
    }

    /**
     * Gets the initialized AI API instance.
     *
     * @return The {@link MCEngineArtificialIntelligenceApi} used by this plugin.
     */
    public MCEngineArtificialIntelligenceApi getArtificialIntelligenceApi() {
        return artificialintelligenceApi;
    }
}
