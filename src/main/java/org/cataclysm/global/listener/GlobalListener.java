package org.cataclysm.global.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.Registrable;
import org.cataclysm.global.CataclysmDispatcher;
import org.cataclysm.global.events.DayChangeEvent;

@Registrable
public class GlobalListener implements Listener {

    @EventHandler
    private void onDayChange(DayChangeEvent event) {
        CataclysmDispatcher dispatcher = Cataclysm.getDispatcher();
        int day = event.getNewDay();
        for (Player player : Bukkit.getOnlinePlayers()) {
            dispatcher.message(player, "El día <#A11818>" + day + "<gray> ha comenzado.");
            dispatcher.typeWriter(player, "Día " + day);
        }
    }

}
