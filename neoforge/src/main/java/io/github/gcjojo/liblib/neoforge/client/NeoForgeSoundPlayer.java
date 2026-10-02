package io.github.gcjojo.liblib.neoforge.client;

import io.github.gcjojo.liblib.client.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class NeoForgeSoundPlayer extends SoundPlayer {

    @Override
    public void playSound(String soundName) {

    }

    @Override
    public void playSound(String soundName, float pitch, float volume) {
        SoundEvent sound = loadSound(soundName);
        if (sound != null)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    @Override
    public SoundEvent loadSound(String soundName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(soundName);
        if (resourceLocation == null) return null;

        return BuiltInRegistries.SOUND_EVENT.getOptional(resourceLocation).orElse(null);
    }
}
