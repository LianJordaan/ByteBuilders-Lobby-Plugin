package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import io.github.lianjordaan.byteBuildersLobbyPlugin.ByteBuildersLobbyPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemManager {
    public static Plugin plugin = ByteBuildersLobbyPlugin.getPlugin(ByteBuildersLobbyPlugin.class);

    // Create the plot menu item
    public static ItemStack createPlotMenuItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK); // Use the desired material
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><dark_aqua>◇ <green>My Plots <dark_aqua>◇"));
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<!i><white>Click to view your plots"));
        meta.lore(lore);

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "my-plots");
        item.setItemMeta(meta);

        return item;
    }

    // Create the plot menu border item
    public static ItemStack createPlotMenuBorderItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE); // Use the desired material
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize(""));

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.setHideTooltip(true);
        item.setItemMeta(meta);

        return item;
    }

    // Create the claim new plot item
    public static ItemStack createClaimNewPlotItem() {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS); // Use the desired material
        ItemMeta meta = item.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><green>Claim New Plot"));
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>Click to claim a new plot"));
        meta.lore(lore);

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "claim-new-plot");

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-click", plugin), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);

        return item;
    }

    //create the previous page item
    public static ItemStack createPreviousPageItem(Integer currentPage, String pageName) {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW); // Use the desired material
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(255, 0, 0));
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Previous Page"));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, pageName);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("page", plugin), PersistentDataType.INTEGER, currentPage - 1);
        item.setItemMeta(meta);

        return preventDropAndMove(item);
    }

    //create the next page item
    public static ItemStack createNextPageItem(Integer currentPage, String pageName) {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW); // Use the desired material
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(0, 255, 0));
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><green>Next Page"));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, pageName);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("page", plugin), PersistentDataType.INTEGER, currentPage + 1);
        item.setItemMeta(meta);

        return preventDropAndMove(item);
    }

    //create the previous page item
    public static ItemStack createBackPageItem(Integer pageNumber, String pageName) {
        ItemStack item = new ItemStack(Material.ARROW); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Back"));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, pageName);
        if (pageNumber != null) {
            meta.getPersistentDataContainer().set(NamespacedKey.fromString("page", plugin), PersistentDataType.INTEGER, pageNumber);
        }
        item.setItemMeta(meta);

        return preventDropAndMove(item);
    }

    //create plot item for create new plot menu
    public static ItemStack createPlotItem(String plotType, Integer maxPlots, Integer usedPlots, Material material, String plotSize) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i>" + ColorUtils.getPlotColor(plotType) + plotType + " Plot"));
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<!i><#808080>Size: " + plotSize));
        lore.add(Component.text(""));
        if (maxPlots == 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>You do not own any plots of this type."));
            lore.add(Component.text(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gold>Get your first " + plotType + " Plot in the store! (/buy)"));
        } else if (usedPlots >= maxPlots) {
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>You have used <red>" + usedPlots + "/" + maxPlots + " of your " + plotType + " Plots."));
            lore.add(Component.text(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gold>Get more " + plotType + " Plots in the store! (/buy)"));
        } else {
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>You have used <green>" + usedPlots + "/" + maxPlots + " of your " + plotType + " Plots."));
            lore.add(Component.text(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!i><green>» <gray>Click to create a new " + plotType + " Plot"));
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.lore(lore);
        int stackSize = Math.min(Math.max(1, maxPlots - usedPlots), 99);
        meta.setMaxStackSize(stackSize);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("create-plot", plugin), PersistentDataType.STRING, plotType);
        item.setItemMeta(meta);
        item.setAmount(stackSize);

        return item;
    }

    public static ItemStack preventDropAndMove(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-click", plugin), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);

        return item;
    }
}
