package org.cataclysm.api.registry;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Set;

public class Registry {
    private final Set<Class<?>> set;
    private final Plugin plugin;

    public Registry(Plugin plugin, @NotNull Reflections reflections) {
        this.set = reflections.getTypesAnnotatedWith(Registrable.class);
        this.plugin = plugin;
    }

    public void commands() {
        PaperCommandManager manager = new PaperCommandManager(plugin);
        set.forEach(clazz -> {
            try {
                if (clazz.getDeclaredConstructor().newInstance() instanceof BaseCommand command)
                    manager.registerCommand(command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void listeners() {
        PluginManager manager = Bukkit.getPluginManager();
        set.forEach(clazz -> {
            try {
                if (clazz.getDeclaredConstructor().newInstance() instanceof Listener listener)
                    manager.registerEvents(listener, plugin);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
