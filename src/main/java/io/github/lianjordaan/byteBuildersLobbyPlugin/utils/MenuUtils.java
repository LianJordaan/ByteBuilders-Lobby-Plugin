package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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

                int id = plot.get("_id").getAsInt();
                String name = plot.get("name").getAsString();
                String description = plot.get("description").getAsString();
                String sizeName = plot.get("sizeName").getAsString();
                String material = plot.get("material").getAsString();
                String skullSkin = plot.get("skullSkin").getAsString();
                int modelData = plot.get("modelData").getAsInt();
                boolean whitelisted = plot.get("whitelisted").getAsBoolean();
                ItemStack item = new ItemStack(Material.valueOf(material.toUpperCase()));
                ItemMeta itemMeta = item.getItemMeta();
                if (material.equalsIgnoreCase("PLAYER_HEAD")) {
                    URL skullSkinUrl;
                    try {
                        skullSkinUrl = new URI(skullSkin).toURL();

                        SkullMeta skullMeta = (SkullMeta) itemMeta;
                        PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
                        PlayerTextures playerTextures = playerProfile.getTextures();
                        playerTextures.setSkin(skullSkinUrl);
                        playerProfile.setTextures(playerTextures);
                        skullMeta.setPlayerProfile(playerProfile);
                    } catch (Exception e) {
                        item = new ItemStack(Material.MAP);
                    }
                }

                itemMeta.displayName(MiniMessage.miniMessage().deserialize("<!i><white>" + name));
                List<Component> lore = new ArrayList<>();
                if (itemMeta.hasLore() && itemMeta.lore() != null) {
                    lore = itemMeta.lore();
                }

                assert lore != null;
                lore.add(MiniMessage.miniMessage().deserialize("<!i><#808080>" + sizeName + " Plot"));
                lore.add(MiniMessage.miniMessage().deserialize("<!i><white>" + description));

                if (whitelisted) {
                    lore.add(MiniMessage.miniMessage().deserialize("<!i><red>Whitelisted"));
                }

                lore.add(MiniMessage.miniMessage().deserialize(""));
                lore.add(MiniMessage.miniMessage().deserialize("<!i><dark_gray>ID: " + id));

                itemMeta.lore(lore);
                itemMeta.setCustomModelData(modelData);

                item.setItemMeta(itemMeta);

                player.getOpenInventory().setItem(slot, ItemManager.preventDropAndMove(item));

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
            player.getOpenInventory().setItem(4, ItemManager.preventDropAndMove(statsItem));


        }).exceptionally(ex -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>Error: Failed to fetch plots. Please try again later or contact the server administrator."));
            player.closeInventory();
            Bukkit.getServer().getLogger().info("Error: " + ex);
            return null;
        });
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
                String plotSize = entry.getValue().getAsJsonObject().get("size").getAsString();
                ItemStack item = ItemManager.createPlotItem(entry.getKey(), maxPlots, usedPlots, material, plotSize);

                int slot = 11 + 2 * index + 12 * Math.floorDiv(index, 3);

                player.getOpenInventory().setItem(slot, ItemManager.preventDropAndMove(item));
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
