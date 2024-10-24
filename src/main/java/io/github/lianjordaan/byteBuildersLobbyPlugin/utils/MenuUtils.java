package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.*;
import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.profile.PlayerTextures;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MenuUtils {
    public static Plugin plugin = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class);

    public static void openPlotsMenu(Player player, Integer page) {
        Inventory inventory = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize("<!i><green>My Plots"));

        //fill the inventory with border items
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, ItemManager.preventDropAndMove(ItemManager.createPlotMenuBorderItem()));
        }
        //fill slots 9 to (size - 9) with air
        for (int i = 9; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
        ItemStack statsItem = new ItemStack(Material.REDSTONE_LAMP);
        ItemMeta statsItemMeta = statsItem.getItemMeta();
        statsItemMeta.displayName(MiniMessage.miniMessage().deserialize("<!i><gold>Information"));
        statsItem.setItemMeta(statsItemMeta);
        inventory.setItem(4, ItemManager.preventDropAndMove(statsItem));

        inventory.setItem(inventory.getSize() - 5, ItemManager.createClaimNewPlotItem());

        if (page != 1) {
            inventory.setItem(inventory.getSize() - 9, ItemManager.createPreviousPageItem(page, "my-plots"));
        }

        player.openInventory(inventory);

        String body = "{\"uuid\":\"" + player.getUniqueId() + "\"}";
        CompletableFuture<String> future = WebRequestUtils.makeWebRequest("http://192.168.0.125:3000/player/get-plots", body, "json");

        // Use the output in the main thread once it's available
        future.thenAccept(result -> {
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            // Get the "plots" array
            JsonArray plots = jsonObject.getAsJsonArray("plots");
            // Get the "plotCounts" array
            JsonObject plotCounts = jsonObject.getAsJsonObject("plotCounts");

            // Set your clamping bounds (e.g., from index 1 to index 5)
            int startIndex = (page - 1) * 36;
            int endIndex = startIndex + 36;

            // Make sure we don't go out of bounds
            int clampEnd = Math.min(endIndex, plots.size());

            if (plots.size() > endIndex) {
                inventory.setItem(inventory.getSize() - 1, ItemManager.createNextPageItem(page, "my-plots"));
            }

            int slot = 9;

            // Loop through the clamped range
            for (int i = startIndex; i < clampEnd; i++) {
                JsonObject plot = plots.get(i).getAsJsonObject();

                ItemStack item = ItemManager.createPlotItem(plot);

                inventory.setItem(slot, ItemManager.preventDropAndMove(item));

                slot++;
            }

            ArrayList<Component> statsItemLore = new ArrayList<>();
            statsItemLore.add(MiniMessage.miniMessage().deserialize("<!i><gray>Plots owned: <gold>" + plots.size()));

            for (Map.Entry<String, JsonElement> entry : plotCounts.entrySet()) {
                String key = entry.getKey();
                int count = entry.getValue().getAsInt();

                // Add to stats item lore
                statsItemLore.add(MiniMessage.miniMessage().deserialize("<!i><gray> - " + key + " Plots: <aqua>" + count));
            }
            statsItem.lore(statsItemLore);
            inventory.setItem(4, ItemManager.preventDropAndMove(statsItem));


        }).exceptionally(ex -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>Error: Failed to fetch plots. Please try again later or contact the server administrator."));
            player.closeInventory();
            Bukkit.getServer().getLogger().info("Error: " + ex);
            return null;
        });
    }

    public static void openPlotEditMenu(Player player, Integer id) {
        Inventory inventory = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize("<!i><green>Settings for Plot ID: " + id));

        //fill the inventory with border items
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, ItemManager.preventDropAndMove(ItemManager.createPlotMenuBorderItem()));
        }
        //fill slots 9 to (size - 9) with air
        for (int i = 9; i < inventory.getSize() - 9; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
        PlotUtils.getPlotObject(id).thenAccept(plot -> {
            inventory.setItem(4, ItemManager.preventDropAndMove(ItemManager.createPlotItem(plot)));
        });

        inventory.setItem(19, ItemManager.preventDropAndMove(ItemManager.createDevelopersListItem(id)));
        inventory.setItem(20, ItemManager.preventDropAndMove(ItemManager.createBuildersListItem(id)));
        inventory.setItem(22, ItemManager.preventDropAndMove(ItemManager.createJoinPlotItem(id)));
        inventory.setItem(24, ItemManager.preventDropAndMove(ItemManager.createCoOwnerPlayersListItem(id)));
        inventory.setItem(25, ItemManager.preventDropAndMove(ItemManager.createEditPlotNameItem(id)));

        inventory.setItem(28, ItemManager.preventDropAndMove(ItemManager.createBannedPlayersListItem(id)));
        inventory.setItem(29, ItemManager.preventDropAndMove(ItemManager.createWhitelistedPlayersListItem(id)));

        ItemManager.createTogglePlotWhitelistItem(id).thenAccept(item -> {
            inventory.setItem(33, ItemManager.preventDropAndMove(item));
        });
        inventory.setItem(34, ItemManager.preventDropAndMove(ItemManager.createEditPlotDescriptionItem(id)));

        player.openInventory(inventory);

    }

    public static void openJoinPlotMenu(Player player, int id) {
        Inventory inventory = Bukkit.createInventory(null, 45, MiniMessage.miniMessage().deserialize("<!i><green>Join Plot"));
    }
    public static void openClaimNewPlotMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, MiniMessage.miniMessage().deserialize("<!i><green>Claim New Plot"));

        //fill the inventory with border items
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, ItemManager.preventDropAndMove(ItemManager.createPlotMenuBorderItem()));
        }
        //loop a certain size (7*3) times.
        for (int i = 0; i < 7*3; i++) {
            int slot = 10 + i + (2 * Math.floorDiv(i, 7));
            inventory.setItem(slot, new ItemStack(Material.AIR));
        }
        ItemStack backItem = ItemManager.createBackPageItem(1, "my-plots");
        inventory.setItem(inventory.getSize() - 9, ItemManager.preventDropAndMove(backItem));

        player.openInventory(inventory);

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("uuid", player.getUniqueId().toString());
        jsonMap.put("rank", LuckpermsUtils.getHighestRank(player));

        // Convert the Map to a JSON string using Gson
        String body = new Gson().toJson(jsonMap);

        CompletableFuture<String> future = WebRequestUtils.makeWebRequest("http://192.168.0.125:3000/player/get-limits", body, "json");

        // Use the output in the main thread once it's available
        future.thenAccept(result -> {
            int index = 0;
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

            ArrayList<Material> materialList = new ArrayList<>(Arrays.asList(Material.POLISHED_ANDESITE, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.EMERALD_BLOCK, Material.NETHERITE_BLOCK, Material.AMETHYST_BLOCK));

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                Material material = materialList.get(index);
                Integer maxPlots = entry.getValue().getAsJsonObject().get("max").getAsInt();
                Integer usedPlots = entry.getValue().getAsJsonObject().get("used").getAsInt();
                String plotSizeName = entry.getValue().getAsJsonObject().get("sizeName").getAsString();
                String plotSize = entry.getValue().getAsJsonObject().get("size").getAsString();
                ItemStack item = ItemManager.createPlotItem(entry.getKey(), maxPlots, usedPlots, material, plotSizeName, plotSize);

                int slot = 11 + 2 * index + 12 * Math.floorDiv(index, 3);

                inventory.setItem(slot, ItemManager.preventDropAndMove(item));
                index++;
            }
        }).exceptionally(ex -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>Error: Failed to fetch plots. Please try again later or contact the server administrator."));
            player.closeInventory();
            Bukkit.getServer().getLogger().info("Error: " + ex);
            return null;
        });
    }
}
