package io.github.lianjordaan.byteBuildersLobbyPlugin.commands;

import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.SpawnUtils;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.WebSocketUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("spawn")) {
            SpawnUtils.spawnPlayer(player);
            return true;
        }

        return false;
    }
}