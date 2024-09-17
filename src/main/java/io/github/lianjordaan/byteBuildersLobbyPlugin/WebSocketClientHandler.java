package io.github.lianjordaan.byteBuildersLobbyPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

    // Constructor accepts PluginManager and Logger
    public WebSocketClientHandler(URI serverUri, PluginManager pluginManager) {
        super(serverUri);
        this.serverUri = serverUri;
        this.pluginManager = pluginManager;
        this.server = pluginManager.getServer();
        this.logger = pluginManager.getLogger();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
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



    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int RECONNECT_DELAY = 30; // Delay in seconds before trying to reconnect
    private boolean reconnectInProgress = false; // Tracks if a reconnect attempt is already in progress
    private static final int RECONNECT_TIMEOUT = 2; // 2-second reconnect timeout
    private static final int MAX_RECONNECT_ATTEMPTS = 10; // Optional: max reconnect attempts

    private void scheduleReconnect() {
        if (reconnectInProgress || getReconnectAttempts() >= MAX_RECONNECT_ATTEMPTS) {
            logger.info("Reconnection already in progress or maximum reconnect attempts reached.");
            return;
        }

        reconnectInProgress = true;

        // Calculate exponential backoff delay
        long delay = Math.min(RECONNECT_DELAY * (long) Math.pow(2, getReconnectAttempts()), 300);
        logger.info("Attempting to reconnect in " + delay + " seconds...");

        reconnectScheduler.schedule(() -> {
            try {
                reconnectBlocking();
                resetReconnectAttempts(); // Reset attempts if reconnection is successful
                reconnectInProgress = false; // Reconnection succeeded, reset the flag
            } catch (InterruptedException e) {
                logger.error("Reconnection attempt interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                reconnectInProgress = false; // Reset flag on interruption
            } catch (Exception e) {
                logger.error("Reconnection failed: " + e.getMessage());
                incrementReconnectAttempts(); // Increment reconnect attempts on failure
                reconnectInProgress = false; // Reset flag so that we can retry
                scheduleReconnect(); // Schedule next reconnect attempt if it fails
            }
        }, delay, TimeUnit.SECONDS);

        // Timeout after 2 seconds (localhost)
        reconnectScheduler.schedule(() -> {
            if (reconnectInProgress) {
                logger.warn("Reconnection attempt timed out after " + RECONNECT_TIMEOUT + " seconds.");
                reconnectInProgress = false; // Reset the flag if timed out
                incrementReconnectAttempts();
                scheduleReconnect(); // Try to reconnect again
            }
        }, RECONNECT_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: " + reason);
        incrementReconnectAttempts(); // Increment the reconnect attempts
        scheduleReconnect(); // Schedule the reconnection
    }

    @Override
    public void onError(Exception ex) {
        logger.error("WebSocket error", ex);
        incrementReconnectAttempts(); // Increment the reconnect attempts
        scheduleReconnect(); // Schedule the reconnection
    }

    private int reconnectAttempts = 0;

    // Increment reconnect attempts count
    private void incrementReconnectAttempts() {
        reconnectAttempts++;
    }

    // Reset reconnect attempts count
    private void resetReconnectAttempts() {
        reconnectAttempts = 0;
    }

    // Get the current reconnect attempts count
    private int getReconnectAttempts() {
        return reconnectAttempts;
    }
}
