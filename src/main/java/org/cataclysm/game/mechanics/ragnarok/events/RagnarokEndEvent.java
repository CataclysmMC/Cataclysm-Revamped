package org.cataclysm.game.mechanics.ragnarok.events;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.cataclysm.game.mechanics.ragnarok.Ragnarok;
import org.jetbrains.annotations.NotNull;

@Getter
public class RagnarokEndEvent extends RagnarokEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public RagnarokEndEvent(Ragnarok ragnarok) {
        super(ragnarok);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}