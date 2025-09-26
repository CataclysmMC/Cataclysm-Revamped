package org.cataclysm.utils.message;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

public class TypeWriter {
    public enum Speed {SLOW,NORMAL,FAST,VERY_FAST}
    
    public static void actionBar(Player player, String msg, @NotNull TypeWriter.Speed typeSpeed) {
        int tickInterval = switch (typeSpeed) {
            case SLOW      -> 4;
            case NORMAL    -> 3;
            case FAST      -> 2;
            case VERY_FAST -> 1;
        };
        actionBar(player, msg, tickInterval);
    }

    public static void actionBar(Player player, @NotNull String msg, int tickInterval) {
        String[] charArray = msg.split("");
        for (int i = 0; i <= charArray.length; i++) {
            String text = msg.substring(0, i);
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                player.sendActionBar(MiniMessage.miniMessage().deserialize(text));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5F, 2.0F);
            }, (long) i * tickInterval);
        }
    }
}
