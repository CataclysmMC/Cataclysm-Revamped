package org.cataclysm.game.mechanics.clock.events;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DayChangeEvent extends ClockEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final int oldDay;
    private final int newDay;

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