package org.cataclysm.global.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;

@CommandAlias("cataclysm|cata|c")
@Description("Cataclysm's main command.")
public class CataclysmCMD extends BaseCommand {

    @Subcommand("time")
    @Description("Anuncia el tiempo actual de Cataclysm")
    private void onTime() {
        if (!(super.getCurrentCommandIssuer().getIssuer() instanceof Player sender)) return;
        Cataclysm.getTime().dispatchTimeInfo(sender, "GENERAL");
    }

}
