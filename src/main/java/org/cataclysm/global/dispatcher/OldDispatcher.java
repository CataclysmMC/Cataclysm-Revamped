package org.cataclysm.global.dispatcher;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class OldDispatcher {
    private static final AtomicLong ACCUMULATED_MILLIS = new AtomicLong(0);
    private static long acumulatedMillis = 0; // compatibilidad con tu API pública

    public static long getAcumulatedMillis() { return ACCUMULATED_MILLIS.get(); }
    public static void setAcumulatedMillis(long v) { ACCUMULATED_MILLIS.set(Math.max(0, v)); }

    private static final ScheduledExecutorService EXECUTOR = Cataclysm.getService();
    private static final MiniMessage MM = MiniMessage.miniMessage();

    /** Audiencia dinámica en tiempo de ejecución (jugadores online actuales). */
    private static Audience currentAudience() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        return players.isEmpty() ? Audience.empty() : Audience.audience(players);
    }

    /** Agenda en EXECUTOR y luego ejecuta en el hilo principal de Bukkit. */
    private static void scheduleSync(long delayMillis, Runnable task) {
        long base = ACCUMULATED_MILLIS.get();
        long at = Math.max(0, base + Math.max(0, delayMillis));
        EXECUTOR.schedule(() ->
                        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task),
                at, TimeUnit.MILLISECONDS
        );
    }

    // -----------------------------------------------------
    // Mensaje global
    // -----------------------------------------------------

    public static void sendMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChatMessenger.sendCataclysmMessage(player, message);
        }
    }

    // -----------------------------------------------------
    // Action Bar con efecto "typewriter"
    // -----------------------------------------------------

    public static void sendActionBar(String text) {
        sendActionBar(text, 10.0); // 10 letras/seg por defecto
    }

    /** LPS = letras por segundo. */
    public static void sendActionBar(String text, double lps) {
        if (text == null || text.isBlank()) return;
        if (lps <= 0) lps = 10.0;

        long letterCount = text.chars().filter(Character::isLetter).count();
        if (letterCount == 0) {
            // No hay letras: muestra completo 1s para mantener consistencia visual
            sendActionBar(text, 1000, 500);
            return;
        }
        int totalDuration = (int) Math.max(1000, Math.round((letterCount / lps) * 1000.0)); // mínimo 1s
        sendActionBar(text, totalDuration, totalDuration / 2);
    }

    /**
     * Muestra el texto en action bar con estilo máquina de escribir.
     * @param totalDuration duración total de la animación (ms)
     * @param cooldown      pausa adicional tras terminar (ms)
     */
    public static void sendActionBar(String text, int totalDuration, int cooldown) {
        if (text == null || text.isBlank()) return;
        int safeTotal = Math.max(0, totalDuration);
        int safeCooldown = Math.max(0, cooldown);

        int length = text.length();
        double interval = (double) safeTotal / length;

        // Programa la secuencia completa respecto al acumulador actual
        long base = ACCUMULATED_MILLIS.get();

        for (int i = 0; i < length; i++) {
            final int idx = i;
            long stepDelay = Math.round(idx * interval);
            scheduleSync(stepDelay, () -> {
                String display = text.substring(0, idx + 1);
                Component message = MM.deserialize(display);
                Audience a = currentAudience();
                a.sendActionBar(message);
                a.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 0.35F, 2F));
            });
        }

        // Avanza el acumulador para la próxima acción en cola
        ACCUMULATED_MILLIS.addAndGet(safeTotal + safeCooldown);
    }

    // -----------------------------------------------------
    // Sonidos y Efectos
    // -----------------------------------------------------

    /** Reproduce una serie de sonidos para la audiencia actual respetando el acumulador. */
    public static void playSounds(Sound... sounds) {
        if (sounds == null || sounds.length == 0) return;
        scheduleSync(0, () -> {
            Audience a = currentAudience();
            for (Sound s : sounds) a.playSound(s);
        });
    }

    /** Añade efectos de poción a todos los jugadores actuales (sincronizado inmediato, sin acumulador). */
    public static void addEffects(PotionEffect... effects) {
        if (effects == null || effects.length == 0) return;
        Audience a = currentAudience();
        a.forEachAudience(ad -> {
            if (ad instanceof Player p) {
                for (PotionEffect e : effects) {
                    if (e != null) p.addPotionEffect(e);
                }
            }
        });
    }

    // -----------------------------------------------------
    // Scheduling utilitario
    // -----------------------------------------------------

    public static void schedule(Runnable runnable) {
        if (runnable == null) return;
        scheduleSync(0, runnable);
    }

    public static void schedule(Runnable runnable, long millis) {
        if (runnable == null) return;
        scheduleSync(Math.max(0, millis), runnable);
    }

    public static void addDelay(long millis) {
        if (millis <= 0) return;
        ACCUMULATED_MILLIS.addAndGet(millis);
    }

    public static void resetDelay() {
        ACCUMULATED_MILLIS.set(0);
    }
}