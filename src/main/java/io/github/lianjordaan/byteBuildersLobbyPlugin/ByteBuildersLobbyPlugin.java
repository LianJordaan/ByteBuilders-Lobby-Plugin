package io.github.lianjordaan.byteBuildersLobbyPlugin;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.lianjordaan.byteBuildersLobbyPlugin.utils.WebSocketUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public final class ByteBuildersLobbyPlugin extends JavaPlugin implements Listener {

    private PluginManager pluginManager = new PluginManager(getServer(), getSLF4JLogger());
    private Server server = pluginManager.getServer();
    private Logger logger = pluginManager.getLogger();

    @Subscribe
    public void onEnable() {
        //register the onplayerChat event
        getServer().getPluginManager().registerEvents(this, this);
        logger.info("ByteBuilders Proxy Plugin initialized!");

        try {
            Properties env = EnvLoader.loadEnv();
            String username = env.getProperty("USERNAME");
            Objects.requireNonNull(username, "USERNAME not set in .env file");
            WebSocketClient webSocketClient = new WebSocketClientHandler(new URI("ws://localhost:3000?username=" + username + "&id=lobby"), pluginManager);
            pluginManager.setWebSocketClient(webSocketClient);
            webSocketClient.connect();
        } catch (Exception e) {
            logger.error("Failed to initialize WebSocket client", e);
        }
    }

}
