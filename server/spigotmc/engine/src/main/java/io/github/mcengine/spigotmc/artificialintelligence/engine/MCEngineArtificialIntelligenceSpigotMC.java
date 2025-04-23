package io.github.mcengine.spigotmc.artificialintelligence.engine;

import io.github.mcengine.api.artificialintelligence.ConversationManager;
import io.github.mcengine.api.artificialintelligence.FunctionCallingLoader;
import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.ThreadPoolManager;
import io.github.mcengine.common.artificialintelligence.command.MCEngineArtificialIntelligenceCommonCommand;
import io.github.mcengine.common.artificialintelligence.listener.MCEngineArtificialIntelligenceCommonListenerConversation;
import io.github.mcengine.spigotmc.artificialintelligence.engine.Metrics;
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

        saveDefaultConfig();

        if (!getConfig().getBoolean("enable", true)) {
            getLogger().info("Plugin disabled via config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        reloadAiComponents();
    }

    /**
     * Called when the plugin is disabled. Used for cleanup or shutdown routines.
     */
    @Override
    public void onDisable() {
        getLogger().info("MCEngine Artificial Intelligence Disabled.");
        HandlerList.unregisterAll(this);

        ConversationManager.terminateAll();
    }

    public void reloadAiComponents() {
        try {
            HandlerList.unregisterAll(this);
            reloadConfig();
    
            artificialintelligenceApi = new MCEngineArtificialIntelligenceApi(this);
            threadPoolManager = new ThreadPoolManager(this);
            functionCallingLoader = artificialintelligenceApi.getFunctionCallingLoader();
    
            PluginManager pluginManager = getServer().getPluginManager();
    
            if (getConfig().getBoolean("conversation.keep", false)) {
                pluginManager.registerEvents(
                    new MCEngineArtificialIntelligenceCommonListenerConversation(
                        this, artificialintelligenceApi, threadPoolManager, functionCallingLoader
                    ),
                    this
                );
            }
    
            getCommand("ai").setExecutor(
                new MCEngineArtificialIntelligenceCommonCommand(this, this::reloadAiComponents)
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
