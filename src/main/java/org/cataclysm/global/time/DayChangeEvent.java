package org.cataclysm.global.time;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the logical in-game day changes inside {@link CataclysmTime}.
 */
@Getter
public class DayChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    /** The day before the change. */
    private final int oldDay;

    /** The new day after the change. */
    private final int newDay;

    /**
     * Creates a new DayChangeEvent.
     *
     * @param oldDay previous day
     * @param newDay new day
     */
    public DayChangeEvent(int oldDay, int newDay) {
        this.oldDay = oldDay;
        this.newDay = newDay;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}