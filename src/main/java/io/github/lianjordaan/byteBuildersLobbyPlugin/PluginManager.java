package io.github.lianjordaan.byteBuildersLobbyPlugin;

import org.bukkit.Server;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;

public class PluginManager {

    private final Server server;
    private final Logger logger;
    private static WebSocketClient webSocketClient;

    public PluginManager(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public Server getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public static WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }
}
