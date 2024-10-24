package io.github.lianjordaan.byteBuildersLobbyPlugin.events;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.MenuUtils;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.PlotUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        NamespacedKey pageKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "page");
        NamespacedKey openMenuKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "open-menu");
        NamespacedKey claimPlotKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "create-plot");

        NamespacedKey ownerUuidKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "plot-owner");
        NamespacedKey plotIdKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "plot-id");

        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

            if (dataContainer.has(pageKey, PersistentDataType.INTEGER)) {
                // Get the value stored with the pageKey
                int pageValue = dataContainer.get(pageKey, PersistentDataType.INTEGER);

                if (dataContainer.has(openMenuKey, PersistentDataType.STRING)) {
                    // Get the value stored with the openMenuKey
                    String openMenuValue = dataContainer.get(openMenuKey, PersistentDataType.STRING);

                    if ("my-plots".equals(openMenuValue)) {
                        MenuUtils.openPlotsMenu(player, pageValue);
                    }
                }
            } else if (dataContainer.has(openMenuKey, PersistentDataType.STRING)) {

                String openMenuValue = dataContainer.get(openMenuKey, PersistentDataType.STRING);

                if ("claim-new-plot".equals(openMenuValue)) {
                    MenuUtils.openClaimNewPlotMenu(player);
                } else if ("manage-join-plot".equals(openMenuValue)) {
                    String ownerUuidValue = dataContainer.get(ownerUuidKey, PersistentDataType.STRING);
                    Integer plotIdValue = dataContainer.get(plotIdKey, PersistentDataType.INTEGER);

                    if ((event.getClick() == ClickType.RIGHT)) {
                        if (player.getUniqueId().toString().equals(ownerUuidValue)) {
                            MenuUtils.openPlotEditMenu(player, plotIdValue);
                        } else {
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<#FF5555>Error: <#AAAAAA>You do not own this plot!"));
                        }
                    } else {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Adding this later :P"));
                    }
                } else if ("join-plot".equals(openMenuValue)) {
                    String ownerUuidValue = dataContainer.get(ownerUuidKey, PersistentDataType.STRING);
                    Integer plotIdValue = dataContainer.get(plotIdKey, PersistentDataType.INTEGER);

                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Adding this later :P"));
                }
            } else {
                if (dataContainer.has(claimPlotKey, PersistentDataType.STRING)) {
                    String claimPlotValue = dataContainer.get(claimPlotKey, PersistentDataType.STRING);
                    PlotUtils.createPlot(player, claimPlotValue);
                }
            }
        }

        // Get the player's game mode
        GameMode playerGameMode = event.getWhoClicked().getGameMode();
        NamespacedKey key = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "no-move");
        NamespacedKey noClickKey = new NamespacedKey(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), "no-click");

        // Always cancel if the item has the no-click tag
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(noClickKey, PersistentDataType.BYTE)) {
                event.setCancelled(true);
                return;
            }
        }

        // Check for the no-move restriction only if not in Creative mode
        if (playerGameMode != GameMode.CREATIVE) {
            // Check if the current item has the no-move tag
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
                if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Handle number key swaps
            if (event.getClick() == ClickType.NUMBER_KEY) {
                ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                if (hotbarItem != null && hotbarItem.hasItemMeta() &&
                        hotbarItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Handle off-hand swap (using the F key)
            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                ItemStack offHandItem = event.getWhoClicked().getInventory().getItemInOffHand();
                if (offHandItem.hasItemMeta() && offHandItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Handle placing in crafting grid
            if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.CRAFTING) {
                if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                        event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
