package org.cataclysm.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.cataclysm.api.registry.Registrable;
import org.cataclysm.game.mechanics.clock.events.DayChangeEvent;
import org.cataclysm.utils.message.ChatMessenger;
import org.cataclysm.utils.message.TypeWriter;

@Registrable
public class GameListener implements Listener {

    @EventHandler
    private void onDayChange(DayChangeEvent event) {
        int day = event.getNewDay();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChatMessenger.cataclysm(player, "El día <#A11818>" + day + "<gray> ha comenzado.");
            TypeWriter.actionBar(player, "Día " + day, TypeWriter.Speed.SLOW);
        }
    }

}
