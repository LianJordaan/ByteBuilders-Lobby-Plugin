package io.github.lianjordaan.byteBuildersLobbyPlugin;

import com.google.common.eventbus.Subscribe;
import io.github.lianjordaan.byteBuildersLobbyPlugin.commands.SetSpawnCommand;
import io.github.lianjordaan.byteBuildersLobbyPlugin.commands.SpawnCommand;
import io.github.lianjordaan.byteBuildersLobbyPlugin.events.*;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;

import java.net.URI;
import java.util.Objects;
import java.util.Properties;

public final class ByteBuildersLobbyPlugin extends JavaPlugin implements Listener {

    private PluginManager pluginManager = new PluginManager(getServer(), getSLF4JLogger());
    private Server server = pluginManager.getServer();
    private Logger logger = pluginManager.getLogger();

    @Subscribe
    public void onEnable() {
        //register the events
        logger.info("Registering events...");
        getServer().getPluginManager().registerEvents(new AsyncChat(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new SwapHands(), this);
        getServer().getPluginManager().registerEvents(new DropItem(), this);
        getServer().getPluginManager().registerEvents(new RightClickItem(), this);
        logger.info("Registering commands...");
        this.getCommand("spawn").setExecutor(new SpawnCommand());
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand());
        logger.info("ByteBuilders Lobby Plugin initialized!");

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
