package org.cataclysm.global.dispatcher;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Getter @Setter
public class CataclysmDispatcher {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final ScheduledExecutorService EXECUTOR = Cataclysm.getService();

    private static final Component CATACLYSM_PREFIX =
            MM.deserialize("<gray><b>[</b><#A11818>Cᴀᴛᴀᴄʟʏꜱᴍ</#><gray><b>]</b> » </gray><light_gray>");

    private final AtomicLong queuedDelayMs = new AtomicLong(0);

    // ---------------------------------------------------------------------
    // Chat
    // ---------------------------------------------------------------------

    /** Sends a formatted global message with a short notification sound. */
    public void sendGlobalMessage(String msg) {
        if (msg == null || msg.isBlank()) return;

        final Component payload = CATACLYSM_PREFIX.append(MM.deserialize(msg));
        final Audience all = Audience.audience(Bukkit.getOnlinePlayers());

        all.sendMessage(payload);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.ITEM_TRIDENT_RETURN, 0.85F, 0.80F);
        }
    }

    /** Sends a formatted message to a single player. */
    public void sendMessage(Player player, String msg) {
        if (player == null || msg == null || msg.isBlank()) return;
        player.sendMessage(CATACLYSM_PREFIX.append(MM.deserialize(msg)));
    }

    // -----------------------------------------------------
    // Action Bar con efecto "typewriter"
    // -----------------------------------------------------

    public void sendTypeWriterActionBar(String text) {
        sendTypeWriterActionBar(text, 10.0);
    }

    public void sendTypeWriterActionBar(String text, double lps) {
        if (text == null || text.isBlank()) return;

        long letterCount = text.chars().filter(Character::isLetter).count();
        if (letterCount == 0) {
            sendTypeWriterActionBar(text, 1000, 500);
            return;
        }
        int totalDuration = (int) Math.max(1000, Math.round((letterCount / lps) * 1000.0)); // mínimo 1s
        sendTypeWriterActionBar(text, totalDuration, totalDuration / 2);
    }

    public void sendTypeWriterActionBar(String text, int totalDuration, int cooldown) {
        if (text == null || text.isBlank()) return;
        int safeTotal = Math.max(0, totalDuration);
        int safeCooldown = Math.max(0, cooldown);

        int length = text.length();
        double interval = (double) safeTotal / length;
        var audience = Bukkit.getOnlinePlayers();

        for (int i = 0; i < length; i++) {
            final int idx = i;
            long stepDelay = Math.round(idx * interval);
            scheduleSync(stepDelay, () -> {
                String display = text.substring(0, idx + 1);
                Component message = FORMATTER.deserialize(display);
                for (Player player : audience) {
                    player.sendActionBar(message);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 0.35F, 1.95F);
                }
            });
        }

        this.currentDelay = safeTotal + safeCooldown;
    }

    private void scheduleSync(long delayMillis, Runnable task) {
        long at = Math.max(0, this.currentDelay + Math.max(0, delayMillis));
        SERVICE.schedule(() -> Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task), at, TimeUnit.MILLISECONDS);
    }
}