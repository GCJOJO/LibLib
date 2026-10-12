package io.github.gcjojo.liblib.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.concurrent.CompletableFuture;

// Gemini Slop
public class ClientProfileResolver {
    public static void resolveClientProfile(ResolvableProfile uncompletedProfile, java.util.function.Consumer<ResolvableProfile> onResolved) {
        Minecraft client = Minecraft.getInstance();

        GameProfile gameProfile = uncompletedProfile.gameProfile();

        CompletableFuture.supplyAsync(() -> {
            try {
                if (gameProfile.getName() != null) {
                    var filledProfile = client.getMinecraftSessionService().hasJoinedServer(gameProfile.getName(), null, null);
                    if (filledProfile != null) return filledProfile;
                }

                return client.getMinecraftSessionService().fetchProfile(gameProfile.getId(), true).profile();
            } catch (Exception e) {
                return gameProfile;
            }
        }).thenAcceptAsync(value -> {
            if (value instanceof GameProfile resolvedGameProfile) {
                ResolvableProfile completedResolvableProfile = new ResolvableProfile(resolvedGameProfile);
                onResolved.accept(completedResolvableProfile);
            }
        }, client);
    }
}
