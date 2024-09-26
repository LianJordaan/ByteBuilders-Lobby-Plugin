package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {

    private static File configFile;
    private static FileConfiguration config;
    private final JavaPlugin plugin;

    public ConfigUtils(JavaPlugin plugin) {
        this.plugin = plugin;
        createConfig();
    }

    // Creates or loads the config.yml file
    public void createConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            // If the file doesn't exist, create the plugin directory and config file
            plugin.getDataFolder().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        // Load the configuration from the file
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    // Saves the current state of the config file to disk
    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not save config.yml!");
            e.printStackTrace();
        }
    }

    // Retrieves the spawn point for a specific world
    public Location getSpawnPoint(String worldName) {
        if (config.contains("worlds." + worldName + ".spawn")) {
            Location location = config.getLocation("worlds." + worldName + ".spawn");

            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                return location;
            }
        }
        return null; // Return null if no spawn point exists
    }

    // Saves a spawn point for a specific world
    public static void setSpawnPoint(Location location) {
        String worldName = location.getWorld().getName();

        config.set("worlds." + worldName + ".spawn", location);

        saveConfig(); // Save changes to the file
    }
}
