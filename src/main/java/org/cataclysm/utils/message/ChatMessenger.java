package org.cataclysm.utils.message;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.cataclysm.utils.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class ChatMessenger {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public static void cataclysm(@NotNull Player player, String msg) {
        player.sendMessage(MM.deserialize(format("<#A11818>Cataclysm") + msg));
    }

    public static void ragnarok(@NotNull Player player, String msg) {
        player.sendMessage(MM.deserialize(format("<#478DB6>Ragnarök") + msg));
    }

    private static String format(String prefix) {
        return "<dark_gray><bold>[</bold>" + TinyCaps.tinyCaps(prefix) + "<dark_gray><bold>]</bold> <dark_gray>» <gray>";
    }
}
