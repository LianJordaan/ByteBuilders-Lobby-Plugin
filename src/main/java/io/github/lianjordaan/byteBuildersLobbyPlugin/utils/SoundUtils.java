package io.github.lianjordaan.byteBuildersLobbyPlugin.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {
    public static void playErrorSound(Player player) {
        player.playSound(Sound.sound(Key.key("minecraft", "entity.shulker.hurt_closed"), Sound.Source.MASTER,1 ,1));
    }
}
