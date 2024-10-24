package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.*;
import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import io.github.lianjordaan.byteBuildersLobbyPlugin.PluginManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlotUtils {
    public static void createPlot(Player player, String size) {
        // Start the server by making a web request
        CompletableFuture.runAsync(() -> {
            Logger logger = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class).getSLF4JLogger();

            try {
                // Make HTTP request to start the server
                URL url = new URI("http://192.168.0.125:3000/create-plot").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create a Map to hold the JSON data
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("name", "<white>" + player.getName() + "'s Plot");
                jsonMap.put("description", "<!i><white>No description.");
                jsonMap.put("size", size);
                jsonMap.put("ownerUuid", player.getUniqueId().toString());
                jsonMap.put("rank", LuckpermsUtils.getHighestRank(player));

                // Convert the Map to a JSON string using Gson
                Gson gson = new Gson();
                String jsonInputString = gson.toJson(jsonMap);

                connection.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                int responseCode = connection.getResponseCode();


                // Read response based on the status code
                try (Scanner scanner = new Scanner(responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();

                    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

                    if (responseCode == 201) {
                        player.sendMessage(Component.text("Your plot has been created. ID: " + jsonResponse.get("id").getAsString()));
                    } else {
                        // Use the message field from the response
                        String errorMessage = jsonResponse.get("message").getAsString();
                        if (jsonResponse.has("shortMessage")) {
                            if (jsonResponse.get("shortMessage").getAsString().equals("limit_reached")) {
                                player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>Error: <gray>You already have the maximum amount of "
                                        + ColorUtils.getPlotColor(jsonResponse.get("plotType").getAsString())
                                        + jsonResponse.get("plotType").getAsString() + " Plots<reset><gray> claimed! <newline>"
                                        + "<green>Purchase more plots from our store using <white>/buy <green>or visit "
                                        + "<gold><u><click:open_url:https://example.com/store>https://example.com/store</click>"));
                            }
                        } else {
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>Error: " + errorMessage));
                        }
                        SoundUtils.playErrorSound(player);

//                        logger.error("Failed to create plot. Response code: {}, Message: {}", responseCode, errorMessage);
                    }
                    Bukkit.getScheduler().runTask(ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class), () -> {
                        player.closeInventory();
                    });
                }
            } catch (Exception e) {
                logger.error("Error creating plot", e);
                player.sendMessage(Component.text("Error: Failed to create plot. Please contact an admin."));
                SoundUtils.playErrorSound(player);
            }
        });
    }


    public static CompletableFuture<Boolean> isWhitelisted(Integer plotID) {
        CompletableFuture<String> future = WebRequestUtils.makeWebRequest("http://192.168.0.125:3000/get-all-plots", null, "");

        // Create a new future to return the result
        return future.thenApply(result -> {
            JsonArray plots = JsonParser.parseString(result).getAsJsonArray();

            // Loop through the array to find the plot with the given ID
            for (int i = 0; i < plots.size(); i++) {
                JsonObject plot = plots.get(i).getAsJsonObject();
                int id = plot.get("_id").getAsInt();

                if (id == plotID) {
                    return plot.get("whitelisted").getAsBoolean();
                }
            }

            // If the plot is not found or not whitelisted, return false
            return false;
        }).exceptionally(ex -> {
            Bukkit.getServer().getLogger().info("Error: " + ex);
            return false;
        });
    }

    public static CompletableFuture<JsonObject> getPlotObject(Integer plotID) {
        CompletableFuture<String> future = WebRequestUtils.makeWebRequest("http://192.168.0.125:3000/get-all-plots", null, "");

        // Create a new future to return the result
        return future.thenApply(result -> {
            JsonArray plots = JsonParser.parseString(result).getAsJsonArray();

            // Loop through the array to find the plot with the given ID
            for (int i = 0; i < plots.size(); i++) {
                JsonObject plot = plots.get(i).getAsJsonObject();
                int id = plot.get("_id").getAsInt();

                if (id == plotID) {
                    return plot; // Return the plot JsonObject
                }
            }

            // If the plot is not found, return null or throw an exception based on your needs
            return null; // You may choose to throw an exception instead
        }).exceptionally(ex -> {
            Bukkit.getServer().getLogger().info("Error: " + ex);
            return null; // Return null on error or handle accordingly
        });
    }
}
