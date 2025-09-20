package org.cataclysm.global.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.Plugin;
import org.cataclysm.Cataclysm;

import java.util.List;

public class CommandRegistry {
    private static final List<BaseCommand> COMMAND_LIST = List.of(
            new CataclysmCMD(),
            new AdminCMD()
    );

    public static void handleRegistry(Plugin plugin) {
        PaperCommandManager pcm = new PaperCommandManager(plugin);
        registerCommands(pcm);
    }

    private static void registerCommands(PaperCommandManager pcm) {
        for (BaseCommand command : COMMAND_LIST) {
            pcm.registerCommand(command);
        }
    }
}