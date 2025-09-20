package org.cataclysm.utils;

import org.jetbrains.annotations.NotNull;

public class MathUtils {

    public static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }

    public static @NotNull String formatSeconds(long time, boolean omitZero) {
        long hours = time / 3600;
        long minutes = (time / 60) % 60;
        long seconds = time % 60;

        StringBuilder builder = new StringBuilder();

        // Si omitZero es true, no se agregan valores en 0
        if (!omitZero || hours != 0) {
            builder.append(hours).append("ʜ ");
        }
        if (!omitZero || minutes != 0) {
            builder.append(minutes).append("ᴍ ");
        }
        if (!omitZero || seconds != 0) {
            builder.append(seconds).append("s");
        }

        return builder.toString().trim();
    }

}