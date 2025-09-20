package org.cataclysm.api.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.cataclysm.Cataclysm;
import org.reflections.Reflections;

public class EventRegistry {
    public static void handleRegistry(Cataclysm cataclysm) {
        Reflections reflections = Cataclysm.getReflections();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Registrable.class)) {
            try {
                if (!(clazz.getDeclaredConstructor().newInstance() instanceof Listener listener)) continue;
                Bukkit.getServer().getPluginManager().registerEvents(listener, cataclysm);
            } catch (Exception exception) {
                exception.fillInStackTrace();
            }
        }
    }
}