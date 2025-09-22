package org.cataclysm.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

public class CataclysmDispatcher {
    public void message(@NotNull Player player, String text) {
        String prefix = "<gray><b>[</b><#A11818>Cᴀᴛᴀᴄʟʏꜱᴍ</#><gray><b>]</b> » </gray><light_gray>";
        player.sendMessage(MiniMessage.miniMessage().deserialize(prefix).append(Component.text(text)));
    }

    public void typeWriter(Player player, @NotNull String message) {
        String[] charArray = message.split("");
        for (int i = 0; i < charArray.length; i++) {
            String text = message.substring(0, i);
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                player.sendActionBar(MiniMessage.miniMessage().deserialize(text));
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 0.5F, 2.0F);
            }, i * 2L);
        }
    }
}