package org.cataclysm.game.mechanics.ragnarok.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cataclysm.game.mechanics.ragnarok.Ragnarok;
import org.jetbrains.annotations.NotNull;

@Getter
public class RagnarokEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Ragnarok ragnarok;

    public RagnarokEvent(Ragnarok ragnarok) {
        this.ragnarok = ragnarok;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}