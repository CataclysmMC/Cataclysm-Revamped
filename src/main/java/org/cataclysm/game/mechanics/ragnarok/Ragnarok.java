package org.cataclysm.game.mechanics.ragnarok;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.JsonConfig;
import org.cataclysm.game.listeners.RagnarokListener;
import org.cataclysm.game.mechanics.ragnarok.events.RagnarokEndEvent;
import org.cataclysm.game.mechanics.ragnarok.events.RagnarokStartEvent;

import java.io.Serializable;

@Getter
public class Ragnarok implements Serializable {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String FILE_NAME = "data/ragnarok.json";
    private static final int BASE_DURATION = 900, MAX_LEVEL = 10, PROGRESS_THRESHOLD = 5;

    // ── BossBar UI ────────────────────────────────────────────────────────────
    private final transient BossBar bossBar = BossBar.bossBar(
            Component.text("\ue666"),
            1f,
            BossBar.Color.BLUE,
            BossBar.Overlay.PROGRESS).addFlags(BossBar.Flag.DARKEN_SCREEN);

    // ── Runtime State ─────────────────────────────────────────────────────────
    private boolean active;
    private int time, duration, progress, level;

    // ── Constructors ──────────────────────────────────────────────────────────
    public Ragnarok(int time, int duration, int progress, boolean active, int level) {
        this.time = time;
        this.duration = duration;
        this.progress = progress;
        this.active = active;
        this.level = level;
    }

    public Ragnarok() {
        this(0, 0, 0, false, 1);
    }

    // ── Core Lifecycle ────────────────────────────────────────────────────────
    public void start() {
        this.duration = BASE_DURATION * (progress + 1) + time;
        this.time = duration;
        this.active = true;
        this.updateBar();
        RagnarokListener.register();
        new RagnarokStartEvent(this).callEvent();
    }

    public void stop() {
        this.time = this.duration = 0;
        this.active = false;
        this.updateBar();
        new RagnarokEndEvent(this).callEvent();
        RagnarokListener.unregister();
    }

    public void tick() {
        this.updateBar();
        if (--time <= 0) stop();
    }

    // ── Mutators ──────────────────────────────────────────────────────────────
    public void setTime(int newTime) {
        this.duration = Math.max(duration, newTime);
        this.time = newTime;
    }

    public void setLevel(int newLevel) {
        this.level = clamp(newLevel, MAX_LEVEL);
        setProgress(0);
    }

    public void setProgress(int newProgress) {
        this.progress = clamp(newProgress, PROGRESS_THRESHOLD);
        if (canLevelUp()) setLevel(level + 1);
    }

    // ── Logic Helpers ─────────────────────────────────────────────────────────
    private int clamp(int val, int max) {
        return Math.max(0, Math.min(val, max));
    }

    public void updateBar() {
        bossBar.progress(getProgressPercent());
    }

    public boolean canLevelUp() {
        return level < MAX_LEVEL && progress >= PROGRESS_THRESHOLD;
    }

    public boolean shouldEnd() {
        return active && time <= 0;
    }

    public boolean isCritical() {
        return active && getRemainingPercent() < 10.0f;
    }

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    public double getIntensity() {
        return 1.0 + (level - 1) * 0.2;
    }

    // ── UI Utils ──────────────────────────────────────────────────────────────
    public float getProgressPercent() {
        return duration <= 0 ? 0f : round4pl((float) time / duration);
    }

    public float getRemainingPercent() {
        return round4pl(1.0f - getProgressPercent());
    }

    public String getBeautyProgress() {
        int filled = Math.max(0, Math.min(10, Math.round(getProgressPercent() * 10)));
        return "⏹".repeat(filled) + "□".repeat(10 - filled);
    }

    public String getBeautyTime() {
        if (time <= 0) return "0s";
        int m = time / 60, s = time % 60;
        return m > 0 ? m + "ᴍ " + s + "s" : s + "ꜱ";
    }

    private float round4pl(float val) {
        return Math.round(val * 10000f) / 10000f;
    }

    // ── Persistence ───────────────────────────────────────────────────────────
    public void save() throws Exception {
        JsonConfig config = JsonConfig.cfg(FILE_NAME, Cataclysm.getInstance());
        JsonObject object = Cataclysm.getGson().toJsonTree(this).getAsJsonObject();
        config.setJsonObject(object);
        config.save();
    }

    public static Ragnarok restoreOrDefault(Ragnarok fallback) throws Exception {
        JsonObject json = JsonConfig.cfg(FILE_NAME, Cataclysm.getInstance()).getJsonObject();
        return json.entrySet().isEmpty()
                ? fallback
                : Cataclysm.getGson().fromJson(json, Ragnarok.class);
    }
}