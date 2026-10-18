package io.github.gcjojo.liblib.fabric.client;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.events.LibLibEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class LibLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LibLib.initClient();

        WorldRenderEvents.START.register((context) -> {
            LibLibEvents.PRE_FRAME_RENDER.invoker().onPreFrameRender(context.tickCounter());
        });


    }
}
