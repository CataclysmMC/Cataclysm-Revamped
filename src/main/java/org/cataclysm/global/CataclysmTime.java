package org.cataclysm.global;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.global.events.DayChangeEvent;
import org.cataclysm.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CataclysmTime {
    private static final String FILE_NAME = "/data/time.json";
    private static final long DAY_SECONDS = 86_400;
    private static final int MAX_DAY = 35;

    private ScheduledFuture<?> ticker;
    private long tickRate;
    private long dayTime;
    private int day;

    /**
     * Creates a manager with custom values and starts ticking.
     *
     * @param day initial day (0 allowed)
     * @param seconds seconds in day [0..86,400]
     * @param tickRate tick interval in ms (>= 1)
     */
    public CataclysmTime(int day, long seconds, long tickRate) {
        this.tickRate = Math.max(1, tickRate);
        this.setDay(day);
        this.setDayTime(seconds);
        this.startTicker();
    }

    /**
     * Creates a manager with defaults:
     * day=0, dayTime=0, tickRate=1000ms. Starts ticking.
     */
    public CataclysmTime() {
        this(0, 0, 1000);
    }

    public void setDay(int day) {
        new DayChangeEvent(this.day, day).callEvent();
        this.day = day;
        this.dayTime = 0;
    }

    public void setDayTime(long dayTime) {
        if (dayTime < 0 || dayTime > DAY_SECONDS) {
            throw new IllegalStateException("Inserted time must be in [0, " + DAY_SECONDS + "] but is: " + dayTime);
        }
        this.dayTime = dayTime;
    }

    public void setTickRate(long tickRate) {
        if (tickRate < 1) {
            throw new IllegalArgumentException("tickRate must be >= 1 ms (was " + tickRate + ")");
        }
        if (this.tickRate == tickRate) return;
        this.tickRate = tickRate;
        if (this.isTicking()) {
            this.stopTicker();
            this.startTicker();
        }
    }

    // ==============================================
    // Time Ticker
    // ==============================================

    public synchronized void startTicker() {
        if (this.ticker != null && !this.ticker.isCancelled()) return;

        this.ticker = Cataclysm.getService().scheduleAtFixedRate(() -> {
            if (this.day > 0 && this.day < MAX_DAY && this.dayTime >= DAY_SECONDS) this.setDay(this.day + 1);
            else this.setDayTime(this.dayTime + 1);
        }, 0, this.tickRate, TimeUnit.MILLISECONDS);
    }

    public synchronized void stopTicker() {
        if (this.ticker == null) return;
        this.ticker.cancel(true);
        this.ticker = null;
    }

    public synchronized boolean isTicking() {
        return this.ticker != null && !this.ticker.isCancelled();
    }

    // ==============================================
    // Utilities
    // ==============================================

    public String getBeautifulTime() {
        return MathUtils.formatSeconds(this.dayTime, false);
    }

    public void dispatchTimeInfo(@Nullable Player player, @NotNull String query) {
        String message = switch (query) {
            case "day"      -> "Nos encontramos en el día <#A11818>" + this.day + "<gray>.";
            case "dayTime"  -> "El tiempo actual es de <#A11818>" + this.getBeautifulTime() + "<gray>.";
            case "tickRate" -> "El ratio de ticks actual es de <#A11818>" + this.tickRate + "<gray>.";
            case "general"  -> "Nos encontramos en el día <#A11818>" + this.day + "<gray>, siendo exactamente las <#A11818>" + getBeautifulTime() + "<gray>.";
            default -> "<red>Consulta no válida";
        };
        if (player == null) Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(message));
        else Cataclysm.getDispatcher().message(player, message);
    }

    // ==============================================
    // Data Management
    // ==============================================

    public static void save() throws Exception {
        JsonConfig jsonConfig = JsonConfig.cfg(FILE_NAME, Cataclysm.getInstance());

        TimeData data = TimeData.toData(Cataclysm.getTimeManager());

        jsonConfig.setJsonObject(Cataclysm.getGson().toJsonTree(data).getAsJsonObject());
        jsonConfig.save();
    }

    public static CataclysmTime load() throws Exception {
        JsonConfig config = JsonConfig.cfg(FILE_NAME, Cataclysm.getInstance());
        JsonObject object = config.getJsonObject();

        CataclysmTime manager;
        if (object.entrySet().isEmpty()) manager = new CataclysmTime();
        else manager = Cataclysm.getGson()
                .fromJson(object, TimeData.class)
                .toManager();
        return manager;
    }

    record TimeData(int day, long dayTime, long tickRate) {
        public static @NotNull TimeData toData(@NotNull CataclysmTime manager) {
            return new TimeData(manager.day, manager.dayTime, manager.tickRate);
        }

        public @NotNull CataclysmTime toManager() {
            return new CataclysmTime(this.day, this.dayTime, this.tickRate);
        }
    }
}