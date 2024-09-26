package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

public class ColorUtils {
    public static String getPlotColor(String plotSize) {
        return switch (plotSize.toLowerCase()) {
            case "small" -> "<#7be8e8>";
            case "basic" -> "<#FFFFFF>";
            case "large" -> "<#f9ff4a>";
            case "massive" -> "<#32fc47>";
            case "mega" -> "<#3e008f>";
            case "super" -> "<#d615d3>";
            default -> "<#FFFFFF>";
        };
    }
}
