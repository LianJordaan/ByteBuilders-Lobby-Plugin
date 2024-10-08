package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import io.github.lianjordaan.byteBuildersLobbyPlugin.PluginManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
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
}
