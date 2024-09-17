package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import io.github.lianjordaan.byteBuildersLobbyPlugin.PluginManager;

public class WebSocketUtils {
    // Function to send a message to a player via WebSocket
    public static void sendMessageToPlayer(String playerName, String message) {
        // Escape special characters for JSON
        String escapedMessage = message.replace("\"", "\\\"");
        String jsonPayload = String.format(
                "{\"type\":\"forwarded-message\",\"targetId\":\"proxy\",\"message\":\"msgPlayer\",\"data\":\"{\\\"player\\\":\\\"%s\\\",\\\"message\\\":\\\"%s\\\"}\"}",
                playerName,
                escapedMessage
        );

        // Send the JSON payload via WebSocket
        PluginManager.getWebSocketClient().send(jsonPayload);
    }

    // Function to send a player to another server via WebSocket
    public static void sendPlayerToServer(String playerName, String serverName) {
        // Escape special characters for JSON
        String jsonPayload = String.format(
                "{\"type\":\"forwarded-message\",\"targetId\":\"proxy\",\"message\":\"sendPlayer\",\"data\":\"{\\\"server\\\":\\\"%s\\\",\\\"player\\\":\\\"%s\\\"}\"}",
                serverName,
                playerName
        );

        // Send the JSON payload via WebSocket
        PluginManager.getWebSocketClient().send(jsonPayload);
    }

    // Function to send a player to another server via WebSocket
    public static void registerServer(int port) {
        // Escape special characters for JSON
        String jsonPayload = String.format(
                "{\"type\":\"forwarded-message\",\"targetId\":\"proxy\",\"message\":\"registerServer\",\"data\":\"{\\\"port\\\":\\\"%s\\\"}\"}",
                port
        );

        // Send the JSON payload via WebSocket
        PluginManager.getWebSocketClient().send(jsonPayload);
    }

    // Function to send a player to another server via WebSocket
    public static void unregisterServer(int port) {
        // Escape special characters for JSON
        String jsonPayload = String.format(
                "{\"type\":\"forwarded-message\",\"targetId\":\"proxy\",\"message\":\"unregisterServer\",\"data\":\"{\\\"port\\\":\\\"%s\\\"}\"}",
                port
        );

        // Send the JSON payload via WebSocket
        PluginManager.getWebSocketClient().send(jsonPayload);
    }
}