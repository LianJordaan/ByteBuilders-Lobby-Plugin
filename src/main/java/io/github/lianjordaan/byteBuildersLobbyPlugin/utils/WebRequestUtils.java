package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class WebRequestUtils {
    public static CompletableFuture<String> makeWebRequest(String urlString, String body, String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // If a body is provided, we assume a POST request, otherwise default to GET
                if (body != null && !body.isEmpty()) {
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    // Set content type based on 'type' parameter
                    if ("json".equalsIgnoreCase(type)) {
                        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    } else if ("txt".equalsIgnoreCase(type)) {
                        connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                    }

                    // Write the body to the output stream
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = body.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                } else {
                    connection.setRequestMethod("GET");
                }

                // Check the response code
                if (connection.getResponseCode() != 200) {
                    throw new RuntimeException("Failed: HTTP error code: " + connection.getResponseCode());
                }

                // Read the response
                StringBuilder response = new StringBuilder();
                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                connection.disconnect();
                return response.toString(); // Return the response

            } catch (Exception e) {
                throw new RuntimeException(e); // Handle exceptions
            }
        });
    }
}
