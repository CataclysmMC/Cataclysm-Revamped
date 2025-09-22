package org.cataclysm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.cataclysm.api.listener.EventRegistry;
import org.cataclysm.global.cmd.CommandRegistry;
import org.cataclysm.global.CataclysmDispatcher;
import org.cataclysm.global.CataclysmTime;
import org.reflections.Reflections;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Cataclysm extends JavaPlugin {
    private static final String ASCII = """
      ____    _  _____  _    ____ _  __   ______  __  __
     / ___|  / \\|_   _|/ \\  / ___| | \\ \\ / / ___||  \\/  |
    | |     / _ \\ | | / _ \\| |   | |  \\ V /\\___ \\| |\\/| |
    | |___ / ___ \\| |/ ___ \\ |___| |___| |  ___) | |  | |
     \\____/_/   \\_\\_/_/   \\_\\____|_____|_| |____/|_|  |_|
    """;

    private static final @Getter ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private static final @Getter Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static final @Getter Reflections reflections = new Reflections("org.cataclysm");

    private static @Getter Cataclysm instance;
    private static @Getter @Setter CataclysmTime timeManager;
    private static @Getter @Setter CataclysmDispatcher dispatcher;

    @Override
    public void onEnable() {
        instance = this;

        try {
            timeManager = CataclysmTime.load();
            dispatcher = new CataclysmDispatcher();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CommandRegistry.handleRegistry(this);
        EventRegistry.handleRegistry(this);

        super.getLogger().info("\n" + ASCII);
        super.getLogger().info("Plugin enabled successfully. v" + getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        try {
            CataclysmTime.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        service.shutdown();
        try {
            if (!service.awaitTermination(3, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException ie) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }

        super.getLogger().info("Plugin disabled successfully.");
    }
}