package org.cataclysm.game.listeners;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.soundtrack.SoundData;
import org.cataclysm.game.mechanics.clock.events.ClockTickEvent;
import org.cataclysm.game.mechanics.ragnarok.Ragnarok;
import org.cataclysm.game.mechanics.ragnarok.events.RagnarokEndEvent;
import org.cataclysm.game.mechanics.ragnarok.events.RagnarokStartEvent;
import org.cataclysm.utils.message.ChatMessenger;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RagnarokListener implements Listener {

    private static final RagnarokListener INSTANCE = new RagnarokListener();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Ragnarok RAGNAROK = Cataclysm.getRagnarok();

    private static final int AMBIENT_SOUND_CHANCE = 3;
    private static final int LIGHTNING_CHANCE = 10;
    private static final double MIN_LIGHTNING_DISTANCE = 30.0;
    private static final double MAX_LIGHTNING_DISTANCE = 50.0;

    private static final long RAGNAROK_TIME = 18000L;
    private static final long NORMAL_TIME = 1000L;
    private static final int INFINITE_DURATION = Integer.MAX_VALUE;
    private static final int NORMAL_WEATHER_DURATION = 24000;

    private static final List<SoundData> START_SOUNDS = List.of(
            new SoundData("cataclysm.ragnarok.horn", 1.0f, 1.0f),
            new SoundData(Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.6f),
            new SoundData(Sound.ENTITY_ZOMBIE_HORSE_DEATH, 1.0f, 0.5f),
            new SoundData(Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1.0f, 0.5f),
            new SoundData(Sound.ITEM_TRIDENT_THUNDER, 0.1f, 0.55f)
    );

    private static final List<SoundData> END_SOUNDS = List.of(
            new SoundData(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f, 0.5f),
            new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 0.75f)
    );

    private static final String START_MESSAGE = "<i>Deus misereatur animarum suarum...";
    private static final String END_MESSAGE = "<i>Beati reliquiae in consummatione saeculi.";

    @EventHandler
    private void onClockAsyncTick(ClockTickEvent event) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(100) < AMBIENT_SOUND_CHANCE) playGlobalAmbientSound();
        if (random.nextInt(100) < LIGHTNING_CHANCE) triggerRandomLightning();
        RAGNAROK.tick();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerBossBar(event.getPlayer(), true);
    }

    @EventHandler
    private void onRagnarokStart(RagnarokStartEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Title startTitle = createRagnarokTitle();

        players.forEach(player -> {
            updatePlayerBossBar(player, true);
            player.showTitle(startTitle);
            playMultipleSounds(player, START_SOUNDS);
            ChatMessenger.ragnarok(player, START_MESSAGE);
        });

        applyWorldAtmosphere(true);
    }

    @EventHandler
    private void onRagnarokEnd(RagnarokEndEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        players.forEach(player -> {
            updatePlayerBossBar(player, false);
            player.stopSound("cataclysm.ragnarok");
            playMultipleSounds(player, END_SOUNDS);
            ChatMessenger.ragnarok(player, END_MESSAGE);
        });

        applyWorldAtmosphere(false);
    }

    private void playGlobalAmbientSound() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float pitch = random.nextFloat(0.5f, 0.75f);

        Bukkit.getOnlinePlayers().forEach(player ->
                player.playSound(player, Sound.AMBIENT_CAVE, SoundCategory.AMBIENT, 1.0f, pitch)
        );
    }

    private void triggerRandomLightning() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) return;

        players.forEach(player ->
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 0.8f, 0.6f)
        );

        getPlayersList(players).stream()
                .skip(ThreadLocalRandom.current().nextInt(players.size()))
                .findFirst()
                .ifPresent(this::strikeLightningNearPlayer);
    }

    private void strikeLightningNearPlayer(@NotNull Player player) {
        Location playerLocation = player.getLocation();
        World world = playerLocation.getWorld();
        if (world == null) return;

        Location lightningLocation = generateRandomLightningLocation(playerLocation, world);
        world.strikeLightning(lightningLocation);
    }

    private Location generateRandomLightningLocation(@NotNull Location center, @NotNull World world) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble(MIN_LIGHTNING_DISTANCE, MAX_LIGHTNING_DISTANCE);

        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;
        double y = world.getHighestBlockYAt((int) x, (int) z);

        return new Location(world, x, y, z);
    }

    private void updatePlayerBossBar(@NotNull Player player, boolean show) {
        BossBar bossBar = RAGNAROK.getBossBar();
        if (show) {
            player.showBossBar(bossBar);
        } else {
            player.hideBossBar(bossBar);
        }
    }

    private void applyWorldAtmosphere(boolean isRagnarok) {
        Optional.ofNullable(Bukkit.getWorld("world"))
                .ifPresent(world -> {
                    if (isRagnarok) {
                        enableRagnarokAtmosphere(world);
                    } else {
                        restoreNormalAtmosphere(world);
                    }
                });
    }

    private void enableRagnarokAtmosphere(@NotNull World world) {
        world.setTime(RAGNAROK_TIME);
        world.setStorm(true);
        world.setThundering(true);
        world.setWeatherDuration(INFINITE_DURATION);
        world.setThunderDuration(INFINITE_DURATION);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    }

    private void restoreNormalAtmosphere(@NotNull World world) {
        world.setTime(NORMAL_TIME);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(NORMAL_WEATHER_DURATION);
        world.setThunderDuration(0);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
    }

    private Title createRagnarokTitle() {
        return Title.title(
                MINI_MESSAGE.deserialize("<#478db6><obf><b>||<reset> <#478db6>Ragnarök <#478db6><obf><b>||"),
                MINI_MESSAGE.deserialize("<gray>Duración: <#478db6>" + RAGNAROK.getBeautyTime()),
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofMillis(2500),
                        Duration.ofMillis(3000)
                )
        );
    }

    private void playMultipleSounds(@NotNull Player player, @NotNull List<SoundData> sounds) {
        sounds.forEach(sound -> sound.play(player));
    }

    private List<Player> getPlayersList(@NotNull Collection<? extends Player> players) {
        return List.copyOf(players);
    }

    public static void register() {
        Bukkit.getPluginManager().registerEvents(INSTANCE, Cataclysm.getInstance());
    }

    public static void unregister() {
        HandlerList.unregisterAll(INSTANCE);
    }
}