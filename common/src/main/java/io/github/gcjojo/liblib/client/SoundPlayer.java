package io.github.gcjojo.liblib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public class SoundPlayer {
    public static void playSound(String soundName) {
        playSound(soundName, 1.0f, 1.0f);
    }

    public static void playSound(String soundName, float pitch, float volume){
        playSound(ResourceLocation.tryParse(soundName), pitch, volume);
    }

    public static void playSound(ResourceLocation soundLocation, float pitch, float volume) {
        loadSound(soundLocation).ifPresent(soundEvent ->
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume)));
    }

    public static Optional<SoundEvent> loadSound(String soundName) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(soundName);
        if (resourceLocation == null) return Optional.empty();

        return loadSound(resourceLocation);
    }

    public static Optional<SoundEvent> loadSound(ResourceLocation soundLocation) {
        return BuiltInRegistries.SOUND_EVENT.getOptional(soundLocation);
    }
}
