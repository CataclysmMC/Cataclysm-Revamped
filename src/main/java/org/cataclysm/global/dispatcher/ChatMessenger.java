package org.cataclysm.global.dispatcher;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChatMessenger {
    public static void sendCataclysmMessage(@NotNull Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(prefix("<#A11818>Cᴀᴛᴀᴄʟʏꜱᴍ") + message));
    }

    @Contract(pure = true)
    private static @NotNull String prefix(String prefix) {
        return wrap(prefix) + " <dark_gray>» <gray>";
    }

    @Contract(pure = true)
    private static @NotNull String wrap(String prefix) {
        return "<dark_gray><bold>[</bold>" + prefix + "<dark_gray><bold>]</bold>";
    }
}
