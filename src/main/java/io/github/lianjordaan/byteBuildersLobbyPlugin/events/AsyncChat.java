package io.github.lianjordaan.byteBuildersLobbyPlugin.events;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.LuckpermsUtils;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.SoundUtils;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.WebSocketUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class AsyncChat implements Listener {

    private final Logger logger = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class).getSLF4JLogger();
    protected final Server server = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class).getServer();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith("start")) {
            String[] parts = message.split(" ");
            if (parts.length == 2) {
                String id = parts[1];
                handleStartCommand(id, event.getPlayer());
                event.getPlayer().sendMessage(Component.text("Getting plot ready... Please wait."));
//                event.setResult(PlayerChatEvent.ChatResult.denied()); // Prevent the command from being shown in chat
            } else {
                event.getPlayer().sendMessage(Component.text("Invalid command usage. Use start <port>"));
            }
        }
        else if (message.startsWith("delete")) {
            String[] parts = message.split(" ");
            if (parts.length == 2) {
                String id = parts[1];
                handleDeleteCommand(event.getPlayer(), id);
                event.getPlayer().sendMessage(Component.text("Deleting plot... Please wait."));
            } else {
                event.getPlayer().sendMessage(Component.text("Invalid command usage. Use create"));
            }
        }
    }

    private void handleStartCommand(String id, Player player) {
        // Start the server by making a web request
        CompletableFuture.runAsync(() -> {
            try {
                // Make HTTP request to start the server
                URL url = new URL("http://localhost:3000/start-server");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                String jsonInputString = String.format("{\"id\": %s}", Integer.valueOf(id));
                connection.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                int responseCode = connection.getResponseCode();

                // Read response based on the status code
                try (Scanner scanner = new Scanner(responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    logger.info("Server start response: {}", response);
                    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

                    if (responseCode == 200) {
                        server.sendMessage(Component.text("Empty server found, joining..."));
                        WebSocketUtils.registerServer(jsonResponse.get("port").getAsInt());
                        WebSocketUtils.sendPlayerToServer(player.getName(), "dyn-" + jsonResponse.get("port").getAsInt());
                    } else if (responseCode == 409) {
                        server.sendMessage(Component.text("Server already registered. Joining..."));

                        WebSocketUtils.registerServer(jsonResponse.get("port").getAsInt());
                        WebSocketUtils.sendPlayerToServer(player.getName(), "dyn-" + jsonResponse.get("port").getAsInt());
                    } else if (responseCode == 404) {
                        server.sendMessage(Component.text("No empty server was found, starting a new one. Please wait..."));
                    } else if (responseCode == 202) {
                        server.getPlayer(player.getUniqueId()).sendMessage(Component.text("A server is starting, please wait for it to become available, and then try again."));
                    } else if (responseCode == 410) { // New response code for plot not found
                        server.getPlayer(player.getUniqueId()).sendMessage(Component.text("The plot ID does not exist."));
                    } else {
                        logger.error("Failed to start the server with response code: {}", responseCode);
                    }
                }
            } catch (IOException e) {
                logger.error("Error starting the server", e);
            }
        });
    }

    private void handleDeleteCommand(Player player, String id) {
        // Start the server by making a web request
        CompletableFuture.runAsync(() -> {
            try {
                // Make HTTP request to start the server
                URL url = new URL("http://localhost:3000/delete-plot");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                boolean bypass = "LianJordaan".equals(player.getName());

                String jsonInputString = String.format("{\"plotId\": %s, \"uuid\": \"%s\", \"bypass\": %s}", id, player.getUniqueId(), bypass);
                connection.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                int responseCode = connection.getResponseCode();

                // Read response based on the status code
                try (Scanner scanner = new Scanner(responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream())) {
                    String response = scanner.useDelimiter("\\A").next();
                    logger.info("Plot delete response: {}", response);
                    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

                    if (responseCode == 200) {
                        player.sendMessage(Component.text("Plot with ID " + jsonResponse.get("id").getAsInt() + " has been deleted."));
                    } else if (responseCode == 403) {
                        player.sendMessage(Component.text("Error: You do not have permission to delete this plot."));
                    } else if (responseCode == 404) {
                        player.sendMessage(Component.text("Error: Plot with ID " + id + " does not exist."));
                    } else if (responseCode == 409) {
                        player.sendMessage(Component.text("Error: Plot with ID " + id + " is running. Please stop the server before deleting."));
                    } else {
                        player.sendMessage(Component.text("Error: Failed to delete plot. Please try again later."));
                        logger.error("Failed to delete the plot with response code: {}", responseCode);
                    }
                }
            } catch (IOException e) {
                logger.error("Error deleting the plot", e);
            }
        });
    }
}
