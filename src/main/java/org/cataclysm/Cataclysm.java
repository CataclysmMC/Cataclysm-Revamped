package org.cataclysm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.cataclysm.api.registry.Registry;
import org.cataclysm.game.mechanics.clock.CataclysmClock;
import org.cataclysm.game.mechanics.ragnarok.Ragnarok;
import org.reflections.Reflections;

public final class Cataclysm extends JavaPlugin {
    private static final String ASCII = """
      ____    _  _____  _    ____ _  __   ______  __  __
     / ___|  / \\|_   _|/ \\  / ___| | \\ \\ / / ___||  \\/  |
    | |     / _ \\ | | / _ \\| |   | |  \\ V /\\___ \\| |\\/| |
    | |___ / ___ \\| |/ ___ \\ |___| |___| |  ___) | |  | |
     \\____/_/   \\_\\_/_/   \\_\\____|_____|_| |____/|_|  |_|
    """;

    private static final @Getter Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final @Getter Reflections reflections = new Reflections("org.cataclysm");

    private static @Getter Cataclysm instance;
    private static @Getter @Setter Ragnarok ragnarok;
    private static @Getter @Setter CataclysmClock clock;


    @Override
    public void onEnable() {
        instance = this;

        try {
            clock = CataclysmClock.restoreOrDefault(new CataclysmClock());
            ragnarok = Ragnarok.restoreOrDefault(new Ragnarok());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        clock.startTickers();

        Registry registry = new Registry(this, reflections);
        registry.listeners();
        registry.commands();

        getLogger().info("\n" + ASCII);
        getLogger().info("Plugin enabled successfully. v" + getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        try {
            ragnarok.save();
            clock.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getLogger().info("Plugin disabled successfully.");
    }
}