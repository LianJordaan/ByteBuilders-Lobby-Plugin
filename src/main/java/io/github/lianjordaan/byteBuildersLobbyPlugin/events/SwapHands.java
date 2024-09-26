package io.github.lianjordaan.byteBuildersLobbyPlugin.events;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataType;

public class SwapHands implements Listener {

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            NamespacedKey key = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "no-move");

            if (event.getOffHandItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                event.setCancelled(true);
            } else if (event.getMainHandItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                event.setCancelled(true);
            }
        }
    }
}
