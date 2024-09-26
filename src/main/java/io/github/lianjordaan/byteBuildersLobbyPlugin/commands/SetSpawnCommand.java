package io.github.lianjordaan.byteBuildersLobbyPlugin.commands;

import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.ConfigUtils;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.SpawnUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("setspawn")) {
            ConfigUtils.setSpawnPoint(player.getLocation());
            return true;
        }

        return false;
    }
}