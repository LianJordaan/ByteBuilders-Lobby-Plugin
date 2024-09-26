package io.github.lianjordaan.byteBuildersLobbyPlugin.events;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

public class DropItem implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        NamespacedKey noClickKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "no-click");

        if (event.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(noClickKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            return;
        }


        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            NamespacedKey key = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "no-drop");

            if (event.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                event.setCancelled(true);
            }
        }
    }
}
