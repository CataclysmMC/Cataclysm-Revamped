package org.cataclysm.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.registry.Registrable;
import org.cataclysm.game.mechanics.clock.CataclysmClock;
import org.cataclysm.game.mechanics.ragnarok.Ragnarok;

@Registrable
@CommandAlias("catadmin")
@CommandPermission("admin.perms")
public class AdminCommand extends BaseCommand {

    @Subcommand("ragnarok")
    @CommandCompletion("start|stop|time")
    private void onRagnarok(CommandSender sender, String action, @Optional Integer time) {
        Ragnarok ragnarok = Cataclysm.getRagnarok();
        switch (action) {
            case "start" -> ragnarok.start();
            case "stop" -> ragnarok.stop();
            case "time" -> {
                if (time != null) ragnarok.setTime(time);
                sender.sendMessage("Ragnarok time: " + ragnarok.getTime());
            }
        }
    }

    @Subcommand("clock")
    @CommandCompletion("time|day|week <value>")
    private void onTime(String query, int value) {
        CataclysmClock clock = Cataclysm.getClock();
        switch (query) {
            case "time" -> clock.setGameTime(value);
            case "day" -> clock.setGameTime(value * 86400);
            case "week" -> clock.setGameTime(value * 604800);
        }
    }

}