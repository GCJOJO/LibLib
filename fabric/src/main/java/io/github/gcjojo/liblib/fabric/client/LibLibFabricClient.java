package io.github.gcjojo.liblib.fabric.client;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.tween.TweenManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class LibLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LibLib.initClient();
        LibLib.setSoundPlayer(new FabricSoundPlayer());

        WorldRenderEvents.START.register((context) -> {
            TweenManager.updateTweens(context.tickDelta(), TweenManager.TweenSide.CLIENT);
        });


    }
}
