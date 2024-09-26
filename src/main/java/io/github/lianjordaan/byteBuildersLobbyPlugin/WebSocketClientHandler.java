package io.github.lianjordaan.byteBuildersLobbyPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebSocketClientHandler extends WebSocketClient {

    private final PluginManager pluginManager;
    private final Server server;
    private final Logger logger;
    private final URI serverUri;
    private int maxAttempts;
    private int attempt;
    private int delay;

    // Constructor accepts PluginManager and Logger
    public WebSocketClientHandler(URI serverUri, PluginManager pluginManager) {
        super(serverUri);
        this.serverUri = serverUri;
        this.pluginManager = pluginManager;
        this.server = pluginManager.getServer();
        this.logger = pluginManager.getLogger();
        this.maxAttempts = 0;
        this.attempt = 0;
        this.delay = 0;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        attempt = 0;
        logger.info("WebSocket connection opened");
        send("{\"type\": \"message\", \"message\": \"Hello from Minecraft Lobby plugin!\"}");
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message: {}", message);

        try {
            // Parse the incoming message
            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
            String type = jsonMessage.get("type").getAsString();

            if ("shutdown".equals(type)) {
                logger.info("Websocket server is shutting down");
                maxAttempts = jsonMessage.get("attempts").getAsInt();
                delay = jsonMessage.get("delay").getAsInt();
                scheduleReconnect(delay, 1);
            }

            if ("forwarded-message".equals(type)) {
                String from = jsonMessage.get("from").getAsString();
                String forwardedMessage = jsonMessage.get("message").getAsString();
                JsonObject jsonData = JsonParser.parseString("{}").getAsJsonObject();
                try {
                    jsonData = JsonParser.parseString(jsonMessage.get("json").getAsString()).getAsJsonObject();
                } catch (Exception ignored) {
                }

                if ("msgPlayer".equals(forwardedMessage)) {
                    JsonObject finalJsonData = jsonData;
                    server.getPlayer(finalJsonData.get("player").getAsString()).sendMessage(MiniMessage.miniMessage().deserialize(finalJsonData.get("message").getAsString()));

                }
            }
        } catch (JsonParseException e) {
            logger.error("Failed to parse message", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: {}", reason);
        if (reason.contains("refused")) {
            attempt++;
            if (attempt > maxAttempts) {
                logger.error("Max reconnect attempts reached, shutting down");

                scheduler.schedule(() -> {
                    Bukkit.getServer().shutdown();
                }, delay, TimeUnit.SECONDS);
            } else {
                logger.error("Failed to connect to backend database, attempting again in {} seconds", delay);
                scheduleReconnect(delay, attempt);
            }
        }
    }

    @Override
    public void onError(Exception ex) {
        logger.error("WebSocket error", ex);
    }

    public void scheduleReconnect(int seconds, int attempt) {
        scheduler.schedule(() -> {
            PluginManager.getWebSocketClient().reconnect();
        }, seconds, TimeUnit.SECONDS);
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


}
