package io.github.lianjordaan.byteBuildersLobbyPlugin.events;

import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.ItemManager;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.SpawnUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SpawnUtils.spawnPlayer(event.getPlayer());
    }

}
