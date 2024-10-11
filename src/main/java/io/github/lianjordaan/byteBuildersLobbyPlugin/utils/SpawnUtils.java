package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public class SpawnUtils {
    private static JavaPlugin plugin = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class);

    public static void spawnPlayer(Player player) {
        ConfigUtils configUtils = new ConfigUtils(plugin);

        Location spawnPoint = configUtils.getSpawnPoint();
        if (spawnPoint != null) {
            player.teleport(spawnPoint);
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>There is no spawn point set for this world!"));
        }
        player.getInventory().clear();
        player.getInventory().setItem(0, ItemManager.createPlotMenuItem());
    }

    public void setSpawn(Player player) {
        ConfigUtils configUtils = new ConfigUtils(plugin);

        configUtils.setSpawnPoint(player.getLocation());
        player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><green>Spawn point set!"));
    }
}
