package org.cataclysm.global.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.cataclysm.api.listener.Registrable;
import org.cataclysm.global.dispatcher.OldDispatcher;
import org.cataclysm.global.time.DayChangeEvent;

@Registrable
public class GlobalListener implements Listener {

    @EventHandler
    private void onDayChange(DayChangeEvent event) {
        int day = event.getNewDay();

        OldDispatcher.sendMessage("El día <#A11818>" + day + "<gray> ha comenzado.");
        OldDispatcher.sendActionBar("Día " + day);

        OldDispatcher.resetDelay();
    }

}
