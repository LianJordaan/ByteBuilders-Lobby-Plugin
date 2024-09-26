package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;

public class LuckpermsUtils {
    public static String getHighestRank(Player player) {
        try {
            // Get the LuckPerms API instance
            LuckPerms api = LuckPermsProvider.get();

            // Fetch the user's LuckPerms groups (inherited)
            List<String> lowerCaseRanks = api.getUserManager()
                    .getUser(player.getUniqueId())
                    .getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build())
                    .stream()
                    .map(Group::getName)  // Get the group's name
                    .map(String::toLowerCase)  // Convert the name to lowercase
                    .toList();

            // Check for ranks in order of priority
            if (lowerCaseRanks.contains("royal")) return "royal";
            if (lowerCaseRanks.contains("emperor")) return "emperor";
            if (lowerCaseRanks.contains("mythic")) return "mythic";
            if (lowerCaseRanks.contains("noble")) return "noble";

            // Default rank if none of the higher ranks are found
            return "default";

        } catch (Exception e) {
            e.printStackTrace();
            return "default";  // Return default if there's an error
        }
    }
}
