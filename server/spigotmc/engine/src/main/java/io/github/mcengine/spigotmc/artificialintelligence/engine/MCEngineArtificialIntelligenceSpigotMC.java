package io.github.mcengine.spigotmc.artificialintelligence.engine;

import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineArtificialIntelligenceSpigotMC extends JavaPlugin {

    private static MCEngineArtificialIntelligenceSpigotMC instance;

    @Override
    public void onEnable() {
        instance = this;
        // Save default config if not already present
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {}

    public static MCEngineArtificialIntelligenceSpigotMC getInstance() {
        return instance;
    }

}