package io.github.lianjordaan.byteBuildersLobbyPlugin.events;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.ItemManager;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.MenuUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import javax.xml.stream.events.Namespace;
import java.awt.*;

public class RightClickItem implements Listener {
    @EventHandler
    public void onRightClickItem(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Plugin plugin = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class);
        NamespacedKey key = new NamespacedKey(plugin, "open-menu");
        String menyToOpen = event.getItem().getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, "");
        if (menyToOpen.equals("my-plots")) {
            event.setCancelled(true);
            MenuUtils.openPlotsMenu(event.getPlayer(), 1);
        }
    }
}
