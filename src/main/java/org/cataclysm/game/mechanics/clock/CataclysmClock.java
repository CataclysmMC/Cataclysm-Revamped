package org.cataclysm.game.mechanics.clock;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.JsonConfig;
import org.cataclysm.game.mechanics.clock.events.ClockTickEvent;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.concurrent.*;

@Getter
public class CataclysmClock implements Serializable {

    private static final String FILE_NAME    = "/data/clock.json";
    private static final int DAY_THRESHOLD   = 86_400;
    private static final int WEEK_THRESHOLD  = 7 * DAY_THRESHOLD;
    private static final int MAX_GAMETIME    = 5 * WEEK_THRESHOLD;
    private static final int MAX_TICKRATE    = 4_000;

    private transient final ScheduledExecutorService scheduler;
    private transient ScheduledFuture<?> task;
    private int gameTime;
    private int tickRate;

    public CataclysmClock(int gametime, int tickrate) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.gameTime = gametime;
        this.tickRate = tickrate;
    }

    public CataclysmClock() {
        this(0, 1000);
    }

    public void startTickers() {
        if (this.task != null) return;
        this.task = this.scheduler.scheduleAtFixedRate(
                () -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> new ClockTickEvent().callEvent());
                    this.gameTime++;
                },
                this.tickRate,
                this.tickRate,
                TimeUnit.MILLISECONDS
        );
    }

    public void stopTickers() {
        if (this.task == null) return;
        this.task.cancel(true);
        this.task = null;
    }

    public void restartTicker() {
        stopTickers();
        startTickers();
    }

    public void setGameTime(int newGameTime) {
        this.gameTime = clamp(newGameTime, MAX_GAMETIME);
        if (this.task != null) restartTicker();
    }

    public void setTickRate(int newTickRate) {
        this.tickRate = clamp(newTickRate, MAX_TICKRATE);
        if (this.task != null) restartTicker();
    }

    private int clamp(int value, int max) {
        return Math.max(0, Math.min(max, value));
    }

    public int getDay() {
        return this.gameTime / DAY_THRESHOLD;
    }

    public int getWeek() {
        return this.gameTime / WEEK_THRESHOLD;
    }

    public @NotNull String getFormattedTime() {
        int secondsOfDay = this.gameTime % DAY_THRESHOLD;
        double dayProgress = (double) secondsOfDay / DAY_THRESHOLD;
        int totalSecondsIn24h = (int) (dayProgress * 86400);

        int hours = totalSecondsIn24h / 3600;
        int minutes = (totalSecondsIn24h % 3600) / 60;
        int seconds = totalSecondsIn24h % 60;

        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    //────── Persistence ──────────────────────────────────────────────
    public void save() throws Exception {
        JsonConfig config = JsonConfig.cfg(FILE_NAME, Cataclysm.getInstance());
        JsonObject object = Cataclysm.getGson().toJsonTree(this).getAsJsonObject();
        config.setJsonObject(object);
        config.save();
    }

    public static CataclysmClock restoreOrDefault(CataclysmClock defaultClock) throws Exception {
        JsonConfig config = JsonConfig.cfg(FILE_NAME, Cataclysm.getInstance());
        JsonObject object = config.getJsonObject();
        if (object.entrySet().isEmpty()) return defaultClock;
        else return Cataclysm.getGson().fromJson(object, CataclysmClock.class);
    }
}