package io.github.mcengine.spigotmc.artificialintelligence.engine;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtilBotManager;
import io.github.mcengine.api.mcengine.MCEngineApi;
import io.github.mcengine.api.mcengine.Metrics;
import io.github.mcengine.common.artificialintelligence.FunctionCallingLoader;
import io.github.mcengine.common.artificialintelligence.ThreadPoolManager;
import io.github.mcengine.common.artificialintelligence.command.MCEngineArtificialIntelligenceCommonCommand;
import io.github.mcengine.common.artificialintelligence.listener.MCEngineArtificialIntelligenceCommonListenerConversation;
import io.github.mcengine.common.artificialintelligence.tabcompleter.MCEngineArtificialIntelligenceCommonTabCompleter;
import io.github.mcengine.spigotmc.artificialintelligence.engine.util.MCEngineArtificialIntelligenceSpigotMCUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
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
    private ThreadPoolManager threadPoolManager;
    private FunctionCallingLoader functionCallingLoader;

    @Override
    public void onEnable() {
        getLogger().info("MCEngine Artificial Intelligence Enabled.");
        instance = this;

        // Initialize bStats metrics
        new Metrics(this, 25556);

        // Save default config if missing
        saveDefaultConfig();

        // Check config to optionally disable plugin
        if (!getConfig().getBoolean("enable", false)) {
            getLogger().info("Plugin disabled via config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MCEngineArtificialIntelligenceSpigotMCUtil.createSimpleFile(this);

        // Initialize and register AI systems
        reloadAiComponents();

        // Load built-in models
        String[] platforms = { "deepseek", "openai", "openrouter" };
        for (String platform : platforms) {
            String modelsKey = "ai." + platform + ".models";
            if (getConfig().isConfigurationSection(modelsKey)) {
                ConfigurationSection section = getConfig().getConfigurationSection(modelsKey);
                for (String key : section.getKeys(false)) {
                    String modelName = section.getString(key);
                    if (modelName != null && !modelName.equalsIgnoreCase("null")) {
                        artificialintelligenceApi.registerModel(platform, modelName);
                    }
                }
            }
        }

        // Load custom server models
        if (getConfig().isConfigurationSection("ai.custom")) {
            for (String server : getConfig().getConfigurationSection("ai.custom").getKeys(false)) {
                String modelsKey = "ai.custom." + server + ".models";
                if (getConfig().isConfigurationSection(modelsKey)) {
                    ConfigurationSection section = getConfig().getConfigurationSection(modelsKey);
                    for (String key : section.getKeys(false)) {
                        String modelName = section.getString(key);
                        if (modelName != null && !modelName.equalsIgnoreCase("null")) {
                            artificialintelligenceApi.registerModel("customurl", server + ":" + modelName);
                        }
                    }
                }
            }
        }

        // Check for updates from GitHub
        MCEngineApi.checkUpdate(this, getLogger(), "github", "MCEngine", "artificialintelligence-original", getConfig().getString("github.token", "null"));
    }

    /**
     * Called when the plugin is disabled. Used for cleanup or shutdown routines.
     */
    @Override
    public void onDisable() {
        getLogger().info("MCEngine Artificial Intelligence Disabled.");
        HandlerList.unregisterAll(this);
        MCEngineArtificialIntelligenceApiUtilBotManager.terminateAll();
    }

    /**
     * Reloads AI components including the API, thread pool, commands, and listeners.
     */
    public void reloadAiComponents() {
        try {
            // Terminate all player sessions before reload
            MCEngineArtificialIntelligenceApiUtilBotManager.terminateAll();

            // Unregister all existing listeners
            HandlerList.unregisterAll(this);
            reloadConfig();

            // Init API and systems
            artificialintelligenceApi = new MCEngineArtificialIntelligenceApi(this);
            threadPoolManager = new ThreadPoolManager(this);
            functionCallingLoader = new FunctionCallingLoader(this, true);

            PluginManager pluginManager = getServer().getPluginManager();

            // Register chat listener for AI conversations
            pluginManager.registerEvents(
                new MCEngineArtificialIntelligenceCommonListenerConversation(this),
                this
            );

            // Register /ai command and tab completer
            getCommand("ai").setExecutor(
                new MCEngineArtificialIntelligenceCommonCommand(this, this::reloadAiComponents)
            );
            getCommand("ai").setTabCompleter(
                new MCEngineArtificialIntelligenceCommonTabCompleter()
            );

            getLogger().info("AI components reloaded successfully.");
        } catch (Exception e) {
            getLogger().severe("Failed to reload AI components: " + e.getMessage());
            e.printStackTrace();
        }
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
