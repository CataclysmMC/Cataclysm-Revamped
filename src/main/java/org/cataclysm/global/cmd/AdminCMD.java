package org.cataclysm.global.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.global.CataclysmTime;

@CommandAlias("admin")
@CommandPermission("admin.perms")
@Description("Admin tools to manage Cataclysm.")
public class AdminCMD extends BaseCommand {

    @Subcommand("time ticker")
    @CommandCompletion("start|stop")
    private void onTimeTicker(String action) {
        CataclysmTime time = Cataclysm.getTime();
        Sound sound;
        switch (action) {
            case "start" -> {
                sound = Sound.BLOCK_BEACON_ACTIVATE;
                time.startTicker();
            }
            case "stop" -> {
                sound = Sound.BLOCK_BEACON_DEACTIVATE;
                time.stopTicker();
            }
            default -> sound = Sound.BLOCK_NOTE_BLOCK_BIT; //Error sound
        }

        Cataclysm.getInstance().getLogger().fine("Time Ticker action '" + action.toUpperCase() + "' executted.");
        if (super.getCurrentCommandIssuer().getIssuer() instanceof Player player)
            player.playSound(player, sound, 1F, 0.85F);
    }

    @Subcommand("time")
    @CommandCompletion("day|dayTime|tickRate")
    private void onTime(String query, int value) {
        CataclysmTime time = Cataclysm.getTime();
        switch (query) {
            case "day" -> time.setDay(value);
            case "dayTime" -> time.setDayTime(value);
            case "tickRate" -> time.setTickRate(value);
        }

        if (!(super.getCurrentCommandIssuer().getIssuer() instanceof Player player))
            time.dispatchTimeInfo(null, query);
        else time.dispatchTimeInfo(player, query);
    }

}