package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonObject;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public static ItemStack createPlotItem(String plotType, Integer maxPlots, Integer usedPlots, Material material, String plotSizeName, String plotSize) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i>" + ColorUtils.getPlotColor(plotType) + plotType + " Plot"));
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<!i><#808080>Size: " + plotSizeName));
        lore.add(Component.text(""));
        if (maxPlots == 0) {
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>You do not own any plots of this type."));
            lore.add(Component.text(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gold>Get your first " + plotType + " Plot in the store! (/buy)"));
        } else if (usedPlots >= maxPlots) {
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>You have used <red>" + usedPlots + "/" + maxPlots + "<gray> of your " + plotType + " Plots."));
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
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("create-plot", plugin), PersistentDataType.STRING, plotSize);
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

    public static ItemStack createDevelopersListItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.BOOKSHELF); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#bb9760>Developers List"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "developers-list");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBuildersListItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.BARREL); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#705026>Builders List"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "builders-list");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBannedPlayersListItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.BARRIER); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#ff0000>Banned Players List"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "banned-players-list");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createWhitelistedPlayersListItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#a9fcf6>Whitelisted Players List"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "whitelisted-players-list");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCoOwnerPlayersListItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.NETHER_STAR); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#aa9dfa>Co-Owner List"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "co-owner-list");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createEditPlotNameItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.OAK_HANGING_SIGN); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#e3b049>Edit Plot Name"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "edit-plot-name");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createEditPlotDescriptionItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.OAK_SIGN); // Use the desired material
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#e3b049>Edit Plot Description"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "edit-plot-description");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static CompletableFuture<ItemStack> createTogglePlotWhitelistItem(Integer plotID) {
        return PlotUtils.isWhitelisted(plotID).thenApply(isWhitelisted -> {
            ItemStack isWhiteListedItem = new ItemStack(Material.LEVER);
            ItemMeta meta = isWhiteListedItem.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#E65E8F>Whitelist: <dark_gray>OFF"));

            // Determine the item type based on the whitelist status
            if (isWhitelisted) {
                isWhiteListedItem = new ItemStack(Material.REDSTONE_TORCH);
                meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#E65E8F>Whitelist: <green>ON"));
            }

            // Set the item meta
            meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "toggle-whitelist");
            meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
            isWhiteListedItem.setItemMeta(meta);

            return isWhiteListedItem; // Return the constructed ItemStack
        }).exceptionally(ex -> {
            Bukkit.getServer().getLogger().info("Error: " + ex);
            ItemStack item = new ItemStack(Material.LEVER);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><#E65E8F>Whitelist: <dark_gray>OFF"));
            return new ItemStack(Material.LEVER); // Return a default item on error
        });
    }
    public static ItemStack createJoinPlotItem(Integer plotID) {
        ItemStack item = new ItemStack(Material.REPEATER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<!i><red>Join Plot"));
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-move", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("no-drop", plugin), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "join-plot");
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, plotID);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createPlotItem(JsonObject plotData){
        int id = plotData.get("_id").getAsInt();
        String name = plotData.get("name").getAsString();
        String description = plotData.get("description").getAsString();
        String sizeName = plotData.get("sizeName").getAsString();
        String material = plotData.get("material").getAsString();
        String skullSkin = plotData.get("skullSkin").getAsString();
        String ownerUuid = plotData.get("ownerUuid").getAsString();
        int modelData = plotData.get("modelData").getAsInt();
        boolean whitelisted = plotData.get("whitelisted").getAsBoolean();
        ItemStack item = new ItemStack(Material.valueOf(material.toUpperCase()));
        ItemMeta itemMeta = item.getItemMeta();
        if (material.equalsIgnoreCase("PLAYER_HEAD")) {
            URL skullSkinUrl;
            try {
                skullSkinUrl = new URI(skullSkin).toURL();

                SkullMeta skullMeta = (SkullMeta) itemMeta;
                PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
                PlayerTextures playerTextures = playerProfile.getTextures();
                playerTextures.setSkin(skullSkinUrl);
                playerProfile.setTextures(playerTextures);
                skullMeta.setPlayerProfile(playerProfile);
            } catch (Exception e) {
                item = new ItemStack(Material.MAP);
            }
        }

        itemMeta.displayName(MiniMessage.miniMessage().deserialize("<!i><white>" + name));
        List<Component> lore = new ArrayList<>();
        if (itemMeta.hasLore() && itemMeta.lore() != null) {
            lore = itemMeta.lore();
        }

        assert lore != null;
        lore.add(MiniMessage.miniMessage().deserialize("<!i><#808080>" + sizeName + " Plot"));
        lore.add(MiniMessage.miniMessage().deserialize("<!i><white>" + description));

        if (whitelisted) {
            lore.add(MiniMessage.miniMessage().deserialize("<!i><red>Whitelisted"));
        }

        lore.add(MiniMessage.miniMessage().deserialize(""));
        lore.add(MiniMessage.miniMessage().deserialize("<!i><dark_gray>ID: " + id));

        itemMeta.lore(lore);
        itemMeta.setCustomModelData(modelData);
        itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-id", plugin), PersistentDataType.INTEGER, id);
        itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("plot-owner", plugin), PersistentDataType.STRING, ownerUuid);
        itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("open-menu", plugin), PersistentDataType.STRING, "manage-join-plot");

        item.setItemMeta(itemMeta);

        return item;
    }

}
