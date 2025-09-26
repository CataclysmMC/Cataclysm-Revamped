package org.cataclysm.api.soundtrack;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundData {
    private final Sound sound;
    private final String key;
    private final float volume;
    private final float pitch;

    public SoundData(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.key = null;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundData(String key, float volume, float pitch) {
        this.sound = null;
        this.key = key;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player player) {
        if (key != null) {
            player.playSound(player, key, volume, pitch);
        } else if (sound != null) {
            player.playSound(player, sound, volume, pitch);
        }
    }
}
